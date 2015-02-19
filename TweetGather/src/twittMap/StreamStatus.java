package twittMap;

import java.util.Date;


public class StreamStatus {
	long sId;
	String sName;
	String sText;
	double sLatitude;
	double sLongitude;
	Date sTime;
	
	public String toString() {
		return sId + "||" + sName + "||" + sLatitude+ "||" + sLongitude+ "||" + sTime +"||" +sText;
	}
}
