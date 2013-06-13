package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
/**
 * This class represents a value for atmospheric pressure.
 * 
 * @author Paul Staroch
 */
public class Pressure extends WeatherPhenomenon {
	/**
	 * Unique name of the individual that corresponds to this object in the
	 * ontology
	 */
	private String name;

	/**
	 * The value for atmospheric pressure (in hectopascal/millibar)
	 */
	private float pressureValue;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains
	 * the main individual in the ontology that has been created by that method
	 * call.
	 */
	private Individual individual;

	/**
	 * A constructor that creates an instance of <tt>Pressure</tt> from a set of
	 * already existing instances of that class. The pressure value will be set
	 * to the arithmetic mean of the pressure values of all these instances.
	 * 
	 * @param name
	 *            the unique name of the individual in the ontology that
	 *            corresponds to this object
	 * @param weatherPhenomena
	 *            a list of instances of <tt>Pressure</tt> that should be used
	 *            to create this instance
	 */
	public Pressure(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;

		pressureValue = 0f;
		for (WeatherPhenomenon phenomenon : weatherPhenomena) {
			pressureValue += ((Pressure) phenomenon).getPressureValue();
		}
		pressureValue /= weatherPhenomena.size();
	}

	/**
	 * A constructor that creates an instance of <tt>Pressure</tt> given its
	 * unique name and its pressure value.
	 * 
	 * @param name
	 *            the unique name of the individual in the ontology that
	 *            corresponds to this object
	 * @param pressureValue
	 *            the pressure value
	 */
	public Pressure(String name, float pressureValue) {
		this.name = name;
		this.pressureValue = pressureValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode,
				onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"),
				roundFloat(pressureValue, Weather.DECIMALS)));
		onto.add(onto.createStatement(blankNode,
				onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"),
				onto.getResource(Weather.NAMESPACE + "hectopascal")));

		OntClass weatherPhenomenonClass = onto.getOntClass(Weather.NAMESPACE
				+ "WeatherPhenomenon");
		individual = onto.createIndividual(Weather.NAMESPACE + name,
				weatherPhenomenonClass);

		onto.add(onto.createStatement(individual,
				onto.getProperty(Weather.NAMESPACE + "hasPressureValue"),
				blankNode));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();

		String blankNode = Weather.generateBlankNode();

		turtle.add(new TurtleStatement(blankNode, Weather.MUO_PREFIX
				+ "numericalValue", "\""
				+ String.valueOf(roundFloat(pressureValue, Weather.DECIMALS))
				+ "\"^^xsd:float"));
		turtle.add(new TurtleStatement(blankNode, Weather.MUO_PREFIX
				+ "measuredIn", Weather.NAMESPACE_PREFIX + "hectopascal"));

		turtle.add(new TurtleStatement(getTurtleName(), "a",
				Weather.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(getTurtleName(),
				Weather.NAMESPACE_PREFIX + "hasPressureValue", blankNode));

		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "pressure=" + roundFloat(pressureValue, Weather.DECIMALS);
	}

	private Float getPressureValue() {
		return pressureValue;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end,
			int current) {
		pressureValue = linearFloatInterpolation(
				((Pressure) intervalStartPhenomenon).getPressureValue(),
				((Pressure) intervalEndPhenomenon).getPressureValue(), start,
				end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end,
			int current) {
		Pressure pressure = new Pressure("pressure" + name, 0f);
		pressure.interpolate(intervalStartPhenomenon, intervalEndPhenomenon,
				start, end, current);
		return pressure;
	}

	@Override
	public Object clone() {
		return new Pressure(name, pressureValue);
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
