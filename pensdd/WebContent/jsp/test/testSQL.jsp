<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.isecinc.pens.inf.helper.ConvertUtils,com.isecinc.pens.inf.helper.Utils" %>
<%@ page import="java.io.*"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Excute SQL</title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
</head>
<%
String output1 = "";
String sql1 = Utils.isNull(request.getParameter("textSQL1"));
String output2 = "";
String sql2 = Utils.isNull(request.getParameter("textSQL2"));

if( !sql1.equals("")){
	 if(request.getParameter("EXECUTE") != null){
		 System.out.println("Execute");
		 if( !sql1.equals(""))
	        output1 =  Utils.excUpdate(sql1);
	 }else{
		 System.out.println("Query");
		 output1 =  Utils.excQuery(sql1);
	 }
 }

  if( !sql2.equals("")){
    output2 =  Utils.excQuery(sql2);
  }
 
  System.out.println("ActionXX1:"+request.getParameter("EXECUTE"));
 System.out.println("ActionXX2:"+request.getParameter("WriteFile"));
  
 if( "BackUpDB".equals(request.getParameter("BackUpDB"))){
	  try {   
	     // com.isecinc.pens.db.backup.DBBackUpManager.process(request);
	  } catch(Exception e) {
	     out.println(e.getMessage());
	  }
  }
%>
<body>
<form name="form1" action="testSQL.jsp" >

  <center>
	<INPUT TYPE="SUBMIT" name ="QUERY" VALUE="Query"> 
	<INPUT TYPE="SUBMIT" name ="EXECUTE"  VALUE="Execute">
	<INPUT TYPE="SUBMIT" name ="BackUpDB"  VALUE="BackUpDB">
	 <BR>
	 Please enter your text SQL 1:
	  <BR>
	  <TEXTAREA NAME="textSQL1" ROWS="7" style=" width : 900px;"><%=sql1 %></TEXTAREA>
	  <BR>
	  <BR>
	   -- Result SQL 1--
	  <BR>
	  <%out.println(output1);%>
	  <br>
	  <TEXTAREA NAME="textSQL2" ROWS="7" style=" width : 900px;"><%=sql2 %></TEXTAREA>
	  <BR>
	   -- Result SQL 2--
	  <BR>
	  <%out.println(output2);%>
  </center>
  
</form>
</body>
</html>