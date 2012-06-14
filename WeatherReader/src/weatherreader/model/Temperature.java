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
	
	public Temperature(String name, float temperatureValue) {
		this.name = name;
		this.temperatureValue = temperatureValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "numericalValue"), roundFloat(temperatureValue, WeatherConstants.DECIMALS)));
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherConstants.UNIT_DEGREES_CELSIUS)));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherPhenomenonClass);
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasTemperatureValue"), blankNode));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "temperature=" + roundFloat(temperatureValue, WeatherConstants.DECIMALS);
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
		Temperature temperature = new Temperature("temperature" + name, 0f);
		temperature.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
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
}
