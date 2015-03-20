<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%
List<References> paymentmethods = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentmethods", paymentmethods, PageContext.PAGE_SCOPE);
%>
<script type="text/javascript">
function loadProduct(e){
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/productQueryOrder.jsp",
				data : "pCode=" + $('#newProductCode').val(),
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					$('#newProductId').val(returnString.split('||')[0]);
					$('#newProductName').val(returnString.split('||')[1]+" "+returnString.split('||')[2]);
					
					var getData2 = $.ajax({
						url: "${pageContext.request.contextPath}/jsp/ajax/UOMQuery.jsp",
						data : "pId=" + $('#newProductId').val()
						+ "&plId=" + $('#pricelistId').val(),
						async: false,
						success: function(getData){
							var returnString = jQuery.trim(getData);
							$('#newUOM').val(returnString.split('|')[0]);
							$('#newPrice').val(returnString.split('|')[2]);
						}
					}).responseText;
				}
			}).responseText;
		});
	}
	$('#newQty').focus();
}

function openPanel(lineId,code,name,qty,trip,promotion1,needBill,paymentMethod,isCancel,isShipment,isPaid,isPrePay){
	clearNewLine();
	//$('#trLineReSchedule').hide();
	$('#addNewLineTbl').show();
	$('#newScheduleDate').hide();
	$('#reSchedule').attr('checked',false);
	
	if(!lineId){
		
		/* alert($('#tripNoCount').val());
		
		//add TripNo +1
		var newTripNo = $('#lastTripNo').val();
		if(newTripNo != null){
			newTripNo = parseInt(newTripNo)+1;
			$('#newTripNo').val(newTripNo);
		} */
		
		$('#addPanelLabel').html('เพิ่มรายการใหม่');
		$('#trLineCancel').hide();
		$('#trLineReSchedule').hide();
		$("select#paymentMethod").val(['${orderForm.order.paymentMethod}']);
		new Epoch('epoch_popup', 'th', document.getElementById('newSDate'));
		new Epoch('epoch_popup', 'th', document.getElementById('newRDate'));
	}else{
		setProduct(code, name);
		$('#addPanelLabel').html('แก้ไขสินค้า/จำนวน/เงินที่เก็บ/Promotion');
		$('#newLineId').val(lineId);
		$('#newTripNo').val(trip);
		$('#newTripNo').attr("readOnly","readOnly");
		$('#newTripNo').attr("class","disableText");
		$('#trLineSDate').hide();
		$('#trLineRDate').hide();
		$('#newQty').val(qty);
		$('#needBill').val(needBill);
		
		if(promotion1 == 'Y'){
			$('#promotion1').attr("checked","checked");
		}else{
			$('#promotion1').attr('checked',false);
		}

		if(paymentMethod!=null){
			// Set Payment Method From Value
			$("select#paymentMethod").val([paymentMethod]);
		}
		else{
			// new Line
			// Default Payment Method from Order PaymentMethod
			$("select#paymentMethod").val(['${orderForm.order.paymentMethod}']);
			
		}

		if(isCancel == 'Y'){
			$('#isCancel').attr("checked","checked");
		}else{
			$('#isCancel').attr('checked',false);
		}

		if(isShipment == 'Y'){
			$('#newProductCode').attr("readOnly","readOnly");
			$('#newProductCode').attr("class","disableText");
			$('#newQty').attr("readOnly","readOnly");
			$('#newQty').attr("class","disableText");
			$('#isCancel').attr("disabled","disabled");
			$('#reSchedule').attr("disabled","disabled");
			isLookProduct =false;
		}

		// การเก็บเงิน,Promotion,วิธีการชำระเงิน,
		if(isPaid == 'Y'){
			$('#needBill').attr("readOnly","readOnly");
			$('#needBill').attr("class","disableText");
			$('#paymentMethod').attr("disabled","disabled");
			$('#paymentMethod').attr("class","disableText");
			$('#promotion1').attr("disabled","disabled");

			if(isPrePay != 'Y')
				$('#isCancel').attr("disabled","disabled");
		}
	}
}

var isLookProduct = true;

function closePanel(){
	$('#addNewLineTbl').hide();
	$('#addNewLineBtn').show();
}
function showProduct(path){
	if(!isLookProduct)
		return;
	
	window.open(path + '/jsp/pop/view/productViewPopup.jsp', 'Product List', 'width=500,height=350,location=No,resizable=No');
}
function setProduct(code, name){
	$('#newProductCode').val(code);
	$('#newProductName').val(name);
	loadProduct(null);
}

function saveNewLine(){
	//Validate
	//alert('เลือกสินค้า');
	if($('#newTripNo').val()=='' || Number($('#newTripNo').val())<0){
		alert('ใส่ครั้งที่ส่งสินค้า');
		$('#newTripNo').focus();
		return false;
	}
	if($('#newProductCode').val()==''){
		alert('เลือกสินค้า');
		$('#newProductCode').focus();
		return false;
	}
	if($('#newQty').val()=='' || Number($('#newQty').val())==0){
		//alert('ใส่จำนวน');
		//$('#newQty').focus();
		//return false;
	}
	if($('#newLineId').val()==0){
		if($('#newSDate').val()==''){
			alert('ใส่วันที่ส่งสินค้า');
			$('#newSDate').focus();
			return false;
		}
		if($('#newRDate').val()==''){
			alert('ใส่วันที่ต้องการสินค้า');
			$('#newRDate').focus();
			return false;
		}
	}
	else{
		if($('#reSchedule').attr( 'checked' )==true && $('#reSchedule').val() == 'Y'){
			if($('#newScheduleDate').val()==''){
				alert('กรุณาบันทึกวันที่แผนที่ต้องการเปลี่ยน');
				$('#newScheduleDate').focus();
				return false;
			}
		}
	}
	
	document.orderForm.action = "${pageContext.request.contextPath}/jsp/memberOrderAction.do?do=saveNewLine";
	document.orderForm.submit();
	return true;
}

function clearNewLine(){
	$('#newTripNo').val(0);
	$('#newTripNo').attr("readOnly","");
	$('#newTripNo').attr("class","");
	$('#newProductCode').val('');
	$('#newProductCode').attr("readOnly","");
	$('#newProductCode').attr("class","");
	$('#isCancel').attr("disabled","");
	$('#newProductName').val('');
	$('#newUOM').val('');
	$('#newQty').val('');
	$('#newQty').attr("readOnly","");
	$('#newQty').attr("class","");
	$('#paymentMethod').attr("disabled","");
	$('#paymentMethod').attr("class","");
	$('#newProductId').val(0);
	$('#newPrice').val(0.0);
	$('#newSDate').val('');
	$('#newRDate').val('');
	$('#trLineCancel').show();
	$('#trLineSDate').show();
	$('#trLineRDate').show();
	$('#newLineId').val(0);
	
	$('#needBill').attr("readOnly","");
	$('#needBill').attr("class","");
	$('#promotion1').attr("disabled","");
	$('#reSchedule').attr("disabled","");
	
	isLookProduct = true;
}

function openDate(reShedule){
	if(reShedule){
		$('#newScheduleDate').show();
		new Epoch('epoch_popup', 'th', document.getElementById('newScheduleDate'));
	}
	else{
		$('#newScheduleDate').hide();
	}
}

function isNumeric(obj){
	if(isNaN(obj.value)){
		obj.value = "";
	}
}

function calcNeedBill(obj){
	/* var price = parseFloat($('#newPrice').val());
	if( !isNaN(obj.value)){
		var qty = parseFloat(obj.value);
		var amount = price*qty ;
		var vat7 = 0.07 * amount;
		var needBill = amount+vat7;
			
		$('#needBill').val(needBill.toFixed(2));
	} */
}

</script>
<input type="button" value="เพิ่มรายการใหม่" id="addNewLineBtn" onclick="openPanel();">
<fieldset id="addNewLineTbl" style="display: none;">
	<legend><span id="addPanelLabel">เพิ่มรายการใหม่</span></legend>
	<table align="center" border="0" cellpadding="3" cellspacing="1" width="100%">
		<tr>
			<td align="left" valign="top" width="100px;">
				ครั้งที่<font color="red">*</font> 
				<html:text property="memberNewLine.tripNo" styleId="newTripNo" size="5" maxlength="5"/><br>
				<!--<html:checkbox property="memberNewLine.promotion" value="Y" onclick="clearNewLine();"/>ของแถม-->
			</td>
			<td align="right">
				สินค้า<font color="red">*</font>
			</td>
			<td align="left">
				<html:text property="memberNewLine.product.code" styleId="newProductCode" maxlength="20" size="10" onblur="loadProduct(null);"/>
				<a href="#" onclick="showProduct('${pageContext.request.contextPath}');" id="lookProduct">
				<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"></a>
				<html:text property="memberNewLine.product.name" styleId="newProductName" size="50" readonly="true" styleClass="disableText"/>
				<html:text property="memberNewLine.uom.id" styleId="newUOM" size="5" maxlength="5" readonly="true" styleClass="disableText"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td align="right">จำนวน<font color="red">*</font></td>
			<td align="left">
				<html:text property="memberNewLine.qty" styleId="newQty" maxlength="20" size="10" style="text-align: right;" 
				onkeydown="return inputNum(event,this);" onblur="isNumeric(this);calcNeedBill(this);"/>
				<html:hidden property="memberNewLine.product.id" styleId="newProductId"/>
				<html:hidden property="memberNewLine.price" styleId="newPrice"/>
			</td>
		</tr>
		<tr id="trLineNeedBill">
			<td></td>
			<td align="right">การเก็บเงิน</td>
			<td align="left">
			<html:text property="memberNewLine.needBill" styleId="needBill"/>
			</td>
		</tr>
		<tr id="trLinePaymentMethod">
			<td></td>
			<td align="right">วิธีการชำระเงิน</td>
			<td align="left">
				<html:select property="memberNewLine.paymentMethod" styleId="paymentMethod">
					<html:options collection="paymentmethods" property="key" labelProperty="name" />
				</html:select>
				<html:hidden property="memberNewLine.paymentMethod"  />
			</td>
		</tr>
		<tr id="trLinePromotion1">
			<td></td>
			<td align="right">Promotion</td>
			<td align="left">
			<html:checkbox property="memberNewLine.promotion1" value="Y" styleId="promotion1"/>
			</td>
		</tr>
		<tr id="trLineSDate">
			<td></td>
			<td align="right">วันที่ส่งจริง<font color="red">*</font></td>
			<td align="left"><html:text property="memberNewLine.shippingDate" styleId="newSDate" maxlength="10" size="15" readonly="true"/></td>
		</tr>
		<tr id="trLineRDate">
			<td></td>
			<td align="right">วันที่แผนการจัดส่ง<font color="red">*</font></td>
			<td align="left"><html:text property="memberNewLine.requestDate" styleId="newRDate" maxlength="10" size="15" readonly="true"/></td>
		</tr>
		<tr id="trLineCancel">
			<td></td>
			<td align="right">ยกเลิก</td>
			<td align="left">
			<html:checkbox property="memberNewLine.iscancel" value="Y" styleId="isCancel"/>
			</td>
		</tr>
		<tr id="trLineReSchedule">
			<td></td>
			<td align="right">ปรับแผนทั้งหมด</td>
			<td align="left">
			<html:checkbox property="reSchedule" value="Y" styleId="reSchedule" onchange="openDate(this.checked);" />&nbsp;&nbsp;&nbsp;
			<html:text property="newScheduleDate" styleId="newScheduleDate" maxlength="10" size="15" readonly="true"/>
			</td>
		</tr>
		<tr>
			<td colspan="3" align="center">
				<input type="button" value="บันทึก" onclick="saveNewLine();">
				<input type="button" value="ยกเลิก" onclick="closePanel();">
			</td>
		</tr>
	</table>
	<html:hidden property="memberNewLine.id" styleId="newLineId"/>
</fieldset>

