<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="com.pens.utils.manual.mergdb.CustMergBean"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.sql.Blob"%>
<%@page import="com.isecinc.pens.inf.helper.FileUtil"%>

<%@ page import="java.io.*"%>

<%

      try{
    	  String customerId = Utils.isNull(request.getParameter("customerId"));
    	  
    	  if(!"".equals(customerId)){
	    	  String imageFileLocalPath=  "";//(String)session.getAttribute("imageFileName");  
	    	  Customer c = new MCustomer().getImageFileName(customerId);
	    	  if(c != null){
	    		  imageFileLocalPath = c.getImageFileName(); 
	    	  }
	    	  
	    	  String typeFile = imageFileLocalPath.substring(imageFileLocalPath.indexOf(".")+1,imageFileLocalPath.length());
	    	 // System.out.println("typeOfFile:"+typeFile);
	     
		       // Place a gif file in your web app's root folder for test.
		       File f = new File(imageFileLocalPath);
		       FileInputStream fis = new FileInputStream(f);
		       byte[] imgData = new byte [(int)f.length()];
		       fis.read(imgData);
		       //response.reset();
		       response.setContentType("image/"+typeFile);
		       //out.clear();
		       OutputStream o = response.getOutputStream();
		       o.write(imgData);
		       o.flush(); 
		       o.close();
		       
		       fis.close();
    	  }
      }catch(Exception e){
    	  e.printStackTrace();
      }
%>