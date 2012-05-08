package weatherreader.model;

public class GeographicalPosition {
	private float latitude;
	private float longitude;
	private float altitude;
	
	public GeographicalPosition(float latitude, float longitude) {
		this(latitude, longitude, 0);
	}
	
	public GeographicalPosition(float latitude, float longitude, float altitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	protected float getAltitude() {
		return altitude;
	}

	protected void setAltitude(float altitude) {
		this.altitude = altitude;
	}
}
