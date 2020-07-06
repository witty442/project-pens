<%@page import="util.MonitorSales"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Check Version Sales App</title>
</head>
<body>
 <% 
 
 if(request.getParameter("GetIt") != null){
   MonitorSales.getAllSalesAppVersion(); 
   
   out.println("You got it D:/SalesApp/SalesAppVersionAll.csv");
 }
 
 %>
 
 <form name="form1" post="CheckVersionSalesApp.jsp" >
   <input type="submit" value="GetIt" name="GetIt"/>
 </form>
</body>
</html>