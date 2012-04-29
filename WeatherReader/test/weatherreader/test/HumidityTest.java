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
public class HumidityTest extends IndividualsTest {
	private void checkHumidity(Float humidityValue, String... expectedConcepts) {
		String[] concepts = { "Humidity", "VeryDry", "Dry", "NormalHumidity", "Moist", "VeryMoist" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasHumidityValue"), humidityValue);
		
		getOnto().add(statement);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testVeryDry() throws Exception {
		// use int here to avoid problems with floating-point numbers
		for(int humidityValue=0; humidityValue<30; humidityValue++) {
			checkHumidity(((float)humidityValue)/100, "Humidity", "VeryDry");			
		}
	}
	
	@Test
	public void testDry() throws Exception {
		for(int humidityValue=30; humidityValue<40; humidityValue++) {
			checkHumidity(((float)humidityValue)/100, "Humidity", "Dry");			
		}
	}
	
	@Test
	public void testNormalHumidity() throws Exception {
		for(int humidityValue=40; humidityValue<=70; humidityValue++) {
			checkHumidity(((float)humidityValue)/100, "Humidity", "NormalHumidity");			
		}
	}
	
	@Test
	public void testMoist() throws Exception {
		for(int humidityValue=71; humidityValue<=80; humidityValue++) {
			checkHumidity(((float)humidityValue)/100, "Humidity", "Moist");			
		}
	}
	
	@Test
	public void testVeryMoist() throws Exception {
		for(int humidityValue=81; humidityValue<=100; humidityValue++) {
			checkHumidity(((float)humidityValue)/100, "Humidity", "VeryMoist");			
		}
	}
}
