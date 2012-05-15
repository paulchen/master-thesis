package weatherreader.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import weatherreader.SunPositionCalculator;

import com.hp.hpl.jena.ontology.OntModel;

public class Weather {
	private Date observationTime;
	private int priority;
	private String source;
	private GeographicalPosition position;
	private List<WeatherReport> weatherReports;	
	private List<Integer> forecastHours;
	private int maxHour;
	
	private Logger log;
	
	public Weather(Date observationTime, int priority, String source,
			GeographicalPosition position, List<Integer> forecastHours) {
		super();
		
		this.observationTime = observationTime;
		this.priority = priority;
		this.source = source;
		this.position = position;
		this.forecastHours = forecastHours;
		this.maxHour = this.forecastHours.get(forecastHours.size() - 1)*2;		
		weatherReports = new ArrayList<WeatherReport>();
		
		log = Logger.getLogger(Weather.class);
	}
	
	public void newWeatherReport(Date startDate, Date endDate, WeatherState weatherState) {
		weatherReports.add(new WeatherReport(observationTime, startDate, endDate, priority, source, position, weatherState));
	}
	
	public void normalizeWeatherReports() {
		log.debug("Normalizing weather reports...");
		
		printWeatherReports("Weather reports before normalization", weatherReports);
		
		/* split weather reports with startDate != endDate */
		Iterator<WeatherReport> it = weatherReports.iterator();
		List<WeatherReport> additionalWeatherReports = new ArrayList<WeatherReport>();
		while(it.hasNext()) {
			WeatherReport report = it.next();
			if(report.getStartTime() != report.getEndTime()) {
				it.remove();
				for(int a=(int)report.getStartTime(); a<report.getEndTime(); a++) {
					// TODO clone weather state?
					additionalWeatherReports.add(new WeatherReport(report.getObservationTime(), (float)a, (float)a, priority, source, report.getPosition(), report.getState()));  
				}
			}
		}
		weatherReports.addAll(additionalWeatherReports);
		
		printWeatherReports("Weather reports after splitting them up", weatherReports);
		
		/* merge weather states */
		Map<Integer, WeatherReport> newWeatherReports = new HashMap<Integer, WeatherReport>();
		for(int a=0; a<=maxHour; a++) {
			// TODO clone weather state
			newWeatherReports.put(a, new WeatherReport(null, a, a+1, 0, "", null, new WeatherState(null, null, null, null, null, null, null, null, new ArrayList<CloudLayer>(), new ArrayList<WeatherCondition>())));
		}
		for(WeatherReport report : weatherReports) {
			WeatherState state = report.getState();
			
			WeatherReport newReport = newWeatherReports.get((int)report.getStartTime());
			newReport.setObservationTime(report.getObservationTime());
			newReport.setPriority(report.getPriority());
			newReport.setSource(report.getSource());
			newReport.setPosition(report.getPosition());
			
			WeatherState newState = newReport.getState();
			
			/* let's assume we get a value only from one source */
			if(state.getTemperatureValue() != null) {
				newState.setTemperatureValue(state.getTemperatureValue());
			}
			if(state.getHumidityValue() != null) {
				newState.setHumidityValue(state.getHumidityValue());
			}
			if(state.getDewPointValue() != null) {
				newState.setDewPointValue(state.getDewPointValue());
			}
			if(state.getPressureValue() != null) {
				newState.setPressureValue(state.getPressureValue());
			}
			if(state.getWindSpeed() != null) {
				newState.setWindSpeed(state.getWindSpeed());
			}
			if(state.getWindDirection() != null) {
				newState.setWindDirection(state.getWindDirection());
			}
			if(state.getPrecipitationProbability() != null) {
				newState.setPrecipitationProbability(state.getPrecipitationProbability());
			}
			if(state.getPrecipitationIntensity() != null) {
				newState.setPrecipitationIntensity(state.getPrecipitationIntensity());
			}
			if(state.getCloudLayers().size() > 0) {
				newState.setCloudLayers(state.getCloudLayers());
			}
			if(state.getWeatherConditions().size() > 0) {
				newState.setWeatherConditions(state.getWeatherConditions());
			}
		}
		
		printWeatherReports("Weather reports after merging states for the same time", newWeatherReports);
		
		/* interpolate missing values */
		Integer lastTemperatureValue = null;
		Integer lastHumidityValue = null;
		Integer lastDewPointValue = null;
		Integer lastPressureValue = null;
		Integer lastWindSpeed = null;
		Integer lastWindDirection = null;
		Integer lastPrecipitationProbability = null;
		Integer lastPrecipitationIntensity = null;
		Integer lastCloudLayers = null;
		Integer lastWeatherConditions = null;
		
		for(int a=0; a<=maxHour; a++) {
			WeatherReport currentReport = newWeatherReports.get(a);
			WeatherState currentState = currentReport.getState();
			
			if(currentState.getTemperatureValue() != null) {
				float lastValue;
				int start;
				if(lastTemperatureValue == null) {
					lastValue = currentState.getTemperatureValue();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastTemperatureValue).getState().getTemperatureValue();
					start = lastTemperatureValue + 1;
				}
				Float thisValue = currentState.getTemperatureValue();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherReports.get(b).getState().setTemperatureValue(newValue);
				}
				
				lastTemperatureValue = a;
			}
			
			if(currentState.getHumidityValue() != null) {
				float lastValue;
				int start;
				if(lastHumidityValue == null) {
					lastValue = currentState.getHumidityValue();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastHumidityValue).getState().getHumidityValue();
					start = lastHumidityValue + 1;
				}
				Float thisValue = currentState.getHumidityValue();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherReports.get(b).getState().setHumidityValue(newValue);
				}
				
				lastHumidityValue = a;
			}
			
			if(currentState.getDewPointValue() != null) {
				float lastValue;
				int start;
				if(lastDewPointValue == null) {
					lastValue = currentState.getDewPointValue();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastDewPointValue).getState().getDewPointValue();
					start = lastDewPointValue + 1;
				}
				Float thisValue = currentState.getDewPointValue();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherReports.get(b).getState().setDewPointValue(newValue);
				}
				
				lastDewPointValue = a;
			}
			
			if(currentState.getPressureValue() != null) {
				float lastValue;
				int start;
				if(lastPressureValue == null) {
					lastValue = currentState.getPressureValue();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastPressureValue).getState().getPressureValue();
					start = lastPressureValue + 1;
				}
				Float thisValue = currentState.getPressureValue();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherReports.get(b).getState().setPressureValue(newValue);
				}
				
				lastPressureValue = a;
			}
			
			if(currentState.getWindSpeed() != null) {
				float lastValue;
				int start;
				if(lastWindSpeed == null) {
					lastValue = currentState.getWindSpeed();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastWindSpeed).getState().getWindSpeed();
					start = lastWindSpeed + 1;
				}
				Float thisValue = currentState.getWindSpeed();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherReports.get(b).getState().setWindSpeed(newValue);
				}
				
				lastWindSpeed = a;
			}
			
			if(currentState.getWindDirection() != null) {
				int lastValue;
				int start;
				if(lastWindDirection == null) {
					lastValue = currentState.getWindDirection();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastWindDirection).getState().getWindDirection();
					start = lastWindDirection + 1;
				}
				int thisValue = currentState.getWindDirection();
				for(int b=start; b<a; b++) {
					int newValue = lastValue + (thisValue - lastValue)/(a-b);
					newWeatherReports.get(b).getState().setWindDirection(newValue);
				}
				
				lastWindDirection = a;
			}
			
			if(currentState.getPrecipitationProbability() != null) {
				float lastValue;
				int start;
				if(lastPrecipitationProbability == null) {
					lastValue = currentState.getPrecipitationProbability();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastPrecipitationProbability).getState().getPrecipitationProbability();
					start = lastPrecipitationProbability + 1;
				}
				Float thisValue = currentState.getPrecipitationProbability();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherReports.get(b).getState().setPrecipitationProbability(newValue);
				}
				
				lastPrecipitationProbability = a;
			}
			
			if(currentState.getPrecipitationIntensity() != null) {
				float lastValue;
				int start;
				if(lastPrecipitationIntensity == null) {
					lastValue = currentState.getPrecipitationIntensity();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastPrecipitationIntensity).getState().getPrecipitationIntensity();
					start = lastPrecipitationIntensity + 1;
				}
				Float thisValue = currentState.getPrecipitationIntensity();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherReports.get(b).getState().setPrecipitationIntensity(newValue);
				}
				
				lastPrecipitationIntensity = a;
			}
			
			if(currentState.getCloudLayers().size() > 0) {
				List<CloudLayer> lastValue;
				int start;
				if(lastCloudLayers == null) {
					lastValue = currentState.getCloudLayers();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastCloudLayers).getState().getCloudLayers();
					start = lastCloudLayers + 1;
				}
				for(int b=start; b<a; b++) {
					newWeatherReports.get(b).getState().setCloudLayers(lastValue);
				}
				
				lastCloudLayers = a;
			}
			
			if(currentState.getWeatherConditions().size() > 0) {
				List<WeatherCondition> lastValue;
				int start;
				if(lastWeatherConditions == null) {
					lastValue = currentState.getWeatherConditions();
					start = 0;
				}
				else {
					lastValue = newWeatherReports.get(lastWeatherConditions).getState().getWeatherConditions();
					start = lastWeatherConditions + 1;
				}
				for(int b=start; b<a; b++) {
					newWeatherReports.get(b).getState().setWeatherConditions(lastValue);
				}
				
				lastWeatherConditions = a;
			}
		}
		
		weatherReports = new ArrayList<WeatherReport>();
		for(int a=0; a<=maxHour; a++) {
			if(forecastHours.contains(a)) {
				weatherReports.add(newWeatherReports.get(a));
			}
		}
		
		printWeatherReports("Weather reports after normalization", weatherReports);
		
		// add sun position data
		SunPositionCalculator sunPositionCalculator = new SunPositionCalculator(position);
		for(WeatherReport report : weatherReports) {
			Date date = new Date((long)(new Date().getTime() + report.getStartTime()*3600000));
			report.getState().setSunPosition(sunPositionCalculator.calculate(date));
		}
		
		printWeatherReports("Final weather states after normalization including sun position data", weatherReports);
	}

	public void createIndividuals(OntModel ontology) {
		int a=0;
		WeatherReport previousReport = null;
		for(WeatherReport report : weatherReports) {
			report.createIndividuals(ontology, ++a, previousReport);
			previousReport = report;
		}
	}
	
	
	private void printWeatherReports(String description, Collection<WeatherReport> reports) {
		log.debug("");		
		log.debug(description + ":");
		log.debug("");		
		for(WeatherReport report : reports) {
			log.debug(report.toString());
		}
		log.debug("");
	}
	
	private void printWeatherReports(String description, Map<Integer, WeatherReport> reports) {
		log.debug("");		
		log.debug(description + ":");
		
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(reports.keySet());
		Collections.sort(keys);
		for(Integer key : keys) {
			log.debug(reports.get(key).toString());
		}
		log.debug("");
	}
	
}
