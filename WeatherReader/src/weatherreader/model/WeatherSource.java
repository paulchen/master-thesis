package weatherreader.model;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public abstract class WeatherSource implements OntologyClass {
	private String name;
	private Individual individual;

	public WeatherSource(String name) {
		this.name = name;
	}
	
	protected abstract String getOntClassName();
	
	@Override
	public void createIndividuals(OntModel onto) {
		OntClass sensorSourceClass = onto.getOntClass(WeatherReport.NAMESPACE + "ServiceSource");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, sensorSourceClass);
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}
}
