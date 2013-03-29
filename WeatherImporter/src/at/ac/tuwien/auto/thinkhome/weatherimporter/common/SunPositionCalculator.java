package at.ac.tuwien.auto.thinkhome.weatherimporter.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import at.ac.tuwien.auto.thinkhome.weatherimporter.model.GeographicalPosition;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.SunPosition;


// stolen from http://www.psa.es/sdg/sunpos.htm
// see http://wiki.happylab.at/w/Sonnenstandsberechner_(f%C3%BCr_sun_tracker_devices)
// TODO document
public class SunPositionCalculator {
	private GeographicalPosition position;

	private static final double EARTH_MEAN_RADIUS = 6371.01;
	private static final double ASTRONOMICAL_UNIT = 149597890;

	private Logger log;
	
	public SunPositionCalculator(GeographicalPosition position) {
		this.position = position;
		
		log = Logger.getLogger(SunPositionCalculator.class);
	}

	public SunPosition calculate() {
		return calculate(new Date());
	}

	public SunPosition calculate(Date date) {
		// Calculate difference in days between the current Julian Day
		// and JD 2451545.0, which is noon 1 January 2000 Universal Time
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		log.debug("Calculating sun position for: " + simpleDateFormat.format(date));

		// Calculate time of the day in UT decimal hours
		double decimalHours = calendar.get(Calendar.HOUR_OF_DAY)
				+ (calendar.get(Calendar.MINUTE) + calendar
						.get(Calendar.SECOND) / 60.0) / 60.0;
		// Calculate current Julian Day
		long aux1 = (calendar.get(Calendar.MONTH) + 1 - 14) / 12;
		long aux2 = (1461 * (calendar.get(Calendar.YEAR) + 4800 + aux1)) / 4
				+ (367 * (calendar.get(Calendar.MONTH) + 1 - 2 - 12 * aux1))
				/ 12
				- (3 * ((calendar.get(Calendar.YEAR) + 4900 + aux1) / 100)) / 4
				+ calendar.get(Calendar.DAY_OF_MONTH) - 32075;
		double julianDate = (double) (aux2) - 0.5 + decimalHours / 24.0;
		// Calculate difference between current Julian Day and JD 2451545.0
		double dElapsedJulianDays = julianDate - 2451545.0;

		// Calculate ecliptic coordinates (ecliptic longitude and obliquity of
		// the ecliptic in radians but without limiting the angle to be less
		// than 2*Pi (i.e., the result may be greater than 2*Pi)
		double omega = 2.1429 - 0.0010394594 * dElapsedJulianDays;
		double meanLongitude = 4.8950630 + 0.017202791698 * dElapsedJulianDays; // Radians
		double meanAnomaly = 6.2400600 + 0.0172019699 * dElapsedJulianDays;
		double eclipticLongitude = meanLongitude + 0.03341607
				* Math.sin(meanAnomaly) + 0.00034894
				* Math.sin(2 * meanAnomaly) - 0.0001134 - 0.0000203
				* Math.sin(omega);
		double eclipticObliquity = 0.4090928 - 6.2140e-9 * dElapsedJulianDays
				+ 0.0000396 * Math.cos(omega);

		// Calculate celestial coordinates ( right ascension and declination )
		// in radians but without limiting the angle to be less than 2*Pi (i.e.,
		// the result may be greater than 2*Pi)
		double sinEclipticLongitude = Math.sin(eclipticLongitude);
		double y = Math.cos(eclipticObliquity) * sinEclipticLongitude;
		double x = Math.cos(eclipticLongitude);
		double rightAscension = Math.atan2(y, x);
		if (rightAscension < 0.0) {
			rightAscension = rightAscension + 2 * Math.PI;
		}
		double declination = Math.asin(Math.sin(eclipticObliquity)
				* sinEclipticLongitude);

		// Calculate local coordinates ( azimuth and zenith angle ) in degrees
		double greenwichMeanSiderealTime = 6.6974243242 + 0.0657098283
				* dElapsedJulianDays + decimalHours;
		double localMeanSiderealTime = (greenwichMeanSiderealTime * 15 + position
				.getLongitude()) * Math.PI / 180;
		double hourAngle = localMeanSiderealTime - rightAscension;
		double latitudeInRadians = position.getLatitude() * Math.PI / 180;
		double cosLatitude = Math.cos(latitudeInRadians);
		double sinLatitude = Math.sin(latitudeInRadians);
		double cosHourAngle = Math.cos(hourAngle);
		double zenithAngle = (Math.acos(cosLatitude * cosHourAngle
				* Math.cos(declination) + Math.sin(declination) * sinLatitude));
		y = -Math.sin(hourAngle);
		x = Math.tan(declination) * cosLatitude - sinLatitude * cosHourAngle;
		double azimuth = Math.atan2(y, x);
		if (azimuth < 0.0) {
			azimuth = azimuth + 2 * Math.PI;
		}
		azimuth = azimuth * 180 / Math.PI;
		// Parallax Correction
		double parallax = (EARTH_MEAN_RADIUS / ASTRONOMICAL_UNIT)
				* Math.sin(zenithAngle);
		zenithAngle = (zenithAngle + parallax) * 180 / Math.PI;

		assert(zenithAngle >= -90f);
		assert(zenithAngle <= 90f);
		assert(azimuth >= 0f);
		assert(azimuth < 360f);
		
		return new SunPosition(zenithAngle, azimuth);
	}
}
