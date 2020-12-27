package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Duration")
public class DurationFL {

    @XmlAttribute(name = "TotalSeconds")
    private BigDecimal totalSeconds;

    public BigDecimal getTotalSeconds() {
	return totalSeconds;
    }

    public void setTotalSeconds(final BigDecimal totalSeconds) {
	this.totalSeconds = totalSeconds;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("DurationFL [totalSeconds=");
	builder.append(totalSeconds);
	builder.append("]");
	return builder.toString();
    }

}
