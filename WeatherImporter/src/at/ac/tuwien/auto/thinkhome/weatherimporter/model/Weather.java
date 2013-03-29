package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.SunPositionCalculator;
import at.ac.tuwien.auto.thinkhome.weatherimporter.turtle.TurtleStore;

import com.hp.hpl.jena.ontology.OntModel;

// TODO javadoc
public class Weather {
	private Date observationTime;
	private int priority;
	private WeatherSource source;
	private GeographicalPosition position;
	private List<WeatherReport> weatherReports;	
	private List<Integer> forecastHours;
	private int maxHour;
	
	private Logger log;
	
	public Weather(Date observationTime, int priority, WeatherSource source,
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
		/* we can use non-unique names here as normalizeWeatherReports() will
		 * create new instances of WeatherReport and Instant which have
		 * unique names 
		 */
		weatherReports.add(new WeatherReport("weatherReport", new Instant("instant", observationTime), Interval.getInterval(startDate), Interval.getInterval(endDate), priority, source, position, weatherState));
	}
	
	public void normalizeWeatherReports() {
		log.debug("Normalizing weather reports...");
		
		printWeatherReports("Weather reports before normalization", weatherReports);
		
		/* split weather reports with startDate != endDate */
		Iterator<WeatherReport> it = weatherReports.iterator();
		List<WeatherReport> additionalWeatherReports = new ArrayList<WeatherReport>();
		while(it.hasNext()) {
			WeatherReport report = it.next();
			if(report.getStartTime().getTime() != report.getEndTime().getTime()) {
				it.remove();
				for(int a=(int)report.getStartTime().getTime(); a<report.getEndTime().getTime(); a++) {
					additionalWeatherReports.add(new WeatherReport("weatherReport" + a, report.getObservationTime(), Interval.getInterval(a), Interval.getInterval(a), priority, source, report.getPosition(), report.getState()));  
				}
			}
		}
		printWeatherReports("Additional weather reports", additionalWeatherReports);
		
		weatherReports.addAll(additionalWeatherReports);
		
		printWeatherReports("Weather reports after splitting them up", weatherReports);
		
		List<Class<? extends WeatherPhenomenon>> phenomenonClasses = new ArrayList<Class<? extends WeatherPhenomenon>>();
		phenomenonClasses.add(Temperature.class);
		phenomenonClasses.add(Humidity.class);
		phenomenonClasses.add(DewPoint.class);
		phenomenonClasses.add(Pressure.class);
		phenomenonClasses.add(Wind.class);
		phenomenonClasses.add(Precipitation.class);
		phenomenonClasses.add(SunPosition.class);
		phenomenonClasses.add(CloudCover.class);
		
		/* merge weather states */
		Map<Integer, WeatherReport> newWeatherReports = new HashMap<Integer, WeatherReport>();
		for(int a=0; a<=maxHour; a++) {
			newWeatherReports.put(a, new WeatherReport("weatherReport" + a, null, Interval.getInterval(a), Interval.getInterval(a+1), 0, null, null, new WeatherState("weatherState" + a)));
		}
		for(WeatherReport report : weatherReports) {
			WeatherState state = report.getState();
			
			WeatherReport newReport = newWeatherReports.get((int)report.getStartTime().getTime());
			newReport.setObservationTime(report.getObservationTime());
			newReport.setPriority(report.getPriority());
			newReport.setSource(report.getSource());
			newReport.setPosition(report.getPosition());
			
			WeatherState newState = (WeatherState)(newReport.getState());
			
			/* let's assume we get a value only from one source */
			for(Class<? extends WeatherPhenomenon> clazz : phenomenonClasses) {
				newState.addPhenomena(state.getPhenomenonType(clazz));
			}

			newState.mergePhenomena(String.valueOf(report.getStartTime()));
			
			if(state.getWeatherConditions().size() > 0) {
				newState.setWeatherConditions(state.getWeatherConditions());
			}
		}
		
		printWeatherReports("Weather reports after merging states for the same time", newWeatherReports);
		
		/* interpolate missing values */
		Map<Class<? extends WeatherPhenomenon>, Integer> lastPhenomena = new HashMap<Class<? extends WeatherPhenomenon>, Integer>();
		
		Integer lastWeatherConditions = null;
		for(int a=0; a<=maxHour; a++) {
			WeatherReport currentReport = newWeatherReports.get(a);
			WeatherState currentState = currentReport.getState();

			for(Class<? extends WeatherPhenomenon> clazz : phenomenonClasses) {
				if(currentState.containsPhenomenonType(clazz)) {
					WeatherPhenomenon lastWeatherPhenomenon;
					int start;
					
					if(!lastPhenomena.containsKey(clazz)) {
						lastWeatherPhenomenon = currentState.getPhenomenonType(clazz).get(0);
						start = 0;
					}
					else {
						lastWeatherPhenomenon = newWeatherReports.get(lastPhenomena.get(clazz)).getState().getPhenomenonType(clazz).get(0);
						start = lastPhenomena.get(clazz) + 1;
					}
					WeatherPhenomenon thisValue = currentState.getPhenomenonType(clazz).get(0);
					for(int b=start; b<a; b++) {
						if(newWeatherReports.get(b).getState().containsPhenomenonType(clazz)) {
							newWeatherReports.get(b).getState().getPhenomenonType(clazz).get(0).interpolate(lastWeatherPhenomenon, thisValue, a, b);
						}
						else {
							newWeatherReports.get(b).getState().addPhenomenon(currentState.getPhenomenonType(clazz).get(0).createInterpolatedPhenomenon(String.valueOf(b), lastWeatherPhenomenon, thisValue, a, b));
						}
					}
					
					lastPhenomena.put(clazz, a);
				}
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
		
		for(int a=0; a<weatherReports.size(); a++) {
			float nextStartTime;
			if(a+1<weatherReports.size()) {
				nextStartTime = weatherReports.get(a+1).getStartTime().getTime();
			}
			else {
				nextStartTime = 2*weatherReports.get(a).getStartTime().getTime() - weatherReports.get(a-1).getStartTime().getTime();
			}
			weatherReports.get(a).setEndTime(Interval.getInterval(nextStartTime));
		}
		
		printWeatherReports("Weather reports after normalization", weatherReports);
		
		// add sun position data
		SunPositionCalculator sunPositionCalculator = new SunPositionCalculator(position);
		for(WeatherReport report : weatherReports) {
			Date date = new Date((long)(new Date().getTime() + report.getStartTime().getTime()*3600000));
			SunPosition position = sunPositionCalculator.calculate(date);
			position.setName("sunPosition" + report.getStartTime());
			report.getState().addPhenomenon(position);
		}
		
		printWeatherReports("Final weather states after normalization including sun position data", weatherReports);
	}

	public void createIndividuals(OntModel ontology) {
		WeatherReport previousReport = null;
		for(WeatherReport report : weatherReports) {
			report.setPreviousReport(previousReport);
			report.createIndividuals(ontology);
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

	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		for(WeatherReport report : weatherReports) {
			turtle.addAll(report.getTurtleStatements());
		}
		return turtle;
	}	
}
