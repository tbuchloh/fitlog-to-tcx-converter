/* inspired by: https://www.geodatasource.com/developers/java */
/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::                                                                         :*/
/*::  This routine calculates the distance between two points (given the     :*/
/*::  latitude/longitude of those points). It is being used to calculate     :*/
/*::  the distance between two locations using GeoDataSource (TM) products   :*/
/*::                                                                         :*/
/*::  Definitions:                                                           :*/
/*::    Southern latitudes are negative, eastern longitudes are positive     :*/
/*::                                                                         :*/
/*::  Function parameters:                                                   :*/
/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
/*::    unit = the unit you desire for results                               :*/
/*::           where: 'M' is statute miles (default)                         :*/
/*::                  'K' is kilometers                                      :*/
/*::                  'N' is nautical miles                                  :*/
/*::  Worldwide cities and other features databases with latitude longitude  :*/
/*::  are available at https://www.geodatasource.com                         :*/
/*::                                                                         :*/
/*::  For enquiries, please contact sales@geodatasource.com                  :*/
/*::                                                                         :*/
/*::  Official Web site: https://www.geodatasource.com                       :*/
/*::                                                                         :*/
/*::           GeoDataSource.com (C) All Rights Reserved 2019                :*/
/*::                                                                         :*/
/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

package de.tbuchloh.fitlogtotcxconverter.geodata;

import org.springframework.util.Assert;

public class DistanceCalculator {

	public static double dist(final Coord p1, final Coord p2, final DistanceUnit unit) {
		// dist = 6378.388 * acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) *
		// cos(lon2 - lon1))
		if (p1.equals(p2)) {
			return 0;
		}
		final var theta = p1.getLon() - p2.getLon();
		var dist = Math.sin(Math.toRadians(p1.getLat())) * Math.sin(Math.toRadians(p2.getLat()))
				+ Math.cos(Math.toRadians(p1.getLat())) * Math.cos(Math.toRadians(p2.getLat()))
						* Math.cos(Math.toRadians(theta));
		dist = Math.acos(dist);
		dist = 6378.388 * dist;
		switch (unit) {
		case KM:
			break;
		case NAUTIC_MILES:
			dist = dist / 1.609344 * 0.8684;
			break;
		case MILES:
			dist = dist / 1.609344;
			break;
		default:
			Assert.state(false, "unknown value: " + unit);
		}
		return dist;
	}

}
