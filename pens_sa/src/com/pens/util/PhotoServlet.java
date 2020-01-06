package com.pens.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import com.isecinc.pens.report.salesanalyst.helper.EnvProperties;

@SuppressWarnings( { "static-access", "serial", "deprecation" })
public class PhotoServlet extends HttpServlet {

	Logger logger = Logger.getLogger("PENS");
	BeanParameter beanParameter = new BeanParameter();
	public String path = beanParameter.getReportPath();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
        //path image-> /PENS/TEST/apps/inst/saleonline/Transaction/Sales-Out-Photo
		//onTest
		String imageFileLocalPath = "D:\\SalesApp\\Images-Prodshow\\";
		File file = null;
		FileInputStream fis = null;
		EnvProperties env = EnvProperties.getInstance();
	    try {
	    	 String fileName = Utils.isNull(request.getParameter("fileName"));
	    	 String pageName = Utils.isNull(request.getParameter("pageName"));
	    	 if( !Utils.isNull(fileName).equals("")){
		    	 if("prodShow".equalsIgnoreCase(pageName)){
		    		//UAT  "d:/dev_temp/Sales-Out-Photo/";
		    		//PROD "/PENS/ERP/apps/inst/saleonline/Transaction/Sales-Out-Photo/";
		    		 
			    	 imageFileLocalPath= env.getProperty("path.prodshow.photo");
		    	 }else if("ProjectC".equalsIgnoreCase(pageName)){
		    		//UAT  "d:/dev_temp/StockCR-ProjectC-Photo/";
		    		//PROD "/PENS/ERP/apps/inst/saleonline/Transaction/StockCR-ProjectC-Photo/";
		    		 
			    	imageFileLocalPath= env.getProperty("path.projectc.photo");   
		    	 }
		    	  imageFileLocalPath +=fileName; 
		    	  System.out.println("imageFileLocalPath:"+imageFileLocalPath);
		    	  
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
	        logger.error(e.getMessage(),e);
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
