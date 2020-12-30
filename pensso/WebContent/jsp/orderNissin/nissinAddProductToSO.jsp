<%@page import="com.isecinc.pens.web.ordernissin.OrderNissinBasket"%>
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
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH"));
String today = sdf.format(new Date());

String custId = request.getParameter("custId");
//System.out.println("nissinAddProductToSO custId["+custId+"]session["+session.getAttribute(custId)+"]");

OrderNissinBasket basket = (OrderNissinBasket)session.getAttribute(custId);
//System.out.println("nissinAddProductToSO custId["+custId+"]session["+session.getAttribute(custId)+"]basket["+basket+"]");

if(basket != null ){
	String json = new String(basket.getJSON().toString());

	//System.out.println("json:"+json);
	session.removeAttribute(custId);
%>
<%=json%>
<% } %>