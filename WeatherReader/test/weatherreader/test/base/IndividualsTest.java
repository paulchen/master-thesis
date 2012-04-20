package weatherreader.test.base;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import weatherreader.model.WeatherReport;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public abstract class IndividualsTest extends TestCase {
	private OntModel onto;
	private Individual weatherState;
	private int WeatherPhenomenonIndex = 0;
	
	protected OntModel getOnto() {
		return onto;
	}
	
	@Before
	public void setUp() {
		// TODO duplicate code here?
		onto = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		arp.read(onto, "file:ThinkHomeWeather.owl");
		
		OntClass weatherObservationClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherObservation");
		Individual weatherObservation = onto.createIndividual(WeatherReport.NAMESPACE + "weatherObservation", weatherObservationClass);
		
		Resource pointClass = onto.getResource(WeatherReport.WGS84 + "Point");
		
		Individual point = onto.createIndividual(WeatherReport.NAMESPACE + "point0", pointClass);
		onto.add(onto.createStatement(weatherObservation, onto.getProperty(WeatherReport.WGS84 + "location"), point));
		onto.add(onto.createStatement(point, RDF.type, pointClass));		
		
		onto.add(onto.createLiteralStatement(point, onto.getProperty(WeatherReport.WGS84 + "lat"), 48.21f));
		onto.add(onto.createLiteralStatement(point, onto.getProperty(WeatherReport.WGS84 + "long"), 16.37f));
		onto.add(onto.createLiteralStatement(point, onto.getProperty(WeatherReport.WGS84 + "alt"), 171));
		
		Resource instantClass = onto.getResource(WeatherReport.TIME + "Instant");
		Individual instant = onto.createIndividual(WeatherReport.NAMESPACE + "instant0", instantClass);

		Calendar calendar = new GregorianCalendar();
		Resource dateTimeClass = onto.getResource(WeatherReport.TIME + "DateTimeDescription");
		Individual dateTime = onto.createIndividual(WeatherReport.NAMESPACE + "dateTime0", dateTimeClass);
		
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherReport.TIME + "unitType"), onto.getResource(WeatherReport.TIME + "unitMinute")));
		onto.add(onto.createLiteralStatement(dateTime, onto.getProperty(WeatherReport.TIME + "minute"), new BigDecimal(calendar.get(Calendar.MINUTE))));
		onto.add(onto.createLiteralStatement(dateTime, onto.getProperty(WeatherReport.TIME + "hour"), new BigDecimal(calendar.get(Calendar.HOUR_OF_DAY))));
		
		String dayString = "---";
		if(calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			dayString += "0";
		}
		dayString += calendar.get(Calendar.DAY_OF_MONTH);
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherReport.TIME + "day"), onto.createTypedLiteral(dayString, XSDDatatype.XSDgDay)));
		
		String monthString = "--";
		if(calendar.get(Calendar.MONTH) < 10) {
			monthString += "0";
		}
		monthString += calendar.get(Calendar.MONTH);
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherReport.TIME + "month"), onto.createTypedLiteral(monthString, XSDDatatype.XSDgMonth)));
		
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherReport.TIME + "year"), onto.createTypedLiteral(String.valueOf(calendar.get(Calendar.YEAR)), XSDDatatype.XSDgYear)));
		
		onto.add(onto.createStatement(instant, onto.getProperty(WeatherReport.TIME + "inDateTime"), dateTime));
		
		onto.add(onto.createLiteralStatement(weatherObservation, onto.getProperty(WeatherReport.NAMESPACE + "hasObservationTime"), instant));

		OntClass sensorSourceClass = onto.getOntClass(WeatherReport.NAMESPACE + "ServiceSource");
		Individual sourceIndividual = onto.createIndividual(WeatherReport.NAMESPACE + "weatherService", sensorSourceClass);
		
		OntClass weatherStateClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherState");
		weatherState = onto.createIndividual(WeatherReport.NAMESPACE + "weather" + 0, weatherStateClass);
		onto.add(onto.createStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasSource"), sourceIndividual));
		onto.add(onto.createLiteralStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasPriority"), 1));
	}
	
	@After
	public void tearDown() {
		/* nothing to do? */
	}

	protected Individual createSingleWeatherPhenomenon() {
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "weatherPhenomenon" + WeatherPhenomenonIndex, weatherPhenomenonClass);
		onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
		
		WeatherPhenomenonIndex++;
		
		return weatherPhenomenon;
	}
}
