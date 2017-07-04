package smart_home;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UpdateData
 */
@WebServlet("/UpdateData")
public class UpdateData extends HttpServlet {
	private static final long serialVersionUID = 4L;
	private static final String Key="DataUpdate";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateData() {
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
		if(request.getParameter("user")!=null&&request.getParameter("user").equals("henry")&&request.getParameter("pass")!=null&&request.getParameter("pass").equals("yiweigang")
				&&request.getParameter("Type")!=null){
			Integer Period;Integer degree;String URL; Character UnitSign;
			if (request.getParameter("URL")!=null){
				URL=request.getParameter("URL").trim();
			}else{
				URL=null;
			}
			
			try {
				Period=Integer.parseInt(request.getParameter("Period").trim());
			}catch (Exception e){
				Period=null;
			}
			try{
				degree=Integer.parseInt(request.getParameter("degree").trim());
			}catch(Exception e){
				degree=null;
			}
			if (request.getParameter("Unit")!=null){
				String tempUnitSign=request.getParameter("Unit").trim();
				if (tempUnitSign.length()==1){
					UnitSign=tempUnitSign.charAt(0);
				}else{
					UnitSign=null;
				}
			}else{
				UnitSign=null;
			}
			
			
			if(request.getParameter("Type").trim().equals("Humidity")){
				try {
					HumidityUpdate.update(degree,UnitSign,Period,URL);
				}catch (Exception e){
					if (Data.debugstatus.get(Key)){
					System.out.println(e);}
					response.setStatus(404);
					response.getWriter().println(e+"<br>");
					response.getWriter().println("Hello"+"<br>");
				}
			}else if(request.getParameter("Type").trim().equals("Temperature")){
				try {
					TemperatureUpdate.update(degree,UnitSign,Period,URL);
				}catch (Exception e){
					if (Data.debugstatus.get(Key)){
					System.out.println(e);}
					response.setStatus(404);
					response.getWriter().println(e+"<br>");
					response.getWriter().println("Hello"+"<br>");
				}
			}else if (request.getParameter("Type").trim().equals("appliance")){
				Integer KWH = degree;
				Integer tempid;
				if (request.getParameter("id")!=null){
					try {
						tempid = Integer.parseInt(request.getParameter("id").trim());
					}catch (Exception e){
						if (Data.debugstatus.get(Key)){
						System.out.println(":|:Id format Wrong Or No Need of Specific Id:|:");}
						response.getWriter().println("Id format Wrong Or No Need of Specific Id"+"<br>");
						String iURL = request.getRemoteAddr();
						String[] URLTemp=iURL.split("\\.");
						if (URLTemp.length==1){URLTemp=iURL.split(":");}
						String trimedID=URLTemp[URLTemp.length-1].trim();
						try{
							tempid = Integer.parseInt(trimedID);
						}catch (Exception e2){
							if (Data.debugstatus.get(Key)){
							System.out.println("/|/IP Address Format Not Supported:Fatal/|/");}
							response.setStatus(404);
							response.getWriter().println("IP Address Format Not Supported:Fatal"+"<br>");
							return;
						}
						
						//tempid = tempid - 10;
						//String id = String.valueOf(tempid);
					}
				}else{
					tempid=null;
				}
				if (request.getParameter("name")!=null&&!request.getParameter("name").isEmpty()
						&&request.getParameter("Color")!=null&&!request.getParameter("Color").isEmpty()){
					try{
						int tempp=Data.validateStringFormatforHex(request.getParameter("Color"));
						if (Data.debugstatus.get(Key)){System.out.println(tempp);}response.getWriter().println(tempp+"<br>");
						ApplianceUpdate.update(KWH, tempid,request.getParameter("name"), request.getParameter("Color"),Period,URL);
					}catch(Exception e){
						e.printStackTrace();
						if (Data.debugstatus.get(Key)){System.out.println("Keep Previous Color");}response.getWriter().println("Keep Previous Color"+"<br>");
						ApplianceUpdate.update(KWH, tempid,request.getParameter("name"), null,Period,URL);
					}
				}else if (request.getParameter("name")!=null&&!request.getParameter("name").isEmpty()){
					ApplianceUpdate.update(KWH, tempid, request.getParameter("name"), null,Period,URL);
				}else if (request.getParameter("Color")!=null&&!request.getParameter("Color").isEmpty()){
					try{
						int tempp=Data.validateStringFormatforHex(request.getParameter("Color"));
						if (Data.debugstatus.get(Key)){System.out.println(tempp);}response.getWriter().println(tempp+"<br>");
						ApplianceUpdate.update(KWH, tempid,null, request.getParameter("Color"),Period,URL);
					}catch(Exception e){
						e.printStackTrace();
						if (Data.debugstatus.get(Key)){System.out.println("Keep Previous Color");}response.getWriter().println("Keep Previous Color"+"<br>");
						ApplianceUpdate.update(KWH, tempid,null, null,Period,URL);
					}
				}else{
					ApplianceUpdate.update(KWH, tempid, null, null,Period,URL);
				}
				
			}else{
				System.out.println("DataUpdate Illegal Content");
				response.sendError(400,"Illegal Content");
				//response.getWriter().println("Illegal Content"+"<br>");
			}
			response.getWriter().close();
		}else{
			System.out.println();
			System.out.println("DataUpdate Illegal Access");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
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
