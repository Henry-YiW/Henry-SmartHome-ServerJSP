package smart_home;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	private static final String Key="ApparatusUpdate";
	private static final long serialVersionUID = 2L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegistrationPlusStateupdate() {
		super();
		// 其实这个初始化数据库驱动的代码，只需要所有的servlet中的一个运行一次就行了，
		// 但是谁知道那个会先被实例化（被客户端调用），所以就都给加上好了；
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		if (request.getParameter("user")!=null&&request.getParameter("user").equals("henry")&&request.getParameter("pass")!=null&&request.getParameter("pass").equals("yiweigang")) {
			String URLfix;String name;String URL = request.getRemoteAddr();
			int passive;String fullURL;int persistentornot;int Alerted;int activated;
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
				persistentornot=0;
			}
			if (request.getParameter("Alerted")!=null&&request.getParameter("Alerted").equals("true")){
				Alerted=Data.True;
			}else if (request.getParameter("Alerted")!=null&&!request.getParameter("Alerted").equals("true")){
				Alerted=Data.False;
			}else {
				Alerted=Data.Default;
			}
			//String name = request.getParameter("name");
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
					//String URL = request.getRemoteAddr();
					fullURL = "http://"+URL + URLfix;
					if (Data.debugstatus.get(Key)){System.out.println(fullURL);}response.getWriter().println(fullURL+"<br>");
					String[] URLTemp=URL.split("\\.");
					if (URLTemp.length==1){URLTemp=URL.split(":");}
					String trimedID=URLTemp[URLTemp.length-1];
					tempid = Integer.parseInt(trimedID);
					//tempid = tempid - 10;
				}
			}
			else {
				//String URL = request.getRemoteAddr();
				fullURL = "http://"+URL + URLfix;
				if (Data.debugstatus.get(Key)){System.out.println(fullURL);}response.getWriter().println(fullURL+"<br>");
				String[] URLTemp=URL.split("\\.");
				if (URLTemp.length==1){URLTemp=URL.split(":");}
				String trimedID=URLTemp[URLTemp.length-1];
				tempid = Integer.parseInt(trimedID);
				//tempid = tempid - 10;
			}
			
			
			updateData(tempid,name,URL,URLfix,fullURL,activated,passive,Alerted,persistentornot,response);
			response.getWriter().close();
		}
		else {
			System.out.println();
			System.out.println("ApparatusUpdate Illegal Access");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}
	}
	
	public static void updateData(int tempid,String name,String URL,String URLfix,String fullURL,int activatedstate,int passive,int Alerted,int persistentornot,HttpServletResponse response) throws IOException{
		String url = "jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
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
			con = DriverManager.getConnection(url);
			statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String sql = "select * from apparatus where id =" + id;
			rs = statement.executeQuery(sql);
			if (rs != null && rs.next()) {
				if(response!=null){response.getWriter().println("dataupdate"+"<br>");}
				rs.previous();
				while (rs.next()) {
					Boolean temp=null;
					switch (activatedstate){
					case 2:temp=true;rs.updateBoolean("activated", true);break;
					case 1:temp=false;rs.updateBoolean("activated", false);break;
					default:break;
					}
					if (Data.debugstatus.get(Key)&&temp!=null&&temp!=rs.getBoolean("activated")){
						System.out.print("dataupdate||");}
					if (name!=null){
					rs.updateString("name", name);
					}
					if (URLfix!=null){
					rs.updateString("URL", fullURL);
					}
					switch (passive){
					case 2:rs.updateBoolean("passive", true);break;
					case 1:rs.updateBoolean("passive", false);break;
					default:break;
					}
					switch (persistentornot){
					case 2:rs.updateBoolean("P", true);break;
					case 1:rs.updateBoolean("P", false);break;
					default:break;
					}
					switch (Alerted){
					case 2:rs.updateBoolean("A", true);break;
					case 1:rs.updateBoolean("A", false);break;
					default:break;
					}
					rs.updateDate("lastactivetime", null);
					rs.updateRow();
				}
			} else {
				if (Data.debugstatus.get(Key)){
				System.out.print("dataCreate||");}
				if(response!=null){response.getWriter().println("dataCreate"+"<br>");}
				PreparedStatement prestatement = con.prepareStatement("insert into apparatus (id,name,URL,activated,passive,A,P) values(?,?,?,?,?,?,?)");
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
				case 2:prestatement.setBoolean(4, true);break;
				default:prestatement.setBoolean(4, false);break;
				}
				switch (passive){
				case 2:prestatement.setBoolean(5, true);break;
				default:prestatement.setBoolean(5, false);break;
				}
				switch (Alerted){
				case 2:prestatement.setBoolean(6, true);break;
				default:prestatement.setBoolean(6, false);break;
				}
				switch (persistentornot){
				case 2:prestatement.setBoolean(7, true);break;
				default:prestatement.setBoolean(7, false);break;
				}
				//prestatement.setTimestamp(6, null);
				prestatement.executeUpdate();
				prestatement.close();
				// ResultSet set = prestatement.executeQuery();
			}
			statement.close();
			con.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("dataError");
			if(response!=null){
				response.setStatus(404);
				response.getWriter().println("dataError"+"<br>");
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
				if(response!=null){
				response.getWriter().close();
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
