package at.ac.tuwien.auto.thinkhome.weatherimporter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.shared.DoesNotExistException;

// TODO javadoc
// TODO test model (and WeatherReader?) with jUnit?
public class Main {
	private static final String PROPERTIES_FILE = "WeatherReader.properties";
	
	public static void main(String[] args) {
		Logger log = Logger.getLogger(Main.class);
		
		// TODO document properties file
		WeatherImporterProperties properties = new WeatherImporterProperties();
		String inputFilename = null;
		String outputFilename = null;
		try {
			properties.load(PROPERTIES_FILE);
			inputFilename = properties.getString("input_file");
			outputFilename = properties.getString("output_file");
		}
		catch (WeatherImporterException e) {
			log.error(e.getMessage());
			System.exit(1);
		}

		OntModel onto = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
//		onto = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		try {
			arp.read(onto, "file:" + inputFilename);
		}
		catch(DoesNotExistException e) {
			log.error("File not found: " + inputFilename);
			System.exit(1);
		}
		
		try {
			float latitude = properties.getFloat("latitude");
			float longitude = properties.getFloat("longitude");
			int altitude = properties.getInt("altitude");
			int lowCloudAltitude = properties.getInt("low_clouds");
			int mediumCloudAltitude = properties.getInt("medium_clouds");
			int highCloudAltitude = properties.getInt("high_clouds");
			
			List<Integer> forecastHours = properties.getIntArray("forecast_hours");
	
			WeatherImporter weatherReader = new WeatherImporter(latitude, longitude, altitude, lowCloudAltitude, mediumCloudAltitude, highCloudAltitude, forecastHours);
			weatherReader.process();
			weatherReader.createIndividuals(onto);
		}
		catch (WeatherImporterException e) {
			log.error(e.getMessage());
			System.exit(1);
		}
		
		RDFWriter writer = onto.getWriter("RDF/XML");
		try {
			writer.write(onto, new FileWriter(outputFilename), null);
		}
		catch (IOException e) {
			log.error(e.getMessage());
			System.exit(1);
		}
	}
}
