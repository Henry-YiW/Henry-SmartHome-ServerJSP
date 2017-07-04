package smart_home;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
//import org.gjt.mm.mysql.Driver;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Servlet implementation class Control
 */
@WebServlet("/Control")
public class Control extends HttpServlet {
	private static final String Key="Control";
	private static final long serialVersionUID = 3L;
	private final OkHttpClient client= Data.client;
	public static volatile boolean stopAll=false;
	
	/** 
	 * @see HttpServlet#HttpServlet()
	 */
	public Control() {
		super();
		//try {
		//	Class.forName("org.gjt.mm.mysql.Driver");
		//} catch (ClassNotFoundException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		// 或 new Driver();
		// 或Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		// 子类用父类的静态方法不会初始化这个静态方法所属的类的子类，而初始化静态方法所属的类（当然还有它的父类了）
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
		if (request.getParameter("user")!=null&&request.getParameter("user").equals(Data.user)&&request.getParameter("pass")!=null&&request.getParameter("pass").equals(Data.pass)) {
			if (request.getParameter("id") != null && request.getParameter("activated") != null) {
				int tempid;
				try{
					tempid = Integer.parseInt(request.getParameter("id").trim());
				}catch(Exception e){
					if (Data.debugstatus.get(Key)){System.out.println("Not a Valid ID");}
					response.setStatus(404);
					response.getWriter().println("Not a Valid ID"+"<br>");
					response.getWriter().close();
					return;
				}
				String id = String.valueOf(tempid);
				String activatedstate = request.getParameter("activated");
				String apparatusURL = null;
				response.getWriter().println("Successful:"+request.getContextPath()+"<br>");
				ResultSet rs = null;
				Statement statement=null;
				Connection con=null;
				boolean intent;boolean passive=false;
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
					if (rs != null) {
						while (rs.next()) {
							apparatusURL = rs.getString("URL");
							passive=rs.getBoolean("passive");
						}
					}

					if (activatedstate.equals("true") ) {
						apparatusURL = apparatusURL + "1";
						intent=true;
					} else {
						apparatusURL = apparatusURL + "2";
						intent=false;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
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
				response.getWriter().println(apparatusURL+"<br>");
				
				if (Data.debugstatus.get(Key)){
					System.out.println();
					System.out.println(apparatusURL);}
				try {
					Request okrequest = new Request.Builder().url(apparatusURL).get().build();
					communicate(client, okrequest,intent,id,passive);
				}
				catch(Exception e){
					response.setStatus(404);
					if (e.getMessage().contains("Connection Completely Failed")){
						response.getWriter().println("Connection Completely Failed"+"<br>");
					}else {
						response.getWriter().println("Invalid URL Won't Connect"+"<br>");
					}
					if (Data.debugstatus.get(Key)){System.out.println(e.getMessage());}
					return;
				}finally{
					response.getWriter().close();
				}
			}
			else{
				System.out.println("Control Illegal Content");
				response.sendError(400,"Illegal Content");
				//response.getWriter().println("Illegal Content"+"<br>");
				response.getWriter().close();
			}
		} else {
			System.out.println();
			System.out.println("Control Illegal Access");
			System.out.println("@"+request.getRemoteUser()+"$"+debugconfig.getRealRemoteAddr(request)+":"+request.getRemotePort()+"("+request.getRemoteHost()+")");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}

	}
	
	void communicate(final OkHttpClient client, final Request okrequest, final boolean intent,final String id,final boolean passive) throws IOException {
		int trytimes=0;
		while (trytimes<=5){
			if (stopAll){return;}
			IOException error=null;
			if (okrequest != null&& client != null) {
				Response OKresponse=null;
				try {
					if (stopAll){return;}
					OKresponse=client.newCall(okrequest).execute();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					error=e;
				}
				if (OKresponse!=null){
					if (OKresponse.isSuccessful()){
						if (Data.debugstatus.get(Key)){
							try {
								System.out.println(OKresponse.body().string());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}}
						if (passive){
							if (intent){
								//client.newCall(new Request.Builder().url("http://localhost:8080/Smart_Home/RegistrationPlusStateupdate?id="+id+"&activated=true").get().build()).enqueue(defaultCallback);
								try {
									RegistrationPlusStateupdate.updateData(Integer.parseInt(id), null, "192.168.1."+id, null, null, Data.True, Data.Default, Data.Default, Data.Default, Data.Default, null);
								} catch (NumberFormatException | IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else{
								//client.newCall(new Request.Builder().url("http://localhost:8080/Smart_Home/RegistrationPlusStateupdate?id="+id+"&activated=false").get().build()).enqueue(defaultCallback);
								try {
									RegistrationPlusStateupdate.updateData(Integer.parseInt(id), null, "192.168.1."+id, null, null, Data.False, Data.Default, Data.Default, Data.Default, Data.Default, null);
								} catch (NumberFormatException | IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						OKresponse.close();
						return;
					} else {
						if (Data.debugstatus.get(Key)){
							if (error!=null){
								System.out.println("Connection Failed@"+error.getMessage());
							}else {
								System.out.println("Connection Failed@"+"Response Failed");
							}
						}
					}
					OKresponse.close();
				} else {
					if (Data.debugstatus.get(Key)){
						System.out.println("Connection Failed@"+error.getMessage());}
				}
			}else {
				throw new IOException ("No Request or Assigned Client");
			}
			if (stopAll){return;}
			trytimes ++;
		}
		throw new IOException ("Connection Completely Failed");
	}

	/*void communicate(final OkHttpClient client, final Request okrequest, final int trytimes,final boolean intent,final String id,final boolean passive) {

		if (trytimes <= 5) {
			if (okrequest != null&& client != null) {

				client.newCall(okrequest).enqueue(new Callback() {

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						// TODO Auto-generated method stub
						if (Data.debugstatus.get(Key)){
						System.out.println("Connection Failed");}
						
						communicate(client, okrequest, trytimes + 1,intent,id,passive);
					}

					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						// TODO Auto-generated method stub
						if (arg1 != null && arg1.isSuccessful()) {
							if (Data.debugstatus.get(Key)){
							System.out.println(arg1.body().string());}
							if (passive){
								if (intent){
									//client.newCall(new Request.Builder().url("http://localhost:8080/Smart_Home/RegistrationPlusStateupdate?id="+id+"&activated=true").get().build()).enqueue(defaultCallback);
									RegistrationPlusStateupdate.updateData(Integer.parseInt(id), null, "192.168.1."+id, null, null, Data.True, Data.Default, Data.Default, Data.Default, null);
								}else{
									//client.newCall(new Request.Builder().url("http://localhost:8080/Smart_Home/RegistrationPlusStateupdate?id="+id+"&activated=false").get().build()).enqueue(defaultCallback);
									RegistrationPlusStateupdate.updateData(Integer.parseInt(id), null, "192.168.1."+id, null, null, Data.False, Data.Default, Data.Default, Data.Default, null);
								}
							}
							
						}
						arg1.close();

					}

				});
			} else {
				System.out.println("URL failed to be fetched");

			}
		}

	}*/

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
