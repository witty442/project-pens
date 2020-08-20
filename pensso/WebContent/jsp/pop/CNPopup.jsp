<%@page import="com.pens.util.DBConnectionApps"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%
String customerId = request.getParameter("customerId");;
System.out.println("customerId:"+customerId);

String selected = request.getParameter("selected");
if(selected==null)selected="";

User user = (User) session.getAttribute("user");

List<CreditNote> zero = new ArrayList<CreditNote>();

List<CreditNote> cns = new ArrayList<CreditNote>(); 
Connection conn = null;
try{
	conn = DBConnectionApps.getInstance().getConnection();
	cns = new MCreditNote().lookUpForReceipt(conn,user.getId(),selected,Integer.parseInt(customerId)); 
	for(CreditNote cn : cns){
		cn.setCreditAmount(new MReceiptCN().calculateCNCreditAmount(conn,cn));
		System.out.println("creditAmt:"+cn.getCreditAmount());
		if(cn.getCreditAmount()==0) 
			zero.add(cn); 
	}
	//remove zero credit
	for(CreditNote r : zero){
		cns.remove(r);
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	conn.close();
}
pageContext.setAttribute("cns",cns,PageContext.PAGE_SCOPE);
%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.CreditNote"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MCreditNote"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.model.MReceiptCN"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
	var prepaid=0;
	var gpp=0;
	function loadMe() {
		prepaid = Number(window.opener.document.getElementById('receiptAmount').value);
		gpp=prepaid;
		//alert(prepaid);
	}

	function addRow() {

		var retArry = new Array();
		var cn;

		var objchk = document.getElementsByName('chkCN');

		var idx = 0;
		for (i = 0; i < objchk.length; i++) {
			if (objchk[i].checked) {
				cn = new Object();
				cn.id=0;
				cn.cnId= document.getElementsByName('creditNoteId')[i].value;
				cn.cnNo= document.getElementsByName('creditNoteNo')[i].value;
				cn.cnInvoiceNo= document.getElementsByName('arInvoiceNo')[i].value;
				cn.totalAmount= document.getElementsByName('totalAmount')[i].value;
				cn.creditAmount = document.getElementsByName('creditAmount')[i].value;
				cn.paidAmount = document.getElementsByName('paidAmount')[i].value;;
				cn.remainAmount = document.getElementsByName('remainAmount')[i].value;
				
				retArry[idx]=cn;
				idx++;
			}
		}
		window.opener.addCN('${pageContext.request.contextPath}',retArry);
		window.close();
	}
</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="ใบแจ้งหนี้"/>
</jsp:include>
<table align="center" border="0" cellpadding="0" cellspacing="1" width="100%" class="result">
	<tr>
		<th>เลขที่ใบลดหนี้</th>
		<th>วันที่ใบลดหนี้</th>
		<th><bean:message key="Bill.No" bundle="sysele"/></th>
		<th><bean:message key="TotalAmount" bundle="sysele"/></th>
		<th><bean:message key="Order.Behindhand" bundle="sysele"/></th>
		<th><bean:message key="Order.Payment" bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="${cns}" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO"/>
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE"/>
			</c:otherwise>
		</c:choose>
		<tr class="<c:out value='${tabclass}'/>">
			<td align="center">
				${results.creditNoteNo}
				<input type="hidden" name="creditNoteId" value="${results.id}">
				<input type="hidden" name="creditNoteNo" value="${results.creditNoteNo}">
				<input type="hidden" name="arInvoiceNo" value="${results.arInvoiceNo}">
				<input type="hidden" name="totalAmount" value="${results.totalAmount}">
			</td>
			<td align="center">
				${results.documentDate}
			</td>
			<td align="center">
				${results.arInvoiceNo}
			</td>
			<td>
				<fmt:formatNumber pattern="#,##0.00" value="${results.totalAmount}"/>
			</td>
			<td>
				<fmt:formatNumber pattern="#,##0.00" value="${results.creditAmount}"/>
				<input type="hidden" name="creditAmount" value="${results.creditAmount}">
				<input type="hidden" name="paidAmount"  value="${results.paidAmount}"/>
				<input type="hidden" name="remainAmount"  value="${results.remainAmount}"/>
			</td>
			<td>
				<input type="checkbox" name="chkCN">
			</td>
		</tr>
	</c:forEach>
	<tr>
		<td align="left" colspan="6" class="footer">&nbsp;</td>
	</tr>
</table>	
<br>
<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td align="center">
			<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" value="บันทึก" class="newPosBtn" onclick="addRow();">
			
			<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn" onclick="window.close();">
		</td>
	</tr>
</table>
<input type="hidden" id="billId" name="billId">
<input type="hidden" id="addressRow" name="addressRow">
<br/>
</body>
</html>