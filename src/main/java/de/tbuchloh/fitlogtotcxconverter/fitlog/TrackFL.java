package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement(name = "Track")
public class TrackFL {

    @XmlElement(name = "pt")
    private List<PtFL> pts;

    @XmlAttribute(name = "StartTime")
    private XMLGregorianCalendar startTime;

    public List<PtFL> getPts() {
	if (pts == null) {
	    pts = new ArrayList<>();
	}
	return pts;
    }

    public XMLGregorianCalendar getStartTime() {
	return startTime;
    }

    public void setStartTime(final XMLGregorianCalendar startTime) {
	this.startTime = startTime;
    }

    @Override
    public String toString() {
	final var maxLen = 10;
	final var builder = new StringBuilder();
	builder.append("TrackFL [pts=");
	builder.append(pts != null ? pts.subList(0, Math.min(pts.size(), maxLen)) : null);
	builder.append(", startTime=");
	builder.append(startTime);
	builder.append("]");
	return builder.toString();
    }

}
