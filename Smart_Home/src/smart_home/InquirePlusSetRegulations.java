package smart_home;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javafx.util.Pair;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class InquirePlusSetRegulations
 */
@WebServlet("/InquirePlusSetRegulations")
public class InquirePlusSetRegulations extends HttpServlet {
	private static final long serialVersionUID = 8L;
	private static final String Key1="RegulationsInquire";
	private static final String Key2="RegulationsSet";
    private static final String Key3="RegulationsDelete";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InquirePlusSetRegulations() {
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
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		if (request.getParameter("user")!=null&&request.getParameter("user").equals(Data.user)&&request.getParameter("pass")!=null&&request.getParameter("pass").equals(Data.pass)) {
			if (request.getParameter("Type")!=null){
				String Type = request.getParameter("Type").trim();Integer id;String Item=null;String Rule=null;Boolean enabled=null;
				if (request.getParameter("item")!=null){
					Item = request.getParameter("item").trim();
				}
				if (request.getParameter("rule")!=null){
					Rule = request.getParameter("rule").trim();
				}
				try {
					id = Integer.parseInt(request.getParameter("id").trim());
				}catch (NumberFormatException e){
					if (Data.debugstatus.get(Key2)||Data.debugstatus.get(Key1)){
						System.out.println(Key1+Key2+":ID Format is Wrong");}
					id=null;
				}catch (Exception e){
					id=null;
				}
				
				if (request.getParameter("enabled")!=null&&request.getParameter("enabled").equals("true")){
					enabled=true;
				}else if(request.getParameter("enabled")!=null&&!request.getParameter("enabled").equals("true")){
					enabled=false;
				}else {
					enabled=null;
				}
				
				switch (Type){
				case "Set":
					if (id!=null&&Item!=null){
						setRule(id,Item,Rule,enabled,response);
					}else {
						System.out.println("InquirePlusSetRegulations Illegal Content");
						response.sendError(400,"Illegal Content");
						//response.getWriter().println("Illegal Content"+"<br>");
						response.getWriter().close();
						return;
					}
					break;
				case "Inquire":
					JSONObject json=inquireRules(id,Item);
					if (json!=null){
						response.setContentType("application/json;charset=utf-8");
						response.getWriter().println(json.toString());
					}else {
						response.getWriter().println(Key1+":Data Error");
						if (Data.debugstatus.get(Key1)){
							System.out.println(Key1+":Data Error");}
						return;
					}
					break;
				case "Delete":
					deleteRules(id,Item,response);
					break;
				default:
					System.out.println("InquirePlusSetRegulations Illegal Content");
					response.sendError(400,"Illegal Content");
					//response.getWriter().println("Illegal Content"+"<br>");
					response.getWriter().close();
					return;
				}
				
				response.getWriter().close();
			}else {
				System.out.println("InquirePlusSetRegulations Illegal Content");
				response.sendError(400,"Illegal Content");
				//response.getWriter().println("Illegal Content"+"<br>");
				response.getWriter().close();
			}
		}
		else {
			System.out.println();
			System.out.println("InquirePlusSetRegulations Illegal Access");
			System.out.println("@"+request.getRemoteUser()+"$"+debugconfig.getRealRemoteAddr(request)+":"+request.getRemotePort()+"("+request.getRemoteHost()+")");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}
	}
	
	public static void setRule (int id,String ItemString,String RuleString,Boolean enabled,HttpServletResponse response){
		if (id<=0){
			try {
				if (response!=null){
					response.getWriter().println(Key2+":No Valid Id");}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Data.debugstatus.get(Key2)){
				System.out.println(Key2+":No Valid Id");}
			return;
		}
		if (ItemString==null||ItemString.isEmpty()){
			try {
				if (response!=null){
					response.getWriter().println(Key2+":No Illegal Item");}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Data.debugstatus.get(Key2)){
				System.out.println(Key2+":No Illegal Item");}
			return;
		}
		if (RuleString==null||RuleString.isEmpty()){
			String[] ItemStrings = ItemString.split("-");
			for (String temp:ItemStrings){
				setRuleImpl(id,temp.trim(),RuleString,enabled,response);
			}
			return;
		}
		String[] ItemStrings = ItemString.split("-");String[] RuleStrings = RuleString.split("-");
		int maxIndex = Math.min(ItemStrings.length, RuleStrings.length);
		ArrayList<String> realItems=new ArrayList<>();ArrayList<String> realRules=new ArrayList<>();
		Pair<String,JSONObject> cache=null;
		while (maxIndex>=1){
			boolean Done=true;cache=null;
			for (int i=0;i<maxIndex;i++){
				if (ItemStrings[i]!=null&&RuleStrings[i]!=null){
					Done=false;
					try {
						JSONObject BaseRule;JSONObject Rule;String Item;
						if (cache==null){
							BaseRule=new JSONObject();
							Item=ItemStrings[i].trim();
							
						}else {
							if (ItemStrings[i].trim().equals(cache.getKey())){
								BaseRule=cache.getValue();
								Item=cache.getKey();
							}else {
								continue;
							}
						}
						Rule=JSONObject.fromObject(RuleStrings[i].trim());
						JSONArray StartDate;JSONArray EndDate;JSONArray StartTime;JSONArray EndTime;JSONArray Enabled;
						StartDate=Rule.optJSONArray(Data.StartDate);if (StartDate==null&&Rule.containsKey(Data.StartDate)){StartDate=new JSONArray().element(Rule.getString(Data.StartDate));}
						EndDate=Rule.optJSONArray(Data.EndDate);if (EndDate==null&&Rule.containsKey(Data.EndDate)){EndDate=new JSONArray().element(Rule.getString(Data.EndDate));}
						StartTime=Rule.optJSONArray(Data.StartTime);if (StartTime==null&&Rule.containsKey(Data.StartTime)){StartTime=new JSONArray().element(Rule.getString(Data.StartTime));}
						EndTime=Rule.optJSONArray(Data.EndTime);if (EndTime==null&&Rule.containsKey(Data.EndTime)){EndTime=new JSONArray().element(Rule.getString(Data.EndTime));}
						Enabled=Rule.optJSONArray(Data.Enabled);if (Enabled==null&&Rule.containsKey(Data.Enabled)){Enabled=new JSONArray().element(Rule.getString(Data.Enabled));}
						if (StartDate!=null||EndDate!=null||StartTime!=null||EndTime!=null){
							cache=new Pair<>(Item,processRule(BaseRule, StartDate, EndDate, StartTime, EndTime, Enabled));
						}
					}catch (Exception e){
						//e.printStackTrace();
					}
					ItemStrings[i]=null;RuleStrings[i]=null;
				}
			}
			if (cache!=null&&cache.getKey()!=null&&cache.getValue()!=null){
				realItems.add(cache.getKey());
				realRules.add(cache.getValue().toString());
			}
			cache=null;
			if (Done){
				break;
			}
		}
		for (int i=0;i<realItems.size();i++){
			setRuleImpl(id,realItems.get(i),realRules.get(i),enabled,response);
		}
		if (realItems.isEmpty()||realRules.isEmpty()){
			try {
				if (response!=null){
					response.getWriter().println(Key2+":No Valid Items or Rules");}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Data.debugstatus.get(Key2)){
				System.out.println(Key2+":No Valid Items or Rules");}
		}
	}
	
	private static JSONObject processRule(JSONObject Rule,JSONArray StartDate,JSONArray EndDate,JSONArray StartTime,JSONArray EndTime,JSONArray Enabled){
		int MaxIndex=0;JSONArray[] temps=new JSONArray[]{StartDate, EndDate, StartTime, EndTime, Enabled};
		for (JSONArray temp:temps){
			MaxIndex=Math.max(MaxIndex, temp!=null?temp.size():0);
		}
		for (int i=0;i<MaxIndex;i++){
			String StartDateData=StartDate!=null?StartDate.optString(i):"";String EndDateData=EndDate!=null?EndDate.optString(i):"";
			String StartTimeData=StartTime!=null?StartTime.optString(i):"";String EndTimeData=EndTime!=null?EndTime.optString(i):"";
			boolean EnabledData=Enabled!=null?Enabled.optBoolean(i):false;
			Rule.accumulate(Data.StartDate, StartDateData).accumulate(Data.EndDate, EndDateData).accumulate(Data.StartTime, StartTimeData)
			.accumulate(Data.EndTime, EndTimeData).accumulate(Data.Enabled, EnabledData);
		}
		return Rule;
	}
	
	public static void setRuleImpl (int tempid,String Item,String Rule,Boolean enabled,HttpServletResponse response){
		ResultSet rs=null;PreparedStatement pstatement=null;Connection con=null;
		String id;
		if (tempid>0){
			id=" where id="+String.valueOf(tempid);
		}else{
			id="";
			try {
				if (response!=null){
					response.getWriter().println(Key2+":No Valid Id");}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Data.debugstatus.get(Key2)){
				System.out.println(Key2+":No Valid Id");}
			return;
		}
		if (Item==null||Item.isEmpty()){
			try {
				if (response!=null){
					response.getWriter().println(Key2+":No Valid Item");}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Data.debugstatus.get(Key2)){
				System.out.println(Key2+":No Valid Item");}
			return;
		}
		
		try {
			Future<Connection> Fcon=Data.ds.getConnectionAsync();
			if (!Fcon.isDone()){
				if (Data.debugstatus.get(Key2)){
					System.out.println(Key2+":Connection is not yet available. We need to wait");}
				int trytimes=0;
				while (!Fcon.isDone()){
					if (trytimes<100){
						Thread.sleep(200);
						trytimes++;
					}else {
						throw new TimeoutException("Wait Too Long, Something Wrong, Got to Quit and Restart");
					}
				}
				if (Data.debugstatus.get(Key2)){
					System.out.println(Key2+":We have Got A Connection");}
			}
			con=Fcon.get();
			String sql="select * from "+Data.Regulation +id + " and item=?";
			String sql2="insert into "+Data.Regulation+" (id,name,item,rule,enabled) values (?,?,?,?,?)";
			pstatement = con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			pstatement.setString(1, Item);
			rs=pstatement.executeQuery();
			if (rs.next()){
				if (Rule!=null){
					rs.updateString("rule", Rule);
				}
				if (enabled!=null){
					rs.updateBoolean("enabled", enabled);
				}
				rs.updateRow();
				if (response!=null){
					response.getWriter().println(Key2+":Reset this Item's Rule"+"</br>");}
				if (Data.debugstatus.get(Key2)){
					System.out.println(Key2+":Reset this Item's Rule");}
			}else  {
				pstatement.close();
				pstatement=con.prepareStatement(sql2);
				pstatement.setInt(1, tempid);
				String nameInDatabase = Inquire.inquireData(tempid,Data.Type_InquireData).optString(Data.apparatus+"-name","no name");
				pstatement.setString(2, nameInDatabase);
				pstatement.setString(3, Item);
				pstatement.setString(4, (Rule!=null)?Rule:"");
				pstatement.setBoolean(5, (enabled!=null)?enabled:true);
				pstatement.executeUpdate();
				if (response!=null){
					response.getWriter().println(Key2+":Create this Item's Rule"+"</br>");}
				if (Data.debugstatus.get(Key2)){
					System.out.println(Key2+":Create this Item's Rule");}
			}
			
			
			
		}catch(Exception e){
			try {
				if (response!=null){
					response.getWriter().println(Key2+e.toString());}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return;
		}finally{
			
			try {
				if(rs!=null){
					rs.close();
				}
				if(pstatement!=null){
					pstatement.close();
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
	
	public static JSONObject inquireRules (Integer tempid,String tempItem){
		ResultSet rs=null;PreparedStatement pstatement=null;Connection con=null;JSONObject json=new JSONObject();
		String id;String Item;
		if (tempid!=null&&tempid>0){
			id=" where id="+String.valueOf(tempid);
		}else{
			id="";
		}
		if (tempItem!=null&&!tempItem.isEmpty()){
			if (!id.isEmpty()){
				Item= " and item=?";
			}else {
				Item= " where item=?";
			}
			
		}else {
			Item="";
		}
		try {
			Future<Connection> Fcon=Data.ds.getConnectionAsync();
			if (!Fcon.isDone()){
				if (Data.debugstatus.get(Key1)){
					System.out.println(Key1+":Connection is not yet available. We need to wait");}
				int trytimes=0;
				while (!Fcon.isDone()){
					if (trytimes<100){
						Thread.sleep(200);
						trytimes++;
					}else {
						throw new TimeoutException("Wait Too Long, Something Wrong, Got to Quit and Restart");
					}
				}
				if (Data.debugstatus.get(Key1)){
					System.out.println(Key1+":We have Got A Connection");}
			}
			con=Fcon.get();
			String sql="select * from "+Data.Regulation +id+Item;
			pstatement = con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			if (!Item.isEmpty()){
				pstatement.setString(1, tempItem);
			}
			rs=pstatement.executeQuery();
			while (rs!=null&&rs.next()){
				json.accumulate("id", rs.getInt("id"));
				json.accumulate("name", rs.getString("name"));
				json.accumulate("item", rs.getString("item"));
				json.accumulate("rule", rs.getString("rule"));
				json.accumulate("enabled", rs.getBoolean("enabled"));
			}
			if (json.isEmpty()){
				return null;
			}else {
				return json;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			
			try {
				if(rs!=null){
					rs.close();
				}
				if(pstatement!=null){
					pstatement.close();
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
	
	public static void deleteRules (Integer tempid,String tempItem,HttpServletResponse response){
		PreparedStatement pstatement=null;Connection con=null;
		String id;String Item;
		if (tempid!=null&&tempid>0){
			id=" where id="+String.valueOf(tempid);
		}else{
			id="";
		}
		if (tempItem!=null&&!tempItem.isEmpty()){
			if (!id.isEmpty()){
				Item= " and item=?";
			}else {
				Item= " where item=?";
			}
			
		}else {
			Item="";
		}
		try {
			Future<Connection> Fcon=Data.ds.getConnectionAsync();
			if (!Fcon.isDone()){
				if (Data.debugstatus.get(Key2)||Data.debugstatus.get(Key1)){
					System.out.println(Key3+":Connection is not yet available. We need to wait");}
				int trytimes=0;
				while (!Fcon.isDone()){
					if (trytimes<100){
						Thread.sleep(200);
						trytimes++;
					}else {
						throw new TimeoutException("Wait Too Long, Something Wrong, Got to Quit and Restart");
					}
				}
				if (Data.debugstatus.get(Key2)||Data.debugstatus.get(Key1)){
					System.out.println(Key3+":We have Got A Connection");}
			}
			con=Fcon.get();
			String sql="delete from "+Data.Regulation +id+Item;
			pstatement = con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			if (!Item.isEmpty()){
				pstatement.setString(1, tempItem);
			}
			pstatement.executeUpdate();
			if (response!=null){
				response.getWriter().println(":Delete this Id's and Item's Rules");}
			if (Data.debugstatus.get(Key2)||Data.debugstatus.get(Key1)){
				System.out.println(":Delete this Id's and Item's Rules");}
			
		}catch(Exception e){
			try {
				if (response!=null){
					response.getWriter().println(Key3+e.toString());}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return;
		}finally{
			
			try {
				if(pstatement!=null){
					pstatement.close();
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
