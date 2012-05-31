package weatherreader.model;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

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
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), humidityValue));
		// TODO get rid of magic constant for individual name here
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/fraction/percent")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasHumidityValue"), blankNode));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "humidity=" + humidityValue;
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
		Humidity humidity = new Humidity(name, 0f);
		humidity.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return humidity;
	}
}
