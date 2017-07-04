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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * Servlet implementation class Inquire
 */
@WebServlet("/Inquire")
public class Inquire extends HttpServlet {
	public static final String Key="ApparatusInquire";
	private static final long serialVersionUID = 1L;
    

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
		
		if(request.getParameter("user")!=null&&request.getParameter("user").equals(Data.user)&&request.getParameter("pass")!=null&&request.getParameter("pass").equals(Data.pass)){
			
			
			//wr.println("<html>");
			//wr.println("<head>");
			//wr.println("<title>MyFirstServlet</title>");
			//wr.println("<h2>Servlet InquireServlet at " + request.getContextPath() + "</h2>");
			//wr.println("</head>");
			//wr.println("<body>");
			int realInquireType=Data.Type_InquireAll;;
			String InquireType = request.getParameter("InquireType");
			if (InquireType!=null){
				switch (InquireType){
				case Data.Type_All:realInquireType=Data.Type_InquireAll;break;
				case Data.Type_Data:realInquireType=Data.Type_InquireData;break;
				case Data.Type_Configurations:realInquireType=Data.Type_InquireConfigurations;break;
				}
			}
			JSONObject json=inquireData(0,realInquireType);
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
			System.out.println("@"+request.getRemoteUser()+"$"+debugconfig.getRealRemoteAddr(request)+":"+request.getRemotePort()+"("+request.getRemoteHost()+")");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}
		
		
	}
	
	public static JSONObject inquireData (int tempid,int InquireType){
		ResultSet rs=null;Statement statement=null;Connection con=null;JSONObject json=new JSONObject();
		String id;
		if (tempid>0){
			id=" where id="+String.valueOf(tempid);
		}else{
			id="";
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
			initiate.activateRegularCheck();
			initiate.activatePassiveUpdationForStatus ();
			//if (justbooted){
			//	initiateStatus(con);
			//	justbooted=false;
			//}
			statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			String sql="select * from "+Data.apparatus+"" +id;
			rs=statement.executeQuery(sql);
			JSONObject CurrentAlertedSet = Data.getCurrentAlertedLog(con);
			JSONArray ids = CurrentAlertedSet.optJSONArray("id");
			if (ids==null){
				JSONArray temp=new JSONArray();
				if (!CurrentAlertedSet.isEmpty()&&CurrentAlertedSet.optInt("id",-1)!=-1){
					ids=temp.element(CurrentAlertedSet.getInt("id"));
				}else {ids=temp;}
			}
			while (rs!=null&&rs.next()){
				switch (InquireType){
				default:
					System.out.println("No such Inquire Type");
					return null;
				case Data.Type_InquireAll:
				case Data.Type_InquireData:
					json.accumulate("id", rs.getInt("id"));
					json.accumulate(Data.apparatus+"-name", rs.getString("name"));
					json.accumulate("URL", rs.getString("URL"));
					json.accumulate("activated", rs.getBoolean("activated"));
					/*boolean Alerted =false;
					for (int i=0;i<ids.size();i++){
						if (ids.getInt(i)==rs.getInt("id")){
							Alerted=true;
							break;
						}
					}*/
					json.accumulate("Alerted", Data.whetherAlerted(rs.getInt("id"), CurrentAlertedSet,ids));
					if (InquireType!=Data.Type_InquireAll){
						break;
					}
				case Data.Type_InquireConfigurations:
					json.accumulate("Passive", rs.getBoolean("passive"));
					json.accumulate("Persistent", rs.getBoolean("P"));
					json.accumulate("Alertable", rs.getBoolean("A"));
					try {
						JSONObject Regulation = InquirePlusSetRegulations.inquireRules(rs.getInt("id"), null);
						JSONArray items = Regulation.optJSONArray("item");if (items==null){items=new JSONArray().element(Regulation.getString("item"));}
						JSONArray rules = Regulation.optJSONArray("rule");if (rules==null){rules=new JSONArray().element(Regulation.getString("rule"));}
						JSONArray enableds = Regulation.optJSONArray("enabled");if (enableds==null){enableds=new JSONArray().element(Regulation.getString("enabled"));}
						int maxindex=Math.max(items.size(), rules.size());maxindex=Math.max(maxindex, enableds.size());
						if (maxindex>1){
							JSONArray regulations = new JSONArray();
							for (int i=0;i<maxindex;i++){
								regulations.add(new JSONObject().accumulate("item", items.optString(i,"")).accumulate("rule", rules.optString(i, "")).accumulate("enabledOfruleset", enableds.optBoolean(i,false)));
							}
							
							if (json.optString("Regulation",null)==null){
								json.accumulate("Regulation", new JSONArray());
							}
							json.accumulate("Regulation", regulations);
						}else {
							JSONObject regulations = new JSONObject ().accumulate("item", items.optString(0,"")).accumulate("rule", rules.optString(0,"")).accumulate("enabledOfruleset", enableds.optBoolean(0,false));
							json.accumulate("Regulation", regulations);
						}
					}catch (Exception e){
						json.accumulate("Regulation", "");
					}
					
					if (InquireType!=Data.Type_InquireAll){
						json.accumulate("id", rs.getInt("id"));
						json.accumulate(Data.apparatus+"-name", rs.getString("name"));
						json.accumulate("URL", rs.getString("URL"));
						break;
					}
				
				}
				//System.out.println(json);
			}
			
			json.accumulate("AlertedLogNumber", ids.size());
			
			
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
	//		PreparedStatement prestatement=con.prepareStatement("update "+Data.apparatus+" set activated=false where passive=true");
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
