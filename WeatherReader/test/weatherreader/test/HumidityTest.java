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
public class HumidityTest extends IndividualsTest {
	private void checkHumidity(Float temperature, String... expectedConcepts) {
		String[] concepts = { "Humidity", "VeryDry", "Dry", "NormalHumidity", "Moist", "VeryMoist" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(Weather.NAMESPACE + "hasHumidity"), temperature);
		
		getOnto().add(statement);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(Weather.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testVeryDry() throws Exception {
		for(float temperature=0f; temperature<.3f; temperature+=.01f) {
			checkHumidity(temperature, "Humidity", "VeryDry");			
		}
	}
	
	@Test
	public void testDry() throws Exception {
		for(float temperature=.3f; temperature<.4f; temperature+=.01f) {
			checkHumidity(temperature, "Humidity", "Dry");			
		}
	}
	
	@Test
	public void testNormalHumidity() throws Exception {
		for(float temperature=.4f; temperature<=.7f; temperature+=.01f) {
			checkHumidity(temperature, "Humidity", "NormalHumidity");			
		}
	}
	
	@Test
	public void testMoist() throws Exception {
		for(float temperature=.71f; temperature<=.8f; temperature+=.01f) {
			checkHumidity(temperature, "Humidity", "Moist");			
		}
	}
	
	@Test
	public void testVeryMoist() throws Exception {
		for(float temperature=.81f; temperature<=1f; temperature+=.01f) {
			checkHumidity(temperature, "Humidity", "VeryMoist");			
		}
	}
}
