<!--
Product Popup for Sales Order 
 -->
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%
List<References> payment= InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
List<References> banks= InitialReferences.getReferenes().get(InitialReferences.BANK);

String row = request.getParameter("row");
String rowseed = request.getParameter("rowseed");

User user = (User) session.getAttribute("user");

%>

<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="java.text.DecimalFormat"%>


<%@page import="com.isecinc.pens.bean.User"%><td align="center"><%=row %></td>
<td align="center"><input type="checkbox" name="recpbyids"/></td>
<td align="center">
	<select name="bys.paymentMethod" onchange="change_payment();">
		<%for(References r : payment){ %>
		<option value="<%=r.getKey() %>"><%=r.getName() %></option>
		<%} %>
	</select>
</td>
<td align="center" style="display: none;">
	<input type="text" name="bys.receiptAmount" size="10" maxlength="20" readonly="readonly" class="disableText" style="text-align: right;" onkeydown="return isNum0to9andpoint(this, event);"/>
</td>
<td align="left">
	<select name="bys.bank">
		<option value=""></option>
		<%for(References r : banks){ %>
		<option value="<%=r.getKey() %>"><%=r.getName() %></option>
		<%} %>
	</select>
</td>
<td align="center"><input type="text" name="bys.chequeNo" size="20" maxlength="20"/></td>
<td align="center"><input type="text" name="bys.chequeDate" id="chqDate_<%=rowseed %>" size="15" readonly="readonly" maxlength="10"/></td>
<td align="center">
	<select name="bys.creditCardType">
		<option value=""></option>
		<option value="VISA">VISA</option>
		<option value="MASTERCARD">Master Card</option>
	</select>
</td>
<td align="center">
	<input type="text" name="bys.paidAmount" size="10" maxlength="20" style="text-align: right;" onkeydown="return isNum0to9andpoint(this, event);"/>
</td>
<%if(!user.getType().equalsIgnoreCase(User.DD)){ %>
<td align="center">
	<input type="checkbox" name="bys.writeOff" value="Y"/>
</td>
<%}%>