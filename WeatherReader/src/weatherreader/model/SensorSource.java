package weatherreader.model;

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
