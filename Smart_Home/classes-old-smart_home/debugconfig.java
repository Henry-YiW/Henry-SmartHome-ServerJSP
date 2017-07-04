package smart_home;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class debugconfig
 */
@WebServlet("/debugconfig")
public class debugconfig extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String [] Types= Data.debugkeys;
    /**
     * @see HttpServlet#HttpServlet()
     */
    
    static {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public debugconfig() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String user=request.getParameter("user"); String pass=request.getParameter("pass");
		String Type=request.getParameter("Type"); String tempactivated=request.getParameter("activated");
		String Refresh=request.getParameter("Refresh");
		if (user!=null&&user.equals("henry")&&pass!=null&&pass.equals("yiweigang")&&Type!=null&&!Type.isEmpty()
				&&tempactivated!=null&&!tempactivated.isEmpty()){
			Type=Type.trim();
			tempactivated=tempactivated.trim();
			boolean activated;String key=null;
			try{
				activated=Boolean.parseBoolean(tempactivated);
			}catch(Exception e){
				System.out.println("Activated Value Wrong");
				response.setStatus(404);
				response.getWriter().println("Activated Value Wrong"+"<br>");
				response.getWriter().println("Hello"+"<br>");
				return;
			}
			for (int i=0;i<Types.length;i++){
				if (Type.equals(Types[i])){
					synchronized (Data.debugstatus){
						Data.debugstatus.put(Type, activated);
					}
					key=Type;
					break;
				}
				
			}
			if (key!=null){
				synchronized (Data.debugstatus){
					response.getWriter().println(key+": "+ Data.debugstatus.get(key)+"<br>");
				}
			}else {
				response.setStatus(404);
				response.getWriter().println("No Such Debug Setting"+"<br>");
			}
			response.getWriter().println("Hello"+"<br>");
			synchronized (Data.debugstatus){
				for (Entry <String,Boolean> temp :Data.debugstatus.entrySet()){
					response.getWriter().println(temp.getKey()+":"+ temp.getValue()+"<br>");
					
				}
			}
			
		}else if (Refresh!=null&&Refresh.equals("true")){
			//request.getRequestURL()     http://localhost:8080/Smart_Home/debugconfig
			//	request.getRequestURI()   /Smart_Home/debugconfig
			//	request.getContextPath()  /Smart_Home/
			//	request.getServletPath()  /debugconfig
			//	request.getQueryString()  Refresh=true
			String RefererHeader= request.getHeader("Referer");
			//System.out.println(RefererHeader+"::"+request.getRequestURL().toString());
			
			if (RefererHeader!=null&&WhetherMatchOneTypeOfReferers(RefererHeader,request)){
				request.setAttribute("debugstatus", ((HashMap<String,Boolean>)Data.debugstatus).clone());
				if (user!=null){
				request.setAttribute("user", user);
				}
				if (pass!=null){
				request.setAttribute("pass", pass);
				}
				request.getRequestDispatcher("/WEB-INF/itemsframe.jsp").forward(request, response);
			}else{
				System.out.println();
				System.out.println("DebugConfig Illegal Forward Access");
				response.sendError(403,"Illegal Forward Access");
				//response.getWriter().println("Illegal Access"+"<br>");
				//response.getWriter().println("Hello"+"<br>");
			}
		}else if(Refresh!=null&&Refresh.equals("Get")){
			if (user!=null){
				request.setAttribute("user", user);
			}
			if (pass!=null){
				request.setAttribute("pass", pass);
			}
			try {
				String url="jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
				Connection con=DriverManager.getConnection(url);
				PreparedStatement pstate= con.prepareStatement("select * from Manifest where item='ContextName'");
				ResultSet rs=pstate.executeQuery();
				if(rs!=null&&rs.next()){
					request.setAttribute("ContextName", rs.getString("value"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			request.getRequestDispatcher("/WEB-INF/debugconfig.jsp").forward(request, response);
		}
		else{
			System.out.println();
			System.out.println("DebugConfig Illegal Access");
			response.setStatus(401);
			response.getWriter().println("Illegal Access"+"<br>");
			response.getWriter().println("Hello"+"<br>");
			
		}
		response.getWriter().close();
	}
	
	boolean WhetherMatchOneTypeOfReferers(String Referer,HttpServletRequest request){
		String RequestURL=request.getRequestURL().toString();
		String ProcessedURL = RequestURL.substring(0,RequestURL.lastIndexOf('/')+1);
		//System.out.println(ProcessedURL);
		//@String AvailableReferer1=ProcessedURL+"debugconfig?Refresh=Get";
		String AvailableReferer2=ProcessedURL+"debugconfig.html";
		if (Referer.equals(AvailableReferer2)){
			return true;
		}else{
			if (Referer.startsWith(ProcessedURL+"debugconfig?")){
				String temp = Referer.substring(Referer.lastIndexOf('/')+1);
				//System.out.println(temp);
				if(temp.contains("Refresh=Get")&&!temp.contains("Refresh=true")){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
