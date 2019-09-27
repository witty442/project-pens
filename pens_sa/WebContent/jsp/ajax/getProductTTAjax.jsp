
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetTTUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetBean"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String priceListId = Utils.isNull(request.getParameter("priceListId"));
String period = Utils.isNull(request.getParameter("period"));
String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
String salesZone = Utils.isNull(request.getParameter("salesZone"));
String itemCode = Utils.isNull(request.getParameter("itemCode"));
String brand = Utils.isNull(request.getParameter("brand"));
System.out.println("Get Product Ajax");
System.out.println("brand:"+brand+",priceListId:"+priceListId+",period:"+period+",custCatNo:"+custCatNo+",salesZone:"+salesZone+",itemCode:"+itemCode);

String outputText = "";
boolean dup = false;
try{
	//format return: itemCode|itemId|itemName|price|orderAmt12Month|orderAmt3Month
	System.out.println("itemCode:"+itemCode);
	if( !"".equals(Utils.isNull(itemCode)) ){ 
		
		//set session Map for check Duplicate
	     if(session.getAttribute("ITEM_IN_PAGE") == null){
	    	 Map<String,String> map = new HashMap<String,String>();
	    	 map.put(itemCode, itemCode);
	    	 session.setAttribute("ITEM_IN_PAGE",map);
	     }else{
	    	 Map<String,String> map = (Map<String,String>)session.getAttribute("ITEM_IN_PAGE");
	    	 if(map.get(itemCode) != null){
	    		 dup = true;
	    	 }else{
	    		//no dup set
	    		 map.put(itemCode, itemCode);
	    		 session.setAttribute("ITEM_IN_PAGE",map);
	    	 } 
	     }
		
		if(dup==false){
		   SalesTargetBean p = SalesTargetTTUtils.getProduct(priceListId, period, custCatNo, salesZone, itemCode,brand);
		   if(p != null){
		      outputText = p.getItemCode()+"|"+p.getItemId()+"|"+p.getItemName()+"|"+p.getPrice()+"|"+p.getOrderAmt12Month()+"|"+p.getOrderAmt3Month();
		    
		      //System.out.println("outputText:"+outputText);
		  }
		}else{
			outputText ="DUPLICATE";
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>