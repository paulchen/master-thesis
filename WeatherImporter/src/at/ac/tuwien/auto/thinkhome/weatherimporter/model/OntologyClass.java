package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

// TODO javadoc
// TODO create TTL file
public interface OntologyClass {
	public void createIndividuals(OntModel onto);

	public Individual getIndividual();
	
	public TurtleStore getTurtleStatements();
	
	public String getTurtleName();
	
	public String toString();
}
