package smart_home;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import net.sf.json.JSONObject;

public class MacroAtmosphericDataInquire {
	private static final String Key="DataInquire";
	private static final String TemperatureFix=Data.Temperature;
	private static final String HumidityFix=Data.Humidity;
	
	

	public static JSONObject getData(String Type,int InquireType) {
		String TypeFix;
		switch (Type){
		case Data.Temperature:TypeFix=TemperatureFix;break;
		case Data.Humidity:TypeFix=HumidityFix;break;
		default:return null;
		}
		
		ResultSet rs = null;Statement statement = null;Connection con = null;JSONObject json = new JSONObject();
		try {
			Future<Connection> Fcon=Data.ds.getConnectionAsync();
			if (!Fcon.isDone()){
				if (Data.debugstatus.get(Key)){
					System.out.println(Key+":Connection is not yet available. We need to wait");}
				int trytimes=0;
				while (!Fcon.isDone()){
					if (trytimes<100){
						Thread.sleep(200);
						trytimes++;
					}else {
						throw new TimeoutException("Wait Too Long, Something Wrong, Got to Quit and Restart");
					}
				}
				if (Data.debugstatus.get(Key)){
					System.out.println(Key+":We have Got A Connection");}
			}
			con=Fcon.get();
			statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String sql = "select * from " + TypeFix;
			rs = statement.executeQuery(sql);
			while (rs != null && rs.next()) {
				boolean set=false;
				switch (InquireType){
				case Data.Type_InquireAll:
				case Data.Type_InquireData:
					if (rs.getInt("id")>0){
						json.accumulate(TypeFix+"-Time", rs.getString("Time"));
						json.accumulate(TypeFix+"-Degree", rs.getInt("Degree"));
					}
					if (InquireType!=Data.Type_InquireAll){
						break;
					}
				case Data.Type_InquireConfigurations:
					if (rs.getInt("id")==-1){
						json.accumulate("Period", rs.getInt("Degree"));
						json.accumulate("URL", rs.getString("URL"));
						if (InquireType!=Data.Type_InquireAll){
							set=true;
						}
					}
					if (InquireType!=Data.Type_InquireAll){
						break;
					}
				}
				if (set){
					break;
				}
				//if (rs.getInt("id")<0){
				//	continue;
				//}
				//json.accumulate(TypeFix+"-Time", rs.getString("Time"));
				//json.accumulate(TypeFix+"-Degree", rs.getInt("Degree"));
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
