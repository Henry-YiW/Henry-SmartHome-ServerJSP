package smart_home;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegistrationPlusStateupdate
 */
@WebServlet("/RegistrationPlusStateupdate")
public class RegistrationPlusStateupdate extends HttpServlet {
	public static final String Key="ApparatusUpdate";
	private static final long serialVersionUID = 2L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegistrationPlusStateupdate() {
		super();
		// 其实这个初始化数据库驱动的代码，只需要所有的servlet中的一个运行一次就行了，
		// 但是谁知道那个会先被实例化（被客户端调用），所以就都给加上好了；
		//try {
		//	Class.forName("org.gjt.mm.mysql.Driver");
		//} catch (ClassNotFoundException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		if (request.getParameter("user")!=null&&request.getParameter("user").equals(Data.user)&&request.getParameter("pass")!=null&&request.getParameter("pass").equals(Data.pass)) {
			String fullURL;String name;String URL = request.getRemoteAddr();
			String URLfix;int passive;int persistentornot;int Alertable;int Alerted;int activated;   boolean Delete;String Type;
			String Item;String Regulation;Boolean enabled;
			if (request.getParameter("URL") == null){
				URLfix=null;
			}else{
				URLfix=request.getParameter("URL").trim();
			}
			if (request.getParameter("name")!=null&&request.getParameter("name").isEmpty()){
				name="no name";
			}else if(request.getParameter("name")!=null&&!request.getParameter("name").isEmpty()){
				name=request.getParameter("name").trim();
			}else{
				name=null;
			}
			if (request.getParameter("activated")!=null&&request.getParameter("activated").equals("true")){
				activated=Data.True;
			}else if (request.getParameter("activated")!=null&&!request.getParameter("activated").equals("true")){
				activated=Data.False;
			}else{
				activated=Data.Default;
			}
			if (request.getParameter("passive")!=null&&request.getParameter("passive").equals("true")){
				passive=Data.True;
			}else if (request.getParameter("passive")!=null&&!request.getParameter("passive").equals("true")){
				passive=Data.False;
			}else{
				passive=Data.Default;
			}
			if (request.getParameter("Per")!=null&&request.getParameter("Per").equals("true")){
				persistentornot=Data.True;
			}else if (request.getParameter("Per")!=null&&!request.getParameter("Per").equals("true")){
				persistentornot=Data.False;
			}else {
				persistentornot=Data.Default;
			}
			if (request.getParameter("Alertable")!=null&&request.getParameter("Alertable").equals("true")){
				Alertable=Data.True;
			}else if (request.getParameter("Alertable")!=null&&!request.getParameter("Alertable").equals("true")){
				Alertable=Data.False;
			}else {
				Alertable=Data.Default;
			}
			if (request.getParameter("Alerted")!=null&&request.getParameter("Alerted").equals("true")){
				Alerted=Data.True;
			}else if (request.getParameter("Alerted")!=null&&!request.getParameter("Alerted").equals("true")){
				Alerted=Data.False;
			}else {
				Alerted=Data.Default;
			}
			if (request.getParameter("Delete")!=null&&request.getParameter("Delete").equals("true")){
				Delete=true;
			}else {
				Delete=false;
			}
			Type = request.getParameter("Type")!=null?request.getParameter("Type").trim():null;
			Item = request.getParameter("Item")!=null?request.getParameter("Item").trim():null;
			Regulation = request.getParameter("Regulation")!=null?request.getParameter("Regulation").trim():null;
			if (request.getParameter("enabled")!=null&&request.getParameter("enabled").equals("true")){
				enabled=true;
			}else if (request.getParameter("enabled")!=null&&!request.getParameter("enabled").equals("true")){
				enabled=false;
			}else {
				enabled=null;
			}
			
			if (Data.debugstatus.get(Key)){System.out.println(name);}response.getWriter().println(name+"<br>");
			
			int tempid;
			if (request.getParameter("id")!=null&&!request.getParameter("id").isEmpty()){
				try {
					tempid=Integer.parseInt(request.getParameter("id").trim());
					fullURL = URLfix;
					if (Data.debugstatus.get(Key)){System.out.println(fullURL);}response.getWriter().println(fullURL+"<br>");
				}catch(Exception e){
					if (Data.debugstatus.get(Key)){
					System.out.println("Not a Valid id");}
					
					fullURL = "http://"+URL + URLfix;
					if (Data.debugstatus.get(Key)){System.out.println(fullURL);}response.getWriter().println(fullURL+"<br>");
					String[] URLTemp=URL.split("\\.");
					if (URLTemp.length==1){URLTemp=URL.split(":");}
					String trimedID=URLTemp[URLTemp.length-1];
					tempid = Integer.parseInt(trimedID);
					
				}
			}
			else {
				
				fullURL = "http://"+URL + URLfix;
				if (Data.debugstatus.get(Key)){System.out.println(fullURL);}response.getWriter().println(fullURL+"<br>");
				String[] URLTemp=URL.split("\\.");
				if (URLTemp.length==1){URLTemp=URL.split(":");}
				String trimedID=URLTemp[URLTemp.length-1];
				tempid = Integer.parseInt(trimedID);
				
			}
			if (URLfix!=null||name!=null||passive!=Data.Default||persistentornot!=Data.Default||Alertable!=Data.Default||Alerted!=Data.Default||activated!=Data.Default||
					Item!=null){
				if (URLfix!=null||name!=null||passive!=Data.Default||persistentornot!=Data.Default||Alertable!=Data.Default||Alerted!=Data.Default||activated!=Data.Default)
					updateData(tempid,name,URL,URLfix,fullURL,activated,passive,Alertable,Alerted,persistentornot,response);
				if (Item!=null)
					InquirePlusSetRegulations.setRule(tempid,Item,Regulation,enabled,response);
			}else if(Type!=null&&Type.equals(Data.Regulation)&&Delete){
				InquirePlusSetRegulations.deleteRules(tempid, Item, response);
			}else if (Type!=null&&Delete){
				Delete (Type,tempid,response);
			}else{
				System.out.println("ApparatusUpdate Illegal Content");
				response.sendError(400,"Illegal Content");
			}
			response.getWriter().close();
		}
		else {
			System.out.println();
			System.out.println("ApparatusUpdate Illegal Access");
			System.out.println("@"+request.getRemoteUser()+"$"+debugconfig.getRealRemoteAddr(request)+":"+request.getRemotePort()+"("+request.getRemoteHost()+")");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}
	}
	
	public static void updateData(int tempid,String name,String URL,String URLfix,String fullURL,int activatedstate,int passive,int Alertable,int Alerted,int persistentornot,HttpServletResponse response) throws IOException{
		ResultSet rs = null;
		Statement statement=null;
		Connection con=null;
		String id;
		if (tempid>0){
			 id = String.valueOf(tempid);
		}else {
			System.out.println("Apparatus Update Illegal ID");
			if(response!=null){
				response.sendError(400,"Illegal ID");
				//response.getWriter().println("Illegal Content"+"<br>");
				response.getWriter().close();
			}
			 return;
		}
		
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
			String sql = "select * from "+Data.apparatus+" where id =" + id;
			rs = statement.executeQuery(sql);
			if (rs != null && rs.next()) {
				if(response!=null){response.getWriter().println("dataupdate"+"<br>");}
				rs.previous();
				while (rs.next()) {
					Boolean temp=null;
					switch (activatedstate){
					case Data.True:temp=true;rs.updateBoolean("activated", true);break;
					case Data.False:temp=false;rs.updateBoolean("activated", false);break;
					default:break;
					}
					if ((Data.debugstatus.get(Key))&&((temp!=null&&temp!=rs.getBoolean("activated"))||(temp!=null&&Data.debugstatus.get(InquireConfigurations.Key)))){
						System.out.print("dataStatusUpdate||");
					}
					if (name!=null){
					rs.updateString("name", name);
					if(Data.debugstatus.get(Key)){
						System.out.println("dataNameUpdate");
					}
					}
					if (URLfix!=null){
					rs.updateString("URL", fullURL);
					if(Data.debugstatus.get(Key)){
						System.out.println("dataURLUpdate");
					}
					}
					temp=null;
					switch (passive){
					case Data.True:temp=true;rs.updateBoolean("passive", true);break;
					case Data.False:temp=false;rs.updateBoolean("passive", false);break;
					default:break;
					}
					if (Data.debugstatus.get(Key)&&temp!=null){
						System.out.println("dataPassiveUpdate");
					}
					temp=null;
					switch (persistentornot){
					case Data.True:temp=true;rs.updateBoolean("P", true);break;
					case Data.False:temp=false;rs.updateBoolean("P", false);break;
					default:break;
					}
					if (Data.debugstatus.get(Key)&&temp!=null){
						System.out.println("dataPersistentUpdate");
					}
					temp=null;
					switch (Alertable){
					case Data.True:temp=true;rs.updateBoolean("A", true);break;
					case Data.False:temp=false;rs.updateBoolean("A", false);break;
					default:break;
					}
					if (Data.debugstatus.get(Key)&&temp!=null){
						System.out.println("dataAlertableUpdate");
					}
					rs.updateDate("lastactivetime", null);
					rs.updateRow();
				}
			} else {
				if (Data.debugstatus.get(Key)){
				System.out.print("dataCreate||");}
				if(response!=null){response.getWriter().println("dataCreate"+"<br>");}
				PreparedStatement prestatement = con.prepareStatement("insert into "+Data.apparatus+" (id,name,URL,activated,passive,A,P) values(?,?,?,?,?,?,?)");
				prestatement.setInt(1, tempid);
				if (name!=null){
					prestatement.setString(2, name);
				}else{
					prestatement.setString(2, "no name");
				}
				if (URLfix!=null){
					prestatement.setString(3, fullURL);
				}else{
					prestatement.setString(3, "http://"+URL+"/arduino/web");
				}
				switch (activatedstate){
				case Data.True:prestatement.setBoolean(4, true);break;
				default:prestatement.setBoolean(4, false);break;
				}
				switch (passive){
				case Data.True:prestatement.setBoolean(5, true);break;
				default:prestatement.setBoolean(5, false);break;
				}
				switch (Alertable){
				case Data.True:prestatement.setBoolean(6, true);break;
				default:prestatement.setBoolean(6, false);break;
				}
				switch (persistentornot){
				case Data.True:prestatement.setBoolean(7, true);break;
				default:prestatement.setBoolean(7, false);break;
				}
				//prestatement.setTimestamp(6, null);
				prestatement.executeUpdate();
				prestatement.close();
				// ResultSet set = prestatement.executeQuery();
			}
			switch (Alerted){
			case Data.True:Data.handleAlerts(tempid,name, true, con);break;
			case Data.False:Data.handleAlerts(tempid,name, false, con);break;
			default:break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("dataError"+e.getMessage());
			if(response!=null){
				response.setStatus(404);
				response.getWriter().println("dataError"+e.getMessage()+"<br>");
				response.getWriter().close();
				}
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
	
	private void Delete (String Type,Integer id,HttpServletResponse response) throws IOException{
		Connection con=null;Statement statement = null; ResultSet rs = null;
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
			switch (Type){
			case Data.apparatus:
				
				String sql = "delete from "+Data.apparatus+" where id = "+String.valueOf(id) ;
				statement = con.createStatement();
				statement.executeUpdate(sql);
				if (Data.debugstatus.get(Key)){
					System.out.println(Data.apparatus+":" +id+" Is Deleted");}
				if(response!=null){response.getWriter().println(Data.apparatus+":" +id+" Is Deleted"+"<br>");}
				break;
			case Data.CurrentAlertedLog:
				String sql2 = "insert into "+Data.AlertedLog+" (id,name,Alerted,Cleared) "
						+ " select id,name,Alerted,Cleared from "+Data.CurrentAlertedLog;
				String sql3 = "truncate "+Data.CurrentAlertedLog;
				statement = con.createStatement();
				statement.addBatch(sql2);
				statement.addBatch(sql3);
				statement.executeBatch();
				if (Data.debugstatus.get(Key)){
					System.out.println(Data.CurrentAlertedLog +" Is Truncated");}
				if(response!=null){response.getWriter().println(Data.CurrentAlertedLog +" Is Truncated"+"<br>");}
				break;
			/*case Data.AlertedLog:
				String sql4 = "truncate "+Data.AlertedLog;
				statement = con.createStatement();
				statement.executeUpdate(sql4);
				if (Data.debugstatus.get(Key)){
					System.out.println(Data.AlertedLog +" Is Truncated");}
				if(response!=null){response.getWriter().println(Data.AlertedLog +" Is Truncated"+"<br>");}
				break;*/
			default:throw new Exception("Unsupported Type");
			}
		}catch (Exception e){
			System.out.println(e.getMessage());
			if(response!=null){
				response.setStatus(404);
				response.getWriter().println(e.getMessage()+"<br>");
				response.getWriter().close();
				}
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
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
