
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%><%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%
String shipDate = (String) request.getParameter("shipdate");
String reqDate = (String) request.getParameter("reqdate");
String target = request.getParameter("target");

Calendar cldS = Calendar.getInstance(new Locale("th","TH"));
cldS.setTime(new SimpleDateFormat("dd/mm/yyyy",new Locale("th","TH")).parse(shipDate));

Calendar cldR = Calendar.getInstance(new Locale("th","TH"));
cldR.setTime(new SimpleDateFormat("dd/mm/yyyy",new Locale("th","TH")).parse(reqDate));

if(target.equalsIgnoreCase("shipdate")){
	//Calculate from shipdate
	cldR = cldS;
	cldR.add(Calendar.DAY_OF_MONTH,3);
	reqDate = new SimpleDateFormat("dd/mm/yyyy",new Locale("th","TH")).format(cldR.getTime());
}else{
	//Calculate from reqdate 
	cldS = cldR;
	cldS.add(Calendar.DAY_OF_MONTH,-3);
	shipDate = new SimpleDateFormat("dd/mm/yyyy",new Locale("th","TH")).format(cldS.getTime());
}

%>
<%=shipDate+"||"+reqDate%>