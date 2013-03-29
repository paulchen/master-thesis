package weatherreader.test;

import org.junit.Test;

import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherPhenomenon;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Wind;

import weatherreader.test.base.IndividualsTest;

// TODO javadoc
public class WindTest extends IndividualsTest {
	private void checkWindSpeed(Float windSpeed, String... expectedConcepts) {
		String[] concepts = { "Wind", "Calm", "LightWind", "StrongWind", "Storm", "Hurricane" };
		WeatherPhenomenon weatherPhenomenon = new Wind("wind", windSpeed, 0);
		testConcepts(concepts, expectedConcepts, weatherPhenomenon);
	}
	
	private void checkWindDirection(int windDirection, String... expectedConcepts) {
		String[] concepts = { "Wind", "DirectionalWind", "EastWind", "SouthWind", "WestWind", "NorthWind" };
		WeatherPhenomenon weatherPhenomenon = new Wind("wind", 10f, windDirection);
		testConcepts(concepts, expectedConcepts, weatherPhenomenon);
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
