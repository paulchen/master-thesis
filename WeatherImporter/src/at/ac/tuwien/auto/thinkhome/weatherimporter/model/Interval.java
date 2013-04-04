package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

// TODO javadoc
public class Interval extends TemporalEntity {
	private String name;
	private float time;
	private Individual individual;
	
	private static Map<Float, Interval> intervals;
	
	public static Interval getInterval(float time) {
		if(intervals == null) {
			intervals = new HashMap<Float, Interval>();
		}
		if(!intervals.containsKey(time)) {
			intervals.put(time, new Interval("interval" + time, time));
		}
		return intervals.get(time);
	}
	
	public static Interval getInterval(Date time) {
		return getInterval(getTime(time));
	}
	
	public Interval(String name, float time) {
		super();
		this.name = name;
		this.time = time;
	}

	public Interval(String name, Date date) {
		super();
		this.name = name;
		
		time = getTime(date);
	}

	private static float getTime(Date date) {
		long seconds = date.getTime()-new Date().getTime();
		float ret = (float)Math.round(seconds/3600000);
		if(ret < 0) {
			/* ignore data for the past */
			ret = 0;
		}
		return ret;
	}
	
	@Override
	public void createIndividuals(OntModel onto) {
		OntClass hourClass = onto.getOntClass(WeatherConstants.NAMESPACE + "Hour");
		Individual hour1 = onto.createIndividual(WeatherConstants.NAMESPACE + "hour" + time, hourClass);
		onto.add(onto.createLiteralStatement(hour1, onto.getProperty(WeatherConstants.TIME + "hours"), new BigDecimal(time)));
		
		Resource intervalClass = onto.getResource(WeatherConstants.TIME + "Interval");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, intervalClass);
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.TIME + "hasDurationDescription"), hour1));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", WeatherConstants.TIME_PREFIX + "Interval"));
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + "hour" + time, "a", WeatherConstants.NAMESPACE_PREFIX + "Hour"));
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + "hour" + time, WeatherConstants.TIME_PREFIX + "hours", "\"" + String.valueOf(new BigDecimal(time)) + "\"^^xsd:decimal"));
		turtle.add(new TurtleStatement(getTurtleName(), WeatherConstants.TIME_PREFIX + "hasDurationDescription", WeatherConstants.NAMESPACE_PREFIX + "hour" + time));
		
		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	public float getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return String.valueOf(time);
	}

	@Override
	public String getTurtleName() {
		return WeatherConstants.NAMESPACE_PREFIX + name;
	}
}
