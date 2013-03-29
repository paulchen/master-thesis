package weatherreader.test;

import org.junit.Test;

import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Pressure;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherPhenomenon;

import weatherreader.test.base.IndividualsTest;

// TODO javadoc
public class PressureTest extends IndividualsTest {
	private void checkPressure(Float pressureValue, String... expectedConcepts) {
		String[] concepts = { "AtmosphericPressure", "VeryLowPressure", "LowPressure", "NormalPressure", "HighPressure", "VeryHighPressure" };
		WeatherPhenomenon weatherPhenomenon = new Pressure("pressure", pressureValue);
		testConcepts(concepts, expectedConcepts, weatherPhenomenon);
	}
	
	@Test
	public void testVeryLowPressure() {
		// use int here to avoid problems with floating-point numbers
		for(int pressureValue=9499; pressureValue<9980; pressureValue+=5) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "VeryLowPressure");			
		}
	}
	
	@Test
	public void testLowPressure() {
		for(int pressureValue=9980; pressureValue<10080; pressureValue+=1) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "LowPressure");			
		}
	}
	
	@Test
	public void testNormalPressure() {
		for(int pressureValue=10080; pressureValue<10180; pressureValue+=1) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "NormalPressure");			
		}
	}
	
	@Test
	public void testHighPressure() {
		for(int pressureValue=10180; pressureValue<10280; pressureValue+=1) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "HighPressure");			
		}
	}
	
	@Test
	public void testVeryHighPressure() {
		for(int pressureValue=10280; pressureValue<10500; pressureValue+=5) {
			checkPressure(((float)pressureValue)/10, "AtmosphericPressure", "VeryHighPressure");			
		}
	}
}
