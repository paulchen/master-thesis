package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

// TODO javadoc
public abstract class WeatherSource implements OntologyClass {
	private String name;
	private Individual individual;

	public WeatherSource(String name) {
		this.name = name;
	}
	
	protected abstract String getOntClassName();
	
	@Override
	public void createIndividuals(OntModel onto) {
		OntClass sensorSourceClass = onto.getOntClass(WeatherConstants.NAMESPACE + "ServiceSource");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, sensorSourceClass);
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", WeatherConstants.NAMESPACE_PREFIX + "ServiceSource"));
		
		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}
	
	@Override
	public String toString() {
		return getOntClassName() + ":" + name;
	}

	@Override
	public String getTurtleName() {
		return WeatherConstants.NAMESPACE_PREFIX + name;
	}
}
