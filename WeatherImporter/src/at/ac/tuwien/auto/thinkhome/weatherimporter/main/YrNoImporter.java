package at.ac.tuwien.auto.thinkhome.weatherimporter.main;

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
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherReport;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherSource;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.WeatherState;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Wind;

// TODO additional modes: change current weather state, remove/replace data
// TODO namespace
/**
 * This is an implementation of the interface {@link Importer} which imports
 * current and future weather data from the Weather service at api.yr.no.
 * 
 * @author Paul Staroch
 */
public class YrNoImporter implements Importer {
	/**
	 * Name of the source to be stored in the instances of {@link WeatherSource}
	 * that are created during the import
	 */
	private static final String source = "yr_no";

	/**
	 * URL of the XML feed where the weather data is obtained from;
	 * <tt>%lat<tt> (for the latitude), <tt>%lon</tt> (for the longitude) and
	 * <tt>%alt</tt> will automatically be replaced by the respective values
	 * when accessing the XML feed.
	 */
	private static final String feedUrl = "http://api.yr.no/weatherapi/locationforecast/1.8/?lat=%lat;lon=%lon;msl=%alt";

	/**
	 * URL of the XML schema the XML document returned by the API can be
	 * validated against
	 */
	private static final String schemaLocation = "http://api.yr.no/weatherapi/locationforecast/1.8/schema";

	/**
	 * altitude for the cloud layer; specified in the properties file
	 */
	private int cloudAltitude;

	/**
	 * Point of time weather data shall be fetched until, given in hours
	 * relative to the current time. The value is determined from maximum value
	 * stored in the array parameter <tt>forecastHours</tt> of the method
	 * {@link #fetchWeather(GeographicalPosition, WeatherImporterProperties, List)}
	 */
	private int forecastPeriod;

	/**
	 * Object that contains all data that has been imported from the weather
	 * service
	 */
	private Weather weather;

	/**
	 * Apache Log4j logger
	 */
	private Logger log;

	/**
	 * Index for the consecutive numbering of all instances of
	 * {@link WeatherReport} being created during the import
	 */
	private int reportIndex;

	/**
	 * The constructor
	 */
	public YrNoImporter() {
		log = Logger.getLogger(YrNoImporter.class);
	}

	/**
	 * Reports an error in the XML document returned by the weather service
	 * 
	 * @param message
	 *            message to be used for reporting the error to Log4j and for
	 *            creating an exception
	 * @throws WeatherImporterException
	 *             in every case
	 */
	private void xmlError(String message) throws WeatherImporterException {
		error("Error in XML input: " + message);
	}

	/**
	 * Reports an error via Log4j and by throwing an exception
	 * 
	 * @param message
	 *            message to be used for reporting the error to Log4j and for
	 *            creating an exception
	 * @throws WeatherImporterException
	 *             in every case
	 */
	private void error(String message) throws WeatherImporterException {
		log.error(message);
		throw new WeatherImporterException(message);
	}

	/**
	 * Checks if a node possesses all attributes that it should possess
	 * 
	 * @param node
	 *            node to be checked for required attributed
	 * @param requiredAttributes
	 *            list of attribute names the node should possess
	 * @throws WeatherImporterException
	 *             if at least one attribute is missing
	 */
	private void checkAttributes(Node node, String... requiredAttributes)
			throws WeatherImporterException {
		for (String requiredAttribute : requiredAttributes) {
			if (node.getAttributes().getNamedItem(requiredAttribute) == null) {
				xmlError("The node <" + node.getLocalName()
						+ "> node the required attribute '" + requiredAttribute
						+ "'");
			}
		}
	}

	/**
	 * Processes one weather state from the import document. It creates
	 * appropriate classes and adds them to the data model.
	 * 
	 * @param node
	 *            the node in the import document that represents the weather
	 *            state to be processed
	 * @throws WeatherImporterException
	 *             if any error occurs
	 */
	private void processWeatherState(Node node) throws WeatherImporterException {
		reportIndex++;

		NamedNodeMap attributes = node.getAttributes();

		Date startDate = DatatypeConverter.parseDateTime(
				attributes.getNamedItem("from").getNodeValue()).getTime();
		Date endDate = DatatypeConverter.parseDateTime(
				attributes.getNamedItem("to").getNodeValue()).getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.debug("Processing <time> element for time period from "
				+ sdf.format(startDate) + " to " + sdf.format(endDate));

		/* cut off everything that's too far in the future */
		if (startDate.getTime() - new Date().getTime() > forecastPeriod * 2) {
			return;
		}
		if (endDate.getTime() - new Date().getTime() > forecastPeriod * 2) {
			endDate = new Date(new Date().getTime() + forecastPeriod * 2);
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
		CloudCover clouds = null;
		
		NodeList child = (NodeList) node.getChildNodes();
		NodeList children = null;
		for (int b = 0; b < child.getLength(); b++) {
			switch (child.item(b).getNodeType()) {
			case Node.ELEMENT_NODE:
				if (!child.item(b).getLocalName().equals("location")) {
					xmlError("The only child element a <time> element may have must be <location>.");
				}
				children = (NodeList) child.item(b).getChildNodes();
				break;

			case Node.TEXT_NODE:
				if (!child.item(b).getTextContent().trim().isEmpty()) {
					xmlError("The <time> element may not have text content.");
				}
				break;

			default:
				xmlError("The <time> element may not have non-element child nodes.");
			}
		}

		if (children == null) {
			xmlError("The <time> element has no child node <location>.");
		}

		List<WeatherCondition> weatherConditions = null;

		/* process all weather phenomena that belong to this weather state */
		for (int b = 0; b < children.getLength(); b++) {
			Node childNode = children.item(b);
			switch (childNode.getNodeType()) {
			case Node.ELEMENT_NODE:
				int coverage;
				if (childNode.getLocalName().equals("temperature")) {
					checkDuplicate(childNode, temperatureValue);
					checkAttributes(childNode, "unit", "value");
					if (!childNode.getAttributes().getNamedItem("unit")
							.getNodeValue().equals("celcius")) {
						xmlError("The temperature unit must be 'celcius'.");
					}
					temperatureValue = Math.round(Float.parseFloat(childNode
							.getAttributes().getNamedItem("value")
							.getNodeValue()) * 100) / 100f;
				} else if (childNode.getLocalName().equals("windDirection")) {
					checkDuplicate(childNode, windDirection);
					checkAttributes(childNode, "deg");
					windDirection = (int) Float
							.parseFloat(childNode.getAttributes()
									.getNamedItem("deg").getNodeValue());
				} else if (childNode.getLocalName().equals("windSpeed")) {
					checkDuplicate(childNode, windSpeed);
					checkAttributes(childNode, "mps");
					windSpeed = Math.round(Float
							.parseFloat(childNode.getAttributes()
									.getNamedItem("mps").getNodeValue()) * 10) / 10f;
				} else if (childNode.getLocalName().equals("humidity")) {
					checkDuplicate(childNode, humidityValue);
					checkAttributes(childNode, "unit", "value");
					if (!childNode.getAttributes().getNamedItem("unit")
							.getNodeValue().equals("percent")) {
						xmlError("The humidity unit must be 'percent'.");
					}
					humidityValue = Math.round(Float.parseFloat(childNode
							.getAttributes().getNamedItem("value")
							.getNodeValue())) / 100f;
				} else if (childNode.getLocalName().equals("pressure")) {
					checkDuplicate(childNode, pressureValue);
					checkAttributes(childNode, "unit", "value");
					if (!childNode.getAttributes().getNamedItem("unit")
							.getNodeValue().equals("hPa")) {
						xmlError("The pressure unit must be 'hPa'.");
					}
					pressureValue = Math.round(Float.parseFloat(childNode
							.getAttributes().getNamedItem("value")
							.getNodeValue()) * 100) / 100f;
				} else if (childNode.getLocalName().equals("cloudiness")) {
					/* will be ignored */
				} else if (childNode.getLocalName().equals("fog")) {
					/* will be ignored */
				} else if (childNode.getLocalName().equals("lowClouds")
						|| childNode.getLocalName().equals("mediumClouds")
						|| childNode.getLocalName().equals("highClouds")) {
					checkAttributes(childNode, "percent");
					coverage = Math.round(Float.parseFloat(childNode
							.getAttributes().getNamedItem("percent")
							.getNodeValue()) * 8 / 100);
					clouds = new CloudCover("clouds" + reportIndex
							+ "_low", cloudAltitude, coverage);
					cloudLayers.add(clouds);
				} else if (childNode.getLocalName().equals("precipitation")) {
					checkDuplicate(childNode, precipitationIntensity);
					checkAttributes(childNode, "unit", "value");
					if (!childNode.getAttributes().getNamedItem("unit")
							.getNodeValue().equals("mm")) {
						xmlError("The unit for the precipitation value must be 'mm'.");
					}
					precipitationIntensity = Math
							.round(Float.parseFloat(childNode.getAttributes()
									.getNamedItem("value").getNodeValue()) * 100) / 100f;
					precipitationProbability = 0f;
					if (precipitationIntensity > 0) {
						precipitationProbability = 1f;
					}
				} else if (childNode.getLocalName().equals("symbol")) {
					checkDuplicate(childNode, weatherConditions);
					checkAttributes(childNode, "id", "number");
					try {
						weatherConditions = getWeatherConditions(Integer
								.parseInt(childNode.getAttributes()
										.getNamedItem("number").getNodeValue()));
					} catch (NumberFormatException e) {
						xmlError("Invalid value for symbol number: "
								+ childNode.getAttributes()
										.getNamedItem("number").getNodeValue());
					}
				} else {
					xmlError("The <location> element has an unknown child element <"
							+ childNode.getLocalName() + ">.");
				}

				break;

			case Node.TEXT_NODE:
				if (!childNode.getTextContent().trim().isEmpty()) {
					xmlError("The <location> element may not have text content.");
				}
				break;

			default:
				xmlError("The <location> element may not have non-element child nodes.");
			}
		}

		if (clouds == null) {
			cloudLayers.add(new CloudCover("clouds" + reportIndex,
					cloudAltitude, 0));
		}
		if (humidityValue != null && temperatureValue != null) {
			dewPointValue = temperatureValue - 20 * (1 - humidityValue);
		}

		if (weatherConditions == null) {
			weatherConditions = new ArrayList<WeatherCondition>();
		}

		List<WeatherPhenomenon> weatherPhenomena = new ArrayList<WeatherPhenomenon>();
		if (temperatureValue != null) {
			weatherPhenomena.add(new Temperature("temperature" + reportIndex,
					temperatureValue));
		}
		if (humidityValue != null) {
			weatherPhenomena.add(new Humidity("humidity" + reportIndex,
					humidityValue));
		}
		if (dewPointValue != null) {
			weatherPhenomena.add(new DewPoint("dewPoint" + reportIndex,
					dewPointValue));
		}
		if (pressureValue != null) {
			weatherPhenomena.add(new Pressure("pressure" + reportIndex,
					pressureValue));
		}
		if (windSpeed != null && windDirection != null) {
			weatherPhenomena.add(new Wind("wind" + reportIndex, windSpeed,
					windDirection));
		}
		if (precipitationProbability != null && precipitationIntensity != null) {
			weatherPhenomena.add(new Precipitation("precipitation"
					+ reportIndex, precipitationIntensity,
					precipitationProbability));
		}

		for (CloudCover cloudLayer : cloudLayers) {
			weatherPhenomena.add(cloudLayer);
		}

		WeatherState weatherState = new WeatherState("weatherState"
				+ reportIndex, weatherPhenomena, weatherConditions);
		weatherState.mergePhenomena(String.valueOf(reportIndex));
		weather.newWeatherReport(startDate, endDate, weatherState);
	}

	/**
	 * Creates a list of instances of {@link WeatherCondition} from the
	 * instances of {@link WeatherCondition} that are specified as parameters
	 * 
	 * @param conditions
	 *            an arbitrary number of instances of {@link WeatherCondition}
	 * @return a list of instances of {@link WeatherCondition}
	 */
	private List<WeatherCondition> getWeatherConditionList(
			WeatherCondition... conditions) {
		List<WeatherCondition> list = new ArrayList<WeatherCondition>();

		for (WeatherCondition condition : conditions) {
			list.add(condition);
		}

		return list;
	}

	/**
	 * Returns an instance of <tt>List&lt;WeatherCondition&gt;</tt> which
	 * contains instances of {@link WeatherCondition} that correspond to the
	 * number specified.
	 * 
	 * See the list of symbols at <a
	 * href="http://api.yr.no/faq.html#symbols">api.yr.no</a> for the mapping of
	 * numbers to weather conditions.
	 * 
	 * @param number
	 *            the number representing a set of weather conditions
	 * @return an instance of <tt>List&lt;WeatherCondition&gt;</tt>
	 * @throws WeatherImporterException
	 *             in case the specified number does not exist
	 */
	private List<WeatherCondition> getWeatherConditions(int number)
			throws WeatherImporterException {
		Map<Integer, List<WeatherCondition>> weatherConditions = new HashMap<Integer, List<WeatherCondition>>();

		/* according to http://api.yr.no/faq.html */
		weatherConditions.put(1, getWeatherConditionList(WeatherCondition.Sun));
		weatherConditions.put(2,
				getWeatherConditionList(WeatherCondition.LightCloud));
		weatherConditions.put(3,
				getWeatherConditionList(WeatherCondition.PartlyCloud));
		weatherConditions.put(4,
				getWeatherConditionList(WeatherCondition.Cloud));
		weatherConditions.put(
				5,
				getWeatherConditionList(WeatherCondition.Rain,
						WeatherCondition.Sun));
		weatherConditions.put(
				6,
				getWeatherConditionList(WeatherCondition.Rain,
						WeatherCondition.Sun, WeatherCondition.Thunder));
		weatherConditions.put(
				7,
				getWeatherConditionList(WeatherCondition.Sleet,
						WeatherCondition.Sun));
		weatherConditions.put(
				8,
				getWeatherConditionList(WeatherCondition.Snow,
						WeatherCondition.Sun));
		weatherConditions
				.put(9, getWeatherConditionList(WeatherCondition.Rain));
		weatherConditions.put(10,
				getWeatherConditionList(WeatherCondition.Rain));
		weatherConditions.put(
				11,
				getWeatherConditionList(WeatherCondition.Rain,
						WeatherCondition.Thunder));
		weatherConditions.put(12,
				getWeatherConditionList(WeatherCondition.Sleet));
		weatherConditions.put(13,
				getWeatherConditionList(WeatherCondition.Snow));
		weatherConditions.put(
				14,
				getWeatherConditionList(WeatherCondition.Snow,
						WeatherCondition.Thunder));
		weatherConditions
				.put(15, getWeatherConditionList(WeatherCondition.Fog));
		weatherConditions
				.put(16, getWeatherConditionList(WeatherCondition.Sun));
		weatherConditions.put(17,
				getWeatherConditionList(WeatherCondition.LightCloud));
		weatherConditions.put(
				18,
				getWeatherConditionList(WeatherCondition.Rain,
						WeatherCondition.Sun));
		weatherConditions.put(
				19,
				getWeatherConditionList(WeatherCondition.Snow,
						WeatherCondition.Sun));
		weatherConditions.put(
				20,
				getWeatherConditionList(WeatherCondition.Sleet,
						WeatherCondition.Sun, WeatherCondition.Thunder));
		weatherConditions.put(
				21,
				getWeatherConditionList(WeatherCondition.Snow,
						WeatherCondition.Sun, WeatherCondition.Thunder));
		weatherConditions.put(
				22,
				getWeatherConditionList(WeatherCondition.Rain,
						WeatherCondition.Thunder));
		weatherConditions.put(
				23,
				getWeatherConditionList(WeatherCondition.Sleet,
						WeatherCondition.Thunder));

		if (!weatherConditions.containsKey(number)) {
			throw new WeatherImporterException(
					"Unknown weather condition number: " + number);
		}
		return weatherConditions.get(number);
	}

	/**
	 * Yields an exception if the second parameter is not null. This method is
	 * used by {@link #processWeatherState(Node)} to determine if a node with a
	 * certain name has already been processed. If such a node has already been
	 * processed, there is more than one node with that name, for though only
	 * one node with that name is allowed.
	 * 
	 * @param childNode
	 *            a DOM node
	 * @param item
	 *            an arbitary object
	 * @throws WeatherImporterException
	 *             if the second parameter is not null
	 */
	private void checkDuplicate(Node childNode, Object item)
			throws WeatherImporterException {
		if (item != null) {
			String elementName = childNode.getLocalName();
			xmlError("A <location> element has more than one <" + elementName
					+ "> child elements.");
		}
	}

	@Override
	public Weather fetchWeather(GeographicalPosition position,
			WeatherImporterProperties properties, List<Integer> forecastHours)
			throws WeatherImporterException {
		/*
		 * This method accesses the API of yr.no and processes the XML document
		 * that is returned.
		 */
		cloudAltitude = properties.getInt("cloud_altitude");

		forecastPeriod = Collections.max(forecastHours) * 3600000;
		reportIndex = 0;

		/* build URL */
		String url = new String(feedUrl);
		url = url.replaceAll("%lat", String.valueOf(position.getLatitude()));
		url = url.replaceAll("%lon", String.valueOf(position.getLongitude()));
		url = url.replaceAll("%alt",
				String.valueOf((int) (Math.round(position.getAltitude()))));
		log.info("Retrieving weather data from URL: " + url);

		NodeList nodes;
		try {
			/* perform the API access */
			HttpClient http = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			request.setHeader(
					"User-Agent",
					"ThinkHome weather component - see https://www.auto.tuwien.ac.at/projectsites/thinkhome/overview.html");
			HttpResponse response = http.execute(request);
			if (response.getStatusLine().getStatusCode() >= 400
					&& response.getStatusLine().getStatusCode() < 500) {
				String error = "Could not fetch weather feed: "
						+ response.getStatusLine().getReasonPhrase();
				log.error(error);
				throw new WeatherImporterException(error);
			}
			HttpEntity entity = response.getEntity();

			/* build a DOM document */
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder builder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = builder.parse(entity.getContent());

			/* validate the document against its XML schema definition */
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new URL(schemaLocation));
			schema.newValidator().validate(new DOMSource(document));

			/* use XPATH in order to locate all relevant nodes */
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression datapointExpression = xpath
					.compile("/weatherdata/product/time");

			weather = new Weather(new Date(), properties.getInt("priority"),
					new ServiceSource(source), position, forecastHours);
			nodes = (NodeList) datapointExpression.evaluate(document,
					XPathConstants.NODESET);
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			throw new WeatherImporterException(e);
		} catch (ParserConfigurationException e) {
			log.error(e.getMessage());
			throw new WeatherImporterException(e);
		} catch (SAXException e) {
			log.error(e.getMessage());
			throw new WeatherImporterException(e);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new WeatherImporterException(e);
		} catch (XPathExpressionException e) {
			log.error(e.getMessage());
			throw new WeatherImporterException(e);
		}

		log.debug("Found " + nodes.getLength() + " <time> nodes.");

		if (nodes.getLength() == 0) {
			xmlError("No <time> nodes found.");
		}

		/* iterate all nodes and extract the weather data from them */
		for (int a = 0; a < nodes.getLength(); a++) {
			processWeatherState(nodes.item(a));
		}

		weather.normalizeWeatherReports();

		log.info("Importing weather data completed");

		return weather;
	}
}
