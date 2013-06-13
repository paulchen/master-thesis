package at.ac.tuwien.auto.thinkhome.weatherimporter.main;

import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.model.GeographicalPosition;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Weather;

/**
 * This class is implemented by any class which imports data from a weather
 * service.
 * 
 * @author Paul Staroch
 */
public interface Importer {
	/**
	 * Fetches the weather from the weather service and return all data that is
	 * to be used by <em>ThinkHome</em>.
	 * 
	 * @param position
	 *            defines the geographical position (latitude, longitude,
	 *            altitude) for which the weather shall be fetched
	 * @param properties
	 *            contains all settings from the properties file
	 * @param forecastHours
	 *            a list of integer values denoting the points in time the
	 *            weather shall be obtained for, relative to the current time in
	 *            hours; e.g. if this list contains the values 0, 1, 2, and 3,
	 *            the method is expected to return an instance of
	 *            {@link Weather} that contains data about the current weather
	 *            and about the weather in 1, 2, and 3 hours.
	 * @return an instance of {@link Weather} containing the data fetched from
	 *         the weather service
	 * @throws WeatherImporterException
	 *             in case any error occurs during accessing the weather service
	 *             and/or processing the data obtained from the weather service
	 */
	public Weather fetchWeather(GeographicalPosition position,
			WeatherImporterProperties properties, List<Integer> forecastHours)
			throws WeatherImporterException;
}
