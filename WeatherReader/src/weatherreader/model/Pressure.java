package weatherreader.model;


import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class Pressure extends WeatherPhenomenon {
	private String name;
	private float pressureValue;
	private Individual individual;

	public Pressure(String name, List<WeatherPhenomenon> weatherPhenomena) {
		super(weatherPhenomena);
		this.name = name;
		
		pressureValue = 0f;
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			pressureValue += ((Pressure)phenomenon).getPressureValue();
		}
		pressureValue /= weatherPhenomena.size();
	}
	
	public Pressure(String name, float pressureValue) {
		this.name = name;
		this.pressureValue = pressureValue;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasPressureValue"), pressureValue));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "pressure=" + roundFloat(pressureValue, WeatherConstants.DECIMALS);
	}

	private Float getPressureValue() {
		return pressureValue;
	}

	@Override
	public void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		pressureValue = linearFloatInterpolation(((Pressure)intervalStartPhenomenon).getPressureValue(), ((Pressure)intervalEndPhenomenon).getPressureValue(), end, current);
	}

	@Override
	public WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current) {
		Pressure pressure = new Pressure("pressure" + name, 0f);
		pressure.interpolate(intervalStartPhenomenon, intervalEndPhenomenon, end, current);
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
}
