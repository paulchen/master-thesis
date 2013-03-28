package weatherreader.model;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

// TODO javadoc
// TODO create TTL file
public interface OntologyClass {
	public void createIndividuals(OntModel onto);

	public Individual getOntIndividual();
	
	public String toString();
}
