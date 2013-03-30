package at.ac.tuwien.auto.thinkhome.weatherimporter.test;

import org.junit.Test;

import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Precipitation;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherPhenomenon;
import at.ac.tuwien.auto.thinkhome.weatherimporter.test.base.IndividualsTest;


// TODO javadoc
public class PrecipitationTest extends IndividualsTest {
	private void checkPrecipitation(Float precipitationIntensity, int precipitationProbability, String... expectedConcepts) {
		String[] concepts = { "Precipitation", "NoRain", "LightRain", "MediumRain", "HeavyRain", "ExtremelyHeavyRain", "TropicalStormRain" };
		WeatherPhenomenon weatherPhenomenon = new Precipitation("precipitation", precipitationIntensity, (float)precipitationProbability/100);
		testConcepts(concepts, expectedConcepts, weatherPhenomenon);
	}
	
	@Test
	public void testNoRain() {
		// use int here to avoid problems with floating-point numbers
		for(int probability=0; probability<=100; probability+=10) {
			checkPrecipitation(0f, probability, "Precipitation", "NoRain");			
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
