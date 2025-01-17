package at.ac.tuwien.auto.thinkhome.weatherimporter.main;

/**
 * Represents an ontological statement consisting of subject, predicate, and
 * object in Turtle syntax. In this class, all three parts of the statement are
 * represented by instances of <tt>String</tt>.
 * 
 * @author Paul Staroch
 * 
 */
public class TurtleStatement implements Comparable<TurtleStatement> {
	/**
	 * Subject of the statement
	 */
	private String subject;

	/**
	 * Predicate of the statement; if it is "rdf:type", it is automatically
	 * replaced by "a".
	 */
	private String predicate;

	/**
	 * Object of the statement
	 */
	private String object;

	/**
	 * The constructor.
	 * 
	 * @param subject
	 *            the subject of the statement
	 * @param predicate
	 *            the predicate of the statement
	 * @param object
	 *            the object of the statement
	 */
	public TurtleStatement(String subject, String predicate, String object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;

		if (predicate.equals("rdf:type")) {
			this.predicate = "a";
		}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result
				+ ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TurtleStatement other = (TurtleStatement) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	@Override
	public int compareTo(TurtleStatement that) {
		if (getSubject().equals(that.getSubject())) {
			if (getPredicate().equals(that.getSubject())) {
				return getObject().compareTo(that.getObject());
			}
			return getPredicate().compareTo(that.getPredicate());
		}
		return getSubject().compareTo(that.getSubject());
	}
}
