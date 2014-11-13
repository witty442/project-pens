<%@page import="net.sf.json.JSONObject"%>
<%@ page language="java" contentType="text/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.isecinc.pens.web.sales.bean.ProductCatalog"%>
<%@page import="com.isecinc.pens.web.sales.bean.Basket"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="net.sf.json.JSONArray"%>
<%
User user = (User) request.getSession().getAttribute("user");
boolean isVanSales = User.VAN.equalsIgnoreCase(user.getRole().getKey());

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH"));
String today = sdf.format(new Date());

//wit edit 29/07/2554   Case VanSales shippingDate = requestDate
Calendar cld = Calendar.getInstance();
if( !"VAN".equals(user.getType()) ){
   cld.add(Calendar.DAY_OF_MONTH,3); // +3 Day
}

String reqDate = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH")).format(cld.getTime());

String custId = request.getParameter("custId");
Basket basket = (Basket)session.getAttribute(custId);
if(basket != null ){
	String json = new String(basket.getJSON().toString());
	
	//System.out.println(json);
	
	session.removeAttribute(custId);
%>
<%=json%>
<% } %>