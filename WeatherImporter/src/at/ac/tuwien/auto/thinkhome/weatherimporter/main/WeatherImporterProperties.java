package at.ac.tuwien.auto.thinkhome.weatherimporter.main;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This is a subclass of <tt>Properties</tt> that adds some convenience methods
 * for fetching settings of certain types. If a key does not exist, these
 * convenience methods throw an adequate exception.
 * 
 * @author Paul Staroch
 * 
 */
public class WeatherImporterProperties extends Properties {
	/**
	 * UID as required by the interface <tt>Serializable</tt>
	 */
	private static final long serialVersionUID = -391442570453412781L;

	/**
	 * Property values may be arrays which are represented by strings that
	 * consists of the array values that are concatenated using a separator
	 * between two values. This is the default separator if it is not specified
	 * when calling the appropriate convenience function.
	 */
	public final String DEFAULT_SEPARATOR = ",";

	/**
	 * Retrieves a property of type <tt>String</tt>
	 * 
	 * @param key
	 *            the name of the property
	 * @return the value of the property
	 * @throws WeatherImporterException
	 *             if there is no property that bears the specified name
	 */
	public String getString(String key) throws WeatherImporterException {
		String value = getProperty(key);
		if (value == null) {
			throw new WeatherImporterException("Missing key '" + key
					+ "' in properties file.");
		}
		return value;
	}

	/**
	 * Retrieves a property of type <tt>Float</tt>
	 * 
	 * @param key
	 *            the name of the property
	 * @return the value of the property
	 * @throws WeatherImporterException
	 *             if there is no property that bears the specified name or the
	 *             value of the property does not have the desired type
	 */
	public Float getFloat(String key) throws WeatherImporterException {
		try {
			return Float.parseFloat(getString(key));
		} catch (NumberFormatException e) {
			throw new WeatherImporterException("Invalid value for key '" + key
					+ "' in properties file.", e);
		}
	}

	/**
	 * Retrieves a property of type <tt>Float</tt>
	 * 
	 * @param key
	 *            the name of the property
	 * @return the value of the property
	 * @throws WeatherImporterException
	 *             if there is no property that bears the specified name or the
	 *             value of the property does not have the desired type
	 */
	public int getInt(String key) throws WeatherImporterException {
		try {
			return Integer.parseInt(getString(key));
		} catch (NumberFormatException e) {
			throw new WeatherImporterException("Invalid value for key '" + key
					+ "' in properties file.", e);
		}
	}

	/**
	 * Retrieves a property of type <tt>List&lt;Integer&gt;</tt>. It is expected
	 * that the value of the property consists of the array values, separated by
	 * commas.
	 * 
	 * @param key
	 *            the name of the property
	 * @return the value of the property
	 * @throws WeatherImporterException
	 *             if there is no property that bears the specified name or the
	 *             value of the property does not have the desired type
	 */
	public List<Integer> getIntArray(String key)
			throws WeatherImporterException {
		return getIntArray(key, DEFAULT_SEPARATOR);
	}

	/**
	 * Retrieves a property of type <tt>List&lt;Integer&gt;</tt>. It is expected
	 * that the value of the property consists of the array values, separated by
	 * the specified separator.
	 * 
	 * @param key
	 *            the name of the property
	 * @param separator
	 *            the String that separates two array values in the property
	 *            value
	 * @return the value of the property
	 * @throws WeatherImporterException
	 *             if there is no property that bears the specified name or the
	 *             value of the property does not have the desired type
	 */
	public List<Integer> getIntArray(String key, String separator)
			throws WeatherImporterException {
		String string = getString(key);
		String parts[] = string.split(separator);
		List<Integer> list = new ArrayList<Integer>();
		for (String part : parts) {
			try {
				list.add(Integer.parseInt(part));
			} catch (NumberFormatException e) {
				throw new WeatherImporterException("Invalid value for key '"
						+ key + "' in properties file.", e);
			}
		}

		return list;
	}

	/**
	 * Loads properties from a properties file
	 * 
	 * @param filename
	 *            name of the properties file
	 * @throws WeatherImporterException
	 *             if any error occurs
	 */
	public void load(String filename) throws WeatherImporterException {
		try {
			super.load(new FileInputStream(filename));
		} catch (Exception e) {
			throw new WeatherImporterException(
					"Could not read properties file - " + e.getMessage());
		}
	}
}
