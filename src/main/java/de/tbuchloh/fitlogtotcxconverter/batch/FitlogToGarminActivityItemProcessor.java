package de.tbuchloh.fitlogtotcxconverter.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.HeartRateInBeatsPerMinuteT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.IntensityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.PositionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.SportT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TriggerMethodT;

import de.tbuchloh.fitlogtotcxconverter.fitlog.ActivityFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.CategoryFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.EquipmentUsedFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.LapFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.LapsFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.PtFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.TrackFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.WeatherFL;
import de.tbuchloh.fitlogtotcxconverter.geodata.Coord;
import de.tbuchloh.fitlogtotcxconverter.geodata.DistanceCalculator;
import de.tbuchloh.fitlogtotcxconverter.geodata.DistanceUnit;
import de.tbuchloh.fitlogtotcxconverter.tcx.ActivityUtils;

public class FitlogToGarminActivityItemProcessor implements ItemProcessor<ActivityFL, JAXBElement<ActivityT>> {

    private static final Logger log = LoggerFactory.getLogger(FitlogToGarminActivityItemProcessor.class);

    private static class PtListSplitter {

	private final List<PtFL> remainingList;

	private double consumedTm = 0;

	public PtListSplitter(final List<PtFL> pts) {
	    remainingList = new ArrayList<>(pts);
	}

	public List<PtFL> nextLapPts(final double lapDuration, final boolean isLastLap) {
	    final double toConsume = consumedTm + lapDuration;
	    final var filtered = remainingList.stream().filter(v -> {
		return (v.getTm() <= toConsume || isLastLap);
	    }).collect(Collectors.toUnmodifiableList());

	    remainingList.removeAll(filtered);
	    consumedTm = CollectionUtils.lastElement(filtered).getTm();

	    return filtered;
	}

	public boolean isEmpty() {
	    return remainingList.isEmpty();
	}

    }

    private SportT convertCategory(final CategoryFL category) {
	try {
	    return SportT.fromValue(category.getName());
	} catch (final IllegalArgumentException e) {
	    switch (category.getName()) {
	    case "Wettkampf":
		return SportT.RUNNING;
	    case "Fahrtspiel":
		return SportT.RUNNING;
	    case "Hügel":
		return SportT.RUNNING;
	    case "Kondition":
		return SportT.RUNNING;
	    default:
		log.warn("unknown category {}", category.getName());
		return SportT.OTHER;
	    }
	}
    }

    private ActivityLapT createActivityLapT(final LapFL lap, final Optional<TrackT> track, final double distanceSoFar) {
	final var r = new ActivityLapT();
	r.setStartTime(lap.getStartTime());

	// AverageHeartRateBpm
	if (Objects.nonNull(lap.getHeartRate())) {
	    final var avgHrValue = lap.getHeartRate().getAverageBPM();
	    r.setAverageHeartRateBpm(createHeartRateInBPM(avgHrValue));
	} else {
	    if (track.isPresent()) {
		final var trackpoints = track.get().getTrackpoint();
		final var avgHrValue = trackpoints.stream().filter(v -> Objects.nonNull(v.getHeartRateBpm()))
			.mapToInt(v -> {
			    return v.getHeartRateBpm().getValue();
			}).average().orElse(0);
		if (avgHrValue > 0) {
		    r.setAverageHeartRateBpm(createHeartRateInBPM((short) avgHrValue));
		}
	    }
	}

	// Calories
	Optional.ofNullable(lap.getCalories()).ifPresent(v -> {
	    r.setCalories(v.getTotalCal().intValue());
	});

	// DistanceMeters
	if (Objects.nonNull(lap.getDistance()) && Objects.nonNull(lap.getDistance().getTotalMeters())) {
	    r.setDistanceMeters(lap.getDistance().getTotalMeters().doubleValue());
	} else if (track.isPresent()) {
	    final var trackpoints = track.get().getTrackpoint();
	    double total = 0;
	    if (!trackpoints.isEmpty()) {
		final var dist1 = distanceSoFar;
		final var dist2 = CollectionUtils.lastElement(trackpoints).getDistanceMeters();
		if (Objects.nonNull(dist2)) {
		    total = dist2 - dist1;
		} else {
		    total = distanceSoFar;
		}
	    }
	    log.debug("setting distance: {}", total);
	    r.setDistanceMeters(total);
	}

	// MaximumHeartRateBpm
	if (track.isPresent()) {
	    final var maxHrValue = track.get().getTrackpoint().stream()
		    .filter(v -> Objects.nonNull(v.getHeartRateBpm())).mapToInt(v -> {
			return v.getHeartRateBpm().getValue();
		    }).max().orElse(0);
	    if (maxHrValue > 0) {
		r.setMaximumHeartRateBpm(createHeartRateInBPM((short) maxHrValue));
	    }
	}

	// TotalTimeSeconds
	r.setTotalTimeSeconds(lap.getDurationSeconds().doubleValue());

	// Intensity
	r.setIntensity(IntensityT.ACTIVE);

	// Notes not available

	r.setTriggerMethod(TriggerMethodT.MANUAL);

	// Track
	if (track.isPresent()) {
	    r.getTrack().add(track.get());
	}
	return r;
    }

    private HeartRateInBeatsPerMinuteT createHeartRateInBPM(final Short value) {
	final var hr = new HeartRateInBeatsPerMinuteT();
	hr.setValue(value);
	return hr;
    }

    private String createNotes(final String notes, final String name, final WeatherFL weather,
	    final EquipmentUsedFL equipmentUsed) {
	final var sb = new StringBuilder();
	if (!StringUtils.isBlank(notes)) {
	    sb.append("Notes: ");
	    sb.append(StringUtils.trim(notes));
	    sb.append("\n");
	}
	if (!StringUtils.isBlank(name)) {
	    sb.append("Name: ");
	    sb.append(StringUtils.trim(name));
	    sb.append("\n");
	}
	if (weather != null) {
	    sb.append("Weather: ");
	    sb.append(weather.getConditions());
	    sb.append(" (");
	    sb.append(weather.getTemp());
	    sb.append(")");
	    sb.append("\n");
	}
	if (equipmentUsed != null) {
	    sb.append(equipmentUsed.getItems().stream().map(v -> {
		return v.getName() + " (" + v.getId() + ")";
	    }).collect(Collectors.joining("\n", "Equipment: ", "")));
	    sb.append("\n");
	}
	return sb.toString();
    }

    private PositionT createPositionT(final Coord p) {
	final var pos = new PositionT();
	pos.setLatitudeDegrees(p.getLat());
	pos.setLongitudeDegrees(p.getLon());
	return pos;
    }

    private XMLGregorianCalendar createTime(final XMLGregorianCalendar startTime, final Integer offsetInSeconds) {
	try {
	    final var time = (XMLGregorianCalendar) startTime.clone();
	    time.add(DatatypeFactory.newInstance().newDuration(offsetInSeconds * 1000));
	    return time;
	} catch (final DatatypeConfigurationException e) {
	    throw new IllegalStateException(e);
	}
    }

    private Optional<TrackT> createTrack(final double lapDuration, final XMLGregorianCalendar startTime,
	    final PtListSplitter ptListSplitter, boolean isLastLap) {
	if (ptListSplitter.isEmpty()) {
	    return Optional.empty();
	}

	final var r = new TrackT();
	for (var v : ptListSplitter.nextLapPts(lapDuration, isLastLap)) {
	    final var tp = new TrackpointT();
	    final var tm = v.getTm();
	    tp.setTime(createTime(startTime, tm));
	    Optional.ofNullable(v.getEle()).ifPresent(ele -> {
		tp.setAltitudeMeters(ele.doubleValue());
	    });
	    Optional.ofNullable(v.getHr()).ifPresent(hr -> {
		tp.setHeartRateBpm(createHeartRateInBPM(hr.shortValue()));
	    });
	    Optional.ofNullable(v.getDist()).ifPresent(ele -> {
		tp.setDistanceMeters(ele.doubleValue());
	    });
	    if (Objects.nonNull(v.getLat()) && Objects.nonNull(v.getLon())) {
		tp.setPosition(createPositionT(new Coord(v.getLat().doubleValue(), v.getLon().doubleValue())));
	    }
	    r.getTrackpoint().add(tp);
	}
	return Optional.of(r);
    }

    @Override
    public JAXBElement<ActivityT> process(final ActivityFL item) throws Exception {
	final var act = new ActivityT();
	act.setSport(convertCategory(item.getCategory()));
	act.setId(item.getStartTime());
	act.setNotes(createNotes(item.getNotes(), item.getName(), item.getWeather(), item.getEquipmentUsed()));

	computeMissingDistances(item);

	fillLaps(item, act);

	return new JAXBElement<ActivityT>(
		new QName("http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2", "Activity", ""),
		ActivityT.class, act);
    }

    private void computeMissingDistances(final ActivityFL item) {
	final var track = item.getTrack();
	if (Objects.isNull(track)) {
	    return;
	}

	Coord prevPt = null;
	double totalDistance = 0;
	for (var pt : track.getPts()) {
	    if (Objects.isNull(pt.getDist())) {
		if (Objects.nonNull(pt.getLat()) && Objects.nonNull(pt.getLon())) {
		    Coord p = new Coord(pt.getLat().doubleValue(), pt.getLon().doubleValue());
		    if (prevPt != null) {
			final var distance = DistanceCalculator.dist(prevPt, p, DistanceUnit.KM) * 1000;
			totalDistance += distance;
		    }
		    prevPt = p;
		} else {
		    log.debug("Pt {} does not contain lat or lon!", pt);
		}
		pt.setDist(new BigDecimal(totalDistance));
	    } else {
		// TODO remove
		// Assert.state(prevPt == null, "prevPt must be null if distanceMeters are
		// set");
		// Assert.state(totalDistance == 0, "totalDistance must be null if
		// distanceMeters are set");
		pt.setDist(new BigDecimal(totalDistance));
	    }
	}
    }

    private void fillLaps(final ActivityFL actIn, final ActivityT actOut) {
	List<PtFL> pts = Collections.emptyList();
	XMLGregorianCalendar startTime = actIn.getStartTime();
	if (Objects.nonNull(actIn.getTrack())) {
	    pts = actIn.getTrack().getPts();
	    // startTime = actIn.getTrack().getStartTime();
	}

	if (Objects.isNull(actIn.getLaps()) || actIn.getLaps().getLaps().isEmpty()) {
	    final var lapIn = new LapFL();
	    lapIn.setDistance(actIn.getDistance());
	    lapIn.setDurationSeconds(actIn.getDuration().getTotalSeconds());
	    lapIn.setStartTime(actIn.getStartTime());
	    lapIn.setCalories(actIn.getCalories());

	    final var laps = new LapsFL();
	    laps.getLaps().add(lapIn);
	    actIn.setLaps(laps);
	}

	double distanceSoFar = 0;
	final var ptListSplitter = new PtListSplitter(pts);
	for (var i = actIn.getLaps().getLaps().iterator(); i.hasNext();) {
	    final var lap = i.next();
	    final var lapDuration = lap.getDurationSeconds().doubleValue();
	    final var track = createTrack(lapDuration, startTime, ptListSplitter, !i.hasNext());
	    final var actLap = createActivityLapT(lap, track, distanceSoFar);
	    actOut.getLap().add(actLap);
	    if (track.isPresent()) {
		distanceSoFar = CollectionUtils.lastElement(track.get().getTrackpoint()).getDistanceMeters();
	    }
	}

	Assert.state(ptListSplitter.isEmpty(), "all pts must be assigned to laps");
	Assert.state(ActivityUtils.getTrackpointCount(actOut) == pts.size(), "trackpoint count does not match!");

	// maybe distance or time has been edited manually
	var durationDiff = 0d;
	if (Objects.nonNull(actIn.getDuration())) {
	    durationDiff = actIn.getDuration().getTotalSeconds().doubleValue()
		    - ActivityUtils.getLapTotalTimeSum(actOut);
	}

	var distanceDiff = 0d;
	if (Objects.nonNull(actIn.getDistance())) {
	    distanceDiff = actIn.getDistance().getTotalMeters().doubleValue() - ActivityUtils.getLapDistanceSum(actOut);
	}

	var caloriesDiff = 0;
	if (Objects.nonNull(actIn.getCalories())) {
	    caloriesDiff = actIn.getCalories().getTotalCal().intValue() - ActivityUtils.getLapCaloriesSum(actOut);
	}
	if (durationDiff > 1 || distanceDiff > 1 || caloriesDiff > 1) {
	    log.info("extra lap (dur:{}, dist:{}, cal:{}) because of manual edits to activity {}", durationDiff,
		    distanceDiff, caloriesDiff, actOut.getId());
	    final var extraLap = new ActivityLapT();
	    extraLap.setDistanceMeters(distanceDiff);
	    extraLap.setTotalTimeSeconds(durationDiff);
	    // extraLap.setNotes("extra lap because of manual edits");
	    extraLap.setStartTime(actIn.getStartTime());
	    extraLap.setIntensity(IntensityT.ACTIVE);
	    extraLap.setTriggerMethod(TriggerMethodT.MANUAL);
	    extraLap.setCalories((int) caloriesDiff);
	    actOut.getLap().add(extraLap);
	}
    }

}
