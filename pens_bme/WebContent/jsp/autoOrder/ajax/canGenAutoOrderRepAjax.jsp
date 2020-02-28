<%@page import="com.isecinc.pens.web.autoorder.AutoOrderDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String storeCode = Utils.isNull(request.getParameter("storeCode"));
String orderDate = Utils.isNull(request.getParameter("orderDate"));
String outputText = "0";//pass
try{
	System.out.println("storeCode:"+storeCode+",orderDate:"+orderDate);
	if( !"".equals(Utils.isNull(storeCode)) && !"".equals(Utils.isNull(orderDate)) ){
	  /** check Lock BME Order **/
		
	  //check other store not confirm (status is null)
		 String storeNotConfirm = AutoOrderDAO.storeOrdeRepNoConfirm(orderDate);
		 if( !Utils.isNull(storeNotConfirm).equals("")){
			 outputText = storeNotConfirm;
		 }else{
			 boolean exits = AutoOrderDAO.isOrderIsGenerated(storeCode,orderDate); 
			 if(exits){
			    outputText = "-1";//Is Generated
			 }else{
				 //sysdate -1 day
				 boolean isGenStockOnhandTmp = AutoOrderDAO.isGeneratedStockOnhandTemp(storeCode);
				 if(!isGenStockOnhandTmp){
					 outputText = "-2";//no gen stock onhand temp
				 }else{
					 //No Lock Bme Order return  -3 
				     boolean accessOrderPage = ControlLockPage.canAccessPage("Order");
					 outputText = accessOrderPage?"-3":"";
				 }
			 }
		 }
		
	}//if
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>