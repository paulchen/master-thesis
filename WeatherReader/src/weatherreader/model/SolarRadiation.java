package weatherreader.model;


import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class SolarRadiation extends WeatherPhenomenon {
	private String name;
	private Float radiationValue;
	private Individual individual;

	public SolarRadiation(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		radiationValue = 0f;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			radiationValue += ((SolarRadiation)phenomenon).getRadiationValue();
		}
		radiationValue /= weatherPhenomena.size();
	}
	
	public SolarRadiation(String name, Float pressureValue) {
		this.name = name;
		this.radiationValue = pressureValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), radiationValue));
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "hectopascal")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasSolarRadiationValue"), blankNode));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "pressure=" + radiationValue;
	}

	private Float getRadiationValue() {
		return radiationValue;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		radiationValue = linearFloatInterpolation(((SolarRadiation)intervalStartPhenomenon).getRadiationValue(), ((SolarRadiation)intervalEndPhenomenon).getRadiationValue(), end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		SolarRadiation pressure = new SolarRadiation(name, 0f);
		pressure.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return pressure;
	}

	@Override
	public Object clone() {
		return new SolarRadiation(name, radiationValue);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
