<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
 String fileName =Utils.isNull(request.getParameter("fileName"));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
 <img id="imageDB" 
  src="${pageContext.request.contextPath}/photoServlet?pageName=prodShow&fileName=<%=fileName%>"
  width="50%" height="50%" border="0"/>
</body>
</html>