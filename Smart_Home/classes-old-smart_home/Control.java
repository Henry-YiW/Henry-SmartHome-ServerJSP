package smart_home;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
//import org.gjt.mm.mysql.Driver;

import okhttp3.Call;
import okhttp3.Callback;
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
	
	/** 
	 * @see HttpServlet#HttpServlet()
	 */
	public Control() {
		super();
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		if (request.getParameter("user")!=null&&request.getParameter("user").equals("henry")&&request.getParameter("pass")!=null&&request.getParameter("pass").equals("yiweigang")) {
			if (request.getParameter("id") != null && request.getParameter("activated") != null) {
				int tempid;
				try{
					tempid = Integer.parseInt(request.getParameter("id").trim());
				}catch(Exception e){
					if (Data.debugstatus.get(Key)){System.out.println("Not a Valid ID");}
					response.setStatus(404);
					response.getWriter().println("Not a Valid ID"+"<br>");
					return;
				}finally{
					response.getWriter().close();
				}
				String id = String.valueOf(tempid);
				String activatedstate = request.getParameter("activated");
				String apparatusURL = null;
				response.getWriter().println("Successful:"+request.getContextPath()+"<br>");
				String url = "jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
				ResultSet rs = null;
				Statement statement=null;
				Connection con=null;
				boolean intent;boolean passive=false;
				try {
					con = DriverManager.getConnection(url);
					statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					String sql = "select * from apparatus where id =" + id;

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
				} catch (SQLException e) {
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
				
				if (Data.debugstatus.get(Key)){System.out.println(apparatusURL);}
				try {
				Request okrequest = new Request.Builder().url(apparatusURL).get().build();
				communicate(client, okrequest, 0,intent,id,passive);}
				catch(Exception e){
					response.setStatus(404);
					response.getWriter().println("Invalid URL Won't Connect"+"<br>");
					e.printStackTrace();
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
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}

	}

	void communicate(final OkHttpClient client, final Request okrequest, final int trytimes,final boolean intent,final String id,final boolean passive) {

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
