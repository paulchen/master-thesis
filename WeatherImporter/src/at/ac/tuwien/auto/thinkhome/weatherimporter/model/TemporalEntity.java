package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

/**
 * This class represents temporal information (either {@link Instant} or {@link Interval}). 
 *  
 * @author Paul Staroch
 *
 */
public abstract class TemporalEntity implements OntologyClass {
	/**
	 * URI of the namespace used by the OWL-Time ontology
	 */
	public static final String TIME = "http://www.w3.org/2006/time#";
	
	/**
	 * Prefix to be used for the namespace which is used by the OWL-Time ontology
	 */
	public static final String TIME_PREFIX = "time:";
}
