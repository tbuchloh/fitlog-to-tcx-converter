package de.tbuchloh.fitlogtotcxconverter.geodata;

import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Coord implements Comparable<Coord> {

	private final ImmutablePair<Double, Double> pair;

	public Coord(final double lat, final double lon) {
		this.pair = new ImmutablePair<>(lat, lon);
	}

	@Override
	public int compareTo(final Coord o) {
		return pair.compareTo(pair);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final var other = (Coord) obj;
		return Objects.equals(pair, other.pair);
	}

	public double getLat() {
		return pair.getLeft();
	}

	public double getLon() {
		return pair.getRight();
	}

	@Override
	public int hashCode() {
		return Objects.hash(pair);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("Coord [pair=");
		builder.append(pair);
		builder.append("]");
		return builder.toString();
	}

}
