package at.ac.tuwien.auto.thinkhome.weatherimporter.turtle;

import java.util.LinkedHashSet;
import java.util.Set;

public class TurtleStore {
	private final int bufferSpace = 3;
	
	private Set<TurtleStatement> statements;
	
	public TurtleStore() {
		this.statements = new LinkedHashSet<TurtleStatement>();
	}
	
	public String printAll() {
		// TODO simplify statements (e.g. rdf:type -> a etc.)
		
		int subjectWidth = 0;
		int predicateWidth = 0;
		
		for(TurtleStatement statement : statements) {
			subjectWidth = Math.max(subjectWidth, statement.getSubject().length());
			predicateWidth = Math.max(predicateWidth, statement.getPredicate().length());
		}

		subjectWidth += bufferSpace;
		predicateWidth += bufferSpace;
		
		// TODO simplifications (store last subject, last predicate)
		// TODO separation lines
		StringBuffer output = new StringBuffer();
		
		String previousSubject = "";
		for(TurtleStatement statement : statements) {
			if(!previousSubject.equals("") && !previousSubject.substring(0, 2).equals("_:") && !statement.getSubject().equals(previousSubject)) {
				output.append(System.getProperty("line.separator"));
			}
			previousSubject = statement.getSubject();
			
			output.append(statement.getSubject());
			output.append(repeat(" ", subjectWidth - statement.getSubject().length()));
			
			output.append(statement.getPredicate());
			output.append(repeat(" ", predicateWidth - statement.getPredicate().length()));
			
			output.append(statement.getObject());
			
			output.append(" .");
			output.append(System.getProperty("line.separator"));
		}
		
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
