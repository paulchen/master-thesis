package at.ac.tuwien.auto.thinkhome.weatherimporter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import at.ac.tuwien.auto.thinkhome.weatherimporter.model.CloudCover;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.DewPoint;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.GeographicalPosition;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Humidity;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Precipitation;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Pressure;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.ServiceSource;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Temperature;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Weather;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherCondition;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherPhenomenon;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherState;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Wind;

import com.hp.hpl.jena.ontology.OntModel;

// TODO move to WeatherImpoter
// TODO modular style (several jars: data model, executable file, actual importer for a certain weather service)
// TODO javadoc
// TODO additional modes: change current weather state, remove/replace data
// TODO namespace
// TODO pluggable import package
public class WeatherReader {
	private static final String source = "yr_no";
	private static final int priority = 421;
	private static final String feedUrl = "http://api.yr.no/weatherapi/locationforecast/1.8/?lat=%lat;lon=%lon;msl=%alt";
	private static final String schemaLocation = "http://api.yr.no/weatherapi/locationforecast/1.8/schema";
	
	private float latitude;
	private float longitude;
	private int altitude;
	
	private int lowCloudAltitude;
	private int mediumCloudAltitude;
	private int highCloudAltitude;
	
	private List<Integer> forecastHours;
	private int forecastPeriod;
	
	private Weather weather;
	
	private Logger log;
	
	private int reportIndex;
	
	public WeatherReader(float latitude, float longitude, int altitude, int lowCloudAltitude, int mediumCloudAltitude, int highCloudAltitude, List<Integer> forecastHours) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		
		this.lowCloudAltitude = lowCloudAltitude;
		this.mediumCloudAltitude = mediumCloudAltitude;
		this.highCloudAltitude = highCloudAltitude;
		
		this.forecastHours = new ArrayList<Integer>(forecastHours);
		Collections.sort(this.forecastHours);
		forecastPeriod = forecastHours.get(forecastHours.size() - 1)*3600000;
		
		log = Logger.getLogger(WeatherReader.class);
		
		reportIndex = 0;
	}
	
	private void xmlError(String message) throws WeatherReaderException {
		error("Error in XML input: " + message);
	}

	private void error(String message) throws WeatherReaderException {
		log.error(message);
		throw new WeatherReaderException(message);
	}
	
	private void checkAttributes(Node node, String... requiredAttributes) throws WeatherReaderException {
		for(String requiredAttribute : requiredAttributes) {
			if(node.getAttributes().getNamedItem(requiredAttribute) == null) {
				xmlError("The node <" + node.getLocalName() + "> node the required attribute '" + requiredAttribute + "'");
			}
		}
	}
	
	private void processWeatherState(Node node) throws WeatherReaderException {
		reportIndex++;
		
		NamedNodeMap attributes = node.getAttributes();
		
		Date startDate = DatatypeConverter.parseDateTime(attributes.getNamedItem("from").getNodeValue()).getTime();
		Date endDate = DatatypeConverter.parseDateTime(attributes.getNamedItem("to").getNodeValue()).getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.debug("Processing <time> element for time period from " + sdf.format(startDate) + " to " + sdf.format(endDate));
		
		/* cut off everything that's too far in the future */
		if(startDate.getTime() - new Date().getTime() > forecastPeriod*2) {
			return;
		}
		if(endDate.getTime() - new Date().getTime() > forecastPeriod*2) {
			endDate = new Date(new Date().getTime() + forecastPeriod*2);
		}
		
		Float temperatureValue = null;
		Float humidityValue = null;
		Float dewPointValue = null;
		Float pressureValue = null;
		Float windSpeed = null;
		Integer windDirection = null;
		Float precipitationProbability = null;
		Float precipitationIntensity = null;
		List<CloudCover> cloudLayers = new ArrayList<CloudCover>();
		
		NodeList child = (NodeList)node.getChildNodes();
		NodeList children = null;
		for(int b=0; b<child.getLength(); b++) {
			switch(child.item(b).getNodeType()) {
				case Node.ELEMENT_NODE:
					if(!child.item(b).getLocalName().equals("location")) {
						xmlError("The only child element a <time> element may have must be <location>.");
					}
					children = (NodeList)child.item(b).getChildNodes();
					break;

				case Node.TEXT_NODE:
					if(!child.item(b).getTextContent().trim().isEmpty()) {
						xmlError("The <time> element may not have text content.");
					}
					break;
					
				default:
					xmlError("The <time> element may not have non-element child nodes.");
			}
		}
		
		if(children == null) {
			xmlError("The <time> element has no child node <location>.");
		}
		
		CloudCover lowClouds = null;
		CloudCover mediumClouds = null;
		CloudCover highClouds = null;
		List<WeatherCondition> weatherConditions = null;
		
		for(int b=0; b<children.getLength(); b++) {
			Node childNode = children.item(b);
			switch(childNode.getNodeType()) {
				case Node.ELEMENT_NODE:
					int coverage;
					if(childNode.getLocalName().equals("temperature")) {
						checkDuplicate(childNode, temperatureValue);
						checkAttributes(childNode, "unit", "value");
						if(!childNode.getAttributes().getNamedItem("unit").getNodeValue().equals("celcius")) {
							xmlError("The temperature unit must be 'celcius'.");
						}
						temperatureValue = Math.round(Float.parseFloat(childNode.getAttributes().getNamedItem("value").getNodeValue())*100)/100f;
					}		
					else if(childNode.getLocalName().equals("windDirection")) {
						checkDuplicate(childNode, windDirection);
						checkAttributes(childNode, "deg");
						windDirection = (int)Float.parseFloat(childNode.getAttributes().getNamedItem("deg").getNodeValue());
					}		
					else if(childNode.getLocalName().equals("windSpeed")) {
						checkDuplicate(childNode, windSpeed);
						checkAttributes(childNode, "mps");
						windSpeed = Math.round(Float.parseFloat(childNode.getAttributes().getNamedItem("mps").getNodeValue())*10)/10f;
					}		
					else if(childNode.getLocalName().equals("humidity")) {
						checkDuplicate(childNode, humidityValue);
						checkAttributes(childNode, "unit", "value");
						if(!childNode.getAttributes().getNamedItem("unit").getNodeValue().equals("percent")) {
							xmlError("The humidity unit must be 'percent'.");
						}
						humidityValue = Math.round(Float.parseFloat(childNode.getAttributes().getNamedItem("value").getNodeValue()))/100f;
					}		
					else if(childNode.getLocalName().equals("pressure")) {
						checkDuplicate(childNode, pressureValue);
						checkAttributes(childNode, "unit", "value");
						if(!childNode.getAttributes().getNamedItem("unit").getNodeValue().equals("hPa")) {
							xmlError("The pressure unit must be 'hPa'.");
						}
						pressureValue = Math.round(Float.parseFloat(childNode.getAttributes().getNamedItem("value").getNodeValue())*100)/100f;
					}		
					else if(childNode.getLocalName().equals("cloudiness")) {
						/* will be ignored */
					}		
					else if(childNode.getLocalName().equals("fog")) {
						/* will be ignored */
					}
					else if(childNode.getLocalName().equals("lowClouds")) {
						checkDuplicate(childNode, lowClouds);
						checkAttributes(childNode, "percent");
						coverage = Math.round(Float.parseFloat(childNode.getAttributes().getNamedItem("percent").getNodeValue())*8/100);
						if(coverage > 0) {
							lowClouds = new CloudCover("clouds" + reportIndex + "_low", lowCloudAltitude, coverage);
							cloudLayers.add(lowClouds);
						}
					}		
					else if(childNode.getLocalName().equals("mediumClouds")) {
						checkDuplicate(childNode, mediumClouds);
						checkAttributes(childNode, "percent");
						coverage = Math.round(Float.parseFloat(childNode.getAttributes().getNamedItem("percent").getNodeValue())*8/100);
						if(coverage > 0) {
							mediumClouds = new CloudCover("clouds" + reportIndex + "_medium", mediumCloudAltitude, coverage);
							cloudLayers.add(mediumClouds);
						}
					}		
					else if(childNode.getLocalName().equals("highClouds")) {
						checkDuplicate(childNode, highClouds);
						checkAttributes(childNode, "percent");
						coverage = Math.round(Float.parseFloat(childNode.getAttributes().getNamedItem("percent").getNodeValue())*8/100);
						if(coverage > 0) {
							highClouds = new CloudCover("clouds" + reportIndex + "_high", highCloudAltitude, coverage);
							cloudLayers.add(highClouds);
						}
					}		
					else if(childNode.getLocalName().equals("precipitation")) {
						checkDuplicate(childNode, precipitationIntensity);
						checkAttributes(childNode, "unit", "value");
						if(!childNode.getAttributes().getNamedItem("unit").getNodeValue().equals("mm")) {
							xmlError("The unit for the precipitation value must be 'mm'.");
						}
						precipitationIntensity = Math.round(Float.parseFloat(childNode.getAttributes().getNamedItem("value").getNodeValue())*100)/100f;
						precipitationProbability = 0f;
						if(precipitationIntensity > 0) {
							precipitationProbability = 1f;
						}
					}		
					else if(childNode.getLocalName().equals("symbol")) {
						checkDuplicate(childNode, weatherConditions);
						checkAttributes(childNode, "id", "number");
						try {
							weatherConditions = getWeatherConditions(Integer.parseInt(childNode.getAttributes().getNamedItem("number").getNodeValue()));
						}
						catch(NumberFormatException e) {
							xmlError("Invalid value for symbol number: " + childNode.getAttributes().getNamedItem("number").getNodeValue());
						}
					}
					else {
						xmlError("The <location> element has an unknown child element <" + childNode.getLocalName() + ">.");
					}
					
					break;
					
				case Node.TEXT_NODE:
					if(!childNode.getTextContent().trim().isEmpty()) {
						xmlError("The <location> element may not have text content.");
					}
					break;
					
				default:
					xmlError("The <location> element may not have non-element child nodes.");
			}
		}
		
		if(lowClouds == null && mediumClouds == null && highClouds == null) {
			cloudLayers.add(new CloudCover("clouds" + reportIndex, mediumCloudAltitude, 0));
		}
		if(humidityValue != null && temperatureValue != null) {
			dewPointValue = temperatureValue - 20*(1-humidityValue);
		}
		
		if(weatherConditions == null) {
			weatherConditions = new ArrayList<WeatherCondition>();
		}
		
		List<WeatherPhenomenon> weatherPhenomena = new ArrayList<WeatherPhenomenon>();
		if(temperatureValue != null) {
			weatherPhenomena.add(new Temperature("temperature" + reportIndex, temperatureValue));
		}
		if(humidityValue != null) {
			weatherPhenomena.add(new Humidity("humidity" + reportIndex, humidityValue));
		}
		if(dewPointValue != null) {
			weatherPhenomena.add(new DewPoint("dewPoint" + reportIndex, dewPointValue));
		}
		if(pressureValue != null) {
			weatherPhenomena.add(new Pressure("pressure" + reportIndex, pressureValue));
		}
		if(windSpeed != null && windDirection != null) {
			weatherPhenomena.add(new Wind("wind" + reportIndex, windSpeed, windDirection));
		}
		if(precipitationProbability != null && precipitationIntensity != null) {
			weatherPhenomena.add(new Precipitation("precipitation" + reportIndex, precipitationIntensity, precipitationProbability));
		}
		
		for(CloudCover cloudLayer : cloudLayers) {
			weatherPhenomena.add(cloudLayer);
		}
		
		WeatherState weatherState = new WeatherState("weatherState" + reportIndex, weatherPhenomena, weatherConditions);
		weatherState.mergePhenomena(String.valueOf(reportIndex));
		weather.newWeatherReport(startDate, endDate, weatherState);
	}
	
	private List<WeatherCondition> getWeatherConditionList(WeatherCondition... conditions) {
		List<WeatherCondition> list = new ArrayList<WeatherCondition>();
		
		for(WeatherCondition condition : conditions) {
			list.add(condition);
		}
		
		return list;
	}
	private List<WeatherCondition> getWeatherConditions(int number) throws WeatherReaderException {
		Map<Integer, List<WeatherCondition>> weatherConditions = new HashMap<Integer, List<WeatherCondition>>();
		
		/* according to http://api.yr.no/faq.html */
		weatherConditions.put(1, getWeatherConditionList(WeatherCondition.Sun));
		weatherConditions.put(2, getWeatherConditionList(WeatherCondition.LightCloud));
		weatherConditions.put(3, getWeatherConditionList(WeatherCondition.PartlyCloud));
		weatherConditions.put(4, getWeatherConditionList(WeatherCondition.Cloud));
		weatherConditions.put(5, getWeatherConditionList(WeatherCondition.Rain, WeatherCondition.Sun));
		weatherConditions.put(6, getWeatherConditionList(WeatherCondition.Rain, WeatherCondition.Sun, WeatherCondition.Thunder));
		weatherConditions.put(7, getWeatherConditionList(WeatherCondition.Sleet, WeatherCondition.Sun));
		weatherConditions.put(8, getWeatherConditionList(WeatherCondition.Snow, WeatherCondition.Sun));
		weatherConditions.put(9, getWeatherConditionList(WeatherCondition.Rain));
		weatherConditions.put(10, getWeatherConditionList(WeatherCondition.Rain));
		weatherConditions.put(11, getWeatherConditionList(WeatherCondition.Rain, WeatherCondition.Thunder));
		weatherConditions.put(12, getWeatherConditionList(WeatherCondition.Sleet));
		weatherConditions.put(13, getWeatherConditionList(WeatherCondition.Snow));
		weatherConditions.put(14, getWeatherConditionList(WeatherCondition.Snow, WeatherCondition.Thunder));
		weatherConditions.put(15, getWeatherConditionList(WeatherCondition.Fog));
		weatherConditions.put(16, getWeatherConditionList(WeatherCondition.Sun));
		weatherConditions.put(17, getWeatherConditionList(WeatherCondition.LightCloud));
		weatherConditions.put(18, getWeatherConditionList(WeatherCondition.Rain, WeatherCondition.Sun));
		weatherConditions.put(19, getWeatherConditionList(WeatherCondition.Snow, WeatherCondition.Sun));
		weatherConditions.put(20, getWeatherConditionList(WeatherCondition.Sleet, WeatherCondition.Sun, WeatherCondition.Thunder));
		weatherConditions.put(21, getWeatherConditionList(WeatherCondition.Snow, WeatherCondition.Sun, WeatherCondition.Thunder));
		weatherConditions.put(22, getWeatherConditionList(WeatherCondition.Rain, WeatherCondition.Thunder));
		weatherConditions.put(23, getWeatherConditionList(WeatherCondition.Sleet, WeatherCondition.Thunder));
		
		if(!weatherConditions.containsKey(number)) {
			throw new WeatherReaderException("Unknown weather condition number: " + number);
		}
		return weatherConditions.get(number);
	}

	private void checkDuplicate(Node childNode, Object item) throws WeatherReaderException {
		if(item != null) {
			String elementName = childNode.getLocalName();
			xmlError("A <location> element has more than one <" + elementName + "> child elements.");
		}
	}

	public void process() throws WeatherReaderException {
		String url = new String(feedUrl);
		url = url.replaceAll("%lat", String.valueOf(latitude));
		url = url.replaceAll("%lon", String.valueOf(longitude));
		url = url.replaceAll("%alt", String.valueOf(altitude));
		log.info("Retrieving weather data from URL: " + url);
		
		NodeList nodes;
		try {
			HttpClient http = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			request.setHeader("User-Agent", "ThinkHome weather component - see https://www.auto.tuwien.ac.at/projectsites/thinkhome/overview.html");
			HttpResponse response = http.execute(request);
			if(response.getStatusLine().getStatusCode() >= 400 && response.getStatusLine().getStatusCode() < 500) {
				String error = "Could not fetch weather feed: " + response.getStatusLine().getReasonPhrase();
				log.error(error);
				throw new WeatherReaderException(error);
			}
			HttpEntity entity = response.getEntity();
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document document = builder.parse(entity.getContent());
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new URL(schemaLocation));
			schema.newValidator().validate(new DOMSource(document));
			
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression datapointExpression = xpath.compile("/weatherdata/product/time");
			
			weather = new Weather(new Date(), priority, new ServiceSource(source), new GeographicalPosition(latitude, longitude, altitude), forecastHours);
			nodes = (NodeList)datapointExpression.evaluate(document, XPathConstants.NODESET);
		}
		catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			throw new WeatherReaderException(e);
		}
		catch (ParserConfigurationException e) {
			log.error(e.getMessage());
			throw new WeatherReaderException(e);
		}
		catch (SAXException e) {
			log.error(e.getMessage());
			throw new WeatherReaderException(e);
		}
		catch (IOException e) {
			log.error(e.getMessage());
			throw new WeatherReaderException(e);
		}
		catch (XPathExpressionException e) {
			log.error(e.getMessage());
			throw new WeatherReaderException(e);
		}
		
		log.debug("Found " + nodes.getLength() + " <time> nodes.");
		
		
		if(nodes.getLength() == 0) {
			xmlError("No <time> nodes found.");
		}

		for(int a=0; a<nodes.getLength(); a++) {
			processWeatherState(nodes.item(a));
		}

		weather.normalizeWeatherReports();
		
		log.info("Importing weather data completed");
	}
	
	public void createIndividuals(OntModel ontology) {
		weather.createIndividuals(ontology);
	}
}
