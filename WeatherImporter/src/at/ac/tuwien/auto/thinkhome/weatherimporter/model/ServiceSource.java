package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

/**
 * This class represents an Internet weather service as the source of weather data.
 *   
 * @author Paul Staroch
 *
 */
public class ServiceSource extends WeatherSource {
	/**
	 * Constructor that sets the name which is to be used for the individual which corresponds to this instance in the ontology.
	 * 
	 * @param name a name
	 */
	public ServiceSource(String name) {
		super(name);
	}

	@Override
	protected String getOntClassName() {
		return "ServiceSource";
	}
}
