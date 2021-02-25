<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.bean.Receipt"%>
<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.pens.util.SIdUtils"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="receiptForm" class="com.isecinc.pens.web.receipt.ReceiptForm" scope="request"/>
<%
User user = (User) request.getSession().getAttribute("user");
String role = user.getType();
List<References> docstatus= InitialReferences.getReferenesByManual(InitialReferences.DOC_STATUS, "SV,VO");
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_mobile_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/receipt.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('receiptDateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('receiptDateTo'));
}
</script>
</head>
<body class="sb-nav-fixed" onload="loadMe()">
     <!-- Include Header Mobile  -->
     <jsp:include page="../header.jsp"  flush="true"/>
     <!-- /Include Header Mobile -->
     
   	<!-- PROGRAM HEADER -->
     	<jsp:include page="../program.jsp">
		<jsp:param name="function" value="Receipt"/>
		<jsp:param name="code" value=""/>
	</jsp:include>
	

	<!-- BODY -->
	<html:form action="/jsp/receiptAction">
	<jsp:include page="../error.jsp"/>
			<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
				<tr>
					<td colspan="4">
						<%if(!role.equalsIgnoreCase(User.DD)){ %>
						<a href="#" onclick="prepare('${pageContext.request.contextPath}','add')">
						<img border=0 src="${pageContext.request.contextPath}/icons/doc_add.gif" align="absmiddle">&nbsp;<bean:message key="CreateNewRecord" bundle="sysprop"/></a>
						<%} %>
						<%if(role.equalsIgnoreCase(User.DD)){ %>
						<a href="#" onclick="prepareMR('${pageContext.request.contextPath}','add')">
						<img border=0 src="${pageContext.request.contextPath}/icons/doc_add.gif" align="absmiddle">&nbsp;<bean:message key="CreateNewRecord" bundle="sysprop"/></a>
						<%} %>
					</td>
				</tr>
				<tr>
					<td width="35%"></td>
					<td width="17%"></td>
					<td width="12%"></td>
					<td></td>
				</tr>
				<tr>
					<td align="right">
						<%if(!((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
						<bean:message key="Customer" bundle="sysprop"/>&nbsp;&nbsp;
						<%}else{ %>
						<bean:message key="Member" bundle="sysprop"/>&nbsp;&nbsp;
						<%} %>
					</td>
					<td align="left" colspan="3">
						<html:text property="receipt.customerName" size="65" readonly="true" styleClass="disableText"/>
						<html:hidden property="receipt.customerId"/>
					</td>
				</tr>
				<tr>
					<td align="right"><bean:message key="Receipt.No" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left"><html:text property="receipt.receiptNo" size="20"/></td>
				</tr>
				<tr>
					<td align="right"><bean:message key="TransactionDate" bundle="sysele"/><bean:message key="DateFrom" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:text property="receipt.receiptDateFrom" maxlength="10" size="15" readonly="true" styleId="receiptDateFrom" />
					</td>
					<td align="right"><bean:message key="DateTo" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:text property="receipt.receiptDateTo" maxlength="10" size="15" readonly="true" styleId="receiptDateTo" />
					</td>
				</tr>
				<tr>
					<td align="right"><bean:message key="Bill.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left"><html:text property="receipt.searchInvoiceNo" size="20"/></td>
				</tr>
				<tr>
					<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td valign="top">
						<html:select property="receipt.docStatus">
							<html:option value=""></html:option>
							<html:options collection="docstatus" property="key" labelProperty="name"/>
						</html:select>
					</td>
				</tr>
			</table>
			<br>
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="center">
						<input type="button" value="§ÈπÀ“" class="newPosBtn" onclick="search('${pageContext.request.contextPath}')">
						<input type="button" value="Clear" class="newNegBtn" onclick="clearForm('${pageContext.request.contextPath}')">
					</td>
				</tr>
			</table>
			<c:if test="${receiptForm.results != null}">
			<%
			  int totalPage = receiptForm.getTotalPage();
			   int totalRecord = receiptForm.getTotalRecord();
			   int currPage =  receiptForm.getCurrPage();
			   int startRec = receiptForm.getStartRec();
			   int endRec = receiptForm.getEndRec();
			   int no = startRec;
			   boolean mobileFlage = true;
			   user.setMobile(true);
				
			   out.println(PageingGenerate.genPageing(user,totalPage, totalRecord, currPage, startRec, endRec, no)); 
			%>
		    <div class="table-responsive">
              <table class="table table-bordered table-striped table-light"
                   id="dataTable" width="100%" cellspacing="0">
                 <thead class="thead-dark">
				<tr>
					<th><bean:message key="No" bundle="sysprop"/></th>
					<th><bean:message key="Receipt.No" bundle="sysele"/></th>
					<th><bean:message key="TransactionDate" bundle="sysele"/></th>
					<th><bean:message key="Order.Payment" bundle="sysele"/></th>
					<th><bean:message key="Description"  bundle="sysele"/></th>
					<%if(user.getRole().getKey().equalsIgnoreCase(User.VAN)){ %>
					  <th><bean:message key="Order.Paid"  bundle="sysele"/></th>
					<%} %>
					<th><bean:message key="Exported"  bundle="sysele"/></th>
					<th ><bean:message key="Interfaces"  bundle="sysele"/></th>
					<th ><bean:message key="Bill.No"  bundle="sysele"/>/„∫≈¥Àπ’È</th>
					<th><bean:message key="Status" bundle="sysele"/></th>
					<th><bean:message key="Edit" bundle="sysprop"/></th>
					<th><bean:message key="View" bundle="sysprop"/></th>
				</tr>	
			 </thead>
			<%
				Receipt[] receiptList = receiptForm.getResults();
				for(int i=0;i<receiptList.length;i++){
					Receipt item = receiptList[i];
				%>
			<tr class="">
				<td class="td_text_center" width="5%"><%=(i+1) %></td>
				<td class="td_text_center" width="10%"><%=item.getReceiptNo() %></td>
				<td class="td_text_center" width="5%"><%=item.getReceiptDate() %></td>
				<td class="td_text_center" width="5%"><%=item.getReceiptAmount() %></td>
				<td class="td_text_center" width="15%"><%=item.getDescription() %></td>
				<%if(user.getRole().getKey().equalsIgnoreCase(User.VAN)){ %>
				<td class="td_text_center" width="5%">
					<%if(Utils.isNull(item.getPrepaid()).equalsIgnoreCase("Y")){ %>
					 <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
					<%} %>
				</td>
				<%} %>
				<td class="td_text_center" width="5%">
					<%if(Utils.isNull(item.getExported()).equalsIgnoreCase("Y")){ %>
					<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
					<%} %>
				</td>
				<td class="td_text_center" width="5%">
					<%if(Utils.isNull(item.getInterfaces()).equalsIgnoreCase("Y")){ %>
					<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
					<%} %>
				</td>
				<td class="td_text_center" width="10%"><%=item.getInvoiceNoLabel() %></td>
				<td class="td_text_center" width="10%"><%=item.getDocStatusLabel() %></td>
				<td class="td_text_center" width="5%">
				  <%if(Utils.isNull(item.getPrepaid()).equalsIgnoreCase("N")
					&& Utils.isNull(item.getExported()).equalsIgnoreCase("N")
					&& Utils.isNull(item.getDocStatus()).equalsIgnoreCase("SV")
				  ){ %>
				
				  <% } %>
				</td>
				<td class="td_text_center" width="5%">
					<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','<%=item.getId()%>');">
					<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
				</td>
			</tr>
			<%} %>
			</table>
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
				<tr>
					<td align="left" class="footer">&nbsp;</td>
				</tr>
			</table>
			</div>
			</c:if>
			<br>
			
			<html:hidden property="receipt.orderType"/>
		</html:form>
										
    <!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->		
</body>
</body>
</html>