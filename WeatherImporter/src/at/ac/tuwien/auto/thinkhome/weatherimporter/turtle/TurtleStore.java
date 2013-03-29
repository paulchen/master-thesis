package at.ac.tuwien.auto.thinkhome.weatherimporter.turtle;

import java.util.HashSet;
import java.util.Set;

public class TurtleStore {
	private final int bufferSpace = 3;
	
	private Set<TurtleStatement> statements;
	
	public TurtleStore() {
		this.statements = new HashSet<TurtleStatement>();
	}
	
	public void printAll() {
		// TODO simplify statements
		// TODO sort statements
		// TODO dump
		
		int subjectWidth = 0;
		int predicateWidth = 0;
		int objectWidth = 0;
		
		for(TurtleStatement statement : statements) {
			subjectWidth = Math.max(subjectWidth, statement.getSubject().length());
			predicateWidth = Math.max(predicateWidth, statement.getPredicate().length());
			objectWidth = Math.max(objectWidth, statement.getObject().length());
		}

		subjectWidth += bufferSpace;
		predicateWidth += bufferSpace;
		objectWidth += bufferSpace;
		
		// TODO simplifications (store last subject, last predicate)
		for(TurtleStatement statement : statements) {
			StringBuffer output = new StringBuffer();
			
			output.append(statement.getSubject());
			output.append(repeat(" ", subjectWidth - statement.getSubject().length()));
			
			output.append(statement.getPredicate());
			output.append(repeat(" ", predicateWidth - statement.getPredicate().length()));
			
			output.append(statement.getObject());
			output.append(repeat(" ", objectWidth - statement.getObject().length()));
			
			System.out.println(output);
		}
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
