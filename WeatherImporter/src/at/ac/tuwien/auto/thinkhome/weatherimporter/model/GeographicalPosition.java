package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class GeographicalPosition implements OntologyClass {
	public static final String WGS84 = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String WGS84_PREFIX = "wgs:";
	
	private float latitude;
	private float longitude;
	private float altitude;
	private Individual individual;
	
	public GeographicalPosition(float latitude, float longitude) {
		this(latitude, longitude, 0);
	}
	
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
