package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * This class represents a cloud layer.
 * 
 * @author Paul Staroch
 */
public class CloudCover extends WeatherPhenomenon {
	/**
	 * Unique name of the individual that corresponds to this object in the ontology 
	 */
	private String name;
	
	/**
	 * Altitude of the cloud layer (in metres above sea level)
	 */
	private int altitude;
	
	/**
	 * Coverage of the cloud layer (in okta, i.e. a value from 0 to 9)
	 */
	private int coverage;
	
	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains the main individual in the ontology that has been created by that method call.
	 */
	private Individual individual;

	/**
	 * A constructor that creates an instance of <tt>CloudCover</tt> from a set of already existing instances of that class. The altitude will be set to the arithmetic mean of the altitudes of all these instances and the coverage will be set to the maximum of all values for the coverage field.
	 * 
	 * @param name the unique name of the individual in the ontology that corresponds to this object
	 * @param weatherPhenomena a list of instances of <tt>CloudCover</tt> that should be used to create this instance 
	 */
	public CloudCover(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		altitude = 0;
		coverage = 0;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			altitude += ((CloudCover)phenomenon).getAltitude();
			if(coverage < ((CloudCover)phenomenon).getCoverage()) {
				coverage = ((CloudCover)phenomenon).getCoverage();
			}
		}
		altitude /= weatherPhenomena.size();
	}

	/**
	 * A constructor that creates an instance of <tt>CloudCover</tt> given its unique name, its altitude and its coverage.
	 * 
	 * @param name the unique name of the individual in the ontology that corresponds to this object
	 * @param altitude the altitude of the cloud layer
	 * @param coverage the coverage of the cloud layer
	 */
	public CloudCover(String name, int altitude, int coverage) {
		super();
		this.name = name;
		this.altitude = altitude;
		this.coverage = coverage;
	}
	
	public int getAltitude() {
		return altitude;
	}
	
	public int getCoverage() {
		return coverage;
	}
	
	@Override
	public String toString() {
		return "[altitude=" + altitude + "; coverage=" + coverage + "]";
	}
	
	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode1 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), coverage));
		onto.add(onto.createStatement(blankNode1, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.NAMESPACE + "okta")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"), altitude));
		onto.add(onto.createStatement(blankNode2, onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"), onto.getResource(Weather.MUO_NAMESPACE + "meter")));
		
 		OntClass weatherPhenomenonClass = onto.getOntClass(Weather.NAMESPACE + "WeatherPhenomenon");
 		individual = onto.createIndividual(Weather.NAMESPACE + name, weatherPhenomenonClass);
 		
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasCloudCover"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasCloudAltitude"), blankNode2));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		String blankNode1 = Weather.generateBlankNode();
		String blankNode2 = Weather.generateBlankNode();
		
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "numericalValue", String.valueOf(coverage)));
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "measuredIn", Weather.NAMESPACE_PREFIX + "okta"));
		
		turtle.add(new TurtleStatement(blankNode2, Weather.MUO_PREFIX + "numericalValue", String.valueOf(altitude)));
		turtle.add(new TurtleStatement(blankNode2, Weather.MUO_PREFIX + "measuredIn", Weather.MUO_PREFIX + "meter"));
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", Weather.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasCloudCover", blankNode1));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasCloudAltitude", blankNode2));
		
		return turtle;
	}
	
	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end, int current) {
		altitude = linearIntInterpolation(((CloudCover)intervalStartPhenomenon).getAltitude(), ((CloudCover)intervalEndPhenomenon).getAltitude(), start, end, current);
		coverage = linearIntInterpolation(((CloudCover)intervalStartPhenomenon).getCoverage(), ((CloudCover)intervalEndPhenomenon).getCoverage(), start, end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end, int current) {
		CloudCover cloudCover = new CloudCover("cloudCover" + name, 0, 0);
		cloudCover.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, start, end, current);
		return cloudCover;
	}

	@Override
	public Object clone() {
		return new CloudCover(name, altitude, coverage);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getTurtleName() {
		return Weather.NAMESPACE_PREFIX + name;
	}
}
