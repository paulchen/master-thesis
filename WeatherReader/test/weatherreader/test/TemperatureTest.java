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
public class TemperatureTest extends IndividualsTest {
	private void checkTemperature(Float temperature, String... expectedConcepts) {
		String[] concepts = { "Temperature", "RoomTemperature", "AboveRoomTemperature", "BelowRoomTemperature", "Cold", "Heat", "Frost" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(Weather.NAMESPACE + "hasTemperature"), temperature);
		
		getOnto().add(statement);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(Weather.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testAboveRoomTemperature() {
		// use int here to avoid problems with floating-point numbers
		for(int temperature=251; temperature<=300; temperature+=1) {
			checkTemperature(((float)temperature)/10, "Temperature", "AboveRoomTemperature");			
		}
	}
	
	@Test
	public void testRoomTemperature() {
		for(int temperature=200; temperature<=250; temperature+=1) {
			checkTemperature(((float)temperature)/10, "Temperature", "RoomTemperature");			
		}
	}
	
	@Test
	public void testBelowRoomTemperature() {
		for(int temperature=100; temperature<200; temperature+=1) {
			checkTemperature(((float)temperature)/10, "Temperature", "BelowRoomTemperature");			
		}
	}
	
	@Test
	public void testCold() {
		for(int temperature=0; temperature<100; temperature+=1) {
			checkTemperature(((float)temperature)/10, "Temperature", "Cold");			
		}
	}
	
	@Test
	public void testHeat() {
		for(int temperature=301; temperature<=100; temperature+=5) {
			checkTemperature(((float)temperature)/10, "Temperature", "Heat");			
		}
	}
	
	@Test
	public void testFrost() {
		for(int temperature=-1000; temperature<0; temperature+=5) {
			checkTemperature(((float)temperature)/10, "Temperature", "Frost");			
		}
	}
}
