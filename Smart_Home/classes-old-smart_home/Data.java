package smart_home;

import java.io.IOException;
//import java.sql.Connection;
//import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tomcat.jdbc.pool.DataSource;
//import org.apache.tomcat.jdbc.pool.interceptor.StatementCache;

import net.sf.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Data {
	public static final OkHttpClient client= new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.MILLISECONDS).build();
	public static final OkHttpClient client2= client.newBuilder().connectTimeout(200, TimeUnit.MILLISECONDS).readTimeout(10000, TimeUnit.MILLISECONDS).writeTimeout(10000, TimeUnit.MILLISECONDS).build();
	public static final OkHttpClient client3= client.newBuilder().connectTimeout(5000, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true).build();
	public static final int Default=0;
	public static final int True=2;
	public static final int False=1;
	public static final String Temperature="Temperature";
	public static final String Humidity="Humidity";
	public static final String appliance="appliance";
	public static final String Apparatus="Apparatus";
	
	public static final String PUSKeyAp="PassiveApparatusFetch";
	public static final String PUSKeyDa="PassiveDataFetch";
	
	public static final String [] debugkeys = {PUSKeyAp,PUSKeyDa,"DataInquire","DataUpdate","ApparatusInquire","ApparatusUpdate","Control"};
	public static final Map<String,Boolean> debugstatus=new HashMap <>(7);
	
	public static DataSource ds;
	
	static {
		synchronized (debugstatus){
			for (String temp:debugkeys){
				debugstatus.put(temp,false);
			}
		}
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Context initContext = new InitialContext();
			ds = (DataSource)initContext.lookup("java:comp/env/jdbc/Smart_Home");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//*其实用getConnectionAsync异步获得连接的方法相比原来的用getConnection同步获得连接的方法
		//-首先就是它不会阻塞(block)连接池，同时也可以让请求连接的线程通过判断isDone()状态，来在连接
		//-还没有准备好的时候做一些其他的工作就像下面这样。 当然其实也可以不判断isDone，直接get，不过
		//-这样的话就会阻塞(block)请求连接的线程，直到获得可用的连接。
		//Connection con=null;
		//try {
		//	Future<Connection> Fcon=ds.getConnectionAsync();
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
			response= client3.newCall(request).execute();//其实在这里response不可能是null，因为根据OKHttpClient的源代码，他如果是null也会throw出一个错误，到下面的catch语块。
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
	
	public static int[] GetRealStatus(String URL){
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
	}
	
	public static boolean WhetherPassive(int id){
		try {
			
			JSONObject json=InquireConfigurations.inquireData(id,Apparatus);
			return json.getBoolean("passive");
		}catch (Exception e){
			return false;
		}
	}
	
	public static boolean StatusInDatabase(int id,boolean inquireactivated){
		try {
			JSONObject json=Inquire.inquireData(id);
			if (inquireactivated){
					return json.getBoolean("activated");
				}else{
					return json.getBoolean("Alerted");
				}
		}catch (Exception e){
			return false;
		}
	}
	
	public static int PeriodPassiveOfAtmosphericData(int id,String Type){
		try{
			
			JSONObject json=InquireConfigurations.inquireData(id, Type);
			if (Type.equals(Data.appliance)&&id==0){
				if (json.getInt("Period")>=0 ){
					return json.getInt("Period");
				}else{
					return -1;
				}
			}else{
				if (json.getInt("Period")>=0 && !json.getString("URL").isEmpty()){
				return json.getInt("Period");
			}else{
				return -1;
			}
			}
			
		}catch(Exception e){
			return -1;
		}
	}
	
	private static class updateCallback implements Callback{
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
	
	
	
	public static long convertDatetoSeconds(String Date,boolean whethertoprint){
		long[] Dataset = new long[3];
		int[] array=formatData(Date);
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
        Dataset[1] += array[2] - 1;
        Dataset[2] = array[3] * 3600;
        Dataset[2] += array[4] * 60;
        Dataset[2] += array[5];
        if (whethertoprint){
        	System.out.println("Days:"+Dataset[1]+"Seconds:"+Dataset[2]);
        }
        Dataset[0] = Dataset[1] * 24 * 3600 + Dataset[2];
        return Dataset[0];
	}
	
	private static int[] formatData(String data){
        //String d ="2016-10-18 22:28:23.0";
        //data=data.substring(0,data.length()-2);
        String[] tempString=data.split(" ");
        String[] tempDate=tempString[0].split("-");
        String[] tempTime=tempString[1].split(":");
        int Date[]=new int[tempDate.length];
        int Time[]=new int[tempTime.length];
        for (int index=0;index<=Date.length-1;index++){
            Date[index]=Math.round(Float.valueOf(tempDate[index]));
            if (Date[index]<=0){Date[index]=1;}
        }
        for (int index=0;index<=Time.length-1;index++){
            Time[index]=Math.round(Float.valueOf(tempTime[index]));
        }

        int[] Array=new int[Date.length+Time.length];
        for (int index=0;index<=Date.length+Time.length-1;index++){
            Array[index]=(index<=Date.length-1)?Date[index]:Time[index-Date.length];
        }
        return Array;
    }
}
