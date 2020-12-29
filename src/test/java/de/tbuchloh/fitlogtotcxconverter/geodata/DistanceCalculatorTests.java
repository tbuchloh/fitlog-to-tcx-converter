package de.tbuchloh.fitlogtotcxconverter.geodata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

public class DistanceCalculatorTests {

	@Test
	void distance_berlinBrandTor_to_lissabonTejoBridge() {
		final var brandTor = new Coord(52.5164, 13.3777);
		final var tejoBridge = new Coord(38.692668, -9.177944);

		// https://www.kompf.de/gps/distcalc.html
		// Berlin Brandenburger Tor - Lissabon Tejo Brücke 2228.929 km 2334.931 km
		// 2317.722 km
		assertThat(DistanceCalculator.dist(brandTor, tejoBridge, DistanceUnit.KM)).isEqualTo(2317.722, within(0.1));
		assertThat(DistanceCalculator.dist(tejoBridge, brandTor, DistanceUnit.KM)).isEqualTo(2317.722, within(0.1));
	}

	@Test
	void distance_example_from_geodatasource() {
		final var p1 = new Coord(32.9697, -96.80322);
		final var p2 = new Coord(29.46786, -98.53506);

		assertThat(DistanceCalculator.dist(p1, p2, DistanceUnit.MILES)).isCloseTo(262.6777938054349, within(0.4));
		assertThat(DistanceCalculator.dist(p1, p2, DistanceUnit.KM)).isEqualTo(422.73893139401383, within(0.52));
		assertThat(DistanceCalculator.dist(p1, p2, DistanceUnit.NAUTIC_MILES)).isEqualTo(228.10939614063963,
				within(0.4));
	}

	@Test
	void distance_ruesselsheimHbf_to_ruesselsheimOpelbruecke() {
		final var hbf = new Coord(49.9917, 8.41321);
		final var opelbruecke = new Coord(50.0049, 8.42182);

		// https://www.kompf.de/gps/distcalc.html
		// Rüsselsheim Bahnhof - Rüsselsheim Opelbrücke km 1.593 km 1.593 km
		assertThat(DistanceCalculator.dist(hbf, opelbruecke, DistanceUnit.KM)).isEqualTo(1.593, within(0.1));
	}

	@Test
	void distance_samePos_return_zero() {
		final var brandTor = new Coord(52.5164, 13.3777);

		assertThat(DistanceCalculator.dist(brandTor, brandTor, DistanceUnit.KM)).isEqualTo(0);
	}

}
