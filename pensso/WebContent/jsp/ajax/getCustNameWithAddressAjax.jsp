
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.PopupDAO"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
User user = (User) request.getSession().getAttribute("user");
String custCode = (String) request.getParameter("custCode");
String outputText = "";
try{

	System.out.println("GetCustNameWithAddressAjax custCode:"+custCode);
	
	if( !"".equals(Utils.isNull(custCode)) ){
		//condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
		PopupBean c = new PopupBean();
		c.setCodeSearch(custCode);
		
	    List<PopupBean> result = PopupDAO.searchCustomerMasterAndAddress(c, "equals",user);
		if(result != null && result.size() >0){
			PopupBean r = result.get(0); 
		    outputText = r.getDesc()+"|"+r.getAddress()+"|"+r.getCustomerId();
		}else{
		   outputText ="";
		} 
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>