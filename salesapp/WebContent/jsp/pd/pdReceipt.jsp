<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.pens.util.DateToolsUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<jsp:useBean id="pdReceiptForm" class="com.isecinc.pens.web.pd.PDReceiptForm" scope="request" />
<%
User user = (User) request.getSession().getAttribute("user");
String role = user.getType();
List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

String today = DateToolsUtil.getCurrentDateTime("dd/MM/yyyy");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>" /></title>
<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/pdreceipt.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript">
	function loadMe() {
		new Epoch('epoch_popup', 'th', document.getElementById('receiptDateFrom'));
		new Epoch('epoch_popup', 'th', document.getElementById('receiptDateTo'));
	}
</script>
</head>
<body class="sb-nav-fixed" onload="loadMe()">

     <!-- Include Header Mobile  -->
     <jsp:include page="../header.jsp"  flush="true"/>
     <!-- /Include Header Mobile -->
     
	<!-- PROGRAM HEADER --> 
	<jsp:include page="../program.jsp">
		<jsp:param name="function" value="ManageCreditReceipt" />
		<jsp:param name="code" value="" />
	</jsp:include> 
				
	<!-- BODY --> 
	<html:form action="/jsp/pdReceipt">
	<jsp:include page="../error.jsp" />
			<table align="center" border="0" cellpadding="3" cellspacing="0"
				width="60%">
				<tr>
					<td width="35%"></td>
					<td width="17%"></td>
					<td width="12%"></td>
					<td></td>
				</tr>
				<tr>
					<td align="right"><bean:message key="Order.No"
							bundle="sysele" />
						<bean:message key="From" bundle="sysele" />&nbsp;&nbsp;</td>
					<td align="left"><html:text
							property="receipt.orderNoFrom" maxlength="30" size="15"
							styleId="orderNoFrom" /></td>
					<td align="right"><bean:message key="To" bundle="sysele" />&nbsp;&nbsp;</td>
					<td align="left"><html:text property="receipt.orderNoTo"
							maxlength="30" size="15" styleId="orderNoTo" /></td>
				</tr>
				<tr>
					<td align="right"><bean:message key="TransactionDate"
							bundle="sysele" />
						<bean:message key="DateFrom" bundle="sysele" />&nbsp;&nbsp;</td>
					<td align="left"><html:text
							property="receipt.receiptDateFrom" maxlength="10" size="15"
							readonly="true" styleId="receiptDateFrom" /></td>
					<td align="right"><bean:message key="DateTo"
							bundle="sysele" />&nbsp;&nbsp;</td>
					<td align="left"><html:text
							property="receipt.receiptDateTo" maxlength="10" size="15"
							readonly="true" styleId="receiptDateTo" /></td>
				</tr>
			</table>
			<br>
			<table align="center" border="0" cellpadding="3" cellspacing="0"
				class="body">
				<tr>
					<td align="center"><a
						href="javascript:search('${pageContext.request.contextPath}')">
							<input type="button" value="����" class="newPosBtn">
							<!-- <img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn"> -->
					</a> <a
						href="javascript:clearForm('${pageContext.request.contextPath}')">
							<input type="button" value="Clear" class="newNegBtn">
							<!-- <img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn"> -->
					</a>
					<a href="#" onclick="window.close();">
					   <input type="button" value="�Դ˹�Ҩ�" class="newPosBtnLong">
					</a>
					</td>
				</tr>
			</table>
			<c:if test="${pdReceiptForm.pdReceipts != null}">
				<div align="left" class="recordfound">
					&nbsp;&nbsp;&nbsp;
					<bean:message key="RecordsFound" bundle="sysprop" />
					&nbsp; <span class="searchResult">${pdReceiptForm.criteria.searchResult}</span>&nbsp;
					<bean:message key="Records" bundle="sysprop" />
				</div>
				<div class="table-responsive">
                <table class="table table-bordered table-striped table-light"
                   id="dataTable" width="100%" cellspacing="0">
                 <thead class="thead-dark">
					<tr>
						<th>�ӴѺ</th>
						<th>�Ţ�����¡�â��</th>
						<th>�ѹ�����觢��</th>
						<th>�١���</th>
						<th>�ӹǹ�Թ</th>
						<th>�Ըա�ê����Թ</th>
						<th>�ѹ������Թ </th>
						<th>�ѹ���˹���� </th>
					</tr>
					</thead>
					<c:forEach var="results" items="${pdReceiptForm.pdReceipts}"
						varStatus="rows">
						<c:choose>
							<c:when test="${rows.index %2 == 0}">
								<c:set var="tabclass" value="lineO" />
							</c:when>
							<c:otherwise>
								<c:set var="tabclass" value="lineE" />
							</c:otherwise>
						</c:choose>
						<tr class="<c:out value='${tabclass}'/>">
							<td class="td_text_center" width="5%"><c:out value='${rows.index+1}'/></td>
							<td class="td_text_center" width="5%">${results.receiptNo}</td>
							<td class="td_text_center" width="5%">${results.receiptDate}</td>
							<td class="td_text" width="20%">${results.customerName}</td>
							<td class="td_text_center" width="10%"><fmt:formatNumber pattern="#,##0.00" value="${results.receiptAmount}"/></td>
							<td class="td_text_center" width="10%">
								<input name="receiptId" type="hidden" value="${results.id}" />
								<select name="pd.paymentMethod" onchange="defaultDate(this,'${rows.index+1}','<%=today%>')" >
									<option value="">&nbsp;</option>
									<option value="CS"><bean:message key="PaymentMethod.Cash" bundle="sysele" /></option>
									<option value="CH"><bean:message key="PaymentMethod.Cheque" bundle="sysele" /></option>
								</select>
							</td>
							<td class="td_text_center" width="5%">
								<input type="text" name="pdReceiptDate" id="pdReceiptDate" maxlength="10" size="10"  readonly="readonly" >
							</td>
							<td class="td_text" width="10%">
							   <%--  <div id="div_chequeDate_${rows.index+1}" style="display:none"> --%>
								  <input type="text" name="chequeDate" maxlength="10" size="10" id="chequeDate" readonly="readonly" disabled="true">
								<!-- </div> -->
								
							</td>
						</tr>
					</c:forEach>
				</table>
				</div>
				<table align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width="100%">
					<tr>
						<td align="left" class="footer">&nbsp;</td>
					</tr>
				</table>
				<script>
					var pdReceiptDate = document.getElementsByName("pdReceiptDate");
					var chequeDate = document.getElementsByName("chequeDate");
					
					for(var i=0;i<pdReceiptDate.length ; i++){
						new Epoch('epoch_popup', 'th', pdReceiptDate[i]);
						//alert(chequeDate+":"+chequeDate.length);
						if(chequeDate != null && chequeDate[i] != null){
						  new Epoch('epoch_popup', 'th', chequeDate[i]);
						}
					}
				
				</script>
			<br>
			<!-- BUTTON -->
			<table align="center" border="0" cellpadding="3" cellspacing="0"
				width="50%">
				<tr>
					<td align="center">
						<!-- a href="#" onclick="backToCusotmer('${pageContext.request.contextPath}','${receiptForm.receipt.customerId}');" -->
						<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
						<a href="#" onclick="save('${pageContext.request.contextPath}')" >
						<input type="button" value="�ѹ�֡" class="newNegBtn">
						</a>
						<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
						<input type="button" value="¡��ԡ" class="newNegBtn">
						</a></td>
					<td width="10%">&nbsp;</td>
				</tr>
			</table>
			</c:if>
			<!--html:hidden property="receipt.orderType"/-->
			<jsp:include page="../searchCriteria.jsp"></jsp:include>
		</html:form>
	 <!-- BODY -->
	 
	<!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->		
</body>
</body>
</html>
 <!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->