package de.tbuchloh.fitlogtotcxconverter.fitlog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement(name = "Activity")
public class ActivityFL {

    @XmlElement(name = "Calories")
    private CaloriesFL calories;

    @XmlElement(name = "Category")
    private CategoryFL category;

    @XmlElement(name = "Distance")
    private DistanceFL distance;

    @XmlElement(name = "Duration")
    private DurationFL duration;

    @XmlElement(name = "EquipmentUsed")
    private EquipmentUsedFL equipmentUsed;

    @XmlAttribute(name = "Id")
    private String id;

    @XmlElement(name = "Laps")
    private LapsFL laps;

    @XmlElement(name = "Location")
    private LocationFL location;

    @XmlElement(name = "Metadata")
    private MetadataFL metadata;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Notes")
    private String notes;

    @XmlAttribute(name = "StartTime")
    private XMLGregorianCalendar startTime;

    @XmlElement(name = "Track")
    private TrackFL track;

    @XmlElement(name = "Weather")
    private WeatherFL weather;

    public CaloriesFL getCalories() {
	return calories;
    }

    public CategoryFL getCategory() {
	return category;
    }

    public DistanceFL getDistance() {
	return distance;
    }

    public DurationFL getDuration() {
	return duration;
    }

    public EquipmentUsedFL getEquipmentUsed() {
	return equipmentUsed;
    }

    public String getId() {
	return id;
    }

    public LapsFL getLaps() {
	return laps;
    }

    public LocationFL getLocation() {
	return location;
    }

    public MetadataFL getMetadata() {
	return metadata;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public XMLGregorianCalendar getStartTime() {
	return startTime;
    }

    public TrackFL getTrack() {
	return track;
    }

    public WeatherFL getWeather() {
        return weather;
    }

    public void setCalories(final CaloriesFL calories) {
	this.calories = calories;
    }

    public void setCategory(final CategoryFL category) {
	this.category = category;
    }

    public void setDistance(final DistanceFL distance) {
	this.distance = distance;
    }

    public void setDuration(final DurationFL duration) {
	this.duration = duration;
    }

    public void setEquipmentUsed(final EquipmentUsedFL equipmentUsed) {
	this.equipmentUsed = equipmentUsed;
    }

    public void setId(final String id) {
	this.id = id;
    }

    public void setLaps(final LapsFL laps) {
	this.laps = laps;
    }

    public void setLocation(final LocationFL location) {
	this.location = location;
    }

    public void setMetadata(final MetadataFL metadata) {
	this.metadata = metadata;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public void setStartTime(final XMLGregorianCalendar startTime) {
	this.startTime = startTime;
    }

    public void setTrack(final TrackFL track) {
	this.track = track;
    }

    public void setWeather(final WeatherFL weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("ActivityFL [calories=");
	builder.append(calories);
	builder.append(", category=");
	builder.append(category);
	builder.append(", distance=");
	builder.append(distance);
	builder.append(", duration=");
	builder.append(duration);
	builder.append(", equipmentUsed=");
	builder.append(equipmentUsed);
	builder.append(", id=");
	builder.append(id);
	builder.append(", laps=");
	builder.append(laps);
	builder.append(", location=");
	builder.append(location);
	builder.append(", metadata=");
	builder.append(metadata);
	builder.append(", name=");
	builder.append(name);
	builder.append(", notes=");
	builder.append(notes);
	builder.append(", startTime=");
	builder.append(startTime);
	builder.append(", track=");
	builder.append(track);
	builder.append(", weather=");
	builder.append(weather);
	builder.append("]");
	return builder.toString();
    }
}
