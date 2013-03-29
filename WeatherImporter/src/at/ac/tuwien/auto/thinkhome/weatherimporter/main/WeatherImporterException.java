package at.ac.tuwien.auto.thinkhome.weatherimporter.main;

//TODO javadoc
public class WeatherImporterException extends Exception {
	private static final long serialVersionUID = -6120259914497375879L;

	public WeatherImporterException() {
		super();
	}

	public WeatherImporterException(String message, Throwable cause) {
		super(message, cause);
	}

	public WeatherImporterException(String message) {
		super(message);
	}

	public WeatherImporterException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
