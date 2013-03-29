package at.ac.tuwien.auto.thinkhome.weatherimporter.turtle;

import java.util.LinkedHashSet;
import java.util.Set;

public class TurtleStore {
	private final int bufferSpace = 3;
	
	private Set<TurtleStatement> statements;
	
	public TurtleStore() {
		this.statements = new LinkedHashSet<TurtleStatement>();
	}
	
	// TODO namespaces
	public String printAll() {
		int subjectWidth = 0;
		int predicateWidth = 0;
		
		for(TurtleStatement statement : statements) {
			subjectWidth = Math.max(subjectWidth, statement.getSubject().length());
			predicateWidth = Math.max(predicateWidth, statement.getPredicate().length());
			
			if(statement.getPredicate().equals("rdf:type")) {
				statement.setPredicate("a");
			}
		}

		subjectWidth += bufferSpace;
		predicateWidth += bufferSpace;
		
		StringBuffer output = new StringBuffer();
		
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
