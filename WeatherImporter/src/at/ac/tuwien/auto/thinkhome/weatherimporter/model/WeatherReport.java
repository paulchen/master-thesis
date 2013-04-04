package at.ac.tuwien.auto.thinkhome.weatherimporter.model;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

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
		OntClass weatherReportClass = onto.getOntClass(Weather.NAMESPACE + "WeatherReport");
		individual = onto.createIndividual(Weather.NAMESPACE + name, weatherReportClass);
		
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasPriority"), priority));
		
		source.createIndividuals(onto);
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasSource"), source.getIndividual()));

		startTime.createIndividuals(onto);
		endTime.createIndividuals(onto);
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasStartTime"), startTime.getIndividual()));
		onto.add(onto.createStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasEndTime"), endTime.getIndividual()));
		
		position.createIndividuals(onto);
		onto.add(onto.createStatement(individual, onto.getProperty(GeographicalPosition.WGS84 + "location"), position.getIndividual()));
		
		observationTime.createIndividuals(onto);
		onto.add(onto.createLiteralStatement(individual, onto.getProperty(Weather.NAMESPACE + "hasObservationTime"), observationTime.getIndividual()));

		if(previousReport != null) {
			weatherState.setPreviousState(previousReport.getState());
		}
		weatherState.createIndividuals(onto);
	}	
	
	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		
		turtle.add(new TurtleStatement(getTurtleName(), "a", Weather.NAMESPACE_PREFIX + "WeatherReport"));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasPriority", String.valueOf(priority)));
		
		turtle.addAll(source.getTurtleStatements());
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasSource", source.getTurtleName()));
		
		turtle.addAll(startTime.getTurtleStatements());
		turtle.addAll(endTime.getTurtleStatements());
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasStartTime", startTime.getTurtleName()));
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasEndTime", endTime.getTurtleName()));
		
		turtle.addAll(position.getTurtleStatements());
		turtle.add(new TurtleStatement(getTurtleName(), GeographicalPosition.WGS84_PREFIX + "location", position.getTurtleName()));
		
		turtle.addAll(observationTime.getTurtleStatements());
		turtle.add(new TurtleStatement(getTurtleName(), Weather.NAMESPACE_PREFIX + "hasObservationTime", observationTime.getTurtleName()));
		
		if(previousReport != null) {
			weatherState.setPreviousState(previousReport.getState());
		}
		turtle.addAll(weatherState.getTurtleStatements());
		
		return turtle;
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
	public Individual getIndividual() {
		return individual;
	}

	public void setPreviousReport(WeatherReport previousReport) {
		this.previousReport = previousReport;
	}

	@Override
	public String getTurtleName() {
		return Weather.NAMESPACE_PREFIX + name;
	}
}
