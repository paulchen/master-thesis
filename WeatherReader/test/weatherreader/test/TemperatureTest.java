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
public class TemperatureTest extends IndividualsTest {
	private void checkTemperature(Float temperatureValue, String... expectedConcepts) {
		String[] concepts = { "Temperature", "RoomTemperature", "AboveRoomTemperature", "BelowRoomTemperature", "Cold", "Heat", "Frost" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Resource blankNode = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), temperatureValue));
		// TODO get rid of magic constant for individual name here
		getOnto().add(getOnto().createStatement(blankNode, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource("http://purl.oclc.org/NET/muo/ucum/unit/temperature/degree-Celsius")));
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasTemperatureValue"), blankNode));
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testAboveRoomTemperature() {
		// use int here to avoid problems with floating-point numbers
		for(int temperatureValue=251; temperatureValue<=300; temperatureValue+=1) {
			checkTemperature(((float)temperatureValue)/10, "Temperature", "AboveRoomTemperature");			
		}
	}
	
	@Test
	public void testRoomTemperature() {
		for(int temperatureValue=200; temperatureValue<=250; temperatureValue+=1) {
			checkTemperature(((float)temperatureValue)/10, "Temperature", "RoomTemperature");			
		}
	}
	
	@Test
	public void testBelowRoomTemperature() {
		for(int temperatureValue=100; temperatureValue<200; temperatureValue+=1) {
			checkTemperature(((float)temperatureValue)/10, "Temperature", "BelowRoomTemperature");			
		}
	}
	
	@Test
	public void testCold() {
		for(int temperatureValue=0; temperatureValue<100; temperatureValue+=1) {
			checkTemperature(((float)temperatureValue)/10, "Temperature", "Cold");			
		}
	}
	
	@Test
	public void testHeat() {
		for(int temperatureValue=301; temperatureValue<=100; temperatureValue+=5) {
			checkTemperature(((float)temperatureValue)/10, "Temperature", "Heat");			
		}
	}
	
	@Test
	public void testFrost() {
		for(int temperatureValue=-1000; temperatureValue<0; temperatureValue+=5) {
			checkTemperature(((float)temperatureValue)/10, "Temperature", "Frost");			
		}
	}
}
