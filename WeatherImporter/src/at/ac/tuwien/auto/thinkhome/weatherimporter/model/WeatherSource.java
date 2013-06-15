package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

/**
 * This class represents a source of weather data (either a {@link Instant} or
 * an {@link Interval}).
 * 
 * @author Paul Staroch
 * 
 */
public abstract class WeatherSource implements OntologyClass {
	/**
	 * Unique name of the individual that corresponds to this object in the
	 * ontology
	 */
	private String name;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains
	 * the main individual in the ontology that has been created by that method
	 * call.
	 */
	private Individual individual;

	/**
	 * Constructor that sets the name which is to be used for the individual
	 * which corresponds to this instance in the ontology.
	 * 
	 * @param name
	 *            a name
	 */
	public WeatherSource(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the concept in the ontology this class corresponds
	 * to.
	 * 
	 * @return the name of the concept in the ontology this class corresponds
	 *         to.
	 */
	protected abstract String getOntClassName();

	@Override
	public void createIndividuals(OntModel onto) {
		OntClass sensorSourceClass = onto.getOntClass(Weather.NAMESPACE
				+ "ServiceSource");
		individual = onto.createIndividual(Weather.NAMESPACE + name,
				sensorSourceClass);
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();

		turtle.add(new TurtleStatement(getTurtleName(), "a",
				Weather.NAMESPACE_PREFIX + "ServiceSource"));

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
		return Weather.NAMESPACE_PREFIX + name;
	}
}
