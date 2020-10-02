<%@page import="util.SessionGen"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="receiptForm" class="com.isecinc.pens.web.receipt.ReceiptForm" scope="request"/>
<%
	String action = (String)request.getParameter("action");
	if(action == null){
		action = "";
	}
	List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
	pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);
	
	List<References> payment= InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
	pageContext.setAttribute("payment",payment,PageContext.PAGE_SCOPE);
	
	List<References> banks= InitialReferences.getReferenes().get(InitialReferences.BANK);
	pageContext.setAttribute("banks",banks,PageContext.PAGE_SCOPE);
	
	List<References> internalBank= InitialReferences.getReferenes().get(InitialReferences.INTERNAL_BANK);
	pageContext.setAttribute("internalBank",internalBank,PageContext.PAGE_SCOPE);
	
	User user = (User) session.getAttribute("user");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/receipt.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
//clear cach
$.ajaxSetup({cache: false});

function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('receiptDate'));
	new Epoch('epoch_popup','th',document.getElementById('chequeDate'));
	//change_payment(document.getElementsByName('receipt.paymentMethod')[0].value);

	calculateAll();
}

function change_payment(val){
	if(val=='CH'||val=='CR'){
		document.getElementById('div_cheque1').style.display='';
		if(val=='CH'){
			document.getElementById('div_cheque2').style.display='';
			document.getElementById('div_cheque3').style.display='none';
			$('#creditCardType').val('');
		}else{
			document.getElementById('div_cheque2').style.display='none';
			document.getElementById('div_cheque3').style.display='';
			$('#chequeDate').val('');
		}
	}else if(val=='CS'){
		document.getElementById('div_cheque1').style.display='none';
		document.getElementById('div_cheque2').style.display='none';
		document.getElementById('div_cheque3').style.display='none';
		$('#bank').val('');
		$('#chequeNo').val('');
		$('#chequeDate').val('');
		$('#creditCardType').val('');
	}
}

</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="Receipt"/>
				<jsp:param name="code" value="${receiptForm.receipt.receiptNo}"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/receiptAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="30%"></td>
								<td width="20%"></td>
								<td width="15%"></td>
								<td></td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="Receipt.No" bundle="sysele"/><font color="red">*</font></td>
								<td align="left"><html:text property="receipt.receiptNo" size="20" readonly="true" styleClass="disableText"/></td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="TransactionDate" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:text property="receipt.receiptDate" styleId="receiptDate" maxlength="10" size="15" readonly="true" />
								</td>
							</tr>
							<tr>
								<td align="right">
									<%if(!((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
									<bean:message key="Customer" bundle="sysele"/>&nbsp;&nbsp;
									<%}else{ %>
									<bean:message key="Member" bundle="sysele"/>&nbsp;&nbsp;
									<%} %>
								</td>
								<td align="left" colspan="3">
									<html:text property="receipt.customerName" size="80" readonly="true" styleClass="disableText"/>
									<html:hidden property="receipt.customerId"/>
								</td>
							</tr>
							<tr>
								<td align="right">
									<bean:message key="InternalBank" bundle="sysele"/><font color="red">*</font>
								</td>
								<td align="left" colspan="3">
									<html:select property="receipt.internalBank">
										<html:options collection="internalBank" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<!-- Bill -->
							<tr>
								<td colspan="4">
									<jsp:include page="receiptCR.jsp"></jsp:include>
								</td>
							</tr>
							<tr><td colspan="4"><hr></td>
							<!-- RECEIPT CN -->
							<tr>
								<td colspan="4">
									<jsp:include page="receiptCN.jsp"></jsp:include>
								</td>
							</tr>
							<tr><td colspan="4"><hr></td>
							<!-- Pay By -->
							<tr>
								<td colspan="4">
									<jsp:include page="receiptBY.jsp"></jsp:include>
								</td>
							</tr>
							<tr><td colspan="4"><hr></td>
							<!-- Total Bill -->
							<tr>
								<td colspan="2"></td>
								<td align="right">ยอดรวมใบแจ้งหนี้<font color="red">*</font></td>
								<td align="left">
									<input type="text" id="totalBillAmountFMT" name="totalBillAmountFMT" size="10" style="text-align: right;" readonly="readonly" class="disableText"> 
									<input type="hidden" id="totalBillAmount" name="totalBillAmount">
								</td>
							</tr>
							<!-- Total CN -->
							<tr>
								<td colspan="2"></td>
								<td align="right">ยอดรวมใบลดหนี้<font color="red">*</font></td>
								<td align="left">
									<input type="text" id="totalCNAmountFMT" name="totalCNAmountFMT" size="10" style="text-align: right;" readonly="readonly" class="disableText"> 
									<input type="hidden" id="totalCNAmount" name="totalCNAmount">
								</td>
							</tr>
							<!-- Total Receipt -->
							<tr>
								<td colspan="2"></td>
								<td align="right"><bean:message key="AmountReceived" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<input type="text" id="txtReceiptAmount" name="txtReceiptAmount" size="10" style="text-align: right;" readonly="readonly" class="disableText"/> 
									<html:hidden property="receipt.receiptAmount" styleId="receiptAmount"/>
								</td>
							</tr>
							<!-- Total Use -->
							<tr>
								<td colspan="2"></td>
								<td align="right"><bean:message key="Receipt.PaidAmount" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<input type="text" id="txtApplyAmount" name="txtApplyAmount" size="10" style="text-align: right;" readonly="readonly" class="disableText"/> 
									<html:hidden property="receipt.applyAmount" styleId="applyAmount"/>
								</td>
							</tr>
							<!-- Total Remain -->
							<tr>
								<td colspan="2"></td>
								<td align="right">คงเหลือ<font color="red">*</font></td>
								<td align="left">
									<input type="text" id="remainAmountFMT" name="remainAmountFMT" size="10" style="text-align: right;" readonly="readonly" class="disableText"/> 
									<input type="hidden" id="remainAmount" name="remainAmount"/>
								</td>
							</tr>
							<tr><td colspan="4"><hr></td>
							<tr>
								<td align="right"></td>
								<td align="left">
									<html:checkbox property="receipt.exported" disabled="true" value="Y"/><bean:message key="Exported"  bundle="sysele"/>
								</td>
								<td align="right"><bean:message key="Status" bundle="sysele"/><font color="red">*</font></td>
								<td valign="top">
									<html:select property="receipt.docStatus">
										<html:options collection="docstatus" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td></td>
								<td align="left">
									<html:checkbox property="receipt.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.SalesRepresent"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="receipt.salesRepresent.name" size="30" readonly="true" styleClass="disableText"/>
									<html:hidden property="receipt.salesRepresent.id"/>
									<html:hidden property="receipt.salesRepresent.code"/>
								</td>
							</tr>
						</table>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">	
									<input type="button" value="บันทึก" class="newPosBtn" onclick="return save('${pageContext.request.contextPath}','<%=user.getType() %>');">
			
									<input type="button" value="ยกเลิก" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${receiptForm.receipt.customerId}');">		
								</td>
							</tr>
						</table>
						<div id="BillList" style="display: none;"></div>
						<div id="ByList" style="display: none;"></div>
						<div id="CNList" style="display: none;"></div>
						<html:hidden property="deletedId"/>
						<html:hidden property="deletedRecpById"/>
						<html:hidden property="receipt.id"/>
						<html:hidden property="receipt.orderType"/>
						<html:hidden property="receipt.prepaid"/>
						<html:hidden property="receipt.exported" value="N"/>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						</html:form>
						<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</body>
</html>