package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

// TODO document
public class ServiceSource extends WeatherSource {
	public ServiceSource(String name) {
		super(name);
	}

	@Override
	protected String getOntClassName() {
		return "ServiceSource";
	}
}