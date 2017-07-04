package smart_home;

import java.io.IOException;
import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class InquireData
 */
@WebServlet("/InquireData")
public class InquireData extends HttpServlet {
	//private static final String Key="DataInquire";
	private static final long serialVersionUID = 5L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InquireData() {
        super();
       
        // TODO Auto-generated constructor stub
    }

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
		if(request.getParameter("user")!=null&&request.getParameter("user").equals("henry")&&request.getParameter("pass")!=null&&request.getParameter("pass").equals("yiweigang")
				&&request.getParameter("Type")!=null){
			
			//wr.println("<html>");
			//wr.println("<head>");
			//wr.println("<title>MyFirstServlet</title>");
			//wr.println("<h2>Servlet InquireServlet at " + request.getContextPath() + "</h2>");
			//wr.println("</head>");
			//wr.println("<body>");
			JSONObject json=inquireData(request.getParameter("Type").trim());
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
			System.out.println("DataInquire Illegal Access");
			response.sendError(401,"Illegal Access");
			//response.getWriter().println("Illegal Access"+"<br>");
			//response.getWriter().println("Hello"+"<br>");
			response.getWriter().close();
		}
	}
	
	public static JSONObject inquireData(String Type){
		switch (Type){
		case Data.Temperature:
			return MacroAtmosphericDataInquire.getData(Data.Temperature);
		case Data.Humidity: 
			return MacroAtmosphericDataInquire.getData(Data.Humidity);
		case Data.appliance:
			return ApplianceInquire.getData();
		default:
			return null;
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
