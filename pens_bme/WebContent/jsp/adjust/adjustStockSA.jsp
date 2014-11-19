<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
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
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="adjustStockSAForm" class="com.isecinc.pens.web.adjuststock.AdjustStockSAForm" scope="session" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adjust_stock_sa.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}

span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
}
function clearForm(path){
	var form = document.adjustStockSAForm;
	form.action = path + "/jsp/adjustStockSAAction.do?do=clear";
	form.submit();
	return true;
}

function cancel(path){
	var form = document.adjustStockSAForm;
	if(confirm("ยืนยันการยกเลิกรายการ")){
		form.action = path + "/jsp/adjustStockSAAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function back(path){
	var form = document.adjustStockSAForm;
	form.action = path + "/jsp/adjustStockSAAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}
function validateItems(){
	var pass = true;
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	
	if(rows.length ==1){
		return false;
	}
	
	var itemIssue = document.getElementsByName("itemIssue");
	var itemIssueQty = document.getElementsByName("itemIssueQty");
	var itemReceipt = document.getElementsByName("itemReceipt");
	var itemReceiptQty = document.getElementsByName("itemReceiptQty");
	
	//alert(itemIssue.length);
	
	var errorList = new Object();
	for(var i= 0;i<itemIssue.length;i++){
		var rowObj = new Object();
		var lineClass ="lineE";
		if(i%2==0){
			lineClass = "lineO";
		}
		
		//alert(itemIssue[i].value );
		
		if(itemIssue[i].value == "" && itemReceipt[i].value =="" 
			&& itemIssueQty[i].value=="" && itemIssueQty[i].value=="0"
			&& itemReceiptQty[i].value=="" && itemReceiptQty[i].value=="0"){
			
			rows[i+1].className ='lineError';
			pass = false;
		}else if(itemIssue[i].value != ""){
			if(    itemReceipt[i].value =="" 
				|| itemIssueQty[i].value=="" || itemIssueQty[i].value=="0"
				|| itemReceiptQty[i].value=="" || itemReceiptQty[i].value=="0"){
				
				rows[i+1].className ='lineError';
				pass = false;
			}else{
				rows[i+1].className =lineClass;
			}
			
		}else if(itemReceipt[i].value !="" ){
			if( itemReceiptQty[i].value=="" || itemReceiptQty[i].value=="0"){
				rows[i+1].className ='lineError';
				pass = false;
			}else{
				rows[i+1].className =lineClass;
			}
		}else{
			rows[i+1].className =lineClass;
		} //if
		
		//alert("rows["+i+"]:"+rows[i].className);
		
	}// for
	return pass ;
}

function save(path){
	var form = document.adjustStockSAForm;
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	
	var transactionDate =$('#transactionDate').val();
	var storeName =$('#storeName').val();
	
	if(transactionDate ==""){
		alert("กรุณากรอกวันที่  Transaction Date");
		return false;
	}
	if(storeName ==""){
		alert("กรุณากรอก ร้านค้า");
		return false;
	}
	
	if(rows.length ==1){
		alert("กรุณาเพิ่มรายการอย่างน้อย 1 รายการ");
		return false;
	}
	
	var pass = validateItems();
	if(pass){
	   form.action = path + "/jsp/adjustStockSAAction.do?do=save";
	   form.submit();
	   return true;
    }else{
    	alert("ใส่ข้อมูลไม่ถูกต้อง");
    	return false;
    }
}

function exported(path){
   var form = document.adjustStockSAForm;
   form.action = path + "/jsp/adjustStockSAAction.do?do=exported";
   form.submit();
   return true;
}

function search(path){
	var form = document.adjustStockSAForm;
	var transactionDate =$('#transactionDate').val();
	var storeCode =$('#storeCode').val();
	if(transactionDate ==""){
		alert("กรุณากรอกวันที่  Transaction Date");
		return false;
	}
	if(storeCode ==""){
		alert("กรุณากรอก ร้านค้า");
		return false;
	}
	
	form.action = path + "/jsp/adjustStockSAAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function verifyData(path){
	var form = document.adjustStockSAForm;
	var transactionDate =$('#transactionDate').val();
	var storeCode =$('#storeCode').val();
	if(transactionDate ==""){
		alert("กรุณากรอกวันที่  Transaction Date");
		return false;
	}
	if(storeCode ==""){
		alert("กรุณากรอก ร้านค้า");
		return false;
	}
	
	form.action = path + "/jsp/adjustStockSAAction.do?do=verifyData&action=newsearch";
	form.submit();
	return true;
}

function openPopupProduct(path,seqNo,types){
	var param = "&types="+types+"&seqNo="+seqNo;
	url = path + "/jsp/searchProductPopupAction.do?do=prepare2&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function addRow(path){
	var form = document.adjustStockSAForm;
	form.action = path + "/jsp/adjustStockSAAction.do?do=addRow";
	form.submit();
	return true;
}

function removeRow(path){
	var form = document.adjustStockSAForm;
	//alert(isSelectOne());
	
	if(isSelectOne()){
		form.action = path + "/jsp/adjustStockSAAction.do?do=removeRow";
		form.submit();
		return true;
	}
	return false;
}

function isSelectOne(){
	//todo play with type
	var chk = document.getElementsByName("linechk");
	//alert(chk.length);
	
	var bcheck=false;
	for(var i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	return true;
}

function openPopupCustomer(path,types,storeType){
    var param = "&types="+types;
        param += "&storeType="+storeType;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare2&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,types){
	//alert(types+":"+desc);
	if("from" == types){
		document.getElementsByName("adjustStockSA.storeCode")[0].value = code;
		document.getElementsByName("adjustStockSA.storeName")[0].value = desc;
	}
}

function getStoreNameKeypress(e,storeCode){
	var form = document.adjustStockForm;
	if(e != null && e.keyCode == 13){
		if(storeCode.value ==''){
			form.storeName.value = '';
		}else{
			getStoreNameModel(storeCode);
		}
	}
}

function getStoreNameModel(storeCode){
	var returnString = "";
	var form = document.adjustStockSAForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameAjax.jsp",
			data : "custCode=" + storeCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	form.storeName.value = returnString;	
}

function getProductKeypress(e,type,itemCode,seqNo){
	var form = document.adjustStockSAForm;
	var itemAdjustDesc = document.getElementsByName("itemAdjustDesc");
	var itemAdjustUom = document.getElementsByName("itemAdjustUom");
	
	
	if(e != null && e.keyCode == 13){
		if(itemCode.value ==''){
			itemAdjustDesc[seqNo-1].value = '';
			itemAdjustUom[seqNo-1].value ='';
		}else{
			getProductModel(type,itemCode,seqNo);
		}
		
	}
}

function getProductModel(type,itemCode,seqNo){
	var itemAdjust = document.getElementsByName("itemAdjust");
	var itemAdjustDesc = document.getElementsByName("itemAdjustDesc");
	var itemAdjustUom = document.getElementsByName("itemAdjustUom");
	
	var returnString = "";
	var form = document.adjustStockForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoProduct.jsp",
			data : "itemCode=" + itemCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	
		if(returnString==''){
			alert("ไม่พบข้อมูล item "+itemCode.value);
			itemCode.focus();
			
			itemAdjust[seqNo-1].value = '';
			itemAdjustDesc[seqNo-1].value = '';
		    itemAdjustUom[seqNo-1].value ='';
		    
		}else{
			var s = returnString.split("|");
			itemAdjustDesc[seqNo-1].value = s[0];
			itemAdjustUom[seqNo-1].value ='EA';
		}
	
}


function isNum(obj){
  if(obj.value != ""){
	var newNum = parseFloat(obj.value);
	if(isNaN(newNum)){
		alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
		obj.value = "";
		obj.focus();
		return false;
	}else{return true;}
   }
  return true;
}


function setProductMainValue(seqNo,types,code,desc,price){
	//alert("seqNo:"+seqNo);
	document.getElementsByName("itemAdjust")[seqNo-1].value = code;
	document.getElementsByName("itemAdjustDesc")[seqNo-1].value = desc;
	document.getElementsByName("itemAdjustUom")[seqNo-1].value = 'EA';

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
				<jsp:param name="function" value="adjustStockSA"/>
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
						<html:form action="/jsp/adjustStockSAAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Transaction Date</td>
									<td >
									 <c:if test="${adjustStockSAForm.mode=='add'}">
										  <html:text property="adjustStockSA.transactionDate" styleId="transactionDate" size="20" readonly="true" />
										  <font color="red">*</font>
								     </c:if>
								     <c:if test="${adjustStockSAForm.mode =='edit'}">
								         <html:text property="adjustStockSA.transactionDate" styleId="transactionDateX" size="20" readonly="true" styleClass="disableText"/>
								     </c:if>
								     <c:if test="${adjustStockSAForm.mode =='view'}">
								         <html:text property="adjustStockSA.transactionDate" styleId="transactionDateX" size="20" readonly="true" styleClass="disableText"/>
								     </c:if>
									</td>
								</tr>
								 <tr>
                                    <td> รหัสร้านค้า</td>
									<td >
									  <c:if test="${adjustStockSAForm.mode=='add'}">
						                 <html:text property="adjustStockSA.storeCode" styleId="storeCode" size="20" 
						                  onkeypress="getStoreNameKeypress(event,this)" onblur="getStoreNameModel(this)"
						                  />
									     <font color="red">*</font>-
									     <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','all')"/>
									  </c:if>
									  <c:if test="${adjustStockSAForm.mode == 'edit'}">
									     <html:text property="adjustStockSA.storeCode" styleId="storeCode" size="20" 
						                  readonly="true"  styleClass="disableText"/>
									  </c:if>
									    <html:text property="adjustStockSA.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="40"/>
									</td>
								</tr>
								
								<tr>
                                    <td> Document No</td>
									<td >
						               <html:text property="adjustStockSA.documentNo" styleId="documentNo" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td> Status</td>
									<td >
						               <html:text property="adjustStockSA.statusDesc"  size="25" readonly="true" styleClass="disableText"/>
						               <html:hidden property="adjustStockSA.status" />
						              
									</td>
								</tr>
						   </table>
					  </div>

            <c:if test="${adjustStockSAForm.verify==true}">
                  <c:if test="${adjustStockSAForm.adjustStockSA.canEdit == true}">
                        <div align="left">
                        &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow('${pageContext.request.contextPath}');"/>	
							<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow('${pageContext.request.contextPath}');"/>	
						</div>
				  </c:if>  			
				  <c:if test="${adjustStockSAForm.resultsSize > 0}">
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableAj">
						       <tr>
									<th >Seq no</th>
									<th ><input type="checkbox" name="chkAll"
										onclick="checkSelect(this,document.getElementsByName('linechk'));" /></th>
									
									<th >Item Adjust</th>
									<th >Issue Description</th>
									<th >UOM</th>
									<th >Adjust Qty</th>
									
							</tr>
						
							<c:forEach var="results" items="${adjustStockSAForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
							
									<tr class="<c:out value='${tabclass}'/>">
										<td class="data_seqNo" align="center">${results.seqNo}</td>
										<td class="data_chkbox" align="center"><input type="checkbox" name="linechk" value="${results.seqNo}"/></td>
										<td class="data_itemAdjust" nowrap>
										   <c:if test="${results.canEdit == true}">
											    <input type="text" name="itemAdjust" id="itemAdjust" value ="${results.itemAdjust}" size="20" 
											    onkeypress="getProductKeypress(event,'issue',this,${results.seqNo})"
											    onchange="getProductModel('issue',this,${results.seqNo})"
											    />
											     <input  tabindex="-1" type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}',${results.seqNo},'itemAdjust')"
											     class="enableNumber"/>
										   </c:if>
										   <c:if test="${results.canEdit == false}">
											    <input type="text" name="itemAdjust" id="itemAdjust" value ="${results.itemAdjust}" size="20" 
											     readonly class="disableNumber"/>
										   </c:if>

										</td>
										<td class="data_itemAdjustDesc"><input tabindex="-1" type="text" name="itemAdjustDesc" value ="${results.itemAdjustDesc}" size="30" readonly class="disableText"/></td>
										<td class="data_itemAdjustUom"><input tabindex="-1" type="text" name="itemAdjustUom" value ="${results.itemAdjustUom}" size="10" readonly class="disableText"/></td>
										
										<td class="data_itemAdjustQty" align="center">
											<c:if test="${results.canEdit == true}">
											    <input type="text" name="itemAdjustQty" id="itemAdjustQty" value ="${results.itemAdjustQty}" size="8" onblur="isNum(this)" 
											    class="enableNumber"/>
											</c:if>
											<c:if test="${results.canEdit == false}">
											   <input type="text" name="itemAdjustQty" id="itemAdjustQty" value ="${results.itemAdjustQty}" size="8" readonly class="disableNumber"/>
											</c:if>
										</td>
									</tr>
							  </c:forEach>
						</c:if>
					</table>
								
								
					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
									<tr>
										<td align="left">
										 <c:if test="${adjustStockSAForm.adjustStockSA.canEdit ==true}">
												<a href="javascript:save('${pageContext.request.contextPath}')">
												  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
												</a>
										  </c:if>	
										 <c:if test="${adjustStockSAForm.mode=='add'}">			
											<a href="javascript:clearForm('${pageContext.request.contextPath}')">
											  <input type="button" value="   Clear   " class="newPosBtnLong">
											</a>
										</c:if>			
										<a href="javascript:back('${pageContext.request.contextPath}')">
											  <input type="button" value="   ปิดหน้าจอ  " class="newPosBtnLong">
										</a>		
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										   	 <c:if test="${adjustStockSAForm.adjustStockSA.canCancel ==true}">
												<a href="javascript:cancel('${pageContext.request.contextPath}')">
												  <input type="button" value="   ยกเลิก   " class="newPosBtnLong"> 
												</a>
										  </c:if>					
										</td>
									</tr>
						</table>
					</div>
				</c:if>
					<!-- ************************Result ***************************************************-->

					<!-- hidden field -->
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