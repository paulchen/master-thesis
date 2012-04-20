package weatherreader.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import weatherreader.model.WeatherReport;
import weatherreader.test.base.IndividualsTest;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class CloudCoverTest extends IndividualsTest {
	private void checkCloudCover(int cloudCover, String... expectedConcepts) {
		String[] concepts = { "CloudCover", "ClearSky", "PartlyCloudy", "MostlyCloudy", "Overcast", "UnknownCloudCover" };
		List<String> expected = Arrays.asList(expectedConcepts);
		
		Individual weatherPhenomenon = createSingleWeatherPhenomenon();
		Statement statement = getOnto().createLiteralStatement(weatherPhenomenon, getOnto().getProperty(WeatherReport.NAMESPACE + "hasCloudCover"), cloudCover);
		
		getOnto().add(statement);
		
		for(String concept : concepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, getOnto().listStatements(weatherPhenomenon, RDF.type, getOnto().getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
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
