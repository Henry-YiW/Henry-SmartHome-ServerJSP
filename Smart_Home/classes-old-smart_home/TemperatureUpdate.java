package smart_home;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class TemperatureUpdate {
	private static final String Key="DataUpdate";
	private volatile  static int index = 1;
	private static int limit = 50;
	
	static {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void update(Integer Degree,Character UnitSign,Integer Period,String URL) throws NumberFormatException {
		ResultSet rs=null;Connection con=null;Statement statement=null;
		synchronized (TemperatureUpdate.class) {
			int count=1;
			if (Degree!=null){
				if (UnitSign!=null){
					switch (UnitSign){
					case 'C':
					case 'c':break;
					case 'F':
					case 'f':Degree=((Degree-32)*5)/9;break;
					case 'K':
					case 'k':Degree=(int) (Degree-273.15);break;
					default:throw new NumberFormatException("Not A Supported Unit");
					}
				}else {
					if (Data.debugstatus.get(Key)){
					System.out.println("No Correlative Unit Sign");}
				}
			}
			if (index>limit){
				index=1;
			}
			try {
				String url = "jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
				con = DriverManager.getConnection(url);
				statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				String sql = "select * from Temperature";
				rs = statement.executeQuery(sql);
				if (Degree!=null&&UnitSign!=null){
					while (true){
						if (rs!=null){
							if (rs.next()){
								if (rs.getInt("id")>0){
									if (rs.getInt("id")!=count){
										if (Data.debugstatus.get(Key)){
										System.out.println("IDataCreatedT");}
										PreparedStatement prestatement = con.prepareStatement("insert into Temperature (Degree,id)values(?,?)");
										prestatement.setInt(1, Degree);
										prestatement.setInt(2, count);
										prestatement.executeUpdate();
										index=1;
										prestatement.close();
										break;
									}
									count++;
								}
								
							}else if (count<=limit){
								if (Data.debugstatus.get(Key)){
								System.out.println("SDataCreatedT");}
								PreparedStatement prestatement = con.prepareStatement("insert into Temperature (Degree,id)values(?,?)");
								prestatement.setInt(1, Degree);
								prestatement.setInt(2, count);
								prestatement.executeUpdate();
								index = 1;
								prestatement.close();
								break;
							}else{
								if (Data.debugstatus.get(Key)){
								System.out.println("DataUpdatedT");}
								PreparedStatement prestatement = con.prepareStatement("update Temperature set Degree =? where id=" +String.valueOf(index));
								if (Degree!=0){
									prestatement.setInt(1, 0);
									prestatement.executeUpdate();
								}else{
									prestatement.setInt(1, 1);
									prestatement.executeUpdate();
								}
								prestatement.setInt(1, Degree);
								prestatement.executeUpdate();
								prestatement.close();
								//rs.absolute(index);用这种方式修改数据无法触发时间戳自动更新。
								//rs.updateInt("Degree", Degree);
								//rs.updateRow();
								index++;
								break;
							}
						}else {
							if (Data.debugstatus.get(Key)){
							System.out.println("NDataCreatedT");}
							PreparedStatement prestatement = con.prepareStatement("insert into Temperature (Degree,id)values(?,?)");
							prestatement.setInt(1, Degree);
							prestatement.setInt(2, 1);
							prestatement.executeUpdate();
							index = 1;
							prestatement.close();
							break;
						}
					}
				}
				
				updateConfigurations(Period,URL,rs,con);
			} catch (SQLException e) {
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
	
	private static void updateConfigurations(Integer Period,String URL,ResultSet rs,Connection con) throws SQLException{
		rs.beforeFirst();
		PreparedStatement prestatement=con.prepareStatement("insert into Humidity (Degree,id,URL)values(?,?,?)");
		if (Period!=null&&URL!=null){
			if (rs.next()&&rs.getInt("id")==-1){
				rs.updateInt("Degree", Period);
				rs.updateString("URL", URL);
				rs.updateTimestamp("Time", null);
				rs.updateRow();
			}else{
				prestatement.setInt(1, Period);
				prestatement.setInt(2,-1);
				prestatement.setString(3, URL);
				prestatement.executeUpdate();
			}
		}
		else if (Period!=null){
			if (rs.next()&&rs.getInt("id")==-1){
				rs.updateInt("Degree", Period);
				rs.updateTimestamp("Time", null);
				rs.updateRow();
			}else{
				prestatement.setInt(1, Period);
				prestatement.setInt(2,-1);
				prestatement.setString(3, "");
				prestatement.executeUpdate();
			}
			
		}
		else if (URL!=null){
			if (rs.next()&&rs.getInt("id")==-1){
				rs.updateString("URL", URL);
				rs.updateTimestamp("Time", null);
				rs.updateRow();
			}else {
				prestatement.setInt(1, -1);
				prestatement.setInt(2, -1);
				prestatement.setString(3, URL);
				prestatement.executeUpdate();
			}
		}
		if (prestatement!=null){
			prestatement.close();
		}
	}
}
