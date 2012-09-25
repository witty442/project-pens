<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%
String cCode = (String) request.getParameter("cCode");
String uId = (String) request.getParameter("uId");
String id = request.getParameter("id");
String whereCause = "";
String cId = "";
String cName = "";
Customer[] results = null;

try{
	if(cCode != null && !cCode.equals("")){
		whereCause += " AND CODE = '" + cCode + "'";
		whereCause += " AND ISACTIVE = 'Y'";
		whereCause += " AND USER_ID = " + uId;
		if(id!=null)
			whereCause += " AND CUSTOMER_ID <> " + id;
		
		results = new MCustomer().search(whereCause);
		if(results != null){ // A-neak.t 27/12/2010
			for(Customer c : results){
				cId = String.valueOf(c.getId());
				cName = c.getName() + " " + c.getName2();
			}
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=cId%>||<%=cName%>