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
 * Servlet implementation class Inquire
 */
@WebServlet("/Inquire")
public class Inquire extends HttpServlet {
	//private static final String Key="ApparatusInquire";
	private static final long serialVersionUID = 1L;
    //private volatile static boolean justbooted=true;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Inquire() {
    	super();
    	//其实这个初始化数据库驱动的代码，只需要所有的servlet中的一个运行一次就行了，
    	//但是谁知道那个会先被实例化（被客户端调用），所以就都给加上好了；
    	try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");
		PrintWriter wr = response.getWriter();
		//response.getWriter().println(request.getParameter("user"));
		//response.getWriter().println(request.getParameter("pass"));
		
		if(request.getParameter("user")!=null&&request.getParameter("user").equals("henry")&&request.getParameter("pass")!=null&&request.getParameter("pass").equals("yiweigang")){
			
			
			//wr.println("<html>");
			//wr.println("<head>");
			//wr.println("<title>MyFirstServlet</title>");
			//wr.println("<h2>Servlet InquireServlet at " + request.getContextPath() + "</h2>");
			//wr.println("</head>");
			//wr.println("<body>");
			JSONObject json=inquireData(0);
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
			System.out.println("ApparatusInquire Illegal Access");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}
		
		
	}
	
	public static JSONObject inquireData (int tempid){
		String url="jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
		ResultSet rs=null;Statement statement=null;Connection con=null;JSONObject json=new JSONObject();
		String id;
		if (tempid>0){
			id=" where id="+String.valueOf(tempid);
		}else{
			id="";
		}
		try {
			con=DriverManager.getConnection(url);
			initiate.activateRegularCheck();
			initiate.activatePassiveUpdationForStatus ();
			//if (justbooted){
			//	initiateStatus(con);
			//	justbooted=false;
			//}
			statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			String sql="select * from apparatus" +id;
			rs=statement.executeQuery(sql);
			while (rs!=null&&rs.next()){
				json.accumulate("id", rs.getInt("id"));
				json.accumulate("name", rs.getString("name"));
				json.accumulate("URL", rs.getString("URL"));
				json.accumulate("activated", rs.getBoolean("activated"));
				json.accumulate("Alerted", rs.getBoolean("A"));
			}
			
			
			return json;
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
	
	//void initiateStatus (Connection con){
	//	try {
	//		PreparedStatement prestatement=con.prepareStatement("update apparatus set activated=false where passive=true");
	//		prestatement.executeUpdate();
	//		prestatement.close();
	//		System.out.println("Has Initiated the Status");
	//	} catch (SQLException e) {
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
	//}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
