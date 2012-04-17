package weatherreader;

//TODO javadoc
public class WeatherReaderException extends Exception {
	private static final long serialVersionUID = -6120259914497375879L;

	public WeatherReaderException() {
		super();
	}

	public WeatherReaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public WeatherReaderException(String message) {
		super(message);
	}

	public WeatherReaderException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
