<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.Product"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%
User user = (User) request.getSession().getAttribute("user");
String productCode = Utils.isNull(request.getParameter("productCode"));
Product p = null;
boolean dup = false;
String outputText = "";
try{
	
	if(productCode != null && productCode.length()>0){
		//set session Map for check Duplicate
	     if(session.getAttribute("ITEM_IN_PAGE") == null){
	    	 Map<String,String> map = new HashMap<String,String>();
	    	 map.put(productCode, productCode);
	    	 session.setAttribute("ITEM_IN_PAGE",map);
	     }else{
	    	 Map<String,String> map = (Map<String,String>)session.getAttribute("ITEM_IN_PAGE");
	    	 if(map.get(productCode) != null){
	    		 dup = true;
	    	 }else{
	    		//no dup set
	    		 map.put(productCode, productCode);
	    		 session.setAttribute("ITEM_IN_PAGE",map);
	    	 } 
	     }
		//if(dup==false){
		   p = new MProduct().getStockProduct(productCode,user);
		   outputText = p.getId()+"|"+p.getName()+"|"+p.getUom1()+"/"+p.getUom2()+"|"+p.getConversionRate();
		//}else{
		//	outputText ="DUPLICATE";
		//}
	}
}catch(Exception e){
	e.printStackTrace();
}
if(p != null)
%>
<%=outputText%>