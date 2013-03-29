package at.ac.tuwien.auto.thinkhome.weatherimporter.model;


import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.turtle.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
public class SolarRadiation extends WeatherPhenomenon {
	private String name;
	private float radiationValue;
	private Individual individual;

	public SolarRadiation(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		radiationValue = 0f;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			radiationValue += ((SolarRadiation)phenomenon).getRadiationValue();
		}
		radiationValue /= weatherPhenomena.size();
	}
	
	public SolarRadiation(String name, float pressureValue) {
		this.name = name;
		this.radiationValue = pressureValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource blankNode = onto.createResource();
		onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "numericalValue"), roundFloat(radiationValue, WeatherConstants.DECIMALS)));
		onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherConstants.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherConstants.NAMESPACE + "wattsPerSquareMeter")));
		
 		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherPhenomenon");
 		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherPhenomenonClass);
 		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasSolarRadiationValue"), blankNode));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		// TODO
		
		return turtle;
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "solarRadiation=" + roundFloat(radiationValue, WeatherConstants.DECIMALS);
	}

	private Float getRadiationValue() {
		return radiationValue;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		radiationValue = linearFloatInterpolation(((SolarRadiation)intervalStartPhenomenon).getRadiationValue(), ((SolarRadiation)intervalEndPhenomenon).getRadiationValue(), end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		SolarRadiation pressure = new SolarRadiation("solarRadiation" + name, 0f);
		pressure.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
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
}
