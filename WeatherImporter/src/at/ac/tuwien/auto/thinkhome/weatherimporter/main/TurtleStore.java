package at.ac.tuwien.auto.thinkhome.weatherimporter.main;

import java.util.LinkedHashSet;
import java.util.Set;

import at.ac.tuwien.auto.thinkhome.weatherimporter.model.GeographicalPosition;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.TemporalEntity;
import at.ac.tuwien.auto.thinkhome.weatherimporter.model.Weather;

// TODO javadoc
public class TurtleStore {
	private final int bufferSpace = 3;
	
	private Set<TurtleStatement> statements;
	
	public TurtleStore() {
		this.statements = new LinkedHashSet<TurtleStatement>();
	}
	
	public String printAll() {
		int subjectWidth = 0;
		int predicateWidth = 0;
		
		for(TurtleStatement statement : statements) {
			subjectWidth = Math.max(subjectWidth, statement.getSubject().length());
			predicateWidth = Math.max(predicateWidth, statement.getPredicate().length());
		}

		subjectWidth += bufferSpace;
		predicateWidth += bufferSpace;
		
		StringBuffer output = new StringBuffer();
		
		output.append("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n");
		output.append("@prefix " + Weather.NAMESPACE_PREFIX + " <" + Weather.NAMESPACE + "> .\n");
		output.append("@prefix " + TemporalEntity.TIME_PREFIX + " <" + TemporalEntity.TIME + "> .\n");
		output.append("@prefix " + GeographicalPosition.WGS84_PREFIX + " <" + GeographicalPosition.WGS84 + "> .\n");
		output.append("@prefix " + Weather.MUO_PREFIX + " <" + Weather.MUO_NAMESPACE + "> .\n");
		output.append("\n");
		
		String previousSubject = "";
		String previousPredicate = "";
		for(TurtleStatement statement : statements) {
			if(!previousSubject.equals("")) {
				if(previousSubject.equals(statement.getSubject())) {
					if(previousPredicate.equals(statement.getPredicate())) {
						output.append(" ,");
					}
					else {
						output.append(" ;");
					}
				}
				else {
					output.append(" .");
				}
				output.append(System.getProperty("line.separator"));
			}
			
			if(!previousSubject.equals("") && !previousSubject.substring(0, 2).equals("_:") && !statement.getSubject().equals(previousSubject)) {
				output.append(System.getProperty("line.separator"));
			}
		
			if(!previousSubject.equals(statement.getSubject())) {
				output.append(statement.getSubject());
				output.append(repeat(" ", subjectWidth - statement.getSubject().length()));
			}
			else {
				output.append(repeat(" ", subjectWidth));
			}
			
			if(!previousSubject.equals(statement.getSubject()) || !previousPredicate.equals(statement.getPredicate())) {
				output.append(statement.getPredicate());
				output.append(repeat(" ", predicateWidth - statement.getPredicate().length()));
			}
			else {
				output.append(repeat(" ", subjectWidth));
			}
			
			output.append(statement.getObject());
			
			previousSubject = statement.getSubject();
			previousPredicate = statement.getPredicate();
		}
		
		output.append(" .");
		output.append(System.getProperty("line.separator"));
		
		return output.toString();
	}

	public void addAll(TurtleStore turtle) {
		statements.addAll(turtle.statements);
	}

	public void add(TurtleStatement statement) {
		statements.add(statement);
	}
	
	private String repeat(String string, int count) {
		return new String(new char[count]).replace("\0", string);
	}
}
