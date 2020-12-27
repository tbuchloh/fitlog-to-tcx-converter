package de.tbuchloh.fitlogtotcxconverter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public abstract class TestUtils {

	public static XMLGregorianCalendar createXmlGregorianCalendar(final String created)
			throws DatatypeConfigurationException {
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(created);
	}

	private TestUtils() {
		// does nothing
	}

}
