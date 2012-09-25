<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="util.DBCPConnectionProvider"%>
<%
String mCode = (String) request.getParameter("mCode");
String whereCause = "";
String mId = "";
String mName = "";
Customer[] results = null;
try{
	if(mCode != null && !mCode.equals("")){
		whereCause += " AND CODE = '" + mCode + "'";
		whereCause += " AND ISACTIVE = 'Y'";
		whereCause += " ORDER BY CUSTOMER_ID ";
		results = new MCustomer().search(whereCause);
		for(Customer m : results){
			mId = String.valueOf(m.getId());
			mName = m.getName() + " " + m.getName2();	
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%><%=(mId.trim()+"||"+mName.trim()).trim()%>