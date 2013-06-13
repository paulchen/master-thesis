package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

/**
 * This class represents anything that is mapped into an individual when the
 * individuals for the ontology are generated.
 * 
 * @author Paul Staroch
 */
public interface OntologyClass {
	/**
	 * Creates invididuals for the data stored in this instance and adds them to
	 * the ontology being specified
	 * 
	 * @param onto
	 *            ontology where to add the individuals to
	 */
	public void createIndividuals(OntModel onto);

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called for this
	 * instance, this contains the main individual in the ontology that has been
	 * created by that method call.
	 * 
	 * @return an individual
	 */
	public Individual getIndividual();

	/**
	 * Returns a set of Turtle statements that contains all data which is
	 * represented by this instance.
	 * 
	 * @return an instance of {@link TurtleStore} that contains all data which
	 *         is represented by this instance.
	 */
	public TurtleStore getTurtleStatements();

	/**
	 * Returns the name of this instance that is used for output to Turtle.
	 * 
	 * @return the name of this instance in the Turtle output
	 */
	public String getTurtleName();

	@Override
	public String toString();
}
