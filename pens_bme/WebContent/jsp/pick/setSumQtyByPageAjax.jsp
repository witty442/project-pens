<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String pageNumber = Utils.isNull(request.getParameter("pageNumber"));
String curPageQty = Utils.isNull(request.getParameter("curPageQty"));
String totalQtyNotInCurPage = Utils.isNull(request.getParameter("totalQtyNotInCurPage"));
if(totalQtyNotInCurPage.equals("")){
	totalQtyNotInCurPage = "0";
}
System.out.println("pageNumber["+pageNumber+"]curPageQty["+curPageQty+"]totalQtyNotInCurPage["+totalQtyNotInCurPage+"]");

int totalAllQty = Integer.parseInt(totalQtyNotInCurPage) + Integer.parseInt(curPageQty);
session.setAttribute("totalAllQty", String.valueOf(totalAllQty));

System.out.println("pageNumber["+pageNumber+"]curPageQty["+curPageQty+"]totalAllQty["+totalAllQty+"]");

%>