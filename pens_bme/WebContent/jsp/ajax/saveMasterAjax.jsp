<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.dao.MasterDAO"%>
<%@page import="com.isecinc.pens.bean.Master"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String action = (String) request.getParameter("action");
String referenceCode = (String) request.getParameter("referenceCode");
String interfaceValue = (String) request.getParameter("interfaceValue");
String interfaceDesc = (String) request.getParameter("interfaceDesc");
String pensValue = (String) request.getParameter("pensValue");
String pensDesc = (String) request.getParameter("pensDesc");
String pensDesc2 = (String) request.getParameter("pensDesc2");
String pensDesc3 = (String) request.getParameter("pensDesc3");
String createUser = (String) request.getParameter("createUser");
String sequence = (String) request.getParameter("sequence");
String status = (String) request.getParameter("status");
String outputText = "";
try{

	System.out.println("referenceCode:"+referenceCode); 
	System.out.println("interfaceValue:"+interfaceValue); 
	
	if( !"".equals(Utils.isNull(referenceCode)) ){
		interfaceDesc = new String(interfaceDesc.getBytes("ISO8859_1"), "UTF-8");
		pensValue = new String(pensValue.getBytes("ISO8859_1"), "UTF-8");
		pensDesc = new String(pensDesc.getBytes("ISO8859_1"), "UTF-8");
		pensDesc2 = new String(pensDesc2.getBytes("ISO8859_1"), "UTF-8");
		pensDesc3 = new String(pensDesc3.getBytes("ISO8859_1"), "UTF-8");
		sequence = new String(sequence.getBytes("ISO8859_1"), "UTF-8");
		status = new String(status.getBytes("ISO8859_1"), "UTF-8");
		
		MasterDAO masterDAO = new MasterDAO(); 
		outputText = masterDAO.save(action,referenceCode, interfaceValue, interfaceDesc, pensValue, pensDesc,pensDesc2, createUser,pensDesc3,sequence,status);
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>