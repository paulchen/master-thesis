package weatherreader.test.model;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

//TODO javadoc
public abstract class WeatherPhenomenon {

	public abstract void createIndividuals(OntModel onto);

	protected abstract Individual getOntIndividual();
}
