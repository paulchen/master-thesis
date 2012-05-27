package weatherreader.test.model;

import weatherreader.model.WeatherReport;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
public class Wind extends WeatherPhenomenon {
	private float windSpeed;
	private String name;
	private Individual individual;
	
	public Wind(String name, float windSpeed) {
		this.name = name;
		this.windSpeed = windSpeed;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), windSpeed));
		// TODO get rid of magic constant for individual name here
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "metresPerSecond")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasWindSpeed"), blankNode));
	}

	@Override
	protected Individual getOntIndividual() {
		return individual; 
	}
}
