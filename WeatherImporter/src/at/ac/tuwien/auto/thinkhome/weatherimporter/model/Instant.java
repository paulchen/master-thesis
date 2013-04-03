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

// TODO javadoc
public class Instant extends TemporalEntity {
	private String name;
	private Date date;
	private Individual individual;
	
	private static Map<Date, Instant> instants;
	
	public static Instant getInstant(Date date) {
		if(instants == null) {
			instants = new HashMap<Date, Instant>();
		}
		if(!instants.containsKey(date)) {
			instants.put(date, new Instant("instant" + instants.size(), date));
		}
		return instants.get(date);
	}
	
	private Instant(String name, Date date) {
		super();
		this.name = name;
		this.date = date;
	}

	@Override
	public void createIndividuals(OntModel onto) {
		Resource instantClass = onto.getResource(WeatherConstants.TIME + "Instant");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, instantClass);

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		Resource dateTimeClass = onto.getResource(WeatherConstants.TIME + "DateTimeDescription");
		// TODO document why this is unique
		Individual dateTime = onto.createIndividual(WeatherConstants.NAMESPACE + "dateTime0", dateTimeClass);
		
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherConstants.TIME + "unitType"), onto.getResource(WeatherConstants.TIME + "unitMinute")));
		onto.add(onto.createLiteralStatement(dateTime, onto.getProperty(WeatherConstants.TIME + "minute"), new BigDecimal(calendar.get(Calendar.MINUTE))));
		onto.add(onto.createLiteralStatement(dateTime, onto.getProperty(WeatherConstants.TIME + "hour"), new BigDecimal(calendar.get(Calendar.HOUR_OF_DAY))));
		
		String dayString = "---";
		if(calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			dayString += "0";
		}
		dayString += calendar.get(Calendar.DAY_OF_MONTH);
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherConstants.TIME + "day"), onto.createTypedLiteral(dayString, XSDDatatype.XSDgDay)));
		
		String monthString = "--";
		if(calendar.get(Calendar.MONTH) < 9) {
			monthString += "0";
		}
		monthString += (calendar.get(Calendar.MONTH)+1);
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherConstants.TIME + "month"), onto.createTypedLiteral(monthString, XSDDatatype.XSDgMonth)));
		
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherConstants.TIME + "year"), onto.createTypedLiteral(String.valueOf(calendar.get(Calendar.YEAR)), XSDDatatype.XSDgYear)));
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.TIME + "inDateTime"), dateTime));
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", WeatherConstants.TIME_PREFIX + "Instant"));
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		// TODO document why this is unique
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + "dateTime0", "a", WeatherConstants.TIME_PREFIX + "DateTimeDescription"));
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + "dateTime0", WeatherConstants.TIME_PREFIX + "unitType", WeatherConstants.TIME_PREFIX + "unitMinute"));
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + "dateTime0", WeatherConstants.TIME_PREFIX + "minute", String.valueOf(new BigDecimal(calendar.get(Calendar.MINUTE)))));
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + "dateTime0", WeatherConstants.TIME_PREFIX + "hour", String.valueOf(new BigDecimal(calendar.get(Calendar.HOUR_OF_DAY)))));
		
		String dayString = "---";
		if(calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			dayString += "0";
		}
		dayString += calendar.get(Calendar.DAY_OF_MONTH);
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + "dateTime0", WeatherConstants.TIME_PREFIX + "day", "\"" + dayString + "\"^^xsd:gDay"));
		
		String monthString = "--";
		if(calendar.get(Calendar.MONTH) < 9) {
			monthString += "0";
		}
		monthString += (calendar.get(Calendar.MONTH)+1);
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + "dateTime0", WeatherConstants.TIME_PREFIX + "month", "\"" + monthString + "\"^^xsd:gMonth"));
		turtle.add(new TurtleStatement(WeatherConstants.NAMESPACE_PREFIX + "dateTime0", WeatherConstants.TIME_PREFIX + "year", "\"" + String.valueOf(new BigDecimal(calendar.get(Calendar.YEAR))) + "\"^^xsd:gYear"));
		turtle.add(new TurtleStatement(getTurtleName(), WeatherConstants.TIME_PREFIX + "inDateTime", WeatherConstants.NAMESPACE_PREFIX + "dateTime0"));
		
		return turtle;
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}
	
	@Override
	public String toString() {
		return date.toString();
	}

	@Override
	public String getTurtleName() {
		return WeatherConstants.NAMESPACE_PREFIX + name;
	}
}
