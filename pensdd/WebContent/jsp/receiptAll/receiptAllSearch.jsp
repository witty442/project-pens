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
<jsp:useBean id="receiptAllForm" class="com.isecinc.pens.web.receiptAll.ReceiptAllForm" scope="request" />
<%
User user = (User) request.getSession().getAttribute("user");
String role = user.getType();
List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

String isAdd = request.getSession(true).getAttribute("isAdd") != null ? (String)request.getSession(true).getAttribute("isAdd") : "Y";
//System.out.println(isAdd);

List<References> paymentmethods = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentmethods", paymentmethods, PageContext.PAGE_SCOPE);

List<References> bankL = InitialReferences.getReferenes().get(InitialReferences.BANK);
pageContext.setAttribute("bankL", bankL,PageContext.PAGE_SCOPE);
%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>" /></title>
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

table.result th.confirm{
width:100%;
text-align: right;
padding-left: 10px;
padding-bottom: 3px;
padding-right: 30px;
padding-top: 3px;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/receiptAll.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">
	function loadMe() {
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateFrom'));
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateTo'));

		<c:if test="${receiptAllForm.results != null}">
		new Epoch('epoch_popup', 'th', document.getElementById('confirmDate'));
		</c:if>
	}

	function selectItem(path){
		var items = new Array();
		var chks = document.getElementsByName('orderLine.receipt');
		var orderLineIds=document.getElementsByName('orderLineId');
		var needBills=document.getElementsByName('orderLine.needBill');
		//alert("xxx"+chks.length);
		//alert("needBills"+needBills.length);
		var item;
		var j=0;
		for(i=0;i<chks.length;i++){
			//alert("xxx"+chks[i].checked);
			if(chks[i].checked){
				item=new Object();
				item.receipt = chks[i].value;
				
				//alert("orderLineIds[i]"+orderLineIds[i].value);
				item.orderLineId=orderLineIds[i].value;
				//alert("item.orderLineId"+item.orderLineId);
				
				item.needBill=needBills[i].value;
				if(needBills[i].value == 0.0 || needBills[i].value == 0){
					//alert("needBills[i]"+needBills[i].value);
					alert("กรุณาใส่จำนวนเงิน เก็บเงินจริง");
					return false;
				}

				items[j]=item;
				j++;
			}
		}
		//alert("items"+items[0].orderLineId);
		save(path,items);
	}

	function checkSelectAll(total){
		calculateReceiptAmt(total);
		for(var i=0;i < total ; i++){
			var isChecked = document.getElementById('confirms'+i+'.isConfirm').checked;
			if(!isChecked){
				document.getElementById("receiptAll").checked = false;
				return true;
			}
		}
		
		document.getElementById("receiptAll").checked = true;
		return true;
	}

	function selectedAll(total){
		for(var i=0;i < total ; i++){
			document.getElementById('confirms'+i+'.isConfirm').checked = ($('#receiptAll').attr('checked'));
		}

		calculateReceiptAmt(total);
		return true;
	}

	function calculateReceiptAmt(total){
		var totalReceiptAmt = 0; 
		for(var i=0;i < total ; i++){
			if(document.getElementById('confirms'+i+'.isConfirm').checked)
				totalReceiptAmt += Number(document.getElementById('confirms'+i+'.confirmAmt').value);
		}

		document.getElementById("totalReceiptAmt").value = totalReceiptAmt;
		return true;
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
				<jsp:param name="function" value="ReceiptConfirmation" />
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
						<html:form action="/jsp/receiptAllAction">
						<jsp:include page="../error.jsp" />
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="43%">&nbsp;</td>
								<td></td>
							</tr>
							<tr>
								<td align="right">วันที่รายการส่งของ ตั้งแต่
								</td>
								<td align="left"><html:text property="order.orderDateFrom"
									maxlength="10" size="15" readonly="true" styleId="orderDateFrom" />&nbsp;&nbsp;
									ถึง&nbsp;&nbsp;
									<html:text property="order.orderDateTo"
									maxlength="10" size="15" readonly="true" styleId="orderDateTo" />
								</td>
							</tr>
							<tr>
								<td align="right">ประเภทการชำระเงิน</td>
								<td align="left">
									<html:select property="order.paymentMethod">
										<html:options collection="paymentmethods" property="key" labelProperty="name" />
									</html:select>
								</td>
							</tr>
						</table>
						<br>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="javascript:search('${pageContext.request.contextPath}')">
									<input type="button" value="ค้นหา" class="newPosBtn">
									</a> 
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<input type="button" value="Clear" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<c:if test="${receiptAllForm.results != null}">
							<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
							<bean:message key="RecordsFound" bundle="sysprop" /> &nbsp;
							<span class="searchResult">${receiptAllForm.receiptAllCriteria.searchResult}</span>&nbsp;<bean:message key="Records" bundle="sysprop" /></div>
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
								<tr>
								<c:set var="resultColSpan" value="12"/>
								<c:if test="${receiptAllForm.criteria.order.paymentMethod=='CH' || receiptAllForm.criteria.order.paymentMethod=='CR'}">
									<c:set var="resultColSpan" value="15"/>
								</c:if>
								<th colspan="${resultColSpan}" class="confirm">
									วันที่รับชำระเงินเข้าระบบ&nbsp;&nbsp;:&nbsp;&nbsp;<html:text property="confirmDate" maxlength="10" size="12" readonly="true" styleId="confirmDate" />&nbsp;&nbsp;&nbsp;&nbsp;
									เลือกชำระเงินทั้งหมด&nbsp;&nbsp;<input id="receiptAll" type="checkbox" onchange="selectedAll(${receiptAllForm.receiptAllCriteria.searchResult})" />
								</th>
								</tr>
								<tr>
									<th><bean:message key="No" bundle="sysprop"/></th>
									<th>รหัสสมาชิก</th>
									<th>ชื่อสมาชิก</th>
									<th>ชื่อสินค้า</th>
									<th><bean:message key="Member.Time" bundle="sysele"/></th>
									<th>เลขที่เอกสาร</th>
									<th>ชุดจัดส่ง</th>
									<th>ประเภทการชำระเงิน</th>
									<th>แผนเก็บเงิน</th>
									<th>เก็บเงินจริง</th>
									<th>วันที่ส่งของ</th>
									<th>ชำระเงิน</th>
									<c:if test="${receiptAllForm.criteria.order.paymentMethod=='CH'}">
									<th>ธนาคารเช็ค</th>
									<th>เลขที่เช็ค</th>
									<th>วันที่เช็ค</th>
									</c:if>
									<c:if test="${receiptAllForm.criteria.order.paymentMethod=='CR'}">
									<th>ธนาคารบัตรเครดิต</th>
									<th>เลขที่บัตรเครดิต</th>
									<th>ชื่อเจ้าของบัตร</th>
									</c:if>
								</tr>
							<c:forEach var="results" items="${receiptAllForm.results}" varStatus="rows">
								<input type="hidden" name='orderLineId' value='${results.id}'>
								
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td><c:out value='${rows.index+1}' /></td>
									<td align="center">${results.customerCode}</td>
									<td align="center">${results.customerName}</td>
									<td align="center">${results.product.name}</td>
									<td align="center">${results.tripNo}</td>
									<td align="center">${results.orderNo}</td>
									<td align="center">${results.deliveryGroup}</td>
									<td align="center">
										<c:set var="receiptAmt" value="${results.needBill}" scope="page"/>
										<c:set var="receiptReadonly" value="" scope="page"/>
										<c:if test="${results.paymentMethod == 'CS'}" >เงินสด
										<c:set var="receiptAmt" value="${results.actNeedBill}" scope="page"/>
										<c:set var="receiptReadonly" value="readonly" scope="page"/>
										</c:if>
										<c:if test="${results.paymentMethod == 'CR'}" >เครดิตการ์ด</c:if>
										<c:if test="${results.paymentMethod == 'CH'}" >เช็ค</c:if>
									</td>
									<td align="center"><fmt:formatNumber type="number" pattern="#,###,##0.00" value="${results.needBill}"/></td>
									<td align="center">
									<input id="confirms${rows.index}.confirmAmt"" name="confirms[${rows.index}].confirmAmt" readonly style="text-align: right; padding-right: 2px;" type="text" size="8" value="${receiptAmt}" onblur="calculateReceiptAmt(${receiptAllForm.receiptAllCriteria.searchResult})" />
									
									</td>
									<td align="center">${results.requestDate}</td>
									<td align="center">
										<input name="confirms[${rows.index}].isConfirm" id="confirms${rows.index}.isConfirm" type="checkbox" 
											value="Y" onchange="checkSelectAll(${receiptAllForm.receiptAllCriteria.searchResult})" />
										<input name="confirms[${rows.index}].lineId" type="hidden" value="${results.id}"/>
										<input name="confirms[${rows.index}].orderNo" type="hidden" value="${results.orderNo}"/>
										<input name="confirms[${rows.index}].paymentMethod" type="hidden" value="${results.paymentMethod}"/>
										<input name="confirms[${rows.index}].actQty" type="hidden" value="${results.actualQty}"/>
										<input name="confirms[${rows.index}].qty" type="hidden" value="${results.qty}"/>
										<input name="confirms[${rows.index}].totalAmt" type="hidden" value="${results.totalAmount}"/>
										<input name="confirms[${rows.index}].needBillAmt" type="hidden" value="${results.needBill}"/>
										<input name="confirms[${rows.index}].tripNo" type="hidden" value="${results.tripNo}"/>
										<input name="confirms[${rows.index}].productCode" type="hidden" value="${results.product.code}"/>
										<input name="confirms[${rows.index}].productName" type="hidden" value="${results.product.name}"/>
										<input name="confirms[${rows.index}].shippingDate" type="hidden" value="${results.shippingDate}"/>
										<input name="confirms[${rows.index}].orderId" type="hidden" value="${results.orderId}"/>			
									</td>
									<c:if test="${receiptAllForm.criteria.order.paymentMethod=='CH'}">
									<td>
										<html:select property="confirms[${rows.index}].bank" styleId="creditCardBank" style="width: 100px ;"  >
											<html:option value=""></html:option>
											<html:options collection="bankL" property="key" labelProperty="name" />
										</html:select>
									</td>
									<td>
									&nbsp;
									</td>
									<td>
									&nbsp;
									</td>
									</c:if>
									<c:if test="${receiptAllForm.criteria.order.paymentMethod=='CR'}">
									<td>
										<select name="confirms[${rows.index}].bank"  style="width: 100px ;">
											<option value="">--กรุณาระบุค่า--</option>
											<c:forEach var="bank" items="${bankL}" varStatus="bankRows">
											<c:set var="selectedValue" value="" />
											<c:if test="${bank.key == results.bank}">
												<c:set var="selectedValue" value="selected" />
											</c:if>
											<option ${selectedValue} value="${bank.key}">${bank.name}</option>
											</c:forEach>
										</select>
									</td>
									<td>
										<input name="confirms[${rows.index}].creditCardNo" type="text" value="${results.creditCardNo}">
									</td>
									<td>
										<input name="confirms[${rows.index}].creditCardName" type="text" value="${results.creditCardName}">
									</td>
									</c:if>
								</tr>
								<c:set var="totalPlanAmt" value="${totalPlanAmt+results.needBill}" scope="page" />
							</c:forEach>
								<tr>
									<td align="right" class="footer" colspan="8">
										<table width="100%">
											<tr>
											<td style="text-align: right ; padding-right: 10px;">รวม</td>
											</tr>
										</table>
									</td>
									<td align="left" class="footer" colspan="1">
									<table width="100%">
											<tr>
											<td style="text-align: center ; padding-right: 10px;">
												<input id="totalPlanAmt" class="disableText" readonly type="text" style="text-align: right; padding-right: 2px;" value="<fmt:formatNumber type="number" pattern="#,###,##0.00" value="${totalPlanAmt}"/>" size="8" />
											</td>
											</tr>
										</table>
									</td>
									<td align="left" class="footer" colspan="1">
									<table width="100%">
											<tr>
											<td style="text-align: center ;">
												<input id="totalReceiptAmt" class="disableText" readonly type="text" style="text-align: right; padding-right: 2px;" value="0" size="8" />
											</td>
										</table>
									</td>
									<td align="left" class="footer" colspan="${resultColSpan-10}">&nbsp;</td>
								</tr>
							</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<c:if test="${receiptAllForm.results != null}">
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="#" onclick="save('${pageContext.request.contextPath}',${receiptAllForm.receiptAllCriteria.searchResult});">
									<input type="button" value="รับชำระเงิน" class="newPosBtn" >
									</a>
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						</c:if>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right"><a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
								<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
								<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
								</a></td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<html:hidden property="order.roundTrip"/>
						<html:hidden property="order.priceListId"/>
						<html:hidden property="order.orderType" value="<%=role %>" />
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