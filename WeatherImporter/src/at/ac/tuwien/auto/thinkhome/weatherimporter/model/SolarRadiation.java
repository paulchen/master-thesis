package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * This class represents a value for solar radiation.
 * 
 * @author Paul Staroch
 */
public class SolarRadiation extends WeatherPhenomenon {
	/**
	 * Unique name of the individual that corresponds to this object in the
	 * ontology
	 */
	private String name;

	/**
	 * The value for solar radiation (in Watts per square metre)
	 */
	private float radiationValue;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains
	 * the main individual in the ontology that has been created by that method
	 * call.
	 */
	private Individual individual;

	/**
	 * A constructor that creates an instance of <tt>SolarRadiation</tt> from a
	 * set of already existing instances of that class. The radiation value will
	 * be set to the arithmetic mean of the radiation values of all these
	 * instances.
	 * 
	 * @param name
	 *            the unique name of the individual in the ontology that
	 *            corresponds to this object
	 * @param weatherPhenomena
	 *            a list of instances of <tt>SolarRadiation</tt> that should be
	 *            used to create this instance
	 */
	public SolarRadiation(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;

		radiationValue = 0f;
		for (WeatherPhenomenon phenomenon : weatherPhenomena) {
			radiationValue += ((SolarRadiation) phenomenon).getRadiationValue();
		}
		radiationValue /= weatherPhenomena.size();
	}

	/**
	 * A constructor that creates an instance of <tt>SolarRadiation</tt> given
	 * its unique name and its radiation value.
	 * 
	 * @param name
	 *            the unique name of the individual in the ontology that
	 *            corresponds to this object
	 * @param pressureValue
	 *            the radiation value
	 */
	public SolarRadiation(String name, float pressureValue) {
		this.name = name;
		this.radiationValue = pressureValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode,
				onto.getProperty(Weather.MUO_NAMESPACE + "numericalValue"),
				roundFloat(radiationValue, Weather.DECIMALS)));
		onto.add(onto.createStatement(blankNode,
				onto.getProperty(Weather.MUO_NAMESPACE + "measuredIn"),
				onto.getResource(Weather.NAMESPACE + "wattsPerSquareMeter")));

		OntClass weatherPhenomenonClass = onto.getOntClass(Weather.NAMESPACE
				+ "WeatherPhenomenon");
		individual = onto.createIndividual(Weather.NAMESPACE + name,
				weatherPhenomenonClass);

		onto.add(onto.createStatement(individual,
				onto.getProperty(Weather.NAMESPACE + "hasSolarRadiationValue"),
				blankNode));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();

		String blankNode = Weather.generateBlankNode();

		turtle.add(new TurtleStatement(blankNode, Weather.MUO_PREFIX
				+ "numericalValue", "\""
				+ String.valueOf(roundFloat(radiationValue, Weather.DECIMALS))
				+ "\"^^xsd:float"));
		turtle.add(new TurtleStatement(blankNode, Weather.MUO_PREFIX
				+ "measuredIn", Weather.NAMESPACE_PREFIX
				+ "wattsPerSquareMeter"));

		turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX + name, "a",
				Weather.NAMESPACE_PREFIX + "WeatherPhenomenon"));
		turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX + name,
				Weather.NAMESPACE_PREFIX + "hasSolarRadiationValue", blankNode));

		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "solarRadiation=" + roundFloat(radiationValue, Weather.DECIMALS);
	}

	private Float getRadiationValue() {
		return radiationValue;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end,
			int current) {
		radiationValue = linearFloatInterpolation(
				((SolarRadiation) intervalStartPhenomenon).getRadiationValue(),
				((SolarRadiation) intervalEndPhenomenon).getRadiationValue(),
				start, end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end,
			int current) {
		SolarRadiation pressure = new SolarRadiation("solarRadiation" + name,
				0f);
		pressure.interpolate(intervalStartPhenomenon, intervalEndPhenomenon,
				start, end, current);
		return pressure;
	}

	@Override
	public Object clone() {
		return new SolarRadiation(name, radiationValue);
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
