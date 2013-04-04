package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
public class SunPosition extends WeatherPhenomenon {
	private double azimuth;
	private double zenith;
	private double elevation;
	private String name;
	private Individual individual;
	
	private static final double EARTH_MEAN_RADIUS = 6371.01;
	private static final double ASTRONOMICAL_UNIT = 149597890;

	private Logger log;
	
	private SunPosition(String name) {
		this.name = name;
		
		log = Logger.getLogger(SunPosition.class);		
	}
	
	public SunPosition(String name, double zenith, double azimuth) {
		this(name);
		this.name = name;
		this.zenith = zenith;
		this.elevation = 90 - this.zenith;
		this.azimuth = azimuth;
	}

	public SunPosition(String name, GeographicalPosition position, Date date) {
		this(name);
		calculatePosition(position, date);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}

	public double getElevation() {
		return elevation;
	}

	public double getZenith() {
		return zenith;
	}

	public void setZenith(double zenith) {
		this.zenith = zenith;
		this.elevation = 90 - zenith;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
		this.zenith = 90 - elevation;
	}
	
	public String toString() {
		String output = "sunPosition=[";
		
		output += "azimuth=" + roundDouble(azimuth, Weather.DECIMALS) + "; ";
		output += "zenith=" + roundDouble(zenith, Weather.DECIMALS) + "; ";
		output += "elevation=" + roundDouble(elevation, Weather.DECIMALS);
		output += "]";
		
		return output;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode1 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), (int)roundDouble(azimuth, Weather.DECIMALS)));
		onto.add(onto.createStatement(blankNode1, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.MUO_NAMESPACE + "degree")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), (float)roundDouble(elevation, Weather.DECIMALS)));
		onto.add(onto.createStatement(blankNode2, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.MUO_NAMESPACE + "degree")));
		
 		OntClass weatherPhenomenonClass = onto.getOntClass(Weather.NAMESPACE + "WeatherPhenomenon");
 		individual = onto.createIndividual(Weather.NAMESPACE + name, weatherPhenomenonClass);
 		
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasSunDirection"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasSunElevationAngle"), blankNode2));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		String blankNode1 = Weather.generateBlankNode();
		String blankNode2 = Weather.generateBlankNode();
		
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "numericalValue", String.valueOf((int)roundDouble(azimuth, Weather.DECIMALS))));
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "measuredIn", Weather.MUO_PREFIX + "degree"));
		
		turtle.add(new TurtleStatement(blankNode2, Weather.MUO_PREFIX + "numericalValue", "\"" + String.valueOf((float)roundDouble(elevation, Weather.DECIMALS)) + "\"^^xsd:float"));
		turtle.add(new TurtleStatement(blankNode2, Weather.MUO_PREFIX + "measuredIn", Weather.MUO_PREFIX + "degree"));
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", Weather.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasSunDirection", blankNode1));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasSunElevationAngle", blankNode2));
		
		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		zenith = linearDoubleInterpolation(((SunPosition)intervalStartPhenomenon).getZenith(), ((SunPosition)intervalEndPhenomenon).getZenith(), end, current);
		elevation = linearDoubleInterpolation(((SunPosition)intervalStartPhenomenon).getElevation(), ((SunPosition)intervalEndPhenomenon).getElevation(), end, current);
		azimuth = 90 - elevation;
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		SunPosition sunPosition = new SunPosition("sunPosition" + name, 0f, 0f);
		sunPosition.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return sunPosition;
	}

	@Override
	public Object clone() {
		return new SunPosition(name, zenith, azimuth);
	}

	@Override
	public String getTurtleName() {
		return Weather.NAMESPACE_PREFIX + name;
	}
	
	private void calculatePosition(GeographicalPosition position, Date date) {
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
		zenith = (Math.acos(cosLatitude * cosHourAngle
				* Math.cos(declination) + Math.sin(declination) * sinLatitude));
		y = -Math.sin(hourAngle);
		x = Math.tan(declination) * cosLatitude - sinLatitude * cosHourAngle;
		azimuth = Math.atan2(y, x);
		if (azimuth < 0.0) {
			azimuth = azimuth + 2 * Math.PI;
		}
		azimuth = azimuth * 180 / Math.PI;
		// Parallax Correction
		double parallax = (EARTH_MEAN_RADIUS / ASTRONOMICAL_UNIT)
				* Math.sin(zenith);
		zenith = (zenith + parallax) * 180 / Math.PI;

		assert(zenith >= -90f);
		assert(zenith <= 90f);
		assert(azimuth >= 0f);
		assert(azimuth < 360f);
		
		elevation = 90 - zenith;
	}
}
