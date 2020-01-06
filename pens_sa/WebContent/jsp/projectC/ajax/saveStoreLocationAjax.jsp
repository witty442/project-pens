<%@page import="com.isecinc.pens.web.projectc.ProjectCBean"%>
<%@page import="com.isecinc.pens.web.projectc.ProjectCDAO"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String storeCode = Utils.isNull(request.getParameter("storeCode"));
String branchId = Utils.isNull(request.getParameter("branchId"));
String storeLat = Utils.isNull(request.getParameter("storeLat"));
String storeLong = Utils.isNull(request.getParameter("storeLong"));
System.out.println("storeCode:"+storeCode);
System.out.println("branchId:"+branchId);
System.out.println("location:"+storeLat+","+storeLong);
String msg = "";
try{
	if( !"".equals(storeCode) && !"".equals(branchId) && !"".equals(storeLat) && !"".equals(storeLong)){
	  ProjectCBean bean = new ProjectCBean();
	  bean.setStoreCode(storeCode);
	  bean.setBranchId(branchId);
	  bean.setStoreLat(storeLat);
	  bean.setStoreLong(storeLong);
	  ProjectCDAO.updateLocationBranch(bean);
	  msg ="Save Location Success";
	}
}catch(Exception e){ 
	e.printStackTrace();
	msg ="!!! Cannot Save Location";
}finally{
}
%>
<%=msg%>