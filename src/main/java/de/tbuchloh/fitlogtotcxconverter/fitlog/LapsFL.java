package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Laps")
public class LapsFL {

    @XmlElement(name = "Lap")
    private List<LapFL> laps;

    public List<LapFL> getLaps() {
	if (laps == null) {
	    laps = new ArrayList<>();
	}
	return laps;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("LapsFL [laps=");
	builder.append(laps);
	builder.append("]");
	return builder.toString();
    }

}
