package at.ac.tuwien.auto.thinkhome.weatherimporter.model;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

/**
 * This class represents a weather report for a certain period of time at a certain location which has been obtained from a certain location.
 * 
 * @author Paul Staroch
 *
 */
public class WeatherReport implements OntologyClass {
	/**
	 * Time when the data in this weather report has been obtained from the data source.
	 */
	private Instant observationTime;
	
	/**
	 * Position this weather report belongs to.
	 */
	private GeographicalPosition position;
	
	/**
	 * Instance of {@link WeatherState} which contains all weather data that belongs to this weather report.
	 */
	private WeatherState weatherState;
	
	/**
	 * Priority of this weather report.
	 */
	private int priority;
	
	/**
	 * Source of the data of this weather report.
	 */
	private WeatherSource source;
	
	/**
	 * Beginning of the period of time this weather report is valid for. 
	 */
	private Interval startTime;

	/**
	 * End of the period of time this weather report is valid for. 
	 */
	private Interval endTime;
	
	/**
	 * Unique name of the individual that corresponds to this object in the ontology 
	 */
	private String name;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains the main individual in the ontology that has been created by that method call.
	 */
	private Individual individual;
	
	/**
	 * Previous weather report obtained at the same time from the same source, if it exists.
	 */
	private WeatherReport previousReport;
	
	/**
	 * Constructor for this class.
	 * 
	 * @param name the unique name of the individual in the ontology that corresponds to this object
	 * @param observationTime the date when the data represented by this report has been obtained
	 * @param startTime the start of the period of time the data in this weather report is valid for 
	 * @param endTime the end of the period of time the data in this weather report is valid for
	 * @param priority the priority of all data in this weather report
	 * @param source the source of all data of this weather report
	 * @param position the geographical position this weather report is valid for
	 * @param weatherState the instance of <tt>WeatherState</tt> that belongs to this instance; it contains all weather data available for this weather report
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
	
	@Override
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
