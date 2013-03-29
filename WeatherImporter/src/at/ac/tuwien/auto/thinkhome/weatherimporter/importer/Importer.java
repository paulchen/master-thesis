package at.ac.tuwien.auto.thinkhome.weatherimporter.importer;

import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.WeatherImporterException;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.WeatherImporterProperties;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.GeographicalPosition;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Weather;

public interface Importer {
	public Weather fetchWeather(GeographicalPosition position, WeatherImporterProperties properties, List<Integer> forecastHours) throws WeatherImporterException;
}
