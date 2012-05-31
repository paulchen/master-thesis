package weatherreader.model;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class DewPoint extends WeatherPhenomenon {
	private String name;
	private Individual individual;
	private float dewPointValue;
	
	public DewPoint(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		dewPointValue = 0;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			dewPointValue += ((DewPoint)phenomenon).getDewPointValue();
		}
		dewPointValue /= weatherPhenomena.size();
	}
	
	public DewPoint(String name, float dewPointValue) {
		super();
		this.name = name;
		this.dewPointValue = dewPointValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), dewPointValue));
		// TODO get rid of magic constant for individual name here
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/temperature/degree-Celsius")));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasDewPointValue"), blankNode));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "dewPoint=" + dewPointValue;
	}

	private float getDewPointValue() {
		return dewPointValue;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		dewPointValue = linearFloatInterpolation(((DewPoint)intervalStartPhenomenon).getDewPointValue(), ((DewPoint)intervalEndPhenomenon).getDewPointValue(), end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		DewPoint dewPoint = new DewPoint(name, 0f);
		dewPoint.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return dewPoint;
	}
}
