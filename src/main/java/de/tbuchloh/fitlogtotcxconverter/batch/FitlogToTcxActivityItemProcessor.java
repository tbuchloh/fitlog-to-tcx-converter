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
import de.tbuchloh.fitlogtotcxconverter.fitlog.WeatherFL;
import de.tbuchloh.fitlogtotcxconverter.geodata.Coord;
import de.tbuchloh.fitlogtotcxconverter.geodata.DistanceCalculator;
import de.tbuchloh.fitlogtotcxconverter.geodata.DistanceUnit;
import de.tbuchloh.fitlogtotcxconverter.tcx.ActivityUtils;

public class FitlogToTcxActivityItemProcessor implements ItemProcessor<ActivityFL, JAXBElement<ActivityT>> {

	private static class PtListSplitter {

		private double consumedTm = 0;

		private final List<PtFL> remainingList;

		public PtListSplitter(final List<PtFL> pts) {
			remainingList = new ArrayList<>(pts);
		}

		public boolean isEmpty() {
			return remainingList.isEmpty();
		}

		public List<PtFL> nextLapPts(final double lapDuration, final boolean isLastLap) {
			final var toConsume = consumedTm + lapDuration;
			final var filtered = remainingList.stream().filter(v -> {
				return v.getTm() <= toConsume || isLastLap;
			}).collect(Collectors.toUnmodifiableList());

			remainingList.removeAll(filtered);
			consumedTm = CollectionUtils.lastElement(filtered).getTm();

			return filtered;
		}

	}

	private static final Logger log = LoggerFactory.getLogger(FitlogToTcxActivityItemProcessor.class);

	private void computeMissingDistances(final ActivityFL item) {
		final var track = item.getTrack();
		if (Objects.isNull(track)) {
			log.trace("Activity {}: no track found. Cannot compute missing distances.", item.getId());
			return;
		}

		Coord prevPt = null;
		var totalDistance = 0D;
		for (final var pt : track.getPts()) {
			if (Objects.isNull(pt.getDist())) {
				log.trace("Activity {}: pt {} no distance value", item.getId(), pt);
				if (Objects.nonNull(pt.getLat()) && Objects.nonNull(pt.getLon())) {
					final var p = new Coord(pt.getLat().doubleValue(), pt.getLon().doubleValue());
					if (prevPt != null) {
						final var distance = DistanceCalculator.dist(prevPt, p, DistanceUnit.KM) * 1000;
						log.trace("Activity {}: computed distance between {} and {} is {}", item.getId(), prevPt, pt);
						totalDistance += distance;
					}
					prevPt = p;
				} else {
					log.trace("Activity {}: pt {} does not contain lat or lon!", item.getId(), pt);
				}
				pt.setDist(new BigDecimal(totalDistance));
			} else {
				log.trace("pt {} contains distance. No need to recompute.");
			}
		}
	}

	private SportT convertCategory(final CategoryFL category) {
		// TODO should be configurable by properties file or something similar
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
			case "Mit Hannah":
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
					r.setAverageHeartRateBpm(createHeartRateInBPM(new BigDecimal(avgHrValue)));
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
			var total = 0D;
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
				r.setMaximumHeartRateBpm(createHeartRateInBPM(new BigDecimal(maxHrValue)));
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

	private HeartRateInBeatsPerMinuteT createHeartRateInBPM(final BigDecimal value) {
		final var hr = new HeartRateInBeatsPerMinuteT();
		hr.setValue(value.shortValue());
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
			final PtListSplitter ptListSplitter, final boolean isLastLap) {
		if (ptListSplitter.isEmpty()) {
			log.trace("No trackpoints available. Cannot create TrackT.");
			return Optional.empty();
		}

		final var r = new TrackT();
		for (final var v : ptListSplitter.nextLapPts(lapDuration, isLastLap)) {
			final var tp = new TrackpointT();
			final var tm = v.getTm();
			tp.setTime(createTime(startTime, tm));
			Optional.ofNullable(v.getEle()).ifPresent(ele -> {
				tp.setAltitudeMeters(ele.doubleValue());
			});
			Optional.ofNullable(v.getHr()).ifPresent(hr -> {
				tp.setHeartRateBpm(createHeartRateInBPM(new BigDecimal(hr)));
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

	private void fillLaps(final ActivityFL actIn, final ActivityT actOut) {
		List<PtFL> pts = Collections.emptyList();
		final var startTime = actIn.getStartTime();
		if (Objects.nonNull(actIn.getTrack())) {
			log.trace("Activity {} has trackpoints.", actIn.getId());
			pts = actIn.getTrack().getPts();
		}

		if (Objects.isNull(actIn.getLaps()) || actIn.getLaps().getLaps().isEmpty()) {
			log.debug("add dummy lap to activity {}", actIn.getId());

			final var lapIn = new LapFL();
			lapIn.setDistance(actIn.getDistance());
			lapIn.setDurationSeconds(actIn.getDuration().getTotalSeconds());
			lapIn.setStartTime(actIn.getStartTime());
			lapIn.setCalories(actIn.getCalories());

			final var laps = new LapsFL();
			laps.getLaps().add(lapIn);
			actIn.setLaps(laps);
		}

		var distanceSoFar = 0D;
		final var ptListSplitter = new PtListSplitter(pts);
		for (final var i = actIn.getLaps().getLaps().iterator(); i.hasNext();) {
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
			log.debug("extra lap (dur:{}, dist:{}, cal:{}) because of manual edits to activity {}", durationDiff,
					distanceDiff, caloriesDiff, actIn.getId());
			final var extraLap = new ActivityLapT();
			extraLap.setDistanceMeters(distanceDiff);
			extraLap.setTotalTimeSeconds(durationDiff);
			extraLap.setNotes("extra lap because of manual edits");
			extraLap.setStartTime(actIn.getStartTime());
			extraLap.setIntensity(IntensityT.ACTIVE);
			extraLap.setTriggerMethod(TriggerMethodT.MANUAL);
			extraLap.setCalories(caloriesDiff);
			actOut.getLap().add(extraLap);
		}
	}

	@Override
	public JAXBElement<ActivityT> process(final ActivityFL item) throws Exception {
		final var act = new ActivityT();
		act.setSport(convertCategory(item.getCategory()));
		act.setId(item.getStartTime());
		act.setNotes(createNotes(item.getNotes(), item.getName(), item.getWeather(), item.getEquipmentUsed()));

		computeMissingDistances(item);

		fillLaps(item, act);

		return new JAXBElement<>(
				new QName("http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2", "Activity", ""),
				ActivityT.class, act);
	}

}
