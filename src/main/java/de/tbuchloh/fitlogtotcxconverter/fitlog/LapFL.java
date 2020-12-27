package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement(name = "Lap")
public class LapFL {
    @XmlElement(name = "Calories")
    private CaloriesFL calories;

    @XmlElement(name = "Distance")
    private DistanceFL distance;

    @XmlAttribute(name = "DurationSeconds")
    private BigDecimal durationSeconds;

    @XmlElement(name = "HeartRate")
    private HeartRateFL heartRate;

    @XmlAttribute(name = "StartTime")
    private XMLGregorianCalendar startTime;

    public CaloriesFL getCalories() {
	return calories;
    }

    public DistanceFL getDistance() {
        return distance;
    }

    public BigDecimal getDurationSeconds() {
	return durationSeconds;
    }

    public HeartRateFL getHeartRate() {
        return heartRate;
    }

    public XMLGregorianCalendar getStartTime() {
	return startTime;
    }

    public void setCalories(final CaloriesFL calories) {
	this.calories = calories;
    }

    public void setDistance(final DistanceFL distance) {
        this.distance = distance;
    }

    public void setDurationSeconds(final BigDecimal durationSeconds) {
	this.durationSeconds = durationSeconds;
    }

    public void setHeartRate(final HeartRateFL heartRate) {
        this.heartRate = heartRate;
    }

    public void setStartTime(final XMLGregorianCalendar startTime) {
	this.startTime = startTime;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("LapFL [calories=");
	builder.append(calories);
	builder.append(", durationSeconds=");
	builder.append(durationSeconds);
	builder.append(", startTime=");
	builder.append(startTime);
	builder.append(", distance=");
	builder.append(distance);
	builder.append(", heartRate=");
	builder.append(heartRate);
	builder.append("]");
	return builder.toString();
    }

}
