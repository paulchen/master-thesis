package weatherreader.test;

import org.junit.Test;

import weatherreader.model.SunPosition;
import weatherreader.model.WeatherPhenomenon;
import weatherreader.test.base.IndividualsTest;

// TODO javadoc
public class SunPositionTest extends IndividualsTest {
	private void checkSunElevation(int sunDirection, int sunElevationAngle, String... expectedConcepts) {
		String[] concepts = { "SunPosition", "Day", "Night", "SunBelowHorizon", "Twilight", "SolarTwilight", "CivilTwilight", "NauticalTwilight", "AstronomicalTwilight" };
		WeatherPhenomenon weatherPhenomenon = new SunPosition("sunPosition", 90-sunElevationAngle, sunDirection);
		testConcepts(concepts, expectedConcepts, weatherPhenomenon);
	}
	
	private void checkSunDirection(int sunDirection, int sunElevationAngle, String... expectedConcepts) {
		String[] concepts = { "SunPosition", "SunFromNorth", "SunFromEast", "SunFromSouth", "SunFromWest" };
		WeatherPhenomenon weatherPhenomenon = new SunPosition("sunPosition", 90-sunElevationAngle, sunDirection);
		testConcepts(concepts, expectedConcepts, weatherPhenomenon);
	}
	
	@Test
	public void testSunFromNorth() {
		for(int direction=0; direction<=45; direction+=15) {
			for(int elevation=-90; elevation<=90; elevation+=15) {
				checkSunDirection(direction, elevation, "SunPosition", "SunFromNorth");
			}
		}
		for(int direction=316; direction<=360; direction+=15) {
			for(int elevation=-90; elevation<=90; elevation+=15) {
				checkSunDirection(direction, elevation, "SunPosition", "SunFromNorth");
			}
		}
		for(int elevation=-90; elevation<=90; elevation+=15) {
			checkSunDirection(359, elevation, "SunPosition", "SunFromNorth");
		}
	}
	
	@Test
	public void testSunFromEast() {
		for(int elevation=-90; elevation<=90; elevation+=10) {
			checkSunDirection(46, elevation, "SunPosition", "SunFromEast");
		}
		for(int direction=60; direction<=135; direction+=15) {
			for(int elevation=-90; elevation<=90; elevation+=10) {
				checkSunDirection(direction, elevation, "SunPosition", "SunFromEast");
			}
		}
	}
	
	@Test
	public void testSunFromSouth() {
		for(int elevation=-90; elevation<=90; elevation+=10) {
			checkSunDirection(136, elevation, "SunPosition", "SunFromSouth");
		}
		for(int direction=150; direction<=225; direction+=15) {
			for(int elevation=-90; elevation<=90; elevation+=10) {
				checkSunDirection(direction, elevation, "SunPosition", "SunFromSouth");
			}
		}
	}
	
	@Test
	public void testSunFromWest() {
		for(int elevation=-90; elevation<=90; elevation+=10) {
			checkSunDirection(226, elevation, "SunPosition", "SunFromWest");
		}
		for(int direction=240; direction<=315; direction+=15) {
			for(int elevation=-90; elevation<=90; elevation+=10) {
				checkSunDirection(direction, elevation, "SunPosition", "SunFromWest");
			}
		}
	}
	
	@Test
	public void testDay() {
		for(int direction=0; direction<360; direction+=30) {
			for(int elevation=6; elevation<=90; elevation+=12) {
				checkSunElevation(direction, elevation, "SunPosition", "Day");
			}
		}
	}
	
	@Test
	public void testSolarTwilight() {
		for(int direction=0; direction<360; direction+=30) {
			for(int elevation=0; elevation<=5; elevation+=5) {
				checkSunElevation(direction, elevation, "SunPosition", "Day", "SolarTwilight");
			}
		}
	}
	
	@Test
	public void testCivilTwilight() {
		for(int direction=0; direction<360; direction+=30) {
			for(int elevation=-6; elevation<0; elevation+=5) {
				checkSunElevation(direction, elevation, "SunPosition", "CivilTwilight", "Twilight", "SunBelowHorizon" );
			}
		}
	}
	
	@Test
	public void testNauticalTwilight() {
		for(int direction=0; direction<360; direction+=30) {
			for(int elevation=-12; elevation<-6; elevation+=5) {
				checkSunElevation(direction, elevation, "SunPosition", "NauticalTwilight", "Twilight", "SunBelowHorizon" );
			}
		}
	}
	
	@Test
	public void testAstronomicalTwilight() {
		for(int direction=0; direction<360; direction+=30) {
			for(int elevation=-18; elevation<-12; elevation+=5) {
				checkSunElevation(direction, elevation, "SunPosition", "AstronomicalTwilight", "Twilight", "SunBelowHorizon" );
			}
		}
	}
	
	@Test
	public void testNight() {
		for(int direction=0; direction<360; direction+=30) {
			for(int elevation=-90; elevation<-18; elevation+=10) {
				checkSunElevation(direction, elevation, "SunPosition", "Night", "SunBelowHorizon");
			}
			checkSunElevation(direction, -19, "SunPosition", "Night", "SunBelowHorizon");
		}
	}
}
