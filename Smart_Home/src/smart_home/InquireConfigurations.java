package smart_home;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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

import net.sf.json.JSONObject;

/**
 * Servlet implementation class InquireConfigurations
 */
@WebServlet("/InquireConfigurations")
public class InquireConfigurations extends HttpServlet {
	private static final long serialVersionUID = 6L;
    public static final String Key = "ConfigurationsInquire";
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");
		PrintWriter wr = response.getWriter();
		if(request.getParameter("user")!=null&&request.getParameter("user").equals(Data.user)&&request.getParameter("pass")!=null&&request.getParameter("pass").equals(Data.pass)){
			if (request.getParameter("Type")!=null){
				//wr.println("<html>");
				//wr.println("<head>");
				//wr.println("<title>MyFirstServlet</title>");
				//wr.println("<h2>Servlet InquireServlet at " + request.getContextPath() + "</h2>");
				//wr.println("</head>");
				//wr.println("<body>");
				JSONObject json=inquireData(0,request.getParameter("Type").trim());
				if (json!=null){
					wr.println(json.toString());
				}else{
					response.setStatus(404);
					wr.println("No Such Data");
				}
				//wr.println("</body>");
				//wr.println("</html>");
				if (wr!=null){
					wr.close();
				}
			}else {
				System.out.println("ConfigurationsInquire Illegal Content");
				response.sendError(400,"Illegal Content");
				//response.getWriter().println("Illegal Content"+"<br>");
				response.getWriter().close();
			}
		}
		else {
			System.out.println();
			System.out.println("ConfigurationsInquire Illegal Access");
			System.out.println("@"+request.getRemoteUser()+"$"+debugconfig.getRealRemoteAddr(request)+":"+request.getRemotePort()+"("+request.getRemoteHost()+")");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}
	}
	
	public  JSONObject inquireData(int tempid,String Type){
		ResultSet rs=null;Statement statement=null;Connection con=null;JSONObject json=new JSONObject();String sql;
		String id;
		initiate.activateRegularCheck();
		initiate.activatePassiveUpdationForStatus ();
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (tempid>0){
			id=" where id="+String.valueOf(tempid);
		}else{
			id="";
		}
		
		try {
			switch (Type){
			case Data.apparatus:
				
				statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				sql="select * from "+Data.apparatus+""+id;
				rs=statement.executeQuery(sql);
				while (rs!=null&&rs.next()){
					json.accumulate("id", rs.getInt("id"));
					json.accumulate("name", rs.getString("name"));
					json.accumulate("URL", rs.getString("URL"));
					json.accumulate("passive", rs.getBoolean("passive"));
					json.accumulate("Persistent", rs.getBoolean("P"));
					json.accumulate("Alertable", rs.getBoolean("A"));
				}
			
				return json;
			case Data.Temperature:
				statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				sql="select * from "+Data.Temperature+" where id = -1";
				rs=statement.executeQuery(sql);
				while (rs!=null&&rs.next()){
					json.accumulate("Period", rs.getInt("Degree"));
					json.accumulate("URL", rs.getString("URL"));
				}
			
			
				return json;
			case Data.Humidity:
				statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				sql="select * from "+Data.Humidity+" where id = -1";
				rs=statement.executeQuery(sql);
				while (rs!=null&&rs.next()){
					json.accumulate("Period", rs.getInt("Degree"));
					json.accumulate("URL", rs.getString("URL"));
				}
			
			
				return json;
			case Data.appliance:
				statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				sql="select * from "+Data.appliance+"";
				rs=statement.executeQuery(sql);
				if (rs!=null&&rs.next()){
					json.accumulate("Period", rs.getInt("Period"));
					rs.previous();
					while (rs.next()){
						if (!id.isEmpty()){
							if (rs.getInt("id")==tempid){
								json.accumulate("id", rs.getInt("id"));
								json.accumulate("name", rs.getString("name"));
								json.accumulate("URL", rs.getString("URL"));
								break;
							}
						}else{
							json.accumulate("id", rs.getInt("id"));
							json.accumulate("name", rs.getString("name"));
							json.accumulate("URL", rs.getString("URL"));
						}
					}
				}
				/*rs.close();
				sql="select * from "+Data.appliance+""+id;
				rs=statement.executeQuery(sql);
				while (rs!=null&&rs.next()){
					json.accumulate("id", rs.getInt("id"));
					json.accumulate("name", rs.getString("name"));
					json.accumulate("URL", rs.getString("URL"));
				}
				 */
				return json;
			default:return null;
		}
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
