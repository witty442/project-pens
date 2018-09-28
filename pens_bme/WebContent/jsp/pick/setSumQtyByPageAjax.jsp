<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%


String pageNumber = Utils.isNull(request.getParameter("pageNumber"));
String curPageQty = Utils.isNull(request.getParameter("curPageQty"));
String totalQtyNotInCurPage = Utils.isNull(request.getParameter("totalQtyNotInCurPage"));
if(totalQtyNotInCurPage.equals("")){
	totalQtyNotInCurPage = "0";
}
System.out.println("****************************");
System.out.println("pageNumber["+pageNumber+"]");
System.out.println("curPageQty["+curPageQty+"]");
System.out.println("totalQtyNotInCurPage["+totalQtyNotInCurPage+"]");

int totalAllQty = Integer.parseInt(totalQtyNotInCurPage) + Integer.parseInt(curPageQty);
session.setAttribute("totalAllQty", String.valueOf(totalAllQty));

System.out.println("totalAllQty["+totalAllQty+"]");


//Refresh curPageQty case Edit on Screen Map key curNumber
if(session.getAttribute("curPageQtyMap") !=null){
	Map<String,String> curPageQtyMap = (Map)session.getAttribute("curPageQtyMap");
	curPageQtyMap.put(pageNumber, curPageQty);
	
	session.setAttribute("curPageQtyMap",curPageQtyMap);
	
	//System.out.println("totalCurPageQty pageNumber["+pageNumber+"]=["+curPageQtyMap.get(pageNumber)+"]");

}else{
	Map<String,String> curPageQtyMap = new HashMap<String,String>();
	curPageQtyMap.put(pageNumber, curPageQty);
	session.setAttribute("curPageQtyMap",curPageQtyMap);
	
	//System.out.println("totalCurPageQty pageNumber["+pageNumber+"]=["+curPageQtyMap.get(pageNumber)+"]");

}

System.out.println("****************************");

%>