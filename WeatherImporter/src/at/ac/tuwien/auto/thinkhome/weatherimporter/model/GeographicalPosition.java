package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

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

	protected float getAltitude() {
		return altitude;
	}

	protected void setAltitude(float altitude) {
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
}
