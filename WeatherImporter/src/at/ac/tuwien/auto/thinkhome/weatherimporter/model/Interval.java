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

/**
 * This class represents an interval given by its length in hours.
 * 
 * @author Paul Staroch
 */
public class Interval extends TemporalEntity {
	/**
	 * Unique name of the individual that corresponds to this object in the
	 * ontology
	 */
	private String name;

	/**
	 * The length of the time interval represented by this instance (in hours)
	 */
	private float time;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains
	 * the main individual in the ontology that has been created by that method
	 * call.
	 */
	private Individual individual;

	/**
	 * A map for instances of this class. This is required to ensure that only
	 * one instance of this class is created for each interval.
	 */
	private static Map<Float, Interval> intervals;

	/**
	 * Retrieves an instance of <tt>Interval</tt> for the given period of time.
	 * The method either returns an instance that has already been created
	 * previously or creates a new instance of no instance for the given period
	 * has previously been created.
	 * 
	 * @param time
	 *            period of time for which to return an instance of
	 *            <tt>Interval</tt>
	 * @return an instance of <tt>Interval</tt> for the date specified
	 */
	public static Interval getInterval(float time) {
		if (intervals == null) {
			intervals = new HashMap<Float, Interval>();
		}
		if (!intervals.containsKey(time)) {
			intervals.put(time, new Interval("interval" + time, time));
		}
		return intervals.get(time);
	}

	/**
	 * Retrieves an instance of <tt>Interval</tt> for the period of time between
	 * now and the given date. The method either returns an instance that has
	 * already been created previously or creates a new instance of no instance
	 * for the given period has previously been created.
	 * 
	 * @param time
	 *            end of the period of time for which to return an instance of
	 *            <tt>Interval</tt>
	 * @return an instance of <tt>Interval</tt> for the period specified
	 */
	public static Interval getInterval(Date time) {
		return getInterval(getTime(time));
	}

	/**
	 * Constructor that creates an instance of <tt>Interval</tt> given its
	 * unique name and the period of time it represents. This constructor is
	 * private to enforce all instances of <tt>Interval</tt> to be created by
	 * the method {@link #getInterval(float)}.
	 * 
	 * @param name
	 *            unique name which is to be used in the ontology as name for
	 *            the individual which corresponds to this instance
	 * @param time
	 *            the period of time this instance of <tt>Interval</tt>
	 *            represents
	 */
	private Interval(String name, float time) {
		super();
		this.name = name;
		this.time = time;
	}

	/**
	 * Constructor that creates an instance of <tt>Interval</tt> given its
	 * unique name and the end date of the period of time it represents. The
	 * current time is selected to be the beginning of the time period. This
	 * constructor is private to enforce all instances of <tt>Interval</tt> to
	 * be created by the method {@link #getInterval(Date)}.
	 * 
	 * @param name
	 *            unique name which is to be used in the ontology as name for
	 *            the individual which corresponds to this instance
	 * @param date
	 *            the end date of the period of time this instance of
	 *            <tt>Interval</tt> represents
	 */
	private Interval(String name, Date date) {
		super();
		this.name = name;

		time = getTime(date);
	}

	/**
	 * This method returns the number of hours between the current time and the
	 * specified date if the specified date is in the future, 0 otherwise.
	 * 
	 * @param date
	 *            an arbitrary date
	 * @return the number of seconds between the current time and the specified
	 *         date if the specified date is in the future, 0 otherwise
	 */
	private static float getTime(Date date) {
		long seconds = date.getTime() - new Date().getTime();
		float ret = (float) Math.round(seconds / 3600000);
		if (ret < 0) {
			/* ignore data for the past */
			ret = 0;
		}
		return ret;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		OntClass hourClass = onto.getOntClass(Weather.NAMESPACE + "Hour");
		Individual hour1 = onto.createIndividual(Weather.NAMESPACE + "hour"
				+ time, hourClass);
		onto.add(onto.createLiteralStatement(hour1,
				onto.getProperty(TIME + "hours"), new BigDecimal(time)));

		Resource intervalClass = onto.getResource(TIME + "Interval");
		individual = onto.createIndividual(Weather.NAMESPACE + name,
				intervalClass);

		onto.add(onto.createLiteralStatement(individual,
				onto.getProperty(TIME + "hasDurationDescription"), hour1));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();

		turtle.add(new TurtleStatement(getTurtleName(), "a", TIME_PREFIX
				+ "Interval"));
		turtle.add(new TurtleStatement(
				Weather.NAMESPACE_PREFIX + "hour" + time, "a",
				Weather.NAMESPACE_PREFIX + "Hour"));
		turtle.add(new TurtleStatement(
				Weather.NAMESPACE_PREFIX + "hour" + time,
				TIME_PREFIX + "hours", "\""
						+ String.valueOf(new BigDecimal(time))
						+ "\"^^xsd:decimal"));
		turtle.add(new TurtleStatement(getTurtleName(), TIME_PREFIX
				+ "hasDurationDescription", Weather.NAMESPACE_PREFIX + "hour"
				+ time));

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
		return Weather.NAMESPACE_PREFIX + name;
	}
}
