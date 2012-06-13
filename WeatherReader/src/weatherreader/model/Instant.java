package weatherreader.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class Instant extends TemporalEntity {
	private String name;
	private Date date;
	private Individual individual;
	
	public Instant(String name, Date date) {
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
		if(calendar.get(Calendar.MONTH) < 10) {
			monthString += "0";
		}
		monthString += calendar.get(Calendar.MONTH);
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherConstants.TIME + "month"), onto.createTypedLiteral(monthString, XSDDatatype.XSDgMonth)));
		
		onto.add(onto.createStatement(dateTime, onto.getProperty(WeatherConstants.TIME + "year"), onto.createTypedLiteral(String.valueOf(calendar.get(Calendar.YEAR)), XSDDatatype.XSDgYear)));
		
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.TIME + "inDateTime"), dateTime));
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}
	
	@Override
	public String toString() {
		return date.toString();
	}
}
