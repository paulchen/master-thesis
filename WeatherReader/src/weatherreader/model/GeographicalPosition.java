package weatherreader.model;

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
		Resource pointClass = onto.getResource(WeatherReport.WGS84 + "Point");
		
		individual = onto.createIndividual(WeatherReport.NAMESPACE + "point0", pointClass);
		onto.add(onto.createStatement(individual, RDF.type, pointClass));		
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherReport.WGS84 + "lat"), latitude));
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherReport.WGS84 + "long"), longitude));
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherReport.WGS84 + "alt"), altitude));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}
}
