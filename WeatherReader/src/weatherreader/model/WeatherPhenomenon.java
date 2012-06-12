package weatherreader.model;

import java.util.List;

//TODO javadoc
public abstract class WeatherPhenomenon implements OntologyClass {
	public WeatherPhenomenon() {
		/* nothing to do */
	}
	
	public WeatherPhenomenon(List<WeatherPhenomenon> phenomena) throws IllegalArgumentException {
		for(WeatherPhenomenon phenomenon : phenomena) {
			if(!phenomenon.getClass().equals(this.getClass())) {
				throw new IllegalArgumentException(
					"Can't use a list containing instances of different subclasses of WeatherPhenomenon.");
			}
		}
	}
	
	public abstract void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current);
	
	protected static double linearDoubleInterpolation(double startValue, double endValue,
			int end, int current) {
		return startValue + (endValue - startValue)/(end-current);
	}
	
	protected static float linearFloatInterpolation(float startValue, float endValue,
			int end, int current) {
		return startValue + (endValue - startValue)/(end-current);
	}
	
	protected static int linearIntInterpolation(int startValue, int endValue,
			int end, int current) {
		return startValue + (int)Math.round((double)(endValue - startValue)/(double)(end-current));
	}

	public abstract WeatherPhenomenon createInterpolatedPhenomenon(
			String name, WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int end, int current);
	
	@Override
	public abstract Object clone();

	public abstract void setName(String name);
	
	protected static double roundDouble(double number, int decimals) {
		double factor = Math.pow(10, decimals);
		return Math.round(number * factor) / factor;
	}
	
	protected static float roundFloat(float number, int decimals) {
		double factor = Math.pow(10, decimals);
		return (float)(Math.round(number * factor) / factor);
	}
}
