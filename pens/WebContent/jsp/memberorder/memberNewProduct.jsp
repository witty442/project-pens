<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
							//$('#uom2').val(returnString.split('|')[1]);
							$('#newPrice').val(returnString.split('|')[2]);
							//$('#price2').val(returnString.split('|')[3]);
							//$('#price1Show').val(addCommas($('#price1').val()));
							//$('#price2Show').val(addCommas($('#price2').val()));
						}
					}).responseText;
				}
			}).responseText;
		});
	}
	$('#newQty').focus();
}

function openPanel(lineId,code,name,qty,trip){
	clearNewLine();
	$('#addNewLineTbl').show();
	$('#addNewLineBtn').hide();
	if(!lineId){
		$('#addPanelLabel').html('������¡������');
		new Epoch('epoch_popup', 'th', document.getElementById('newSDate'));
		new Epoch('epoch_popup', 'th', document.getElementById('newRDate'));
	}else{
		setProduct(code, name);
		$('#addPanelLabel').html('����Թ���/�ӹǹ');
		$('#newLineId').val(lineId);
		$('#newTripNo').val(trip);
		$('#newTripNo').attr("readOnly","readOnly");
		$('#newTripNo').attr("class","disableText");
		$('#trLineSDate').hide();
		$('#trLineRDate').hide();
		$('#newQty').val(qty);
	}
}

function closePanel(){
	$('#addNewLineTbl').hide();
	$('#addNewLineBtn').show();
}
function showProduct(path){
	window.open(path + '/jsp/pop/view/productViewPopup.jsp', 'Product List', 'width=500,height=350,location=No,resizable=No');
}
function setProduct(code, name){
	$('#newProductCode').val(code);
	$('#newProductName').val(name);
	loadProduct(null);
}

function saveNewLine(){
	//Validate
	if($('#newTripNo').val()=='' || Number($('#newTripNo').val())==0){
		alert('�����駷�����Թ���');
		$('#newTripNo').focus();
		return false;
	}
	if($('#newProductCode').val()==''){
		alert('���͡�Թ���');
		$('#newProductCode').focus();
		return false;
	}
	if($('#newQty').val()=='' || Number($('#newQty').val())==0){
		//alert('���ӹǹ');
		//$('#newQty').focus();
		//return false;
	}
	if($('#newLineId').val()==0){
		if($('#newSDate').val()==''){
			alert('����ѹ������Թ���');
			$('#newSDate').focus();
			return false;
		}
		if($('#newRDate').val()==''){
			alert('����ѹ����ͧ����Թ���');
			$('#newRDate').focus();
			return false;
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
	$('#newProductName').val('');
	$('#newUOM').val('');
	$('#newQty').val('');
	$('#newProductId').val(0);
	$('#newPrice').val(0.0);
	$('#newSDate').val('');
	$('#newRDate').val('');
	$('#trLineSDate').show();
	$('#trLineRDate').show();
	$('#newLineId').val(0);
}
</script>
<input type="button" value="������¡������" id="addNewLineBtn" onclick="openPanel();">
<fieldset id="addNewLineTbl" style="display: none;">
	<legend><span id="addPanelLabel">������¡������</span></legend>
	<table align="center" border="0" cellpadding="3" cellspacing="1" width="100%">
		<tr>
			<td align="left" valign="top" width="100px;">
				���駷��<font color="red">*</font> 
				<html:text property="memberNewLine.tripNo" styleId="newTripNo" size="5" maxlength="5"/><br>
				<!--<html:checkbox property="memberNewLine.promotion" value="Y" onclick="clearNewLine();"/>�ͧ��-->
			</td>
			<td align="right">
				�Թ���<font color="red">*</font>
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
			<td align="right">�ӹǹ<font color="red">*</font></td>
			<td align="left">
				<html:text property="memberNewLine.qty" styleId="newQty" maxlength="20" size="10" style="text-align: right;"/>
				<html:hidden property="memberNewLine.product.id" styleId="newProductId"/>
				<html:hidden property="memberNewLine.price" styleId="newPrice"/>
			</td>
		</tr>
		<tr id="trLineSDate">
			<td></td>
			<td align="right">�ѹ������Թ���<font color="red">*</font></td>
			<td align="left"><html:text property="memberNewLine.shippingDate" styleId="newSDate" maxlength="10" size="15" readonly="true"/></td>
		</tr>
		<tr id="trLineRDate">
			<td></td>
			<td align="right">�ѹ����Ѻ�Թ���<font color="red">*</font></td>
			<td align="left"><html:text property="memberNewLine.requestDate" styleId="newRDate" maxlength="10" size="15" readonly="true"/></td>
		</tr>
		<tr>
			<td colspan="3" align="center">
				<input type="button" value="�ѹ�֡" onclick="return saveNewLine();">
				<input type="button" value="¡��ԡ" onclick="closePanel();">
			</td>
		</tr>
	</table>
	<html:hidden property="memberNewLine.id" styleId="newLineId"/>
</fieldset>

