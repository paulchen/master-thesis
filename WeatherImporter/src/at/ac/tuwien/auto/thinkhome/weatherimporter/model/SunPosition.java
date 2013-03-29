package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import at.ac.tuwien.auto.thinkhome.weatherimporter.turtle.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.turtle.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
public class SunPosition extends WeatherPhenomenon {
	private double azimuth;
	private double zenith;
	private double elevation;
	private String name;
	private Individual individual;
	
	public SunPosition(String name, double zenith, double azimuth) {
		this.name = name;
		this.zenith = zenith;
		this.elevation = 90 - this.zenith;
		this.azimuth = azimuth;
	}

	public SunPosition(double zenith, double azimuth) {
		this("sunPosition", zenith, azimuth);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}

	public double getElevation() {
		return elevation;
	}

	public double getZenith() {
		return zenith;
	}

	public void setZenith(double zenith) {
		this.zenith = zenith;
		this.elevation = 90 - zenith;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
		this.zenith = 90 - elevation;
	}
	
	public String toString() {
		String output = "sunPosition=[";
		
		output += "azimuth=" + roundDouble(azimuth, WeatherConstants.DECIMALS) + "; ";
		output += "zenith=" + roundDouble(zenith, WeatherConstants.DECIMALS) + "; ";
		output += "elevation=" + roundDouble(elevation, WeatherConstants.DECIMALS);
		output += "]";
		
		return output;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode1 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "numericalValue"), (int)roundDouble(azimuth, WeatherConstants.DECIMALS)));
		onto.add(onto.createStatement(blankNode1, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherConstants.MUO_NAMESPACE + "degree")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "numericalValue"), (float)roundDouble(elevation, WeatherConstants.DECIMALS)));
		onto.add(onto.createStatement(blankNode2, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherConstants.MUO_NAMESPACE + "degree")));
		
 		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherPhenomenon");
 		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherPhenomenonClass);
 		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasSunDirection"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasSunElevationAngle"), blankNode2));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		String blankNode1 = Weather.generateBlankNode();
		String blankNode2 = Weather.generateBlankNode();
		
		turtle.add(new TurtleStatement(blankNode1, WeatherConstants.MUO_PREFIX + "numericalValue", String.valueOf((int)roundDouble(azimuth, WeatherConstants.DECIMALS))));
		turtle.add(new TurtleStatement(blankNode1, WeatherConstants.MUO_PREFIX + "measuredIn", WeatherConstants.MUO_PREFIX + "degree"));
		
		turtle.add(new TurtleStatement(blankNode2, WeatherConstants.MUO_PREFIX + "numericalValue", String.valueOf((float)roundDouble(elevation, WeatherConstants.DECIMALS)) + "^^xsd:float"));
		turtle.add(new TurtleStatement(blankNode2, WeatherConstants.MUO_PREFIX + "measuredIn", WeatherConstants.MUO_PREFIX + "degree"));
		
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + name, "a", WeatherConstants.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + name, WeatherConstants.NAMESPACE_PREFIX + "hasSunDirection", blankNode1));
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + name, WeatherConstants.NAMESPACE_PREFIX + "hasSunElevationAngle", blankNode2));
		
		return turtle;
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		zenith = linearDoubleInterpolation(((SunPosition)intervalStartPhenomenon).getZenith(), ((SunPosition)intervalEndPhenomenon).getZenith(), end, current);
		elevation = linearDoubleInterpolation(((SunPosition)intervalStartPhenomenon).getElevation(), ((SunPosition)intervalEndPhenomenon).getElevation(), end, current);
		azimuth = 90 - elevation;
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		SunPosition sunPosition = new SunPosition("sunPosition" + name, 0f, 0f);
		sunPosition.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return sunPosition;
	}

	@Override
	public Object clone() {
		return new SunPosition(name, zenith, azimuth);
	}
}
