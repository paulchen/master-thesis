package weatherreader.model;

// TODO javadoc
public class CloudLayer {
	private int altitude;
	private int coverage;
	public CloudLayer(int altitude, int coverage) {
		super();
		this.altitude = altitude;
		this.coverage = coverage;
	}
	public int getAltitude() {
		return altitude;
	}
	public int getCoverage() {
		return coverage;
	}
	@Override
	public String toString() {
		return "[altitude=" + altitude + "; coverage=" + coverage + "]";
	}
}
