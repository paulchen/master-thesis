package weatherreader.test;

import org.junit.Test;

import weatherreader.model.SolarRadiation;
import weatherreader.model.WeatherPhenomenon;
import weatherreader.test.base.IndividualsTest;

// TODO javadoc
public class SolarRadiationTest extends IndividualsTest {
	private void checkSunRadiation(float sunRadiationValue, String... expectedConcepts) {
		String[] concepts = { "SolarRadiation", "NoRadiation", "LowRadiation", "MediumRadiation", "HighRadiation", "VeryHighRadiation" };
		WeatherPhenomenon weatherPhenomenon = new SolarRadiation("solarRadiation", sunRadiationValue);
		testConcepts(concepts, expectedConcepts, weatherPhenomenon);
	}
	
	@Test
	public void testNoRadiation() {
		checkSunRadiation(0f, "SolarRadiation", "NoRadiation");
	}

	@Test
	public void testLowRadiation() {
		// use int here to avoid problems with floating-point numbers
		for(int radiationValue=1; radiationValue<250; radiationValue+=8) {
			checkSunRadiation(radiationValue, "SolarRadiation", "LowRadiation");
		}
	}
	
	@Test
	public void testMediumRadiation() {
		for(int radiationValue=250; radiationValue<500; radiationValue+=3) {
			checkSunRadiation(radiationValue, "SolarRadiation", "MediumRadiation");
		}
	}
	
	@Test
	public void testHighRadiation() {
		for(int radiationValue=500; radiationValue<750; radiationValue+=3) {
			checkSunRadiation(radiationValue, "SolarRadiation", "HighRadiation");
		}
	}
	
	@Test
	public void testVeryHighRadiation() {
		for(int radiationValue=750; radiationValue<1500; radiationValue+=10) {
			checkSunRadiation(radiationValue, "SolarRadiation", "VeryHighRadiation");
		}
	}
}
