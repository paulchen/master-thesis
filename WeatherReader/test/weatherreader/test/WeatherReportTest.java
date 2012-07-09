package weatherreader.test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import weatherreader.model.GeographicalPosition;
import weatherreader.model.Instant;
import weatherreader.model.Interval;
import weatherreader.model.ServiceSource;
import weatherreader.model.WeatherConstants;
import weatherreader.model.WeatherReport;
import weatherreader.model.WeatherSource;
import weatherreader.model.WeatherState;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class WeatherReportTest extends TestCase {
	private String[] timeConcepts = { "CurrentWeatherReport",
			"ForecastWeatherReport", "ShortRangeForecastReport", "MediumRangeForecastReport", "LongRangeForecastReport",
			"Forecast1HourWeatherReport", "Forecast2HoursWeatherReport", "Forecast3HoursWeatherReport",
			"Forecast6HoursWeatherReport", "Forecast9HoursWeatherReport", "Forecast12HoursWeatherReport",
			"Forecast15HoursWeatherReport", "Forecast18HoursWeatherReport", "Forecast21HoursWeatherReport",
			"Forecast24HoursWeatherReport" };
	
	private OntModel onto;
	private int reportIndex = 0;

	private GeographicalPosition position;
	private WeatherSource source;
	private int priority;
	
	@Before
	public void setUp() {
		onto = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		arp.read(onto, "file:ThinkHomeWeather.owl");
		
		position = new GeographicalPosition(48.21f, 16.37f, 171f);
		priority = 421;
		source = new ServiceSource("testSource");
	}
	
	private void testConcepts(float startTime, String[] allConcepts, String... expectedConcepts) {
		reportIndex++;
		
		Interval startInterval = Interval.getInterval(startTime);
		Interval endInterval = Interval.getInterval(startTime+1);
		Instant observationTime = new Instant("instant" + reportIndex, new Date());
		WeatherState weatherState = new WeatherState("weatherState" + reportIndex);
		WeatherReport weatherReport = new WeatherReport(
				"weatherReport" + reportIndex,
				observationTime, startInterval, endInterval,
				priority, source, position, weatherState);
		
		weatherReport.createIndividuals(onto);
		
		List<String> expected = Arrays.asList(expectedConcepts);
		
		for(String concept : allConcepts) {
			assertEquals(expected.contains(concept) ? 1 : 0, onto.listStatements(weatherReport.getOntIndividual(), RDF.type, onto.getOntClass(WeatherConstants.NAMESPACE + concept)).toSet().size());
		}
	}
	
	@Test
	public void testTimedWeatherReports() {
		testConcepts(0f, timeConcepts, "CurrentWeatherReport");
		testConcepts(1f, timeConcepts, "ForecastWeatherReport", "ShortRangeForecastReport", "Forecast1HourWeatherReport");
		testConcepts(2f, timeConcepts, "ForecastWeatherReport", "ShortRangeForecastReport", "Forecast2HoursWeatherReport");
		testConcepts(3f, timeConcepts, "ForecastWeatherReport", "ShortRangeForecastReport", "Forecast3HoursWeatherReport");
		testConcepts(4f, timeConcepts, "ForecastWeatherReport", "MediumRangeForecastReport");
		testConcepts(5f, timeConcepts, "ForecastWeatherReport", "MediumRangeForecastReport");
		testConcepts(6f, timeConcepts, "ForecastWeatherReport", "MediumRangeForecastReport", "Forecast6HoursWeatherReport");
		testConcepts(7f, timeConcepts, "ForecastWeatherReport", "MediumRangeForecastReport");
		testConcepts(8f, timeConcepts, "ForecastWeatherReport", "MediumRangeForecastReport");
		testConcepts(9f, timeConcepts, "ForecastWeatherReport", "MediumRangeForecastReport", "Forecast9HoursWeatherReport");
		testConcepts(10f, timeConcepts, "ForecastWeatherReport", "MediumRangeForecastReport");
		testConcepts(11f, timeConcepts, "ForecastWeatherReport", "MediumRangeForecastReport");
		testConcepts(12f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport", "Forecast12HoursWeatherReport");
		testConcepts(13f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport");
		testConcepts(14f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport");
		testConcepts(15f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport", "Forecast15HoursWeatherReport");
		testConcepts(16f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport");
		testConcepts(17f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport");
		testConcepts(18f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport", "Forecast18HoursWeatherReport");
		testConcepts(19f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport");
		testConcepts(20f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport");
		testConcepts(21f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport", "Forecast21HoursWeatherReport");
		testConcepts(22f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport");
		testConcepts(23f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport");
		testConcepts(24f, timeConcepts, "ForecastWeatherReport", "LongRangeForecastReport", "Forecast24HoursWeatherReport");
	}
}
