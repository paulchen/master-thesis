package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStatement;
import at.ac.tuwien.auto.thinkhome.weatherimporter.main.TurtleStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

/**
 * An instance of this class represents all data that is known for a certain
 * instance of {@link WeatherReport}.
 * 
 * @author Paul Staroch
 * 
 */
public class WeatherState implements OntologyClass {
	/**
	 * Unique name of the individual that corresponds to this object in the
	 * ontology
	 */
	private String name;

	/**
	 * List of all weather conditions that are associated with this instance
	 */
	private List<WeatherCondition> weatherConditions;

	/**
	 * Once {@link #createIndividuals(OntModel)} has been called, this contains
	 * the main individual in the ontology that has been created by that method
	 * call.
	 */
	private Individual individual;

	/**
	 * List of all weather phenomena that are associated with this instance
	 */
	private List<WeatherPhenomenon> weatherPhenomena;

	/**
	 * Previous weather state, if it exists
	 */
	private WeatherState previousState;

	@Override
	public String toString() {
		List<WeatherPhenomenon> phenomena = new ArrayList<WeatherPhenomenon>(
				weatherPhenomena);
		Collections.sort(phenomena, new Comparator<WeatherPhenomenon>() {
			@Override
			public int compare(WeatherPhenomenon o1, WeatherPhenomenon o2) {
				return o1.getClass().getName()
						.compareTo(o2.getClass().getName());
			}
		});

		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (WeatherPhenomenon phenomenon : phenomena) {
			buffer.append(phenomenon.toString() + "; ");
		}
		buffer.append("weatherConditions=" + weatherConditions.toString());
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * Creates an instance of <tt>WeatherState</tt> given its unique name.
	 * 
	 * @param name
	 *            the unique name of the individual in the ontology that
	 *            corresponds to this object
	 */
	public WeatherState(String name) {
		this(name, new ArrayList<WeatherPhenomenon>(),
				new ArrayList<WeatherCondition>());
	}

	/**
	 * Creates an instance of <tt>WeatherState</tt> given its unique name, a
	 * list of instances of <tt>WeatherPhenomenon</tt> and a list of instances
	 * of <tt>WeatherCondition</tt>.
	 * 
	 * @param name
	 *            the unique name of the individual in the ontology that
	 *            corresponds to this object
	 * @param weatherPhenomena
	 *            a list of instances of <tt>WeatherPhenomenon</tt> that belong
	 *            to this weather state
	 * @param weatherConditions
	 *            a list of instances of <tt>WeatherCondition</tt> that belong
	 *            to this weather state
	 */
	public WeatherState(String name, List<WeatherPhenomenon> weatherPhenomena,
			List<WeatherCondition> weatherConditions) {
		this.name = name;
		this.weatherPhenomena = weatherPhenomena;
		this.weatherConditions = weatherConditions;
	}

	protected void setWeatherConditions(List<WeatherCondition> weatherConditions) {
		this.weatherConditions = weatherConditions;
	}

	@Override
	public TurtleStore getTurtleStatements() {
		TurtleStore turtle = new TurtleStore();

		turtle.add(new TurtleStatement(getTurtleName(), "a",
				Weather.NAMESPACE_PREFIX + "WeatherState"));
		for (WeatherPhenomenon phenomenon : weatherPhenomena) {
			turtle.addAll(phenomenon.getTurtleStatements());

			turtle.add(new TurtleStatement(phenomenon.getTurtleName(),
					Weather.NAMESPACE_PREFIX + "belongsToWeatherState",
					getTurtleName()));
		}
		for (WeatherCondition condition : weatherConditions) {
			turtle.add(new TurtleStatement(getTurtleName(),
					Weather.NAMESPACE_PREFIX + "hasCondition",
					Weather.NAMESPACE_PREFIX + condition.toString()));
		}

		if (previousState != null) {
			turtle.add(new TurtleStatement(Weather.NAMESPACE_PREFIX
					+ previousState.getName(), Weather.NAMESPACE_PREFIX
					+ "hasNextWeatherState", getTurtleName()));
		}

		return turtle;
	}

	/**
	 * Returns all instances of <tt>WeatherPhenomenon</tt> having the specified
	 * type.
	 * 
	 * @param type
	 *            a type
	 * @return a list of instances of <tt>WeatherPhenomenon</tt>
	 */
	public List<WeatherPhenomenon> getPhenomenonType(
			Class<? extends WeatherPhenomenon> type) {
		List<WeatherPhenomenon> phenomena = new ArrayList<WeatherPhenomenon>();

		for (WeatherPhenomenon phenomenon : weatherPhenomena) {
			if (phenomenon.getClass().equals(type)) {
				phenomena.add(phenomenon);
			}
		}

		return phenomena;
	}

	/**
	 * Checks whether an instance of the given subtype of
	 * <tt>WeatherPhenomenon</tt> is associated with this instance.
	 * 
	 * @param type
	 *            a type
	 * @return true, if an instance is associated with this instance, false
	 *         otherwise
	 */
	public boolean containsPhenomenonType(
			Class<? extends WeatherPhenomenon> type) {
		return getPhenomenonType(type).size() > 0;
	}

	public List<WeatherCondition> getWeatherConditions() {
		return weatherConditions;
	}

	/**
	 * Adds an instance of <tt>WeatherPhenomenon</tt> to the list of instances
	 * of this class stored in this instance.
	 * 
	 * @param phenomenon
	 *            the instance of <tt>WeatherPhenomenon</tt> that is to be added
	 */
	public void addPhenomenon(WeatherPhenomenon phenomenon) {
		weatherPhenomena.add(phenomenon);
	}

	@Override
	public void createIndividuals(OntModel onto) {
		OntClass weatherStateClass = onto.getOntClass(Weather.NAMESPACE
				+ "WeatherState");
		individual = onto.createIndividual(Weather.NAMESPACE + name,
				weatherStateClass);

		for (WeatherPhenomenon phenomenon : weatherPhenomena) {
			phenomenon.createIndividuals(onto);
			onto.add(onto.createStatement(
					phenomenon.getIndividual(),
					onto.getProperty(Weather.NAMESPACE
							+ "belongsToWeatherState"), individual));
		}

		for (WeatherCondition condition : weatherConditions) {
			onto.add(onto.createStatement(individual, onto
					.getProperty(Weather.NAMESPACE + "hasCondition"), onto
					.getIndividual(Weather.NAMESPACE + condition.toString())));
		}

		if (previousState != null) {
			Individual previousStateIndividual = previousState.getIndividual();
			onto.add(onto.createStatement(
					previousStateIndividual,
					onto.getProperty(Weather.NAMESPACE + "hasNextWeatherState"),
					individual));
		}
	}

	@Override
	public Individual getIndividual() {
		return individual;
	}

	/**
	 * Adds an list of instance of <tt>WeatherPhenomenon</tt> to the list of
	 * instances of this class stored in this instance.
	 * 
	 * @param phenomena
	 *            the instances of <tt>WeatherPhenomenon</tt> that are to be added
	 */
	public void addPhenomena(List<WeatherPhenomenon> phenomena) {
		for (WeatherPhenomenon phenomenon : phenomena) {
			weatherPhenomena.add(phenomenon);
		}
	}

	/**
	 * Checks the instances of <tt>WeatherPhenomenon</tt> which are associated
	 * with this object for duplicate types; if there are duplicate types, the
	 * corresponding instances of <tt>WeatherPhenomenon</tt> are merged.
	 * 
	 * @param suffix
	 *            a suffix which is to be appended to the name of newly created
	 *            instances of <tt>WeatherPhenomenon</tt>
	 */
	public void mergePhenomena(String suffix) {
		List<Class<? extends WeatherPhenomenon>> types = new ArrayList<Class<? extends WeatherPhenomenon>>();

		for (WeatherPhenomenon phenomenon : weatherPhenomena) {
			if (!types.contains(phenomenon.getClass())) {
				types.add(phenomenon.getClass());
			}
		}

		for (Class<? extends WeatherPhenomenon> type : types) {
			String name = type.getSimpleName() + suffix;
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
			mergePhenomena(name, type);
		}
	}

	/**
	 * Merges all instances of <tt>WeatherPhenomenon</tt> of the given type
	 * which are associated with this instance.
	 * 
	 * @param name
	 *            the name of the newly created instance of
	 *            <tt>WeatherPhenomenon</tt>
	 * @param type
	 *            a subtype of <tt>WeatherPhenomenon</tt>; all instances of this
	 *            type which are associated with this instances are merged
	 */
	public void mergePhenomena(String name,
			Class<? extends WeatherPhenomenon> type) {
		List<WeatherPhenomenon> phenomena = new ArrayList<WeatherPhenomenon>();

		Iterator<WeatherPhenomenon> iterator = weatherPhenomena.iterator();
		while (iterator.hasNext()) {
			WeatherPhenomenon phenomenon = iterator.next();

			if (phenomenon.getClass().equals(type)) {
				phenomena.add(phenomenon);
				iterator.remove();
			}
		}

		try {
			Constructor<? extends WeatherPhenomenon> constructor = type
					.getConstructor(String.class, List.class);
			weatherPhenomena.add(constructor.newInstance(name, phenomena));
		} catch (NoSuchMethodException e) {
			/* ignore as it won't happen */
		} catch (SecurityException e) {
			/* ignore as it won't happen */
		} catch (IllegalArgumentException e) {
			/* ignore as it won't happen */
		} catch (InvocationTargetException e) {
			/* ignore as it won't happen */
		} catch (InstantiationException e) {
			/* ignore as it won't happen */
		} catch (IllegalAccessException e) {
			/* ignore as it won't happen */
		}
	}

	public void setPreviousState(WeatherState previousState) {
		this.previousState = previousState;
	}

	@Override
	public Object clone() {
		WeatherState weatherState = new WeatherState(name);
		weatherState.weatherConditions = new ArrayList<WeatherCondition>(
				weatherConditions);
		weatherState.previousState = previousState;

		weatherState.weatherPhenomena = new ArrayList<WeatherPhenomenon>();
		for (WeatherPhenomenon weatherPhenomenon : weatherPhenomena) {
			weatherState.weatherPhenomena
					.add((WeatherPhenomenon) (weatherPhenomenon.clone()));
		}

		return weatherState;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getTurtleName() {
		return Weather.NAMESPACE_PREFIX + name;
	}
}
