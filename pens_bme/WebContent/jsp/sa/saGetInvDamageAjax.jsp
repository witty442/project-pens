
<%@page import="com.isecinc.pens.dao.SADamageDAO"%>
<%@page import="com.isecinc.pens.bean.SADamageBean"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String invRefWal = Utils.isNull(request.getParameter("invRefWal"));
System.out.println("invRefWal:"+invRefWal);
String outputText = "";
try{
	//System.out.println("code:"+code);
	if( !"".equals(Utils.isNull(invRefWal)) ){
		String inRefWalRe = SADamageDAO.getInvRefwalInDamageHead(invRefWal);
		if( !Utils.isNull(inRefWalRe).equals("")){
		    outputText =inRefWalRe;
		}else{
		    outputText ="";
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>