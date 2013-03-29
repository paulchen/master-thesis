package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import at.ac.tuwien.auto.thinkhome.weatherimporter.turtle.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

// TODO javadoc
// TODO create TTL file
public interface OntologyClass {
	public void createIndividuals(OntModel onto);

	public Individual getOntIndividual();
	
	public TurtleStore getTurtleStatements();
	
	public String toString();
}
