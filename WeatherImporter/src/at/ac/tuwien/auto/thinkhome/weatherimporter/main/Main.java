package at.ac.tuwien.auto.thinkhome.weatherimporter.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import at.ac.tuwien.auto.thinkhome.weatherimporter.model.GeographicalPosition;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Weather;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.shared.DoesNotExistException;

// TODO javadoc
// TODO test model (and WeatherImporter?) with jUnit?
public class Main {
	private static final String PROPERTIES_FILE = "WeatherImporter.properties";
	private static Logger log;
	
	public static void main(String[] args) {
		log = Logger.getLogger(Main.class);
		
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

		if(args[0].equals("turtle")) {
			Weather weather = fetchData(properties);
			TurtleStore turtle = weather.getTurtleStatements();
			String output = turtle.printAll();
			try {
				FileWriter writer = new FileWriter(properties.getString("turtle_file"));
				writer.write(output);
				writer.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (WeatherImporterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
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
		
		if(args.length != 1) {
			log.error("Invalid number of arguments.");
			System.exit(1);
		}
		
		if(args[0].equals("fetch")) {
			Weather weather = fetchData(properties);
			weather.createIndividuals(onto);
		}
		else if(args[0].equals("timestamps")) {
			// TODO
		}
		else if(args[0].equals("remove")) {
			// TODO
		}
		else {
			log.error("Invalid action: " + args[0]);
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
	
	private static Weather fetchData(WeatherImporterProperties properties) {
		try {
			float latitude = properties.getFloat("latitude");
			float longitude = properties.getFloat("longitude");
			int altitude = properties.getInt("altitude");
			
			List<Integer> forecastHours = properties.getIntArray("forecast_hours");
	
			String importerClassName = properties.getString("importer_class");
			Class<?> importerClass = Class.forName(importerClassName);
			Importer importer = (Importer)importerClass.newInstance();
			
			GeographicalPosition position = new GeographicalPosition(latitude, longitude, altitude);
			
			return importer.fetchWeather(position, properties, forecastHours);
		}
		catch (WeatherImporterException e) {
			log.error(e.getMessage());
			System.exit(1);
		}
		catch (ClassNotFoundException e) {
			log.error("Importer class not found.");
			System.exit(1);
		}
		catch (InstantiationException e) {
			log.error("Importer class cannot be instantiated.");
			System.exit(1);
		}
		catch (IllegalAccessException e) {
			log.error("Importer class cannot be instantiated.");
			System.exit(1);
		}
		
		// just to make javac stop complaining about missing "return" statement
		return null;
	}
}
