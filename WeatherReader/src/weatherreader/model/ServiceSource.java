package weatherreader.model;


public class ServiceSource extends WeatherSource {
	public ServiceSource(String name) {
		super(name);
	}

	@Override
	protected String getOntClassName() {
		return "ServiceSource";
	}
}
