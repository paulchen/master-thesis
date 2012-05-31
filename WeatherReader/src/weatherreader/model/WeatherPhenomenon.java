package weatherreader.model;

import java.util.List;

//TODO javadoc
public abstract class WeatherPhenomenon implements OntologyClass {
	public WeatherPhenomenon() {
		/* nothing to do */
	}
	
	public WeatherPhenomenon(List<WeatherPhenomenon> phenomena) {
		for(WeatherPhenomenon phenomenon : phenomena) {
			if(!phenomenon.getClass().equals(this.getClass())) {
				// TODO initiate apocalypse
			}
		}
	}
	
	public abstract void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current);
	
	// TODO do not round here?
	protected float linearDoubleInterpolation(double startValue, double endValue,
			int end, int current) {
		return Math.round((startValue + (endValue - startValue)/(end-current))*100)/100f;
	}
	
	protected float linearFloatInterpolation(float startValue, float endValue,
			int end, int current) {
		return Math.round((startValue + (endValue - startValue)/(end-current))*100)/100f;
	}
	
	protected int linearIntInterpolation(int startValue, int endValue,
			int end, int current) {
		return startValue + (endValue - startValue)/(end-current);
	}

	public abstract WeatherPhenomenon createInterpolatedPhenomenon(
			String name, WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current);
}
