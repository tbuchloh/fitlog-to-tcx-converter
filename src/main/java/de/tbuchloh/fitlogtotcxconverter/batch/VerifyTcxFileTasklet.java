package de.tbuchloh.fitlogtotcxconverter.batch;

import static de.tbuchloh.fitlogtotcxconverter.tcx.TrainingCenterDatabaseUtils.distanceMeters;
import static de.tbuchloh.fitlogtotcxconverter.tcx.TrainingCenterDatabaseUtils.getActivityCount;
import static de.tbuchloh.fitlogtotcxconverter.tcx.TrainingCenterDatabaseUtils.totalTimeSeconds;

import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;
import org.xml.sax.SAXParseException;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.PositionT;

import de.tbuchloh.fitlogtotcxconverter.tcx.ActivityUtils;
import de.tbuchloh.fitlogtotcxconverter.tcx.TrainingCenterDatabaseUtils;

public class VerifyTcxFileTasklet implements Tasklet {

	private static final Logger log = LoggerFactory.getLogger(VerifyTcxFileTasklet.class);

	@Override
	public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
		final var path = contribution.getStepExecution().getJobParameters().getString("output.file");
		final var tcxResource = new FileSystemResource(path);
		final var tdb = TrainingCenterDatabaseUtils.loadTrainingCenterDatabase(tcxResource);
		log.info(StringUtils.repeat('-', 120));
		log.info("training database activityCount:{}, distance:{}, time:{}", getActivityCount(tdb), distanceMeters(tdb),
				totalTimeSeconds(tdb));
		tdb.getActivities().getActivity().stream().forEach(act -> {
			log.info(
					"activity id:{}, lapDistance:{}, trackpointDistance:{}, totalTime:{}, calories:{}, lapCnt:{}, trackpointCnt:{}, avgHr:{}",
					act.getId(), ActivityUtils.getLapDistanceSum(act), ActivityUtils.getTrackpointDistance(act),
					ActivityUtils.getLapTotalTimeSum(act), ActivityUtils.getLapCaloriesSum(act), act.getLap().size(),
					ActivityUtils.getTrackpointCount(act), ActivityUtils.getTrackpointHeartRateAverage(act));
			act.getLap().stream().flatMap(lap -> lap.getTrack().stream())
					.flatMap(track -> track.getTrackpoint().stream()).forEach(tp -> {
						log.debug("    trackpoint: startTime:{}, distance:{}, lat:{}, lon:{}", tp.getTime(),
								tp.getDistanceMeters(),
								Optional.ofNullable(tp.getPosition()).orElse(new PositionT()).getLatitudeDegrees(),
								Optional.ofNullable(tp.getPosition()).orElse(new PositionT()).getLongitudeDegrees());
					});
			;
		});
		log.info(StringUtils.repeat('-', 120));

		final var errors = TrainingCenterDatabaseUtils.validate(tcxResource);
		if (errors.isEmpty()) {
			log.info("training db is valid xml.");
		} else {
			log.error("training db validation failed! {}",
					errors.stream().map(SAXParseException::getMessage).collect(Collectors.joining(" | ")));
			contribution.setExitStatus(ExitStatus.FAILED);
		}
		return RepeatStatus.FINISHED;
	}

}
