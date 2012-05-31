package weatherreader.model;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

public interface OntologyClass {
	public void createIndividuals(OntModel onto);

	public Individual getOntIndividual();
	
	public String toString();
}
