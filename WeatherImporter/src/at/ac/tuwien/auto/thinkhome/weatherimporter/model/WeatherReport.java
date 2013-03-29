package at.ac.tuwien.auto.thinkhome.weatherimporter.model;
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
		OntClass weatherReportClass = onto.getOntClass(WeatherConstants.NAMESPACE + "WeatherReport");
		individual = onto.createIndividual(WeatherConstants.NAMESPACE + name, weatherReportClass);
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasPriority"), priority));
		
		source.createIndividuals(onto);
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasSource"), source.getOntIndividual()));

		startTime.createIndividuals(onto);
		endTime.createIndividuals(onto);
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasStartTime"), startTime.getOntIndividual()));
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasEndTime"), endTime.getOntIndividual()));
		
		position.createIndividuals(onto);
		onto.add(onto.createStatement(individual, onto.getProperty(WeatherConstants.WGS84 + "location"), position.getOntIndividual()));
		
		observationTime.createIndividuals(onto);
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(WeatherConstants.NAMESPACE + "hasObservationTime"), observationTime.getOntIndividual()));

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
