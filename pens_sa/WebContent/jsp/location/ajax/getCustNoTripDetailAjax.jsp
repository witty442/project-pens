
<%@page import="com.isecinc.pens.web.location.LocationBean"%>
<%@page import="com.isecinc.pens.web.location.TripAction"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String customerCode = Utils.isNull(request.getParameter("customerCode"));
String salesrepId =  Utils.isNull(request.getParameter("salesrepId"));
String returnMsg = "";
try{
	System.out.println("getCustNoTripDetailAjax customerCode:"+customerCode+",salesrepId:"+salesrepId);
	if( !Utils.isNull(customerCode).equals("")){
		LocationBean o = new LocationBean();
		o.setCustomerCode(customerCode);
		o.setSalesrepId(salesrepId);
		LocationBean bean = TripAction.searchCustomerNoTripDetail(o);
		if(bean != null){
			returnMsg = bean.getCustomerName()+"|"+bean.getTripDay()+"|"+bean.getTripDay2()+"|"+bean.getTripDay3();
		}
		System.out.println("returnMsg:"+returnMsg);
	}

}catch(Exception e){ 
	e.printStackTrace();
}finally{
}
%>
<%=returnMsg%>