package at.ac.tuwien.auto.thinkhome.weatherimporter.common;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


// TODO javadoc
public class WeatherImporterProperties extends Properties {
	private static final long serialVersionUID = -391442570453412781L;
	
	public final String DEFAULT_SEPARATOR = ",";
	
	public String getString(String key) throws WeatherImporterException {
		String value = getProperty(key);
		if(value == null) {
			throw new WeatherImporterException("Missing key '" + key + "' in properties file.");
		}
		return value;
	}
	
	public Float getFloat(String key) throws WeatherImporterException {
		try {
			return Float.parseFloat(getString(key));
		}
		catch (NumberFormatException e) {
			throw new WeatherImporterException("Invalid value for key '" + key + "' in properties file.", e);
		}
	}
	
	public int getInt(String key) throws WeatherImporterException {
		try {
			return Integer.parseInt(getString(key));
		}
		catch (NumberFormatException e) {
			throw new WeatherImporterException("Invalid value for key '" + key + "' in properties file.", e);
		}
	}
	
	public List<Integer> getIntArray(String key) throws WeatherImporterException {
		return getIntArray(key, DEFAULT_SEPARATOR);
	}
	
	public List<Integer> getIntArray(String key, String separator) throws WeatherImporterException {
		String string = getString(key);		
		String parts[] = string.split(separator);
		List<Integer> list = new ArrayList<Integer>();
		for(String part : parts) {
			try {
				list.add(Integer.parseInt(part));
			}
			catch (NumberFormatException e) {
				throw new WeatherImporterException("Invalid value for key '" + key + "' in properties file.", e);
			}
		}
		
		return list;
	}
	
	public void load(String filename) throws WeatherImporterException {
		try {
			super.load(new FileInputStream(filename));
		}
		catch (Exception e) {
			throw new WeatherImporterException("Could not read properties file - " + e.getMessage());
		}		
	}
}
