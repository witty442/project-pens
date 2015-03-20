<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.isecinc.pens.inf.helper.ConvertUtils,com.isecinc.pens.inf.helper.Utils" %>
<%@ page import="java.io.*"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Test DB ,FTP Connection</title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
</head>
<%
String output = "";
if( "TestDBCon".equals(request.getParameter("TestDBCon"))){
	  try {   
		  output = test.TestALL.testDBCon();
	  } catch(Exception e) {
	     out.println(e.getMessage());
	  }
}else if( "TestFTPCon".equals(request.getParameter("TestFTPCon"))){
	 try {   
		  output = test.TestALL.testFTPCon();
	  } catch(Exception e) {
	     out.println(e.getMessage());
	  }
}

%>
<body>
<form name="form1" action="testConn.jsp" >

  <center>
	<INPUT TYPE="SUBMIT" name ="TestDBCon" VALUE="TestDBCon"> 
	<INPUT TYPE="SUBMIT" name ="TestFTPCon"  VALUE="TestFTPCon">
	 <BR>
	 Result:
	  <BR>
	  <TEXTAREA NAME="textSQL1" ROWS="5" style=" width : 250px;"><%=output%></TEXTAREA>
	  <BR>
  </center>
  
</form>
</body>
</html>