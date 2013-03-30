package at.ac.tuwien.auto.thinkhome.weatherimporter.test;

import org.junit.Test;

import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Humidity;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherPhenomenon;
import at.ac.tuwien.auto.thinkhome.weatherimporter.test.base.IndividualsTest;


// TODO javadoc
public class HumidityTest extends IndividualsTest {
	private void checkHumidity(Float humidityValue, String... expectedConcepts) {
		String[] concepts = { "Humidity", "VeryDry", "Dry", "NormalHumidity", "Moist", "VeryMoist" };
		WeatherPhenomenon weatherPhenomenon = new Humidity("humidity", humidityValue);
		testConcepts(concepts, expectedConcepts, weatherPhenomenon);
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
