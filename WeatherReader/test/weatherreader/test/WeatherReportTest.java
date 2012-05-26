package weatherreader.test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import weatherreader.model.WeatherReport;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class WeatherReportTest extends TestCase {
	private String[] timeConcepts = { "TimedWeatherReport", "CurrentWeatherReport",
			"ForecastWeatherReport", "ShortRangeForecastReport", "MediumRangeForecastReport", "LongRangeForecastReport",
			"Forecast1HourWeatherReport", "Forecast2HoursWeatherReport", "Forecast3HoursWeatherReport",
			"Forecast6HoursWeatherReport", "Forecast9HoursWeatherReport", "Forecast12HoursWeatherReport",
			"Forecast15HoursWeatherReport", "Forecast18HoursWeatherReport", "Forecast21HoursWeatherReport",
			"Forecast24HoursWeatherReport" };
	
	private OntModel onto;
	private int reportIndex = 0;
	
	@Before
	public void setUp() {
		onto = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		arp.read(onto, "file:ThinkHomeWeather.owl");
	}
	
	private void testConcepts(float startTime, String[] allConcepts, String... expectedConcepts) {
		reportIndex++;
		
		// TODO duplicate code here?
		OntClass weatherStateClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherState");
		Individual weatherReport = onto.createIndividual(WeatherReport.NAMESPACE + "weather" + reportIndex, weatherStateClass);
				
		OntClass hourClass = onto.getOntClass(WeatherReport.NAMESPACE + "Hour");
		Individual hour1 = onto.createIndividual(WeatherReport.NAMESPACE + "hour" + startTime, hourClass);
		onto.add(onto.createLiteralStatement(hour1, onto.getProperty(WeatherReport.TIME + "hours"), new BigDecimal(startTime)));

		Resource intervalClass = onto.getResource(WeatherReport.TIME + "Interval");
		Individual interval1 = onto.createIndividual(WeatherReport.NAMESPACE + "interval" + reportIndex, intervalClass);
		onto.add(onto.createStatement(interval1, onto.getProperty(WeatherReport.TIME + "hasDurationDescription"), hour1));
		onto.add(onto.createStatement(weatherReport, onto.getProperty(WeatherReport.NAMESPACE + "hasStartTime"), interval1));

		List<String> expected = Arrays.asList(expectedConcepts);
		
		for(String concept : allConcepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, onto.listStatements(weatherReport, RDF.type, onto.getOntClass(WeatherReport.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testTimedWeatherReports() {
		testConcepts(0f, timeConcepts, "TimedWeatherReport", "CurrentWeatherReport");
	}
}
