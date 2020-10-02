<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.Constants"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockInvForm" class="com.isecinc.pens.web.stockinv.StockInvForm" scope="session" />
<%
String mode = stockInvForm.getMode();
System.out.println("mode:"+mode);
int tabIndex = 0;
if(stockInvForm.getResults() != null){
	tabIndex = stockInvForm.getResults().size()*2;
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<script type="text/javascript">
function loadMe(){
	 <%if(mode.equals("add")){ %>
	      addRow(false);
	  <%}else{%>
	      addRow(true);
	 <%  }  %>
}
function clearForm(path){
	var form = document.stockInvForm;
	form.action = path + "/jsp/stockInvAction.do?do=clearInitStock";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.stockInvForm;
	form.action = path + "/jsp/stockInvAction.do?do=export";
	form.submit();
	return true;
}

function back(path){
	var form = document.stockInvForm;
	form.action = path + "/jsp/stockInvAction.do?do=searchHead&action=back";
	form.submit();
	return true;
}
function save(path){ 
	var form = document.stockInvForm;
	/* if(!validateData()){
		alert("กรุณาระบุข้อมูลให้ถูกต้อง");
		return false;
	} */
	<%if (stockInvForm.getBean().getTransType().equals("OUT")) {%>
	   var remark = document.getElementById("remark");
	   if(remark.value ==""){
		   alert("กรุณา ระบุหมายเหตุ ");
		   remark.focus();
		   return false;
	   }
	<%}%>
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	form.action = path + "/jsp/stockInvAction.do?do=saveInitStock";
	form.submit();
	return true;
}
function confirmInitStock(path){ 
	var form = document.stockInvForm;
	
	<%if (stockInvForm.getBean().getTransType().equals("OUT")) {%>
	   var remark = document.getElementById("remark");
	   if(remark.value ==""){
		   alert("กรุณา ระบุหมายเหตุ ");
		   remark.focus();
		   return false;
	   }
	<%}%>
	
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	form.action = path + "/jsp/stockInvAction.do?do=confirmInitStock";
	form.submit();
	return true;
}
function addRow(focus){
	var rows = $('#tblProduct tr').length-1;
	var className = 'lineO';
	if(rows%2 !=0){
		className = 'lineE';
	}
	var tabIndex = parseFloat(document.getElementById("tabIndex").value);
	tabIndex++;
	
	var lineId = rows+1;
	//alert("lineId["+lineId+"]");
	
	var rowData ="<tr class='"+className+"'>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='checkbox' tabindex ='-1' name='linechk' value='0'/>"+
	    "  <input type='hidden' tabindex ='-1' name='lineId' />"+
	    "</td>";
	    tabIndex++;
	    rowData += "<td class='td_text_center' width='10%'> "+
	    "   <input type='text'"+
	    "   onkeypress=getAutoKeypress(event,this,"+lineId+",'ProductStockOnhand','') "+
	    "   name='productCode' size='10' autoComplete='off' tabindex="+tabIndex+"/>"+
	    "   <input type='hidden' tabindex ='-1' name='productId' autoComplete='off' />"+
	    "</td>";
	    rowData +="<td class='td_text_center' width='30%'> "+
	    "  <input type='text' tabindex ='-1' name='productName' readonly class='disableText' size='50'/>"+
	    "</td>";
	    rowData +="<td class='td_text_center' width='10%'> "+
	    "  <input type='text' tabindex ='-1' name='uom' readonly class='disableText' size='5'/>"+
	    "   <input type='hidden' tabindex ='-1' name='uom1' />"+
	    "   <input type='hidden' tabindex ='-1' name='uom2' />"+
	    "</td>";
	    rowData +="<td class='td_text_center' width='10%'> "+
	    "  <input type='text' tabindex ='-1' name='status' value='' readonly class='disableText' size='5'/>"+
	    "</td>"; 
	    tabIndex++;
	    rowData +="<td class='td_text_center' width='15%'> "+
	    " <input type='text' tabindex ='"+tabIndex+"' name='qty1' autoComplete='off'"+ 
	    "  class='enableNumber' size='10' onkeydown='return isNum(this,event);' onblur='sumTotal()'/>"+
	    "</td>"+
	    tabIndex++;
	    rowData +="<td class='td_text_center' width='15%'> "+
	    " <input type='text' tabindex ='"+tabIndex+"' name='qty2' autoComplete='off'"+ 
	    "   class='enableNumber' size='10' onkeydown='return isNum(this,event)' onblur='sumTotal()'/> "+
	    "</td>"+
	    "</tr>";
 
    $('#tblProduct').append(rowData);
    //set next tabIndex
    document.getElementById("tabIndex").value = tabIndex;
    
    //set focus default
    var productCode = document.getElementsByName("productCode");
    if(focus){
    	productCode[lineId-1].focus();
    }
}
function getAutoKeypress(e,obj,lineId,pageName,criteriaType){
	var form = document.barcodeForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("Product" == pageName){
				document.getElementsByName("productCode")[lineId-1].value = '';
				document.getElementsByName("productId")[lineId-1].value = '';
				document.getElementsByName("productName")[lineId-1].value = '';
				document.getElementsByName("uom")[lineId-1].value = '';
				document.getElementsByName("qty1")[lineId-1].value = '';
				document.getElementsByName("qty2")[lineId-1].value = '';
				document.getElementsByName("uom1")[lineId-1].value = retArr[4];
				document.getElementsByName("uom2")[lineId-1].value = retArr[5];
			}
		}else{
			//check dup product
			if(checkDupProduct(obj,lineId-1)){
				alert("ไม่สามารถ ระบุรหัสสินค้าซ้ำได้ ");
				document.getElementsByName("productCode")[lineId-1].value = '';
				document.getElementsByName("productId")[lineId-1].value = '';
				document.getElementsByName("productName")[lineId-1].value = '';
				document.getElementsByName("uom")[lineId-1].value = '';
				document.getElementsByName("qty1")[lineId-1].value = '';
				document.getElementsByName("qty2")[lineId-1].value = '';
				document.getElementsByName("uom1")[lineId-1].value = '';
				document.getElementsByName("uom2")[lineId-1].value = '';
				document.getElementsByName("productCode")[lineId-1].focus();
				return false;
			}else{
			   getAutoDetail(obj,lineId,pageName,criteriaType);
			}
		}//if
	}//if
}
function getAutoDetail(obj,lineId,pageName,criteriaType){
	var returnString = "";
	var form = document.stockInvForm;
	
	//prepare parameter
	var param = "";
	if("ProductStockOnhand"==pageName){
		param  ="pageName="+pageName;
		param +="&productCode="+obj.value;
	}
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getAutoKeypressAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	 
	if("ProductStockOnhand" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			document.getElementsByName("productCode")[lineId-1].value = retArr[1];;
			document.getElementsByName("productId")[lineId-1].value = retArr[2];
			document.getElementsByName("productName")[lineId-1].value = retArr[3];
			document.getElementsByName("uom")[lineId-1].value = retArr[4]+"/"+retArr[5];
			document.getElementsByName("uom1")[lineId-1].value = retArr[4];
			document.getElementsByName("uom2")[lineId-1].value = retArr[5];
			
			document.getElementsByName("productCode")[lineId-1].readOnly = true;
			document.getElementsByName("productCode")[lineId-1].className ='disableText';
			document.getElementsByName("qty1")[lineId-1].focus();
			
			if(document.getElementsByName("uom2")[lineId-1].value==""){
				document.getElementsByName("qty2")[lineId-1].className="disableNumber";
				document.getElementsByName("qty2")[lineId-1].readOnly =true;
			}
			addRow(false);
		}else{
			alert("ไม่พบข้อมูล");
			document.getElementsByName("productCode")[lineId-1].focus();
			
			document.getElementsByName("productCode")[lineId-1].value = '';
			document.getElementsByName("productId")[lineId-1].value = '';
			document.getElementsByName("productName")[lineId-1].value = '';
			document.getElementsByName("uom")[lineId-1].value = '';
			document.getElementsByName("uom1")[lineId-1].value = '';
			document.getElementsByName("uom2")[lineId-1].value = '';
			document.getElementsByName("qty1")[lineId-1].value = '';
			document.getElementsByName("qty2")[lineId-1].value = '';
		}
	}
}
function checkDupProduct(productCodeNew,curRowId){
	var productCode = document.getElementsByName("productCode");
	var status = document.getElementsByName("status");
	//alert("productCodeNew{"+productCodeNew.value+"}curRowId["+curRowId+"]");
	for(var i=0;i<productCode.length;i++){
		if(status[i].value !='DELETE' && i != curRowId && productCodeNew.value==productCode[i].value){
			//alert("productCodeNew[]"+productCodeNew.value+"]curRowId["+curRowId+"]productCode["+i+"]["+productCode[i].value+"]");
			return true;
		}
	}
	return false;
 }
function removeRow(){
	//todo play with type
	var tbl = document.getElementById('tblProduct');
	var chk = document.getElementsByName("linechk");
	var status = document.getElementsByName("status");
	var lineId = document.getElementsByName("lineId");
	var deleteLineIds = document.getElementById("deleteLineIds");
	var drow;
	var bcheck=false;
	for(var i=0;i<chk.length;i++){
		if(chk[i].checked){
			//alert(status[i].value);
			if(status[i].value =="SUCCESS"){
				alert("ไม่สามารถลบรายการ สถานะ Success ได้ ");
				bcheck=true;
				chk[i].checked =false;//reset check
			}else if(status[i].value !="DELETE"){
				drow = tbl.rows[i+1];
				status[i].value ="DELETE";
				$(drow).hide();
				bcheck=true;
				//alert(lineId[i].value);
				if(lineId[i].value != 0){
					deleteLineIds.value = deleteLineIds.value+lineId[i].value+",";
				}
			}
		}
	}
	if(!bcheck){
		alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;
	}
}
function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
}
function validateData(){
	var pass = true;
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	if(rows.length ==1){
		return false;
	}
	var productCode = document.getElementsByName("productCode");
	var status = document.getElementsByName("status");
	var qty1 = document.getElementsByName("qty1");
	var qty2 = document.getElementsByName("qty2");
	
	//alert(itemIssue.length);
	
	var errorList = new Object();
    for(var i= 0;i<productCode.length;i++){
		var rowObj = new Object();
		var lineClass ="lineE";
		if(i%2==0){
			lineClass = "lineO";
		}
		//alert(itemIssue[i].value );
		
		if(productCode[i].value != "" && status[i].value !='DELETE'){
			if( qty1[i].value =="" && qty2[i].value ==""){
			  rows[i+1].className ='lineError';
			  pass = false;
			}
		}
		
		//no error
		if(pass){
			rows[i+1].className =lineClass;
		}
		//alert("rows["+i+"]:"+rows[i].className);
	}// for
	return pass ;
} 

function sumTotal(){
	var productCode = document.getElementsByName("productCode");
	var status = document.getElementsByName("status");
	var qty1 = document.getElementsByName("qty1");
	var qty2 = document.getElementsByName("qty2");
	var sumQty1 = 0;
	var sumQty2 = 0;
    for(var i= 0;i<productCode.length;i++){
		if(productCode[i].value != "" && status[i].value !='DELETE'){
			if( qty1[i].value !=""){
				sumQty1 += convetTxtObjToFloat(qty1[i]);
			}
			if( qty2[i].value !=""){
				sumQty2 += convetTxtObjToFloat(qty2[i]);
			}
		}
	}// for
	
	document.getElementById("totalQty1").value = sumQty1;
	document.getElementById("totalQty2").value = sumQty2;
	toCurrenyNoDigit(document.getElementById("totalQty1"));
	toCurrenyNoDigit(document.getElementById("totalQty2"));
} 
function openPopupPage(page){
	var form = document.stockInvForm;
	var path = document.getElementById("path").value;
    var param = "&page="+page;
	var url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	
	PopupCenterFullHeight(url,"GetProduct By SubBrand",900);
}

function setMainValue(page,data){
	if("SUBBRAND_STOCK"==page){
	   document.getElementById("productCodeSelect").value = data;
	  
	   //submit from to merg data table
	   var form = document.stockInvForm;
	   var path = document.getElementById("path").value;
	   form.action = path + "/jsp/stockInvAction.do?do=initProductTable";
	   form.submit();
	   return true;
	}
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
				<jsp:param name="function" value="StockInv"/>
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
						<html:form action="/jsp/stockInvAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						    <%if(request.getAttribute("msgList") != null ) {
						       List<String> msgList = (List)request.getAttribute("msgList");
						       if(msgList != null && msgList.size() >0){
						    	   for(int i=0;i<msgList.size();i++){
						    %>
						         <tr>
                                    <td colspan="3"> <font color="red"><%=msgList.get(i) %></font></td>
								</tr>
							<%}}} %>
						       <tr>
                                    <td>วันที่ทำรายการ</td>
									<td>		
									   <html:text property="bean.transactionDate" styleId="transactionDate" size="20" readonly="true" styleClass="disableText"/>			 
									</td>
									 <td> เลขที่เอกสาร</td>
									<td>					
										 <html:text property="bean.headerId" styleId="headerId" size="5" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								 <tr>
                                    <td valign="bottom">  ประเภทเอกสาร:</td>
									<td valign="bottom">
									  <b><font size="2">			
									  <html:hidden property="bean.transType" disabled="true"/>
										<u>
											<%if(stockInvForm.getBean().getTransType().equals("IN")) {
												out.print("บันทึกสินค้า <font size='3' color='blue'> เข้าคลัง</font>");
											}else{
												out.print("บันทึกสินค้า <font size='3' color='red'>ออกจากคลัง</font>");
											}
										%>
										</font></u></b>	
									</td>
									<td align="right" valign="bottom"> หมายเหตุ</td>
									<td>					
										 <html:textarea property="bean.remark" styleId="remark" cols="40" rows="3"></html:textarea>
									</td>
								</tr>
						   </table>
				 <!-- Table Data -->
				<c:if test="${stockInvForm.results != null}">
                  	    <c:if test="${stockInvForm.bean.canEdit == true}">
	                        <div align="left">
								<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow();"/>	
								<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow();"/>	
								<input type="button" class="newPosBtn" name="btSearch" id="btSearch" 
								   value="เพิ่มสินค้า โดยกลุ่ม SubBrand" onclick="openPopupPage('SUBBRAND_STOCK')"/>
							</div>
				        </c:if> 
				         		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
							<!-- 	<th >No</th> -->
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >รหัสสินค้า</th>
								<th >ชื่อสินค้า</th>
								<th >หน่วยนับ</th>
								<th >สถานะ</th>	
								<th >จำนวน(เต็ม)</th>		
								<th >จำนวน(เศษ)</th>					
							</tr>
							<c:forEach var="results" items="${stockInvForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text_center" nowrap width="10%">
										  <input type="checkbox" name="linechk" value="${results.lineId}"/>
										  <input type="hidden" name="lineId" value="${results.lineId}" />
										</td>
										<td class="td_text_center" width="10%">
										   <input onkeypress="getAutoKeypress(event,this,${results.no},'ProductStockOnhand')"
										   type="text" name="productCode" value ="${results.productCode}" size="10" readonly class="disableText"/>
										   <input type="hidden" name="productId" value="${results.productId}"  autoComplete='off'/>
										</td>
										<td class="td_text_center" width="30%">
											<input type="text" name="productName" value ="${results.productName}" size="50" readonly class="disableText"/>
										</td>
										<td class='td_text_center' width='10%'>
										   <input type="text" name="uom" value ="${results.uom}" size="5" readonly class="disableText"/>
										   <input type="hidden" name="uom1" value="${results.uom1}" />
										   <input type="hidden" name="uom2" value="${results.uom2}" />
										</td>
										 <td class='td_text_center' width='10%'>
										   <input type="text" name="status" value ="${results.status}" size="5" readonly class="disableText"/>
										</td> 
										<td class='td_text_center' width='15%'>
										 <input type="text" name="qty1" value ="${results.qty1}" size="10" ${results.qty1Readonly} 
										 autoComplete='off' class="${results.qty1Style}" 
										 onkeydown='return isNum(this,event)' onblur='sumTotal()'>
										</td>
										<td class='td_text_center' width='15%'>
										 <input type="text" name="qty2" value ="${results.qty2}" size="10" ${results.qty2Readonly} 
										 autoComplete='off' class="${results.qty2Style}" 
										 onkeydown='return isNum(this,event)'  onblur='sumTotal()'/>
										</td>
									</tr>
							  </c:forEach>
					</table>
					    <!-- TOTAL -->
						<table id="tblProduct2" align="center" border="0" cellpadding="3" cellspacing="1" width="100%">
						     <tr>
						       <td colspan="5" width='70%' align="right"><b>Total</b></td>
						       <td class='td_text_center' width='15%'> 
						          <input type="text" id="totalQty1" value ="${stockInvForm.bean.qty1}" size="10" readonly class="disableNumber"/>
						       </td>
						       <td class='td_text_center' width='15%'>
						          <input type="text" id="totalQty2" value ="${stockInvForm.bean.qty2}" size="10" readonly class="disableNumber"/>
						       </td>
						     </tr>
						</table>
				</c:if>
					   <!-- Table Data -->
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
								   <a href="javascript:exportToExcel('${pageContext.request.contextPath}')"> 
								       <input type="button" value="Export To Excel " class="newPosBtnLong"/>
								     </a>&nbsp;&nbsp;&nbsp;
									 <c:if test="${stockInvForm.bean.canEdit == true}">
										<a href="javascript:save('${pageContext.request.contextPath}')"> 
										  <%if(stockInvForm.getBean().getTransType().equals("IN")) { %>
											  <input type="button" value="บันทึกข้อมูล เข้าคลัง " class="newPosBtnLong"/>
										  <%}else{ %>
											  <input type="button" value="บันทึกข้อมูล ออกจากคลัง " class="newPosBtnLong"/>
										  <%}%>
										 </a>
									 </c:if>
									  <c:if test="${stockInvForm.bean.canConfirm == true}">
										<a href="javascript:confirmInitStock('${pageContext.request.contextPath}')">
										<%if(stockInvForm.getBean().getTransType().equals("IN")) { %>
											<input type="button" value="ยืนยันสินค้า เข้าคลัง " class="newPosBtnLong"/>
										<% }else{ %>
											<input type="button" value="ยืนยันสินค้า ออกจากคลัง " class="newPosBtnLong"/>
										<%}%>
										</a>
									 </c:if>
								    &nbsp;			
									<a href="javascript:back('${pageContext.request.contextPath}','','add')">
									  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
									</a>	
									</td>
									<td align="right">
								  </td>
							</tr>
						</table>
					  </div>
					<!-- ************************Result ***************************************************-->
					<!-- hidden field -->
					 <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
					 <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
					 <input type="hidden" id="productCodeSelect" name="productCodeSelect" />
					 <html:hidden property="bean.deleteLineIds" styleId="deleteLineIds"/>
					 <html:hidden property="bean.headerId"/>
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
<!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->