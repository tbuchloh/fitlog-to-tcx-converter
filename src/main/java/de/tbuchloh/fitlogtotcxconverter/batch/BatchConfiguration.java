package de.tbuchloh.fitlogtotcxconverter.batch;

import java.io.IOException;
import java.util.Objects;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.lang.NonNull;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;

import de.tbuchloh.fitlogtotcxconverter.fitlog.ActivityFL;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	public static final QName ACTIVITIES_QNAME = new QName("http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2",
			"Activities");

	private static final String ACTIVITY_FRAGMENT_ROOT_NAME = "{http://www.zonefivesoftware.com/xmlschemas/FitnessLogbook/v3}Activity";

	public static final QName TRAINING_DB_QNAME = new QName(
			"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2", "TrainingCenterDatabase");

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job convertFitlogFileJob(final JobCompletionNotificationListener listener, final Step step1,
			final Step verifyTcxFileStep) {
		return jobBuilderFactory.get("convertFitlogFileJob").incrementer(new RunIdIncrementer()).listener(listener)
				.flow(step1).next(verifyTcxFileStep).end().build();
	}

	@Bean
	public ItemProcessor<ActivityFL, JAXBElement<ActivityT>> processor() {
		return new FitlogToTcxActivityItemProcessor();
	}

	@StepScope
	@Bean
	public StaxEventItemReader<ActivityFL> reader(@Value("#{jobParameters['input.file']}") final String path) {
		Objects.requireNonNull(path);

		final var marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan("de.tbuchloh.fitlogtotcxconverter.fitlog");
		return new StaxEventItemReaderBuilder<ActivityFL>().name("fitlogFileResourceReader") //
				.resource(new FileSystemResource(path)) //
				.addFragmentRootElements(ACTIVITY_FRAGMENT_ROOT_NAME) //
				.unmarshaller(marshaller) //
				.build();
	}

	@Bean
	public Step step1(final ItemReader<ActivityFL> reader, final ItemWriter<JAXBElement<ActivityT>> writer) {
		return stepBuilderFactory.get("step1").<ActivityFL, JAXBElement<ActivityT>>chunk(10).reader(reader)
				.processor(processor()).writer(writer).build();
	}

	@Bean
	public Step verifyTcxFileStep() {
		return stepBuilderFactory.get("verifyTcxFileStep").tasklet(new VerifyTcxFileTasklet()).build();
	}

	@StepScope
	@Bean
	public StaxEventItemWriter<JAXBElement<ActivityT>> writer(
			@NonNull @Value("#{jobParameters['output.file']}") final String outputFilePath) {
		Objects.requireNonNull(outputFilePath);

		final var marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(ActivityT.class);
		marshaller.setCheckForXmlRootElement(false);
		marshaller.setSupportJaxbElementClass(true);
		final var eventFactory = XMLEventFactory.newInstance();
		return new StaxEventItemWriterBuilder<JAXBElement<ActivityT>>().name("tcxFileResourceWriter") //
				.resource(new FileSystemResource(outputFilePath)) //
				.rootTagName(TRAINING_DB_QNAME.toString()).marshaller(marshaller) //
				.headerCallback(writer -> {
					try {
						writer.add(eventFactory.createStartElement("", ACTIVITIES_QNAME.getNamespaceURI(),
								ACTIVITIES_QNAME.getLocalPart()));
					} catch (final XMLStreamException e) {
						throw new IOException("could not start element 'Activities'", e);
					}
				}).footerCallback(writer -> {
					try {
						writer.add(eventFactory.createEndElement("", ACTIVITIES_QNAME.getNamespaceURI(),
								ACTIVITIES_QNAME.getLocalPart()));
					} catch (final XMLStreamException e) {
						throw new IOException("could not end element 'Activities'", e);
					}
				}).build();
	}
}
