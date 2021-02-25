package com.isecinc.pens.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.model.MCustomer;
import com.pens.util.Utils;

@SuppressWarnings( { "static-access", "serial", "deprecation" })
public class PhotoCustomerServlet extends HttpServlet {

	Logger logger = Logger.getLogger("PENS");

	//BeanParameter beanParameter = new BeanParameter();
	//public String path = beanParameter.getReportPath();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		 File file = null;
		 FileInputStream fis = null;
	    try {
	    	 String customerId = Utils.isNull(request.getParameter("customerId"));
	    	  logger.debug("PhotoCustomerServlet customerId:"+customerId);
	    	  if(!"".equals(customerId)){
		    	  String imageFileLocalPath=  "";//(String)session.getAttribute("imageFileName");  
		    	  Customer c = new MCustomer().getImageFileName(customerId);
		    	  if(c != null){
		    		  imageFileLocalPath = c.getImageFileName(); 
		    		  logger.debug("imageFileLocalPath:"+imageFileLocalPath);
		    	  }
		    	  
		    	  String typeFile = imageFileLocalPath.substring(imageFileLocalPath.indexOf(".")+1,imageFileLocalPath.length());
		    	  System.out.println("typeOfFile:"+typeFile);
		     
			       // Place a gif file in your web app's root folder for test.
			       file = new File(imageFileLocalPath);
			       fis = new FileInputStream(file);
			       byte[] imgData = new byte [(int)file.length()];
			       fis.read(imgData);
			       //response.reset();
			       response.setContentType("image/"+typeFile);
	               response.getOutputStream().write(imgData);
	    	  }
	       
	    } catch (Exception e) {
	        e.printStackTrace();
	    }finally{
	    	if(fis != null){
	    		fis.close();fis=null;
	    	}
	    	if(file !=null){
	    		file=null;
	    	}
	    }
	}
	
	
}
