package at.ac.tuwien.auto.thinkhome.weatherimporter.model;

public final class WeatherConstants {
	public static final int DECIMALS = 2;
	public static final String NAMESPACE = "http://www.semanticweb.org/ontologies/2011/9/ThinkHomeWeather.owl#";
	public static final String NAMESPACE_PREFIX = "weather:";
	
	public static final String WGS84 = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String WGS84_PREFIX = "wgs:";
	
	public static final String TIME = "http://www.w3.org/2006/time#";
	public static final String TIME_PREFIX = "time:";
	
	public static final String MUO_NAMESPACE = "http://purl.oclc.org/NET/muo/muo#";
	public static final String MUO_PREFIX = "muo:";
	
	public static final String UNIT_METER = "http://purl.oclc.org/NET/muo/ucum/unit/length/meter";
	public static final String UNIT_DEGREES_CELSIUS = "http://purl.oclc.org/NET/muo/ucum/unit/temperature/degree-Celsius";
	public static final String UNIT_DEGREE = "http://purl.oclc.org/NET/muo/ucum/unit/plane-angle/degree";
	public static final String UNIT_PERCENT = "http://purl.oclc.org/NET/muo/ucum/unit/fraction/percent";
	
	private WeatherConstants() {
		/* do nothing */
	}
}
