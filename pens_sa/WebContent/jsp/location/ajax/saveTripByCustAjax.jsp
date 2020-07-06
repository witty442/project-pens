
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.DBConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.location.LocationBean"%>
<%@page import="com.isecinc.pens.web.location.TripAction"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
Connection conn = null;
User user = (User) request.getSession().getAttribute("user");
String message = "";
try{
	conn = DBConnection.getInstance().getConnectionApps();
	conn.setAutoCommit(false);
	
	String customerCode = Utils.isNull(request.getParameter("customerCode"));
	String customerType = Utils.isNull(request.getParameter("customerType"));
	String tripDay = Utils.isNull(request.getParameter("tripDay"));
	String tripDay2 = Utils.isNull(request.getParameter("tripDay2"));
	String tripDay3 = Utils.isNull(request.getParameter("tripDay3"));
	
	//Get Old Data for check
	LocationBean bean = new LocationBean();
	bean.setCustomerCode(customerCode);
	LocationBean beanCheck = TripAction.searchCustomerTripDetail(conn, bean);
	
	//set new edit trip
	bean = new LocationBean();
	bean.setCustomerCode(customerCode);
	bean.setTripDay(tripDay);
	bean.setTripDay2(tripDay2);
	bean.setTripDay3(tripDay3);
	bean.setCustomerType(customerType);
	
	bean.setTripDayDB(beanCheck.getTripDayDB());
	bean.setTripDayDB2(beanCheck.getTripDayDB2());
	bean.setTripDayDB3(beanCheck.getTripDayDB3());
	bean.setCustomerTypeDB(beanCheck.getCustomerTypeDB());
	
	//check db data to input screen fro update 
	/* if( Utils.isNull(bean.getTripDay()).equals("")){
		bean.setTripDay(beanCheck.getTripDayDB());
	}
	if( Utils.isNull(bean.getTripDay2()).equals("")){
		bean.setTripDay2(beanCheck.getTripDayDB2());
	}
	if( Utils.isNull(bean.getTripDay3()).equals("")){
		bean.setTripDay3(beanCheck.getTripDayDB3());
	} */
	
	bean.setCreateUser(user.getUserName());
	bean.setCustAccountId(beanCheck.getCustAccountId());
	bean.setPartySiteId(beanCheck.getPartySiteId());
	
	//Update Trip Customer ,CustomerType
	String msg = TripAction.updateCustTrip(bean);

	//search refresh to display
	if( !"".equals(customerCode)){
	   bean = TripAction.searchCustomerTripDetail(conn, bean);
	}
	if(msg.startsWith("success")){
	   message ="บันทึกข้อมูลเรียบร้อย";
	   conn.commit();
	}else{
	   message ="ไม่สามารถบันทึกข้อมูลเรียบร้อย \n"+msg;
	   conn.rollback();
	}

}catch(Exception e){ 
	conn.rollback();
	e.printStackTrace();
	message = "ERROR "+e.getMessage();
}finally{
	if(conn != null){
		conn.close();conn =null;
	}
}
%>
<%=message%>