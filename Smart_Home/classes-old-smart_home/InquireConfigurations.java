package smart_home;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InquireConfigurations() {
        super();
        try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");
		PrintWriter wr = response.getWriter();
		if(request.getParameter("user")!=null&&request.getParameter("user").equals("henry")&&request.getParameter("pass")!=null&&request.getParameter("pass").equals("yiweigang")
				&&request.getParameter("Type")!=null){
			
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
		}
		else {
			System.out.println();
			System.out.println("ConfigurationsInquire Illegal Access");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}
	}
	
	public static JSONObject inquireData(int tempid,String Type){
		String url="jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
		ResultSet rs=null;Statement statement=null;Connection con=null;JSONObject json=new JSONObject();String sql;
		String id;
		initiate.activateRegularCheck();
		initiate.activatePassiveUpdationForStatus ();
		try{
			con=DriverManager.getConnection(url);
		}catch (Exception e){
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
			case Data.Apparatus:
				
				statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				sql="select * from apparatus"+id;
				rs=statement.executeQuery(sql);
				while (rs!=null&&rs.next()){
					json.accumulate("id", rs.getInt("id"));
					json.accumulate("name", rs.getString("name"));
					json.accumulate("URL", rs.getString("URL"));
					json.accumulate("passive", rs.getBoolean("passive"));
					json.accumulate("Persistent", rs.getBoolean("P"));
				}
			
				return json;
			case Data.Temperature:
				statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				sql="select * from Temperature where id = -1";
				rs=statement.executeQuery(sql);
				while (rs!=null&&rs.next()){
					json.accumulate("Period", rs.getInt("Degree"));
					json.accumulate("URL", rs.getString("URL"));
				}
			
			
				return json;
			case Data.Humidity:
				statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				sql="select * from Humidity where id = -1";
				rs=statement.executeQuery(sql);
				while (rs!=null&&rs.next()){
					json.accumulate("Period", rs.getInt("Degree"));
					json.accumulate("URL", rs.getString("URL"));
				}
			
			
				return json;
			case Data.appliance:
				statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				sql="select * from appliance";
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
				sql="select * from appliance"+id;
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
