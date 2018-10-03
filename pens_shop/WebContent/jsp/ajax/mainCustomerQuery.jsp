<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%
	User user = (User) session.getAttribute("user");
	String customerCode = request.getParameter("custcode");
	Customer c = null;
	String isMain = (String) request.getParameter("main");
	String id = request.getParameter("id");
	try {
		String whereCause = " AND ISACTIVE = 'Y' ";
		if (isMain.equals("Y")) {
			whereCause += " AND USER_ID = " + user.getId();
		}
		
		if(id!=null)
			whereCause += " AND CUSTOMER_ID <> " + id;
		
		whereCause += " AND CUSTOMER_TYPE = '"
				+ user.getCustomerType().getKey() + "' ";
		whereCause += " AND CODE = '" + customerCode + "' ";

		Customer[] cs = new MCustomer().search(whereCause);
		if (cs != null) {
			c = cs[0];
		}
	} catch (Exception e) {
		e.printStackTrace();
	} 
%>
<%
	if (c != null) {
%>
<%=c.getId()%>::<%=c.getCode()%>::<%=(c.getName() + " " + c.getName2()).trim()%>
<%
	}
%>