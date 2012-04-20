package weatherreader.model;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

//TODO javadoc
public class WeatherState {
	private float startDate;
	private float endDate;
	private Float temperature;
	private Float humidity;
	private Float dewPoint;
	private Float pressure;
	private Float windSpeed;
	private Integer windDirection;
	private Float precipitationProbability;
	private Float precipitationValue;
	private List<CloudLayer> cloudLayers;
	private List<WeatherCondition> weatherConditions;
	private String source;
	private int priority;
	
	public WeatherState(Date startDate, Date endDate, Float temperature,
			Float humidity, Float dewPoint, Float pressure, Float windSpeed,
			Integer windDirection, Float precipitationProbability,
			Float precipitationValue, List<CloudLayer> cloudLayers,
			List<WeatherCondition> weatherConditions, String source, int priority) {
		this(0, 0, temperature, humidity, dewPoint, pressure, windSpeed, windDirection, precipitationProbability, precipitationValue, cloudLayers, weatherConditions, source, priority);
		
		long startSeconds = startDate.getTime()-new Date().getTime();
		this.startDate = (float)Math.round(startSeconds/3600000);
		if(this.startDate < 0) {
			/* ignore data for the past */
			this.startDate = 0;
		}
		long endSeconds = endDate.getTime()-new Date().getTime();
		this.endDate = (float)Math.round(endSeconds/3600000);
		if(this.endDate < 0) {
			/* ignore data for the past */
			this.endDate = 0;
		}		
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append("startDate=" + startDate + "; ");
		buffer.append("endDate=" + endDate + "; ");
		buffer.append("temperature=" + temperature + "; ");
		buffer.append("humidity=" + humidity + "; ");
		buffer.append("dewPoint=" + dewPoint + "; ");
		buffer.append("pressure=" + pressure + "; ");
		buffer.append("windSpeed=" + windSpeed + "; ");
		buffer.append("windDirection=" + windDirection + "; ");
		buffer.append("precipitationProbability=" + precipitationProbability + "; ");
		buffer.append("precipitationValue=" + precipitationValue + "; ");
		buffer.append("cloudLayers=" + cloudLayers.toString() + "; ");
		buffer.append("weatherConditions=" + weatherConditions.toString());
		buffer.append("]");
		return buffer.toString();
	}

	public WeatherState(float startDate, float endDate, Float temperature,
			Float humidity, Float dewPoint, Float pressure, Float windSpeed,
			Integer windDirection, Float precipitationProbability,
			Float precipitationValue, List<CloudLayer> cloudLayers,
			List<WeatherCondition> weatherConditions, String source, int priority) {
		super();
		
		this.startDate = startDate;
		this.endDate = endDate;
		this.temperature = temperature;
		this.humidity = humidity;
		this.dewPoint = dewPoint;
		this.pressure = pressure;
		this.windSpeed = windSpeed;
		this.windDirection = windDirection;
		this.precipitationProbability = precipitationProbability;
		this.precipitationValue = precipitationValue;
		this.cloudLayers = cloudLayers;
		this.weatherConditions = weatherConditions;
		this.source = source;
		this.priority = priority;
	}
	
	protected void setStartDate(float startDate) {
		this.startDate = startDate;
	}

	protected void setEndDate(float endDate) {
		this.endDate = endDate;
	}

	protected void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	protected void setHumidity(Float humidity) {
		this.humidity = humidity;
	}

	protected void setDewPoint(Float dewPoint) {
		this.dewPoint = dewPoint;
	}

	protected void setPressure(Float pressure) {
		this.pressure = pressure;
	}

	protected void setWindSpeed(Float windSpeed) {
		this.windSpeed = windSpeed;
	}

	protected void setWindDirection(Integer windDirection) {
		this.windDirection = windDirection;
	}

	protected void setPrecipitationProbability(Float precipitationProbability) {
		this.precipitationProbability = precipitationProbability;
	}

	protected void setPrecipitationValue(Float precipitationValue) {
		this.precipitationValue = precipitationValue;
	}

	protected void setCloudLayers(List<CloudLayer> cloudLayers) {
		this.cloudLayers = cloudLayers;
	}

	protected void setWeatherConditions(List<WeatherCondition> weatherConditions) {
		this.weatherConditions = weatherConditions;
	}

	protected void setSource(String source) {
		this.source = source;
	}

	protected void setPriority(int priority) {
		this.priority = priority;
	}

	public void createIndividuals(OntModel onto, Individual weatherObservation, int stateIndex, WeatherState previousState) {
		OntClass sensorSourceClass = onto.getOntClass(WeatherReport.NAMESPACE + "ServiceSource");
		Individual sourceIndividual = onto.createIndividual(WeatherReport.NAMESPACE + source, sensorSourceClass);

		OntClass weatherStateClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherState");
		Individual weatherState = onto.createIndividual(WeatherReport.NAMESPACE + "weather" + stateIndex, weatherStateClass);
		onto.add(onto.createStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasSource"), sourceIndividual));
		onto.add(onto.createLiteralStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasPriority"), priority));

		OntClass hourClass = onto.getOntClass(WeatherReport.NAMESPACE + "Hour");
		Individual hour = onto.createIndividual(WeatherReport.NAMESPACE + "hour" + startDate, hourClass);
		onto.add(onto.createLiteralStatement(hour, onto.getProperty(WeatherReport.TIME + "hours"), new BigDecimal(startDate)));
		
		Resource intervalClass = onto.getResource(WeatherReport.TIME + "Interval");
		Individual interval = onto.createIndividual(WeatherReport.NAMESPACE + "interval" + startDate, intervalClass);
		onto.add(onto.createStatement(interval, onto.getProperty(WeatherReport.TIME + "hasDurationDescription"), hour));
		onto.add(onto.createStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasTime"), interval));
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		if(temperature != null) {
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "temperature" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasTemperature"), temperature));
		}
		if(humidity != null) {
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "humidity" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasHumidity"), humidity));
		}
		if(dewPoint != null) {
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "dewPoint" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasDewPoint"), dewPoint));
		}
		if(pressure != null) {
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "pressure" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasPressure"), pressure));
		}
		if(windSpeed != null || windDirection != null) {
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "wind" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			if(windSpeed != null) {
				onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasWindSpeed"), windSpeed));
			}
			if(windDirection != null) {
				onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasWindDirection"), windDirection));
			}
		}
		if(precipitationProbability != null || precipitationValue != null) {
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "precipitation" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			if(precipitationProbability != null) {
				onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasPrecipitationProbability"), precipitationProbability));
			}
			if(precipitationValue != null) {
				onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasPrecipitationValue"), precipitationValue));
			}
		}
		
		int index = 0;
		for(CloudLayer cloudLayer : cloudLayers) {
			Individual cloudLayerIndividual = onto.createIndividual(WeatherReport.NAMESPACE + "cloudLayer" + stateIndex + "_" + index, weatherPhenomenonClass);
			onto.add(onto.createStatement(cloudLayerIndividual, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			onto.add(onto.createLiteralStatement(cloudLayerIndividual, onto.getProperty(WeatherReport.NAMESPACE + "hasCloudCover"), cloudLayer.getCoverage()));
			onto.add(onto.createLiteralStatement(cloudLayerIndividual, onto.getProperty(WeatherReport.NAMESPACE + "hasCloudAltitude"), cloudLayer.getAltitude()));
			index++;
		}
		
		for(WeatherCondition condition : weatherConditions) {
			onto.add(onto.createStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasCondition"), onto.getIndividual(WeatherReport.NAMESPACE + condition.toString())));
		}
		
		if(previousState != null) {
			Individual previousStateIndividual = onto.getIndividual(WeatherReport.NAMESPACE + "weather" + (stateIndex-1));
			onto.add(onto.createStatement(previousStateIndividual, onto.getProperty(WeatherReport.NAMESPACE + "hasNextWeatherState"), weatherState));
		}
	}

	public float getStartDate() {
		return startDate;
	}

	public float getEndDate() {
		return endDate;
	}

	public Float getTemperature() {
		return temperature;
	}

	public Float getHumidity() {
		return humidity;
	}

	public Float getDewPoint() {
		return dewPoint;
	}

	public Float getPressure() {
		return pressure;
	}

	public Float getWindSpeed() {
		return windSpeed;
	}

	public Integer getWindDirection() {
		return windDirection;
	}

	public Float getPrecipitationProbability() {
		return precipitationProbability;
	}

	public Float getPrecipitationValue() {
		return precipitationValue;
	}

	public List<CloudLayer> getCloudLayers() {
		return cloudLayers;
	}

	public List<WeatherCondition> getWeatherConditions() {
		return weatherConditions;
	}

	public String getSource() {
		return source;
	}

	public int getPriority() {
		return priority;
	}
}
