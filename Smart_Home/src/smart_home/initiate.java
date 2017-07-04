package smart_home;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class initiate
 */
//@WebServlet("/initiate")
public class initiate implements ServletContextListener {
	
	private static final long serialVersionUID = 10L;
	private volatile static regularCheck rC;
	private volatile static PassiveUpdationForStatus PUS;
	private volatile static boolean stopnewThread=false;
	private final static String inquirefix="inquire";
	private final static int RCperiod=12*3600*1000;
	private final static int PUSperiod=2*1000;
	//private static volatile boolean firstbooted=true;
	

    @Override
	public void contextDestroyed(ServletContextEvent arg0) {
    	stopnewThread=true;
    	Data.client.dispatcher().executorService().shutdown();
		Data.client.dispatcher().cancelAll();
		Data.client.connectionPool().evictAll();
		try {
			Data.client.cache().close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Control.stopAll=true;
		stopRc();
		stopPUS();
		System.out.println();
		System.out.println("Time to exit");
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Control.stopAll=false;
		try {
			init(0);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
    public void init (int trytimes) throws ServletException{
    		Connection con=null;
    		try {
    			System.out.println("Has Started");
    			Future<Connection> Fcon=Data.ds.getConnectionAsync();
    			if (!Fcon.isDone()){
    				if (Data.debugstatus.get(InquireConfigurations.Key)){
    					System.out.println("initiate"+":Connection is not yet available. We need to wait");}
    				int trytimes2=0;
    				while (!Fcon.isDone()){
    					if (trytimes2<100){
    						Thread.sleep(200);
    						trytimes2++;
    					}else {
    						throw new TimeoutException("Wait Too Long, Something Wrong, Got to Quit and Restart");
    					}
    				}
    				if (Data.debugstatus.get(InquireConfigurations.Key)){
    					System.out.println("initiate"+":We have Got A Connection");}
    			}
    			con=Fcon.get();
				resetActivated(con);
				activateRegularCheck();
				activatePassiveUpdationForStatus ();
				//con.close();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			if (trytimes<=5-1){
    				init(trytimes+1);
    			}
    		}finally{
    			try {
    				if (con!=null){
    					con.close();
    				}
    			}catch (SQLException e){
    				e.printStackTrace();
    			}
    			
    		}
    		
    }
    
    public static void activatePassiveUpdationForStatus (){
    	if (!stopnewThread){
    		if (PUS==null||!PUS.isAlive()){
    		PUS=new PassiveUpdationForStatus(PUSperiod);
    		PUS.setName("PassiveUpdation");
    		PUS.setDaemon(true);
    		PUS.start();
    		}
    	}
    	
    }
    
    public static void activateRegularCheck (){
    	if (!stopnewThread){
    		if (rC==null||!rC.isAlive()){
    		rC=new regularCheck(RCperiod);
    		rC.setName("RegularCheck");
    		rC.setDaemon(true);
    		rC.start();
    		}
    	}
    	
    	
    }
    
    public static void stopRc(){
    	if (rC!=null){
    		rC.interrupt();
    	}
    }
    
    public static void stopPUS(){
    	if (PUS!=null){
    		PUS.interrupt();
    	}
    }
    
    private static class PassiveUpdationForStatus extends Thread{
    	final int period;
    	
    	@Override
		public void run() {
    		JSONObject json;
    		JSONArray ids;JSONArray URLs;int [] RealStatusWithAlerted;int RealStatus;int Alerted;
    		Object[] degree;
    		try {
    			int TemperaturePeriod=Data.PeriodPassiveOfAtmosphericData(0,Data.Temperature,null);
    			int HumidityPeriod=Data.PeriodPassiveOfAtmosphericData(0,Data.Humidity,null);
    			int AppliancePeriod=Data.PeriodPassiveOfAtmosphericData(0,Data.appliance,null);
    			long accumulatedPeriod =0;
    			long accumulatedTimeofT=0;
    			long accumulatedTimeofH=0;
    			long accumulatedTimeofA=0;
    			int times=0;
    			while (true){
    				if(Thread.currentThread().isInterrupted()){
						return;
					}
    				try{
    					if (TemperaturePeriod>=0&&accumulatedPeriod>=accumulatedTimeofT+TemperaturePeriod){
    						//System.out.println("Temperature");
    						try{
    							String URL=InquireData.inquireData(Data.Temperature, Data.Type_InquireConfigurations).getString("URL");
    							degree=Data.syncGetRealDegree(URL+inquirefix);
    							if (degree [0]!=null&&degree[1]!=null){
    								TemperatureUpdate.update((Integer)degree[0],(Character)degree[1],null,null);
    							}
    						}
    						catch (NullPointerException e){
            					e.printStackTrace();
            					return;
            				}
            				catch(Exception e){
            					e.printStackTrace();
            				}
    						
    						accumulatedTimeofT+=TemperaturePeriod;
    						
    					}
    					if(Thread.currentThread().isInterrupted()){
    						return;
    					}
    					if (HumidityPeriod>=0&&accumulatedPeriod>=accumulatedTimeofH+HumidityPeriod){
    						
    						try{
    							String URL=InquireData.inquireData(Data.Humidity, Data.Type_InquireConfigurations).getString("URL");
    							degree=Data.syncGetRealDegree(URL+inquirefix);
    							if (degree[0]!=null&&degree[1]!=null){
    								HumidityUpdate.update((Integer)degree[0],(Character)degree[1],null,null);
    							}
    						}
    						catch (NullPointerException e){
            					e.printStackTrace();
            					return;
            				}
            				catch(Exception e){
            					e.printStackTrace();
            				}
    						
    						accumulatedTimeofH+=HumidityPeriod;
    						
    					}
    					if(Thread.currentThread().isInterrupted()){
    						return;
    					}
    					if (AppliancePeriod>=0&&accumulatedPeriod>=accumulatedTimeofA+AppliancePeriod){
    						try{
    							json=InquireData.inquireData(Data.appliance, Data.Type_InquireConfigurations);
    							ids=json.optJSONArray("id");
    							if (ids==null){ids=new JSONArray().element(json.getInt("id"));}
    							URLs=json.optJSONArray("URL");
    							if (URLs==null){URLs=new JSONArray().element(json.getString("URL"));}
    							for (int i=0;i<ids.size();i++){
    								try{
    									int id=ids.getInt(i);
    									if (Data.PeriodPassiveOfAtmosphericData(id, Data.appliance,json)>=0){
    										degree=Data.syncGetRealDegree(URLs.getString(i)+inquirefix);
    										if (degree[0]!=null){
    											ApplianceUpdate.update((Integer)degree[0], id,null,null,null,null);
    										}
    									}
    								}
    								catch (NullPointerException e){
    									e.printStackTrace();
    									return;
    								}
    								catch(Exception e){
    									e.printStackTrace();
    								}
    							}
    						}
    						catch (NullPointerException e){
            					e.printStackTrace();
            					return;
            				}
            				catch(Exception e){
            					//e.printStackTrace();
            				}
    						
    						
    						
    						accumulatedTimeofA+=AppliancePeriod;
    						
    					}
    					if(Thread.currentThread().isInterrupted()){
    						return;
    					}
    					
    					if (times>=5){
    						int T=Data.PeriodPassiveOfAtmosphericData(0,Data.Temperature,null);
    						int H=Data.PeriodPassiveOfAtmosphericData(0,Data.Humidity,null);
    						int A=Data.PeriodPassiveOfAtmosphericData(0,Data.appliance,null);
    						if (TemperaturePeriod!=T){
    							accumulatedTimeofT=accumulatedPeriod;
    							TemperaturePeriod=T;
    						}
    						if (HumidityPeriod!=H){
    							accumulatedTimeofH=accumulatedPeriod;
    							HumidityPeriod=H;
    						}
    						if (AppliancePeriod!=A){
    							accumulatedTimeofA=accumulatedPeriod;
    							AppliancePeriod=A;
    						}
    						
    						times=0;
    					}else{
    						times++;
    					}
    					if(Thread.currentThread().isInterrupted()){
    						return;
    					}
    					
    					json=Inquire.inquireData(0,Data.Type_InquireConfigurations);
    					ids=json.optJSONArray("id");
						if (ids==null){ids=new JSONArray().element(json.getInt("id"));}
						URLs=json.optJSONArray("URL");
						if (URLs==null){URLs=new JSONArray().element(json.getString("URL"));}
            			for (int i=0;i<ids.size();i++){
            				try{
            					int id=ids.getInt(i);
                				if (Data.WhetherPassive(id,ids,json)){
                					RealStatusWithAlerted=Data.syncGetRealStatus(URLs.getString(i)+inquirefix);
                					if(Thread.currentThread().isInterrupted()){
                						return;
                					}
                					RealStatus = RealStatusWithAlerted[0];
                					Alerted = RealStatusWithAlerted[1]; 
                					if(RealStatus==Data.True){
                						RegistrationPlusStateupdate.updateData(id, null, "192.168.1."+id, null, null, Data.True, Data.Default, Data.Default, Data.Default, Data.Default, null);
                					}else if(RealStatus==Data.False){
                						RegistrationPlusStateupdate.updateData(id, null, "192.168.1."+id, null, null, Data.False, Data.Default, Data.Default, Data.Default, Data.Default, null);
                					}
                					if(Alerted==Data.True){
                						RegistrationPlusStateupdate.updateData(id, null, "192.168.1."+id, null, null, Data.Default, Data.Default, Data.Default, Data.True, Data.Default, null);
                					}else if(Alerted==Data.False){
                						RegistrationPlusStateupdate.updateData(id, null, "192.168.1."+id, null, null, Data.Default, Data.Default, Data.Default, Data.False, Data.Default, null);
                					}
                					
                					
                				}
            				}
            				catch (NullPointerException e){
            					e.printStackTrace();
            					return;
            				}
            				catch(Exception e){
            					e.printStackTrace();
            				}
            				
            			}
    				}
    				catch (NullPointerException e){
    					e.printStackTrace();
    					return;
    				}
    				catch (Exception e){
    					//e.printStackTrace();
    				}finally{
    					
    				}
    				if(Thread.currentThread().isInterrupted()){
						return;
					}
    				Thread.sleep(period);
    				accumulatedPeriod+=period;
        		}
    		}catch (Exception e){
    			e.printStackTrace();
    		}finally{
    			
    		}
    		
    		
		}

		PassiveUpdationForStatus (int period){
    		this.period=period;
    	}
    }
    
    private static class regularCheck extends Thread{
    	final int period;Connection con;
		@Override
		public void run() {
			String sql="select * from "+Data.apparatus+"";
			ResultSet rs=null;PreparedStatement prestatement=null;
			DateFormat format=Data.format;
			try {
				
				while(true){
					if(Thread.currentThread().isInterrupted()){
						return;
					}
					try {
						System.out.println();
						System.out.println("regularCheck:"+format.format(new Date()));
						Future<Connection> Fcon=Data.ds.getConnectionAsync();
						if (!Fcon.isDone()){
		    				if (Data.debugstatus.get(InquireConfigurations.Key)){
		    					System.out.println("RegularCheck"+":Connection is not yet available. We need to wait");}
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
		    					System.out.println("RegularCheck"+":We have Got A Connection");}
		    			}
		    			con=Fcon.get();
						prestatement=con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
						rs=prestatement.executeQuery();
						if (rs!=null){
							while (rs.next()){
								if (!rs.getBoolean("P")){
									try{
										System.out.println(format.format(new Date()));
									if(Math.abs(Data.convertDatetoSeconds(format.format(new Date()),Data.DateTime,false)-Data.convertDatetoSeconds(rs.getString("lastactivetime"),Data.DateTime,false))
										>=28*24*3600){
										Data.convertDatetoSeconds(format.format(new Date()),Data.DateTime,true);
										Data.convertDatetoSeconds(rs.getString("lastactivetime"),Data.DateTime,true);
										rs.deleteRow();
									}
									}catch (Exception e){
										e.printStackTrace();
									}
								}
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						if(rs!=null){
							rs.close();
						}
						if(prestatement!=null){
							prestatement.close();
						}
						if(con!=null){
							con.close();
						}
					}
					if(Thread.currentThread().isInterrupted()){
						return;
					}
					Thread.sleep(period);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try {
					if(rs!=null){
						rs.close();
					}
					if(prestatement!=null){
						prestatement.close();
					}
					if(con!=null){
						con.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}
		
		regularCheck(int period){
			this.period=period;
		}
    	
    }
    
    void resetActivated(Connection con){
    	ResultSet rs=null;//Statement statement;
    	PreparedStatement prestatement=null;
    	try {
    		//String sql="update "+Data.apparatus+" set activated=false where passive=true";
			prestatement=con.prepareStatement("select * from "+Data.apparatus+"",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			//prestatement.executeUpdate();
    		//statement=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs= prestatement.executeQuery();
			if(rs!=null){
				while(rs.next()){
					if(rs.getBoolean("passive")){
						rs.updateBoolean("activated", false);
						rs.updateRow();
					}
				}
				System.out.println("Has Initiated the Status");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null){
					rs.close();
				}
				if(prestatement!=null){
					prestatement.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
    }

	
    
	

}
