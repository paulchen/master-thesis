package weatherreader.model;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class SunPosition extends WeatherPhenomenon {
	private double azimuth;
	private double zenith;
	private double elevation;
	private String name;
	private Individual individual;
	
	public SunPosition(String name, double zenith, double azimuth) {
		this.name = name;
		this.zenith = zenith*100;
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
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);

		Resource blankNode1 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), (int)roundDouble(azimuth, WeatherConstants.DECIMALS)));
		// TODO get rid of magic constant for individual name here
		onto.add(onto.createStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree")));
		
		Resource blankNode2 = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), (float)roundDouble(elevation, WeatherConstants.DECIMALS)));
		// TODO get rid of magic constant for individual name here
		onto.add(onto.createStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree")));
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasSunDirection"), blankNode1));
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasSunElevationAngle"), blankNode2));
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
		SunPosition sunPosition = new SunPosition(name, 0f, 0f);
		sunPosition.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return sunPosition;
	}

	@Override
	public Object clone() {
		return new SunPosition(name, zenith, azimuth);
	}
}
