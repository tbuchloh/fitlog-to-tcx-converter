package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Calories")
public class CaloriesFL {

    @XmlAttribute(name = "TotalCal")
    private BigDecimal totalCal;

    public BigDecimal getTotalCal() {
	return totalCal;
    }

    public void setTotalCal(final BigDecimal totalCal) {
	this.totalCal = totalCal;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("CaloriesFL [totalCal=");
	builder.append(totalCal);
	builder.append("]");
	return builder.toString();
    }

}
