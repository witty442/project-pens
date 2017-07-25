<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="com.isecinc.pens.inf.helper.FileUtil"%>
<%@page import="com.isecinc.pens.web.customer.CustomerForm"%>
<%@ page import="java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title>Show image </title>
<head></head>
<%
try{
	  String customerId = Utils.isNull(request.getParameter("customerId"));
	  Customer c = null;
	  if(!"".equals(customerId)){
  	     String imageFileLocalPath=  "";//(String)session.getAttribute("imageFileName");  
  	     c = new MCustomer().getImageFileName(customerId);
	  }
%>
<body>
  <div id="imageDisp">
   <b>รูปภาพร้าน <%=Utils.isNull(c.getName())+" "+Utils.isNull(c.getName2()) %></b>
  </div>
  <div id="imageDisp">
   <img id="imageDB" src="${pageContext.request.contextPath }/photoServlet?customerId=<%=customerId %>" border="0"/>
 </div>
</body>
<%
}catch(Exception e){
	e.printStackTrace();
}
%>
</html>