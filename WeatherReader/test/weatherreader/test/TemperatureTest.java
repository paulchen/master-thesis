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
		for(float temperature=25.1f; temperature<=30f; temperature+=.1f) {
			checkTemperature(temperature, "Temperature", "AboveRoomTemperature");			
		}
	}
	
	@Test
	public void testRoomTemperature() {
		for(float temperature=20f; temperature<=25f; temperature+=.1f) {
			checkTemperature(temperature, "Temperature", "RoomTemperature");			
		}
	}
	
	@Test
	public void testBelowRoomTemperature() {
		for(float temperature=10f; temperature<20f; temperature+=.1f) {
			checkTemperature(temperature, "Temperature", "BelowRoomTemperature");			
		}
	}
	
	@Test
	public void testCold() {
		for(float temperature=0f; temperature<10f; temperature+=.1f) {
			checkTemperature(temperature, "Temperature", "Cold");			
		}
	}
	
	@Test
	public void testHeat() {
		for(float temperature=30.1f; temperature<=100f; temperature+=.5f) {
			checkTemperature(temperature, "Temperature", "Heat");			
		}
	}
	
	@Test
	public void testFrost() {
		for(float temperature=-100f; temperature<0f; temperature+=.5f) {
			checkTemperature(temperature, "Temperature", "Frost");			
		}
	}
}
