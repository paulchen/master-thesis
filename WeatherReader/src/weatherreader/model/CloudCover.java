package weatherreader.model;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

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
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasCloudCover"), coverage));
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasCloudAltitude"), altitude));
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
		CloudCover cloudCover = new CloudCover("cloudCover" + name, 0, 0);
		cloudCover.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return cloudCover;
	}

	@Override
	public Object clone() {
		return new CloudCover(name, altitude, coverage);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
