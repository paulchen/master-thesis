package weatherreader.model;


import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
public class Wind extends WeatherPhenomenon {
	private String name;
	private Individual individual;
	private float windSpeed;
	private int windDirection;
	
	public Wind(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		windSpeed = 0;
		windDirection = 0;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			windSpeed += ((Wind)phenomenon).getWindSpeed();
			windDirection += ((Wind)phenomenon).getWindDirection();
		}
		windSpeed /= weatherPhenomena.size();
		windDirection /= weatherPhenomena.size();
	}
	
	public Wind(String name, float windSpeed, int windDirection) {
		this.name = name;
		this.windSpeed = windSpeed;
		this.windDirection = windDirection;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode1 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "numericalValue"), roundFloat(windSpeed, WeatherConstants.DECIMALS)));
		onto.add(onto.createStatement(blankNode1, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherConstants.NAMESPACE + "metresPerSecond")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "numericalValue"), windDirection));
		onto.add(onto.createStatement(blankNode2, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherConstants.UNIT_DEGREE)));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasWindSpeed"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasWindDirection"), blankNode2));
	}

	@Override
	public Individual getOntIndividual() {
		return individual; 
	}

	@Override
	public String toString() {
		String output;
		
		output = "wind=[";
		output += "speed=" + roundFloat(windSpeed, WeatherConstants.DECIMALS) + ";";
		output += "direction=" + windDirection + "]";
		
		return output;
	}
	
	private int getWindDirection() {
		return windDirection;
	}

	private float getWindSpeed() {
		return windSpeed;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		windSpeed = linearFloatInterpolation(((Wind)intervalStartPhenomenon).getWindSpeed(), ((Wind)intervalEndPhenomenon).getWindSpeed(), end, current);
		windDirection = linearIntInterpolation(((Wind)intervalStartPhenomenon).getWindDirection(), ((Wind)intervalEndPhenomenon).getWindDirection(), end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		Wind wind = new Wind("wind" + name, 0f, 0);
		wind.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return wind;
	}

	@Override
	public Object clone() {
		return new Wind(name, windSpeed, windDirection);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
