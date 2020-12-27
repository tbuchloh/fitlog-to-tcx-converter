package de.tbuchloh.fitlogtotcxconverter.fitlog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "EquipmentItem")
public class EquipmentItemFL {

	@XmlAttribute(name = "Id")
	private String id;

	@XmlAttribute(name = "Name")
	private String name;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("EquipmentItemFL [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
