package smart_home;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class Cleaner
 */
//@WebServlet("/Cleaner")
public class Cleaner implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		Data.client.dispatcher().cancelAll();
		Data.client.connectionPool().evictAll();
		initiate.stopRc();
		initiate.stopPUS();
		
		System.out.println("Cleaner Clean");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
