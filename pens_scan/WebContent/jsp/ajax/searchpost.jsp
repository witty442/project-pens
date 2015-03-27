<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
String condType = request.getParameter("condType");
String condNo = request.getParameter("condNo");
System.out.println("tutiya condNo2.="+condNo);
String code1="";

if (condNo !=null) {
//--------------------
if (condNo.equalsIgnoreCase("1")) {
	    code1 = request.getParameter("condCode");  
	    request.getSession().setAttribute("condition1value", code1);
	    System.out.println("tutiya code2.="+code1);
	    
	     
	    request.getSession().setAttribute("condition1type", condType);
	    System.out.println("tutiya type2.="+condType);
	 }
//-----------------------
}
%>
Hello....<%=code1%>:<%=condType%>
</body>
</html>