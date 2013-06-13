package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

/**
 * This class represents a single weather sensor or a set of weather sensors as the source of weather data.
 *   
 * @author Paul Staroch
 *
 */
public class SensorSource extends WeatherSource {
	/**
	 * Constructor that sets the name which is to be used for the individual which corresponds to this instance in the ontology.
	 * 
	 * @param name a name
	 */
	public SensorSource(String name) {
		super(name);
	}

	@Override
	protected String getOntClassName() {
		return "SensorSource";
	}
}
