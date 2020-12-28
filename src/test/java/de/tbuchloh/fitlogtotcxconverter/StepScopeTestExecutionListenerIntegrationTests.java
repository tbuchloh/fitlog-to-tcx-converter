package de.tbuchloh.fitlogtotcxconverter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.tbuchloh.fitlogtotcxconverter.fitlog.ActivityFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.CaloriesFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.CategoryFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.DistanceFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.DurationFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.EquipmentItemFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.EquipmentUsedFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.HeartRateFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.LapFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.LapsFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.LocationFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.MetadataFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.PtFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.TrackFL;
import de.tbuchloh.fitlogtotcxconverter.fitlog.WeatherFL;

@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FitlogToTcxConverterApplication.class)
public class StepScopeTestExecutionListenerIntegrationTests {

	static class ActivityBuilder {

		private final ActivityFL obj = new ActivityFL();

		public ActivityBuilder addEquipmentItem(final EquipmentItemFL itemFL) {
			final var r = Optional.ofNullable(obj.getEquipmentUsed()).orElse(new EquipmentUsedFL());
			r.getItems().add(itemFL);
			obj.setEquipmentUsed(r);
			return this;
		}

		public ActivityBuilder addLap(final LapFL lap) {
			final var r = Optional.ofNullable(obj.getLaps()).orElse(new LapsFL());
			r.getLaps().add(lap);
			obj.setLaps(r);
			return this;
		}

		public ActivityFL build() {
			return obj;
		}

		public ActivityBuilder calories(final String value) {
			final var r = new CaloriesFL();
			r.setTotalCal(new BigDecimal(value));
			obj.setCalories(r);
			return this;
		}

		public ActivityBuilder category(final String id, final String name) {
			final var r = new CategoryFL();
			r.setId(id);
			r.setName(name);
			obj.setCategory(r);
			return this;
		}

		public ActivityBuilder distance(final String value) {
			final var expDur = new DistanceFL();
			expDur.setTotalMeters(new BigDecimal(value));
			obj.setDistance(expDur);
			return this;
		}

		public ActivityBuilder duration(final String value) {
			final var r = new DurationFL();
			r.setTotalSeconds(new BigDecimal(value));
			obj.setDuration(r);
			return this;
		}

		public ActivityBuilder id(final String value) {
			obj.setId(value);
			return this;
		}

		public ActivityBuilder location(final String value) {
			final var r = new LocationFL();
			r.setName(value);
			obj.setLocation(r);
			return this;
		}

		public ActivityBuilder metadata(final String source, final String created, final String modified)
				throws DatatypeConfigurationException {
			final var meta = new MetadataFL();
			meta.setSource(source);
			meta.setCreated(TestUtils.createXmlGregorianCalendar(created));
			meta.setModified(TestUtils.createXmlGregorianCalendar(modified));
			obj.setMetadata(meta);
			return this;
		}

		public ActivityBuilder name(final String value) {
			obj.setName(value);
			return this;
		}

		public ActivityBuilder notes(final String value) {
			obj.setNotes(value);
			return this;
		}

		ActivityBuilder startTime(final String value) throws DatatypeConfigurationException {
			obj.setStartTime(TestUtils.createXmlGregorianCalendar(value));
			return this;
		}

		public ActivityBuilder track(final TrackFL value) {
			obj.setTrack(value);
			return this;
		}

		public ActivityBuilder weather(final String conditions, final String temp) {
			final var weather = new WeatherFL();
			weather.setConditions(conditions);
			weather.setTemp(new BigDecimal(temp));
			obj.setWeather(weather);
			return this;
		}

	}

	public static class EquipmentItemBuilder {

		private final EquipmentItemFL obj = new EquipmentItemFL();

		public EquipmentItemFL build() {
			return obj;
		}

		public EquipmentItemBuilder id(final String value) {
			obj.setId(value);
			return this;
		}

		public EquipmentItemBuilder name(final String value) {
			obj.setName(value);
			return this;
		}

	}

	static class LapBuilder {

		private final LapFL obj = new LapFL();

		public LapFL build() {
			return obj;
		}

		public LapBuilder calories(final String value) {
			final var r = new CaloriesFL();
			r.setTotalCal(new BigDecimal(value));
			obj.setCalories(r);
			return this;
		}

		public LapBuilder distance(final String value) {
			final var r = new DistanceFL();
			r.setTotalMeters(new BigDecimal(value));
			obj.setDistance(r);
			return this;
		}

		public LapBuilder durationSeconds(final String value) {
			obj.setDurationSeconds(new BigDecimal(value));
			return this;
		}

		public LapBuilder heartReate(final String value) {
			final var r = new HeartRateFL();
			r.setAverageBPM(Short.parseShort(value));
			obj.setHeartRate(r);
			return this;
		}

		public LapBuilder startTime(final String value) throws DatatypeConfigurationException {
			obj.setStartTime(TestUtils.createXmlGregorianCalendar(value));
			return this;
		}

	}

	public static class PtBuilder {

		private final PtFL obj = new PtFL();

		public PtFL build() {
			return obj;
		}

		public PtBuilder dist(final String value) {
			obj.setDist(new BigDecimal(value));
			return this;
		}

		public PtBuilder ele(final String value) {
			obj.setEle(new BigDecimal(value));
			return this;
		}

		public PtBuilder hr(final String value) {
			obj.setHr(Integer.parseInt(value));
			return this;
		}

		public PtBuilder lat(final String value) {
			obj.setLat(new BigDecimal(value));
			return this;
		}

		public PtBuilder lon(final String value) {
			obj.setLon(new BigDecimal(value));
			return this;
		}

		public PtBuilder tm(final String value) {
			obj.setTm(Integer.parseInt(value));
			return this;
		}

	}

	public static class TrackBuilder {

		private final TrackFL obj = new TrackFL();

		public TrackBuilder addPt(final PtFL value) {
			obj.getPts().add(value);
			return this;
		}

		public TrackFL build() {
			return obj;
		}

		public TrackBuilder startTime(final String value) throws DatatypeConfigurationException {
			obj.setStartTime(TestUtils.createXmlGregorianCalendar(value));
			return this;
		}

	}

	@Autowired
	private ItemStreamReader<ActivityFL> reader;

	public StepExecution getStepExecution() {
		final var paramBuilder = new JobParametersBuilder()
				.addString("input.file", "src/test/resources/test_cases_1.fitlog")
				.addString("output.file", "target/example1.tcx");

		final var execution = MetaDataInstanceFactory.createStepExecution(paramBuilder.toJobParameters());
		return execution;
	}

	@Test
	public void testReader() throws Exception {
		// The reader is initialized and bound to the input data
		final var ctx = new ExecutionContext();
		reader.open(ctx);

		final var lap = new LapBuilder().startTime("2019-01-01T13:54:20Z").durationSeconds("3407.17")
				.distance("10009.11").heartReate("144").calories("875").build();
		final var equipmentItem = new EquipmentItemBuilder().id("14b85f56-9736-4ae3-a195-7d2ad8e87614")
				.name("Asics - Kayano Gel 24-Gelb").build();
		final var exp1 = new ActivityBuilder().startTime("2019-01-01T13:54:20Z")
				.id("558ee8ca-61b0-4669-8c1f-286a425e9c75").duration("3407.17").distance("10009.11").calories("875")
				.addLap(lap).addEquipmentItem(equipmentItem).category("403526fd-1791-4b9b-93e1-8439e6708ca4", "Running")
				.location("Hildesheim")
				.metadata("Importiert von Forerunner305 (Unit ID 3802678411)", "2019-01-01T17:55:57Z",
						"2019-01-01T17:56:58Z")
				.name("Galgenberg (See)").weather("Overcast", "27").notes("Batterie leer")
				.track(new TrackBuilder().startTime("2019-01-01T13:54:20Z")
						.addPt(new PtBuilder().tm("0").lat("52.1470832824707").lon("9.98455715179444")
								.ele("112.734565734863").dist("0").hr("66").build())
						.addPt(new PtBuilder().tm("1").lat("52.1470832824707").lon("9.98454189300537")
								.ele("112.725410461426").dist("1.325133").hr("65").build())
						.addPt(new PtBuilder().tm("3402").lat("52.1467361450195").lon("9.98437690734863")
								.ele("114.604164123535").dist("9995.687").hr("142").build())
						.addPt(new PtBuilder().tm("3407").lat("52.146858215332").lon("9.98435115814209")
								.ele("113.901596069336").dist("10009.11").hr("141").build())
						.build())
				.build();

		assertThat(reader.read()).usingRecursiveComparison().isEqualTo(exp1);

		final var exp2 = new ActivityBuilder().startTime("2019-12-29T09:25:25Z")
				.id("b9281dcb-9d7b-4299-8d04-6fe0aaf45c8f").duration("3403.43").distance("10035.3").calories("863")
				.category("403526fd-1791-4b9b-93e1-8439e6708ca4", "Running").location("Hildesheim")
				.metadata("Importiert von Garmin - Forerunner 305 [USB] ", "2019-12-31T15:45:54Z",
						"2019-12-31T15:46:20Z")
				.track(new TrackBuilder().startTime("2019-12-29T09:25:25Z")
						.addPt(new PtBuilder().tm("698").lat("52.1424865722656").lon("9.97064590454102")
								.ele("122.721542358398").dist("2116.796").hr("153").build())
						.addPt(new PtBuilder().tm("1618").lat("52.1314086914062").lon("9.99991798400879")
								.ele("160.704116821289").dist("4873.105").hr("152").build())
						.addPt(new PtBuilder().tm("3281").lat("52.1443252563477").lon("9.98304843902588")
								.ele("133.22216796875").dist("9635.828").hr("158").build())
						.addPt(new PtBuilder().tm("3403").lat("52.1469993591309").lon("9.98429679870606")
								.ele("113.066184997559").dist("10035.3").hr("147").build())
						.build())
				.build();

		assertThat(reader.read()).usingRecursiveComparison()
				.ignoringFields("equipmentUsed", "laps", "name", "weather", "notes").isEqualTo(exp2);

		assertThat(reader.read()).isNotNull();

		assertThat(reader.read()).isNull();
	}

}
