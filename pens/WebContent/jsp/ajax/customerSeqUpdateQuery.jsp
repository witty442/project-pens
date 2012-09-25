
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.process.CustomerSequenceProcess"%>
<%@page import="com.isecinc.pens.bean.CustomerSequence"%>
<%@page import="java.util.List"%><%@ page language="java"
	contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	List<CustomerSequence> pos = new ArrayList<CustomerSequence>();
	String error = "";
	try {
		pos = new CustomerSequenceProcess().updateSequence();
	} catch (Exception ex) {
		error = ex.toString();
	}
%>
<%if(error.length()>0){%>
<center>
<br>
<font color="red"><b>Process Fail : <%=error%></b></font>
</center>
<%}else{%>
<%if(pos==null){%>
<center>
<br>
<font color="red"><b>ไม่พบข้อมูลผู้ใช้งานในการประมวลผล</b></font>
</center>
<%}else{
	int i=0;
	String className;
%>
<br>
&nbsp;&nbsp;&nbsp;ประมวลผลทั้งหมด <font color="red"><%=pos.size() %></font> รายการ
<table align="center" border="0" cellpadding="0" cellspacing="1" class="result">
	<tr>
		<th>ID</th>
		<th>Territory</th>
		<th>Province</th>
		<th>District</th>
		<th>Current Next</th>
	</tr>
	<%for(CustomerSequence m : pos){ %>
	<%if(i%2==0){className="lineO";}else{className="lineE";} %>
	<tr class="<%=className %>">
		<td align="center"><%=m.getId() %></td>
		<td align="center"><%=m.getTerritory() %></td>
		<td align="center"><%=m.getProvince() %></td>
		<td align="center"><%=m.getDistrict() %></td>
		<td align="right"><%=m.getCurrentNext() %></td>
	</tr>
	<%i++; %>
	<%} %>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="0" class="result">
	<tr>
		<td align="left" class="footer">&nbsp;</td>
	</tr>
</table>
<%} %>
<%} %>
