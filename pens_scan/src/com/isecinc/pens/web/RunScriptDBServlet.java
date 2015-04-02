package com.isecinc.pens.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.isecinc.pens.web.runscriptdb.RunScriptDBAction;


public class RunScriptDBServlet  extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1406874613983725131L;
	private Logger logger = Logger.getLogger("PENS");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void init() throws ServletException {
		logger.info("Start ManualRunScriptDBServlet...");
		try {
			 ServletContext sc =  getServletConfig().getServletContext();
			 RunScriptDBAction.runProcessOnStartSalesApp(sc);
		
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			
		}
	}
}
