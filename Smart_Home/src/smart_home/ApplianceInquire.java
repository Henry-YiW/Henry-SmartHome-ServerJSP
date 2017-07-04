package smart_home;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import net.sf.json.JSONObject;

public class ApplianceInquire {
	private static final String Key="DataInquire";
	
	

	public static JSONObject getData(int tempid,int InquireType) {
		String id;
		if (tempid>0){
			id=" where id="+String.valueOf(tempid);
		}else{
			id="";
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
			String sql = "select * from "+Data.appliance+"";
			rs = statement.executeQuery(sql);
			while (rs!=null&&rs.next()){
				boolean set=false;
				switch (InquireType){
				case Data.Type_InquireAll:
				case Data.Type_InquireConfigurations:
					if (rs.getRow()==1){
						json.accumulate("Period", rs.getInt("Period"));
					}
					if (!id.isEmpty()){
						if (rs.getInt("id")==tempid){
							json.accumulate("id", rs.getInt("id"));
							json.accumulate(Data.appliance+"-name", rs.getString("name"));
							json.accumulate("URL", rs.getString("URL"));
							set=true;
						}
					}else{
						json.accumulate("id", rs.getInt("id"));
						json.accumulate(Data.appliance+"-name", rs.getString("name"));
						json.accumulate("URL", rs.getString("URL"));
					}
					
					if (InquireType!=Data.Type_InquireAll){
						break;
					}
				case Data.Type_InquireData:
					if (!id.isEmpty()){
						if (rs.getInt("id")==tempid){
							json.accumulate("KWH", rs.getInt("KWH"));
							//json.accumulate("Color", rs.getString("Color"));
							set=true;
						}
					}else{
						json.accumulate("KWH", rs.getInt("KWH"));
						//json.accumulate("Color", rs.getString("Color"));
					}
					
					if (InquireType!=Data.Type_InquireAll){
						if (!id.isEmpty()){
							if (rs.getInt("id")==tempid){
								json.accumulate("id", rs.getInt("id"));
								json.accumulate(Data.appliance+"-name", rs.getString("name"));
							}
						}else{
							json.accumulate("id", rs.getInt("id"));
							json.accumulate(Data.appliance+"-name", rs.getString("name"));
						}
						break;
					}
				}
				json.accumulate("Color", rs.getString("Color"));
				if (set){
					break;
				}
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
