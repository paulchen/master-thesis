package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * This class represents an instant.
 * 
 * @author Paul Staroch
 */
public class Instant extends TemporalEntity {
	/**
	 * Unique name of the individual that corresponds to this object in the
	 * ontology
	 */
	private String name;

	/**
	 * The point of time represented by this instance
	 */
	private Date date;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains
	 * the main individual in the ontology that has been created by that method
	 * call.
	 */
	private Individual individual;

	/**
	 * Map of instances that have already been created. This is necessary in
	 * order to create only one instance of <tt>Instant</tt> for each date.
	 */
	private static Map<Date, Instant> instants;

	/**
	 * Retrieves an instance of <tt>Instant</tt> for the given date. The method
	 * either returns an instance that has already been created previously or
	 * creates a new instance of no instance for the given date has previously
	 * been created.
	 * 
	 * @param date
	 *            date for which to return an instance of <tt>Instant</tt>
	 * @return an instance of <tt>Instant</tt> for the date specified
	 */
	public static Instant getInstant(Date date) {
		if (instants == null) {
			instants = new HashMap<Date, Instant>();
		}
		if (!instants.containsKey(date)) {
			instants.put(date, new Instant("instant" + instants.size(), date));
		}
		return instants.get(date);
	}

	/**
	 * Constructor that creates an instance of <tt>Instant</tt> given its unique
	 * name and the date it represents. This constructor is private to enforce
	 * all instances of <tt>Instant</tt> to be created by the method
	 * {@link #getInstant(Date)}.
	 * 
	 * @param name
	 *            unique name which is to be used in the ontology as name for
	 *            the individual which corresponds to this instance
	 * @param date
	 *            the date this instance of <tt>Instant</tt> represents
	 */
	private Instant(String name, Date date) {
		super();
		this.name = name;
		this.date = date;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource instantClass = onto.getResource(TIME + "Instant");
		individual = onto.createIndividual(Weather.NAMESPACE + name,
				instantClass);

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		Resource dateTimeClass = onto.getResource(TIME + "DateTimeDescription");
		Individual dateTime = onto.createIndividual(Weather.NAMESPACE
				+ "dateTime0", dateTimeClass);

		onto.add(onto.createStatement(dateTime,
				onto.getProperty(TIME + "unitType"),
				onto.getResource(TIME + "unitMinute")));
		onto.add(onto.createLiteralStatement(dateTime,
				onto.getProperty(TIME + "minute"),
				new BigDecimal(calendar.get(Calendar.MINUTE))));
		onto.add(onto.createLiteralStatement(dateTime,
				onto.getProperty(TIME + "hour"),
				new BigDecimal(calendar.get(Calendar.HOUR_OF_DAY))));

		String dayString = "---";
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			dayString += "0";
		}
		dayString += calendar.get(Calendar.DAY_OF_MONTH);
		onto.add(onto.createStatement(dateTime, onto.getProperty(TIME + "day"),
				onto.createTypedLiteral(dayString, XSDDatatype.XSDgDay)));

		String monthString = "--";
		if (calendar.get(Calendar.MONTH) < 9) {
			monthString += "0";
		}
		monthString += (calendar.get(Calendar.MONTH) + 1);
		onto.add(onto.createStatement(dateTime,
				onto.getProperty(TIME + "month"),
				onto.createTypedLiteral(monthString, XSDDatatype.XSDgMonth)));

		onto.add(onto.createStatement(dateTime,
				onto.getProperty(TIME + "year"), onto.createTypedLiteral(
						String.valueOf(calendar.get(Calendar.YEAR)),
						XSDDatatype.XSDgYear)));

		onto.add(onto.createStatement(individual,
				onto.getProperty(TIME + "inDateTime"), dateTime));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();

		turtle.add(new TurtleStatement(getTurtleName(), "a", TIME_PREFIX
				+ "Instant"));

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX + "dateTime0",
				"a", TIME_PREFIX + "DateTimeDescription"));
		turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX + "dateTime0",
				TIME_PREFIX + "unitType", TIME_PREFIX + "unitMinute"));
		turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX + "dateTime0",
				TIME_PREFIX + "minute", String.valueOf(new BigDecimal(calendar
						.get(Calendar.MINUTE)))));
		turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX + "dateTime0",
				TIME_PREFIX + "hour", String.valueOf(new BigDecimal(calendar
						.get(Calendar.HOUR_OF_DAY)))));

		String dayString = "---";
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			dayString += "0";
		}
		dayString += calendar.get(Calendar.DAY_OF_MONTH);
		turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX + "dateTime0",
				TIME_PREFIX + "day", "\"" + dayString + "\"^^xsd:gDay"));

		String monthString = "--";
		if (calendar.get(Calendar.MONTH) < 9) {
			monthString += "0";
		}
		monthString += (calendar.get(Calendar.MONTH) + 1);
		turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX + "dateTime0",
				TIME_PREFIX + "month", "\"" + monthString + "\"^^xsd:gMonth"));
		turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX + "dateTime0",
				TIME_PREFIX + "year", "\""
						+ String.valueOf(new BigDecimal(calendar
								.get(Calendar.YEAR))) + "\"^^xsd:gYear"));
		turtle.add(new TurtleStatement(getTurtleName(), TIME_PREFIX
				+ "inDateTime", Weather.NAMESPACE_PREFIX + "dateTime0"));

		return turtle;
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	@Override
	public String toString() {
		return date.toString();
	}

	@Override
	public String getTurtleName() {
		return Weather.NAMESPACE_PREFIX + name;
	}
}
