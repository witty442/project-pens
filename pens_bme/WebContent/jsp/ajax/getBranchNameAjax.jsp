
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.SummaryDAO"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.bean.Master"%>
<%@page import="java.sql.Connection"%>

<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%

String custCode = Utils.isNull(request.getParameter("custCode"));
String storeType = Utils.isNull(request.getParameter("storeType"));
String outputText = "";
try{
	System.out.println("custCode:"+custCode+",storeType:"+storeType);
	if( !"".equals(Utils.isNull(custCode)) ){
		if( "ALL".equalsIgnoreCase(Utils.isNull(custCode)) ){
			 outputText = "ALL Branch";
		}else{
			//condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
			PopupForm c = new PopupForm();
			c.setCodeSearch(custCode);
			
		    List<PopupForm> dataList = SummaryDAO.searchBranchMaster(c, storeType, "equals");
		    if(dataList != null && dataList.size()>0){
			   outputText = Utils.isNull(dataList.get(0).getBranchName());
		    }
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>