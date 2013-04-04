package at.ac.tuwien.auto.thinkhome.weatherimporter.model;


import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
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
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), roundFloat(probability, Weather.DECIMALS)));
		onto.add(onto.createStatement(blankNode1, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.NAMESPACE + "percent")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), roundFloat(intensity, Weather.DECIMALS)));
		onto.add(onto.createStatement(blankNode2, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.MUO_NAMESPACE + "millimetresPerHour")));
		
 		OntClass weatherPhenomenonClass = onto.getOntClass(Weather.NAMESPACE + "WeatherPhenomenon");
 		individual = onto.createIndividual(Weather.NAMESPACE + name, weatherPhenomenonClass);
 		
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasPrecipitationProbability"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasPrecipitationIntensity"), blankNode2));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		String blankNode1 = Weather.generateBlankNode();
		String blankNode2 = Weather.generateBlankNode();
		
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "numericalValue", "\"" + String.valueOf(roundFloat(probability, Weather.DECIMALS)) + "\"^^xsd:float"));
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "measuredIn", Weather.NAMESPACE_PREFIX + "percent"));
		
		turtle.add(new TurtleStatement(blankNode2, Weather.MUO_PREFIX + "numericalValue", "\"" + String.valueOf(roundFloat(intensity, Weather.DECIMALS)) + "\"^^xsd:float"));
		turtle.add(new TurtleStatement(blankNode2, Weather.MUO_PREFIX + "measuredIn", Weather.MUO_PREFIX + "millimetresPerHour"));
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", Weather.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasPrecipitationProbability", blankNode1));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasPrecipitationIntensity", blankNode2));
		
		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		String output;
		
		output = "precipitation=[";
		output += "intensity=" + roundFloat(intensity, Weather.DECIMALS) + ";";
		output += "probability=" + roundFloat(probability, Weather.DECIMALS) + "]";
		
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

	@Override
	public String getTurtleName() {
		return Weather.NAMESPACE_PREFIX + name;
	}
}
