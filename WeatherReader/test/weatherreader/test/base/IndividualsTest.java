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

// TODO javadoc
public abstract class IndividualsTest extends TestCase {
	private OntModel onto;
	private int weatherIndex = 0;
	
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
	}
	
	@After
	public void tearDown() {
		/* nothing to do? */
	}

	protected Individual createSingleWeatherPhenomenon() {
		OntClass weatherStateClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherState");
		Individual weatherState = onto.createIndividual(WeatherReport.NAMESPACE + "weather" + weatherIndex, weatherStateClass);
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "weatherPhenomenon" + weatherIndex, weatherPhenomenonClass);
		onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
		
		weatherIndex++;
		
		return weatherPhenomenon;
	}
}
