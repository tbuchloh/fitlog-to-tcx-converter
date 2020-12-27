package de.tbuchloh.fitlogtotcxconverter;

import static de.tbuchloh.fitlogtotcxconverter.TestUtils.createXmlGregorianCalendar;
import static de.tbuchloh.fitlogtotcxconverter.tcx.ActivityUtils.getLapCaloriesSum;
import static de.tbuchloh.fitlogtotcxconverter.tcx.ActivityUtils.getLapDistanceSum;
import static de.tbuchloh.fitlogtotcxconverter.tcx.ActivityUtils.getLapTotalTimeSum;
import static de.tbuchloh.fitlogtotcxconverter.tcx.ActivityUtils.getTrackpointCount;
import static de.tbuchloh.fitlogtotcxconverter.tcx.ActivityUtils.getTrackpointDistance;
import static de.tbuchloh.fitlogtotcxconverter.tcx.TrainingCenterDatabaseUtils.loadTrainingCenterDatabase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.Assert.assertEquals;

import javax.xml.datatype.DatatypeConfigurationException;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.IntensityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.SportT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;

@SpringBootTest
//@SpringBatchTest
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = { BatchConfiguration.class, JobCompletionNotificationListener.class })
class FitlogToTcxConverterApplicationTests {

	@Autowired
	private Job job;

	@Autowired
	private JobLauncher jobLauncher;

	@Test
	public void runTestCases() throws Exception {
		// setup
		final var inputFilePath = "src/test/resources/example1.fitlog";
		final var outputFilePath = "target/FitlogToTcxConverterApplicationTests_runJob.tcx";
		final var expectedActivityCnt = 6;

		// execute
		final var je1 = jobLauncher.run(job, new JobParametersBuilder().addString("input.file", inputFilePath)
				.addString("output.file", outputFilePath).toJobParameters());

		// verify
		verifyJobExecution(je1, expectedActivityCnt);

		final var tdb = verifyTrainingCenterDatabase(outputFilePath, expectedActivityCnt);

		verifyActivity2(tdb);

		verifyActivity3(tdb);

		verifyActivity4(tdb);

		verifyActivity5(tdb);
	}

	private void verifyActivity2(final TrainingCenterDatabaseT tdb) throws DatatypeConfigurationException {
		final var act_2 = tdb.getActivities().getActivity().get(2);
		assertThat(act_2).hasFieldOrPropertyWithValue("id", createXmlGregorianCalendar("2011-04-17T11:00:09Z"))
				.hasFieldOrPropertyWithValue("sport", SportT.RUNNING).hasFieldOrPropertyWithValue("notes",
						"Notes: Platz 12 M30, 47:14,2, 126 insgesamt\n" + "\n"
								+ "128	Buchloh, Tobias	Hildesheim	77	M30	12	116	47:14,2	3777\n"
								+ "Name: Wedekindlauf 2011\n" + "Weather: Cloudy (19)\n"
								+ "Equipment: Asics - Kayano Gel 16 (39eb2cfc-27df-4320-8520-5e3f5314ec30)\n");
		assertThat(act_2.getLap()).hasSize(9);

		final var lap_2_0 = act_2.getLap().get(0);
		assertThat(lap_2_0).hasFieldOrPropertyWithValue("startTime", createXmlGregorianCalendar("2011-04-17T11:00:10Z"))
				.hasFieldOrPropertyWithValue("totalTimeSeconds", 519.45).hasFieldOrPropertyWithValue("calories", 160)
				.hasFieldOrPropertyWithValue("intensity", IntensityT.ACTIVE);
		assertThat(lap_2_0.getDistanceMeters()).isCloseTo(1873.59253, within(10d));
		assertThat(lap_2_0.getTrack()).hasSize(1);
		assertThat(lap_2_0.getTrack().get(0).getTrackpoint()).hasSize(128);
		assertThat(lap_2_0.getAverageHeartRateBpm().getValue()).isEqualTo((short) 165);
		assertThat(lap_2_0.getMaximumHeartRateBpm().getValue()).isEqualTo((short) 175);

		final var lap_2_1 = act_2.getLap().get(1);
		assertThat(lap_2_1).hasFieldOrPropertyWithValue("startTime", createXmlGregorianCalendar("2011-04-17T11:08:49Z"))
				.hasFieldOrPropertyWithValue("totalTimeSeconds", 257.18).hasFieldOrPropertyWithValue("calories", 84)
				.hasFieldOrPropertyWithValue("intensity", IntensityT.ACTIVE);
		assertThat(lap_2_1.getDistanceMeters()).isCloseTo(966.4617, within(10d));
		assertThat(lap_2_1.getTrack()).hasSize(1);
		assertThat(lap_2_1.getTrack().get(0).getTrackpoint()).hasSize(63);
		assertThat(lap_2_1.getAverageHeartRateBpm().getValue()).isEqualTo((short) 173);
		assertThat(lap_2_1.getMaximumHeartRateBpm().getValue()).isEqualTo((short) 177);

		final var lap_2_8 = act_2.getLap().get(8);
		assertThat(lap_2_8).hasFieldOrPropertyWithValue("startTime", createXmlGregorianCalendar("2011-04-17T11:42:32Z"))
				.hasFieldOrPropertyWithValue("totalTimeSeconds", 282.58).hasFieldOrPropertyWithValue("calories", 90)
				.hasFieldOrPropertyWithValue("intensity", IntensityT.ACTIVE);
		assertThat(lap_2_8.getDistanceMeters()).isCloseTo(1039.24219, within(60d));
		assertThat(lap_2_8.getTrack()).hasSize(1);
		assertThat(lap_2_8.getTrack().get(0).getTrackpoint()).hasSizeGreaterThanOrEqualTo(65);
		assertThat(lap_2_8.getAverageHeartRateBpm().getValue()).isEqualTo((short) 183);
		assertThat(lap_2_8.getMaximumHeartRateBpm().getValue()).isEqualTo((short) 189);

		final var softly_2 = new SoftAssertions();
		softly_2.assertThat(getTrackpointCount(act_2)).isEqualTo(674L);
		softly_2.assertThat(getTrackpointDistance(act_2)).isCloseTo(10083.473632812, within(10d));
		softly_2.assertThat(getLapDistanceSum(act_2)).isCloseTo(10083.473632812, within(10d));
		softly_2.assertThat(getLapTotalTimeSum(act_2)).isEqualTo(2824.78);
		softly_2.assertThat(getLapCaloriesSum(act_2)).isEqualTo(877);
		softly_2.assertAll();
	}

	private void verifyActivity3(final TrainingCenterDatabaseT tdb) {
		final var act_3 = tdb.getActivities().getActivity().get(3);
		final var lap_3_0 = act_3.getLap().get(0);
		assertThat(lap_3_0.getTotalTimeSeconds()).isEqualTo(1800d);
		assertThat(lap_3_0.getDistanceMeters()).isEqualTo(6000d);
	}

	private void verifyActivity4(final TrainingCenterDatabaseT tdb) {
		final var act_4 = tdb.getActivities().getActivity().get(4);
		assertThat(act_4.getLap()).hasSizeGreaterThanOrEqualTo(1);
		final var lap_4_0 = act_4.getLap().get(0);
		assertThat(lap_4_0.getTotalTimeSeconds()).isEqualTo(356.19);
		assertThat(lap_4_0.getDistanceMeters()).isEqualTo(1048.883);
		assertThat(lap_4_0.getAverageHeartRateBpm().getValue()).isEqualTo((short) 126);
		assertThat(lap_4_0.getCalories()).isEqualTo(86);

		assertThat(act_4.getLap()).hasSize(2);
		final var lap_4_1 = act_4.getLap().get(1);
		assertThat(lap_4_1.getTotalTimeSeconds()).isEqualTo(1165 - 356.19);
		assertThat(lap_4_1.getDistanceMeters()).isEqualTo(3790 - 1048.883);
		assertThat(lap_4_1.getCalories()).isEqualTo(330 - 86);
	}

	private void verifyActivity5(final TrainingCenterDatabaseT tdb) {
		final var act_5 = tdb.getActivities().getActivity().get(5);
		assertThat(act_5.getLap()).hasSizeGreaterThanOrEqualTo(1);
		final var lap_5_0 = act_5.getLap().get(0);
		assertThat(lap_5_0.getTotalTimeSeconds()).isEqualTo(2245.8);
		assertThat(lap_5_0.getDistanceMeters()).isEqualTo(7100.153);
		assertThat(lap_5_0.getAverageHeartRateBpm().getValue()).isEqualTo((short) 150);
		assertThat(lap_5_0.getCalories()).isEqualTo(629);

		final var softly_5 = new SoftAssertions();
		softly_5.assertThat(getTrackpointCount(act_5)).isEqualTo(487L);
		softly_5.assertThat(getTrackpointDistance(act_5)).isCloseTo(7100, within(1d));
		softly_5.assertThat(getLapDistanceSum(act_5)).isCloseTo(7100, within(1d));
		softly_5.assertThat(getLapTotalTimeSum(act_5)).isEqualTo(2245.8);
		softly_5.assertThat(getLapCaloriesSum(act_5)).isEqualTo(629);
		softly_5.assertAll();
	}

	private void verifyJobExecution(final JobExecution je1, final int expectedActivityCnt) {
		assertEquals(BatchStatus.COMPLETED, je1.getStatus());
		assertEquals(0, je1.getAllFailureExceptions().size());
		assertEquals(2, je1.getStepExecutions().size());

		final var se = je1.getStepExecutions().iterator().next();

		assertThat(se.getStatus()).isEqualTo(BatchStatus.COMPLETED);
		assertThat(se.getReadCount()).isEqualTo(expectedActivityCnt);
		assertThat(se.getWriteCount()).isEqualTo(expectedActivityCnt);
		assertThat(se.getCommitCount()).isEqualTo(1);
	}

	private TrainingCenterDatabaseT verifyTrainingCenterDatabase(final String outputFilePath,
			final int expectedActivityCnt) {
		final var tdb = loadTrainingCenterDatabase(new FileSystemResource(outputFilePath));
		assertThat(tdb.getActivities().getActivity()).hasSize(expectedActivityCnt);
		return tdb;
	}

}
