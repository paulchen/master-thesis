package at.ac.tuwien.auto.thinkhome.weatherimporter.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.jena.PelletReasonerFactory;


import at.ac.tuwien.auto.thinkhome.weatherimporter.model.CloudCover;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Precipitation;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Temperature;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Weather;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherPhenomenon;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherState;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Wind;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.vocabulary.RDF;

public class WeatherStateTest extends TestCase {
	private OntModel onto;
	private int stateIndex = 0;
	
	@Before
	public void setUp() {
		onto = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		arp.read(onto, "file:ThinkHomeWeather.owl");
	}
	
	@After
	public void tearDown() {
		/* nothing to do */
	}
	
	private void testState(WeatherPhenomenon[] phenomena, String[] expectedConcepts) {
		String[] allConcepts = {
				"CalmWeather", "WindyWeather", "StormyWeather",
				"ClearWeather", "CloudyWeather",
				"ColdWeather", "PleasantTemperatureWeather", "HotWeather",
				"NoRainWeather", "RainyWeather", "VeryRainyWeather",
			};
		List<String> expected = Arrays.asList(expectedConcepts);

		WeatherState state = new WeatherState("weatherState" + stateIndex);
		stateIndex++;
		
		state.addPhenomena(Arrays.asList(phenomena));
		
		state.createIndividuals(onto);
		
		for(String concept : allConcepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, onto.listStatements(state.getIndividual(), RDF.type, onto.getOntClass(Weather.NAMESPACE + concept)).toSet().size());
		}		
	}
	
	@Test
	public void testWind() {
		for(int a=0; a<10; a++) {
			testState(new WeatherPhenomenon[] { new Wind("wind" + a, a, 0) }, new String[] { "CalmWeather" });
		}
		for(int a=10; a<20; a++) {
			testState(new WeatherPhenomenon[] { new Wind("wind" + a, a, 0) }, new String[] { "WindyWeather" });
		}
		for(int a=20; a<100; a++) {
			testState(new WeatherPhenomenon[] { new Wind("wind" + a, a, 0) }, new String[] { "WindyWeather", "StormyWeather" });
		}
	}

	@Test
	public void testCloudCover() {
		for(int a=0; a<5; a++) {
			testState(new WeatherPhenomenon[] { new CloudCover("cloud" + a, 1000, a) }, new String[] { "ClearWeather" });
		}
		for(int a=5; a<9; a++) {
			testState(new WeatherPhenomenon[] { new CloudCover("cloud" + a, 1000, a) }, new String[] { "CloudyWeather" });
		}
	}
	
	@Test
	public void testTemperature() {
		for(int a=-50; a<10; a++) {
			testState(new WeatherPhenomenon[] { new Temperature("temperature" + a, a) }, new String[] { "ColdWeather" });
		}
		for(int a=10; a<31; a++) {
			testState(new WeatherPhenomenon[] { new Temperature("temperature" + a, a) }, new String[] { "PleasantTemperatureWeather" });
		}
		for(int a=31; a<50; a++) {
			testState(new WeatherPhenomenon[] { new Temperature("temperature" + a, a) }, new String[] { "HotWeather" });
		}
	}
	
	@Test
	public void testPrecipitation() {
		testState(new WeatherPhenomenon[] { new Precipitation("rain0", 0, 1) }, new String[] { "NoRainWeather" });
		for(int a=1; a<21; a++) {
			testState(new WeatherPhenomenon[] { new Precipitation("rain" + a, a, 1) }, new String[] { "RainyWeather" });
		}
		for(int a=21; a<100; a++) {
			testState(new WeatherPhenomenon[] { new Precipitation("rain" + a, a, 1) }, new String[] { "RainyWeather", "VeryRainyWeather" });
		}
	}
	
	// TODO FairWeather, AiringWeather, SunProtectionWeather, SevereWeather
}
