package de.tbuchloh.fitlogtotcxconverter.geodata;

import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Coord implements Comparable<Coord> {
    
    private ImmutablePair<Double, Double> pair;

    public Coord(double lat, double lon) {
	this.pair = new ImmutablePair<Double, Double>(lat, lon);
    }

    public double getLat() {
	return pair.getLeft();
    }
    
    public double getLon() {
	return pair.getRight();
    }

    @Override
    public int compareTo(Coord o) {
	return pair.compareTo(pair);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Coord [pair=");
	builder.append(pair);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	return Objects.hash(pair);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	Coord other = (Coord) obj;
	return Objects.equals(pair, other.pair);
    }

}
