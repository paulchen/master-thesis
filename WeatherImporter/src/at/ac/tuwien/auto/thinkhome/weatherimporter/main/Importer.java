package at.ac.tuwien.auto.thinkhome.weatherimporter.main;

import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.model.GeographicalPosition;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Weather;

public interface Importer {
	public Weather fetchWeather(GeographicalPosition position, WeatherImporterProperties properties, List<Integer> forecastHours) throws WeatherImporterException;
}
