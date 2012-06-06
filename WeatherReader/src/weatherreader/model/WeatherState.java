package weatherreader.model;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.ibm.icu.text.Collator;

//TODO javadoc
public class WeatherState implements OntologyClass {
	private String name;
	private List<WeatherCondition> weatherConditions;
	private Individual individual;
	private List<WeatherPhenomenon> weatherPhenomena;
	private WeatherState previousState;
	
	@Override
	public String toString() {
		List<WeatherPhenomenon> phenomena = new ArrayList<WeatherPhenomenon>(weatherPhenomena);
		Collections.sort(phenomena, new Comparator<WeatherPhenomenon>() {
			@Override
			public int compare(WeatherPhenomenon o1, WeatherPhenomenon o2) {
				Collator collator = Collator.getInstance();
				return collator.compare(o1.getClass().getName(), o2.getClass().getName());
			}
		});
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for(WeatherPhenomenon phenomenon : phenomena) {
			buffer.append(phenomenon.toString() + "; ");
		}
		buffer.append("weatherConditions=" + weatherConditions.toString());
		buffer.append("]");
		return buffer.toString();
	}

	public WeatherState(String name) {
		this(name, new ArrayList<WeatherPhenomenon>(), new ArrayList<WeatherCondition>());
	}
	
	public WeatherState(String name, List<WeatherPhenomenon> weatherPhenomena, List<WeatherCondition> weatherConditions) {
		this.name = name;
		this.weatherPhenomena = weatherPhenomena;
		this.weatherConditions = weatherConditions;
	}	
	
	protected void setWeatherConditions(List<WeatherCondition> weatherConditions) {
		this.weatherConditions = weatherConditions;
	}

	public void createIndividuals(OntModel onto, Individual weatherObservation, int stateIndex, WeatherState previousState) {
		OntClass weatherStateClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherState");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + "weatherState" + stateIndex, weatherStateClass);
		
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			phenomenon.createIndividuals(onto);
			onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasWeatherPhenomenon"), phenomenon.getOntIndividual()));
		}
		
		for(WeatherCondition condition : weatherConditions) {
			onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasCondition"), onto.getIndividual(WeatherReport.NAMESPACE + condition.toString())));
		}
		
		if(previousState != null) {
			Individual previousStateIndividual = previousState.getOntIndividual();
			onto.add(onto.createStatement(previousStateIndividual, onto.getProperty(WeatherReport.NAMESPACE + "hasNextWeatherState"), individual));
		}
	}

	public List<WeatherPhenomenon> getPhenomenonType(Class<? extends WeatherPhenomenon> type) {
		List<WeatherPhenomenon> phenomena = new ArrayList<WeatherPhenomenon>();
		
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			if(phenomenon.getClass().equals(type)) {
				phenomena.add(phenomenon);
			}
		}
		
		return phenomena;
	}
	
	public boolean containsPhenomenonType(Class<? extends WeatherPhenomenon> type) {
		return getPhenomenonType(type).size() > 0;
	}
	
	public List<WeatherCondition> getWeatherConditions() {
		return weatherConditions;
	}
	
	public void addPhenomenon(WeatherPhenomenon phenomenon) {
		weatherPhenomena.add(phenomenon);
	}

	public void createIndividuals(OntModel onto) {
		OntClass weatherStateClass = onto.getOntClass(WeatherReport.NAMESPACE + "WeatherState");
		individual = onto.createIndividual(WeatherReport.NAMESPACE + name, weatherStateClass);
		
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			phenomenon.createIndividuals(onto);
			onto.add(onto.createStatement(phenomenon.getOntIndividual(), onto.getProperty(WeatherReport.NAMESPACE + "belongsToWeatherState"), individual));
		}
		
		for(WeatherCondition condition : weatherConditions) {
			onto.add(onto.createStatement(individual, onto.getProperty(WeatherReport.NAMESPACE + "hasCondition"), onto.getIndividual(WeatherReport.NAMESPACE + condition.toString())));
		}
		
		if(previousState != null) {
			Individual previousStateIndividual = previousState.getOntIndividual();
			onto.add(onto.createStatement(previousStateIndividual, onto.getProperty(WeatherReport.NAMESPACE + "hasNextWeatherState"), individual));
		}
	}

	public Individual getOntIndividual() {
		return individual;
	}

	public void addPhenomena(List<WeatherPhenomenon> phenomena) {
		for(WeatherPhenomenon phenomenon : phenomena) {
			weatherPhenomena.add(phenomenon);
		}
	}

	public void mergePhenomena(String suffix) {
		List<Class<? extends WeatherPhenomenon>> types = new ArrayList<Class<? extends WeatherPhenomenon>>();
		
		for(WeatherPhenomenon phenomenon : weatherPhenomena) {
			if(!types.contains(phenomenon.getClass())) {
				types.add(phenomenon.getClass());
			}
		}
		
		for(Class<? extends WeatherPhenomenon> type : types) {
			String name = type.getSimpleName() + suffix;
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
			mergePhenomena(name, type);
		}
	}
	
	public void mergePhenomena(String suffix, Class<? extends WeatherPhenomenon> type) {
		List<WeatherPhenomenon> phenomena = new ArrayList<WeatherPhenomenon>();
		
		Iterator<WeatherPhenomenon> iterator = weatherPhenomena.iterator();
		while(iterator.hasNext()) {
			WeatherPhenomenon phenomenon = iterator.next();
			
			if(phenomenon.getClass().equals(type)) {
				phenomena.add(phenomenon);
				iterator.remove();
			}
		}
		
		try {
			Constructor<? extends WeatherPhenomenon> constructor = type.getConstructor(String.class, List.class);
			weatherPhenomena.add(constructor.newInstance(suffix, phenomena));
		}
		catch (NoSuchMethodException e) {
			/* ignore as it won't happen */
		}
		catch (SecurityException e) {
			/* ignore as it won't happen */
		}
		catch (IllegalArgumentException e) {
			/* ignore as it won't happen */
		}
		catch (InvocationTargetException e) {
			/* ignore as it won't happen */
		}
		catch (InstantiationException e) {
			/* ignore as it won't happen */
		}
		catch (IllegalAccessException e) {
			/* ignore as it won't happen */
		}
	}

	public void setPreviousState(WeatherState previousState) {
		this.previousState = previousState;
	}
	
	@Override
	public Object clone() {
		WeatherState weatherState = new WeatherState(name);		
		weatherState.weatherConditions = new ArrayList<WeatherCondition>(weatherConditions);
		weatherState.previousState = previousState;
		
		weatherState.weatherPhenomena = new ArrayList<WeatherPhenomenon>();
		for(WeatherPhenomenon weatherPhenomenon : weatherPhenomena) {
			weatherState.weatherPhenomena.add((WeatherPhenomenon)(weatherPhenomenon.clone()));
		}
		
		return weatherState;
	}

	public String getName() {
		return name;
	}
}
