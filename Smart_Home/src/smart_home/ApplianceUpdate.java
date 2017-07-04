package smart_home;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class ApplianceUpdate {
	private static final String Key="DataUpdate";
	
	
	public static void update(Integer KWH,Integer id,String name,String Color,Integer Period,String URL){
		ResultSet rs = null;
		Statement statement=null;
		Connection con=null;
		boolean hasUpdated=false;
		Integer initialPeriod=null;
		synchronized(ApplianceUpdate.class){
			
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
				String sql = "select * from "+Data.appliance+"";// where id =" + String.valueOf(id);
				if(id!=null){
					rs = statement.executeQuery(sql);
					if (rs != null) {
						
						while (rs.next()) {
							if (rs.getInt("id")==id){
								if (Data.debugstatus.get(Key)){
								System.out.println("dataupdateA");}
								if(KWH!=null){
									rs.updateInt("KWH", KWH);
								}
								if (name!=null){
									rs.updateString("name", name);
								}
								if (Color!=null){
									rs.updateString("Color", Color);
								}
								if (URL!=null){
									rs.updateString("URL", URL);
								}
								rs.updateTimestamp("lastactivetime", null);
								rs.updateRow();
								
								hasUpdated=true;
								break;
							}
						}
						if (!hasUpdated){
							if (Data.debugstatus.get(Key)){
							System.out.println("dataCreateA");}
							rs.beforeFirst();
							if (rs.next()){
								initialPeriod=rs.getInt("Period");
								rs.previous();
							}
							PreparedStatement prestatement = con.prepareStatement("insert into "+Data.appliance+" (id,name,KWH,Color,URL,Period) values(?,?,?,?,?,?)");
							prestatement.setInt(1, id);
							if (name!=null){
								prestatement.setString(2, name);}
							else{prestatement.setString(2, "no name");}
							if (KWH!=null){
								prestatement.setInt(3, KWH);
							}else{prestatement.setInt(3, 0);}
							if (Color!=null){
								prestatement.setString(4, Color);
							}else{prestatement.setString(4, "Undefined");}
							if (URL!=null){
								prestatement.setString(5, URL);
							}else{prestatement.setString(5, "");}
							if (Period!=null){
								prestatement.setInt(6, Period);
							}else{prestatement.setInt(6, -1);}
							prestatement.executeUpdate();
							prestatement.close();
						}
						rs.close();
					} else {
						if (Data.debugstatus.get(Key)){
						System.out.println("NdataCreateA");}
						PreparedStatement prestatement = con.prepareStatement("insert into "+Data.appliance+" (id,name,KWH,Color,URL,Period) values(?,?,?,?,?,?)");
						prestatement.setInt(1, id);
						if (name!=null){
							prestatement.setString(2, name);}
						else{prestatement.setString(2, "no name");}
						if (KWH!=null){
							prestatement.setInt(3, KWH);
						}else{prestatement.setInt(3, 0);}
						if (Color!=null){
							prestatement.setString(4, Color);
						}else{prestatement.setString(4, "Undefined");}
						if (URL!=null){
							prestatement.setString(5, URL);
						}else{prestatement.setString(5, "");}
						if (Period!=null){
							prestatement.setInt(6, Period);
						}else{prestatement.setInt(6, -1);}
						prestatement.executeUpdate();
						prestatement.close();
						// ResultSet set = prestatement.executeQuery();
					}
				}
				
				
				
				if (Period!=null){
					initialPeriod=Period;
				}
				if (initialPeriod!=null){
					rs = statement.executeQuery(sql);
					boolean firsted=false;
					while(rs.next()){
						rs.updateInt("Period", initialPeriod);
						if (!firsted){
						rs.updateTimestamp("lastactivetime", null);
						}
						rs.updateRow();
						firsted=true;
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("dataError");
				e.printStackTrace();
			}finally{
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
		}
		
	}
	
	
}
