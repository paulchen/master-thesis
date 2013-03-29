package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.turtle.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.turtle.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
public class Humidity extends WeatherPhenomenon {
	private String name;
	private Individual individual;
	private float humidityValue;
	
	public Humidity(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		humidityValue = 0;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			humidityValue += ((Humidity)phenomenon).getHumidityValue();
		}
		humidityValue /= weatherPhenomena.size();
	}
	
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
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "numericalValue"), roundFloat(humidityValue, WeatherConstants.DECIMALS)));
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherConstants.NAMESPACE + "percent")));
		
 		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherPhenomenon");
 		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherPhenomenonClass);
 		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasHumidityValue"), blankNode));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		String blankNode = Weather.generateBlankNode();
		
		turtle.add(new TurtleStatement(blankNode, WeatherConstants.MUO_PREFIX + "numericalValue", String.valueOf(roundFloat(humidityValue, WeatherConstants.DECIMALS) + "f")));
		turtle.add(new TurtleStatement(blankNode, WeatherConstants.MUO_PREFIX + "measuredIn", WeatherConstants.NAMESPACE_PREFIX + "percent"));
		
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + name, "a", WeatherConstants.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + name, WeatherConstants.NAMESPACE_PREFIX + "hasHumidityValue", blankNode));
		
		return turtle;
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "humidity=" + roundFloat(humidityValue, WeatherConstants.DECIMALS);
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		humidityValue = linearFloatInterpolation(((Humidity)intervalStartPhenomenon).getHumidityValue(), ((Humidity)intervalEndPhenomenon).getHumidityValue(), end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		Humidity humidity = new Humidity("humidity" + name, 0f);
		humidity.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
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
}
