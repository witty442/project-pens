<%@page import="com.pens.util.DBConnectionApps"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.model.MAdjust"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.model.MOrder"%>
<%@page import="com.isecinc.pens.model.MReceiptLine"%>
<%@page import="com.isecinc.pens.model.MCreditNote"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String selected = request.getParameter("selected");
if(selected==null)selected="";
String custId = request.getParameter("cust");
String billToAddressId = request.getParameter("billToAddressId");
User user = (User) session.getAttribute("user");
Connection conn = null;
try{
	//init Connection 
	conn = DBConnectionApps.getInstance().getConnection();
	
	List<Order> invoicesAll = new MOrder().lookUpByOrderAR(conn,user.getId(),Integer.parseInt(custId) ,Integer.parseInt(billToAddressId) 
			,user.getOrderType().getKey(),"not in",selected);
	double totalCreditNoteAmt = 0; 
	double totalAdjustAmt = 0; 
	 
	
	System.out.println("invoicesAll :"+invoicesAll.size());
	
	List<Order> orders  = new ArrayList<Order>();
	MCreditNote creditNote = new MCreditNote();
	MAdjust adjust = new MAdjust();
	
	for(Order r : invoicesAll){
		r.setCreditAmount(new MReceiptLine().calculateCreditAmount(conn,r));
		
		totalCreditNoteAmt = creditNote.getTotalCreditNoteAmt(conn,r.getArInvoiceNo());
		
		totalAdjustAmt = adjust.getTotalAdjustAmtInvoice(conn,r.getArInvoiceNo());  
		
		r.setCreditNoteAmt(totalCreditNoteAmt);
		r.setAdjustAmt(totalAdjustAmt);
		r.setOpenAmt();
		
		System.out.println("OpenAmt(remain_amt):"+r.getOpenAmt());
		//remove zero credit
		if(r.getOpenAmt() > 0.01){
			orders.add(r);
		}
	}
	pageContext.setAttribute("orders",orders,PageContext.PAGE_SCOPE);
}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn != null){
		conn.close();conn=null;
	}
}

%>
<html>
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
	function loadMe() {
		var prepaid=0;
		var prepaid = Number(window.opener.document.getElementById('receiptAmount').value);
		var gpp=prepaid;
		
		//alert(prepaid);
	}
	function addRow() {
		var retArry = new Array();
		var bill;

		var objchk = document.getElementsByName('chkReceipts');

		var idx = 0;
		for (var c = 0; c < objchk.length; c++) {
			if (objchk[c].checked) {
				bill = new Object();
				bill.rows=0;
				bill.invoiceId = document.getElementsByName('invoiceId')[c].value;
				bill.invoiceNo = document.getElementsByName('arInvoiceNo')[c].value;
				bill.salesOrderNo = document.getElementsByName('salesOrderNo')[c].value;
				bill.netAmount = document.getElementsByName('netAmount')[c].value;
				bill.creditAmount = eval(document.getElementsByName('creditAmount')[c].value)+eval(document.getElementsByName('creditNoteAmt')[c].value);
				bill.paidAmount = document.getElementsByName('paidAmount')[c].value;;
				bill.remainAmount = document.getElementsByName('remainAmount')[c].value;

				retArry[idx]=bill;
				idx++;
			}
		}
		window.opener.addBill('${pageContext.request.contextPath}',retArry);
		window.close();
	}
function chkAllFlag(chkAll){
	var invoiceIdS = document.getElementsByName('invoiceId');
	var objchk = document.getElementsByName('chkReceipts');
	for (var c = 0; c < invoiceIdS.length; c++) {
		objchk[c].checked = chkAll.checked;
	}	
}
</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="���˹��"/>
</jsp:include>
<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<td align="center">
					<input type="button" value="�ѹ�֡" class="newPosBtn" onclick="addRow();">

					<input type="button" value="¡��ԡ" class="newNegBtn" onclick="window.close();">
				</td>
			</tr>
		</table>
<!-- <div style="overflow:auto;height:300px;"> -->
		<table align="center" border="0" cellpadding="0" cellspacing="1" width="100%" class="result">
			<tr>
				<th>�Ţ������˹��</th>
				<th>�Ţ�����¡�â��</th>
				<th>�ʹ�Թ���</th>
				<th>Ŵ˹��</th>
				<th>��ҧ����.</th>
				<th>�Ѻ���� <p>
				   <input type="checkbox" id="chkAll" name="chkAll" onclick="chkAllFlag(this)"/>
				 </p>
				</th>
				
			</tr>
			<c:forEach var="results" items="${orders}" varStatus="rows">
				<c:choose>
					<c:when test="${rows.index %2 == 0}">
						<c:set var="tabclass" value="lineO"/>
					</c:when>
					<c:otherwise>
						<c:set var="tabclass" value="lineE"/>
					</c:otherwise>
				</c:choose>
				<tr class="<c:out value='${tabclass}'/>">
					<td>
						${results.arInvoiceNo}
						<input type="hidden" name="arInvoiceNo" value="${results.arInvoiceNo}">
						<input type="hidden" name="invoiceId" value="${results.invoiceId}">
					</td>
					<td>
						${results.salesOrderNo}
						<input type="hidden" name="salesOrderNo" value="${results.salesOrderNo}">
					</td>
					<td>
						<fmt:formatNumber pattern="#,##0.00" value="${results.netAmount}"/>
						<input type="hidden" name="netAmount" value="${results.netAmount}">
					</td>
					<td>
						<fmt:formatNumber pattern="#,##0.00" value="${results.creditNoteAmt}"/>
						<input type="hidden" name="creditNoteAmt" value="${results.creditNoteAmt}">
					</td>
					<td>
						<fmt:formatNumber pattern="#,##0.00" value="${results.openAmt}"/>
						<input type="hidden" name="creditAmount" value="${results.creditAmount}">
						<input type="hidden" name="paidAmount"  value="${results.paidAmount}"/>
						<input type="hidden" name="remainAmount"  value="${results.openAmt}"/>
					</td>
					<td>
						<input type="checkbox" name="chkReceipts" >
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
					<input type="button" value="�ѹ�֡" class="newPosBtn" onclick="addRow();">

					<input type="button" value="¡��ԡ" class="newNegBtn" onclick="window.close();">
				</td>
			</tr>
		</table>
<!-- 	</div> -->
	
<input type="hidden" id="billId" name="billId">
<input type="hidden" id="addressRow" name="addressRow">
<br/>
</body>
</html>