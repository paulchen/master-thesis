package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This class represents a geographical position on Earth, specified using the WGS84 reference model (latitude, longitude, altitude). 
 * 
 * @author Paul Staroch
 */
public class GeographicalPosition implements OntologyClass {
	/**
	 * URI of the namespace used by the WGS84 vocabulary
	 */
	public static final String WGS84 = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	
	/**
	 * Prefix to be used for the namespace which is used by the WGS84 ontology
	 */
	public static final String WGS84_PREFIX = "wgs:";
	
	/**
	 * The latitude of the position represented by this object (in degrees, Northern latitudes are represented by positive values)
	 */
	private float latitude;
	
	/**
	 * The longitude of the position represented by this object (in degrees, Eastern longitudes are represented by positive values)
	 */
	private float longitude;
	
	/**
	 * The altitude of the position represented by this object (in metres above sea level)
	 */
	private float altitude;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains the main individual in the ontology that has been created by that method call.
	 */
	private Individual individual;
	
	/**
	 * A constructor that creates an instance of <tt>GeographicalPosition</tt> given its latitude and its longitude. The altitude will be set to 0.
	 * 
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public GeographicalPosition(float latitude, float longitude) {
		this(latitude, longitude, 0);
	}
	
	/**
	 * A constructor that creates an instance of <tt>GeographicalPosition</tt> given its latitude, its longitude and its altitude.
	 * 
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @param altitude the altitude
	 */
	public GeographicalPosition(float latitude, float longitude, float altitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource pointClass = onto.getResource(GeographicalPosition.WGS84 + "Point");
		
		individual = onto.createIndividual(Weather.NAMESPACE + "point0", pointClass);
		onto.add(onto.createStatement(individual, RDF.type, pointClass));		
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(GeographicalPosition.WGS84 + "lat"), WeatherPhenomenon.roundFloat(latitude, Weather.DECIMALS)));
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(GeographicalPosition.WGS84 + "long"), WeatherPhenomenon.roundFloat(longitude, Weather.DECIMALS)));
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(GeographicalPosition.WGS84 + "alt"), WeatherPhenomenon.roundFloat(altitude, Weather.DECIMALS)));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", GeographicalPosition.WGS84_PREFIX + "Point"));
		turtle.add(new TurtleStatement(getTurtleName(), GeographicalPosition.WGS84_PREFIX + "lat", "\"" + String.valueOf(WeatherPhenomenon.roundFloat(latitude, Weather.DECIMALS) + "\"^^xsd:float")));
		turtle.add(new TurtleStatement(getTurtleName(), GeographicalPosition.WGS84_PREFIX + "lon", "\"" + String.valueOf(WeatherPhenomenon.roundFloat(longitude, Weather.DECIMALS) + "\"^^xsd:float")));
		turtle.add(new TurtleStatement(getTurtleName(), GeographicalPosition.WGS84_PREFIX + "alt", "\"" + String.valueOf(WeatherPhenomenon.roundFloat(altitude, Weather.DECIMALS) + "\"^^xsd:float")));
		
		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		String output = "[";
		
		output += "latitude=" + WeatherPhenomenon.roundFloat(latitude, Weather.DECIMALS) + "; ";
		output += "longitude=" + WeatherPhenomenon.roundFloat(longitude, Weather.DECIMALS) + "; ";
		output += "altitude=" + WeatherPhenomenon.roundFloat(altitude, Weather.DECIMALS);
		output += "]";
		
		return output;
	}

	@Override
	public String getTurtleName() {
		return Weather.NAMESPACE_PREFIX + "point0";
	}
}
