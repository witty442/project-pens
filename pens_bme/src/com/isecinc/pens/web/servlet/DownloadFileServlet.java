package com.isecinc.pens.web.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.pens.util.Utils;

@SuppressWarnings( { "static-access", "serial", "deprecation" })
public class DownloadFileServlet extends HttpServlet {

	Logger logger = Logger.getLogger("PENS");
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
        Connection conn = null;
	    try {
	    	User user = (User) request.getSession().getAttribute("user");
	    	String pathFile = Utils.isNull("pathFile");
	    	
	    } catch (Exception e) {
	        e.printStackTrace();
	        logger.error(e.getMessage(),e);
	    }finally{
	    
	    }
	}
	
	
}
