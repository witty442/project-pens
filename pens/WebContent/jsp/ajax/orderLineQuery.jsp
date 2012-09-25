
<%@page import="com.isecinc.pens.model.MOrderLine"%>
<%@page import="com.isecinc.pens.bean.OrderLine"%>
<%@page import="com.isecinc.pens.model.MOrder"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.Messages"%>
<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="com.isecinc.pens.model.MTrip"%><%@ page language="java"
	contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String memberId = request.getParameter("memberId");
	String orderId = request.getParameter("orderId");
	String selected = request.getParameter("selected");
	System.out.println("select"+selected);
	
	List<OrderLine> lines = new MOrderLine().lookUp(Integer.parseInt(orderId));
	//business for line
	String[] ss = selected.split(",");
	//1.remove paid & selected
	List<OrderLine> rm = new ArrayList<OrderLine>();
	for(OrderLine l:lines){
		//remove paid
		if(l.getPayment().equalsIgnoreCase("Y"))rm.add(l);
		//remove selected
		for(String s : ss){
			if (s.trim().length() > 0) {
				if(l.getId()==Integer.parseInt(s)){rm.add(l);}
			}
		}
	}
	for(OrderLine l:rm){
		lines.remove(l);
	}
	
	MOrder mOrder = new MOrder();
%>

<%@page import="java.text.DecimalFormat"%>

<%@page import="java.util.ArrayList"%>
<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop"/></th>
		<th class="name"><bean:message key="Product.Name" bundle="sysele"/></th>
		<th><bean:message key="Product.UOM" bundle="sysele"/></th>
		<th><bean:message key="Quantity" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Overall" bundle="sysele"/></th>
		<th><bean:message key="Order.ShipmentDate" bundle="sysele"/></th>
		<th><bean:message key="Order.ReceiveDate" bundle="sysele"/></th>
		<th><bean:message key="Member.Time" bundle="sysele"/></th>
		<th>เลือก</th>
	</tr>
	<%for(OrderLine l:lines){ %>
	<tr class="lineO">
		<td align="center"><%=l.getLineNo() %></td>
		<td align="left"><%=l.getProduct().getCode() %>&nbsp;<%=l.getProduct().getName() %></td>
		<td align="center"><%=l.getUom().getCode() %></td>
		<td align="right"><%=new Double(l.getQty()).intValue() %></td>
		<td align="right" ><%=new DecimalFormat("#,##0.00").format(l.getTotalAmount()) %></td>
		<td align="center"><%=l.getShippingDate() %></td>
		<td align="center"><%=l.getRequestDate() %></td>
		<td align="center">
			<%=l.getTripNo() %>
			<textarea name="lineDesc" style="display: none;">ครั้งที่ <%=l.getTripNo()%> <%=l.getProduct().getCode()%> <%=l.getProduct().getName() %> : <%=new Double(l.getQty()).intValue() %> <%=l.getUom().getCode() %> วันที่ส่ง <%=l.getShippingDate() %> วันที่รับ <%=l.getRequestDate() %></textarea>
			<input type="hidden" name="lineAmount" value="<%=new DecimalFormat("###0.00").format(l.getTotalAmount()) %>">
			<input type="hidden" name="lineOrderId" value="<%=l.getOrderId() %>">
			<input type="hidden" name="lineOrderNo" value="<%=mOrder.find(String.valueOf(l.getOrderId())).getOrderNo() %>">
		</td>
		<td align="center"><input type="checkbox" name="chkLine" value="<%=l.getId() %>"></td>
	</tr>
	<%} %>
</table>
<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td class="footer">&nbsp;</td>
	</tr>
</table>