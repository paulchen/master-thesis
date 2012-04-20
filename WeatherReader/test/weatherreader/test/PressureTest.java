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
public class PressureTest extends IndividualsTest {
	private void checkPressure(Float pressure, String... expectedConcepts) {
		String[] concepts = { "AtmosphericPressure", "VeryLowPressure", "LowPressure", "NormalPressure", "HighPressure", "VeryHighPressure" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasPressure"), pressure);
		
		getOnto().add(statement);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testVeryLowPressure() {
		// use int here to avoid problems with floating-point numbers
		for(int pressure=9499; pressure<9980; pressure+=5) {
			checkPressure(((float)pressure)/10, "AtmosphericPressure", "VeryLowPressure");			
		}
	}
	
	@Test
	public void testLowPressure() {
		for(int pressure=9980; pressure<10080; pressure+=1) {
			checkPressure(((float)pressure)/10, "AtmosphericPressure", "LowPressure");			
		}
	}
	
	@Test
	public void testNormalPressure() {
		for(int pressure=10080; pressure<10180; pressure+=1) {
			checkPressure(((float)pressure)/10, "AtmosphericPressure", "NormalPressure");			
		}
	}
	
	@Test
	public void testHighPressure() {
		for(int pressure=10180; pressure<10280; pressure+=1) {
			checkPressure(((float)pressure)/10, "AtmosphericPressure", "HighPressure");			
		}
	}
	
	@Test
	public void testVeryHighPressure() {
		for(int pressure=10280; pressure<10500; pressure+=5) {
			checkPressure(((float)pressure)/10, "AtmosphericPressure", "VeryHighPressure");			
		}
	}
}
