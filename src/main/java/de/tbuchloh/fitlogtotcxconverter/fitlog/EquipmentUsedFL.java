package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "EquipmentUsed")
public class EquipmentUsedFL {

    @XmlElement(name = "EquipmentItem")
    private List<EquipmentItemFL> items;

    public List<EquipmentItemFL> getItems() {
	if (items == null) {
	    items = new ArrayList<>();
	}
	return items;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("EquipmentUsedFL [items=");
	builder.append(items);
	builder.append("]");
	return builder.toString();
    }

}
