package at.ac.tuwien.auto.thinkhome.weatherimporter.model;


import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * This class represents a temperature value.
 * 
 * @author Paul Staroch
 */
public class Temperature extends WeatherPhenomenon {
	/**
	 * Unique name of the individual that corresponds to this object in the ontology 
	 */
	private String name;
	
	/**
	 * The temperature value (in degrees celsius)
	 */
	private float temperatureValue;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains the main individual in the ontology that has been created by that method call.
	 */
	private Individual individual;
	
	/**
	 * A constructor that creates an instance of <tt>Temperature</tt> from a set of already existing instances of that class. The temperature value will be set to the arithmetic mean of the temperature values of all these instances.
	 * 
	 * @param name the unique name of the individual in the ontology that corresponds to this object
	 * @param weatherPhenomena a list of instances of <tt>Temperature</tt> that should be used to create this instance 
	 */
	public Temperature(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		temperatureValue = 0;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			temperatureValue += ((Temperature)phenomenon).getTemperatureValue();
		}
		temperatureValue /= weatherPhenomena.size();
	}
	
	/**
	 * A constructor that creates an instance of <tt>Temperature</tt> given its unique name and its temperature value.
	 * 
	 * @param name the unique name of the individual in the ontology that corresponds to this object
	 * @param temperatureValue the temperature value
	 */
	public Temperature(String name, float temperatureValue) {
		this.name = name;
		this.temperatureValue = temperatureValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), roundFloat(temperatureValue, Weather.DECIMALS)));
		onto.add(onto.createStatement(blankNode, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.MUO_NAMESPACE + "degrees-Celsius")));
		
 		OntClass weatherPhenomenonClass = onto.getOntClass(Weather.NAMESPACE + "WeatherPhenomenon");
 		individual = onto.createIndividual(Weather.NAMESPACE + name, weatherPhenomenonClass);
 		
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasTemperatureValue"), blankNode));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		String blankNode = Weather.generateBlankNode();
		
		turtle.add(new TurtleStatement(blankNode, Weather.MUO_PREFIX + "numericalValue", "\"" + String.valueOf(roundFloat(temperatureValue, Weather.DECIMALS)) + "\"^^xsd:float"));
		turtle.add(new TurtleStatement(blankNode, Weather.MUO_PREFIX + "measuredIn", Weather.MUO_PREFIX + "degrees-Celsius"));
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", Weather.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasTemperatureValue", blankNode));
		
		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "temperature=" + roundFloat(temperatureValue, Weather.DECIMALS);
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end, int current) {
		temperatureValue = linearFloatInterpolation(((Temperature)intervalStartPhenomenon).getTemperatureValue(), ((Temperature)intervalEndPhenomenon).getTemperatureValue(), start, end, current);
	}

	private float getTemperatureValue() {
		return temperatureValue;
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end, int current) {
		Temperature temperature = new Temperature("temperature" + name, 0f);
		temperature.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, start, end, current);
		return temperature;
	}

	@Override
	public Object clone() {
		return new Temperature(name, temperatureValue);
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
