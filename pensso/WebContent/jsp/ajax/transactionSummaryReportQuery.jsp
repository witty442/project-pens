<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
User user = (User) session.getAttribute("user");
String dateFrom = (String)request.getParameter("datefrom");
String dateTo = (String)request.getParameter("dateto");
String custId = (String)request.getParameter("custId");
String productCodeFrom = (String)request.getParameter("productCodeFrom");
String productCodeTo = (String)request.getParameter("productCodeTo");

TransactionSummary ts = new TransactionSummary();
ts.setOrderType(user.getOrderType().getKey());
ts.setUserId(user.getId());
ts.setDateFrom(dateFrom);
ts.setDateTo(dateTo);
ts.setCustomerId(custId);
ts.setProductCodeFrom(productCodeFrom);
ts.setProductCodeTo(productCodeTo);

List<TransactionSummaryCustomer> ords = new ArrayList<TransactionSummaryCustomer>();

String error = "";
try{
	ords = new TransactionSummaryProcess().getSummary(ts);
	if(ords.size()==0)error = InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc();
}catch(Exception e){
	e.printStackTrace();
	error = e.toString();
}finally{
	
}

int i=0;
%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="com.isecinc.core.bean.Messages"%>
<%@page import="com.isecinc.pens.bean.TransactionSummary"%>
<%@page import="com.isecinc.pens.bean.TransactionSummaryCustomer"%>
<%if(ords.size() > 0){%>

<%@page import="com.isecinc.pens.process.TransactionSummaryProcess"%><div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
<bean:message key="RecordsFound" bundle="sysprop" />&nbsp;<span class="searchResult"><%= ords.size()%></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
</div>
<!-- ORDER -->
<%if(ords.size()>0){ %>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
<%for(TransactionSummaryCustomer c : ords){ %>
	<tr>
		<td colspan="5" class="footer" align="left">
			<img border=0 src="${pageContext.request.contextPath}/icons/user_active.gif">
			<b>
				<font color="red"><%=c.getCustomer().getCode() %></font>&nbsp;
				<%=c.getCustomer().getName() %>&nbsp;&nbsp;
				<bean:message key="Address" bundle="sysele" />
				<%=c.getAddress().getLineString() %>
			</b>
		</td>
		<td class="footerNoAlign" align="right">
			<span class="searchResult"><%=c.getSummaries().size() %></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
		</td>
	</tr>
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop" /></th>
		<th class="date"><bean:message key="TransactionDate" bundle="sysele" /></th>
		<th><bean:message key="Product" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Stocked" bundle="sysele" /></th>
		<th class="costprice"><bean:message key="Sold" bundle="sysele" /></th>
		<th class="costprice"><bean:message key="Promotioned" bundle="sysele" /></th>
	</tr>
	<%i=1; %>
	<%for(TransactionSummary o : c.getSummaries()){ %>
	<tr class="lineO">
		<td><%=i++ %></td>
		<td><%=o.getTransactionDate()%></td>
		<td align="left"><%=o.getTransactionProduct()%></td>
		<td align="center"><%=o.getStockMainUOM()%>/<%=o.getStockSubUOM()%></td>
		<td align="center"><%=o.getOrderMainUOM()%>/<%=o.getOrderSubUOM()%></td>
		<td align="center"><%=o.getPromoMainUOM()%>/<%=o.getPromoSubUOM()%></td>
	</tr>
	<%} %>
<%} %>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td class="footer">&nbsp;</td>
	</tr>	
</table>

<%} %>	
<script language="javascript">
	$('#msg').html('');
</script>
<%}else{ %>
<script language="javascript">
	$('#msg').html('<%=error%>');
</script>
<%} %>
