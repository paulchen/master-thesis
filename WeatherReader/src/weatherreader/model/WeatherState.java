package weatherreader.model;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

//TODO javadoc
public class WeatherState {
	private Float temperatureValue;
	private Float humidityValue;
	private Float dewPointValue;
	private Float pressureValue;
	private Float windSpeed;
	private Integer windDirection;
	private Float precipitationProbability;
	private Float precipitationIntensity;
	private List<CloudLayer> cloudLayers;
	private List<WeatherCondition> weatherConditions;
	private SunPosition sunPosition;
	
	/*
	public WeatherState(Float temperatureValue,
			Float humidityValue, Float dewPointValue, Float pressureValue, Float windSpeed,
			Integer windDirection, Float precipitationProbability,
			Float precipitationIntensity, List<CloudLayer> cloudLayers,
			List<WeatherCondition> weatherConditions) {
		this(0, 0, temperatureValue, humidityValue, dewPointValue, pressureValue, windSpeed, windDirection,
				precipitationProbability, precipitationIntensity, cloudLayers, weatherConditions);
	}
	*/
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append("temperatureValue=" + temperatureValue + "; ");
		buffer.append("humidityValue=" + humidityValue + "; ");
		buffer.append("dewPointValue=" + dewPointValue + "; ");
		buffer.append("pressureValue=" + pressureValue + "; ");
		buffer.append("windSpeed=" + windSpeed + "; ");
		buffer.append("windDirection=" + windDirection + "; ");
		buffer.append("precipitationProbability=" + precipitationProbability + "; ");
		buffer.append("precipitationIntensity=" + precipitationIntensity + "; ");
		buffer.append("cloudLayers=" + cloudLayers.toString() + "; ");
		buffer.append("weatherConditions=" + weatherConditions.toString() + "; ");
		buffer.append("sunPosition=" + sunPosition);
		buffer.append("]");
		return buffer.toString();
	}

	public WeatherState(Float temperatureValue,
			Float humidityValue, Float dewPointValue, Float pressureValue, Float windSpeed,
			Integer windDirection, Float precipitationProbability,
			Float precipitationIntensity, List<CloudLayer> cloudLayers,
			List<WeatherCondition> weatherConditions) {
		super();
		
		this.temperatureValue = temperatureValue;
		this.humidityValue = humidityValue;
		this.dewPointValue = dewPointValue;
		this.pressureValue = pressureValue;
		this.windSpeed = windSpeed;
		this.windDirection = windDirection;
		this.precipitationProbability = precipitationProbability;
		this.precipitationIntensity = precipitationIntensity;
		this.cloudLayers = cloudLayers;
		this.weatherConditions = weatherConditions;
	}
	
	protected void setTemperatureValue(Float temperatureValue) {
		this.temperatureValue = temperatureValue;
	}

	protected void setHumidityValue(Float humidityValue) {
		this.humidityValue = humidityValue;
	}

	protected void setDewPointValue(Float dewPointValue) {
		this.dewPointValue = dewPointValue;
	}

	protected void setPressureValue(Float pressureValue) {
		this.pressureValue = pressureValue;
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

	protected void setPrecipitationIntensity(Float precipitationIntensity) {
		this.precipitationIntensity = precipitationIntensity;
	}

	protected void setCloudLayers(List<CloudLayer> cloudLayers) {
		this.cloudLayers = cloudLayers;
	}

	protected void setWeatherConditions(List<WeatherCondition> weatherConditions) {
		this.weatherConditions = weatherConditions;
	}

	protected void setSunPosition(SunPosition position) {
		this.sunPosition = position;
	}

	// TODO move previousState to WeatherReport
	public void createIndividuals(OntModel onto, Individual weatherObservation, int stateIndex /*, WeatherState previousState */ ) {
		OntClass weatherStateClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherState");
		Individual weatherState = onto.createIndividual(WeatherReport.NAMESPACE + "weatherState" + stateIndex, weatherStateClass);
		// TODO move to WeatherReport
		/*
		OntClass sensorSourceClass = onto.getOntClass(WeatherReport.NAMESPACE + "ServiceSource");
		Individual sourceIndividual = onto.createIndividual(WeatherReport.NAMESPACE + source, sensorSourceClass);

		onto.add(onto.createStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasSource"), sourceIndividual));
		onto.add(onto.createLiteralStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasPriority"), priority));
		
		OntClass hourClass = onto.getOntClass(WeatherReport.NAMESPACE + "Hour");
		Individual hour = onto.createIndividual(WeatherReport.NAMESPACE + "hour" + startDate, hourClass);
		onto.add(onto.createLiteralStatement(hour, onto.getProperty(WeatherReport.TIME + "hours"), new BigDecimal(startDate)));
		
		Resource intervalClass = onto.getResource(WeatherReport.TIME + "Interval");
		Individual interval = onto.createIndividual(WeatherReport.NAMESPACE + "interval" + startDate, intervalClass);
		onto.add(onto.createStatement(interval, onto.getProperty(WeatherReport.TIME + "hasDurationDescription"), hour));
		onto.add(onto.createStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasTime"), interval));
		*/
		
		OntClass weatherPhenomenonClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherPhenomenon");
		if(temperatureValue != null) {
			Resource blankNode = onto.createResource();
			onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), temperatureValue));
			// TODO get rid of magic constant for individual name here
			onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/temperature/degree-Celsius")));
			
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "temperature" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasTemperatureValue"), blankNode));
		}
		if(humidityValue != null) {
			Resource blankNode = onto.createResource();
			onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), humidityValue));
			// TODO get rid of magic constant for individual name here
			onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/fraction/percent")));
			
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "humidity" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasHumidityValue"), blankNode));
		}
		if(dewPointValue != null) {
			Resource blankNode = onto.createResource();
			onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), temperatureValue));
			// TODO get rid of magic constant for individual name here
			onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/temperature/degree-Celsius")));
			
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "dewPoint" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasDewPointValue"), blankNode));
		}
		if(pressureValue != null) {
			Resource blankNode = onto.createResource();
			onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), pressureValue));
			onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "hectopascal")));
			
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "pressure" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasPressureValue"), blankNode));
		}
		if(windSpeed != null || windDirection != null) {
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "wind" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			if(windSpeed != null) {
				Resource blankNode1 = onto.createResource();
				onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), windSpeed));
				onto.add(onto.createStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "metresPerSecond")));
				
				onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasWindSpeed"), blankNode1));
			}
			if(windDirection != null) {
				Resource blankNode2 = onto.createResource();
				onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), windDirection));
				// TODO get rid of magic constant for individual name here
				onto.add(onto.createStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree")));
				
				onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasWindDirection"), blankNode2));
			}
		}
		if(precipitationProbability != null || precipitationIntensity != null) {
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "precipitation" + stateIndex, weatherPhenomenonClass);
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			
			if(precipitationProbability != null) {
				Resource blankNode = onto.createResource();
				onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), precipitationProbability));
				onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "millimetresPerHour")));
				
				onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasPrecipitationProbability"), blankNode));
//				onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasPrecipitationIntensity"), precipitationProbability));
			}
			if(precipitationIntensity != null) {
				Resource blankNode = onto.createResource();
				onto.add(onto.createLiteralStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), precipitationIntensity));
				// TODO get rid of magic constant for individual name here
				onto.add(onto.createStatement(blankNode, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/fraction/percent")));
				
				onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasPrecipitationIntensity"), blankNode));
//				onto.add(onto.createLiteralStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasPrecipitationValue"), precipitationIntensity));
			}
		}
		
		if(sunPosition != null) {
			Individual weatherPhenomenon = onto.createIndividual(WeatherReport.NAMESPACE + "sunPosition" + stateIndex, weatherPhenomenonClass);
			
			Resource blankNode1 = onto.createResource();
			onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), (int)sunPosition.getAzimuth()));
			// TODO get rid of magic constant for individual name here
			onto.add(onto.createStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree")));
			
			Resource blankNode2 = onto.createResource();
			onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), (float)sunPosition.getElevation()));
			// TODO get rid of magic constant for individual name here
			onto.add(onto.createStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree")));
			
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasSunDirection"), blankNode1));
			onto.add(onto.createStatement(weatherPhenomenon, onto.getProperty(WeatherReport.NAMESPACE + "hasSunElevationAngle"), blankNode2));
		}
		
		int index = 0;
		for(CloudLayer cloudLayer : cloudLayers) {
			Resource blankNode1 = onto.createResource();
			onto.add(onto.createLiteralStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), cloudLayer.getCoverage()));
			onto.add(onto.createStatement(blankNode1, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource(WeatherReport.NAMESPACE + "okta")));
			
			Resource blankNode2 = onto.createResource();
			onto.add(onto.createLiteralStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "numericalValue"), cloudLayer.getAltitude()));
			onto.add(onto.createStatement(blankNode2, onto.getProperty(WeatherReport.MUO_NAMESPACE + "measuredIn"), onto.getResource("http://purl.oclc.org/NET/muo/ucum/unit/length/meter")));
			
			Individual cloudLayerIndividual = onto.createIndividual(WeatherReport.NAMESPACE + "cloudLayer" + stateIndex + "_" + index, weatherPhenomenonClass);
			onto.add(onto.createStatement(cloudLayerIndividual, onto.getProperty(WeatherReport.NAMESPACE + "belongsToState"), weatherState));
			onto.add(onto.createLiteralStatement(cloudLayerIndividual, onto.getProperty(WeatherReport.NAMESPACE + "hasCloudCover"), blankNode1));
			onto.add(onto.createLiteralStatement(cloudLayerIndividual, onto.getProperty(WeatherReport.NAMESPACE + "hasCloudAltitude"), blankNode2));
			index++;
		}
		
		for(WeatherCondition condition : weatherConditions) {
			onto.add(onto.createStatement(weatherState, onto.getProperty(WeatherReport.NAMESPACE + "hasCondition"), onto.getIndividual(WeatherReport.NAMESPACE + condition.toString())));
		}
		
		/*
		if(previousState != null) {
			Individual previousStateIndividual = onto.getIndividual(WeatherReport.NAMESPACE + "weather" + (stateIndex-1));
			onto.add(onto.createStatement(previousStateIndividual, onto.getProperty(WeatherReport.NAMESPACE + "hasNextWeatherState"), weatherState));
		}
		*/
	}

	public Float getTemperatureValue() {
		return temperatureValue;
	}

	public Float getHumidityValue() {
		return humidityValue;
	}

	public Float getDewPointValue() {
		return dewPointValue;
	}

	public Float getPressureValue() {
		return pressureValue;
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

	public Float getPrecipitationIntensity() {
		return precipitationIntensity;
	}

	public List<CloudLayer> getCloudLayers() {
		return cloudLayers;
	}

	public List<WeatherCondition> getWeatherConditions() {
		return weatherConditions;
	}
}
