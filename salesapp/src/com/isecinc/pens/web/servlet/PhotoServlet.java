package com.isecinc.pens.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.pens.util.EnvProperties;
import com.pens.util.Utils;

@SuppressWarnings( { "static-access", "serial", "deprecation" })
public class PhotoServlet extends HttpServlet {

	Logger logger = Logger.getLogger("PENS");


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		 String imageFileLocalPath = "";
		 File file = null;
		 FileInputStream fis = null;
	    try {
	    	 String pageName = Utils.isNull(request.getParameter("pageName"));
	    	 String fileName = Utils.isNull(request.getParameter("fileName"));
	    	 if( !Utils.isNull(fileName).equals("")){
		    	 if("prodShow".equalsIgnoreCase(pageName)){
			    	 imageFileLocalPath=  EnvProperties.getInstance().getProperty("path.image.prodshow.local");
		    	 }
		    	 
		    	 imageFileLocalPath += fileName;
		    	 logger.debug("imageFileLocalPath:"+imageFileLocalPath);
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
