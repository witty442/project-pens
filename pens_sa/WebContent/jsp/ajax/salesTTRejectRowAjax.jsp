
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetTTDAO"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
User user = (User)session.getAttribute("user");
String period = Utils.isNull(request.getParameter("period"));
String salesZone = Utils.isNull(request.getParameter("salesZone"));
String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
String brand = Utils.isNull(request.getParameter("brand"));
String startDate = Utils.isNull(request.getParameter("startDate"));
String rejectReason = new String(Utils.isNull(request.getParameter("rejectReason")).getBytes("ISO8859_1"), "UTF-8"); ;
String outputText = "";
try{
	//System.out.println("id:"+id+",lineId:"+lineId);
	//System.out.println("rejectReason:"+rejectReason);
	if(  !Utils.isNull(period).equals("") && !Utils.isNull(salesZone).equals("") && !Utils.isNull(custCatNo).equals("")){
		boolean r = SalesTargetTTDAO.salesRejectItem_TT(user, custCatNo, salesZone, brand, period, startDate, rejectReason);
		if(r){
		  outputText =SalesTargetConstants.STATUS_REJECT;
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>