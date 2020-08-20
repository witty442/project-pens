<%@page import="com.isecinc.pens.web.clearinvoice.ClearInvoice"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="clearInvoiceForm" class="com.isecinc.pens.web.clearinvoice.ClearInvoiceForm" scope="session" />
<%
String role = ((User)session.getAttribute("user")).getType();

List<References> types= new ArrayList<References>();
types.add(new References("DETAIL","รายละเอียด"));
types.add(new References("TOTAL","ผลรวม"));


pageContext.setAttribute("types",types,PageContext.PAGE_SCOPE);
%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('orderDateFrom'));
	new Epoch('epoch_popup', 'th', document.getElementById('orderDateTo'));
}

function search(path){
	document.clearInvoiceForm.action = path + "/jsp/clearInvoiceAction.do?do=search";
	document.clearInvoiceForm.submit();
	return true;
}
function updatePaidAmount(path){
	if(confirm("ยืนยัน update ข้อมูล")){
	   document.clearInvoiceForm.action = path + "/jsp/clearInvoiceAction.do?do=save";
	   document.clearInvoiceForm.submit();
	   return true;
	}
	return false;
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
				<jsp:param name="function" value="ClearInvoice"/>
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
						<html:form action="/jsp/clearInvoiceAction">
						<jsp:include page="../error.jsp"/>
						
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="60%">
						<tr>
							<td align="right">
							     จาก วันที่ขาย&nbsp;&nbsp; 
							</td>
							<td align="left">
							  <html:text property="bean.orderDateFrom" styleId="orderDateFrom" readonly="true"/>
							    ถึง วันที่ขาย&nbsp;&nbsp; <html:text property="bean.orderDateTo" styleId="orderDateTo"/></td>
						</tr>
						<tr>
							<td align="right">รหัสร้านค้า&nbsp;&nbsp;</td>
							<td align="left"><html:text property="bean.customerCode" styleId="customerCode"/>
							&nbsp;&nbsp;
							AR Invoice No
							<html:text property="bean.arInvoiceNo" styleId="arInvoiceNo"/>
							</td>
						</tr>
						<tr>
							<td align="right">เงื่อนไนการ update</td>
							<td align="left"><html:text property="bean.condition" styleId="condition" size="70"/>
							</td>
						</tr>
						<tr>
							<td align="center" colspan="2">
							 ex. remain_amount <= 100 and remain_amount >=0.9
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
								  <input type="button" value="Clear" class="newPosBtn">
								</a>
								<a href="javascript:updatePaidAmount('${pageContext.request.contextPath}')">
								  <input type="button" value="Update Paid Amount" class="newPosBtn">
								</a>
							</td>
						</tr>
					</table>
					<!-- RESULT -->
				    
			        <c:if test="${clearInvoiceForm.results != null}">
						  <table align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
							<tr>
							    <th>No</th>
								<th>Customer ID</th>
								<th>Customer Code</th>
								<th>Customer Name</th>
								<th>Order ID</th>
								<th>Ar Invoice No</th>
								<th>Total Amount</th>
								<th>Vat Amount</th>
								<th>Net Amount</th>
								<th>Paid Amount</th>
								<th>Remain Amount(Calc)</th>
								<!-- <th>Remain Amount(Receipt)</th> -->
							</tr>
							<%
							String trClass ="lineE";
							for(int i=0;i<clearInvoiceForm.getResults().size();i++){
								ClearInvoice item = clearInvoiceForm.getResults().get(i);
								if(i%2==0){
									trClass="lineO";
								}
							%>
							<tr class="<%=trClass%>">
							   <td class="td_text_center" width="2%"><%=item.getNo() %></td>
							   <td class="td_text_center" width="6%"><%=item.getCustomerId() %></td>
								<td class="td_text" width="8%"><%=item.getCustomerCode() %></td>
								<td class="td_text" width="12%"><%=item.getCustomerName() %></td>
								<td class="td_text_center" width="5%"><%=item.getOrderId() %></td>
								<td class="td_text_center" width="9%"><%=item.getArInvoiceNo() %></td>
								<td class="td_number" width="9%"><%=item.getTotalAmount() %></td>
								<td class="td_number" width="9%"><%=item.getVatAmount() %></td>
								<td class="td_number" width="10%"><%=item.getNetAmount() %></td>
								<td class="td_number" width="10%"><%=item.getPaidAmount() %></td>
								<td class="td_number" width="10%"><%=item.getRemainAmountCalc() %></td>
								<%-- <td class="td_number" width="10%"><%=item.getRemainAmount() %></td> --%>
								
							</tr>
							<%} %>
						</table>
                    </c:if>
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
</html>