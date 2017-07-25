package util;

/**
 * @Title:ReportUtil
 * @Description: utility of jasper report
 * @Author A-neak.t
 * @Version 1.0
 * @CreateDate: 10/11/2010
 * @CurrentVersion 1.0
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MCustomer;

@SuppressWarnings( { "static-access", "serial", "deprecation" })
public class PhotoServlet extends HttpServlet {

	Logger logger = Logger.getLogger("PENS");

	BeanParameter beanParameter = new BeanParameter();
	public String path = beanParameter.getReportPath();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    try {
	    	 String customerId = Utils.isNull(request.getParameter("customerId"));
	    	  
	    	  if(!"".equals(customerId)){
		    	  String imageFileLocalPath=  "";//(String)session.getAttribute("imageFileName");  
		    	  Customer c = new MCustomer().getImageFileName(customerId);
		    	  if(c != null){
		    		  imageFileLocalPath = c.getImageFileName(); 
		    	  }
		    	  
		    	  String typeFile = imageFileLocalPath.substring(imageFileLocalPath.indexOf(".")+1,imageFileLocalPath.length());
		    	  System.out.println("typeOfFile:"+typeFile);
		     
			       // Place a gif file in your web app's root folder for test.
			       File f = new File(imageFileLocalPath);
			       FileInputStream fis = new FileInputStream(f);
			       byte[] imgData = new byte [(int)f.length()];
			       fis.read(imgData);
			       //response.reset();
			       response.setContentType("image/"+typeFile);
	               response.getOutputStream().write(imgData);
	    	  }
	       
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
}
