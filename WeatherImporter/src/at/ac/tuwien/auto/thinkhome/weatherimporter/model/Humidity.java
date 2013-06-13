package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * This class represents a value for relative humidity.
 * 
 * @author Paul Staroch
 */
public class Humidity extends WeatherPhenomenon {
	/**
	 * Unique name of the individual that corresponds to this object in the ontology 
	 */
	private String name;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains the main individual in the ontology that has been created by that method call.
	 */
	private Individual individual;

	/**
	 * The humidity value (between 0 and 1)
	 */
	private float humidityValue;
	
	/**
	 * A constructor that creates an instance of <tt>Humidity</tt> from a set of already existing instances of that class. The humidity value will be set to the arithmetic mean of the humidity values of all these instances.
	 * 
	 * @param name the unique name of the individual in the ontology that corresponds to this object
	 * @param weatherPhenomena a list of instances of <tt>Humidity</tt> that should be used to create this instance 
	 */
	public Humidity(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		humidityValue = 0;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			humidityValue += ((Humidity)phenomenon).getHumidityValue();
		}
		humidityValue /= weatherPhenomena.size();
	}
	
	/**
	 * A constructor that creates an instance of <tt>Humidity</tt> given its unique name and its humidity value.
	 * 
	 * @param name the unique name of the individual in the ontology that corresponds to this object
	 * @param humidityValue the humidity value
	 */
	public Humidity(String name, float humidityValue) {
		super();
		this.name = name;
		this.humidityValue = humidityValue;
	}

	private float getHumidityValue() {
		return humidityValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), roundFloat(humidityValue, Weather.DECIMALS)));
		onto.add(onto.createStatement(blankNode, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.NAMESPACE + "percent")));
		
 		OntClass weatherPhenomenonClass = onto.getOntClass(Weather.NAMESPACE + "WeatherPhenomenon");
 		individual = onto.createIndividual(Weather.NAMESPACE + name, weatherPhenomenonClass);
 		
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasHumidityValue"), blankNode));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		String blankNode = Weather.generateBlankNode();
		
		turtle.add(new TurtleStatement(blankNode, Weather.MUO_PREFIX + "numericalValue", "\"" + String.valueOf(roundFloat(humidityValue, Weather.DECIMALS) + "\"^^xsd:float")));
		turtle.add(new TurtleStatement(blankNode, Weather.MUO_PREFIX + "measuredIn", Weather.NAMESPACE_PREFIX + "percent"));
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", Weather.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasHumidityValue", blankNode));
		
		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "humidity=" + roundFloat(humidityValue, Weather.DECIMALS);
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end, int current) {
		humidityValue = linearFloatInterpolation(((Humidity)intervalStartPhenomenon).getHumidityValue(), ((Humidity)intervalEndPhenomenon).getHumidityValue(), start, end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end, int current) {
		Humidity humidity = new Humidity("humidity" + name, 0f);
		humidity.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, start, end, current);
		return humidity;
	}

	@Override
	public Object clone() {
		return new Humidity(name, humidityValue);
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
