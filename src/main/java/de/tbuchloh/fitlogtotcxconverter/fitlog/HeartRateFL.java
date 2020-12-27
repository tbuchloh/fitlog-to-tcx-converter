package de.tbuchloh.fitlogtotcxconverter.fitlog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "HeartRate")
public class HeartRateFL {

	@XmlAttribute(name = "AverageBPM")
	private Short averageBPM;

	public Short getAverageBPM() {
		return averageBPM;
	}

	public void setAverageBPM(final Short averageBPM) {
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
