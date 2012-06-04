package weatherreader.model;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

// TODO javadoc
public class WeatherReport implements OntologyClass {
	private Instant observationTime;
	private GeographicalPosition position;
	private WeatherState weatherState;
	private int priority;
	private WeatherSource source;
	private Interval startTime;
	private Interval endTime;
	private String name;
	private Individual individual;
	private WeatherReport previousReport;
	
	// TODO move this somewhere else?
	public static final String NAMESPACE = "http://www.semanticweb.org/ontologies/2011/9/ThinkHomeWeather.owl#";
	public static final String WGS84 = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String TIME = "http://www.w3.org/2006/time#";
	public static final String MUO_NAMESPACE = "http://purl.oclc.org/NET/muo/muo#";
	
	/*
	public WeatherReport(String name, Instant observationTime, Interval startTime, Interval endTime,
			int priority, WeatherSource source, GeographicalPosition position,
			WeatherState weatherState) {
		
		/* TODO
		long startSeconds = startTime.getTime()-new Date().getTime();
		float startHours = (float)Math.round(startSeconds/3600000);
		if(startHours < 0) {
			/* ignore data for the past *
			startHours = 0;
		}
		long endSeconds = endTime.getTime()-new Date().getTime();
		float endHours = (float)Math.round(endSeconds/3600000);
		if(endHours < 0) {
			/* ignore data for the past *
			endHours = 0;
		}
		
		
		initReport(name, observationTime, startHours, endHours, priority, source, position, weatherState);
	} */

	/*
	protected WeatherReport(String name, Instant observationTime, Interval startTime, Interval endTime,
			int priority, WeatherSource source, GeographicalPosition position,
			WeatherState weatherState) {
		initReport(name, observationTime, startTime, endTime, priority, source, position, weatherState);
	}
	*/
	
	public WeatherReport(String name, Instant observationTime, Interval startTime, Interval endTime,
			int priority, WeatherSource source, GeographicalPosition position,
			WeatherState weatherState) {
		this.name = name;
		this.observationTime = observationTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.priority = priority;
		this.source = source;
		this.position = position;
		this.weatherState = weatherState;
	}
	
	public void createIndividuals(OntModel onto) {
		OntClass weatherReportClass = onto.getOntClass(NAMESPACE + "WeatherReport");
		individual = onto.createIndividual(NAMESPACE + name, weatherReportClass);
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasPriority"), priority));
		
		source.createIndividuals(onto);
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasSource"), source.getOntIndividual()));

		startTime.createIndividuals(onto);
		endTime.createIndividuals(onto);
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasStartTime"), startTime.getOntIndividual()));
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasStartTime"), endTime.getOntIndividual()));
		
		position.createIndividuals(onto);
		onto.add(onto.createStatement(individual, onto.getProperty(WGS84 + "location"), position.getOntIndividual()));
		
		observationTime.createIndividuals(onto);
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(NAMESPACE + "hasObservationTime"), observationTime.getOntIndividual()));

		if(previousReport != null) {
			weatherState.setPreviousState(previousReport.getState());
		}
		weatherState.createIndividuals(onto);
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

	public Interval getStartTime() {
		return startTime;
	}

	public Interval getEndTime() {
		return endTime;
	}

	public Instant getObservationTime() {
		return observationTime;
	}

	public GeographicalPosition getPosition() {
		return position;
	}

	public void setObservationTime(Instant observationTime) {
		this.observationTime = observationTime;
	}

	protected int getPriority() {
		return priority;
	}

	protected WeatherSource getSource() {
		return source;
	}

	protected void setPriority(int priority) {
		this.priority = priority;
	}

	protected void setSource(WeatherSource source) {
		this.source = source;
	}
	
	protected void setPosition(GeographicalPosition position) {
		this.position = position;
	}

	protected void setEndTime(Interval endTime) {
		this.endTime = endTime;
	}

	@Override
	public Individual getOntIndividual() {
		return individual;
	}

	public void setPreviousReport(WeatherReport previousReport) {
		this.previousReport = previousReport;
	}
}
