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
public class PrecipitationTest extends IndividualsTest {
	private void checkPrecipitation(Float precipitationValue, int precipitationProbability, String... expectedConcepts) {
		String[] concepts = { "Precipitation", "NoRain", "LightRain", "MediumRain", "HeavyRain", "ExtremelyHeavyRain", "TropicalStormRain" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Float floatProbability = (float)precipitationProbability/100;
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement1 = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasPrecipitationValue"), precipitationValue);
		Statement statement2 = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasPrecipitationProbability"), floatProbability);
		
		getOnto().add(statement1);
		getOnto().add(statement2);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testNoRain() {
		// use int here to avoid problems with floating-point numbers
		for(int probability=0; probability<=100; probability+=10) {
			checkPrecipitation(0f, probability, "Precipitation", "NoRain");			
		}
		for(int value=0; value<=100; value+=5) {
			checkPrecipitation((float)value, 0, "Precipitation", "NoRain");			
		}
	}
	
	@Test
	public void testLightRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int value=1; value<=5; value+=1) {
				checkPrecipitation((float)value, probability, "Precipitation", "LightRain");			
			}
		}
	}
	
	@Test
	public void testMediumRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int value=6; value<=20; value+=1) {
				checkPrecipitation((float)value, probability, "Precipitation", "MediumRain");			
			}
		}
	}
	
	@Test
	public void testHeavyRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int value=21; value<=50; value+=5) {
				checkPrecipitation((float)value, probability, "Precipitation", "HeavyRain");			
			}
		}
	}
	
	@Test
	public void testExtremelyHeavyRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int value=51; value<=100; value+=7) {
				checkPrecipitation((float)value, probability, "Precipitation", "ExtremelyHeavyRain");			
			}
		}
	}
	
	@Test
	public void testTropicalStormRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int value=101; value<=200; value+=5) {
				checkPrecipitation((float)value, probability, "Precipitation", "TropicalStormRain");			
			}
		}
	}
}
