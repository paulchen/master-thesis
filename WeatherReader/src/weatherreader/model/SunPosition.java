package weatherreader.model;

// TODO WeatherPhenomenon; correctly merge in WeatherState
public class SunPosition {
	private double azimuth;
	private double zenith;
	private double elevation;

	public SunPosition(double zenith, double azimuth) {
		this.zenith = zenith;
		this.elevation = 90 - zenith;
		this.azimuth = azimuth;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}

	public double getZenith() {
		return zenith;
	}

	public void setZenith(double zenith) {
		this.zenith = zenith;
		this.elevation = 90 - zenith;
	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
		this.zenith = 90 - elevation;
	}
	
	public String toString() {
		String output = "";
		
		output += "azimuth=" + azimuth + "; ";
		output += "zenith=" + zenith + "; ";
		output += "elevation=" + elevation;
		
		return output;
	}
}
