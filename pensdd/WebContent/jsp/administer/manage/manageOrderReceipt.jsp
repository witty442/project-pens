<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<% 

%>
<jsp:useBean id="manageOrderReceiptForm" class="com.isecinc.pens.web.admin.ManageOrderReceiptForm" scope="request" />
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.isecinc.pens.bean.Receipt"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('documentDate'));	
}

function search(){
	if($('#documentDate').val()==''){
		alert('����ѹ������ͧ��ä���');
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
	if(!confirm('���¡��ԡ��¡�â�� ��¡��ԡ��¡���Ѻ���Тͧ��¡�â�¹�����\r\n��ҹ����������?')){return false;}
	document.manageOrderReceiptForm.action='${pageContext.request.contextPath}/jsp/manageOrderReceiptAction.do?do=save&type=OM&id='+id;
	document.manageOrderReceiptForm.submit();
}

function saveCancel(){
	var totalRows = document.getElementById("totalRows").value;
	var isChecked = false;
	for(var i=0;i < totalRows ; i++){
		isChecked = document.getElementsByName("receipts["+i+"].isCancel")[0].checked;
		if(isChecked)
			break;
	}
	
	if(!isChecked){
		alert("��س����͡��¡�÷���ͧ���¡��ԡ");
		return false;
	}else{
		if(!confirm('��ͧ���¡��ԡ��¡���Ѻ���й��\r\n��ҹ����������?')){return false;}
		document.manageOrderReceiptForm.action='${pageContext.request.contextPath}/jsp/manageOrderReceiptAction.do?do=save&type=RR';
		document.manageOrderReceiptForm.submit();
	}
}
function checkedCancelGroup(idx){
	// Get Current Value
	//alert(idx);
	var tripNo = document.getElementsByName("receipts["+idx+"].tripNo")[0].value;
	var totalRows = document.getElementById("totalRows").value;
	
	var isCancel =  document.getElementsByName("receipts["+idx+"].isCancel")[0].checked;
	
	//check next
	for(var n=0;n<totalRows;n++){
		var nTripNo = document.getElementsByName("receipts["+n+"].tripNo")[0].value;
		if(nTripNo == tripNo){
			//alert("orderNo["+orderNo+"]nOrderNo["+nOrderNo+"]nTripNo["+nTripNo+"]tripNo["+tripNo+"]");
			document.getElementsByName("receipts["+(n)+"].isCancel")[0].checked = isCancel;
		}
	}//for
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
				<jsp:param name="function" value="ManageReceipt"/>
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
								<td></td>
							</tr>
							<tr>
								<td align="right">�ѹ������¡��<font color="red">*</font></td>
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
									<!--<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">-->
									<input type="button" value="����" class="newPosBtn">
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<!--<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">-->
									<input type="button" value="Clear" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<!-- RESULT -->
						
						<%if(manageOrderReceiptForm.getReceiptSize()>0){ %>
						<input id="totalRows" type="hidden" value="<%=manageOrderReceiptForm.getReceiptSize()%>">
						<br>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<td colspan="6" class="footer" align="left">
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
							    <th class="code">���駷����</th>
								<th width="120px;"><bean:message key="TransactionDate" bundle="sysele"/></th>
								<th><bean:message key="Customer" bundle="sysele"/></th>
								<th class="costprice"><bean:message key="Order.Payment" bundle="sysele"/></th>
								<th class="status">¡��ԡ��¡��</th>
							</tr>
							<%int i=1;
							  int index =0;%>
							<%for(Receipt o : manageOrderReceiptForm.getReceipts()){ %>
							<tr class="lineO">
								<td><%=i++ %></td>
								<td align="left"><%=o.getReceiptNo()%></td>
								<td><%=o.getTripNo()%></td>
								<td><%=o.getReceiptDate()%></td>
								<td align="left"><%=o.getCustomerName()%></td>
								<td align="right"><%=new DecimalFormat("#,##0.00").format(o.getReceiptAmount())%></td>
								<td align="center">
								  <input name="receipts[<%=index %>].id" type="hidden" value="<%=o.getId()%>"/>
								  <input name="receipts[<%=index %>].isCancel"  type="checkbox" value="Y" onchange="checkedCancelGroup(<%=index%>)" >
								  <input name="receipts[<%=index %>].tripNo" type="hidden" value="<%=o.getTripNo()%>"/>
								  <input name="receipts[<%=index %>].customerName" type="hidden" value="<%=o.getCustomerName()%>"/>
									     
									<!--  <a href="javascript:cancelRR('<%=o.getId() %>','<%=o.getTripNo()%>');">
									<img src="${pageContext.request.contextPath}/icons/uncheck.gif" border="0" align="absmiddle"></a>
									-->
								</td>
							</tr>
							<% index++;} %>
						</table>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<td class="footer">&nbsp;</td>
							</tr>	
						</table>
						<%} %>
						<br>
						<!-- BUTTON -->
						
						 <%if(manageOrderReceiptForm.getReceiptSize()>0){ %>
							<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
								<tr>
									<td align="center">
										  <a name="save" href="#" onclick="saveCancel('${pageContext.request.contextPath}','${shipmentForm.shipmentCriteria.searchResult}');">
										    <input type="button" value="�ѹ�֡¡��ԡ" class="newPosBtn">
										  </a>
									  
										<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
										   <input type="button" value="�Դ˹�Ҩ�" class="newNegBtn">
									    </a>
									</td>
								</tr>
							</table>
			             <% } %>
			 
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