package weatherreader.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import weatherreader.model.WeatherReport;
import weatherreader.test.base.IndividualsTest;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class SolarRadiationTest extends IndividualsTest {
	private void checkSunRadiation(float sunRadiationValue, String... expectedConcepts) {
		String[] concepts = { "SolarRadiation", "NoRadiation", "LowRadiation", "MediumRadiation", "HighRadiation", "VeryHighRadiation" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Resource blankNode = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), sunRadiationValue));
		getOnto().add(getOnto().createStatement(blankNode, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource(WeatherReport.NAMESPACE + "wattsPerSquareMeter")));
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasSolarRadiationValue"), blankNode));
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testNoRadiation() {
		checkSunRadiation(0f, "SolarRadiation", "NoRadiation");
	}

	@Test
	public void testLowRadiation() {
		// use int here to avoid problems with floating-point numbers
		for(int radiationValue=1; radiationValue<250; radiationValue+=8) {
			checkSunRadiation(radiationValue, "SolarRadiation", "LowRadiation");
		}
	}
	
	@Test
	public void testMediumRadiation() {
		for(int radiationValue=250; radiationValue<500; radiationValue+=3) {
			checkSunRadiation(radiationValue, "SolarRadiation", "MediumRadiation");
		}
	}
	
	@Test
	public void testHighRadiation() {
		for(int radiationValue=500; radiationValue<750; radiationValue+=3) {
			checkSunRadiation(radiationValue, "SolarRadiation", "HighRadiation");
		}
	}
	
	@Test
	public void testVeryHighRadiation() {
		for(int radiationValue=750; radiationValue<1500; radiationValue+=10) {
			checkSunRadiation(radiationValue, "SolarRadiation", "VeryHighRadiation");
		}
	}
}
