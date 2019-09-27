package com.isecinc.pens.web.batchtask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.isecinc.pens.report.salesanalyst.helper.EnvProperties;
import com.isecinc.pens.report.salesanalyst.helper.FileUtil;
import com.pens.util.Utils;

@SuppressWarnings( { "static-access", "serial", "deprecation" })
public class BatchTaskDownloadServlet extends HttpServlet {

	Logger logger = Logger.getLogger("PENS");

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		String pathFile = "";
		EnvProperties env = EnvProperties.getInstance();
	    try {
	    	 pathFile = env.getProperty("path.temp");
	    	 String fileName = Utils.isNull(request.getParameter("fileName"));
	    	 if( !Utils.isNull(fileName).equals("")){
	    		 
	    		 pathFile +=fileName; 
		    	 logger.debug("pathFile:"+pathFile);
		    	  
	    		//read file from temp file
				 byte[] bytes = FileUtil.readFileToByte(new FileInputStream(pathFile));
				 
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				response.setContentType("application/pdf");
				
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.write(bytes, 0, bytes.length);
				servletOutputStream.flush();
				servletOutputStream.close();
	    	 }
	       
	    } catch (Exception e) {
	        e.printStackTrace();
	        logger.error(e.getMessage(),e);
	    }finally{
	    	
	    }
	}
	
	
}
