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
public class SunPositionTest extends IndividualsTest {
	private void checkSunDirection(int sunDirection, int sunElevationAngle, String... expectedConcepts) {
//		String[] concepts = { "SunPosition", "SunFromNorth", "SunFromEast", "SunFromSouth", "SunFromWest", "Day", "Night", "SolarTwilight", "CivilTwilight", "NauticalTwilight", "AstronomicalTwilight" };
		String[] concepts = { "SunPosition", "SunFromNorth", "SunFromEast", "SunFromSouth", "SunFromWest" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Resource blankNode1 = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode1, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), sunDirection));
		// TODO get rid of magic constant for individual name here
		getOnto().add(getOnto().createStatement(blankNode1, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource("http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree")));
		
		Resource blankNode2 = getOnto().createResource();
		getOnto().add(getOnto().createLiteralStatement(blankNode2, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), (float)sunElevationAngle));
		// TODO get rid of magic constant for individual name here
		getOnto().add(getOnto().createStatement(blankNode2, getOnto().getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), getOnto().getResource("http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree")));
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasSunDirection"), blankNode1));
		getOnto().add(getOnto().createStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasSunElevationAngle"), blankNode2));
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
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
				System.out.println(direction + " " + elevation);
				checkSunDirection(direction, elevation, "SunPosition", "SunFromWest");
			}
		}
	}
}
