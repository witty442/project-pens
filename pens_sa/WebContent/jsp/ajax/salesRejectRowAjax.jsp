
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetDAO"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetUtils"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
User user = (User)session.getAttribute("user");
long id = Utils.convertStrToLong(request.getParameter("id"),0);
long lineId = Utils.convertStrToLong(request.getParameter("lineId"),0);
String rejectReason = new String(Utils.isNull(request.getParameter("rejectReason")).getBytes("ISO8859_1"), "UTF-8"); ;
String outputText = "";
try{
	//System.out.println("id:"+id+",lineId:"+lineId);
	//System.out.println("rejectReason:"+rejectReason);
	if( 0 != id && 0 != lineId  ){
		boolean r = SalesTargetDAO.salesRejectItem(user, id, lineId,rejectReason); 
		if(r){
		  outputText =SalesTargetConstants.STATUS_REJECT;
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>