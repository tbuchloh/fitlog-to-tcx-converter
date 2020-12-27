package de.tbuchloh.fitlogtotcxconverter.fitlog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement(name = "Metadata")
public class MetadataFL {

	@XmlAttribute(name = "Created")
	private XMLGregorianCalendar created;

	@XmlAttribute(name = "Modified")
	private XMLGregorianCalendar modified;

	@XmlAttribute(name = "Source")
	private String source;

	public XMLGregorianCalendar getCreated() {
		return created;
	}

	public XMLGregorianCalendar getModified() {
		return modified;
	}

	public String getSource() {
		return source;
	}

	public void setCreated(final XMLGregorianCalendar created) {
		this.created = created;
	}

	public void setModified(final XMLGregorianCalendar modified) {
		this.modified = modified;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("MetadataFL [created=");
		builder.append(created);
		builder.append(", modified=");
		builder.append(modified);
		builder.append(", source=");
		builder.append(source);
		builder.append("]");
		return builder.toString();
	}

}
