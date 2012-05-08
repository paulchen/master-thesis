package weatherreader;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class SunPosition {
	public static void main(String[] args) {
		SunPosition position = new SunPosition();
	
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
		calendar.set(2012, 4, 8, 14, 0, 0);
		System.out.println(position.calculate(48f, 16f, calendar.getTime()));
//		System.out.println(position.calculate(48f, 16f, new Date()));
	}

	// TODO new types for lat/lon?
	// TODO lat/lon as instance variables
	private String calculate(float latitude, float longitude, Date date) {
		double dZenithAngle;
		double dAzimuth;
		
		double dEarthMeanRadius = 6371.01;
		double dAstronomicalUnit = 149597890;
		
		// Main variables
		double dElapsedJulianDays;
		double dDecimalHours;
		double dEclipticLongitude;
		double dEclipticObliquity;
		double dRightAscension;
		double dDeclination;

		// Auxiliary variables
		double dY;
		double dX;

		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		
		// Calculate difference in days between the current Julian Day 
		// and JD 2451545.0, which is noon 1 January 2000 Universal Time
		{
			double dJulianDate;
			long liAux1;
			long liAux2;
			// Calculate time of the day in UT decimal hours
			dDecimalHours = calendar.get(Calendar.HOUR_OF_DAY) + (calendar.get(Calendar.MINUTE)
				+ calendar.get(Calendar.SECOND) / 60.0 ) / 60.0;
			// Calculate current Julian Day
			liAux1 =(calendar.get(Calendar.MONTH)+1-14)/12;
			liAux2=(1461*(calendar.get(Calendar.YEAR) + 4800 + liAux1))/4 + (367*(calendar.get(Calendar.MONTH)+1 
				- 2-12*liAux1))/12- (3*((calendar.get(Calendar.YEAR) + 4900 
			+ liAux1)/100))/4+calendar.get(Calendar.DAY_OF_MONTH)-32075;
			dJulianDate=(double)(liAux2)-0.5+dDecimalHours/24.0;
			// Calculate difference between current Julian Day and JD 2451545.0 
			dElapsedJulianDays = dJulianDate-2451545.0;
		}

		// Calculate ecliptic coordinates (ecliptic longitude and obliquity of the 
		// ecliptic in radians but without limiting the angle to be less than 2*Pi 
		// (i.e., the result may be greater than 2*Pi)
		{
			double dMeanLongitude;
			double dMeanAnomaly;
			double dOmega;
			dOmega=2.1429-0.0010394594*dElapsedJulianDays;
			dMeanLongitude = 4.8950630+ 0.017202791698*dElapsedJulianDays; // Radians
			dMeanAnomaly = 6.2400600+ 0.0172019699*dElapsedJulianDays;
			dEclipticLongitude = dMeanLongitude + 0.03341607*Math.sin( dMeanAnomaly ) 
				+ 0.00034894*Math.sin( 2*dMeanAnomaly )-0.0001134
				-0.0000203*Math.sin(dOmega);
			dEclipticObliquity = 0.4090928 - 6.2140e-9*dElapsedJulianDays
				+0.0000396*Math.cos(dOmega);
		}

		// Calculate celestial coordinates ( right ascension and declination ) in radians 
		// but without limiting the angle to be less than 2*Pi (i.e., the result may be 
		// greater than 2*Pi)
		{
			double dSin_EclipticLongitude;
			dSin_EclipticLongitude= Math.sin( dEclipticLongitude );
			dY = Math.cos( dEclipticObliquity ) * dSin_EclipticLongitude;
			dX = Math.cos( dEclipticLongitude );
			dRightAscension = Math.atan2( dY,dX );
			if( dRightAscension < 0.0 ) dRightAscension = dRightAscension + 2*Math.PI;
			dDeclination = Math.asin( Math.sin( dEclipticObliquity )*dSin_EclipticLongitude );
		}

		// Calculate local coordinates ( azimuth and zenith angle ) in degrees
		{
			double dGreenwichMeanSiderealTime;
			double dLocalMeanSiderealTime;
			double dLatitudeInRadians;
			double dHourAngle;
			double dCos_Latitude;
			double dSin_Latitude;
			double dCos_HourAngle;
			double dParallax;
			dGreenwichMeanSiderealTime = 6.6974243242 + 
				0.0657098283*dElapsedJulianDays 
				+ dDecimalHours;
			dLocalMeanSiderealTime = (dGreenwichMeanSiderealTime*15 
				+ longitude)*Math.PI/180;
			dHourAngle = dLocalMeanSiderealTime - dRightAscension;
			dLatitudeInRadians = latitude*Math.PI/180;
			dCos_Latitude = Math.cos( dLatitudeInRadians );
			dSin_Latitude = Math.sin( dLatitudeInRadians );
			dCos_HourAngle= Math.cos( dHourAngle );
			dZenithAngle = (Math.acos( dCos_Latitude*dCos_HourAngle
				*Math.cos(dDeclination) + Math.sin( dDeclination )*dSin_Latitude));
			dY = -Math.sin( dHourAngle );
			dX = Math.tan( dDeclination )*dCos_Latitude - dSin_Latitude*dCos_HourAngle;
			dAzimuth = Math.atan2( dY, dX );
			if ( dAzimuth < 0.0 ) 
				dAzimuth = dAzimuth + 2*Math.PI;
			dAzimuth = dAzimuth*180/Math.PI;
			// Parallax Correction
			dParallax=(dEarthMeanRadius/dAstronomicalUnit)
				*Math.sin(dZenithAngle);
			dZenithAngle=(dZenithAngle 
				+ dParallax)*180/Math.PI;
		}
		
		System.out.println(dElapsedJulianDays);
		System.out.println(dDecimalHours);
		System.out.println(dEclipticLongitude);
		System.out.println(dEclipticObliquity);
		System.out.println(dRightAscension);
		System.out.println(dDeclination);
		
		return dZenithAngle + " " + dAzimuth;
	}
}
