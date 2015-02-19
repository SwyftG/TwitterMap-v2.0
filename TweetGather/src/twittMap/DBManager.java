package twittMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;

public class DBManager {
	Connection conn;
	Statement stmt;
	String url;
	String user;
	String password;
	String sql;
	ResultSet rs;
	String queryFormat = "(select sLatitude, sLongitude from status "
			+ "where sDate>\'%s\' and sDate<\'%s\') " + "union "
			+ "(select sLatitude, sLongitude from status "
			+ "where sDate='%s' and sTime>='%s') " + "union "
			+ "(select sLatitude, sLongitude from status "
			+ "where sDate='%s' and sTime<='%s')";
	String queryFormatSameDate = "select sLatitude, sLongitude from status "
			+ "where sDate=\'%s\' and sTime between \'%s\' and \'%s\' ";

	public DBManager() {
		conn = null;
		stmt = null;
		url = null;
		user = null;
		password = null;
		sql = null;
		rs = null;
	}

	public void getDirver() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("load driver error");
			e.printStackTrace();
		}
	}

	public void connect() {
		try {
			url = "jdbc:mysql://localhost:3306/twittmap";
			user = "root";
			password = "";
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("connect error");
			e.printStackTrace();
		}
	}

	public void connectAWS() {
		try {
			url = "jdbc:mysql://twittmap.czbkhl5yewet.ap-northeast-1.rds.amazonaws.com:3306/twittmap";
			user = "root";
			password = "roottoor";
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("connect error");
			e.printStackTrace();
		}
	}

	public void update(StreamStatus streamStatus) {
		long sId = streamStatus.sId;
		String sName = streamStatus.sName;
		String sText = streamStatus.sText;
		double sLatitude = streamStatus.sLatitude;
		double sLongitude = streamStatus.sLongitude;
		java.sql.Time sTime = new java.sql.Time(streamStatus.sTime.getTime());
		java.sql.Date sDate = new java.sql.Date(streamStatus.sTime.getTime());
		try {
			stmt = conn.createStatement();
			PreparedStatement updateStatus = null;
			sql = "insert into status values(?,?,?,?,?,?,?)";

			try {
				updateStatus = conn.prepareStatement(sql);
				updateStatus.setLong(1, sId);
				updateStatus.setString(2, sName);
				updateStatus.setDouble(3, sLatitude);
				updateStatus.setDouble(4, sLongitude);
				updateStatus.setString(5, sText);
				updateStatus.setTime(6, sTime);
				updateStatus.setDate(7, sDate);
				// updateStatus.setdat

				updateStatus.executeUpdate();
			} catch (SQLException e) {
				// System.out.println(sId + "||" +sText);
			} finally {
				updateStatus.close();
			}
		} catch (SQLException e) {
			System.out.println("sql error");
			e.printStackTrace();
		}
	}

	public String queryNum() {
		String out = null;
		try {
			stmt = conn.createStatement();
			sql = "select count(*) as num from status ";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				out = rs.getString("num") + "";
				// System.out.println(out);
			}

		} catch (SQLException e) {
			System.out.println("sql error");
			e.printStackTrace();
		}
		return out;
	}

	public void shutdown() {
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (Exception e) {
			System.out.println("close db error");
			e.printStackTrace();
		}
	}

	public List<List<String>> queryLatLng(String begindate, String begintime,
			String enddate, String endtime) {
		List<List<String>> result = new ArrayList<List<String>>();
		try {
			stmt = conn.createStatement();
			if (begindate.equals(enddate))
				sql = String.format(queryFormatSameDate, begindate, begintime,
						endtime);
			else
				sql = String.format(queryFormat, begindate, enddate, begindate,
						begintime, enddate, endtime);
			System.out.println(sql);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				List<String> pair = new ArrayList<String>();
				pair.add(rs.getString("sLatitude"));
				pair.add(rs.getString("sLongitude"));
				result.add(pair);
			}
		} catch (SQLException e) {
			System.out.println("sql error");
			e.printStackTrace();
		}
		return result;
	}
	
		/*
	 * insert text analyse into attitude table
	 */
	public void insertAttitude(long sId, String polarity, String score) {
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = 
		     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		try {
			stmt = conn.createStatement();
			PreparedStatement updateStatus = null;
			sql = "insert into attitude values(?,?,?,?)";

			try {
				updateStatus = conn.prepareStatement(sql);
				updateStatus.setLong(1, sId);
				updateStatus.setString(2, polarity);
				updateStatus.setString(3, score);
				updateStatus.setString(4, currentTime);
				
				updateStatus.executeUpdate();
			} catch (SQLException e) {
				//System.out.println(sId + "||" +sText);
			} finally {
				updateStatus.close();
			}
		} catch (SQLException e) {
			System.out.println("sql error");
			e.printStackTrace();
		}
	}
	/*
	 * get all between begin and end from attitude table and output json. Time format could be "2014-12-1 14:14:51".
	 */
	public String getJsonFromAttitude(String begin, String end) {
		JSONObject obj = new JSONObject();
		JSONArray jarray = new JSONArray();
		try {
			try {
				stmt = conn.createStatement();
				PreparedStatement preparedStatement;
				if( end == null	){
					sql = "select * from attitude where insertDateTime > ?";
					preparedStatement = conn.prepareStatement(sql);
					preparedStatement.setString(1, begin);
				}
				else{
					sql = "select * from attitude where insertDateTime between ? and ?";
					preparedStatement = conn.prepareStatement(sql);
					preparedStatement.setString(1, begin);
					preparedStatement.setString(2, end);
				}
				
				System.out.println(preparedStatement);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					String sid = rs.getString("sid");
					String polarity = rs.getString("polarity");
					String score = rs.getString("score");
					// String insertDateTime = rs.getString("insertDateTime");
					JSONObject ob = new JSONObject();
					ob.put("sid", sid);
					ob.put("polarity", polarity);
					ob.put("score", score);
					// ob.put("insertDateTime", insertDateTime);
					jarray.put(ob);
				}

			} catch (SQLException e) {
				System.out.println("sql error");
				e.printStackTrace();
			}
			obj.put("outcome", jarray);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return obj.toString();
	}
	
	public String getJsonFromAttitude(String begin){
		return getJsonFromAttitude(begin, null);
	}
	
	/*
	 * public static void main(String args[]) { DBManager ma = new DBManager();
	 * ma.getDirver(); ma.connectAWS(); ma.queryNum(); ma.shutdown(); }
	 */

}
