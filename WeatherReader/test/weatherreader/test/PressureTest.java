package weatherreader.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import weatherrader.model.Weather;
import weatherreader.test.base.IndividualsTest;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class PressureTest extends IndividualsTest {
	private void checkPressure(Float pressure, String... expectedConcepts) {
		String[] concepts = { "AtmosphericPressure", "VeryLowPressure", "LowPressure", "NormalPressure", "HighPressure", "VeryHighPressure" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(Weather.NAMESPACE + "hasPressure"), pressure);
		
		getOnto().add(statement);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(Weather.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testVeryLowPressure() {
		for(float pressure=949.9f; pressure<998f; pressure+=.5f) {
			checkPressure(pressure, "AtmosphericPressure", "VeryLowPressure");			
		}
	}
	
	@Test
	public void testLowPressure() {
		for(float pressure=998f; pressure<1008f; pressure+=.1f) {
			checkPressure(pressure, "AtmosphericPressure", "LowPressure");			
		}
	}
	
	@Test
	public void testNormalPressure() {
		for(float pressure=1008f; pressure<1018f; pressure+=.1f) {
			checkPressure(pressure, "AtmosphericPressure", "NormalPressure");			
		}
	}
	
	@Test
	public void testHighPressure() {
		for(float pressure=1018f; pressure<1028f; pressure+=.1f) {
			checkPressure(pressure, "AtmosphericPressure", "HighPressure");			
		}
	}
	
	@Test
	public void testVeryHighPressure() {
		for(float pressure=1028f; pressure<1050f; pressure+=.5f) {
			checkPressure(pressure, "AtmosphericPressure", "VeryHighPressure");			
		}
	}
}
