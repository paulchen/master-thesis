package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

// TODO javadoc
public class SensorSource extends WeatherSource {
	public SensorSource(String name) {
		super(name);
	}

	@Override
	protected String getOntClassName() {
		return "SensorSource";
	}
}
