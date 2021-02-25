<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%

User user = (User) request.getSession().getAttribute("user");
String role = user.getType();
List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

String isAdd = request.getSession(true).getAttribute("isAdd") != null ? (String)request.getSession(true).getAttribute("isAdd") : "Y";
//System.out.println(isAdd);
%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>" /></title>
<style type="text/css">
</style>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/table_mobile_style.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/salesOrder.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript">
	function loadMe() {
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateFrom'));
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateTo'));
	}
</script>
</head>
<body class="sb-nav-fixed" onload="loadMe()">

     <!-- Include Header Mobile  -->
     <jsp:include page="../header.jsp"  flush="true"/>
     <!-- /Include Header Mobile -->
       
   	<!-- PROGRAM HEADER -->
     	<jsp:include page="../program.jsp">
		<jsp:param name="function" value="SalesOrder" />
		<jsp:param name="code" value=""/>
	</jsp:include>
    
	<!-- BODY --> 
	<html:form action="/jsp/saleOrderAction">
	<jsp:include page="../error.jsp" />
		<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
			<tr>
				<c:if test="${orderForm.order.priceListId!=0}">
				<td colspan="4" align="left">
					<a href="#" onclick="prepare('${pageContext.request.contextPath}','add')">
					<img border=0 src="${pageContext.request.contextPath}/icons/doc_add.gif" align="absmiddle">
					&nbsp;<bean:message key="CreateNewRecord" bundle="sysprop" /></a>
				</td>
				</c:if>
				<c:if test="${orderForm.order.priceListId==0}">
				<td colspan="4" align="center">
					<span class="errormsg">${orderForm.order.pricelistLabel}</span>
					<html:hidden property="order.pricelistLabel"/>
				</td>
				</c:if>
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
				<bean:message key="Customer" bundle="sysele" />&nbsp;&nbsp; <%}else{ %>
				<bean:message key="Member" bundle="sysele" />&nbsp;&nbsp; <%} %>
				</td>
				<td colspan="3"><html:text property="order.customerName"
					size="65" readonly="true" styleClass="disableText" /> <html:hidden
					property="order.customerId" /></td>
			</tr>
			<tr>
				<td align="right"><bean:message key="DocumentNo"
					bundle="sysele" />&nbsp;&nbsp;</td>
				<td align="left"><html:text property="order.orderNo" size="20" /></td>
			</tr>
			<tr>
				<td align="right"><bean:message key="TransactionDate"
					bundle="sysele" /> <bean:message key="DateFrom" bundle="sysele" />&nbsp;&nbsp;
				</td>
				<td align="left"><html:text property="order.orderDateFrom"
					maxlength="10" size="15" readonly="true" styleId="orderDateFrom" />
				</td>
				<td align="right"><bean:message key="DateTo" bundle="sysele" />&nbsp;&nbsp;</td>
				<td align="left"><html:text property="order.orderDateTo"
					maxlength="10" size="15" readonly="true" styleId="orderDateTo" />
				</td>
			</tr>
			<tr>
				<td align="right"><bean:message key="Order.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
				<td align="left"><html:text property="order.salesOrderNo"
					size="20" /></td>
				<td align="right"><bean:message key="Bill.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
				<td align="left"><html:text property="order.arInvoiceNo"
					size="20" /></td>
			</tr>
			<tr>
				<td align="right"><bean:message key="Status" bundle="sysele" />&nbsp;&nbsp;</td>
				<td align="left"><html:select property="order.docStatus">
					<html:option value=""></html:option>
					<html:options collection="docstatus" property="key"
						labelProperty="name" />
				</html:select></td>
			</tr>
		</table>
		<br>
		<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
			<tr>
				<td align="center">
				
					<input type="button" value="ค้นหา" class="newPosBtn" onclick="search('${pageContext.request.contextPath}')">
					<input type="button" value="Clear" class="newNegBtn" onclick="clearForm('${pageContext.request.contextPath}')">
					
				</td>
			</tr>
		</table>
		<c:if test="${orderForm.results != null}">
		 <%
			  int totalPage = orderForm.getTotalPage();
			   int totalRecord = orderForm.getTotalRecord();
			   int currPage =  orderForm.getCurrPage();
			   int startRec = orderForm.getStartRec();
			   int endRec = orderForm.getEndRec();
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
					<th ><bean:message key="No" bundle="sysprop" /></th>
					<th ><bean:message key="DocumentNo" bundle="sysele" /></th>
					<th ><bean:message key="TransactionDate" bundle="sysele" /></th>
					<th ><bean:message key="TotalAmount" bundle="sysele" /></th>
					<th ><bean:message key="Exported" bundle="sysele"/></th>
					<th ><bean:message key="Interfaces" bundle="sysele"/></th>
					<th ><bean:message key="Order.Paid" bundle="sysele"/></th>
					<th ><bean:message key="Order.No"  bundle="sysele"/></th>
					<th ><bean:message key="Bill.No"  bundle="sysele"/></th>
					<th ><bean:message key="Status" bundle="sysele" /></th>
					<th ><bean:message key="Order.Search.CreditNote" bundle="sysprop" /></th>
					<th >ทำรายการ</th>
					<th ><bean:message key="View" bundle="sysprop" /></th>
				</tr>
			   </thead>
		     <%
				Order[] orderList = orderForm.getResults();
				for(int i=0;i<orderList.length;i++){
					Order item = orderList[i];
				%>
				<tr class="">
					<td width="36px;"><%=(i+1) %></td>
					<td align="center" width="88px;"><%=item.getOrderNo() %></td>
					<td align="center" width="88px;"><%=item.getOrderDate() %></td>
					<td align="right" width="62px;"><%=item.getNetAmount() %></td>
					<td align="center" width="52px;">
						 <%if(Utils.isNull(item.getExported()).equalsIgnoreCase("Y")){ %>
							<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
						<%} %>
					</td>
					<td align="center" width="52px;">
						 <%if(Utils.isNull(item.getInterfaces()).equalsIgnoreCase("Y")){ %>
							<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
						<%} %>
					</td>
					<td align="center" width="50px;">
						 <%if(Utils.isNull(item.getPayment()).equalsIgnoreCase("Y")){ %>
					     	<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
						<%} %>
					</td>
					<td align="center" width="88px;"><%=item.getSalesOrderNo() %></td>
					<td align="center" width="75px;"><%=item.getArInvoiceNo() %></td>
					<td align="center" width="50px;"><%=item.getDocStatusLabel() %></td>
					<td align="center">
						<%if(item.isHasCreditNote()){ %>
							<a href="#" onclick="javascript:creditNoteList('${pageContext.request.contextPath}','<%=item.getArInvoiceNo()%>');">
							<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
						<%} %>
					</td>
					
					<td align="center" width="52px;">
					  <%if(Utils.isNull(item.getExported()).equalsIgnoreCase("N")
						 && Utils.isNull(item.getDocStatus()).equalsIgnoreCase("SV")){ %>
								<%if(Utils.isNull(item.getPayment()).equalsIgnoreCase("N")
									 && item.isPromotionSP()){ %>
	                               <a href="#" onclick="javascript:prepareEditOrderSP('${pageContext.request.contextPath}','edit','<%=item.getId()%>');">
							          <img border=0 src="${pageContext.request.contextPath}/icons/process.gif"></a>
							      <%}else{ %>
									 <a href="#" onclick="javascript:prepareEditOrder('${pageContext.request.contextPath}','edit','<%=item.getId()%>');">
							          <img border=0 src="${pageContext.request.contextPath}/icons/process.gif"></a>
						<%        }
					     }
						%>
							
						<%if(role.equals(User.VAN) && Utils.isNull(item.getExported()).equalsIgnoreCase("N")
							&& Utils.isNull(item.getDocStatus()).equalsIgnoreCase("SV") 
							&& Utils.isNull(item.getPayment()).equalsIgnoreCase("N")
							){ %>
								
							  <a href="#" onclick="javascript:prepareEditReceipt('${pageContext.request.contextPath}','edit','<%=item.getId()%>');">
						      <img border=0 src="${pageContext.request.contextPath}/icons/process.gif" title="รายการนี้ยังไม่ทำรายการรับเงิน"></a>						   
								
					    <% } %>
					</td>
					<td align="center">
				       <%if(item.isPromotionSP()){ %>
                             <a href="#" onclick="javascript:prepareSP('${pageContext.request.contextPath}','view','<%=item.getId()%>');">
					         <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
						<%}else{ %>
						     <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','<%=item.getId()%>');">
					         <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
						<%} %>
					</td>
				</tr>
			<%} %>
				<tr>
					<td align="left" class="footer" colspan="13">&nbsp;</td>
				</tr>
			 </table>
		   </div>
		</c:if>
		<br>
		<!-- BUTTON -->
		<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
			<tr>
				<td align="right">
				   <input type="button" value="ปิดหน้าจอ" class="newNegBtn" onclick="backToCusotmer('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
				</td>
				<td width="10%">&nbsp;</td>
			</tr>
		</table>
		<html:hidden property="order.roundTrip"/>
		<html:hidden property="order.priceListId"/>
		<html:hidden property="order.orderType" value="<%=role %>" />
	</html:form> 
	<!-- BODY -->
							
    <!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->	
    	
</body>
</html>