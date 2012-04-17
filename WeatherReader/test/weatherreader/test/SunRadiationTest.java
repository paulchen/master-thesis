package weatherreader.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import weatherreader.model.Weather;
import weatherreader.test.base.IndividualsTest;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class SunRadiationTest extends IndividualsTest {
	private void checkSunRadiation(float sunRadiation, String... expectedConcepts) {
		String[] concepts = { "SunRadiation", "NoRadiation", "LowRadiation", "MediumRadiation", "HighRadiation", "VeryHighRadiation" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(Weather.NAMESPACE + "hasSunRadiation"), sunRadiation);
		
		getOnto().add(statement);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(Weather.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testNoRadiation() {
		checkSunRadiation(0f, "SunRadiation", "NoRadiation");
	}

	@Test
	public void testLowRadiation() {
		for(float radiation=1f; radiation<250f; radiation+=8f) {
			checkSunRadiation(radiation, "SunRadiation", "LowRadiation");
		}
	}
	
	@Test
	public void testMediumRadiation() {
		for(float radiation=250f; radiation<500f; radiation+=3f) {
			checkSunRadiation(radiation, "SunRadiation", "MediumRadiation");
		}
	}
	
	@Test
	public void testHighRadiation() {
		for(float radiation=500f; radiation<750f; radiation+=3f) {
			checkSunRadiation(radiation, "SunRadiation", "HighRadiation");
		}
	}
	
	@Test
	public void testVeryHighRadiation() {
		for(float radiation=750f; radiation<1500f; radiation+=10f) {
			checkSunRadiation(radiation, "SunRadiation", "VeryHighRadiation");
		}
	}
}
