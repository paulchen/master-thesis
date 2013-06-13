package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

import java.util.List;

/**
 * This class represents a weather phenomenon (temperature, precipitation etc.)
 * 
 * @author Paul Staroch
 * 
 */
public abstract class WeatherPhenomenon implements OntologyClass {
	/**
	 * Constructor for this class
	 */
	public WeatherPhenomenon() {
		/* nothing to do */
	}

	/**
	 * Constructor for this class.
	 * 
	 * @param phenomena
	 *            a list of instances of <tt>WeatherPhenomenon</tt>
	 * @throws IllegalArgumentException
	 *             if one item in this list does not have the same type as this
	 *             instance
	 */
	public WeatherPhenomenon(List<WeatherPhenomenon> phenomena)
			throws IllegalArgumentException {
		for (WeatherPhenomenon phenomenon : phenomena) {
			if (!phenomenon.getClass().equals(this.getClass())) {
				throw new IllegalArgumentException(
						"Can't use a list containing instances of different subclasses of WeatherPhenomenon.");
			}
		}
	}

	/**
	 * Given instances of <tt>WeatherPhenomenon</tt> for given start and end
	 * time (both instances are of the same type), this method sets the values
	 * of the current instance for the given &quot;current time&quot; using
	 * values calculated using linear interpolation.
	 * 
	 * @param name
	 *            the unique name of the newly created instance of
	 *            <tt>WeatherPhenomenon</tt>
	 * @param intervalStartPhenomenon
	 *            instance of <tt>WeatherPhenomenon</tt> for the start time
	 * @param intervalEndPhenomenon
	 *            instance of <tt>WeatherPhenomenon</tt> for the end time
	 * @param start
	 *            start time of the interval
	 * @param end
	 *            end time of the interval
	 * @param current
	 *            current time
	 */
	public abstract void interpolate(WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end,
			int current);

	/**
	 * Perform linear interpolation for values of type <tt>Double</tt>. A time
	 * interval is given by its start and end time; additionally, the values at
	 * the start and the end time of this interval are given. This method
	 * interpolates a value for the current time which is given by yet another
	 * parameter.
	 * 
	 * @param startValue
	 *            value at the start of the interval
	 * @param endValue
	 *            value at the end of the interval
	 * @param start
	 *            start time of the interval
	 * @param end
	 *            end time of the interval
	 * @param current
	 *            current time
	 * @return the interpolated value
	 */
	protected static double linearDoubleInterpolation(double startValue,
			double endValue, int start, int end, int current) {
		return startValue + (endValue - startValue)
				* ((double) current - start) / ((double) end - start);
	}

	/**
	 * Perform linear interpolation for values of type <tt>Float</tt>. A time
	 * interval is given by its start and end time; additionally, the values at
	 * the start and the end time of this interval are given. This method
	 * interpolates a value for the current time which is given by yet another
	 * parameter.
	 * 
	 * @param startValue
	 *            value at the start of the interval
	 * @param endValue
	 *            value at the end of the interval
	 * @param start
	 *            start time of the interval
	 * @param end
	 *            end time of the interval
	 * @param current
	 *            current time
	 * @return the interpolated value
	 */
	protected static float linearFloatInterpolation(float startValue,
			float endValue, int start, int end, int current) {
		return startValue + (endValue - startValue) * ((float) current - start)
				/ ((float) end - start);
	}

	/**
	 * Perform linear interpolation for values of type <tt>int</tt>. A time
	 * interval is given by its start and end time; additionally, the values at
	 * the start and the end time of this interval are given. This method
	 * interpolates a value for the current time which is given by yet another
	 * parameter.
	 * 
	 * @param startValue
	 *            value at the start of the interval
	 * @param endValue
	 *            value at the end of the interval
	 * @param start
	 *            start time of the interval
	 * @param end
	 *            end time of the interval
	 * @param current
	 *            current time
	 * @return the interpolated value
	 */
	protected static int linearIntInterpolation(int startValue, int endValue,
			int start, int end, int current) {
		return startValue
				+ (int) Math.round((double) (endValue - startValue)
						/ (double) (end - current));
	}

	/**
	 * Given instances of <tt>WeatherPhenomenon</tt> for given start and end
	 * time (both instances are of the same type), this method creates an
	 * instance of the same type for a given &quot;current time&quot; using
	 * values calculated using linear interpolation.
	 * 
	 * @param name
	 *            the unique name of the newly created instance of
	 *            <tt>WeatherPhenomenon</tt>
	 * @param intervalStartPhenomenon
	 *            instance of <tt>WeatherPhenomenon</tt> for the start time
	 * @param intervalEndPhenomenon
	 *            instance of <tt>WeatherPhenomenon</tt> for the end time
	 * @param start
	 *            start time of the interval
	 * @param end
	 *            end time of the interval
	 * @param current
	 *            current time
	 * @return the interpolated value
	 */
	public abstract WeatherPhenomenon createInterpolatedPhenomenon(String name,
			WeatherPhenomenon intervalStartPhenomenon,
			WeatherPhenomenon intervalEndPhenomenon, int start, int end,
			int current);

	@Override
	public abstract Object clone();

	public abstract void setName(String name);

	/**
	 * Rounds a number to the given number of decimals.
	 * 
	 * @param number
	 *            number to be rounded
	 * @param decimals
	 *            number of decimals
	 * @return the given number, rounded to the given number of decimals
	 */
	protected static double roundDouble(double number, int decimals) {
		double factor = Math.pow(10, decimals);
		return Math.round(number * factor) / factor;
	}

	/**
	 * Rounds a number to the given number of decimals.
	 * 
	 * @param number
	 *            number to be rounded
	 * @param decimals
	 *            number of decimals
	 * @return the given number, rounded to the given number of decimals
	 */
	protected static float roundFloat(float number, int decimals) {
		double factor = Math.pow(10, decimals);
		return (float) (Math.round(number * factor) / factor);
	}
}
