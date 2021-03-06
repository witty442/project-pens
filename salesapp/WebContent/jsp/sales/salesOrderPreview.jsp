<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.web.sales.OrderForm"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.bean.OrgRuleBean"%>
<%@page import="com.isecinc.pens.model.MOrgRule"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();

String action = (String)request.getParameter("action");
if(action == null){
	action = "";
}

List<Address> address = new ArrayList<Address>();
address = new MAddress().lookUp(orderForm.getOrder().getCustomerId());
pageContext.setAttribute("addresses",address,PageContext.PAGE_SCOPE);

List<References> vatcodes = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatcodes",vatcodes,PageContext.PAGE_SCOPE);

List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

List<References> paymentTerm = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_TERM);
pageContext.setAttribute("paymentTerm",paymentTerm,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);

List<References> w1List = new MOrgRule().getW1RefList("","");
pageContext.setAttribute("w1List",w1List,PageContext.PAGE_SCOPE);

List<References> vanPaymentMethod = InitialReferences.getReferenes().get(InitialReferences.VAN_PAYMENT_METHOD);
pageContext.setAttribute("vanPaymentMethod",vanPaymentMethod,PageContext.PAGE_SCOPE);

//Filter Can Receipt More Cash //1:can, 0: cannot, -1:no pay prev bill
OrderForm orderFrom = null;
String receiptCreditFlag = "0";
if(request.getAttribute("orderForm") != null){
  orderFrom = (OrderForm)request.getAttribute("orderForm");
  receiptCreditFlag = Utils.isNull(orderFrom.getReceiptCreditFlag());
}

System.out.println("Message:"+request.getAttribute("Message"));

String saveBtnDisable = "";
String saveBtnStyle ="newPosBtn";
if(request.getAttribute("do_not_save") != null){
	saveBtnDisable = "disabled";
	//saveBtnStyle ="newPosBtnDisable";
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/salesOrder.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/salesOrderProduct.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
 
<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript">
function loadMe(){
	calculatePrice();	
	
	//new Epoch('epoch_popup','th',document.getElementById('orderDate'));
	<%if(request.getAttribute("Message") != null){%>
	  alert("<%=request.getAttribute("Message")%>");
	<%} %>
}

function printListOrderProductReport(path,userType){
	var customerId = document.getElementsByName("order.customerId");
   // window.open(path + "/jsp/saleOrderAction.do?do=printListOrderProductReport&customerId="+customerId[0].value, "Print2", "width=100,height=100,location=No,resizable=No");
	window.open(path + "/jsp/pop/printPopup.jsp?report_name=list_order_product&customerId="+customerId[0].value, "Print2", "width=100,height=100,location=No,resizable=No");
}

function stampPrint(){
	var printDateTimePick = document.getElementsByName("order.printDateTimePick")[0];
	var printCountPick = document.getElementsByName("order.printCountPick")[0];
	
	printCountPick.value = parseInt(printCountPick.value)+1;
	var d = new Date(); 
	var dd = d.getDate();
	    dd = dd.toString().length==1?"0"+dd:dd;
	var MM = (d.getMonth()+1);
	    MM = MM.toString().length==1?"0"+MM:MM;
	var year = (d.getFullYear()+543).toString();
	var hours = (d.getHours()).toString();
	    hours = hours.toString().length==1?"0"+hours:hours;
	var minite = (d.getMinutes()).toString();
	    minite = minite.toString().length==1?"0"+minite:minite;
	    
	var currentDateStr = dd+""+MM+""+year+""+hours+""+minite;
	//alert(currentDateStr);
	printDateTimePick.value = currentDateStr;
}

function validateVanPaymentMethod(){
	var r = true;
	var vanPaymentMethod = document.getElementsByName("order.vanPaymentMethod")[0];
	if(vanPaymentMethod.value =='CREDIT'){
		<%if(receiptCreditFlag.equalsIgnoreCase("0")){%>
		     document.getElementById("error-dialog-content").innerHTML  = "��ҹ��ҹ�� �ѧ������Դ�Է��� ��������� �� ��س��駷ҧ���Ѵ������ͫػ���� ��������ͧ�Դ�Է����â������";
		     $("#brand-dialog").modal({backdrop: 'static', keyboard: true,show:true}) ; 
		     r = false;
		<%}if(receiptCreditFlag.equalsIgnoreCase("-1")){%>
		    document.getElementById("error-dialog-content").innerHTML  = "��ҹ��ҹ�� �ѧ�պ����� ����ѧ��������Թ  ��ͧ���Թ�����ҡ�͹ �֧����ö�Դ��Ţ����������";
		    $("#brand-dialog").modal({backdrop: 'static', keyboard: true,show:true}) ; 
	        r = false;
		<%}%>
	}
	return r;
}
function validateVanCreditLimit(){
	var r = true;
	var vanPaymentMethod = document.getElementsByName("order.vanPaymentMethod")[0];
	var custCreditLimit = document.getElementsByName("custCreditLimit")[0];
	var netAmount = document.getElementsByName("order.netAmount")[0];
	if(vanPaymentMethod.value =='CREDIT'){
		//alert("creditLimit["+custCreditLimit.value+"]:netAmount["+netAmount.value+"]") ;
		if(parseFloat(netAmount.value) > parseFloat(custCreditLimit.value)){
		   //alert("creditLimit["+parseFloat(custCreditLimit.value)+"]:netAmount["+parseFloat(netAmount.value)+"]") ;
		   
		   document.getElementById("error-dialog-content").innerHTML  = "�ʹ�Թ����� �Թǧ�Թ����˹� �������ö����¡�â���� ǧ�Թ�������ͧ��ҹ��� ("+custCreditLimit.value+")";
		   $("#brand-dialog").modal({backdrop: 'static', keyboard: true,show:true}) ; 
		   r = false;
		}
	}
	return r;
}

</script>
</head>
<body  class="sb-nav-fixed" onload="loadMe();">

   <!-- Include Header Mobile  -->
     <jsp:include page="../header.jsp"  flush="true"/>
    <!-- /Include Header Mobile -->
    
   	<!-- PROGRAM HEADER -->
     <jsp:include page="../program.jsp">
		<jsp:param name="function" value="SalesOrder"/>
		<jsp:param name="code" value="${orderForm.order.orderNo}"/>
	</jsp:include>
	
	<!-- BODY -->
	<html:form action="/jsp/saleOrderAction">
	<jsp:include page="../error.jsp"/>
			<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
				<%if(User.TT.equals(user.getType())){%>
				<tr>
					<td width="30%" align="right">�к��Ţ��� PO �١���(�����) </td>
					<td width="25%"><html:text property="order.poNumber" size="20"  readonly="true" styleClass="disableText"/></td>
					<td width="15%"></td>
					<td></td>
				</tr>
				<%}else{ %>
				    <tr>
						<td width="30%" align="right"></td>
						<td width="25%"></td>
						<td width="15%"></td>
						<td></td>
					</tr>
				 <%} %>
				<tr>
					<td align="right"></td>
					<td align="left"></td>
					<td align="right"><bean:message key="DocumentNo" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:text property="order.orderNo" size="20" readonly="true" styleClass="disableText"/>
					</td>
				</tr>
				<tr>
					<td></td><td></td>
					<td align="right"><bean:message key="TransactionDate" bundle="sysele"/><font color="red">*</font></td>
					<td align="left">
						<html:text property="order.orderDate" maxlength="10" size="15" readonly="true" styleId="orderDate" styleClass="disableText"/>
					</td>
				</tr>
				<tr>
					<td align="right">
						<bean:message key="Customer" bundle="sysprop"/>&nbsp;&nbsp;
					</td>
					<td align="left" colspan="3">
						<html:text property="order.customerName" size="80" readonly="true" styleClass="disableText"/>
						<html:hidden property="order.customerId"/>
					</td>
				</tr>
				<tr>
					<td align="right"><bean:message key="Order.DeliveryAddress" bundle="sysele"/><font color="red">*</font></td>
					<td align="left" colspan="3">
						<html:select property="order.shipAddressId" style="width:80%" disabled="true" styleClass="disableText">
							<html:options collection="addresses" property="id" labelProperty="lineString"/>
						</html:select>
						<html:hidden property="order.shipAddressId"/>
					</td>
				</tr>
				<tr>
					<td align="right"><bean:message key="Order.DeliveryDocAddress" bundle="sysele"/><font color="red">*</font></td>
					<td align="left" colspan="3">
						<html:select property="order.billAddressId" style="width:80%" disabled="true" styleClass="disableText">
							<html:options collection="addresses" property="id" labelProperty="lineString"/>
						</html:select>
						<html:hidden property="order.billAddressId"/>
					</td>
				</tr>
				<tr>
					<td colspan="4" align="center">
					<div class="table-responsive">
					 <table id="tblProduct" class="table table-bordered table-striped table-light">
					    <thead class="thead-dark">
						<tr>
							<th ><bean:message key="No" bundle="sysprop"/></th>
							<td style="display: none;"></td>
							<th><bean:message key="Product.Name" bundle="sysele"/></th>
							<th><bean:message key="Product.UOM" bundle="sysele"/></th>
							<th><bean:message key="Quantity" bundle="sysele"/></th>
							<th><bean:message key="Price.Unit" bundle="sysele"/></th>
							<th><bean:message key="Total" bundle="sysele"/></th>
							<th><bean:message key="Discount" bundle="sysele"/></th>
							<th><bean:message key="TotalExcludeDiscount" bundle="sysele"/></th>
							<th>����</th>
							<th><bean:message key="Promotion" bundle="sysele"/></th>
						</tr>
						</thead>
						<c:forEach var="lines1" items="${orderForm.lines}" varStatus="rows1">
						<c:choose>
							<c:when test="${rows1.index %2 == 0}">
								<c:set var="tabclass" value="lineO"/>
							</c:when>
							<c:otherwise>
								<c:set var="tabclass" value="lineE"/>
							</c:otherwise>
						</c:choose>
						<tr class="${tabclass}">
							<td>${rows1.index + 1}</td>
							<td style="display: none;">${lines1.lineNo}</td>
							<td align="left">
								${lines1.product.code}&nbsp;${lines1.product.name}
								<input type="hidden" name='lines.id' value='${lines1.id}'>
								<input type='hidden' name='lines.row' value='${lines1.lineNo}'>
								<input type='hidden' name='lines.productId' value='${lines1.product.id}'>
								<input type='hidden' name='lines.product' value='${lines1.product.code}'>
								<input type='hidden' name='lines.productLabel' value='${lines1.product.name}'>
								
								<input type='hidden' name='lines.uom' value='${lines1.uom.id}'>
								<input type='hidden' name='lines.uom1' value='${lines1.uom1.id}'>
								<input type='hidden' name='lines.uom2' value='${lines1.uom2.id}'>
								<input type='hidden' name='lines.uomLabel' value='${lines1.uom.code}'>
								<input type='hidden' name='lines.uomLabel1' value='${lines1.uom1.code}'>
								<input type='hidden' name='lines.uomLabel2' value='${lines1.uom2.code}'>
								<input type='hidden' name='lines.fullUom' value='${lines1.fullUom}'>
								<input type='hidden' name='lines.price' value='${lines1.price}'>
								<input type='hidden' name='lines.price1' value='${lines1.price1}'>
								<input type='hidden' name='lines.price2' value='${lines1.price2}'>
								<input type='hidden' name='lines.qty' value='${lines1.qty}'>
								<input type='hidden' name='lines.qty1' value='${lines1.qty1}'>
								<input type='hidden' name='lines.qty2' value='${lines1.qty2}'>
								<input type="hidden" name='lines.amount' value='${lines1.lineAmount}'>
								<input type="hidden" name='lines.amount1' value='${lines1.lineAmount1}'>
								<input type="hidden" name='lines.amount2' value='${lines1.lineAmount2}'>
								<input type='hidden' name='lines.disc' value='${lines1.discount}'>
								<input type='hidden' name='lines.disc1' value='${lines1.discount1}'>
								<input type='hidden' name='lines.disc2' value='${lines1.discount2}'>
								<input type='hidden' name='lines.afdisc'>
								<input type='hidden' name='lines.afdisc1'>
								<input type='hidden' name='lines.afdisc2'>
								<input type="hidden" name='lines.vat' value='${lines1.vatAmount}'>
								<input type='hidden' name='lines.vat1' value='${lines1.vatAmount1}'>
								<input type='hidden' name='lines.vat2' value='${lines1.vatAmount2}'>
								<input type='hidden' name='lines.total' value='${lines1.totalAmount}'>
								<input type='hidden' name='lines.total1' value='${lines1.totalAmount1}'>
								<input type='hidden' name='lines.total2' value='${lines1.totalAmount2}'>
								
								<input type='hidden' name='lines.ship' value='${lines1.shippingDate}'>
								<input type='hidden' name='lines.req' value='${lines1.requestDate}'>
								<input type='hidden' name='lines.promo' value='${lines1.promotion}'>
								<input type='hidden' name='lines.lineno' value='${lines1.lineNo}'>
								<input type='hidden' name='lines.tripno' value='${lines1.tripNo}'>
								<input type='hidden' name='lines.taxable' value='${lines1.taxable}'>
							</td>
							<td align="center">
						        ${lines1.fullUom}
							</td>
							<td align="right">
								
								<c:choose>
									<c:when test="${lines1.promotion=='Y'}">
										<c:choose>
											<c:when test="${(lines1.product.uom.id==lines1.uom1.id && lines1.product.uom.id==lines1.uom2.id) || (lines1.product.uom.id!=lines1.uom1.id && lines1.product.uom.id!=lines1.uom2.id)}">
												<fmt:formatNumber pattern="#,##0" value="${lines1.qty1 + lines1.qty2}"/>
											
											</c:when>
											<c:otherwise>
												<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>/
												<fmt:formatNumber pattern="#,##0" value="${lines1.qty2}"/>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>/
										<fmt:formatNumber pattern="#,##0" value="${lines1.qty2}"/>												
									</c:otherwise>
								</c:choose>
								
							</td>
							<td align="right">
								
								<c:choose>
									<c:when test="${lines1.promotion=='Y'}">
										<fmt:formatNumber pattern="#,##0.00000" value="0"/>
									</c:when>
									<c:otherwise>
										<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price1}"/>/
										<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price2}"/>												
									</c:otherwise>
								</c:choose>											
							
							</td>
							<td align="right">
								<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount}"/>
							</td>
							<td align="right">
								<fmt:formatNumber pattern="#,##0.00000" value="${lines1.discount}"/>
							</td>
							<td align="right">
								<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount - lines1.discount}"/>
							</td>
							<td align="center">
								<c:if test="${lines1.taxable=='Y'}">
									<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
								</c:if>
							</td>
							<td align="center">
								<c:if test="${lines1.promotion=='Y'}">
									<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
								</c:if>
							</td>
						</tr>
						</c:forEach>
					  </table>
					</div>
					<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
						<tr>
							<td align="left" colspan="12" class="footer">&nbsp;</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td></td><td></td>
					<td align="right">�ʹ�����͹���� &nbsp;&nbsp;</td>
					<td align="left">
						<input type="text" id="tempTotalAmount" name="tempTotalAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
						<html:hidden property="order.totalAmount"/>
					</td>
				</tr>
				<tr>
					<td></td><td></td>
					<td align="right"><bean:message key="Tax" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<input type="text" id="tempVatAmount" name="tempVatAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
						<html:hidden property="order.vatAmount"/>
					</td>
				</tr>
				<tr>
					<td></td><td></td>
					<td align="right">�ʹ�����������������&nbsp;&nbsp;</td>
					<td align="left">
						<input type="text" id="tempTotalAmountNonVat" name="tempTotalAmountNonVat" readonly="readonly" class="disableText" style="text-align: right;"/>
						<html:hidden property="order.totalAmountNonVat"/>
					</td>
				</tr>
				<tr>
					<td></td><td></td>
					<td align="right"><bean:message key="Net" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<input type="text" id="tempNetAmount" name="tempNetAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
						<html:hidden property="order.netAmount"/>
					</td>
				</tr>
				<tr>
					<td colspan="4"><hr/></td>
				</tr>
				<tr>
					<td></td>
					<%if(User.VAN.equals(user.getType())){%>
						<td class="textSpecial">
							     ������
							 <html:select property="order.vanPaymentMethod">
								<html:options collection="vanPaymentMethod" property="key" labelProperty="name"/>
							</html:select>
					   </td>
					<%}else if(User.TT.equals(user.getType())){%>	
				       <html:hidden property="order.vanPaymentMethod"/>
					<!-- New Issue 15/05/2555 Add Bill Place -->
					<td nowrap>
						    ʶҹ����͡�Թ���/������� :
						    <html:select property="order.placeOfBilled"  styleClass="normalText">
								<html:options collection="w1List" property="key" labelProperty="name"/>
							</html:select>
							</td>
						 <%}else{ %>
						 <td></td>
						<%} %>
					<td></td>
					<td valign="top">
						<html:checkbox property="order.payment" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Order.Paid" bundle="sysele"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td valign="top">
						<html:checkbox property="order.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td valign="top">
						<html:checkbox property="order.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
					</td>
				</tr>
				<tr>
					<td align="right"><bean:message key="Order.No" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:text property="order.salesOrderNo" size="20" readonly="true" styleClass="disableText"/>
					</td>
					<td align="right"><bean:message key="Bill.No" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:text property="order.arInvoiceNo" size="20" readonly="true" styleClass="disableText"/>
					</td>
				</tr>
				<tr>
					<td align="right">Sales Representative&nbsp;&nbsp;</td>
					<td align="left">
						<html:text property="order.salesRepresent.name" size="30" readonly="true" styleClass="disableText"/>
						<html:hidden property="order.salesRepresent.id"/>
						<html:hidden property="order.salesRepresent.code"/>
					</td>
					<td align="right"><bean:message key="Status" bundle="sysele"/><font color="red">*</font></td>
					<td valign="top">
						<html:select property="order.docStatus" disabled="true" styleClass="disableText">
							<html:options collection="docstatus" property="key" labelProperty="name"/>
						</html:select>
						<html:hidden property="order.docStatus"/>
					</td>
				</tr>
			</table>
			<br>
			<!-- BUTTON -->
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="center">
						<input type="button" value="����Թ���" class="newPosBtn" onclick="return backadd('${pageContext.request.contextPath}');">
						<!-- WIT EDIT:04/08/2554 ***************-->
<!--									<a href="#" onclick="return autoReceipt('${pageContext.request.contextPath}','<%=user.getType() %>');">-->
<!--									    <input type="button" value="�ѹ�֡" class="newPosBtn">-->
<!--									</a>-->
						<!-- WIT EDIT:04/08/2554 ***************-->

						<!-- OLD CODE ************************ -->
						<% if(request.getAttribute("do_not_save") == null){%>
						   <input type="button" value="�ѹ�֡" class="<%=saveBtnStyle %>" onclick="save('${pageContext.request.contextPath}','<%=user.getType() %>')" <%=saveBtnDisable %>>
						<%} %>
						<!-- OLD CODE ************************ -->
						<%if(User.VAN.equals(user.getType())){%>
						 
						   <input type="button" value="��������Ժ�ͧ" class="newPosBtnLong" onclick="stampPrint();printListOrderProductReport('${pageContext.request.contextPath}','<%=user.getType() %>')">
						   
						<%} %>		
						<input type="button" value="¡��ԡ" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
						
					</td>
				</tr>
			</table>
			<span title="SalesOrderPreview">...</span>
			<span title="<%=Utils.isNull(request.getSession().getAttribute("PREV_STEP_ORDER_ACTION")) %>">...</span>
			
			<!-- Hidden Field Fix Value -->
			<html:hidden property="order.salesOrderNo" />
			<html:hidden property="order.arInvoiceNo"/>
			<html:hidden property="order.docStatus" />
			<html:hidden property="order.salesRepresent.name"/>
			<html:hidden property="order.salesRepresent.id"/>
			<html:hidden property="order.salesRepresent.code"/>
			<html:hidden property="order.salesOrderNo" />
			<html:hidden property="order.arInvoiceNo"/>
			
			<html:hidden property="order.priceListId" styleId="order.priceListId"/>
			<html:hidden property="order.vatCode"/>
			<html:hidden property="order.paymentTerm"/>
			<html:hidden property="order.paymentMethod"/>
			<html:hidden property="order.oraBillAddressID"/>
			<html:hidden property="order.oraShipAddressID"/>
			
			<!-- AUTO RECEIPT -->
			<html:hidden property="autoReceiptFlag"/>
			<html:hidden property="autoReceipt.paymentMethod"/>
			<html:hidden property="autoReceipt.bank"/>
			<html:hidden property="autoReceipt.chequeNo"/>
			<html:hidden property="autoReceipt.chequeDate"/>
			<html:hidden property="autoReceipt.creditCardType"/>
			<html:hidden property="autoReceipt.internalBank"/>
			<!--  -->
			<!--  Can Receipt Credit (VAN)-->
			<html:hidden property="receiptCreditFlag"/>
		    <html:hidden property="custCreditLimit"/>
		
			<html:hidden property="deletedId"/>
			<html:hidden property="order.orderType"/>
			<html:hidden property="order.id"/>
			<html:hidden property="order.customerId"/>
			<html:hidden property="order.exported"/>
			<html:hidden property="order.isCash"/>
			
			<html:hidden property="order.printDateTimePick"/>
			<html:hidden property="order.printCountPick"/>
			
			<!-- ForTest -->
		 	<%-- printDateTimePick:<html:text property="order.printDateTimePick"/><br/>
			printCountPick:<html:text property="order.printCountPick"/> --%>
			
			<input type="hidden" name="memberVIP" value="${memberVIP}"/>
			<div id="productList" style="display: none;"></div>
			<div id="ByList" style="display: none;"></div>
	</html:form>
		
	<!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->		
    
    <!-- Product-Content -->
     <div  class="modal fade" id="error-dialog" role="dialog" >
       <div class="modal-dialog-full-width modal-dialog modal-fluid">
	      <div class="modal-content-full-width modal-content">
	        <div class=" modal-header-full-width  modal-header text-center">
	           <button type="button" class="btn btn-default" data-dismiss="modal" onclick='addProductToSalesOrder()'>OK</button>
	           <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	           
	           <button type="button" class="close" data-dismiss="modal">&times;</button>
	        </div>
	        <div class="modal-body">
	            <div id="error-dialog-content">
	               Content By Code
	            </div>
	        </div>
	        <div class="modal-footer-full-width  modal-footer text-center">
	          <button type="button" class="btn btn-default" data-dismiss="modal" onclick='addProductToSalesOrder()'>OK</button>
	          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        </div>
	      </div>
	    </div>
    </div> 	
</body>
</html>

 <!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->

