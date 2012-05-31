package weatherreader.model;

import java.math.BigDecimal;
import java.util.Date;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class Interval extends TemporalEntity {
	private String name;
	private float time;
	private Individual individual;
	
	public Interval(String name, float time) {
		super();
		this.name = name;
		this.time = time;
	}

	public Interval(String name, Date date) {
		super();
		this.name = name;
		
		long seconds = date.getTime()-new Date().getTime();
		time = (float)Math.round(seconds/3600000);
		if(time < 0) {
			/* ignore data for the past */
			time = 0;
		}
	}

	@Override
	public void createIndividuals(OntModel onto) {
		OntClass hourClass = onto.getOntClass(WeatherReport.NAMESPACE + "Hour");
		Individual hour1 = onto.createIndividual(WeatherReport.NAMESPACE + "hour" + time, hourClass);
		onto.add(onto.createLiteralStatement(hour1, onto.getProperty(WeatherReport.TIME + "hours"), new BigDecimal(time)));
		
		Resource intervalClass = onto.getResource(WeatherReport.TIME + "Interval");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name + time, intervalClass);
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	public float getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return String.valueOf(time);
	}
}
