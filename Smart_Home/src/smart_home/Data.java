package smart_home;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;
import org.apache.tomcat.jdbc.pool.DataSource;
//import org.apache.tomcat.jdbc.pool.interceptor.StatementCache;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Data {
	public static String user; public static String pass;
	
	public static final OkHttpClient client= new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.MILLISECONDS).build();
	public static final OkHttpClient client2= client.newBuilder().connectTimeout(200, TimeUnit.MILLISECONDS).readTimeout(3000, TimeUnit.MILLISECONDS).writeTimeout(3000, TimeUnit.MILLISECONDS).build();
	public static final OkHttpClient client3= client.newBuilder().connectTimeout(5000, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true).build();
	public static final int Default=0;
	public static final int True=2;
	public static final int False=1;
	
	public static final String Type_All="All";
	public static final String Type_Data="Data";
	public static final String Type_Configurations="Configurations";
	public static final int Type_InquireAll=0;
	public static final int Type_InquireData=1;
	public static final int Type_InquireConfigurations=2;
	public static final String Temperature="Temperature";
	public static final String Humidity="Humidity";
	public static final String appliance="appliance";
	public static final String apparatus="apparatus";
	public static final String CurrentAlertedLog = "CurrentAlertedLog";
	public static final String AlertedLog = "AlertedLog";
	public static final String Regulation = "Regulation";
	public static final String Manifest="Manifest";
	
	public static final String StartTime="StartTime";public static final String EndTime="EndTime";
	public static final String StartDate="StartDate";public static final String EndDate="EndDate";
	public static final String SleepTime="SleepTime";public static final String ActiveTime="ActiveTime";
	public static final String Enabled="Enabled";
	
	public static final String PUSKeyAp="PassiveApparatusFetch";
	public static final String PUSKeyDa="PassiveDataFetch";
	
	public static final int DateTime=0;
	public static final int Date=1;
	public static final int Time=2;
	
	public static final String [] debugkeys = {PUSKeyAp,PUSKeyDa,"DataInquire","DataUpdate","ApparatusInquire","ApparatusUpdate","Control","ConfigurationsInquire","RegulationsInquire","RegulationsSet"};
	public static final Map<String,Boolean> debugstatus=new HashMap <>(8);
	
	//public static final DateFormat format=SimpleDateFormat.getDateTimeInstance();
	public static final DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//hh是12小时的，HH是24小时的。
	
	public static DataSource ds;
	
	static {
		synchronized (debugstatus){
			for (String temp:debugkeys){
				debugstatus.put(temp,false);
			}
		}
		try {
			Context initContext = new InitialContext();
			ds = (DataSource)initContext.lookup("java:comp/env/jdbc/Smart_Home");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//*��ʵ��getConnectionAsync�첽������ӵķ������ԭ������getConnectionͬ��������ӵķ���
		//-���Ⱦ�������������(block)���ӳأ�ͬʱҲ�������������ӵ��߳�ͨ���ж�isDone()״̬����������
		//-��û��׼���õ�ʱ����һЩ�����Ĺ����������������� ��Ȼ��ʵҲ���Բ��ж�isDone��ֱ��get������
		//-�����Ļ��ͻ�����(block)�������ӵ��̣߳�ֱ����ÿ��õ����ӡ�
		//Connection con=null;
		//try {
		//	Future<Connection> Fcon=Data.ds.getConnectionAsync();
		//	while (!Fcon.isDone()){
		//		System.out.println("Connection is not yet available. We can do some other business");
		//	}
		//	con=Fcon.get();
		//} catch (SQLException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//} catch (InterruptedException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//} catch (ExecutionException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
		setUserPass();
	}
	
	public static void setUserPass(){
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
			rs=pstate.executeQuery();String UserPass;
			if(rs!=null&&rs.next()){
				UserPass=rs.getString("value");
			}else {
				System.out.println("No User&Pass Item");
				return;
			}
			if (UserPass!=null&&!UserPass.isEmpty()){
				String[] tempUserPass = UserPass.split(":");
				user=tempUserPass[0];
		        if (tempUserPass.length>1){
		            pass=tempUserPass[tempUserPass.length-1];
		        }else {
		            pass="";
		        }
			}else {
				System.out.println("No Illegal User&Pass Item");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	
	public static Object[] syncGetRealDegree(String URL){
		Object [] Results= new Object[2];
		Request request = new Request.Builder().get().url(URL).build();
		if(Thread.currentThread().isInterrupted()){
			return null;
		}
		Response response=null;
		try {
			if(Thread.currentThread().isInterrupted()){
				return null;
			}
			response= client3.newCall(request).execute();//��ʵ������response��������null����Ϊ����OKHttpClient��Դ���룬�������nullҲ��throw��һ�����󣬵������catch��顣
		}catch (Exception e){
			if (Data.debugstatus.get(PUSKeyDa)){
			System.out.print("/DCF/");}
		}
		
		if (response!=null){
			if (response.isSuccessful()){
				try{
					String body=response.body().string().toUpperCase();//Degree:
					String prefix = body.substring(0,7);
					char sign = 0;
					if (Data.debugstatus.get(PUSKeyDa)){
					System.out.println("["+prefix+"]");}
					if (prefix.equals("DEGREE:")){
						try{
							Integer startindex=7;Integer endindex=null;
							for (int i=7;i<body.length();i++){
								if (!Character.isDigit(body.charAt(i))){
									sign=body.charAt(i);
									endindex=i;
									break;
								}
							}
							String degree=body.substring(startindex, endindex);
							int tempint=Integer.parseInt(degree);
							Results[0]=tempint;
							Results[1]=sign;
							return Results;
						}catch (Exception e){
							if (Data.debugstatus.get(PUSKeyDa)){
							System.out.print("||DegreeResponse String Format Not Right||");}
						}
					}else{
						if (Data.debugstatus.get(PUSKeyDa)){
						System.out.print("||No Degree Sign||");}
					}
				}catch (Exception e){
					if (Data.debugstatus.get(PUSKeyDa)){
					System.out.print("||DegreeResponse String Not Long Enough||");}
				}
				
				if(Thread.currentThread().isInterrupted()){
					response.close();
					return null;
				}
			}
			
			response.close();
		}
		if(Thread.currentThread().isInterrupted()){
			return null;
		}
		
		Results[0]=null;
		Results[1]=null;
		//return -2147483648;
		return Results;
	}
	
	public static int[] syncGetRealStatus (String URL){
		Request request = new Request.Builder().get().url(URL).build();
		if(Thread.currentThread().isInterrupted()){
			return null;
		}
		Response response=null;
		try {
			if(Thread.currentThread().isInterrupted()){
				return null;
			}
			response= client2.newCall(request).execute();
		}catch (Exception e){
			if (Data.debugstatus.get(PUSKeyAp)){
			System.out.print("/CF/");}
		}
		int [] StatusSet = new int[2]; int Status=Default;int Alerted=Default;
		if (response!=null){
			if (response.isSuccessful()){
				try{
					if(Thread.currentThread().isInterrupted()){
						response.close();
						return null;
					}
					String [] body=response.body().string().toUpperCase().split(",");
					if(Thread.currentThread().isInterrupted()){
						response.close();
						return null;
					}
					if (body[0].charAt(6)=='N'){
						Status=True;
					}else if(body[0].charAt(6)=='F'){
						Status=False;
					}
					if(Thread.currentThread().isInterrupted()){
						response.close();
						return null;
					}
					if (body[1].charAt(0)=='N'){
						Alerted=False;
					}else if(body[1].charAt(0)=='A'){
						Alerted=True;
					}
				}catch (Exception e){
					if (Data.debugstatus.get(PUSKeyAp)){
					System.out.print("||Response String Format Not Right||");}
				}
				
				
			}
			if(Thread.currentThread().isInterrupted()){
				response.close();
				return null;
			}
			response.close();
		}
		if(Thread.currentThread().isInterrupted()){
			return null;
		}
		StatusSet[0]=Status;StatusSet[1]=Alerted;
		return StatusSet;
	}
	
	/*public static int[] asyncGetRealStatus(String URL){
		Request request = new Request.Builder().get().url(URL).build();
		updateCallback callback= new updateCallback();
		client2.newCall(request).enqueue(callback);
		while (!callback.isExecuted()){
			if(Thread.currentThread().isInterrupted()){
				return null;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		return callback.getStatus();
	}*/
	
	public static boolean WhetherPassive(int id,JSONArray ids,JSONObject json){
		try {
			JSONArray passives = json.optJSONArray("Passive");
			if (passives==null){passives=new JSONArray().element(json.getBoolean("Passive"));}
			for (int i=0;i<ids.size();i++){
				if (ids.getInt(i)==id){
					return passives.getBoolean(i);
				}
			}
			return false;
			//JSONObject json=InquireConfigurations.inquireData(id,apparatus);
			//return json.getBoolean("passive");
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public static int PeriodPassiveOfAtmosphericData(int id,String Type,JSONObject json){
		try{
			//JSONObject json=InquireConfigurations.inquireData(id, Type);
			if (json==null){
				if (!Type.equals(Data.appliance)){
					json=InquireData.inquireData(Type,Data.Type_InquireConfigurations);
				}else {
					json=ApplianceInquire.getData(id, Data.Type_InquireConfigurations);
				}
			}
			int Period = json.optInt("Period",-1);
			JSONArray URLs=json.optJSONArray("URL");
			if (URLs==null){URLs=new JSONArray().element(json.getString("URL"));}
			JSONArray ids=json.optJSONArray("id");
			if (ids==null){ids=new JSONArray().element(json.optInt("id",0));}
			int maxIndex = Math.min(URLs.size(), ids.size());
			for (int i=0;i<maxIndex;i++){
				if (ids.getInt(i)==id){
					String URL=URLs.getString(i);
					if (Type.equals(Data.appliance)&&id==0){
						if (Period>=0 ){
							return Period;
						}else{
							return -1;
						}
					}else{
						if (Period>=0 && !URL.isEmpty()){
							return Period;
						}else{
							return -1;
						}
					}
				}
			}
			return -1;
		}catch(Exception e){
			//e.printStackTrace();
			return -1;
		}
	}
	
	/*private static class updateCallback implements Callback{
		private int Status=Default;private boolean executed=false;
		private int Alerted=Default;
		@Override
		public void onFailure(Call arg0, IOException arg1) {
			executed=true;
			if (Data.debugstatus.get(PUSKeyAp)){
				System.out.print("/CF/");}
			arg0.cancel();
		}

		@Override
		public void onResponse(Call arg0, Response arg1) throws IOException {
			String[] body;
			if (arg1!=null){
				if (arg1.isSuccessful()){
					try{
						body=arg1.body().string().toUpperCase().split(",");
						if (body[0].charAt(6)=='N'){
							Status=True;
						}else if(body[0].charAt(6)=='F'){
							Status=False;
						}
						if (body[1].charAt(0)=='N'){
							Alerted=False;
						}else if(body[1].charAt(0)=='A'){
							Alerted=True;
						}
					}catch (Exception e){
						if (Data.debugstatus.get(PUSKeyAp)){
						System.out.print("||Response String Format Not Right||");}
					}
				}
				arg1.close();
			}
			executed=true;
			arg0.cancel();
			
		}
		
		private int[] getStatus(){
			int [] temp=new int[2];
			temp[0]=Status;
			temp[1]=Alerted;
			return temp;
		}
		
		private boolean isExecuted(){
			return executed;
		}
	}*/
	
	public static JSONObject getCurrentAlertedLog (Connection con){
		String sql = "select * from "+Data.CurrentAlertedLog;
		ResultSet rs= null; PreparedStatement pstatement=null;
		JSONObject AlertedLogSet = new JSONObject ();
		try {
			pstatement = con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs=pstatement.executeQuery();
			while (rs.next()){
				AlertedLogSet.accumulate("id", rs.getInt("id"));
				AlertedLogSet.accumulate("name", rs.getString("name"));
				AlertedLogSet.accumulate("Alerted", rs.getString("Alerted"));
				AlertedLogSet.accumulate("Cleared", rs.getString("Cleared"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			AlertedLogSet.accumulate("DataError", true);
			e.printStackTrace();
		}finally{
			try{
				if (rs!=null){
					rs.close();
				}
				if (pstatement!=null){
					pstatement.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return AlertedLogSet;
	}
	
	public static boolean whetherAlerted (int id, JSONObject Log, JSONArray ids){
		if (Log.optBoolean("DataError",false)){return true;}
		//JSONArray ids = Log.optJSONArray("id");
		if (ids==null){
			JSONArray temp=new JSONArray();
			if (!Log.isEmpty()&&Log.optInt("id",-1)!=-1){
				ids=temp.element(Log.getInt("id"));
			}else {ids=temp;}
		}
		JSONArray cleareds = Log.optJSONArray("Cleared");
		if (cleareds==null){
			JSONArray temp=new JSONArray();
			if (!Log.isEmpty()&&!Log.optString("Cleared","--").equals("--")){
				cleareds=temp.element(Log.getString("Cleared"));
			}else {cleareds=temp;}
		}
		if (ids==null||ids.isEmpty()){return false;}
		if (cleareds==null||cleareds.isEmpty()){return true;}
		boolean HasAlerted=false;
		for(int i=0;i<ids.size();i++){
			if (ids.getInt(i)==id){
				if (cleareds.getString(i).toLowerCase().equals("null")){
					return true;
				}
				HasAlerted=true;
			}
		}
		if (HasAlerted){
			//return 3;
		}
		return false;
	}
	
	public static void handleAlerts (int id,String name,boolean alerted, Connection con){
		ResultSet rs=null;PreparedStatement pstatement=null;
		String sql= "select * from "+Data.CurrentAlertedLog+" where id = "+String.valueOf(id);
		String sql2 = "update "+Data.CurrentAlertedLog+" set Cleared = now() where Ind = ?";
		String sql3= "insert into "+Data.CurrentAlertedLog+" (id,name,Alerted) values (?,?,now())";
		try {
			if (WhetherAlertableWithRegulation(id)){
				if (Data.debugstatus.get(RegistrationPlusStateupdate.Key)&&Data.debugstatus.get(InquireConfigurations.Key)){
					System.out.print("//Decided to Handle Alerts @"+id+"//");}
				pstatement = con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs =pstatement.executeQuery();
				if (rs.last()&&rs.getString("Cleared")==null){
					if (!alerted){
						int Ind=rs.getInt("Ind");
						pstatement.close();
						pstatement=con.prepareStatement(sql2);
						pstatement.setInt(1, Ind);
						pstatement.executeUpdate();
						//rs.updateTimestamp("Cleared", null);
						System.out.println("DisAlerted:@"+id);
					}
				}else if (alerted){
					pstatement.close();
					pstatement = con.prepareStatement(sql3);
					pstatement.setInt(1, id);
					if (name!=null){
						pstatement.setString(2, name);
					}else {
						String nameInDatabase = Inquire.inquireData(id,Data.Type_InquireData).optString(Data.apparatus+"-name","no name");
						pstatement.setString(2, nameInDatabase);
					}
					pstatement.executeUpdate();
					System.out.println("Alerted:@"+id);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null){
				rs.close();
				}
				if(pstatement!=null){
					pstatement.close();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*public static class AlertedLogItem {
		int id;String name;String AlertedTime;String ClearedTime;
		AlertedLogItem (int id,String name,String AlertedTime,String ClearedTime){
			this.id=id; this.name=name; this.AlertedTime=AlertedTime;this.ClearedTime=ClearedTime;
		}
	}*/
	
	public static boolean WhetherAlertableWithRegulation (int id){
		JSONObject ApparatusConfigurations = Inquire.inquireData(id,Data.Type_InquireConfigurations);
		boolean Alertable = ApparatusConfigurations.getBoolean("Alertable");
		try{
			if (Alertable){
				JSONObject json = InquirePlusSetRegulations.inquireRules(id, null);
				if (json==null||json.isEmpty()){
					return Alertable;
				}
				JSONArray Items = json.optJSONArray("item"); JSONArray Rules = json.optJSONArray("rule");
				JSONArray Enableds = json.optJSONArray("enabled");
				if (Items==null){Items=new JSONArray().element(json.getString("item"));}
				if (Rules==null){Rules=new JSONArray().element(json.getString("rule"));}
				if (Enableds==null){Enableds=new JSONArray().element(json.getString("enabled"));}
				int MaxIndex=Math.min(Items.size(), Rules.size());MaxIndex=Math.min(MaxIndex, Enableds.size());
				
				String now= winnowoutFirstfromDateTime(Data.format.format(new Date()));JSONObject Rule;Boolean Whether=null;Boolean resultedWhether=null;
				long nowDate=convertDatetoSeconds(now,Data.Date,false);long nowTime=convertDatetoSeconds(now,Data.Time,false);
				//System.out.println("@"+id+"now:"+now);System.out.println("@"+id+"nowDate"+nowDate);System.out.println("@"+id+"nowTime"+nowTime);
				
				for (int i=0;i<MaxIndex;i++){
					JSONArray actualStartDateRule = null;JSONArray actualEndDateRule=null;JSONArray actualStartTimeRule=null;JSONArray actualEndTimeRule=null;
					JSONArray actualEnabled=null;long StartDate;long EndDate;long StartTime;long EndTime;
					if (Enableds.getBoolean(i)){
						try {
							if (Whether!=null){
								Whether=null;
							}
							Rule=JSONObject.fromObject(Rules.getString(i).trim());
							if (Rule.containsKey(Data.StartDate)){
								actualStartDateRule=Rule.optJSONArray(Data.StartDate);
								if (actualStartDateRule==null){actualStartDateRule=new JSONArray().element(Rule.getString(Data.StartDate));}}
							if (Rule.containsKey(Data.EndDate)){
								actualEndDateRule=Rule.optJSONArray(Data.EndDate);
								if (actualEndDateRule==null){actualEndDateRule=new JSONArray().element(Rule.getString(Data.EndDate));}}
							if (Rule.containsKey(Data.StartTime)){
								actualStartTimeRule=Rule.optJSONArray(Data.StartTime);
								if (actualStartTimeRule==null){actualStartTimeRule=new JSONArray().element(Rule.getString(Data.StartTime));}}
							if (Rule.containsKey(Data.EndTime)){
								actualEndTimeRule=Rule.optJSONArray(Data.EndTime);
								if (actualEndTimeRule==null){actualEndTimeRule=new JSONArray().element(Rule.getString(Data.EndTime));}}
							if (Rule.containsKey(Data.Enabled)){
								actualEnabled=Rule.optJSONArray(Data.Enabled);
								if (actualEnabled==null){actualEnabled=new JSONArray().element(Rule.getString(Data.Enabled));}}
							int tempMaxIndex=Math.max(actualStartDateRule!=null?actualStartDateRule.size():0, actualEndDateRule!=null?actualEndDateRule.size():0);
							tempMaxIndex=Math.max(tempMaxIndex, actualStartTimeRule!=null?actualStartTimeRule.size():0);
							tempMaxIndex=Math.max(tempMaxIndex, actualEndTimeRule!=null?actualEndTimeRule.size():0);
							
							//System.out.println("@"+id+"tempMaxIndex"+tempMaxIndex);
							for (int ii=0;ii<tempMaxIndex;ii++){
								Boolean tempWhether=null;
								if (actualEnabled!=null&&ii<actualEnabled.size()&&!actualEnabled.optBoolean(ii)){
									continue;
								}
								if (Items.getString(i).equals(Data.SleepTime)){
									//System.out.println("@"+id+"Item"+"RUN");
									if (actualStartDateRule!=null&&ii<actualStartDateRule.size()){
										try{
											StartDate=convertDatetoSeconds(actualStartDateRule.getString(ii),Data.Date,false);
											if (tempWhether==null){
												tempWhether=false;
											}
											if (nowDate<StartDate){
												tempWhether|=true;
											}else {
												tempWhether|=false;
											}
										}catch (Exception e){ }
									}
									if (actualEndDateRule!=null&&ii<actualEndDateRule.size()){
										//System.out.println("@"+id+"EndDate"+"RUN");
										try{
											EndDate=convertDatetoSeconds(actualEndDateRule.getString(ii),Data.Date,false);
											if (tempWhether==null){
												tempWhether=false;
											}
											if (nowDate>EndDate){
												tempWhether|=true;
											}else {
												tempWhether|=false;
											}
										}catch (Exception e){ }
									}
									if (actualStartTimeRule!=null&&ii<actualStartTimeRule.size()){
										try{
											StartTime=convertDatetoSeconds(actualStartTimeRule.getString(ii),Data.Time,false);
											if (tempWhether==null){
												tempWhether=false;
											}
											if (nowTime<StartTime){
												tempWhether|=true;
											}else {
												tempWhether|=false;
											}
										}catch (Exception e){ }
									}
									if (actualEndTimeRule!=null&&ii<actualEndTimeRule.size()){
										try{
											EndTime=convertDatetoSeconds(actualEndTimeRule.getString(ii),Data.Time,false);
											if (tempWhether==null){
												tempWhether=false;
											}
											if (nowTime>EndTime){
												tempWhether|=true;
											}else {
												tempWhether|=false;
											}
										}catch (Exception e){ }
									}
									if (tempWhether!=null){
										if (Whether==null){
											Whether=true;
										}
										Whether&=tempWhether;
									}
								}else if (Items.getString(i).equals(Data.ActiveTime)){
									if (actualStartDateRule!=null&&ii<actualStartDateRule.size()){
										try{
											StartDate=convertDatetoSeconds(actualStartDateRule.getString(ii),Data.Date,false);
											if (tempWhether==null){
												tempWhether=true;
											}
											if (nowDate>=StartDate){
												tempWhether&=true;
											}else {
												tempWhether&=false;
											}
										}catch (Exception e){ }
											/*StartDate=convertDatetoSeconds(Rule.getString(Data.StartDate),Data.Date,false);
											if (Whether==null){
												Whether=true;
											}
											if (nowDate>=StartDate){
												Whether&=true;
											}else {
												Whether&=false;
											}*/
									}
									if (actualEndDateRule!=null&&ii<actualEndDateRule.size()){
										try{
											EndDate=convertDatetoSeconds(actualEndDateRule.getString(ii),Data.Date,false);
											if (tempWhether==null){
												tempWhether=true;
											}
											if (nowDate<=EndDate){
												tempWhether&=true;
											}else {
												tempWhether&=false;
											}
										}catch (Exception e){ }
										/*EndDate=convertDatetoSeconds(Rule.getString(Data.EndDate),Data.Date,false);
										if (Whether==null){
											Whether=true;
										}
										if (nowDate<=EndDate){
											Whether&=true;
										}else {
											Whether&=false;
										}*/
									}
									if (actualStartTimeRule!=null&&ii<actualStartTimeRule.size()){
										try{
											StartTime=convertDatetoSeconds(actualStartTimeRule.getString(ii),Data.Time,false);
											if (tempWhether==null){
												tempWhether=true;
											}
											if (nowTime>=StartTime){
												tempWhether&=true;
											}else {
												tempWhether&=false;
											}
										}catch (Exception e){ }
										/*StartTime=convertDatetoSeconds(Rule.getString(Data.StartTime),Data.Time,false);
										if (Whether==null){
											Whether=true;
										}
										if (nowTime>=StartTime){
											Whether&=true;
										}else {
											Whether&=false;
										}*/
									}
									if (actualEndTimeRule!=null&&ii<actualEndTimeRule.size()){
										try{
											EndTime=convertDatetoSeconds(actualEndTimeRule.getString(ii),Data.Time,false);
											if (tempWhether==null){
												tempWhether=true;
											}
											if (nowTime<=EndTime){
												tempWhether&=true;
											}else {
												tempWhether&=false;
											}
										}catch (Exception e){ }
										/*EndTime=convertDatetoSeconds(Rule.getString(Data.EndTime),Data.Time,false);
										if (Whether==null){
											Whether=true;
										}
										if (nowTime<=EndTime){
											Whether&=true;
										}else {
											Whether&=false;
										}*/
									}
									if (tempWhether!=null){
										if (Whether==null){
											Whether=false;
										}
										Whether|=tempWhether;
									}
								}
							}
						}catch (Exception e){
							//e.printStackTrace();
						}finally{
							if (Whether==null){
								
							}
							else if (Whether==true){
								resultedWhether=true;
								break;
							}else if (Whether==false){
								resultedWhether=false;
							}
						}
					}
				}
				
				if (resultedWhether!=null){
					/*try {
						RegistrationPlusStateupdate.updateData(id, null, null, null, null, Data.Default, Data.Default, Whether?Data.True:Data.False, Data.Default, Data.Default, null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					if (Data.debugstatus.get(RegistrationPlusStateupdate.Key)&&Data.debugstatus.get(InquireConfigurations.Key)){
					System.out.print("@"+id+"Whether"+(resultedWhether)+"/");}
					return resultedWhether;
				}else {
					return Alertable;
				}
			}else {
				return Alertable;
			}
		}catch (Exception e){
			//e.printStackTrace();
			return Alertable;
		}
	}
	
	public static String winnowoutFirstfromDateTime (String DateTime){
		DateTime=DateTime.trim();
		String [] tempDateTime = DateTime.split(" ");
		String sign="-";
		String [] tempFirst = tempDateTime[0].split(sign);
		if (tempFirst.length<=1){
			sign=":";
			tempFirst=tempFirst[0].split(sign);
		}
		String result="";
		for (int i=1;i<tempFirst.length;i++){
			if (i<tempFirst.length-1){
				result+=tempFirst[i]+sign;
			}else {
				result+=tempFirst[i];
			}
		}
		result +=" ";
		for (int i=1;i<tempDateTime.length;i++){
			result+=tempDateTime[i];
		}
		result = result.trim();
		if (result.isEmpty()){
			result=DateTime;
		}
		return result;
	}
	
	public static int validateStringFormatforHex (String number)throws NumberFormatException{
        long sum=0;
        if (number.length()!=10){
            throw new NumberFormatException("Length is not Right");
        }
        number=number.substring(2,number.length());
        number=number.toUpperCase();
        //System.out.println(number);
        for (int i=0;i<number.length();i++){
            int multiplier=0;
            switch (number.charAt(i)){
                case '0':multiplier=0;break;
                case '1':multiplier=1;break;
                case '2':multiplier=2;break;
                case '3':multiplier=3;break;
                case '4':multiplier=4;break;
                case '5':multiplier=5;break;
                case '6':multiplier=6;break;
                case '7':multiplier=7;break;
                case '8':multiplier=8;break;
                case '9':multiplier=9;break;
                case 'A':multiplier=10;break;
                case 'B':multiplier=11;break;
                case 'C':multiplier=12;break;
                case 'D':multiplier=13;break;
                case 'E':multiplier=14;break;
                case 'F':multiplier=15;break;
                default:throw new NumberFormatException("Invalid Char in the String");

            }
            sum+=multiplier*Math.pow(16,(number.length()-i-1));
            //System.out.print(sum);
        }
        //System.out.println();
        //System.out.println(sum);
        if(sum>4294967295L){
            return 2147483647;}
        // else if (sum<=2147483647){
        //    return (int)sum;
        //}
        //long positivizedmaximumofint = 4294967295L;
        //int Int = (int)((positivizedmaximumofint+1-sum)*(-1));
        //System.out.println(Int);
        return (int)sum;
        
    }
	
	
	
	public static long convertDatetoSeconds(String Date,int Type,boolean whethertoprint){
		long[] Dataset = new long[3];
		int[] array=formatData(Date.trim());
		//Dataset[1]+=365*(array[0]-1)+Math.floor((array[0]-1)/4);
        for (int index = 1; index < array[0]; index++) {
            Dataset[1] += ((index) % 4 == 0) ? 366 : 365;
        }
        for (int index = 1; index <= array[1] - 1; index++) {
            if (index == 2) {
                Dataset[1] += (array[0] % 4 == 0) ? 29 : 28;
            } else if (index <= 7 && (index - 1) % 2 == 0) {
                Dataset[1] += 31;
            } else if (index > 7 && (index) % 2 == 0) {
                Dataset[1] += 31;
            } else {
                Dataset[1] += 30;
            }
        }
        if (array[2]<=0){
        	Dataset[1]+=0;
        }else{
        	Dataset[1] += array[2] - 1;
        }
        Dataset[2] = array[3] * 3600;
        Dataset[2] += array[4] * 60;
        Dataset[2] += array[5];
        if (whethertoprint){
        	System.out.println("Days:"+Dataset[1]+"Seconds:"+Dataset[2]);
        }
        Dataset[0] = Dataset[1] * 24 * 3600 + Dataset[2];
        switch (Type){
        case Data.DateTime:return Dataset[0];
        case Data.Date:return Dataset[1];
        case Data.Time:return Dataset[2];
        default:break;
        }
        return Dataset[0];
	}
	
	private static int[] formatData(String data){
        //String d ="2016-10-18 22:28:23.0";
        //data=data.substring(0,data.length()-2);
		String[] tempDate;String[] tempTime=null;int[] Time=new int[]{-1,-1,-1};int[] Date=new int[]{-1,-1,-1};
		
		String[] tempString=data.split(" ");
		
        tempDate=tempString[0].split("-");
        if (tempDate.length>1){
        	for (int i=0;i<=Date.length-1;i++){
        		int index = i-(Date.length-tempDate.length);
        		if (index>=0){
        			Date[i]=Math.round(Float.parseFloat(tempDate[index]));
        		}
            	if (Date[i]<=0){Date[i]=1;}
        	}
        }
        if (tempString.length>1){
            tempTime=tempString[1].split(":");
        }
        else {
            tempTime=tempString[0].split(":");
        }
        if (tempTime.length>1){
        	for (int i=0;i<=Time.length-1;i++){
        		int index = i-(Time.length-tempTime.length);
        		if (index>=0){
        			Time[i]=Math.round(Float.parseFloat(tempTime[index]));
        		}
        		if (Time[i]<0){Time[i]=0;}
        	}
        }
        
        int[] Array=new int[Time.length+Date.length];
        for (int index=0;index<=Time.length+Date.length-1;index++){
            Array[index]=(index<=3-1)?Date[index]:Time[index-Date.length];
        }
        int sum=0;
        for (int temp:Array){
        	sum+=temp;
        }
        if (sum<=Array.length*-1){
        	throw new NumberFormatException ("String doesn't fit any Format");
        }
        return Array;
    }
}
