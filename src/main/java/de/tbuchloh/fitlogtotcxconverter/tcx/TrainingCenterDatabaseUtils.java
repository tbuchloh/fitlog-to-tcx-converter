package de.tbuchloh.fitlogtotcxconverter.tcx;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.SchemaFactory;

import org.springframework.core.io.Resource;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;

public abstract class TrainingCenterDatabaseUtils {

    public static final class LoadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LoadException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    private static final class ValidationErrorHandler implements ErrorHandler {

	private List<SAXParseException> errors = new ArrayList<>();

	@Override
	public void error(SAXParseException exception) throws SAXException {
	    errors.add(exception);
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
	    errors.add(exception);
	}

	public List<SAXParseException> getErrors() {
	    return Collections.unmodifiableList(errors);
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
	    errors.add(exception);
	}

    }

    public static final class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ValidationException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    public static int getActivityCount(TrainingCenterDatabaseT tdb) {
	return tdb.getActivities().getActivity().size();
    }

    public static TrainingCenterDatabaseT loadTrainingCenterDatabase(final Resource resource) throws LoadException {
	try {
	    final var staxSource = new StAXSource(
		    XMLInputFactory.newInstance().createXMLEventReader(resource.getInputStream()));
	    final var marshaller = new Jaxb2Marshaller();
	    marshaller.setPackagesToScan("com.garmin.xmlschemas.trainingcenterdatabase.v2");
	    marshaller.setMappedClass(TrainingCenterDatabaseT.class);
	    return (TrainingCenterDatabaseT) marshaller.unmarshal(staxSource);
	} catch (XmlMappingException | XMLStreamException | FactoryConfigurationError | IOException e) {
	    throw new LoadException("could not load training db!", e);
	}
    }

    public static List<SAXParseException> validate(final Resource resource) {
	try {
	    final var schemaRes = Objects
		    .requireNonNull(TrainingCenterDatabaseUtils.class.getResource("/TrainingCenterDatabasev2.xsd"));
	    final var schema = SchemaFactory.newDefaultInstance().newSchema(schemaRes);
	    final var staxSource = new StAXSource(
		    XMLInputFactory.newInstance().createXMLEventReader(resource.getInputStream()));
	    final var validator = schema.newValidator();
	    final var errorHandler = new ValidationErrorHandler();
	    validator.setErrorHandler(errorHandler);
	    validator.validate(staxSource);
	    return errorHandler.getErrors();
	} catch (SAXException | XMLStreamException | FactoryConfigurationError | IOException e) {
	    throw new ValidationException("could not validate training db!", e);
	}
    }

    public static BigDecimal distanceMeters(final TrainingCenterDatabaseT tdb) {
	final var sumAsDouble = tdb.getActivities().getActivity().stream().flatMap(v -> v.getLap().stream())
		.mapToDouble(ActivityLapT::getDistanceMeters).sum();
	return new BigDecimal(sumAsDouble);
    }
    
    public static BigDecimal totalTimeSeconds(final TrainingCenterDatabaseT tdb) {
	final var sumAsDouble = tdb.getActivities().getActivity().stream().flatMap(v -> v.getLap().stream())
		.mapToDouble(ActivityLapT::getTotalTimeSeconds).sum();
	return new BigDecimal(sumAsDouble);
    }

    private TrainingCenterDatabaseUtils() {
	// does nothing
    }

}
