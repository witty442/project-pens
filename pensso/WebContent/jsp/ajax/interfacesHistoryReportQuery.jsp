<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
User user = (User) session.getAttribute("user");
String dateOrder = (String)request.getParameter("dateOrder");

InterfaceSummary infs = new InterfaceSummary();
infs.setRecordTime(dateOrder);
infs.setUserId(user.getId());
infs.setOrderType(user.getOrderType().getKey());

List<InterfaceSummary> ords = new ArrayList<InterfaceSummary>();
List<InterfaceSummary> rcps = new ArrayList<InterfaceSummary>();
List<InterfaceSummary> vsts = new ArrayList<InterfaceSummary>();

String error = "";
try{
	InterfaceSummaryProcess pr = new InterfaceSummaryOrder();
	ords = pr.getSummaryInterfaces(infs);
	
	pr = new InterfaceSummaryReceipt();
	rcps = pr.getSummaryInterfaces(infs);
	
	if(!user.getType().equalsIgnoreCase(User.DD))
	{
		pr = new InterfaceSummaryVisit();
		vsts = pr.getSummaryInterfaces(infs);
	}	
	if(ords.size()+rcps.size()+vsts.size()==0)error = InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc();
}catch(Exception e){
	e.printStackTrace();
	error = e.toString();
}finally{
	
}

int i=0;
%>
<%@page import="com.isecinc.pens.process.interfaces.InterfaceSummaryVisit"%>
<%@page import="com.isecinc.pens.process.interfaces.InterfaceSummaryReceipt"%>
<%@page import="com.isecinc.pens.process.interfaces.InterfaceSummaryOrder"%>
<%@page import="com.isecinc.pens.process.interfaces.InterfaceSummaryProcess"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.InterfaceSummary"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%if(ords.size() + rcps.size() + vsts.size() > 0){%>

<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="com.isecinc.core.bean.Messages"%>
<%@page import="java.text.DecimalFormat"%><div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
	<bean:message key="RecordsFound" bundle="sysprop" />&nbsp;<span class="searchResult"><%= ords.size()+ rcps.size()+vsts.size()%></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
</div>
<!-- ORDER -->
<%if(ords.size()>0){ %>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td colspan="8" class="footer" align="left">
			<img border=0 src="${pageContext.request.contextPath}/icons/doc_active.gif">
			<b><bean:message key="SalesOrder" bundle="sysprop" /></b>
		</td>
		<td class="footerNoAlign" align="right">
			<span class="searchResult"><%=ords.size() %></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
		</td>
	</tr>
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop" /></th>
		<th class="code"><bean:message key="DocumentNo" bundle="sysele" /></th>
<!--		<th class="code"><bean:message key="Status" bundle="sysele"/></th>-->
		<th width="120px;"><bean:message key="TransactionDate" bundle="sysele" /></th>
		<th><bean:message key="Customer" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="TotalAmount" bundle="sysele" /></th>
		<th class="status"><bean:message key="Exported" bundle="sysele"/></th>
		<th class="status"><bean:message key="Interfaces" bundle="sysele"/></th>
		<th><bean:message key="Order.No"  bundle="sysele"/></th>
		<th><bean:message key="Bill.No"  bundle="sysele"/></th>
	</tr>
	<%i=1; %>
	<%for(InterfaceSummary o : ords){ %>
	<tr class="lineO">
		<td><%=i++ %></td>
		<td align="left"><%=o.getRecordNo()%></td>
		
		<td><%=o.getRecordTime()%></td>
		<td align="left"><%=o.getCustomer()%></td>
		<td align="right"><%=new DecimalFormat("#,##0.00").format(o.getRecordAmount())%></td>
		<td>
			<%if(o.getExported().equalsIgnoreCase("Y")){ %>
			<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
			<%} %>
		</td>
		<td>
			<%if(o.getInterfaces().equalsIgnoreCase("Y")){ %>
			<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
			<%} %>
		</td>
		<td><%=o.getRecordReference1() %></td>
		<td><%=o.getRecordReference2() %></td>
	</tr>
	<%} %>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td class="footer">&nbsp;</td>
	</tr>	
</table>
<%} %>
<!-- RECEIPT -->
<%if(rcps.size()>0){ %>
<br>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td colspan="6" class="footer" align="left">
			<img border=0 src="${pageContext.request.contextPath}/icons/doc_active.gif">
			<b><bean:message key="Receipt" bundle="sysprop" /></b>
		</td>
		<td class="footerNoAlign" align="right">
			<span class="searchResult"><%=rcps.size() %></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
		</td>
	</tr>
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop"/></th>
		<th class="code"><bean:message key="Receipt.No" bundle="sysele"/></th>
<!--		<th class="code"><bean:message key="Status" bundle="sysele"/></th>-->
		<th width="120px;"><bean:message key="TransactionDate" bundle="sysele"/></th>
		<th><bean:message key="Customer" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Order.Payment" bundle="sysele"/></th>
		<th class="status"><bean:message key="Exported"  bundle="sysele"/></th>
		<th class="status"><bean:message key="Interfaces" bundle="sysele"/></th>
	</tr>
	<%i=1; %>
	<%for(InterfaceSummary o : rcps){ %>
	<tr class="lineO">
		<td><%=i++ %></td>
		<td align="left"><%=o.getRecordNo()%></td>
		<td><%=o.getRecordTime()%></td>
		<td align="left"><%=o.getCustomer()%></td>
		<td align="right"><%=new DecimalFormat("#,##0.00").format(o.getRecordAmount())%></td>
		<td>
			<%if(o.getExported().equalsIgnoreCase("Y")){ %>
			<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
			<%} %>
		</td>
		<td>
			<%if(o.getInterfaces().equalsIgnoreCase("Y")){ %>
			<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
			<%} %>
		</td>
	</tr>
	<%} %>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td class="footer">&nbsp;</td>
	</tr>	
</table>
<%} %>
<!-- VISIT -->
<%if(vsts.size()>0){ %>
<br>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td colspan="6" class="footer" align="left">
			<img border=0 src="${pageContext.request.contextPath}/icons/doc_active.gif">
			<b><bean:message key="CustomerVisit" bundle="sysprop" /></b>
		</td>
		<td class="footerNoAlign" align="right">
			<span class="searchResult"><%=vsts.size() %></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
		</td>
	</tr>	
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop"/></th>
		<th class="code"><bean:message key="CustomerVisit.No" bundle="sysele"/></th>
		<th width="120px;"><bean:message key="TransactionDate" bundle="sysele"/></th>
		<th><bean:message key="Customer" bundle="sysele"/></th>
		<th class="status"><bean:message key="ClosedSales" bundle="sysele"/></th>
		<th class="status"><bean:message key="Exported"  bundle="sysele"/></th>
		<th class="status"><bean:message key="Interfaces" bundle="sysele"/></th>
	</tr>
	<%i=1; %>
	<%for(InterfaceSummary o : vsts){ %>
	<tr class="lineO">
		<td><%=i++ %></td>
		<td align="left"><%=o.getRecordNo()%></td>
		<td><%=o.getRecordTime()%></td>
		<td align="left"><%=o.getCustomer()%></td>
		<td>
			<%if(o.getClosed().equalsIgnoreCase("Y")){ %>
			<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
			<%} %>
		</td>
		<td>
			<%if(o.getExported().equalsIgnoreCase("Y")){ %>
			<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
			<%} %>
		</td>
		<td>
			<%if(o.getInterfaces().equalsIgnoreCase("Y")){ %>
			<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
			<%} %>
		</td>
	</tr>
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