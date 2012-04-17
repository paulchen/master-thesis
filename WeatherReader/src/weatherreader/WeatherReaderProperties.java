package weatherreader;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// TODO javadoc
public class WeatherReaderProperties extends Properties {
	private static final long serialVersionUID = -391442570453412781L;
	
	public final String DEFAULT_SEPARATOR = ",";
	
	public String getString(String key) throws WeatherReaderException {
		String value = getProperty(key);
		if(value == null) {
			throw new WeatherReaderException("Missing key '" + key + "' in properties file.");
		}
		return value;
	}
	
	public Float getFloat(String key) throws WeatherReaderException {
		try {
			return Float.parseFloat(getString(key));
		}
		catch (NumberFormatException e) {
			throw new WeatherReaderException("Invalid value for key '" + key + "' in properties file.", e);
		}
	}
	
	public int getInt(String key) throws WeatherReaderException {
		try {
			return Integer.parseInt(getString(key));
		}
		catch (NumberFormatException e) {
			throw new WeatherReaderException("Invalid value for key '" + key + "' in properties file.", e);
		}
	}
	
	public List<Integer> getIntArray(String key) throws WeatherReaderException {
		return getIntArray(key, DEFAULT_SEPARATOR);
	}
	
	public List<Integer> getIntArray(String key, String separator) throws WeatherReaderException {
		String string = getString(key);		
		String parts[] = string.split(separator);
		List<Integer> list = new ArrayList<Integer>();
		for(String part : parts) {
			try {
				list.add(Integer.parseInt(part));
			}
			catch (NumberFormatException e) {
				throw new WeatherReaderException("Invalid value for key '" + key + "' in properties file.", e);
			}
		}
		
		return list;
	}
	
	public void load(String filename) throws WeatherReaderException {
		try {
			super.load(new FileInputStream(filename));
		}
		catch (Exception e) {
			throw new WeatherReaderException("Could not read properties file - " + e.getMessage());
		}		
	}
}
