package smart_home;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

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
	private static final long serialVersionUID = 7L;
    private String [] Types= Data.debugkeys;
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String user=request.getParameter("user"); String pass=request.getParameter("pass");
		String Type=request.getParameter("Type"); String tempactivated=request.getParameter("activated");
		String Refresh=request.getParameter("Refresh");String NewAccount=request.getParameter("NewAccount");
		if (user!=null&&user.equals(Data.user)&&pass!=null&&pass.equals(Data.pass)&&Type!=null&&!Type.isEmpty()
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
				System.out.println("@"+request.getRemoteUser()+"$"+debugconfig.getRealRemoteAddr(request)+":"+request.getRemotePort()+"("+request.getRemoteHost()+")");
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
			Connection con=null;ResultSet rs=null;PreparedStatement pstate=null;
			try {
				Future<Connection> Fcon=Data.ds.getConnectionAsync();
				if (!Fcon.isDone()){
					if (Data.debugstatus.get(InquireConfigurations.Key)){
						System.out.println("DebugConfig"+":Connection is not yet available. We need to wait");}
					int trytimes=0;
					while (!Fcon.isDone()){
						if (trytimes<100){
							Thread.sleep(200);
							trytimes++;
						}else {
							throw new TimeoutException("Wait Too Long, Something Wrong, Got to Quit and Restart");
						}
					}
					if (Data.debugstatus.get(InquireConfigurations.Key)){
						System.out.println("DebugConfig"+":We have Got A Connection");}
				}
				con=Fcon.get();
				pstate= con.prepareStatement("select * from "+Data.Manifest+" where item='ContextName'");
				rs=pstate.executeQuery();
				if(rs!=null&&rs.next()){
					request.setAttribute("ContextName", rs.getString("value"));
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				response.sendError(404,"DataError");
				return;
				//e.printStackTrace();
			}finally{
				try {
					if(rs!=null){
						rs.close();
					}
					if(pstate!=null){
						pstate.close();
					}
					if(con!=null){
						con.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			request.getRequestDispatcher("/WEB-INF/debugconfig.jsp").forward(request, response);
		}else if (Refresh!=null&&Refresh.equals("ResetAccount")&&user!=null&&user.equals(Data.user)&&pass!=null&&pass.equals(Data.pass)
				&&NewAccount!=null&&!NewAccount.isEmpty()){
			Connection con=null;ResultSet rs=null;PreparedStatement pstate=null;
			try {
				Future<Connection> Fcon=Data.ds.getConnectionAsync();
				if (!Fcon.isDone()){
					if (Data.debugstatus.get(InquireConfigurations.Key)){
						System.out.println("DebugConfig"+":Connection is not yet available. We need to wait");}
					int trytimes=0;
					while (!Fcon.isDone()){
						if (trytimes<100){
							Thread.sleep(200);
							trytimes++;
						}else {
							throw new TimeoutException("Wait Too Long, Something Wrong, Got to Quit and Restart");
						}
					}
					if (Data.debugstatus.get(InquireConfigurations.Key)){
						System.out.println("DebugConfig"+":We have Got A Connection");}
				}
				con=Fcon.get();
				pstate= con.prepareStatement("select * from "+Data.Manifest+" where item='User&Pass'",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				//֮ǰһֱ����not updatable������ܿ�����JDBC ���ӳ����˸���PreparedStatement�������������¸�����֮ǰData ��̬���������õķ�����,
				//���ɵķ��������ͬ���ǲ�û��Ҫ��Concur_Updatable���Ǹ�PreparedStatement�������ڰ�Data ��̬���������õ�setUserPass()���PreparedStatementҲ�ĳ�Concur_Updatable��
				//�Ͳ��ٳ�����������ˡ�
				rs=pstate.executeQuery();
				if(rs!=null&&rs.next()){
					rs.updateString("value", NewAccount);
					rs.updateRow();
				}else {
					pstate.close();
					pstate=con.prepareStatement("insert into "+Data.Manifest+" (item,value) values (User&Pass,?)");
					pstate.setString(1, NewAccount);
					pstate.executeUpdate();
				}
				Data.setUserPass();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				response.sendError(404,"DataError");
				//e.printStackTrace();
			}finally{
				try {
					if(rs!=null){
						rs.close();
					}
					if(pstate!=null){
						pstate.close();
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
		else{
			System.out.println();
			System.out.println("DebugConfig Illegal Access");
			System.out.println("@"+request.getRemoteUser()+"$"+debugconfig.getRealRemoteAddr(request)+":"+request.getRemotePort()+"("+request.getRemoteHost()+")");
			response.setStatus(401);
			response.getWriter().println("Illegal Access"+"<br>");
			response.getWriter().println("Hello"+"<br>");
			
		}
		response.getWriter().close();
	}
	
	boolean WhetherMatchOneTypeOfReferers(String Referer,HttpServletRequest request){
		String RequestURL=request.getRequestURL().toString();
		String ProcessedURL = RequestURL.substring(0,RequestURL.lastIndexOf('/')+1);
		if (Data.debugstatus.get(InquireConfigurations.Key)&&Data.debugstatus.get(Inquire.Key)&&Data.debugstatus.get(InquireData.Key)){
			System.out.println("RequestURL:"+RequestURL);
			System.out.println("Referer:"+Referer);
		}
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
	
	public static String getRealRemoteAddr(HttpServletRequest request){
		String ipAddress = request.getHeader("x-forwarded-for");String sign="Forwarded-";
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");sign="ProxyClient-";
		}
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");sign="WL-ProxyClient-";
		}
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();sign="Direct-";
			if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
				//InetAddress inet=null;
				//try {
				//	inet = InetAddress.getLocalHost();
				//} catch (UnknownHostException e) {
				//	e.printStackTrace();
				//}
				//ipAddress= inet.getHostAddress();
			}
		}
		
		if(ipAddress!=null && ipAddress.contains(",")){ //"****.****.****.****".length() = 15 �������IPv6�Ͳ�����ô���ˡ�
			if(ipAddress.indexOf(",")>0){
				ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
			}
		}
		return sign+ipAddress; 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
