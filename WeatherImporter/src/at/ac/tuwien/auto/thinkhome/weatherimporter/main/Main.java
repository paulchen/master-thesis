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

// TODO test model (and WeatherImporter?) with jUnit?
/**
 * This is the Main class of <em>Weather Importer</em>. It contains the
 * {@link #main(String[])} method. Purpose of this class is to read command line
 * arguments and settings from the properties file. Based on this input, other
 * classes will be instantiated and their methods will be called in order to
 * perform the requested task.
 * <br><br>
 * Please refer to the properties file for documentation about its settings.
 * 
 * @author Paul Staroch
 */
public class Main {
	/**
	 * name of the properties file
	 */
	private static final String PROPERTIES_FILE = "WeatherImporter.properties";

	/**
	 * Log4j object
	 */
	private static Logger log;

	/**
	 * The program's main method. It will read the command line arguments and
	 * settings from the properties file. Based on this input, other classes
	 * will be instantiated and their methods will be called in order to perform
	 * the requested task.
	 * <br><br>
	 * Any error lead to an exit code > 0.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		log = Logger.getLogger(Main.class);

		if (args.length != 1) {
			log.error("Invalid number of arguments.");
			System.exit(1);
		}

		// TODO document properties file
		/* read properties file */
		WeatherImporterProperties properties = new WeatherImporterProperties();
		String inputFilename = null;
		String outputFilename = null;
		try {
			properties.load(PROPERTIES_FILE);
			inputFilename = properties.getString("input_file");
			outputFilename = properties.getString("output_file");
		} catch (WeatherImporterException e) {
			log.error(e.getMessage());
			System.exit(1);
		}

		/* turtle mode */
		if (args[0].equals("turtle")) {
			/* fetch data from weather service */
			Weather weather = fetchData(properties);

			/* obtain Turtle statements and convert them to a String */
			String output = weather.getTurtleStatements().printAll();

			/* finally, write them to the output file */
			try {
				FileWriter writer = new FileWriter(
						properties.getString("turtle_file"));
				writer.write(output);
				writer.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} catch (WeatherImporterException e) {
				System.err.println(e.getMessage());
			}
			return;
		}

		/* read input file using Jena */
		OntModel onto = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
		// ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		RDFReader arp = onto.getReader("RDF/XML");
		arp.setProperty("embedding", "true");
		try {
			arp.read(onto, "file:" + inputFilename);
		} catch (DoesNotExistException e) {
			log.error("File not found: " + inputFilename);
			System.exit(1);
		}

		if (args[0].equals("fetch")) {
			/* fetch mode: fetch weather data ... */
			Weather weather = fetchData(properties);

			/* and add them to the ontology */
			weather.createIndividuals(onto);
		} else if (args[0].equals("timestamps")) {
			// TODO
		} else if (args[0].equals("remove")) {
			// TODO
		} else {
			log.error("Invalid action: " + args[0]);
			System.exit(1);
		}

		/* finally, write the resulting ontology to disk */
		RDFWriter writer = onto.getWriter("RDF/XML");
		try {
			writer.write(onto, new FileWriter(outputFilename), null);
		} catch (IOException e) {
			log.error(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Fetches weather data from a service. The class specified in the
	 * properties file will be instantiated and its method
	 * {@link Importer#fetchWeather(GeographicalPosition, WeatherImporterProperties, List)}
	 * will be called.
	 * 
	 * @param properties
	 *            settings that have been read from the properties file
	 * @return an object containing all the data that has been fetched
	 */
	private static Weather fetchData(WeatherImporterProperties properties) {
		try {
			/* position for where to fetch the weather data */
			float latitude = properties.getFloat("latitude");
			float longitude = properties.getFloat("longitude");
			int altitude = properties.getInt("altitude");
			GeographicalPosition position = new GeographicalPosition(latitude,
					longitude, altitude);

			/* For which hours shall WeatherReport objects be generated? */
			List<Integer> forecastHours = properties
					.getIntArray("forecast_hours");

			/* instantiate the class specified in the properties file */
			String importerClassName = properties.getString("importer_class");
			Class<?> importerClass = Class.forName(importerClassName);
			Importer importer = (Importer) importerClass.newInstance();

			/* perform the actual fetching and return the result */
			return importer.fetchWeather(position, properties, forecastHours);
		} catch (WeatherImporterException e) {
			log.error(e.getMessage());
			System.exit(1);
		} catch (ClassNotFoundException e) {
			log.error("Importer class not found.");
			System.exit(1);
		} catch (InstantiationException e) {
			log.error("Importer class cannot be instantiated.");
			System.exit(1);
		} catch (IllegalAccessException e) {
			log.error("Importer class cannot be instantiated.");
			System.exit(1);
		}

		/* just to make javac stop complaining about missing "return" statement */
		return null;
	}
}
