package at.ac.tuwien.auto.thinkhome.weatherimporter.model;


import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
/**
 * This class represents data about wind (direction and speed).
 * 
 * @author Paul Staroch
 */
public class Wind extends WeatherPhenomenon {
	/**
	 * Unique name of the individual that corresponds to this object in the ontology 
	 */
	private String name;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains the main individual in the ontology that has been created by that method call.
	 */
	private Individual individual;
	
	/**
	 * Wind speed (in metres per second)
	 */
	private float windSpeed;
	
	/**
	 * Wind direction (in degrees, North equals 0 degrees, East equals 90 degrees etc.)
	 */
	private int windDirection;
	
	/**
	 * A constructor that creates an instance of <tt>Wind</tt> from a set of already existing instances of that class.
	 * Both wind speed and wind direction will be set to the arithmetic mean of the corresponding values of all these instances 
	 * 
	 * @param name the unique name of the individual in the ontology that corresponds to this object
	 * @param weatherPhenomena a list of instances of <tt>Wind</tt> that should be used to create this instance 
	 */
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
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), roundFloat(windSpeed, Weather.DECIMALS)));
		onto.add(onto.createStatement(blankNode1, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.NAMESPACE + "metresPerSecond")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), windDirection));
		onto.add(onto.createStatement(blankNode2, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.MUO_NAMESPACE + "degree")));
		
 		OntClass weatherPhenomenonClass = onto.getOntClass(Weather.NAMESPACE + "WeatherPhenomenon");
 		individual = onto.createIndividual(Weather.NAMESPACE + name, weatherPhenomenonClass);
 		
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasWindSpeed"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasWindDirection"), blankNode2));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		String blankNode1 = Weather.generateBlankNode();
		String blankNode2 = Weather.generateBlankNode();
		
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "numericalValue", "\"" + String.valueOf(roundFloat(windSpeed, Weather.DECIMALS)) + "\"^^xsd:float"));
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "measuredIn", Weather.NAMESPACE_PREFIX + "metresPerSecond"));
		
		turtle.add(new TurtleStatement(blankNode2, Weather.MUO_PREFIX + "numericalValue", String.valueOf(windDirection)));
		turtle.add(new TurtleStatement(blankNode2, Weather.MUO_PREFIX + "measuredIn", Weather.MUO_PREFIX + "degree"));
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", Weather.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasWindSpeed", blankNode1));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasWindDirection", blankNode2));
		
		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual; 
	}

	@Override
	public String toString() {
		String output;
		
		output = "wind=[";
		output += "speed=" + roundFloat(windSpeed, Weather.DECIMALS) + ";";
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
			WeatherPhenomenon intervalEndPhenomenon, int start, int end, int current) {
		windSpeed = linearFloatInterpolation(((Wind)intervalStartPhenomenon).getWindSpeed(), ((Wind)intervalEndPhenomenon).getWindSpeed(), start, end, current);
		windDirection = linearIntInterpolation(((Wind)intervalStartPhenomenon).getWindDirection(), ((Wind)intervalEndPhenomenon).getWindDirection(), start, end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end, int current) {
		Wind wind = new Wind("wind" + name, 0f, 0);
		wind.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, start, end, current);
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

	@Override
	public String getTurtleName() {
		return Weather.NAMESPACE_PREFIX + name;
	}
}
