<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="orderThreeMonthsReportForm" class="com.isecinc.pens.web.report.salesorder.OrderThreeMonthsReportForm" scope="request" />
<%

%>

<%@page import="com.isecinc.pens.SystemProperties"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>" /></title>
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
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe() {
	new Epoch('epoch_popup','th',document.getElementById('dateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('dateTo'));
}

function search(path){
	if(document.getElementById('msg')!=null){
		document.getElementById('msg').innerHTML = '';
	}
	document.orderThreeMonthsReportForm.action = path + "/jsp/orderThreeMonthsReportAction.do?do=search&rf=Y";
	document.orderThreeMonthsReportForm.submit();
	return true;
}

function clearForm(path){
	document.orderThreeMonthsReportForm.action = path + "/jsp/orderThreeMonthsReportAction.do?do=clearForm";
	document.orderThreeMonthsReportForm.submit();
	return true;
}

function viewProduct(path,custId,userId,from,to){
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/ThreeMonthsReportQuery.jsp",
			data : "customerId=" + custId
			+ "&userId=" +userId
			+ "&datefrom=" +from
			+ "&dateto=" +to
			,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				//document.getElementById('tdp_'+custId).innerHTML = returnString;
				document.getElementById('trd_product').innerHTML = returnString;
				
			}
		}).responseText;
	});
	
	if(document.getElementById("product_"+custId).style.display==''){
		document.getElementById("product_"+custId).style.display='none';
	}else{
		document.getElementById("product_"+custId).style.display='';
	}
	
}

function loadCustomer(e){
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/customerQuery.jsp",
				data : "cCode=" + $('#cCode').val() + "&uId=" + $('#userId').val(),
				success: function(getData){
					var returnString = jQuery.trim(getData);
					$('#cId').val(returnString.split('||')[0]);
					$('#cName').val(returnString.split('||')[1]);
				}
			}).responseText;
		});
	}
}

//Lookup customer
function showMainCustomer(path){
	window.open(path + "/jsp/pop/view/customerViewPopup.jsp?uId="+ $('#userId').val(), "Customer List", "width=500,height=350,location=No,resizable=No");
}

function setMainCustomer(code, name){
	$('#cCode').val(code);
	$('#cName').val(name);
	loadCustomer(null);
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
				<jsp:param name="function" value="OrderHistoryReport" />
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
						<html:form action="/jsp/orderThreeMonthsReportAction">
						<jsp:include page="../error.jsp" />
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="45%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Customer.Name" bundle="sysele"/><bean:message key="Customer" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
								    <html:text property="orderThreeMonths.customerCode" styleId="cCode" onkeyup="loadCustomer(event)" size="15"/>&nbsp;
								    <html:hidden property="orderThreeMonths.customerId" styleId="cId"/>
									<a href="#" onclick="showMainCustomer('${pageContext.request.contextPath}');" id="lookCustomer">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"/></a>
								</td>
							</tr>
							<tr>
								<td align="right"></td>
								<td align="left">
									<html:text property="orderThreeMonths.customerName" styleId="cName" size="40" readonly="true" styleClass="disableText"/>&nbsp;
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="DateFrom" bundle="sysele"/></td>
								<td align="left">
									<html:text property="orderThreeMonths.dateFrom" styleId="dateFrom" readonly="true" size="15"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="DateTo" bundle="sysele"/></td>
								<td align="left">
									<html:text property="orderThreeMonths.dateTo" styleId="dateTo" readonly="true" size="15"/>
								</td>
							</tr>
						</table>
						<br>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="javascript:search('${pageContext.request.contextPath}')">
									<!--<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ค้นหา" class="newPosBtn">
									</a> 
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<!--<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">-->
									<input type="button" value="Clear" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<c:if test="${orderThreeMonthsReportForm.results != null}">
							<br>
							<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%;">
								<tr>
									<td align="left" width="60%" valign="top">
										<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
											<bean:message key="RecordsFound" bundle="sysprop" /> &nbsp;
											<span class="searchResult">${orderThreeMonthsReportForm.criteria.searchResult}</span>&nbsp;
											<bean:message key="Records" bundle="sysprop" />
										</div>
										<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
											<tr>
												<th class="order"><bean:message key="No" bundle="sysprop" /></th>
												<th class="name"><bean:message key="Customer" bundle="sysele" /></th>
												<!-- <th class="code">จำนวนรายการขายทั้งหมด</th> -->
												<th class="status">ยอดเงินรวม</th>
												<th class="status"><bean:message key="Product.Description" bundle="sysele"/></th>
											</tr>
											<c:forEach var="results" items="${orderThreeMonthsReportForm.results}" varStatus="rows">
												<c:choose>
													<c:when test="${rows.index %2 == 0}">
														<c:set var="tabclass" value="lineO" />
													</c:when>
													<c:otherwise>
														<c:set var="tabclass" value="lineE" />
													</c:otherwise>
												</c:choose>
												<tr class="<c:out value='${tabclass}'/>">
													<td align="center"><c:out value='${rows.index+1}' /></td>
													<td align="left"><c:out value='${results.customerName}' /></td>
													<!-- <td align="right"><c:out value='${results.totalOrder}' /></td> -->
													<td align="right"><fmt:formatNumber pattern="#,##0.00" value='${results.totalAmount}' /></td>
													<td align="center">
														<a href="#" onclick="return viewProduct('${pageContext.request.contextPath}','${results.customerId}','${user.id}','${results.dateFrom}','${results.dateTo}');">
														<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
													</td>
												</tr>
											</c:forEach>
											<tr>
											    <td align="right" class="footer_right">&nbsp;</td>
												<td align="right" class="footer_right">ยอดรวมทั้งหมด</td>
												<td align="right" class="footer_right"> <fmt:formatNumber pattern="#,##0.00" value='${orderThreeMonthsReportForm.totalAmount}' /></td>
												<td align="right" class="footer_right">&nbsp;</td>
											</tr>
										</table>
										
									</td>
									<td align="left" id="trd_product" valign="top"></td>
								</tr>
							</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									</a></td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<input type="hidden" id="userId" name="userId" value="${user.id}">
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
  	</tr>
</table>
</body>
</html>