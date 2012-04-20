package weatherreader.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import weatherreader.model.WeatherReport;
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
		Statement statement = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasSunRadiation"), sunRadiation);
		
		getOnto().add(statement);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testNoRadiation() {
		checkSunRadiation(0f, "SunRadiation", "NoRadiation");
	}

	@Test
	public void testLowRadiation() {
		// use int here to avoid problems with floating-point numbers
		for(int radiation=1; radiation<250; radiation+=8) {
			checkSunRadiation(radiation, "SunRadiation", "LowRadiation");
		}
	}
	
	@Test
	public void testMediumRadiation() {
		for(int radiation=250; radiation<500; radiation+=3) {
			checkSunRadiation(radiation, "SunRadiation", "MediumRadiation");
		}
	}
	
	@Test
	public void testHighRadiation() {
		for(int radiation=500; radiation<750; radiation+=3) {
			checkSunRadiation(radiation, "SunRadiation", "HighRadiation");
		}
	}
	
	@Test
	public void testVeryHighRadiation() {
		for(int radiation=750; radiation<1500; radiation+=10) {
			checkSunRadiation(radiation, "SunRadiation", "VeryHighRadiation");
		}
	}
}
