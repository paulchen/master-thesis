package weatherrader.model;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO javadoc
public class Weather {
	private Date observationTime;
	private float latitude;
	private float longitude;
	private int altitude;
	private List<WeatherState> weatherStates;
	
	public static final String NAMESPACE = "http://www.semanticweb.org/ontologies/2011/9/ThinkHomeWeather.owl#";
	public static final String WGS84 = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String TIME = "http://www.w3.org/2006/time#";
	
	private List<Integer> forecastHours;
	private int maxHour;
	
	private Logger log;
	
	public Weather(float latitude, float longitude, int altitude, List<Integer> forecastHours) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.observationTime = new Date();
		this.weatherStates = new ArrayList<WeatherState>();
		this.altitude = altitude;
		this.forecastHours = new ArrayList<Integer>(forecastHours);
		Collections.sort(this.forecastHours);
		this.maxHour = this.forecastHours.get(forecastHours.size() - 1)*2;
		
		log = Logger.getLogger(Weather.class);
	}
	
	public void addWeatherState(WeatherState state) {
		weatherStates.add(state);
	}
	
	public void createIndividuals(OntModel onto) {
		OntClass weatherObservationClass = onto.getOntClass(NAMESPACE + "WeatherObservation");
		Individual weatherObservation = onto.createIndividual(NAMESPACE + "weatherObservation", weatherObservationClass);
		
		Resource pointClass = onto.getResource(WGS84 + "Point");
		
		Individual point = onto.createIndividual(NAMESPACE + "point0", pointClass);
		onto.add(onto.createStatement(weatherObservation, onto.getProperty(WGS84 + "location"), point));
		onto.add(onto.createStatement(point, RDF.type, pointClass));		
		
		onto.add(onto.createLiteralStatement(point, onto.getProperty(WGS84 + "lat"), latitude));
		onto.add(onto.createLiteralStatement(point, onto.getProperty(WGS84 + "long"), longitude));
		onto.add(onto.createLiteralStatement(point, onto.getProperty(WGS84 + "alt"), altitude));
		
		Resource instantClass = onto.getResource(TIME + "Instant");
		Individual instant = onto.createIndividual(NAMESPACE + "instant0", instantClass);

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(observationTime);
		Resource dateTimeClass = onto.getResource(TIME + "DateTimeDescription");
		Individual dateTime = onto.createIndividual(NAMESPACE + "dateTime0", dateTimeClass);
		
		onto.add(onto.createStatement(dateTime, onto.getProperty(TIME + "unitType"), onto.getResource(TIME + "unitMinute")));
		onto.add(onto.createLiteralStatement(dateTime, onto.getProperty(TIME + "minute"), new BigDecimal(calendar.get(Calendar.MINUTE))));
		onto.add(onto.createLiteralStatement(dateTime, onto.getProperty(TIME + "hour"), new BigDecimal(calendar.get(Calendar.HOUR_OF_DAY))));
		
		String dayString = "---";
		if(calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			dayString += "0";
		}
		dayString += calendar.get(Calendar.DAY_OF_MONTH);
		onto.add(onto.createStatement(dateTime, onto.getProperty(TIME + "day"), onto.createTypedLiteral(dayString, XSDDatatype.XSDgDay)));
		
		String monthString = "--";
		if(calendar.get(Calendar.MONTH) < 10) {
			monthString += "0";
		}
		monthString += calendar.get(Calendar.MONTH);
		onto.add(onto.createStatement(dateTime, onto.getProperty(TIME + "month"), onto.createTypedLiteral(monthString, XSDDatatype.XSDgMonth)));
		
		onto.add(onto.createStatement(dateTime, onto.getProperty(TIME + "year"), onto.createTypedLiteral(String.valueOf(calendar.get(Calendar.YEAR)), XSDDatatype.XSDgYear)));
		
		onto.add(onto.createStatement(instant, onto.getProperty(TIME + "inDateTime"), dateTime));
		
		onto.add(onto.createLiteralStatement(weatherObservation, onto.getProperty(NAMESPACE + "hasObservationTime"), instant));

		int a = 0;
		WeatherState previousState = null;
		for(WeatherState state : weatherStates) {
			state.createIndividuals(onto, weatherObservation, a, previousState);
			a++;
			previousState = state;
		}
	}
	
	private void printWeatherStates(String description, Collection<WeatherState> states) {
		log.debug("");		
		log.debug(description + ":");
		log.debug("");		
		for(WeatherState state : states) {
			log.debug(state.toString());
		}
		log.debug("");
	}
	
	private void printWeatherStates(String description, Map<Integer, WeatherState> states) {
		log.debug("");		
		log.debug(description + ":");
		
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(states.keySet());
		Collections.sort(keys);
		for(Integer key : keys) {
			log.debug(states.get(key).toString());
		}
		log.debug("");
	}
	
	public void normalizeWeatherStates() {
		log.debug("Normalizing weather states...");
		
		printWeatherStates("Weather states before normalization", weatherStates);
		
		/* split weather states with startDate != endDate */
		Iterator<WeatherState> it = weatherStates.iterator();
		List<WeatherState> additionalWeatherStates = new ArrayList<WeatherState>();
		while(it.hasNext()) {
			WeatherState state = it.next();
			if(state.getStartDate() != state.getEndDate()) {
				it.remove();
				for(int a=(int)state.getStartDate(); a<state.getEndDate(); a++) {
					additionalWeatherStates.add(new WeatherState((float)a, (float)a, state.getTemperature(), state.getHumidity(), state.getDewPoint(), state.getPressure(),
							state.getWindSpeed(), state.getWindDirection(), state.getPrecipitationProbability(), state.getPrecipitationValue(),
							state.getCloudLayers(), state.getWeatherConditions(), state.getSource(), state.getPriority()));
				}
			}
		}
		weatherStates.addAll(additionalWeatherStates);
		
		printWeatherStates("Weather states after splitting them up", weatherStates);
		
		/* merge weather states */
		Map<Integer, WeatherState> newWeatherStates = new HashMap<Integer, WeatherState>();
		for(int a=0; a<=maxHour; a++) {
			newWeatherStates.put(a, new WeatherState((float)a, (float)a, null, null, null, null, null, null, null, null, new ArrayList<CloudLayer>(), new ArrayList<WeatherCondition>(), "", 0));
		}
		for(WeatherState state : weatherStates) {
			WeatherState newState = newWeatherStates.get((int)state.getStartDate());
			newState.setPriority(state.getPriority());
			newState.setSource(state.getSource());
			
			/* let's assume we get a value only from one source */
			if(state.getTemperature() != null) {
				newState.setTemperature(state.getTemperature());
			}
			if(state.getHumidity() != null) {
				newState.setHumidity(state.getHumidity());
			}
			if(state.getDewPoint() != null) {
				newState.setDewPoint(state.getDewPoint());
			}
			if(state.getPressure() != null) {
				newState.setPressure(state.getPressure());
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
			if(state.getPrecipitationValue() != null) {
				newState.setPrecipitationValue(state.getPrecipitationValue());
			}
			if(state.getCloudLayers().size() > 0) {
				newState.setCloudLayers(state.getCloudLayers());
			}
			if(state.getWeatherConditions().size() > 0) {
				newState.setWeatherConditions(state.getWeatherConditions());
			}
		}
		
		printWeatherStates("Weather states after merging states for the same time", newWeatherStates);
		
		/* interpolate missing values */
		Integer lastTemperature = null;
		Integer lastHumidity = null;
		Integer lastDewPoint = null;
		Integer lastPressure = null;
		Integer lastWindSpeed = null;
		Integer lastWindDirection = null;
		Integer lastPrecipitationProbability = null;
		Integer lastPrecipitationValue = null;
		Integer lastCloudLayers = null;
		Integer lastWeatherConditions = null;
		
		for(int a=0; a<=maxHour; a++) {
			WeatherState currentState = newWeatherStates.get(a);
			
			if(currentState.getTemperature() != null) {
				float lastValue;
				int start;
				if(lastTemperature == null) {
					lastValue = currentState.getTemperature();
					start = 0;
				}
				else {
					lastValue = newWeatherStates.get(lastTemperature).getTemperature();
					start = lastTemperature + 1;
				}
				Float thisValue = currentState.getTemperature();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherStates.get(b).setTemperature(newValue);
				}
				
				lastTemperature = a;
			}
			
			if(currentState.getHumidity() != null) {
				float lastValue;
				int start;
				if(lastHumidity == null) {
					lastValue = currentState.getHumidity();
					start = 0;
				}
				else {
					lastValue = newWeatherStates.get(lastHumidity).getHumidity();
					start = lastHumidity + 1;
				}
				Float thisValue = currentState.getHumidity();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherStates.get(b).setHumidity(newValue);
				}
				
				lastHumidity = a;
			}
			
			if(currentState.getDewPoint() != null) {
				float lastValue;
				int start;
				if(lastDewPoint == null) {
					lastValue = currentState.getDewPoint();
					start = 0;
				}
				else {
					lastValue = newWeatherStates.get(lastDewPoint).getDewPoint();
					start = lastDewPoint + 1;
				}
				Float thisValue = currentState.getDewPoint();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherStates.get(b).setDewPoint(newValue);
				}
				
				lastDewPoint = a;
			}
			
			if(currentState.getPressure() != null) {
				float lastValue;
				int start;
				if(lastPressure == null) {
					lastValue = currentState.getPressure();
					start = 0;
				}
				else {
					lastValue = newWeatherStates.get(lastPressure).getPressure();
					start = lastPressure + 1;
				}
				Float thisValue = currentState.getPressure();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherStates.get(b).setPressure(newValue);
				}
				
				lastPressure = a;
			}
			
			if(currentState.getWindSpeed() != null) {
				float lastValue;
				int start;
				if(lastWindSpeed == null) {
					lastValue = currentState.getWindSpeed();
					start = 0;
				}
				else {
					lastValue = newWeatherStates.get(lastWindSpeed).getWindSpeed();
					start = lastWindSpeed + 1;
				}
				Float thisValue = currentState.getWindSpeed();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherStates.get(b).setWindSpeed(newValue);
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
					lastValue = newWeatherStates.get(lastWindDirection).getWindDirection();
					start = lastWindDirection + 1;
				}
				int thisValue = currentState.getWindDirection();
				for(int b=start; b<a; b++) {
					int newValue = lastValue + (thisValue - lastValue)/(a-b);
					newWeatherStates.get(b).setWindDirection(newValue);
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
					lastValue = newWeatherStates.get(lastPrecipitationProbability).getPrecipitationProbability();
					start = lastPrecipitationProbability + 1;
				}
				Float thisValue = currentState.getPrecipitationProbability();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherStates.get(b).setPrecipitationProbability(newValue);
				}
				
				lastPrecipitationProbability = a;
			}
			
			if(currentState.getPrecipitationValue() != null) {
				float lastValue;
				int start;
				if(lastPrecipitationValue == null) {
					lastValue = currentState.getPrecipitationValue();
					start = 0;
				}
				else {
					lastValue = newWeatherStates.get(lastPrecipitationValue).getPrecipitationValue();
					start = lastPrecipitationValue + 1;
				}
				Float thisValue = currentState.getPrecipitationValue();
				for(int b=start; b<a; b++) {
					Float newValue = Math.round((lastValue + (thisValue - lastValue)/(a-b))*100)/100f;
					newWeatherStates.get(b).setPrecipitationValue(newValue);
				}
				
				lastPrecipitationValue = a;
			}
			
			if(currentState.getCloudLayers().size() > 0) {
				List<CloudLayer> lastValue;
				int start;
				if(lastCloudLayers == null) {
					lastValue = currentState.getCloudLayers();
					start = 0;
				}
				else {
					lastValue = newWeatherStates.get(lastCloudLayers).getCloudLayers();
					start = lastCloudLayers + 1;
				}
				for(int b=start; b<a; b++) {
					newWeatherStates.get(b).setCloudLayers(lastValue);
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
					lastValue = newWeatherStates.get(lastWeatherConditions).getWeatherConditions();
					start = lastWeatherConditions + 1;
				}
				for(int b=start; b<a; b++) {
					newWeatherStates.get(b).setWeatherConditions(lastValue);
				}
				
				lastWeatherConditions = a;
			}
		}
		
		weatherStates = new ArrayList<WeatherState>();
		for(int a=0; a<=maxHour; a++) {
			if(forecastHours.contains(a)) {
				weatherStates.add(newWeatherStates.get(a));
			}
		}
		
		printWeatherStates("Final weather states after normalization", weatherStates);
	}
}
