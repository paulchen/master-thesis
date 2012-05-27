package weatherreader.test.model;

import weatherreader.model.WeatherReport;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
// TODO support for cloud altitude 
public class CloudCover extends WeatherPhenomenon {

	private String name;
	private int cloudCover;

	private Individual individual;
	
	// TODO use separate enum for octa?
	public CloudCover(String name, int cloudCover) {
		this.name = name;
		this.cloudCover = cloudCover;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), cloudCover));
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "okta")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasCloudCover"), blankNode));
	}

	@Override
	protected Individual getOntIndividual() {
		return individual;
	}
}
