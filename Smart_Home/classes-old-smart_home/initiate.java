package smart_home;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	private final static String url="jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
	private final static String inquirefix="inquire";
	private final static int RCperiod=12*3600*1000;
	private final static int PUSperiod=2*1000;
	//private static volatile boolean firstbooted=true;
       
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
	
    public initiate() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
	public void contextDestroyed(ServletContextEvent arg0) {
    	stopnewThread=true;
    	Data.client.dispatcher().executorService().shutdown();
		Data.client.dispatcher().cancelAll();
		Data.client.connectionPool().evictAll();
		try {
			Data.client.cache().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopRc();
		stopPUS();
		System.out.println();
		System.out.println("Time to exit");
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		try {
			init(0);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
    public void init (int trytimes) throws ServletException{
    	
    		
    		Connection con;
    		try {
    			System.out.println("Has Started");
    			con = DriverManager.getConnection(url);
				resetActivated(con);
				activateRegularCheck();
				activatePassiveUpdationForStatus ();
				con.close();
    		} catch (SQLException e) {
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
    			int TemperaturePeriod=Data.PeriodPassiveOfAtmosphericData(0,Data.Temperature);
    			int HumidityPeriod=Data.PeriodPassiveOfAtmosphericData(0,Data.Humidity);
    			int AppliancePeriod=Data.PeriodPassiveOfAtmosphericData(0,Data.appliance);
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
    						
    						try{
    							String URL=InquireConfigurations.inquireData(0, Data.Temperature).getString("URL");
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
    							String URL=InquireConfigurations.inquireData(0, Data.Humidity).getString("URL");
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
    							json=InquireConfigurations.inquireData(0, Data.appliance);
    							ids=json.getJSONArray("id");
    							URLs=json.getJSONArray("URL");
    							for (int i=0;i<ids.size();i++){
    								try{
    									int id=ids.getInt(i);
    									if (Data.PeriodPassiveOfAtmosphericData(id, Data.appliance)>=0){
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
            					e.printStackTrace();
            				}
    						
    						
    						
    						accumulatedTimeofA+=AppliancePeriod;
    						
    					}
    					if(Thread.currentThread().isInterrupted()){
    						return;
    					}
    					
    					if (times>=5){
    						int T=Data.PeriodPassiveOfAtmosphericData(0,Data.Temperature);
    						int H=Data.PeriodPassiveOfAtmosphericData(0,Data.Humidity);
    						int A=Data.PeriodPassiveOfAtmosphericData(0,Data.appliance);
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
    					
    					json=Inquire.inquireData(0);
            			ids=json.getJSONArray("id");
            			URLs=json.getJSONArray("URL");
            			for (int i=0;i<ids.size();i++){
            				try{
            					int id=ids.getInt(i);
                				if (Data.WhetherPassive(id)){
                					RealStatusWithAlerted=Data.syncGetRealStatus(URLs.getString(i)+inquirefix);
                					if(Thread.currentThread().isInterrupted()){
                						return;
                					}
                					RealStatus = RealStatusWithAlerted[0];
                					Alerted = RealStatusWithAlerted[1]; 
                					if(RealStatus==Data.True){
                						RegistrationPlusStateupdate.updateData(id, null, "192.168.1."+id, null, null, Data.True, Data.Default, Data.Default, Data.Default, null);
                					}else if(RealStatus==Data.False){
                						RegistrationPlusStateupdate.updateData(id, null, "192.168.1."+id, null, null, Data.False, Data.Default, Data.Default, Data.Default, null);
                					}
                					if(Alerted==Data.True){
                						RegistrationPlusStateupdate.updateData(id, null, "192.168.1."+id, null, null, Data.Default, Data.Default, Data.True, Data.Default, null);
                					}else if(Alerted==Data.False){
                						RegistrationPlusStateupdate.updateData(id, null, "192.168.1."+id, null, null, Data.Default, Data.Default, Data.False, Data.Default, null);
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
    					e.printStackTrace();
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
			String sql="select * from apparatus";
			ResultSet rs=null;PreparedStatement prestatement=null;
			DateFormat format=SimpleDateFormat.getDateTimeInstance();
			try {
				
				while(true){
					if(Thread.currentThread().isInterrupted()){
						return;
					}
					try {
						System.out.println();
						System.out.println("regularCheck:"+format.format(new Date()));
						con = DriverManager.getConnection(url);
						prestatement=con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
						rs=prestatement.executeQuery();
						if (rs!=null){
							while (rs.next()){
								if (!rs.getBoolean("P")){
									try{
									if(Math.abs(Data.convertDatetoSeconds(format.format(new Date()),false)-Data.convertDatetoSeconds(rs.getString("lastactivetime"),false))
										>=28*24*3600){
										Data.convertDatetoSeconds(format.format(new Date()),true);
										Data.convertDatetoSeconds(rs.getString("lastactivetime"),true);
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
    	String url="jdbc:mysql://localhost/smart_home?user=root&password=yiweigang&useUnicode=true&characterEncoding=utf-8";
    	ResultSet rs=null;//Statement statement;
    	PreparedStatement prestatement=null;
    	try {
    		con=DriverManager.getConnection(url);
    		//String sql="update apparatus set activated=false where passive=true";
			prestatement=con.prepareStatement("select * from apparatus",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
		} catch (SQLException e) {
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
