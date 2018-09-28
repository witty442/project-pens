<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>Show Image</title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">

</script>
<%
String customerId = Utils.isNull(request.getParameter("customerId"));
Customer c = null;
try{
	  if(!"".equals(customerId)){
	     String imageFileLocalPath=  "";//(String)session.getAttribute("imageFileName");  
	     c = new MCustomer().getImageFileName(customerId);
	  }
}catch(Exception e){
	e.printStackTrace();
}
%>
</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
	<div id="imageDisp" align="center">
	   <font size="5"><b>รูปภาพร้าน <%=Utils.isNull(c.getName())+" "+Utils.isNull(c.getName2()) %></b></font>
	  </div>
	  <div id="imageDisp" align="center">
	   <img id="imageDB" src="${pageContext.request.contextPath }/photoCustomerServlet?customerId=<%=customerId %>" border="0"/>
	 </div>
<br/>
</body>
</html>