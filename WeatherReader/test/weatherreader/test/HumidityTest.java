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
		// use int here to avoid problems with floating-point numbers
		for(int humidity=0; humidity<30; humidity++) {
			checkHumidity(((float)humidity)/100, "Humidity", "VeryDry");			
		}
	}
	
	@Test
	public void testDry() throws Exception {
		for(int humidity=30; humidity<40; humidity++) {
			checkHumidity(((float)humidity)/100, "Humidity", "Dry");			
		}
	}
	
	@Test
	public void testNormalHumidity() throws Exception {
		for(int humidity=40; humidity<=70; humidity++) {
			checkHumidity(((float)humidity)/100, "Humidity", "NormalHumidity");			
		}
	}
	
	@Test
	public void testMoist() throws Exception {
		for(int humidity=71; humidity<=80; humidity++) {
			checkHumidity(((float)humidity)/100, "Humidity", "Moist");			
		}
	}
	
	@Test
	public void testVeryMoist() throws Exception {
		for(int humidity=81; humidity<=100; humidity++) {
			checkHumidity(((float)humidity)/100, "Humidity", "VeryMoist");			
		}
	}
}
