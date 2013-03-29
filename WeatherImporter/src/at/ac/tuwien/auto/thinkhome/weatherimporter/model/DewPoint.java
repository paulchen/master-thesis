package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.util.List;

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
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherPhenomenon");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherPhenomenonClass);
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasDewPointValue"), roundFloat(dewPointValue, WeatherConstants.DECIMALS)));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return "dewPoint=" + roundFloat(dewPointValue, WeatherConstants.DECIMALS);
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
}
