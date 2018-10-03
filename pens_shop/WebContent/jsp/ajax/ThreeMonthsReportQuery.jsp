<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
String customerId = (String)request.getParameter("customerId");
String userId = (String)request.getParameter("userId");
String dateFrom = (String)request.getParameter("datefrom");
String dateTo = (String)request.getParameter("dateto");

Customer customer = new MCustomer().find(customerId);
pageContext.setAttribute("cust",customer,PageContext.PAGE_SCOPE);

List<OrderThreeMonths> trxs = new ArrayList<OrderThreeMonths>();

try{
	trxs = new OrderThreeMonthsProcess().getDetail(customerId,dateFrom,dateTo,Integer.parseInt(userId));
}catch(Exception e){
	e.printStackTrace();
}finally{
	
}
%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.report.salesorder.OrderThreeMonths"%>
<%@page import="com.isecinc.pens.report.salesorder.OrderThreeMonthsProcess"%>
<%int i=0; %>
<%String className=""; %>
<%if(trxs.size()>0){ %>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
&nbsp;&nbsp;<font color="red">${cust.code}</font>&nbsp;${cust.name}
<table align="center" border="0" cellpadding="0" cellspacing="1" class="result">
	<tr>
		<th><bean:message key="Product" bundle="sysele" /></th>
		<th>ÂÍ´à§Ô¹ÃÇÁ</th>
	</tr>
	<%for(OrderThreeMonths m : trxs){ %>
	<%if(i%2==0){className="lineO";}else{className="lineE";} %>
	<tr class="<%=className %>">
		<td align="left">
			&nbsp;<%=(i+1)+"." %>&nbsp;&nbsp;
			<%=m.getProduct().getCode()%>-<%=m.getProduct().getName() %>
		</td>
		<td align="right">
			<%=new DecimalFormat("#,#00.00").format(m.getTotalAmount()) %>&nbsp;
		</td>
	</tr>
	<%i++; %>
	<%} %>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="0" class="result">
	<tr>
		<td align="left" colspan="10" class="footer">&nbsp;</td>
	</tr>
</table>
<%} %>


