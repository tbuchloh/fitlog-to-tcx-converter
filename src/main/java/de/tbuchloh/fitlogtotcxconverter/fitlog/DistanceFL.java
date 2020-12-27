package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Distance")
public class DistanceFL {

    @XmlAttribute(name = "TotalMeters")
    private BigDecimal totalMeters;

    public BigDecimal getTotalMeters() {
	return totalMeters;
    }

    public void setTotalMeters(final BigDecimal totalMeters) {
	this.totalMeters = totalMeters;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("DistanceFL [totalMeters=");
	builder.append(totalMeters);
	builder.append("]");
	return builder.toString();
    }

}
