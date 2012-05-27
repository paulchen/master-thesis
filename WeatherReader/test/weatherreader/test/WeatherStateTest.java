package weatherreader.test;

import junit.framework.TestCase;

import org.junit.After;
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

public class WeatherStateTest extends TestCase {
	private OntModel onto;
	private int stateIndex = 0;
	
	@Before
	public void setUp() {
		onto = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		arp.read(onto, "file:ThinkHomeWeather.owl");
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test
	public void testWindyWeather() {
		// TODO duplicate code here?
		OntClass weatherStateClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherState");
		Individual weatherState = onto.createIndividual(WeatherReport.NAMESPACE + "weatherState" + stateIndex, weatherStateClass);
		
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), 30f));
		// TODO get rid of magic constant for individual name here
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "metresPerSecond")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "wind" + stateIndex, weatherPhenomenonClass);
		onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToWeatherState"), weatherState));
		onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasWindSpeed"), blankNode));
		
		assertEquals(1, onto.listStatements(weatherPhenomenon, RDF.type, onto.getOntClass(WeatherReport.NAMESPACE + "Storm")).toSet().size());
		assertEquals(1, onto.listStatements(weatherState, RDF.type, onto.getOntClass(WeatherReport.NAMESPACE + "WindyWeather")).toSet().size());
		
		stateIndex++;
	}
}
