package de.tbuchloh.fitlogtotcxconverter.fitlog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Location")
public class LocationFL {

	@XmlAttribute(name = "Name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("LocationFL [name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
