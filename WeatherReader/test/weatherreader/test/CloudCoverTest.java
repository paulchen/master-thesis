package weatherreader.test;

import org.junit.Test;

import weatherreader.model.CloudCover;
import weatherreader.model.WeatherPhenomenon;
import weatherreader.test.base.IndividualsTest;

// TODO javadoc
public class CloudCoverTest extends IndividualsTest {
	private void checkCloudCover(int cloudCover, String... expectedConcepts) {
		String[] concepts = { "CloudCover", "ClearSky", "PartlyCloudy", "MostlyCloudy", "Overcast", "UnknownCloudCover" };
		WeatherPhenomenon weatherPhenomenon = new CloudCover("cloudCover", 1000, cloudCover);
		testConcepts(concepts, expectedConcepts, weatherPhenomenon);
	}
	
	@Test
	public void testClearSky() {
		checkCloudCover(0, "CloudCover", "ClearSky");
	}
	
	@Test
	public void testPartlyCloudy() {
		checkCloudCover(1, "CloudCover", "PartlyCloudy");
		checkCloudCover(2, "CloudCover", "PartlyCloudy");
		checkCloudCover(3, "CloudCover", "PartlyCloudy");
		checkCloudCover(4, "CloudCover", "PartlyCloudy");
	}
	
	@Test
	public void testMostlyCloudy() {
		checkCloudCover(5, "CloudCover", "MostlyCloudy");
		checkCloudCover(6, "CloudCover", "MostlyCloudy");
		checkCloudCover(7, "CloudCover", "MostlyCloudy");
	}
	
	@Test
	public void testOvercast() {
		checkCloudCover(8, "CloudCover", "Overcast");
	}
	
	@Test
	public void testUnknownCloudCover() {
		checkCloudCover(9, "CloudCover", "UnknownCloudCover");
	}
}
