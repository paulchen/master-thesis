package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import at.ac.tuwien.auto.thinkhome.weatherimporter.turtle.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.turtle.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class GeographicalPosition implements OntologyClass {
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
		Resource pointClass = onto.getResource(WeatherConstants.WGS84 + "Point");
		
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + "point0", pointClass);
		onto.add(onto.createStatement(individual, RDF.type, pointClass));		
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.WGS84 + "lat"), WeatherPhenomenon.roundFloat(latitude, WeatherConstants.DECIMALS)));
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.WGS84 + "long"), WeatherPhenomenon.roundFloat(longitude, WeatherConstants.DECIMALS)));
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.WGS84 + "alt"), WeatherPhenomenon.roundFloat(altitude, WeatherConstants.DECIMALS)));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", WeatherConstants.WGS84_PREFIX + "Point"));
		turtle.add(new TurtleStatement(getTurtleName(), WeatherConstants.WGS84_PREFIX + "lat", String.valueOf(WeatherPhenomenon.roundFloat(latitude, WeatherConstants.DECIMALS) + "^^xsd:float")));
		turtle.add(new TurtleStatement(getTurtleName(), WeatherConstants.WGS84_PREFIX + "lon", String.valueOf(WeatherPhenomenon.roundFloat(longitude, WeatherConstants.DECIMALS) + "^^xsd:float")));
		turtle.add(new TurtleStatement(getTurtleName(), WeatherConstants.WGS84_PREFIX + "alt", String.valueOf(WeatherPhenomenon.roundFloat(altitude, WeatherConstants.DECIMALS) + "^^xsd:float")));
		
		return turtle;
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		String output = "[";
		
		output += "latitude=" + WeatherPhenomenon.roundFloat(latitude, WeatherConstants.DECIMALS) + "; ";
		output += "longitude=" + WeatherPhenomenon.roundFloat(longitude, WeatherConstants.DECIMALS) + "; ";
		output += "altitude=" + WeatherPhenomenon.roundFloat(altitude, WeatherConstants.DECIMALS);
		output += "]";
		
		return output;
	}

	@Override
	public String getTurtleName() {
		return WeatherConstants.NAMESPACE_PREFIX + "point0";
	}
}
