<%@page import="java.util.Date"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
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
  if( "".equals(Utils.isNull(manageOrderReceiptForm.getDocumentDateFrom()))){
	  manageOrderReceiptForm.setDocumentDateFrom(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
  }
  if( "".equals(Utils.isNull(manageOrderReceiptForm.getDocumentDateTo()))){
	  manageOrderReceiptForm.setDocumentDateTo(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
  }
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('documentDateFrom'));	
	new Epoch('epoch_popup','th',document.getElementById('documentDateTo'));	
	
	<%if("back".equalsIgnoreCase(Utils.isNull(request.getParameter("action")))){%>
	  searchBack();
	<%}%>
}

function search(){
	if($('#documentDateFrom').val()==''){
		alert('����ѹ������ͧ��ä���');
		$('#documentDateFrom').focus();
		return false;
	}
	if($('#documentDateTo').val()==''){
		alert('����ѹ������ͧ��ä���');
		$('#documentDateTo').focus();
		return false;
	}
	document.manageOrderReceiptForm.action='${pageContext.request.contextPath}/jsp/manageOrderReceiptAction.do?do=search';
	document.manageOrderReceiptForm.submit();
}
function searchBack(){
	if($('#documentDateFrom').val()==''){
		alert('����ѹ������ͧ��ä���');
		$('#documentDateFrom').focus();
		return false;
	}
	document.manageOrderReceiptForm.action='${pageContext.request.contextPath}/jsp/manageOrderReceiptAction.do?do=search&action=searchBack';
	document.manageOrderReceiptForm.submit();
}
function clearForm(){
	window.location = '${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';
}

function cancelOM(id){
	if(!confirm('���¡��ԡ��¡�â�� ��¡��ԡ��¡���Ѻ���Тͧ��¡�â�¹�����\r\n��ҹ����������?')){return false;}
	document.manageOrderReceiptForm.action='${pageContext.request.contextPath}/jsp/manageOrderReceiptAction.do?do=save&type=OM&id='+id;
	document.manageOrderReceiptForm.submit();
}

function cancelRR(id){
	if(!confirm('��ͧ���¡��ԡ��¡���Ѻ���й��\r\n��ҹ����������?')){return false;}
	document.manageOrderReceiptForm.action='${pageContext.request.contextPath}/jsp/manageOrderReceiptAction.do?do=save&type=RR&id='+id;
	document.manageOrderReceiptForm.submit();
}
function viewReceipt(id){
	var path='${pageContext.request.contextPath}';
    var documentDateFrom = document.getElementById("documentDateFrom").value;
    var documentDateTo = document.getElementById("documentDateTo").value;
    var customerCode = document.getElementById("customerCode").value;
    var customerName = document.getElementById("customerName").value;
    
	var param ="documentDateFrom="+documentDateFrom;
    param +="&documentDateTo="+documentDateTo;
    param +="&customerCode="+customerCode;
    param +="&customerName="+customerName;
    
    /** save cri for back from ReceiptView.jsp **/
	var getData = $.ajax({
		url: "${pageContext.request.contextPath}/jsp/ajax/setManageOrderReceiptCriSessionAjax.jsp",
		data : encodeURI(param),
		async: false,
		cache: false,
		success: function(getData){
		  returnString = jQuery.trim(getData);
		}
	}).responseText;
	
	document.manageOrderReceiptForm.action = path+"/jsp/receiptAction.do?do=prepare&id=" + id + "&action=view&fromPage=manageOrderReceipt";
	document.manageOrderReceiptForm.submit();
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');loadMe();" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="ManageOrderReceipt"/>
				<jsp:param name="code" value=""/>
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
						<html:form action="/jsp/manageOrderReceiptAction">
						<jsp:include page="../../error.jsp"/>
						<!-- CRITERIA -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="45%"></td>
								<td width="10%"></td>
								<td width="45%"></td>
							</tr>
							<tr>
								<td align="right">�ѹ������¡�� �ҡ<font color="red">*</font></td>
								<td align="left">
									<html:text property="documentDateFrom" styleId="documentDateFrom" size="15" readonly="true"/>
								</td>
								<td align="left">�֧
									<html:text property="documentDateTo" styleId="documentDateTo" size="15" readonly="true"/>
								</td>
							</tr>
							<tr>
								<td align="right">������ҹ���<font color="red"></font></td>
								<td align="left">
									<html:text property="customerCode" styleId="customerCode" size="15" styleClass="\" autoComplete=\"off"/>
								</td>
								<td align="left">������ҹ���
									<html:text property="customerName" styleId="customerName" size="30" styleClass="\" autoComplete=\"off"/>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<input type="button" value="����" class="newPosBtn" onclick="search('${pageContext.request.contextPath}')">
									
									<input type="button" value="Clear" class="newNegBtn" onclick=clearForm('${pageContext.request.contextPath}')">
									
								</td>
							</tr>
						</table>
						<!-- RESULT -->
						<%if(manageOrderReceiptForm.getOrderSize()+manageOrderReceiptForm.getReceiptSize()>0){ %>
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
							<bean:message key="RecordsFound" bundle="sysprop" />&nbsp;<span class="searchResult"><%=manageOrderReceiptForm.getOrderSize()+manageOrderReceiptForm.getReceiptSize() %></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
						</div>
			
						<%if(manageOrderReceiptForm.getReceipts().size()>0){ %>
						<br>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
							<tr>
								<td colspan="8" class="footer" align="left">
									<img border=0 src="${pageContext.request.contextPath}/icons/doc_active.gif">
									<b><bean:message key="Receipt" bundle="sysprop" /></b>
								</td>
								<td class="footerNoAlign" align="right">
									<span class="searchResult"><%=manageOrderReceiptForm.getReceiptSize() %></span>&nbsp;<bean:message key="Records" bundle="sysprop" />
								</td>
							</tr>
							<tr>
								<th ><bean:message key="No" bundle="sysprop"/></th>
								<th ><bean:message key="Receipt.No" bundle="sysele"/></th>
								<th ><bean:message key="TransactionDate" bundle="sysele"/></th>
								<th ><bean:message key="Customer" bundle="sysele"/></th>
								<th >ʶҹ�</th>
								<th >�觢����������ǹ��ҧ</th>
								<th ><bean:message key="Order.Payment" bundle="sysele"/></th>
								<th >¡��ԡ��¡��</th>
								<th >�ʴ�</th>
							</tr>
							<%int i=1; %>
							<%for(Receipt o : manageOrderReceiptForm.getReceipts()){ %>
							<tr class="lineO">
								<td class="td_text_center" width="5%"><%=i++ %></td>
								<td class="td_text_center" width="10%"><%=o.getReceiptNo()%></td>
								<td class="td_text_center" width="5%"><%=o.getReceiptDate()%></td>
								<td class="td_text" width="25%"><%=o.getCustomerName()%></td>
								<td class="td_text_center" width="5%">
								<%
								  if(o.getDocStatus().equalsIgnoreCase("VO")){
									  out.print("¡��ԡ");
								  }else{
									  out.print("�ѹ�֡"); 
								  }
								%>
								<td class="td_text_center" width="5%">
								   <%if(o.getExported().equalsIgnoreCase("Y")){ %>
									   <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									<%} %>
								</td>
								</td>
								<td class="td_number" width="10%"><%=new DecimalFormat("#,##0.00").format(o.getReceiptAmount())%></td>
								<td class="td_text_center" width="10%">
								    <%if( !Utils.isNull(o.getDocStatus()).equalsIgnoreCase("VO")
								    	&& !Utils.isNull(o.getExported()).equalsIgnoreCase("Y")) {%>
									   <a href="javascript:cancelRR('<%=o.getId() %>');">
									   <img src="${pageContext.request.contextPath}/icons/uncheck.gif" border="0" align="absmiddle"></a>
								    <%} %>
								</td>
								<td  class="td_text_center" width="10%">
									<a href="javascript:viewReceipt('<%=o.getId() %>');">
									 <img src="${pageContext.request.contextPath}/icons/lookup.gif" border="0" align="absmiddle">
									</a>
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
						<%} %>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<input type="button" value="�Դ˹�Ҩ�" class="newNegBtn" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
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
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>