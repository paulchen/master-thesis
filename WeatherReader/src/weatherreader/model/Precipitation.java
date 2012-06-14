package weatherreader.model;


import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

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
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasPrecipitationProbability"), roundFloat(probability, WeatherConstants.DECIMALS)));
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasPrecipitationIntensity"), roundFloat(intensity, WeatherConstants.DECIMALS)));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		String output;
		
		output = "precipitation=[";
		output += "intensity=" + roundFloat(intensity, WeatherConstants.DECIMALS) + ";";
		output += "probability=" + roundFloat(probability, WeatherConstants.DECIMALS) + "]";
		
		return output;
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

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
