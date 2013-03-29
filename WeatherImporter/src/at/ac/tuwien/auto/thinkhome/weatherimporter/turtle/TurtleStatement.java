package at.ac.tuwien.auto.thinkhome.weatherimporter.turtle;

// TODO comparable
// TODO equals, hashCode
// TODO simplification: rdf:type -> a etc.
public class TurtleStatement {
	private String subject;
	private String predicate;
	private String object;
	
	public TurtleStatement(String subject, String predicate, String object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	protected String getSubject() {
		return subject;
	}

	protected String getPredicate() {
		return predicate;
	}

	protected String getObject() {
		return object;
	}
}
