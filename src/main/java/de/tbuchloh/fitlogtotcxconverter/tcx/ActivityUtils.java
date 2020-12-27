package de.tbuchloh.fitlogtotcxconverter.tcx;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;

public abstract class ActivityUtils {

    public static int getLapCaloriesSum(final ActivityT act) {
	return act.getLap().stream().mapToInt(ActivityLapT::getCalories).sum();
    }

    public static double getLapDistanceSum(final ActivityT act) {
	return act.getLap().stream().mapToDouble(ActivityLapT::getDistanceMeters).sum();
    }

    public static double getLapTotalTimeSum(final ActivityT act) {
	return act.getLap().stream().mapToDouble(ActivityLapT::getTotalTimeSeconds).sum();
    }

    public static long getTrackpointCount(final ActivityT act) {
	return act.getLap().stream().flatMap(m -> m.getTrack().stream()).flatMap(v -> v.getTrackpoint().stream())
		.count();
    }

    public static double getTrackpointDistance(final ActivityT act) {
	final var vals = act.getLap().stream().flatMap(m -> m.getTrack().stream())
		.flatMap(v -> v.getTrackpoint().stream())
		.map(v -> Optional.ofNullable(v.getDistanceMeters()).orElse(0d))
		.collect(Collectors.toUnmodifiableList());
	if (vals.isEmpty()) {
	    return 0;
	}
	return CollectionUtils.lastElement(vals);
    }

    public static short getTrackpointHeartRateAverage(final ActivityT act) {
	return (short) act.getLap().stream().flatMap(v -> v.getTrack().stream())
		.flatMap(v -> v.getTrackpoint().stream()).filter(v -> Objects.nonNull(v.getHeartRateBpm()))
		.mapToInt(v -> {
		    return v.getHeartRateBpm().getValue();
		}).average().orElse(0d);
    }

    private ActivityUtils() {
	// does nothing
    }

}
