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
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), windSpeed));
		onto.add(onto.createStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "metresPerSecond")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), windDirection));
		// TODO get rid of magic constant for individual name here
		onto.add(onto.createStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasWindSpeed"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasWindDirection"), blankNode2));
	}

	@Override
	public Individual getOntIndividual() {
		return individual; 
	}

	@Override
	public String toString() {
		return "wind=[speed=" + windSpeed + ";direction=" + windDirection + "]";
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
		Wind wind = new Wind(name, 0f, 0);
		wind.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return wind;
	}
}
