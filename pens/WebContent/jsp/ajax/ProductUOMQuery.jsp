<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%><%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%@page import="util.DBCPConnectionProvider"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.bean.UOM"%>
<%
String pID = (String)request.getParameter("pId");

Connection conn = null;
Statement stmt = null;
ResultSet rst = null;
List<UOM> uoms = new ArrayList<UOM>();

try{
	
	if(pID != null && pID.length() > 0){
		String sql = " select p.uom_id, u.code "+
			" from m_product p, m_uom u "+
			" where p.uom_id = u.uom_id "+
			" and p.product_id = '" + pID +"' ";
		conn = new DBCPConnectionProvider().getConnection(conn);
		stmt = conn.createStatement();
		rst = stmt.executeQuery(sql);
		while(rst.next()){
			uoms.add(new UOM(rst.getString("uom_id"),"",rst.getString("code")));
		}
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{
		rst.close();
	}catch(Exception e2){}
	try{
		stmt.close();
	}catch(Exception e2){}
	try{
		conn.close();
	}catch(Exception e2){}
}
%>
<%for(UOM u : uoms){ %>
<option value="<%=u.getId()%>"><%=u.getName()%></option>
<%}%>