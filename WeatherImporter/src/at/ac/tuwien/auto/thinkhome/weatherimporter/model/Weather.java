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

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * This class represents everything that is obtained from a weather source at a
 * single time.
 * 
 * @author Paul Staroch
 * 
 */
public class Weather {
	public static final int DECIMALS = 2;

	/**
	 * URI of the namespace used by the ThinkHomeWeather ontology
	 */
	public static final String NAMESPACE = "http://www.semanticweb.org/ontologies/2011/9/ThinkHomeWeather.owl#";

	/**
	 * Prefix to be used for the namespace which is used by the ThinkHomeWeather
	 * ontology
	 */
	public static final String NAMESPACE_PREFIX = "weather:";

	/**
	 * URI of the namespace used by the MUO ontology (Measurement Units
	 * Ontology)
	 */
	public static final String MUO_NAMESPACE = "http://purl.oclc.org/NET/muo/muo#";

	/**
	 * Prefix to be used for the namespace which is used by the MUO ontology
	 */
	public static final String MUO_PREFIX = "muo:";

	/**
	 * URI of the unit "metre" in the MUO ontology
	 */
	public static final String UNIT_METER = "http://purl.oclc.org/NET/muo/ucum/unit/length/meter";

	/**
	 * URI of the unit "degrees Celsius" in the MUO ontology
	 */
	public static final String UNIT_DEGREES_CELSIUS = "http://purl.oclc.org/NET/muo/ucum/unit/temperature/degree-Celsius";

	/**
	 * URI of the unit "degrees" in the MUO ontology
	 */
	public static final String UNIT_DEGREE = "http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree";

	/**
	 * URI of the unit "percent" in the MUO ontology
	 */
	public static final String UNIT_PERCENT = "http://purl.oclc.org/NET/muo/ucum/unit/fraction/percent";

	/**
	 * Date when the data in this weather instance has been obtained from its
	 * source
	 */
	private Date observationTime;

	/**
	 * Priority of all data represented by this instance
	 */
	private int priority;

	/**
	 * Source of the data represented by this instance
	 */
	private WeatherSource source;

	/**
	 * The geographical position all data represented by this instance is valid
	 * for
	 */
	private GeographicalPosition position;

	/**
	 * Collection of all associated instances of {@link WeatherReport}
	 */
	private List<WeatherReport> weatherReports;

	/**
	 * List of integers that specify for which points of time weather reports
	 * shall be obtained (in hours, relative to the current time)
	 */
	private List<Integer> forecastHours;

	/**
	 * Point of time of the last weather report to be obtained (calculated from
	 * <tt>forecastHours</tt>)
	 */
	private int maxHour;

	/**
	 * Log4j logger
	 */
	private Logger log;

	/**
	 * The constructor for the class.
	 * 
	 * @param observationTime
	 *            the time all data represented by this instance was obtained
	 * @param priority
	 *            priority of all data represented by this instance
	 * @param source
	 *            the source for all data represented by this instance
	 * @param position
	 *            the geographical position the data represented by this
	 *            instance is valid for
	 * @param forecastHours
	 *            a list of integers that specify for which points of time
	 *            weather reports shall be obtained (in hours, relative to the
	 *            current time)
	 */
	public Weather(Date observationTime, int priority, WeatherSource source,
			GeographicalPosition position, List<Integer> forecastHours) {
		super();

		this.observationTime = observationTime;
		this.priority = priority;
		this.source = source;
		this.position = position;
		this.forecastHours = forecastHours;
		this.maxHour = this.forecastHours.get(forecastHours.size() - 1) * 2;
		weatherReports = new ArrayList<WeatherReport>();

		log = Logger.getLogger(Weather.class);
	}

	/**
	 * Adds a new weather report to the list of weather reports managed by this
	 * instance
	 * 
	 * @param startDate
	 *            start of the period the new weather report is valid for
	 * @param endDate
	 *            end of the period the new weather report is valid for
	 * @param weatherState
	 *            weather state that contains all weather data for the new
	 *            weather report
	 */
	public void newWeatherReport(Date startDate, Date endDate,
			WeatherState weatherState) {
		/*
		 * we can use non-unique names here as normalizeWeatherReports() will
		 * create new instances of WeatherReport and Instant which have unique
		 * names
		 */
		weatherReports.add(new WeatherReport("weatherReport", Instant
				.getInstant(observationTime), Interval.getInterval(startDate),
				Interval.getInterval(endDate), priority, source, position,
				weatherState));
	}

	/**
	 * Performs a &quot;normalization&quot; on all data stored in this class by
	 * performing the following steps:
	 * 
	 * <ul>
	 * <li>Weather reports that are valid for more than one hour are splitted
	 * into several weather reports, each valid for one hour</li>
	 * <li>Weather reports which are valid for the same time are merged</li>
	 * <li>Missing weather reports are interpolated</li>
	 * <li>Sun position data is added to each weather report</li>
	 * </ul>
	 */
	public void normalizeWeatherReports() {
		log.debug("Normalizing weather reports...");

		printWeatherReports("Weather reports before normalization",
				weatherReports);

		/* split weather reports with startDate != endDate */
		Iterator<WeatherReport> it = weatherReports.iterator();
		List<WeatherReport> additionalWeatherReports = new ArrayList<WeatherReport>();
		while (it.hasNext()) {
			WeatherReport report = it.next();
			if (report.getStartTime().getTime() != report.getEndTime()
					.getTime()) {
				it.remove();
				for (int a = (int) report.getStartTime().getTime(); a < report
						.getEndTime().getTime(); a++) {
					additionalWeatherReports.add(new WeatherReport(
							"weatherReport" + a, report.getObservationTime(),
							Interval.getInterval(a), Interval.getInterval(a),
							priority, source, report.getPosition(), report
									.getState()));
				}
			}
		}
		printWeatherReports("Additional weather reports",
				additionalWeatherReports);

		weatherReports.addAll(additionalWeatherReports);

		printWeatherReports("Weather reports after splitting them up",
				weatherReports);

		List<Class<? extends WeatherPhenomenon>> phenomenonClasses = new ArrayList<Class<? extends WeatherPhenomenon>>();
		phenomenonClasses.add(Temperature.class);
		phenomenonClasses.add(Humidity.class);
		phenomenonClasses.add(DewPoint.class);
		phenomenonClasses.add(Pressure.class);
		phenomenonClasses.add(Wind.class);
		phenomenonClasses.add(Precipitation.class);
		phenomenonClasses.add(SunPosition.class);
		phenomenonClasses.add(CloudCover.class);

		/* merge weather reports */
		Map<Integer, WeatherReport> newWeatherReports = new HashMap<Integer, WeatherReport>();
		for (int a = 0; a <= maxHour; a++) {
			newWeatherReports.put(a, new WeatherReport("weatherReport" + a,
					null, Interval.getInterval(a), Interval.getInterval(a + 1),
					0, null, null, new WeatherState("weatherState" + a)));
		}
		for (WeatherReport report : weatherReports) {
			WeatherState state = report.getState();

			WeatherReport newReport = newWeatherReports.get((int) report
					.getStartTime().getTime());
			newReport.setObservationTime(report.getObservationTime());
			newReport.setPriority(report.getPriority());
			newReport.setSource(report.getSource());
			newReport.setPosition(report.getPosition());

			WeatherState newState = (WeatherState) (newReport.getState());

			/* let's assume we get a value only from one source */
			for (Class<? extends WeatherPhenomenon> clazz : phenomenonClasses) {
				newState.addPhenomena(state.getPhenomenonType(clazz));
			}

			newState.mergePhenomena(String.valueOf(report.getStartTime()));

			if (state.getWeatherConditions().size() > 0) {
				newState.setWeatherConditions(state.getWeatherConditions());
			}
		}

		printWeatherReports(
				"Weather reports after merging states for the same time",
				newWeatherReports);

		/* interpolate missing values */
		Map<Class<? extends WeatherPhenomenon>, Integer> lastPhenomena = new HashMap<Class<? extends WeatherPhenomenon>, Integer>();

		Integer lastWeatherConditions = null;
		for (int a = 0; a <= maxHour; a++) {
			WeatherReport currentReport = newWeatherReports.get(a);
			WeatherState currentState = currentReport.getState();

			for (Class<? extends WeatherPhenomenon> clazz : phenomenonClasses) {
				if (currentState.containsPhenomenonType(clazz)) {
					WeatherPhenomenon lastWeatherPhenomenon;
					int start;

					if (!lastPhenomena.containsKey(clazz)) {
						lastWeatherPhenomenon = currentState.getPhenomenonType(
								clazz).get(0);
						start = 0;
					} else {
						lastWeatherPhenomenon = newWeatherReports
								.get(lastPhenomena.get(clazz)).getState()
								.getPhenomenonType(clazz).get(0);
						start = lastPhenomena.get(clazz) + 1;
					}
					WeatherPhenomenon thisValue = currentState
							.getPhenomenonType(clazz).get(0);
					for (int b = start; b < a; b++) {
						if (newWeatherReports.get(b).getState()
								.containsPhenomenonType(clazz)) {
							newWeatherReports
									.get(b)
									.getState()
									.getPhenomenonType(clazz)
									.get(0)
									.interpolate(lastWeatherPhenomenon,
											thisValue, start, a, b);
						} else {
							newWeatherReports
									.get(b)
									.getState()
									.addPhenomenon(
											currentState
													.getPhenomenonType(clazz)
													.get(0)
													.createInterpolatedPhenomenon(
															String.valueOf(b),
															lastWeatherPhenomenon,
															thisValue, start,
															a, b));
						}
					}

					lastPhenomena.put(clazz, a);
				}
			}

			if (currentState.getWeatherConditions().size() > 0) {
				List<WeatherCondition> lastValue;
				int start;
				if (lastWeatherConditions == null) {
					lastValue = currentState.getWeatherConditions();
					start = 0;
				} else {
					lastValue = newWeatherReports.get(lastWeatherConditions)
							.getState().getWeatherConditions();
					start = lastWeatherConditions + 1;
				}
				for (int b = start; b < a; b++) {
					newWeatherReports.get(b).getState()
							.setWeatherConditions(lastValue);
				}

				lastWeatherConditions = a;
			}
		}

		weatherReports = new ArrayList<WeatherReport>();
		for (int a = 0; a <= maxHour; a++) {
			if (forecastHours.contains(a)) {
				weatherReports.add(newWeatherReports.get(a));
			}
		}

		for (int a = 0; a < weatherReports.size(); a++) {
			float nextStartTime;
			if (a + 1 < weatherReports.size()) {
				nextStartTime = weatherReports.get(a + 1).getStartTime()
						.getTime();
			} else {
				nextStartTime = 2
						* weatherReports.get(a).getStartTime().getTime()
						- weatherReports.get(a - 1).getStartTime().getTime();
			}
			weatherReports.get(a).setEndTime(
					Interval.getInterval(nextStartTime));
		}

		printWeatherReports("Weather reports after normalization",
				weatherReports);

		// add sun position data
		for (WeatherReport report : weatherReports) {
			Date date = new Date((long) (new Date().getTime() + report
					.getStartTime().getTime() * 3600000));
			report.getState().addPhenomenon(
					new SunPosition("sunPosition" + report.getStartTime(),
							position, date));
		}

		printWeatherReports(
				"Final weather states after normalization including sun position data",
				weatherReports);
	}

	/**
	 * Creates invididuals for the data stored in this instance and adds them to
	 * the ontology being specified
	 * 
	 * @param ontology
	 *            ontology where to add the individuals to
	 */
	public void createIndividuals(OntModel ontology) {
		WeatherReport previousReport = null;
		for (WeatherReport report : weatherReports) {
			report.setPreviousReport(previousReport);
			report.createIndividuals(ontology);
			previousReport = report;
		}
	}

	/**
	 * Sends a textual representation of the data of all specified weather
	 * reports to Log4j.
	 * 
	 * @param description
	 *            a description which is to be prepended to the output
	 * @param reports
	 *            a list of weather reports which are to be dumped via Log4j.
	 */
	private void printWeatherReports(String description,
			Collection<WeatherReport> reports) {
		log.debug("");
		log.debug(description + ":");
		log.debug("");
		for (WeatherReport report : reports) {
			log.debug(report.toString());
		}
		log.debug("");
	}

	/**
	 * Sends a textual representation of the data of all specified weather
	 * reports to Log4j.
	 * 
	 * @param description
	 *            a description which is to be prepended to the output
	 * @param reports
	 *            a map of weather reports which are to be dumped via Log4j. The
	 *            reports will be processed in ascending order of the keys in
	 *            the map.
	 */
	private void printWeatherReports(String description,
			Map<Integer, WeatherReport> reports) {
		log.debug("");
		log.debug(description + ":");

		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(reports.keySet());
		Collections.sort(keys);
		for (Integer key : keys) {
			log.debug(reports.get(key).toString());
		}
		log.debug("");
	}

	/**
	 * Returns a set of Turtle statements that contains all data which is
	 * represented by this instance.
	 * 
	 * @return an instance of {@link TurtleStore} that contains all data which
	 *         is represented by this instance.
	 */
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();
		for (WeatherReport report : weatherReports) {
			turtle.addAll(report.getTurtleStatements());
		}
		return turtle;
	}

	private static int blankNodeId = 0;

	public static String generateBlankNode() {
		blankNodeId++;
		return "_:blank" + blankNodeId;
	}
}
