package weatherreader.test.base;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import weatherreader.model.WeatherReport;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;

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
		onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToWeatherState"), weatherState));
		
		weatherIndex++;
		
		return weatherPhenomenon;
	}
}
