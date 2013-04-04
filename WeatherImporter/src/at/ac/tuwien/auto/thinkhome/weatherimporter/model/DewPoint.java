package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

// TODO javadoc
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
		OntClass weatherPhenomenonClass = onto.getOntClass(Weather.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(Weather.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasDewPointValue"), roundFloat(dewPointValue, Weather.DECIMALS)));
	}
	
	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		/* TODO
		String blankNode = Weather.generateBlankNode();
		
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "numericalValue", String.valueOf(coverage)));
		turtle.add(new TurtleStatement(blankNode1, Weather.MUO_PREFIX + "measuredIn", Weather.NAMESPACE_PREFIX + "okta"));
		*/
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", Weather.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasDewPointValue", "\"" + String.valueOf(roundFloat(dewPointValue, Weather.DECIMALS) + "\"^^xsd:float")));
		
		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "dewPoint=" + roundFloat(dewPointValue, Weather.DECIMALS);
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
		DewPoint dewPoint = new DewPoint("dewPoint" + name, 0f);
		dewPoint.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
		return dewPoint;
	}

	@Override
	public Object clone() {
		return new DewPoint(name, dewPointValue);
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
