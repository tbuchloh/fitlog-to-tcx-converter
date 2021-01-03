package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "HeartRate")
public class HeartRateFL {

	@XmlAttribute(name = "AverageBPM")
	private BigDecimal averageBPM;

	public BigDecimal getAverageBPM() {
		return averageBPM;
	}

	public void setAverageBPM(final BigDecimal averageBPM) {
		this.averageBPM = averageBPM;
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("HeartRateFL [averageBPM=");
		builder.append(averageBPM);
		builder.append("]");
		return builder.toString();
	}
}
