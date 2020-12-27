package de.tbuchloh.fitlogtotcxconverter.fitlog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pt")
public class PtFL {

    @XmlAttribute(name = "dist")
    private BigDecimal dist;

    @XmlAttribute(name = "ele")
    private BigDecimal ele;

    @XmlAttribute(name = "hr")
    private Integer hr;

    @XmlAttribute(name = "lat")
    private BigDecimal lat;

    @XmlAttribute(name = "lon")
    private BigDecimal lon;

    @XmlAttribute(name = "tm")
    private Integer tm;

    public BigDecimal getDist() {
	return dist;
    }

    public BigDecimal getEle() {
	return ele;
    }

    public Integer getHr() {
	return hr;
    }

    public BigDecimal getLat() {
	return lat;
    }

    public BigDecimal getLon() {
	return lon;
    }

    public Integer getTm() {
	return tm;
    }

    public void setDist(final BigDecimal dist) {
	this.dist = dist;
    }

    public void setEle(final BigDecimal ele) {
	this.ele = ele;
    }

    public void setHr(final Integer hr) {
	this.hr = hr;
    }

    public void setLat(final BigDecimal lat) {
	this.lat = lat;
    }

    public void setLon(final BigDecimal lon) {
	this.lon = lon;
    }

    public void setTm(final Integer tm) {
	this.tm = tm;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("PtFL [tm=");
	builder.append(tm);
	builder.append(", lat=");
	builder.append(lat);
	builder.append(", lon=");
	builder.append(lon);
	builder.append(", ele=");
	builder.append(ele);
	builder.append(", dist=");
	builder.append(dist);
	builder.append(", hr=");
	builder.append(hr);
	builder.append("]");
	return builder.toString();
    }
}
