<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.Product"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%

String pCode = null;
String pID = "";
String pName = "";
String desc="";
String code="";

if(request.getParameter("pCode")!=null)
	pCode = request.getParameter("pCode");

String whereCause = "";
Product[] resutls = null;

try{
	
	if(pCode != null && pCode.length()>0){
		whereCause += " AND CODE LIKE '%" + pCode + "%'";
		
		resutls = new MProduct().search(whereCause);
		for(Product p : resutls){
			pID = String.valueOf(p.getId());
			pName = p.getName();
			desc = p.getDescription();
			code = p.getCode();
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=pID%>||<%=pName%>||<%=desc%>||<%=code %>