package weatherreader.model;


import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class Temperature extends WeatherPhenomenon {

	private String name;
	private float temperatureValue;
	
	private Individual individual;
	
	public Temperature(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		temperatureValue = 0;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			temperatureValue += ((Temperature)phenomenon).getTemperatureValue();
		}
		temperatureValue /= weatherPhenomena.size();
	}
	
	// TODO constructor taking List<WeatherPhenomenon> argument
	public Temperature(String name, float temperatureValue) {
		this.name = name;
		this.temperatureValue = temperatureValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), temperatureValue));
		// TODO get rid of magic constant for individual name here
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/temperature/degree-Celsius")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasTemperatureValue"), blankNode));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "temperature=" + temperatureValue;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		temperatureValue = linearFloatInterpolation(((Temperature)intervalStartPhenomenon).getTemperatureValue(), ((Temperature)intervalEndPhenomenon).getTemperatureValue(), end, current);
	}

	private float getTemperatureValue() {
		return temperatureValue;
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		Temperature temperature = new Temperature(name, 0f);
		temperature.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return temperature;
	}
}
