
<%@page import="com.isecinc.pens.bean.SAEmpBean"%>
<%@page import="com.isecinc.pens.dao.SAEmpDAO"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String empId = Utils.isNull(request.getParameter("empId"));
System.out.println("empId:"+empId);
String outputText = "";
try{
	//System.out.println("code:"+code);
	if( !"".equals(Utils.isNull(empId)) ){
		SAEmpBean bean = SAEmpDAO.getEmp(empId);
		if(bean != null ){
		    outputText = bean.getName()+"|"+bean.getSurName()+"|"+bean.getBranch()+"|"+bean.getGroupStore();
		}else{
		    outputText ="";
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>