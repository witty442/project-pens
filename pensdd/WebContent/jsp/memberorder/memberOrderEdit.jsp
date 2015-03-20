<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.model.MOrderLine"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String totalAmount = new DecimalFormat("#,##0.00").format(orderForm.getOrder().getTotalAmount());
String vatAmount = new DecimalFormat("#,##0.00").format(orderForm.getOrder().getVatAmount());
String netAmount = new DecimalFormat("#,##0.00").format(orderForm.getOrder().getNetAmount());

double totalNeedBillAmt = orderForm.getOrder().getTotalNeedBill();
pageContext.setAttribute("totalNeedBillAmt",totalNeedBillAmt,PageContext.PAGE_SCOPE);

List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

List<OrderLine> lines = orderForm.getLines();
orderForm.getOrder().getPriceListId();
List<String> trips = new ArrayList<String>(); 
List<String> cannotEditTrips = new ArrayList<String>();
%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.isecinc.pens.bean.OrderLine"%>
<%@page import="com.isecinc.pens.model.MMemberProduct"%>
<%@page import="com.isecinc.pens.bean.MemberProduct"%>
<%@page import="com.isecinc.pens.model.MProductPrice"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--

-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">
function loadMe(){
	$('#tempTotalAmount').val('<%=totalAmount%>');
	$('#tempVatAmount').val('<%=vatAmount%>');
	$('#tempNetAmount').val('<%=netAmount%>');
}

var editSpanObj;
var editTxtObj;
var editIconObj;
var saveIconObj;
var cancelTripNos = new Array();
var cannotEditTripNos = new Array();

function editLine(id,qty){
	
	if(editSpanObj)editSpanObj.show();
	if(editTxtObj)editTxtObj.hide();
	if(editIconObj)editIconObj.show();
	if(saveIconObj)saveIconObj.hide();
	editSpanObj=null;
	editTxtObj=null;
	editIconObj=null;
	saveIconObj=null;

	editSpanObj = $('#qtyLine_'+id);	
	editTxtObj = $('#qtyTextLine_'+id);
	editIconObj = $('#editLine_'+id);
	saveIconObj = $('#saveLine_'+id);
	
	editSpanObj.hide();
	editTxtObj.show();
	editIconObj.hide();
	saveIconObj.show();

	$('#qty_'+id).focus();

}

function saveLine(id){
	var txtQty = $('#qty_'+id);
	//alert(txtQty.val());
	//to Save Line action send id, qty -- recalculate price...
	$('#id').val(id);
	$('#orderLineQty').val(txtQty.val());
	document.orderForm.action = "${pageContext.request.contextPath}/jsp/memberOrderAction.do?do=saveLine";
	document.orderForm.submit();

}

var sdSpanObj;
var sdTxtObj;
var editTripIconObj;
var saveTripIconObj;

var rdSpanObj;
var rdTxtObj;

function edtiTrip(lineId){
	if(sdSpanObj)sdSpanObj.show();
	if(sdTxtObj)sdTxtObj.hide();
	if(rdSpanObj)rdSpanObj.show();
	if(rdTxtObj)rdTxtObj.hide();
	
	if(editTripIconObj)editTripIconObj.show();
	if(saveTripIconObj)saveTripIconObj.hide();
	
	sdSpanObj=null;
	sdTxtObj=null;
	rdSpanObj=null;
	rdTxtObj=null;
	editTripIconObj=null;
	saveTripIconObj=null;

	sdSpanObj = $('#sdLine_'+lineId);	
	sdTxtObj = $('#sdTextLine_'+lineId);
	rdSpanObj = $('#rdLine_'+lineId);	
	rdTxtObj = $('#rdTextLine_'+lineId);
	editTripIconObj = $('#editTrip_'+lineId);
	saveTripIconObj = $('#saveTrip_'+lineId);
	
	sdSpanObj.hide();
	sdTxtObj.show();
	rdSpanObj.hide();
	rdTxtObj.show();
	editTripIconObj.hide();
	saveTripIconObj.show();

	new Epoch('epoch_popup', 'th', document.getElementById('ship_'+lineId));
	new Epoch('epoch_popup', 'th', document.getElementById('request_'+lineId));
	
}

function saveTrip(lineId){
	var orderId = $('#orderId').val();
	var txtShip = $('#ship_'+lineId);
	var txtRequest = $('#request_'+lineId);
	$('#newShipDate').val(txtShip.val());
	$('#newReqDate').val(txtRequest.val());
	$('#editLineId').val(lineId);
	//to Save Line action send TripNo, OrderId...Ship/Request
	document.orderForm.action = "${pageContext.request.contextPath}/jsp/memberOrderAction.do?do=saveTrip";
	document.orderForm.submit();
}

function cancelTrip(tripNo){
	for(var x=0;x<cancelTripNos.length;x++){
		if(cancelTripNos[x] == tripNo)
			return false;
	}
	
	if(!confirm('ต้องการยกเลิก trip? #'+tripNo))return false;
	$('#editTripNo').val(tripNo);
	document.orderForm.action = "${pageContext.request.contextPath}/jsp/memberOrderAction.do?do=cancelTrip";
	document.orderForm.submit();
	return true;
}

function viewComment(tripNo,orderId,expoted){
	window.open('${pageContext.request.contextPath}/jsp/memberorder/memberTripComment.jsp?tripNo='+tripNo+'&orderId='+orderId+'&exported='+expoted, 'TripComment', 'width=300,height=200,location=No,resizable=No');
}

function saveNeedExport(tripNo,chks){
	alert(tripNo);
	alert(chks);
}

function saveNeedExport(tripNo,chks){
	var orderId = $('#orderId').val();
	$('#editTripNo').val(tripNo);
	var exp='N';
	if(chks)exp='Y';
	//to Save Line action send TripNo, OrderId...need export
	document.orderForm.action = "${pageContext.request.contextPath}/jsp/memberOrderAction.do?do=changeNeedExport&needExport="+exp;
	document.orderForm.submit();
}

function saveOrderRemark(){
	document.orderForm.action = "${pageContext.request.contextPath}/jsp/memberOrderAction.do?do=saveOrderRemark";
	document.orderForm.submit();
}

function saveCallBeforeHeader(chks){
	var  msg = "";
	if(chks){
		msg = "ยืนยัน โทรก่อนส่ง";
	}else{
		msg = "ยืนยัน ยกเลิกโทรก่อนส่ง";
	}
	if(confirm(msg)){
		
		//var shipmentArr= $("input[id='shipmentChks']").map(function(){return $(this).val();}).get();
		//alert(shipmentArr.length);
		
		var call='N';
		if(chks)call='Y';
		document.orderForm.action = "${pageContext.request.contextPath}/jsp/memberOrderAction.do?do=saveCallBeforeHeader&needCall="+call;
		document.orderForm.submit();
   }
}

function saveCallBeforeLine(id, chks){
	var call='N';
	if(chks)call='Y';
	document.orderForm.action = "${pageContext.request.contextPath}/jsp/memberOrderAction.do?do=saveCallBeforeLine&needCall="+call+"&lineId="+id;
	document.orderForm.submit();
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<!-- PROGRAM HEADER -->
<jsp:include page="../program.jsp">
	<jsp:param name="function" value="SalesOrder"/>
	<jsp:param name="code" value="${orderForm.order.orderNo}"/>
</jsp:include>
<!-- BODY -->

<html:form action="/jsp/saleOrderAction">
<jsp:include page="../error.jsp"/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="25%"></td>
		<td width="20%"></td>
		<td width="15%"></td>
		<td></td>
	</tr>
	<tr>
		<td align="right"></td>
		<td align="left"></td>
		<td align="right"><bean:message key="DocumentNo" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<html:text property="order.orderNo" size="20" readonly="true" styleClass="disableText"/>
		</td>
	</tr>
	<tr>
		<td> </td>
		 <td>
		    <c:if test="${orderForm.order.oldPriceFlag=='Y' }">
		     <font color="red" size="3"><b>ราคาเดิม **</b></font>
		    </c:if>
		 </td>
		<td align="right"><bean:message key="TransactionDate" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<html:text property="order.orderDate" maxlength="10" size="15" readonly="true" styleId="orderDate" styleClass="disableText"/>
		</td>
	</tr>
	<tr>
		<td align="right">
			<bean:message key="Member" bundle="sysele"/>&nbsp;&nbsp;
		</td>
		<td align="left">
		   
			<html:text property="order.memberCode" size="10" readonly="true" styleClass="disableText"/>
			<html:text property="order.customerName" size="60" readonly="true" styleClass="disableText"/>
			<html:hidden property="order.customerId"/>
		</td>
		<td align="right">
			<bean:message key="CallBeforeSend" bundle="sysele"/>&nbsp;&nbsp;
		</td>
		<td align="left">
			<html:checkbox property="order.callBeforeSend" onclick="saveCallBeforeHeader(this.checked);" value="Y"/>
		</td>
	</tr>
	
	<tr>
		<td colspan="4">
			<jsp:include page="memberNewProduct.jsp"></jsp:include>
		</td>
	</tr>
	<tr>
		<td colspan="4">
		<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
			<tr>
				<th><bean:message key="Member.Time" bundle="sysele"/></th>
				<th class="order"><bean:message key="No" bundle="sysprop"/></th>
				<th class="name"><bean:message key="Product.Name" bundle="sysele"/></th>
				<th><bean:message key="Product.UOM" bundle="sysele"/></th>
				<th><bean:message key="Quantity" bundle="sysele"/></th>
				<th>ส่งจริง</th>
				<th class="costprice"><bean:message key="Price.Unit" bundle="sysele"/></th>
				<th class="costprice"><bean:message key="Overall" bundle="sysele"/></th>
				
				<th>วันที่แผนการจัดส่ง</th>
				<th>วันที่ส่งจริง</th>
				
				<th class="status">แก้ไขวันที่</th>
				<th class="status">ยกเลิกครั้งที่ส่ง</th>
				<th class="status">วิธีการชำระเงิน</th>
				<th class="status">จำนวนเงินตามแผน</th>
				<th class="status">จำนวนเงินเก็บจริง</th>
				<th class="status">Promotion</th>
				<th class="status">แก้ไขสินค้า/จำนวน/เงินที่เก็บ/Promotion</th>
				<th class="status">หมายเหตุ</th>
				<th class="status">ส่งสินค้าแล้ว</th>
				<th class="status">รับชำระแล้ว</th>
				<th class="status">โอนข้อมูลแล้ว</th>
				<th class="status">สร้างข้อมูลที่ระบบกลางแล้ว</th>
				<th><bean:message key="CallBeforeSend" bundle="sysele"/></th>
			</tr>
			<%	int ot=-1; 
				boolean isInTheSameTrip=false;
				String shipmentValue = "";
				String paymentValue = "";
				String exportedValue = "";
				String interfaceValue = "";
				String promotionValue = "";
				String callBeforeSend = "";
				boolean isShipment = false;
				boolean isPayment = false;
				boolean isExported = false;
				String classLine = "lineE";
				String tempTripNo = "";
				int no = 0;
				int tripNoCount = 1;
				int lastTripNo = 0;

				// Open Loop Order Line Detail
				for(OrderLine l : lines){ 
					if(ot!=l.getTripNo()){ 
						ot=l.getTripNo(); 
						isInTheSameTrip=false; 
						no++;
						tripNoCount = 1;
						
					}else{ 
						isInTheSameTrip=true; 
						tripNoCount++;
					}
					//lastTripNo
					lastTripNo = l.getTripNo();
					
					if(no%2==0){
						if("Y".equals(l.getPromotion1())){
						   classLine ="lineP";
						}else{
						   classLine ="lineE";
						}
					}else{
						if("Y".equals(l.getPromotion1())){
						  classLine = "lineP";
						}else{
						  classLine = "lineO";
						}
					}
					
					isShipment = "Y".equals(l.getShipment());
					isPayment = "Y".equals(l.getPayment());
					isExported = "Y".equals(l.getExported());
					shipmentValue = (isShipment)?"checked":"";
					paymentValue = (isPayment)?"checked":"";
					exportedValue = (isExported)?"checked":"";
					interfaceValue= ("Y".equals(l.getInterfaces())?"checked":"");
					promotionValue = ("Y".equals(l.getPromotion1())?"checked":"");
					//Disable CallBeforeSend case shipment ='Y'
					callBeforeSend = (isShipment)?"disabled":"";
					
					if(isShipment || isPayment){
						if(!trips.contains(""+l.getTripNo())){
							trips.add(""+l.getTripNo());
						}
						
						if(isShipment)
							cannotEditTrips.add(""+l.getId());
					}
					
					
			%>
			
			<tr class="<%=classLine%>">
				<td align="center" class="tripNo"><%= ((!isInTheSameTrip)?""+ot:"") %></td>
				<td align="center" ><%=tripNoCount%></td>
				<td align="left"><%=l.getProduct().getCode() %>&nbsp;<%=l.getProduct().getName()%></td>
				<td align="center"><%=l.getUom().getCode() %></td>
				<td align="right">
					<span id="qtyLine_<%=l.getId() %>"><%=new Double(l.getQty()).intValue() %></span>
					<span id="qtyTextLine_<%=l.getId() %>" style="display: none;">
						<input type="text" id="qty_<%=l.getId() %>" value="<%=new Double(l.getQty()).intValue() %>" size="5" maxlength="5" style="text-align: right;"/>
					</span>
				</td>
				<td align="right"><%=new Double(l.getActualQty()).intValue()%></td>
				<td align="right" id="prLine_<%=l.getId() %>"><%=new DecimalFormat("#,##0.00").format(l.getPrice()) %></td>
				<td align="right" id="ttLine_<%=l.getId() %>"><%=new DecimalFormat("#,##0.00").format(l.getTotalAmount()) %></td>
				<td align="center">
					<span id="rdLine_<%=l.getId() %>"><%=l.getRequestDate() %></span>
					<span id="rdTextLine_<%=l.getId() %>" style="display: none;">
						<input type="text" id="request_<%=l.getId() %>" value="<%=l.getRequestDate() %>" maxlength="10" size="10" readonly="readonly"> 
					</span>
				</td>
				<td align="center">
					<span id="sdLine_<%=l.getId() %>"><%=l.getShippingDate() %></span>
					<span id="sdTextLine_<%=l.getId() %>" style="display: none;">
						<input type="text" id="ship_<%=l.getId() %>" value="<%=l.getShippingDate() %>" maxlength="10" size="10" readonly="readonly"> 
					</span>
				</td>
				<td align="center">
					<%if(l.getExported().equalsIgnoreCase("N")){ %>
					<span id="editTrip_<%=l.getId()%>">
					<a href="javascript:edtiTrip('<%=l.getId() %>');">
					<img src="${pageContext.request.contextPath}/icons/doc_edit.gif" align="absmiddle"/></a>
					</span>
					<span id="saveTrip_<%=l.getId() %>" style="display: none;">
					<a href="javascript:saveTrip('<%=l.getId() %>');">
					<img src="${pageContext.request.contextPath}/icons/process.gif" align="absmiddle"/></a>
					</span>
					<%}else{ %>
					<img src="${pageContext.request.contextPath}/icons/doc_edit_dis.gif" align="absmiddle"/>
					<%} %>
				</td>
				<!-- Print Cancel Shipment -->
				<%if(!isInTheSameTrip){%>
				<td align="center">
					<span id="cancelTrip_<%=l.getTripNo() %>">
					<a href="#" onclick="cancelTrip('<%=l.getTripNo() %>');">
					<img src="${pageContext.request.contextPath}/icons/doc_inactive.gif" align="absmiddle"/></a>
					</span>
				</td>
				<%}else{ %>
				<td>&nbsp;</td>
				<% } %>
				<td>
					<% if("CS".equals(l.getPaymentMethod())) { %>
					เงินสด
					<% } else if("CR".equals(l.getPaymentMethod())) { %>
					เครดิตการ์ด
					<% } else if("CH".equals(l.getPaymentMethod())) { %>
					เช็ค
					<% } %>
				</td>
				<td align="right"><%=new DecimalFormat("#,##0").format(l.getNeedBill())%></td>
				<td align="right"><%=new DecimalFormat("#,##0").format(l.getActNeedBill())%></td>
				<td align="center">
					<input type="checkbox" <%=promotionValue%> disabled="disabled">
				</td>
				<td align="center">
					<%if(!isExported){ %>
					<a href="javascript:openPanel('<%=l.getId() %>','<%=l.getProduct().getCode() %>','<%=l.getProduct().getId() %>',<%=l.getQty() %>,<%=l.getTripNo() %>,'<%=l.getPromotion1() %>',<%=l.getNeedBill() %>,'<%=l.getPaymentMethod()%>','<%=l.getIscancel()%>','<%=l.getShipment()%>','<%=l.getPayment()%>','<%=l.getPrepay()%>');">
						<img src="${pageContext.request.contextPath}/icons/doc_edit.gif" align="absmiddle"/>
					</a>
					<%}else{ %>
					<img src="${pageContext.request.contextPath}/icons/doc_edit_dis.gif" align="absmiddle"/>
					<%} %>
				</td>
				<%if(!isInTheSameTrip){ %>
				<td align="center">
					<a href="#" onclick="viewComment('<%=l.getTripNo() %>','<%=l.getOrderId() %>','<%=l.getExported() %>');">
					<img src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"/></a>
				</td>
				<td align="center">
					<input type="checkbox" <%=shipmentValue%> disabled="disabled" class="disableText" id="shipmentChks">
				</td>
				<td align="center">
					<input type="checkbox" <%=paymentValue%> disabled="disabled" class="disableText">
				</td>
				<td align="center">
					<input type="checkbox" <%=exportedValue%> disabled="disabled" class="disableText">
				</td>
				<td align="center">
					<input type="checkbox" <%=interfaceValue%> disabled="disabled" class="disableText">
				</td>
			<%}else{ %>
				<td>&nbsp;</td>
				<td align="center"><input type="checkbox" <%=shipmentValue%> disabled="disabled" class="disableText"  id="shipmentChks"></td>
				<td align="center"><input type="checkbox" <%=paymentValue%> disabled="disabled" class="disableText"></td>
				<td></td>
				<td></td>
			<%} %>
				<td>
					<input type="checkbox" <%=callBeforeSend%> onclick="saveCallBeforeLine('<%=l.getId()%>', this.checked);" <%=l.getCallBeforeSend().equals("Y") ? "checked" : "" %>>
				</td>
			</tr>
			<%} // End For Loop %>
		</table>
		<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
			<tr>
				<td align="left" class="footer">&nbsp;</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td align="right" rowspan="3" valign="top">หมายเหตุ</td>
		<td align="left" rowspan="3" valign="top">
			<html:textarea property="order.remark" cols="30" rows="5"></html:textarea>
			<input type="button" value="บันทึกหมายเหตุ" onclick="saveOrderRemark();">
		</td>
		<td align="right">ยอดสุทธิรวม<span id="totalAmt" style="display: none;"><bean:message key="TotalAmount" bundle="sysele"/></span>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="tempTotalNeedNill" name="tempTotalNeedNill" readonly="readonly" class="disableText" 
			style="text-align: right;" value="<fmt:formatNumber type="number" pattern="#,###,##0.00" value="${totalNeedBillAmt}" />" />
			<html:hidden property="order.totalNeedBill" styleId="totalNeedBill"/>
			
			<input type="text" id="tempTotalAmount" name="tempTotalAmount" readonly="readonly" class="disableText" style="text-align: right;display:none;"/>
			<html:hidden property="order.totalAmount" styleId="totalAmount"/>
		</td>
	</tr>
	<tr>
		<td align="right">
			<span id="taxLabel" style="display: none;">
			<bean:message key="Tax" bundle="sysele"/>
			</span>
			&nbsp;&nbsp;
			</td>
		<td align="left">
			<input type="text" id="tempVatAmount" name="tempVatAmount" readonly="readonly" class="disableText" style="text-align: right;display:none;"/>
			<html:hidden property="order.vatAmount"/>
		</td>
	</tr>
	<tr>
		<td align="right"><span id="netAmtLabel" style="display: none;"><bean:message key="Net" bundle="sysele"/></span>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="tempNetAmount" name="tempNetAmount" readonly="readonly" class="disableText" style="text-align: right;display:none;"/>
			<html:hidden property="order.netAmount"/>
		</td>
	</tr>
	<tr>
		<td colspan="4"><hr/></td>
	</tr>
	<tr>
		<td></td>
		<td align="left">
			<html:checkbox property="order.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
		</td>
		<td align="right"></td>
		<td valign="top">
			<html:checkbox property="order.payment" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Order.Paid" bundle="sysele"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td align="left">
			<html:checkbox property="order.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Order.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<html:text property="order.salesOrderNo" size="20" readonly="true" styleClass="disableText"/>
		</td>
		<td align="right"><bean:message key="Bill.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<html:text property="order.arInvoiceNo" size="20" readonly="true" styleClass="disableText"/>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Order.SalesRepresent"  bundle="sysele"/>&nbsp;&nbsp;</td>
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
		</td>
	</tr>
</table>
<br>
<!--  -->
<html:hidden property="order.priceListId" styleId="pricelistId"/>
<html:hidden property="order.vatCode"/>
<html:hidden property="order.orderType"/>
<html:hidden property="order.id" styleId="orderId"/>
<html:hidden property="order.roundTrip"/>
<html:hidden property="order.customerId"/>
<html:hidden property="order.exported" value="N"/>
<html:hidden property="order.isCash" value="N"/>
<html:hidden property="id" styleId="id"/>
<html:hidden property="orderLineQty" styleId="orderLineQty"/>
<html:hidden property="newShipDate" styleId="newShipDate"/>
<html:hidden property="newReqDate" styleId="newReqDate"/>
<html:hidden property="editTripNo" styleId="editTripNo"/>
<html:hidden property="editLineId" styleId="editLineId"/>
<input type="hidden" name="memberVIP" value="${memberVIP}"/>
<input type="hidden" name="lastTripNo" id="lastTripNo" value="<%=lastTripNo%>"/>
<input type="hidden" name="tripNoCount" id="tripNoCount" value="<%=tripNoCount%>"/>

<div id="productList" style="display: none;"></div>
<div id="ByList" style="display: none;"></div>
<jsp:include page="../searchCriteria.jsp"></jsp:include>
</html:form>
<!-- BODY -->
</body>
</html>
<script>
<%
int idx = 0;
for(String no : trips){	%>
cancelTripNos[<%=idx%>]=<%=no%>;
<%idx++; } %>
<%
for(String no : cannotEditTrips){%>
$('#editTrip_<%=no%>').hide();
<%idx++; }%>
</script>