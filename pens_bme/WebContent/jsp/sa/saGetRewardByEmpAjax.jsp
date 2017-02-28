
<%@page import="com.isecinc.pens.dao.SATranDAO"%>
<%@page import="com.isecinc.pens.bean.SATranBean"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String empId = Utils.isNull(request.getParameter("empId"));
String typeInvoice = Utils.isNull(request.getParameter("typeInvoice"));
String checkStockDate = Utils.isNull(request.getParameter("checkStockDate"));
System.out.println("empId:"+empId+",typeInvoice:"+typeInvoice+",checkStockDate:"+checkStockDate);
String outputText = "";
try{
	//System.out.println("code:"+code);
	if( !"".equals(Utils.isNull(empId)) ){
		SATranBean bean = SATranDAO.getRewardByEmp(empId, typeInvoice, checkStockDate);
		if(bean != null ){ 
			if( !Utils.isNull(bean.getPayDate()).equals("")){
			   outputText = bean.getAmt()+"|"+bean.getPayDate();  
			}
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>