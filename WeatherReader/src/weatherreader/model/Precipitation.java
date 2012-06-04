package weatherreader.model;


import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class Precipitation extends WeatherPhenomenon {

	private String name;
	private float intensity;
	private float probability;
	
	private Individual individual;

	public Precipitation(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		intensity = 0f;
		probability = 0f;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			intensity += ((Precipitation)phenomenon).getIntensity();
			probability += ((Precipitation)phenomenon).getProbability();
		}
		intensity /= weatherPhenomena.size();
		probability /= weatherPhenomena.size();
	}

	public Precipitation(String name, float intensity, float probability) {
		this.name = name;
		this.intensity = intensity;
		this.probability = probability;
	}

	private float getProbability() {
		return probability;
	}

	private float getIntensity() {
		return intensity;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode1 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), probability));
		// TODO get rid of magic constant for individual name here
		onto.add(onto.createStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/fraction/percent")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), intensity));
		onto.add(onto.createStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "millimetresPerHour")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasPrecipitationProbability"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasPrecipitationIntensity"), blankNode2));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "precipitation=[intensity=" + intensity + ";probability=" + probability + "]";
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		intensity = linearFloatInterpolation(((Precipitation)intervalStartPhenomenon).getIntensity(), ((Precipitation)intervalEndPhenomenon).getIntensity(), end, current);
		probability = linearFloatInterpolation(((Precipitation)intervalStartPhenomenon).getProbability(), ((Precipitation)intervalEndPhenomenon).getProbability(), end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(
			String name, WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		Precipitation precipitation = new Precipitation("precipitation" + name, 0f, 0f);
		precipitation.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return precipitation;
	}

	@Override
	public Object clone() {
		return new Precipitation(name, intensity, probability);
	}
}
