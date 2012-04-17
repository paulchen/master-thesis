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
public class PrecipitationTest extends IndividualsTest {
	private void checkPrecipitation(Float precipitationValue, int precipitationProbability, String... expectedConcepts) {
		String[] concepts = { "Precipitation", "NoRain", "LightRain", "MediumRain", "HeavyRain", "ExtremelyHeavyRain", "TropicalStormRain" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Float floatProbability = (float)precipitationProbability/100;
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement1 = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(Weather.NAMESPACE + "hasPrecipitationValue"), precipitationValue);
		Statement statement2 = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(Weather.NAMESPACE + "hasPrecipitationProbability"), floatProbability);
		
		getOnto().add(statement1);
		getOnto().add(statement2);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(Weather.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testNoRain() {
		for(int probability=0; probability<=1; probability+=10) {
			checkPrecipitation(0f, probability, "Precipitation", "NoRain");			
		}
		for(float value=0f; value<=100f; value+=5f) {
			checkPrecipitation(value, 0, "Precipitation", "NoRain");			
		}
	}
	
	@Test
	public void testLightRain() {
		for(int probability=1; probability<=1; probability+=10) {
			for(float value=1f; value<=5f; value+=1f) {
				checkPrecipitation(value, probability, "Precipitation", "LightRain");			
			}
		}
	}
	
	@Test
	public void testMediumRain() {
		for(int probability=1; probability<=1; probability+=10) {
			for(float value=6f; value<=20f; value+=1f) {
				checkPrecipitation(value, probability, "Precipitation", "MediumRain");			
			}
		}
	}
	
	@Test
	public void testHeavyRain() {
		for(int probability=1; probability<=1; probability+=10) {
			for(float value=21f; value<=50f; value+=5f) {
				System.out.println(value + " " + probability);
				checkPrecipitation(value, probability, "Precipitation", "HeavyRain");			
			}
		}
	}
	
	@Test
	public void testExtremelyHeavyRain() {
		for(int probability=1; probability<=1; probability+=10) {
			for(float value=51f; value<=100f; value+=7f) {
				checkPrecipitation(value, probability, "Precipitation", "ExtremelyHeavyRain");			
			}
		}
	}
	
	@Test
	public void testTropicalStormRain() {
		for(int probability=1; probability<=1; probability+=10) {
			for(float value=101f; value<=200f; value+=5f) {
				checkPrecipitation(value, probability, "Precipitation", "TropicalStormRain");			
			}
		}
	}
}
