package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Weather")
public class WeatherFL {

    @XmlAttribute(name = "Conditions")
    private String conditions;

    @XmlAttribute(name = "Temp")
    private BigDecimal temp;

    public String getConditions() {
        return conditions;
    }

    public BigDecimal getTemp() {
        return temp;
    }

    public void setConditions(final String conditions) {
        this.conditions = conditions;
    }

    public void setTemp(final BigDecimal temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("WeatherFL [conditions=");
	builder.append(conditions);
	builder.append(", temp=");
	builder.append(temp);
	builder.append("]");
	return builder.toString();
    }
}
