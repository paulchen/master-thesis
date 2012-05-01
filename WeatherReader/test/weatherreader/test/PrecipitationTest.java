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
public class PrecipitationTest extends IndividualsTest {
	private void checkPrecipitation(Float precipitationIntensity, int precipitationProbability, String... expectedConcepts) {
		String[] concepts = { "Precipitation", "NoRain", "LightRain", "MediumRain", "HeavyRain", "ExtremelyHeavyRain", "TropicalStormRain" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Float floatProbability = (float)precipitationProbability/100;
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		
		Resource blankNode1 = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode1, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), floatProbability));
		// TODO get rid of magic constant for individual name here
		getOnto().add(getOnto().createStatement(blankNode1, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource("http://purl.oclc.org/NET/muo/ucum/unit/fraction/percent")));
		
		Resource blankNode2 = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode2, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), precipitationIntensity));
		getOnto().add(getOnto().createStatement(blankNode2, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource(WeatherReport.NAMESPACE + "millimetresPerHour")));
		
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasPrecipitationProbability"), blankNode1));
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasPrecipitationIntensity"), blankNode2));
		
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
		for(int intensity=0; intensity<=100; intensity+=5) {
			checkPrecipitation((float)intensity, 0, "Precipitation", "NoRain");			
		}
	}
	
	@Test
	public void testLightRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int intensity=1; intensity<=5; intensity+=1) {
				checkPrecipitation((float)intensity, probability, "Precipitation", "LightRain");			
			}
		}
	}
	
	@Test
	public void testMediumRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int intensity=6; intensity<=20; intensity+=1) {
				checkPrecipitation((float)intensity, probability, "Precipitation", "MediumRain");			
			}
		}
	}
	
	@Test
	public void testHeavyRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int intensity=21; intensity<=50; intensity+=5) {
				checkPrecipitation((float)intensity, probability, "Precipitation", "HeavyRain");			
			}
		}
	}
	
	@Test
	public void testExtremelyHeavyRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int intensity=51; intensity<=100; intensity+=7) {
				checkPrecipitation((float)intensity, probability, "Precipitation", "ExtremelyHeavyRain");			
			}
		}
	}
	
	@Test
	public void testTropicalStormRain() {
		for(int probability=1; probability<=100; probability+=10) {
			for(int intensity=101; intensity<=200; intensity+=5) {
				checkPrecipitation((float)intensity, probability, "Precipitation", "TropicalStormRain");			
			}
		}
	}
}
