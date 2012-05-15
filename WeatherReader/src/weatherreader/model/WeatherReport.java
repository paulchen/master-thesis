package weatherreader.model;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class WeatherReport {
	private Date observationTime;
	private GeographicalPosition position;
	private WeatherState weatherState;
	private int priority;
	private String source;
	private float startTime;
	private float endTime;
	
	public static final String NAMESPACE = "http://www.semanticweb.org/ontologies/2011/9/ThinkHomeWeather.owl#";
	public static final String WGS84 = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String TIME = "http://www.w3.org/2006/time#";
	public static final String MUO_NAMESPACE = "http://purl.oclc.org/NET/muo/muo#";
	
	public WeatherReport(Date observationTime, Date startTime, Date endTime,
			int priority, String source, GeographicalPosition position,
			WeatherState weatherState) {
		long startSeconds = startTime.getTime()-new Date().getTime();
		float startHours = (float)Math.round(startSeconds/3600000);
		if(startHours < 0) {
			/* ignore data for the past */
			startHours = 0;
		}
		long endSeconds = endTime.getTime()-new Date().getTime();
		float endHours = (float)Math.round(endSeconds/3600000);
		if(endHours < 0) {
			/* ignore data for the past */
			endHours = 0;
		}
		
		initReport(observationTime, startHours, endHours, priority, source, position, weatherState);
	}

	protected WeatherReport(Date observationTime, float startTime, float endTime, 
			int priority, String source, GeographicalPosition position,
			WeatherState weatherState) {
		initReport(observationTime, startTime, endTime, priority, source, position, weatherState);
	}
	
	private void initReport(Date observationTime, float startTime, float endTime, 
			int priority, String source, GeographicalPosition position,
			WeatherState weatherState) {
		this.observationTime = observationTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.priority = priority;
		this.source = source;
		this.position = position;
		this.weatherState = weatherState;
	}
	
	public void createIndividuals(OntModel onto, int reportIndex, WeatherReport previousReport) {
		OntClass weatherReportClass = onto.getOntClass(NAMESPACE + "WeatherReport");
		Individual weatherReport = onto.createIndividual(NAMESPACE + "weatherReport" + reportIndex, weatherReportClass);
		
		OntClass sensorSourceClass = onto.getOntClass(WeatherReport.NAMESPACE + "ServiceSource");
		Individual sourceIndividual = onto.createIndividual(WeatherReport.NAMESPACE + source, sensorSourceClass);

		onto.add(onto.createStatement(weatherReport, onto.getProperty(WeatherReport.NAMESPACE + "hasSource"), sourceIndividual));
		onto.add(onto.createLiteralStatement(weatherReport, onto.getProperty(WeatherReport.NAMESPACE + "hasPriority"), priority));
		
		OntClass hourClass = onto.getOntClass(WeatherReport.NAMESPACE + "Hour");
		Individual hour1 = onto.createIndividual(WeatherReport.NAMESPACE + "hour" + startTime, hourClass);
		onto.add(onto.createLiteralStatement(hour1, onto.getProperty(WeatherReport.TIME + "hours"), new BigDecimal(startTime)));
		Individual hour2 = onto.createIndividual(WeatherReport.NAMESPACE + "hour" + startTime, hourClass);
		onto.add(onto.createLiteralStatement(hour2, onto.getProperty(WeatherReport.TIME + "hours"), new BigDecimal(startTime)));
		
		Resource intervalClass = onto.getResource(WeatherReport.TIME + "Interval");
		Individual interval1 = onto.createIndividual(WeatherReport.NAMESPACE + "interval" + startTime, intervalClass);
		onto.add(onto.createStatement(interval1, onto.getProperty(WeatherReport.TIME + "hasDurationDescription"), hour1));
		onto.add(onto.createStatement(weatherReport, onto.getProperty(WeatherReport.NAMESPACE + "hasStartTime"), interval1));

		Individual interval2 = onto.createIndividual(WeatherReport.NAMESPACE + "interval" + endTime, intervalClass);
		onto.add(onto.createStatement(interval2, onto.getProperty(WeatherReport.TIME + "hasDurationDescription"), hour2));
		onto.add(onto.createStatement(weatherReport, onto.getProperty(WeatherReport.NAMESPACE + "hasEndTime"), interval2));

		Resource pointClass = onto.getResource(WGS84 + "Point");
		
		Individual point = onto.createIndividual(NAMESPACE + "point0", pointClass);
		onto.add(onto.createStatement(weatherReport, onto.getProperty(WGS84 + "location"), point));
		onto.add(onto.createStatement(point, RDF.type, pointClass));		
		
		onto.add(onto.createLiteralStatement(point, onto.getProperty(WGS84 + "lat"), position.getLatitude()));
		onto.add(onto.createLiteralStatement(point, onto.getProperty(WGS84 + "long"), position.getLongitude()));
		onto.add(onto.createLiteralStatement(point, onto.getProperty(WGS84 + "alt"), position.getAltitude()));
		
		Resource instantClass = onto.getResource(TIME + "Instant");
		Individual instant = onto.createIndividual(NAMESPACE + "instant0", instantClass);

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(observationTime);
		Resource dateTimeClass = onto.getResource(TIME + "DateTimeDescription");
		Individual dateTime = onto.createIndividual(NAMESPACE + "dateTime0", dateTimeClass);
		
		onto.add(onto.createStatement(dateTime, onto.getProperty(TIME + "unitType"), onto.getResource(TIME + "unitMinute")));
		onto.add(onto.createLiteralStatement(dateTime, onto.getProperty(TIME + "minute"), new BigDecimal(calendar.get(Calendar.MINUTE))));
		onto.add(onto.createLiteralStatement(dateTime, onto.getProperty(TIME + "hour"), new BigDecimal(calendar.get(Calendar.HOUR_OF_DAY))));
		
		String dayString = "---";
		if(calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			dayString += "0";
		}
		dayString += calendar.get(Calendar.DAY_OF_MONTH);
		onto.add(onto.createStatement(dateTime, onto.getProperty(TIME + "day"), onto.createTypedLiteral(dayString, XSDDatatype.XSDgDay)));
		
		String monthString = "--";
		if(calendar.get(Calendar.MONTH) < 10) {
			monthString += "0";
		}
		monthString += calendar.get(Calendar.MONTH);
		onto.add(onto.createStatement(dateTime, onto.getProperty(TIME + "month"), onto.createTypedLiteral(monthString, XSDDatatype.XSDgMonth)));
		
		onto.add(onto.createStatement(dateTime, onto.getProperty(TIME + "year"), onto.createTypedLiteral(String.valueOf(calendar.get(Calendar.YEAR)), XSDDatatype.XSDgYear)));
		
		onto.add(onto.createStatement(instant, onto.getProperty(TIME + "inDateTime"), dateTime));
		
		onto.add(onto.createLiteralStatement(weatherReport, onto.getProperty(NAMESPACE + "hasObservationTime"), instant));

		if(previousReport != null) {
			weatherState.createIndividuals(onto, weatherReport, reportIndex, previousReport.getState());
		}
		else {
			weatherState.createIndividuals(onto, weatherReport, reportIndex, null);
		}
	}	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("[");
		buffer.append("startTime=" + startTime + "; ");
		buffer.append("endTime=" + endTime + "; ");
		buffer.append("source=" + source + "; ");
		buffer.append("priority=" + priority + "; ");
		buffer.append("weatherState=" + weatherState.toString());
		buffer.append("]");
		
		return buffer.toString();
	}

	public WeatherState getState() {
		return weatherState;
	}

	public float getStartTime() {
		return startTime;
	}

	public float getEndTime() {
		return endTime;
	}

	public Date getObservationTime() {
		return observationTime;
	}

	public GeographicalPosition getPosition() {
		return position;
	}

	public void setObservationTime(Date observationTime) {
		this.observationTime = observationTime;
	}

	protected int getPriority() {
		return priority;
	}

	protected String getSource() {
		return source;
	}

	protected void setPriority(int priority) {
		this.priority = priority;
	}

	protected void setSource(String source) {
		this.source = source;
	}
	
	protected void setPosition(GeographicalPosition position) {
		this.position = position;
	}

	protected void setEndTime(float endTime) {
		this.endTime = endTime;
	}
}
