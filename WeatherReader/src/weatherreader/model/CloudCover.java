package weatherreader.model;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
public class CloudCover extends WeatherPhenomenon {
	private String name;
	private int altitude;
	private int coverage;
	
	private Individual individual;

	public CloudCover(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		altitude = 0;
		coverage = 0;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			altitude += ((CloudCover)phenomenon).getAltitude();
			if(coverage < ((CloudCover)phenomenon).getCoverage()) {
				coverage = ((CloudCover)phenomenon).getCoverage();
			}
		}
		altitude /= weatherPhenomena.size();
	}

	// TODO use separate enum for octa?
	public CloudCover(String name, int altitude, int coverage) {
		super();
		this.name = name;
		this.altitude = altitude;
		this.coverage = coverage;
	}
	
	public int getAltitude() {
		return altitude;
	}
	
	public int getCoverage() {
		return coverage;
	}
	
	@Override
	public String toString() {
		return "[altitude=" + altitude + "; coverage=" + coverage + "]";
	}
	
	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode1 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), coverage));
		onto.add(onto.createStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "okta")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), altitude));
		onto.add(onto.createStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/length/meter")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasCloudCover"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasCloudAltitude"), blankNode2));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		altitude = linearIntInterpolation(((CloudCover)intervalStartPhenomenon).getAltitude(), ((CloudCover)intervalEndPhenomenon).getAltitude(), end, current);
		coverage = linearIntInterpolation(((CloudCover)intervalStartPhenomenon).getCoverage(), ((CloudCover)intervalEndPhenomenon).getCoverage(), end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		CloudCover cloudCover = new CloudCover(name, 0, 0);
		cloudCover.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return cloudCover;
	}
}
