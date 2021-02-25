<%@page import="com.pens.util.DateUtil"%>
<%@page import="java.util.Date"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="manageOrderReceiptForm" class="com.isecinc.pens.web.admin.ManageOrderReceiptForm" scope="request" />
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.isecinc.pens.bean.Receipt"%>
<html>

<% 
  //default current date
 // System.out.println("docuDate["+Utils.isNull(manageOrderReceiptForm.getDocumentDate())+"]");
  if( "".equals(Utils.isNull(manageOrderReceiptForm.getDocumentDate()))){
	  manageOrderReceiptForm.setDocumentDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
  }
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
 <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<style type="text/css">

</style>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('documentDate'));	
}

function search(){
	if($('#documentDate').val()==''){
		alert('ใส่วันที่ที่ต้องการค้นหา');
		$('#documentDate').focus();
		return false;
	}
	document.manageOrderReceiptForm.action='${pageContext.request.contextPath}/jsp/manageOrderReceiptAction.do?do=search';
	document.manageOrderReceiptForm.submit();
}

function clearForm(){
	window.location = '${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';
}

function cancelOM(id){
	if(!confirm('การยกเลิกรายการขาย จะยกเลิกรายการรับชำระของรายการขายนี้ด้วย\r\nท่านแน่ใจหรือไม่?')){return false;}
	document.manageOrderReceiptForm.action='${pageContext.request.contextPath}/jsp/manageOrderReceiptAction.do?do=save&type=OM&id='+id;
	document.manageOrderReceiptForm.submit();
}

function cancelRR(id){
	if(!confirm('ต้องการยกเลิกรายการรับชำระนี้\r\nท่านแน่ใจหรือไม่?')){return false;}
	document.manageOrderReceiptForm.action='${pageContext.request.contextPath}/jsp/manageOrderReceiptAction.do?do=save&type=RR&id='+id;
	document.manageOrderReceiptForm.submit();
}
</script>
</head>
<body class="sb-nav-fixed" onload="loadMe()">

     <!-- Include Header Mobile  -->
     <jsp:include page="../../header.jsp"  flush="true"/>
     <!-- /Include Header Mobile -->
     
   	<!-- PROGRAM HEADER -->
     	<jsp:include page="../../program.jsp">
		<jsp:param name="function" value="ManageOrderReceipt"/>
		<jsp:param name="code" value=""/>
	</jsp:include>
	
	<!-- BODY -->
	<html:form action="/jsp/manageOrderReceiptAction">
	<jsp:include page="../../error.jsp"/>
		<!-- CRITERIA -->
		<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
			<tr>
				<td width="45%"></td>
				<td></td>
			</tr>
			<tr>
				<td align="right">วันที่ทำรายการ<font color="red">*</font></td>
				<td align="left">
					<html:text property="documentDate" styleId="documentDate" size="15" readonly="true"/>
				</td>
			</tr>
		</table>
		<br>
		<!-- BUTTON -->
		<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
			<tr>
				<td align="center">
					<a href="javascript:search('${pageContext.request.contextPath}')">
					<input type="button" value="ค้นหา" class="newPosBtn">
					</a>
					<a href="javascript:clearForm('${pageContext.request.contextPath}')">
					<input type="button" value="Clear" class="newNegBtn">
					</a>
				</td>
			</tr>
		</table>
		<!-- RESULT -->
		<%if(manageOrderReceiptForm.getOrderSize()+manageOrderReceiptForm.getReceiptSize()>0){ %>
		<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
			<bean:message key="RecordsFound" bundle="sysprop" />&nbsp;<span class="searchResult"><%=manageOrderReceiptForm.getOrderSize()+manageOrderReceiptForm.getReceiptSize() %></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
		</div>
		<!-- ORDER -->
		<%if(manageOrderReceiptForm.getOrderSize()>0){ %>
		  <div class="table-responsive">
            <table class="table table-bordered table-striped table-light"
                   id="dataTable" width="100%" cellspacing="0">
            <thead class="thead-dark">
			<tr>
				<td colspan="5" class="footer" align="left">
					<img border=0 src="${pageContext.request.contextPath}/icons/doc_active.gif">
					<b><bean:message key="SalesOrder" bundle="sysprop" /></b>
				</td>
				<td class="footerNoAlign" align="right">
					<span class="searchResult"><%=manageOrderReceiptForm.getOrderSize() %></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
				</td>
			</tr>
			<tr>
				<th class="order"><bean:message key="No" bundle="sysprop" /></th>
				<th class="code"><bean:message key="DocumentNo" bundle="sysele" /></th>
				<th width="120px;"><bean:message key="TransactionDate" bundle="sysele" /></th>
				<th><bean:message key="Customer" bundle="sysele"/></th>
				<th class="costprice"><bean:message key="TotalAmount" bundle="sysele" /></th>
				<th class="status">ยกเลิกรายการ</th>
			</tr>
			</thead>
			<%int i=1; %>
			<%for(Order o : manageOrderReceiptForm.getOrders()){ %>
			<tr class="lineO">
				<td><%=i++ %></td>
				<td align="left"><%=o.getOrderNo()%></td>
				<td><%=o.getOrderDate()%></td>
				<td align="left"><%=o.getCustomerName()%></td>
				<td align="right"><%=new DecimalFormat("#,##0.00").format(o.getNetAmount())%></td>
				<td align="center">
					<a href="javascript:cancelOM('<%=o.getId() %>');">
					<img src="${pageContext.request.contextPath}/icons/uncheck.gif" border="0" align="absmiddle"></a>
				</td>
			</tr>
			<%} %>
		</table>
	   </div>
		<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
			<tr>
				<td class="footer">&nbsp;</td>
			</tr>	
		</table>
		<%} %>
		<%if(manageOrderReceiptForm.getReceipts().size()>0){ %>
		<br>
		  <div class="table-responsive">
            <table class="table table-bordered table-striped table-light"
                   id="dataTable" width="100%" cellspacing="0">
            <thead class="thead-dark">
			<tr>
				<td colspan="5" class="footer" align="left">
					<img border=0 src="${pageContext.request.contextPath}/icons/doc_active.gif">
					<b><bean:message key="Receipt" bundle="sysprop" /></b>
				</td>
				<td class="footerNoAlign" align="right">
					<span class="searchResult"><%=manageOrderReceiptForm.getReceiptSize() %></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
				</td>
			</tr>
			<tr>
				<th class="order"><bean:message key="No" bundle="sysprop"/></th>
				<th class="code"><bean:message key="Receipt.No" bundle="sysele"/></th>
				<th width="120px;"><bean:message key="TransactionDate" bundle="sysele"/></th>
				<th><bean:message key="Customer" bundle="sysele"/></th>
				<th class="costprice"><bean:message key="Order.Payment" bundle="sysele"/></th>
				<th class="status">ยกเลิกรายการ</th>
			</tr>
			</thead>
			<%int i=1; %>
			<%for(Receipt o : manageOrderReceiptForm.getReceipts()){ %>
			<tr class="lineO">
				<td><%=i++ %></td>
				<td align="left"><%=o.getReceiptNo()%></td>
				<td><%=o.getReceiptDate()%></td>
				<td align="left"><%=o.getCustomerName()%></td>
				<td align="right"><%=new DecimalFormat("#,##0.00").format(o.getReceiptAmount())%></td>
				<td align="center">
					<a href="javascript:cancelRR('<%=o.getId() %>');">
					<img src="${pageContext.request.contextPath}/icons/uncheck.gif" border="0" align="absmiddle"></a>
				</td>
			</tr>
			<%} %>
		</table>
		</div>
		<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
			<tr>
				<td class="footer">&nbsp;</td>
			</tr>	
		</table>
		<%} %>
		<%} %>
		<br>
		
	</html:form>
		
	<!-- Include Footer Mobile  -->
    <jsp:include page="../../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->		
						
</body>
</html>