<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.ControlLockPage"%>
<%@page import="com.isecinc.pens.web.autoorder.AutoOrderBean"%>
<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="autoOrderForm" class="com.isecinc.pens.web.autoorder.AutoOrderForm" scope="session" />

<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "autoOrderForm");

boolean accessOrderPage = ControlLockPage.canAccessPage("Order");
System.out.println("accessOrderPage:"+accessOrderPage);

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_fixhead_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
 
 
<script type="text/javascript">
//'.tbl-content' consumed little space for vertical scrollbar, scrollbar width depend on browser/os/platfrom. Here calculate the scollbar width .
$(window).on("load resize ", function() {
  var scrollWidth = $('.tbl-content').width() - $('.tbl-content table').width();
  $('.tbl-header').css({'padding-right':scrollWidth});
}).resize();

</script>

<script type="text/javascript">
function loadMe(){
  /** for popup BatchTask in page **/
	 <%if( !"".equals(Utils.isNull(request.getAttribute("BATCH_TASK_NAME")))){%>
	    //lockscreen
	    var path = document.getElementById("path").value;
	    /** Init progressbar **/
		$(function() {
			// update the block message 
	        $.blockUI({ message: "<h2>กำลังทำรายการ     กรุณารอสักครู่......</h2>" }); 
		}); 
		    
		//submitedGenStockOnhandTemp
		var url = path+'/jsp/batchTaskAction.do?do=prepare&pageAction=new&initBatchAction=initBatchFromPageByPopup&pageName=<%=Utils.isNull(request.getAttribute("BATCH_TASK_NAME"))%>';
		popupFull(url,'<%=Utils.isNull(request.getAttribute("BATCH_TASK_NAME"))%>');
<%}%>

<%if(autoOrderForm.getResults() != null && autoOrderForm.getResults().size() >0){ %>
  sumTotal();
<%}%>
}

function genAutoOrder(path){
	var form = document.autoOrderForm;
	if(form.storeCode.value ==""){
		alert("กรุณาเลือก ร้านค้า");
		form.storeCode.focus();
		return false;
	}else{
		//validate is OrderDate is Generated
		var returnString= "";
		var param = "storeCode="+form.storeCode.value+"&orderDate="+form.orderDate.value;
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/autoOrder/ajax/canGenAutoOrderRepAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
		
		//alert("returnString:"+returnString);
		
		if(returnString.length >2){ //Return StoreCode No Confirm
			alert("ร้านค้า "+returnString+" ยังไม่ทำการ Confirm To Order กรุณากลับไป Confirm ก่อนทำรายการต่อได้ ");
			return false;
		}
		if(returnString=='-1'){
			alert("ร้านค้า "+form.storeCode.value+" วันที่ Order : "+form.orderDate.value+"  Generate Order เติมเต็มไปแล้ว ");
			return false;
		}
		if(returnString=='-2'){
			alert("ร้านค้า "+form.storeCode.value+" ยังไม่บันทึกยอด Onhand สำหรับทำ Order Replenisment");
			return false;
		}
		
		/** check Lock BME Order **/
	    if(returnString=='-3'){
		      if(confirm("ท่านยัง  ไม่ได้ Lock หน้า Order B'me to Wacoal  ยืนยันทำการ Lock and Gen Order Replenisment")){
		    	  form.action = path + "/jsp/autoOrderAction.do?do=genAutoOrder";
				  form.submit();
				   return true;
		      }
		 }else{
			 if(confirm("ยืนยัน Gen Order Replenisment")){ 
				form.action = path + "/jsp/autoOrderAction.do?do=genAutoOrder";
				form.submit();
				return true;
			 }
		}
	}
}

function gotoStockOnhandPage(path){
	var url="";
	var form = document.autoOrderForm;
	var storeCode = form.storeCode.value;
	var param = "&storeCode="+storeCode+"&orderDate="+form.orderDate.value;
	if(storeCode ==""){
		alert('กรุณาระบุ รหัสร้านค้า');
		storeCode.focus();
		return false;
	}
	if(storeCode.indexOf('020047') != -1){
       url = path+'/jsp/reportAllAction.do?do=prepare&action=new&pageName=reportEndDateLotus'+param;
	}else if(storeCode.indexOf('020049') != -1){
       url = path+'/jsp/reportAllAction.do?do=prepare&action=new&pageName=reportSizeColorBigC'+param;
	}
	//alert(url);
	//window.location = encodeURI(url);
	
	link(true,url);
}
function genStockOnhandRepTemp(){
	//alert("genStockOnhandRepTemp");
   var form = document.autoOrderForm;
   var path = form.path.value;

   if(form.storeCode.value ==""){ 
	   alert("กรุณากรอกข้อมูลรหัสร้านค้า");
	   form.storeCode.focus();
	   return false;
   } 
   if(confirm("กรุณายืนยันการ Gen บันทึกยอด Onhand สำหรับทำ Order Replenisment")){
		form.action = path + "/jsp/autoOrderAction.do?do=genStockOnhandRepTemp&";
		form.submit();
		return true;
   }
}
function searchBatch(){
	//unlockScreen
	setTimeout($.unblockUI, 100); 
	 
	var form = document.autoOrderForm;
	var path = form.path.value;
	form.action = path + "/jsp/autoOrderAction.do?do=searchBatch";
	form.submit();
	return true;
}
function search(path){
	var form = document.autoOrderForm;
	var path = form.path.value;
    if(form.storeCode.value ==""){ 
	   alert("กรุณากรอกข้อมูลรหัสร้านค้า");
	   form.storeCode.focus();
	   return false;
   } 
	form.action = path + "/jsp/autoOrderAction.do?do=search";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.autoOrderForm;
	var path = form.path.value;
    if(form.storeCode.value ==""){ 
	   alert("กรุณากรอกข้อมูลรหัสร้านค้า");
	   form.storeCode.focus();
	   return false;
   } 
	form.action = path + "/jsp/autoOrderAction.do?do=export";
	form.submit();
	return true;
}
function clearForm(path){
	var form = document.autoOrderForm;
	form.action = path + "/jsp/autoOrderAction.do?do=prepareSearch&action=new";
	form.submit();
	return true;
}
function saveOrderRep(path){
	var form = document.autoOrderForm;
	var groupCode = document.getElementsByName("groupCode");
	//alert("groupCode length:"+groupCode.length);
	if(groupCode.length ==0){
		alert("กรุณา Generate Order เติมเต็มก่อน ทำการ บันทึก/แก้ไข ข้อมูล");
		return false;
	}
	if(confirm("ยืนยัน บันทึก/แก้ไข ข้อมูล")){
		/**  Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/autoOrderAction.do?do=save";
		form.submit();
		return true;
	}
}
function confirmTempRepToOrder(path){
	var form = document.autoOrderForm;
	if(form.storeCode.value ==""){ 
		alert("กรุณากรอกข้อมูลรหัสร้านค้า");
		form.storeCode.focus();
		return false;
	} 
	if(confirm("ยืนยัน Confirm To BME Order")){
		/**  Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/autoOrderAction.do?do=confirmTempRepToOrder";
		form.submit();
		return true;
	}
}
function lockOrderPage(path,lock){
	var form = document.autoOrderForm;
	//case unlock manual check Order_rep status is null cannot unlock
	//alert("lock:"+lock);
	if(lock=="N"){
		//validate is OrderDate is Generated
		var returnString= "";
		var param = "orderDate="+form.orderDate.value;
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/autoOrder/ajax/canUnlockBmeOrderAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
		
		//alert(returnString);
		if(returnString.length >2){ //Return StoreCode No Confirm
			alert("ไม่สามารถ Unlockได้   ร้านค้า "+returnString+" ยังไม่ทำการ Confirm To Order กรุณากลับไป Confirm ก่อนทำรายการต่อ ");
			return false;
		}
	}
	var form = document.autoOrderForm;
		form.action = path + "/jsp/autoOrderAction.do?do=lockOrderPage&lock="+lock;
		form.submit();
		return true;
}

/** open popup **/
function openPopup(path,pageName){
	var form = document.autoOrderForm;
	var param = "&pageName="+pageName;
	 if("StoreOrderRep" == pageName){
		param +="&hideAll=true&storeCode="+form.storeCode.value;
	}
	url = path + "/jsp/popupAction.do?do=prepareAll&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.autoOrderForm;
	if("StoreOrderRep" == pageName){
		//alert(code);
		form.storeCode.value = code;
		form.storeName.value = desc;
	}
} 
/** get autoKeypress Ajax **/
function getAutoOnblur(e,obj,pageName){
	var form = document.autoOrderForm;
	if(obj.value ==''){
		if("StoreOrderRep" == pageName){
			form.storeCode.value = '';
			form.storeName.value = "";
		}
	}else{
		getAutoDetail(obj,pageName);
	}
}
function getAutoKeypress(e,obj,pageName){
	var form = document.autoOrderForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("StoreOrderRep" == pageName){
				form.storeCode.value = '';
				form.storeName.value = "";
			}
		}else{
			getAutoDetail(obj,pageName);
		}
	}
}
function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.autoOrderForm;
	//prepare parameter
	var param = "";
	if("StoreOrderRep"==pageName){
		param  ="pageName="+pageName;
		param +="&storeCode="+obj.value;
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
	 
	if("StoreOrderRep" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.storeCode.value = retArr[1];
			form.storeName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.storeCode.focus();
			form.storeCode.value = '';
			form.storeName.value = "";
		}
	}
}
function validateOrderQty(obj,row){
	var onhandQty = convetTxtObjToFloat(document.getElementsByName("wacoalOnhandQty")[row]);
	//validate Onhand Qty
	var orderQty = convetTxtObjToFloat(obj);
	
	if(orderQty > onhandQty){
		alert("จำนวนรวม Order QTY("+orderQty+") สินค้านี้มีมากว่า  Onhand QTY("+onhandQty+")");
		obj.value = onhandQty;
		obj.focus();
		return false;
	}
	
	sumTotal();
	return true;
}
function sumTotal(){
	var totalOrderQty = 0;
	var orderQtyObj = document.getElementsByName("orderQty");
	
	for(var i=0;i<orderQtyObj.length;i++){
		totalOrderQty += convetTxtObjToFloat(orderQtyObj[i]);
	}
	var totalOrderQtyObj = document.getElementsByName("totalOrderQty")[0];
	totalOrderQtyObj.value =totalOrderQty;
	//convertTo Currency No Digit
	toCurrenyNoDigit(totalOrderQtyObj);
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
				<jsp:param name="function" value="AutoOrder"/>
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
						<html:form action="/jsp/autoOrderAction">
						<jsp:include page="../error.jsp"/>
						
					    <div align="center">
					     <table align="center" border="0" cellpadding="3" cellspacing="0" >
					     <tr>
								<td align="right" colspan="2">
								  <%
						             if( accessOrderPage==false){ %>
						                
										 <font color="red" size="3"><u>กำลัง Lock หน้า Order B'me to Wacoal </u> </font>  
										 &nbsp; &nbsp; &nbsp; &nbsp;
										 <!-- BTN UNLOCK -->
										<a href="javascript:lockOrderPage('${pageContext.request.contextPath}','N')">
										  <input type="button" value="Unlock Order B'me to Wacoal" class="newPosBtnLong">
										</a>
									<%}else{ %>
										 <font color="green" size="3"><u> ไม่ได้ Lock หน้า Order B'me to Wacoal </u> </font>
										  &nbsp; &nbsp; &nbsp; &nbsp;
										  <!-- BTN LOCK -->
									    <a href="javascript:lockOrderPage('${pageContext.request.contextPath}','Y')">
										  <input type="button" value="Lock Order B'me to Wacoal" class="newPosBtnLong">
										</a>
									<%} %>&nbsp;
								 </td>
							</tr>
					       <tr>
								<td align="right">
								    วันที่ Order <font color="red">*</font>
								 </td>
								  <td>
								  <html:text property="bean.orderDate" styleId="orderDate" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td  align="right">รหัสร้านค้า <font color="red">*</font></td>
								<td>
								  <html:text property="bean.storeCode" styleId="storeCode" size="20" 
								    onkeypress="getAutoKeypress(event,this,'StoreOrderRep')"
                                    onblur="getAutoOnblur(event,this,'StoreOrderRep')"
                                    styleClass="\" autoComplete=\"off"/>-
								  <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','StoreOrderRep')"/>
								  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="40"/>
								
								  <html:hidden property="bean.subInv" styleId="subInv"/>
								  <html:hidden property="bean.storeNo" styleId="storeNo"/>
								</td>
							</tr>
					   </table>
					   <br/>
					   <table  align="center" border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td valign="middle" align="center">
								    <a href="javascript:search('${pageContext.request.contextPath}')">
									  <input type="button" value="  ค้นหา   " class="newPosBtnLong">
									</a>
									 <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="  Export   " class="newPosBtnLong">
									</a>
								    <a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>	
						            
								</td></tr>
								<tr><td valign="middle">
								    <a href="#" onclick="javascript:gotoStockOnhandPage('${pageContext.request.contextPath}');">
						                <input type="button" value="ไปหน้าจอ Gen Stock Onhand" class="newPosBtnLong"/>
						             </a>
						          
									<!-- <a href="javascript:genStockOnhandRepTemp()">
									  <input type="button" value="บันทึกยอด Onhand สำหรับทำ Order Replenisment" class="newPosBtnLong"/>
									</a> --> 
									 
								    <a href="javascript:genAutoOrder('${pageContext.request.contextPath}')">
									  <input type="button" value="Generate Order เติมเต็ม " class="newPosBtnLong"> 
									</a>
									<%if(autoOrderForm.getBean().isCanSave()){ %>
									     <a href="javascript:saveOrderRep('${pageContext.request.contextPath}')">
									     <input type="button" value="   บันทึก/แก้ไข    " class="newPosBtnLong">
									    </a>	
									<%} %>
									
									<%if(autoOrderForm.getBean().isCanSave()){ %>
									 <a href="javascript:confirmTempRepToOrder('${pageContext.request.contextPath}')">
									  <input type="button" value="Confirm To Order" class="newPosBtnLong">
									</a>
									<%} %>
									
							</td></tr>
					  </table>
				     </div>
				     <br/>
                     <!-- Batch Task Result--> 
                    <jsp:include page="/jsp/batchtask/batchTaskPopupResult.jsp"></jsp:include>
                    <br/>
                    
					<!-- ************************Result ***************************************************-->
					<%if(autoOrderForm.getResults() != null && autoOrderForm.getResults().size() >0){ %>
					<div class='tbl-header'>
					   <table align="center" border="1" cellpadding="3" cellspacing="0" class='table_fix'>
		               <thread>
		                <tr>
		                  <th width="4%">No</th>
		                  <th width="10%">Group</th>
		                  <th width="7%">Size/ Color</th>
		                  <th width="8%">Item</th>
		                  <th width="10%">WC Onhand - ยอด Order ที่คีย์ไว้</th>
		                  <th width="10%">Stock หน้าร้าน</th>
		                  <th width="10%">ยอดขาย ย้อนหลัง</th>
		                  <th width="15%">Recommend ให้เติมเต็ม</th>
		                  <th width="14%">ยอดที่ต้องการเติมเต็ม</th>
		                  <th width="6%">Status</th>
		                  <th width="6%">Min/Max</th>
		                </tr>     
		                </thread>
		               </table>
		              </div>
		              
		              <div class='tbl-content'>  
		               <table id='tblProductContent' class='table_fix' border='1' cellpadding='3' cellspacing='1'> 
                       <tbody> 
		            <%
		             for(int i=0;i<autoOrderForm.getResults().size();i++){
		            	 AutoOrderBean item = autoOrderForm.getResults().get(i);
		            %>
		             <tr class='lineO'>
		               <td class="td_text_center" width="4%"><%=(i+1) %> </td>
		               <td class="td_text_center" width="10%">
		                 <input type="text" name="groupCode" class="disableText" readonly size="10" value="<%=item.getGroupCode()%>">
		               </td>
		               <td class="td_text_center" width="7%">
		                 <input type="text" name="sizeColor" class="disableText" readonly size="3" value="<%=item.getSizeColor()%>">
		               </td>
		               <td class="td_text_center" width="8%">
		                 <input type="text" name="pensItem" class="disableText" readonly size="6" value="<%=item.getPensItem()%>">
		               </td>
		               <td class="td_number" width="10%">
		                 <input type="text" name="wacoalOnhandQty" class="disableNumber" readonly size="7" value="<%=item.getWacoalOnhandQty()%>">
		               </td>
		               <td class="td_number" width="10%">
		                 <input type="text" name="shopOnhandQty" class="disableNumber" readonly size="7" value="<%=item.getShopOnhandQty()%>">
		               </td>
		               <td class="td_number" width="10%">
		                 <input type="text" name="salesQty" class="disableNumber" readonly size="7" value="<%=item.getSalesQty()%>">
		               </td>
		               <td class="td_number" width="15%">
		                 <input type="text" name="recommandQty" class="disableNumber" 
		                 readonly size="7" value="<%=item.getRecommendQty()%>"
		                 title="<%=item.getRecommendCalcQty()%>">
		               </td>
		               <td class="td_number" width="14%">
		                 <input type="text" name="orderQty" class="enableNumber" 
		                 size="10" value="<%=(item.getOrderQty().equals("0")?"":item.getOrderQty())%>"
		                 onkeydown="return inputNum(event);" autoComplete="off"
		                 onchange="validateOrderQty(this,<%=i%>);">
		               </td>
		               <td class="td_text_center" width="6%"><%=item.getStatus()%></td>
		               <td class="td_text_center" width="6%"><%=item.getMinQty()%>/<%=item.getMaxQty()%></td>
		             </tr>
		            <%} %>
		            </tbody>
		            </table>
		            </div>
		            <!-- Total -->
		            <div class='tbl-header'>
		             <table align="center" border="1" cellpadding="1" cellspacing="0"  class='table_footer_fix'>
		               <thread>
		                <tr class="row_hilight">
		                  <td width="4%"></td>
		                  <td width="10%"></td>
		                  <td width="7%"></td>
		                  <td width="8%"></td>
		                  <td width="10%"></td>
		                  <td width="10%"></td>
		                  <td width="10%"></td>
		                  <td width="15%" class="td_number"><b>Total</b></td>
		                  <td width="14%" class="td_number">
		                   <input type="text" name="totalOrderQty" class="disableNumberBold" 
			                 readonly size="10" value="">
		                  </td>
		                  <td width="6%"></td>
		                  <td width="6%"></td>
		                </tr>     
		                </thread>
		               </table>
		              </div>
		           <%} %>
                    <!-- ************************Result ***************************************************-->
                    <!-- Hidden -->
                    <input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
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