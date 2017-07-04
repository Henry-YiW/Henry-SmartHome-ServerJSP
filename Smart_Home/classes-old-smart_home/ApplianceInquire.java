package smart_home;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONObject;

public class ApplianceInquire {
	//private static final String Key="DataInquire";
	
	static {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JSONObject getData() {
		String url = "jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
		ResultSet rs = null;Statement statement = null;Connection con = null;JSONObject json = new JSONObject();
		try {
			con = DriverManager.getConnection(url);
			statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String sql = "select * from appliance";
			rs = statement.executeQuery(sql);
			while (rs != null && rs.next()) {
				json.accumulate("name", rs.getString("name"));
				json.accumulate("KWH", rs.getInt("KWH"));
				json.accumulate("Color", rs.getString("Color"));
				json.accumulate("id", rs.getInt("id"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null){
					rs.close();
				}
				if(statement!=null){
					statement.close();
				}
				if(con!=null){
					con.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return json;
	}
}
