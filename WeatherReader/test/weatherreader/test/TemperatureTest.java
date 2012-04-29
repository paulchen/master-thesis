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
public class TemperatureTest extends IndividualsTest {
	private void checkTemperature(Float temperatureValue, String... expectedConcepts) {
		String[] concepts = { "Temperature", "RoomTemperature", "AboveRoomTemperature", "BelowRoomTemperature", "Cold", "Heat", "Frost" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasTemperatureValue"), temperatureValue);
		
		getOnto().add(statement);
		
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
