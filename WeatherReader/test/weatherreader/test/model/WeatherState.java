package weatherreader.test.model;

import java.util.ArrayList;
import java.util.List;

import weatherreader.model.WeatherReport;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

//TODO javadoc
public class WeatherState {
	private String name;
	private List<WeatherPhenomenon> phenomena;
	private Individual individual;

	public WeatherState(String name) {
		this.name = name;
		phenomena = new ArrayList<WeatherPhenomenon>();
	}

	public void addPhenomenon(WeatherPhenomenon phenomenon) {
		phenomena.add(phenomenon);
	}

	public void createIndividuals(OntModel onto) {
		OntClass weatherStateClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherState");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherStateClass);
		
		for(WeatherPhenomenon phenomenon : phenomena) {
			phenomenon.createIndividuals(onto);
			onto.add(onto.createStatement(phenomenon.getOntIndividual(), onto.getProperty(WeatherReport.NAMESPACE + "belongsToWeatherState"), individual));
		}
	}

	public Individual getOntIndividual() {
		return individual;
	}
}
