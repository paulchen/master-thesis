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
public class PressureTest extends IndividualsTest {
	private void checkPressure(Float pressureValue, String... expectedConcepts) {
		String[] concepts = { "AtmosphericPressure", "VeryLowPressure", "LowPressure", "NormalPressure", "HighPressure", "VeryHighPressure" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Resource blankNode = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), pressureValue));
		// TODO get rid of magic constant for individual name here
		getOnto().add(getOnto().createStatement(blankNode, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource(WeatherReport.NAMESPACE + "hectopascal")));
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasPressureValue"), blankNode));

		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testVeryLowPressure() {
		// use int here to avoid problems with floating-point numbers
		for(int pressureValue=9499; pressureValue<9980; pressureValue+=5) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "VeryLowPressure");			
		}
	}
	
	@Test
	public void testLowPressure() {
		for(int pressureValue=9980; pressureValue<10080; pressureValue+=1) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "LowPressure");			
		}
	}
	
	@Test
	public void testNormalPressure() {
		for(int pressureValue=10080; pressureValue<10180; pressureValue+=1) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "NormalPressure");			
		}
	}
	
	@Test
	public void testHighPressure() {
		for(int pressureValue=10180; pressureValue<10280; pressureValue+=1) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "HighPressure");			
		}
	}
	
	@Test
	public void testVeryHighPressure() {
		for(int pressureValue=10280; pressureValue<10500; pressureValue+=5) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "VeryHighPressure");			
		}
	}
}
