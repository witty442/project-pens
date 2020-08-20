<!--
Product Popup for Member 
 -->
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
//String pCatID = (String)request.getParameter("pCatID");

String visit = "";
if(request.getParameter("visit")!=null)
	visit = request.getParameter("visit");

System.out.println("visit:"+visit);

pageContext.setAttribute("visit",visit,PageContext.PAGE_SCOPE);

String row = "";

//List<UOM> uoms = new MUOM().lookUp();
if(request.getParameter("row")!=null)
{
	row = request.getParameter("row");
}
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.UOM"%>
<%@page import="com.isecinc.pens.model.MUOM"%>
<%@page import="com.pens.util.ConvertNullUtil"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" language="javascript">
function loadMe(){
	if('<%=row%>'!=''){
		putData(<%=row%>);
		loadProduct(null);
	}
	$('#pCode').focus();
}

$('#lookProduct').click(
	loadProduct(null)
);

// call ajax
function loadProduct(e){
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/productQuery.jsp",
				data : "pCode=" + $('#pCode').val(),
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					$('#productId').val(returnString.split('||')[0]);
					//$('#pName').val(returnString.split('||')[3]+" "+returnString.split('||')[1]+" "+returnString.split('||')[2]);
					$('#pName').val(returnString.split('||')[1]);
					var getData2 = $.ajax({
						url: "${pageContext.request.contextPath}/jsp/ajax/ProductUOMQuery.jsp",
						data : "pId=" + $('#productId').val(),
						async: false,
						success: function(getData){
							var returnString = jQuery.trim(getData);
							$('#uom').html("");
							$(returnString).appendTo('#uom');	
						}
					}).responseText;
				}
			}).responseText;
		});
	}
}

function putData(rowNo){
	rowNo--;

	var mdoc = window.opener.document;
	var ids = mdoc.getElementsByName('prod.id')[rowNo];
	var pIds = mdoc.getElementsByName('prod.product.id')[rowNo];
	var rows = mdoc.getElementsByName('prod.row')[rowNo];
	var pCodes = mdoc.getElementsByName('prod.product.code')[rowNo];
	var pNames = mdoc.getElementsByName('prod.product.name')[rowNo];
	var uoms = mdoc.getElementsByName('prod.uomId')[rowNo];
	var uomsLabel = mdoc.getElementsByName('uomLabel')[rowNo];
	var amounts = mdoc.getElementsByName('prod.orderQty')[rowNo];

	//alert(pNames.value);
	
	$('#pId').val(pIds.value);
	$('#pCode').val(pCodes.value);
	$('#pName').val(pNames.value);
	$('#uom').val(uoms.value);
	$('#amount').val(amounts.value);
	$('#memberProductId').val(ids.value);
	$('#productRow').val(rowNo);

	if('<%=visit%>'=='Y'){
		var amounts2 = mdoc.getElementsByName('prod.orderQty2')[rowNo];
		$('#amount2').val(amounts2.value);
	}
	
}

function addRow(){
	if(jQuery.trim($('#pCode').val()) == '' || jQuery.trim($('#pName').val()) == ''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		$('#pCode').focus();
		return false;
	}  
	if(jQuery.trim($('#amount').val()) == ''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		$('#amount').focus();
		return false;
	}
	
	var product = new Object();
	product.pCode = $('#pCode').val();
	product.pName = $('#pName').val();
	product.uomId = $('#uom').val();
	product.uomLabel = $('#uom option:selected').text();
	product.amount = $('#amount').val();
	product.pId = $('#productId').val();
	product.row = $('#productRow').val();
	
	if('<%=visit%>'=='Y'){
		if(jQuery.trim($('#amount2').val()) == ''){
			alert('กรุณาระบุข้อมูลให้ครบถ้วน');
			$('#amount2').focus();
			return false;
		}
		product.amount2 = $('#amount2').val();
	}

	if(!checkNum($('#amount'))){return;}
	
	if($('#memberProductId').val()==0){
		product.id = 0;
	}else{
		product.id = $('#memberProductId').val();
	}

	if(product.row==''){
		window.opener.addProduct('${pageContext.request.contextPath}', product);
	}else{
		window.opener.setValueToProduct('${pageContext.request.contextPath}', product);
	}
	window.close();
}

function showProduct(path){
	window.open(path + "/jsp/pop/view/productViewPopup.jsp", "Product List", "width=500,height=350,location=No,resizable=No");
}
function setProduct(code, name){
	$('#pCode').val(code);
	$('#pName').val(name);
	loadProduct(null);
}
</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="สินค้า"/>
</jsp:include>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="45%"></td>
		<td></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Product" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<input type="text" id="pCode" name="pCode" onkeyup="loadProduct(event)"/>&nbsp;
			<a href="#" onclick="showProduct('${pageContext.request.contextPath}');" id="lookProduct">
			<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
		</td>
	</tr>
	<tr>
		<td align="right"></td>
		<td align="left"><input type="text" id="pName" name="pName" size="50" readonly="readonly" class="disableText"/></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Product.UOM" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<select id="uom" name="uom" >
			</select>
		</td>
	</tr>
	<tr>
		<td align="right">
			<bean:message key="Amount" bundle="sysele"/><c:if test="${visit=='Y'}">หน้าร้าน</c:if><font color="red">*</font>
		</td>
		<td align="left"><input type="text" id="amount" name="amount" size="10" style="text-align: right;" onkeydown="return inputNum(event);"></td>
	</tr>
	<c:if test="${visit=='Y'}">
	<tr>
		<td align="right">
			<bean:message key="Amount" bundle="sysele"/>หลังร้าน<font color="red">*</font>
		</td>
		<td align="left"><input type="text" id="amount2" name="amount2" size="10" style="text-align: right;" onkeydown="return inputNum(event);"></td>
	</tr>
	</c:if>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td align="center" colspan="2">
			<a href="#" onclick="addRow();">
			<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" value="บันทึก" class="newPosBtn">
			</a>
			<a href="#" onclick="window.close();">
			<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn">
			</a>
		</td>
	</tr>
</table>
<input type="hidden" id="productId" name="productId">
<input type="hidden" id="productRow" name="productRow">
<input type="hidden" id="memberProductId" name="memberProductId"/>
<br/>
</body>
</html>