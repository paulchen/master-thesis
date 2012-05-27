package weatherreader.test.model;

import weatherreader.model.WeatherReport;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class Temperature extends WeatherPhenomenon {

	private String name;
	private float temperatureValue;
	
	private Individual individual;
	
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
	protected Individual getOntIndividual() {
		return individual;
	}
}
