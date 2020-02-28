<%@page import="com.isecinc.pens.web.autoorder.AutoOrderDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String storeCode = Utils.isNull(request.getParameter("storeCode"));
String outputText = "0";//pass
try{
	System.out.println("storeCode:"+storeCode);
	if( !"".equals(Utils.isNull(storeCode))){
		/** CanGenStock Onhand Temp Rep  **/
		 boolean can = AutoOrderDAO.isCanGenStockOnhandTempRep(storeCode); 
		 if(can){
		    outputText = "1";//Can Gen
		 }
	}//if
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>