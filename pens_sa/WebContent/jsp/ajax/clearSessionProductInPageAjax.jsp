
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%
String itemCodeArr = Utils.isNull(request.getParameter("itemCodeArr"));
String itemCode = "";
String returnText="TEMP";
try{
	//format return: itemCode|itemId|itemName|price|orderAmt12Month|orderAmt3Month
	System.out.println("ClearSessionProductInPageAjax itemCodeArr:"+itemCodeArr);
	if( !"".equals(Utils.isNull(itemCodeArr)) ){ 
		//set session Map for check Duplicate
	     if(session.getAttribute("ITEM_IN_PAGE") != null){
	    	 Map<String,String> map = (HashMap<String,String>)session.getAttribute("ITEM_IN_PAGE");
	    	 String[] itemCodeArrTemp = itemCodeArr.split("\\|");
	    	 if(itemCodeArrTemp != null && itemCodeArrTemp.length>0){
	    		for(int i=0;i<itemCodeArrTemp.length;i++){
	    		   itemCode = itemCodeArrTemp[i];
	    		   if(!Utils.isNull(itemCode).equals("")){
	    	          map.remove(itemCode);
	    		   }
	    		}
	    	 }
	    	 session.setAttribute("ITEM_IN_PAGE",map);
	     }
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=returnText %>
