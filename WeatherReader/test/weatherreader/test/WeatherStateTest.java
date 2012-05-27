package weatherreader.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import weatherreader.model.WeatherReport;
import weatherreader.test.model.WeatherPhenomenon;
import weatherreader.test.model.WeatherState;
import weatherreader.test.model.Wind;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.vocabulary.RDF;

public class WeatherStateTest extends TestCase {
	private OntModel onto;
	private int stateIndex = 0;
	
	@Before
	public void setUp() {
		onto = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		arp.read(onto, "file:ThinkHomeWeather.owl");
	}
	
	@After
	public void tearDown() {
		/* nothing to do */
	}
	
	private void testState(WeatherPhenomenon[] phenomena, String[] expectedConcepts) {
		String[] allConcepts = {
				"CalmWeather", "WindyWeather", "StormyWeather"
			};
		List<String> expected = Arrays.asList(expectedConcepts);

		WeatherState state = new WeatherState("weatherState" + stateIndex);
		stateIndex++;
		
		// TODO method addAll?
		for(WeatherPhenomenon phenomenon : phenomena) {
			state.addPhenomenon(phenomenon);
		}
		
		state.createIndividuals(onto);
		
		for(String concept : allConcepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, onto.listStatements(state.getOntIndividual(), RDF.type, onto.getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}		
	}
	
	@Test
	public void testWindyWeather() {
		for(int a=0; a<10; a++) {
			testState(new WeatherPhenomenon[] { new Wind("wind" + a, a) }, new String[] { "CalmWeather" });
		}
		for(int a=10; a<20; a++) {
			testState(new WeatherPhenomenon[] { new Wind("wind" + a, a) }, new String[] { "WindyWeather" });
		}
		for(int a=20; a<100; a++) {
			testState(new WeatherPhenomenon[] { new Wind("wind" + a, a) }, new String[] { "WindyWeather", "StormyWeather" });
		}
	}
}
