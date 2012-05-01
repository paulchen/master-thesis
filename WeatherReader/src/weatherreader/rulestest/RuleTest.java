package weatherreader.rulestest;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import weatherreader.model.WeatherReport;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.PrintUtil;

public class RuleTest {
	private static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	public static void main(String[] args) throws Exception {
//		OntModel onto = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
		OntModel onto = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		
		String inputFilename = "WeatherReaderTest.owl";
		
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		arp.read(onto, "file:" + inputFilename);
	
//		List<Rule> rules = Rule.rulesFromURL("file:test.rules");
//		GenericRuleReasoner ruleReasoner = new GenericRuleReasoner(rules);
//		InfModel model = ModelFactory.createInfModel(ruleReasoner, onto);
		OntModel model = onto;
		
//		Resource r = model.getResource(Weather.NAMESPACE + "NormalPressure");
//		StmtIterator it = model.listStatements(r, null, (RDFNode)null);
		
//		Property p = model.getProperty(Weather.NAMESPACE + "hasFollowingPhenomenon");
//		StmtIterator it = model.listStatements(null, p, (RDFNode)null);

		// String[] classes = { "StablePressure", "DecreasingPressure", "IncreasingPressure" };
		String[] classes = { "HighPressure", "LowPressure", "NormalPressure", "CurrentWeatherState", "WeatherStateFromSensor" };
		
		for(String clazz : classes) {
			System.out.println(clazz + ":");
			
			Property p = model.getProperty(RDF_NAMESPACE + "type");
			Resource r = model.getResource(WeatherReport.NAMESPACE + clazz);
			StmtIterator it = model.listStatements(null, p, r);
			
			while(it.hasNext()) {
				System.out.println(" - " + PrintUtil.print(it.nextStatement()));
			}
			System.out.println();
		}
	}
}
