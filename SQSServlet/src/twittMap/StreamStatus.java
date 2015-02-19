package twittMap;

import java.util.Date;

import twitter4j.JSONException;
import twitter4j.JSONObject;


public class StreamStatus {
	long sId;
	String sName;
	String sText;
	double sLatitude;
	double sLongitude;
	Date sTime;
	
	public String toString() {
		JSONObject jobj = new JSONObject();
		try {
			jobj.put("sId", sId);
			jobj.put("sName", sName);
			jobj.put("sText", sText);
			jobj.put("sLatitude", sLatitude);
			jobj.put("sLongitude", sLongitude);
			jobj.put("sTime", sTime);			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return jobj.toString();
	}
}
