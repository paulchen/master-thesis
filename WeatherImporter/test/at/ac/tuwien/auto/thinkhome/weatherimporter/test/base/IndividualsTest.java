package at.ac.tuwien.auto.thinkhome.weatherimporter.test.base;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.mindswap.pellet.jena.PelletReasonerFactory;


import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Weather;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherPhenomenon;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherState;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public abstract class IndividualsTest extends TestCase {
	private OntModel onto;
	private int weatherIndex = 0;
	
	protected OntModel getOnto() {
		return onto;
	}
	
	@Before
	public void setUp() {
		onto = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		arp.read(onto, "file:ThinkHomeWeather.owl");
	}
	
	@After
	public void tearDown() {
		/* nothing to do? */
	}

	protected void testConcepts(String[] concepts, String[] expectedConcepts,
			WeatherPhenomenon weatherPhenomenon) {
		weatherIndex++;
		
		weatherPhenomenon.setName("phenomenon" + weatherIndex);
		WeatherState weatherState = new WeatherState("weather" + weatherIndex);
		weatherState.addPhenomenon(weatherPhenomenon);

		weatherState.createIndividuals(getOnto());
		
		List<String> expected = Arrays.asList(expectedConcepts);

		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon.getIndividual(), RDF.type, getOnto().getOntClass(Weather.NAMESPACE + concept)).toSet().size());
		}
	}
}
