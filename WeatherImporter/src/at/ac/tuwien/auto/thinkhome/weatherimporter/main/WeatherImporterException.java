package at.ac.tuwien.auto.thinkhome.weatherimporter.main;

/**
 * Exception class for the Weather Importer. It is a simple subclass of
 * <tt>Exception</tt> that does not have any special functionality.
 * 
 * @author Paul Staroch
 */
public class WeatherImporterException extends Exception {
	/**
	 * UID as required by the interface <tt>Serializable</tt>
	 */
	private static final long serialVersionUID = -6120259914497375879L;

	/**
	 * Constructor specifying an empty message and no cause
	 */
	public WeatherImporterException() {
		super();
	}

	/**
	 * Constructor specifying both a message and a cause
	 */
	public WeatherImporterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor specifying a message and no cause
	 */
	public WeatherImporterException(String message) {
		super(message);
	}

	/**
	 * Constructor specifying an empty message and a cause
	 */
	public WeatherImporterException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
