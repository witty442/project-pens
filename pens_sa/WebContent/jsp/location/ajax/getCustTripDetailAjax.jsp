
<%@page import="com.isecinc.pens.web.location.LocationBean"%>
<%@page import="com.isecinc.pens.web.location.TripAction"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String customerCode = Utils.isNull(request.getParameter("customerCode"));
String returnMsg = "";
try{
	System.out.println("getCustTripDetailAjax customerCode:"+customerCode);
	if( !Utils.isNull(customerCode).equals("")){
		LocationBean o = new LocationBean();
		o.setCustomerCode(customerCode);
		LocationBean bean = TripAction.searchCustomerTripDetail(o);
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