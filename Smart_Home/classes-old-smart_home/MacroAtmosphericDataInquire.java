package smart_home;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONObject;

public class MacroAtmosphericDataInquire {
	private static final String Key="DataInquire";
	private static final String TemperatureFix="Temperature";
	private static final String HumidityFix="Humidity";
	
	static {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JSONObject getData(String Type) {
		String TypeFix;
		switch (Type){
		case Data.Temperature:TypeFix=TemperatureFix;break;
		case Data.Humidity:TypeFix=HumidityFix;break;
		default:return null;
		}
		
		String url = "jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
		ResultSet rs = null;Statement statement = null;Connection con = null;JSONObject json = new JSONObject();
		try {
			con = DriverManager.getConnection(url);
			statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String sql = "select * from " + TypeFix;
			rs = statement.executeQuery(sql);
			while (rs != null && rs.next()) {
				if (rs.getInt("id")<0){
					continue;
				}
				json.accumulate(TypeFix+"-Time", rs.getString("Time"));
				json.accumulate(TypeFix+"-Degree", rs.getInt("Degree"));
			}
			sql="select * from ColorSet where name ='"+TypeFix+"'";
			rs.close();
			rs=statement.executeQuery(sql);String ColorSet="";
			while (rs != null && rs.next()) {
				for (int i=1;i<=18;i++){
					try{
						ColorSet+=rs.getString("c"+String.valueOf(i))+" ";
					}catch (Exception e){
						if (Data.debugstatus.get(Key)){
							System.out.print("::No Such Color::");
							
						}
					}
					
				}
			}
			ColorSet=ColorSet.trim().replace(' ', ',');
			json.accumulate(TypeFix+"-ColorSet", ColorSet);
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
