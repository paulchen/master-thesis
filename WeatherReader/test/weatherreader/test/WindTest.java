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
public class WindTest extends IndividualsTest {
	private void checkWindSpeed(Float windSpeed, String... expectedConcepts) {
		String[] concepts = { "Wind", "Calm", "LightWind", "StrongWind", "Storm", "Hurricane" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Resource blankNode = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), windSpeed));
		getOnto().add(getOnto().createStatement(blankNode, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource(WeatherReport.NAMESPACE + "metresPerSecond")));
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasWindSpeed"), blankNode));
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	private void checkWindDirection(int windDirection, String... expectedConcepts) {
		String[] concepts = { "Wind", "DirectionalWind", "EastWind", "SouthWind", "WestWind", "NorthWind" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Resource blankNode1 = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode1, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), 10f));
		getOnto().add(getOnto().createStatement(blankNode1, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource(WeatherReport.NAMESPACE + "metresPerSecond")));
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasWindSpeed"), blankNode1));

		Resource blankNode2 = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode2, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), windDirection));
		getOnto().add(getOnto().createStatement(blankNode2, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource(WeatherReport.NAMESPACE + "metresPerSecond")));
		
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasWindDirection"), blankNode2));
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testCalm() {
		// use int here to avoid problems with floating-point numbers
		for(int windSpeed=0; windSpeed<10; windSpeed++) {
			checkWindSpeed(((float)windSpeed)/10, "Wind", "Calm");			
		}
	}
	
	@Test
	public void testLightWind() {
		for(int windSpeed=10; windSpeed<100; windSpeed+=5) {
			checkWindSpeed(((float)windSpeed)/10, "Wind", "LightWind");			
		}
	}
	
	@Test
	public void testStrongWind() {
		for(int windSpeed=100; windSpeed<200; windSpeed+=5) {
			checkWindSpeed(((float)windSpeed)/10, "Wind", "StrongWind");			
		}
	}
	
	@Test
	public void testStorm() {
		for(int windSpeed=200; windSpeed<320; windSpeed+=5) {
			checkWindSpeed(((float)windSpeed)/10, "Wind", "Storm");			
		}
	}
	
	@Test
	public void testHurricane() {
		for(int windSpeed=321; windSpeed<1000; windSpeed+=5) {
			checkWindSpeed(((float)windSpeed)/10, "Wind", "Storm", "Hurricane");			
		}
	}
	
	@Test
	public void testWestWind() {
		for(int windDirection = 225; windDirection < 315; windDirection += 5) {
			checkWindDirection(windDirection, "Wind", "DirectionalWind", "WestWind");
		}
	}
	
	@Test
	public void testNorthWind() {
		for(int windDirection = 315; windDirection < 405; windDirection += 5) {
			checkWindDirection(windDirection%360, "Wind", "DirectionalWind", "NorthWind");
		}
	}
	
	@Test
	public void testEastWind() {
		for(int windDirection = 45; windDirection < 135; windDirection += 5) {
			checkWindDirection(windDirection, "Wind", "DirectionalWind", "EastWind");
		}
	}
	
	@Test
	public void testSouthWind() {
		for(int windDirection = 135; windDirection < 225; windDirection += 5) {
			checkWindDirection(windDirection, "Wind", "DirectionalWind", "SouthWind");
		}
	}
}
