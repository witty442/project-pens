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
<jsp:useBean id="scanCheckForm" class="com.isecinc.pens.web.pick.ScanCheckForm" scope="session" />
<%
 String mode = scanCheckForm.getMode();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

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
	 new Epoch('epoch_popup', 'th', document.getElementById('openDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('closeDate'));
	 
	 //alert('<%=mode%>');
	 <%if( mode.equals("edit")){ %>
	    //add blank row
	    addRow();
	    //load data 
	    getIssueModel('<%=scanCheckForm.getBean().getIssueReqNo()%>','<%=scanCheckForm.getBean().getWareHouse()%>','<%=scanCheckForm.getBean().getBoxNo()%>');
	   
	    /** Backup totalQty for add qty in screen **/
	     document.getElementsByName("totalQtyTemp")[0].value = document.getElementsByName("bean.totalQty")[0].value;
	  
	  <%}else if( mode.equals("view")){ %>
	    //load data 
	    getIssueModel('<%=scanCheckForm.getBean().getIssueReqNo()%>','<%=scanCheckForm.getBean().getWareHouse()%>','<%=scanCheckForm.getBean().getBoxNo()%>');
	   
	    /** Backup totalQty for add qty in screen **/
	     document.getElementsByName("totalQtyTemp")[0].value = document.getElementsByName("bean.totalQty")[0].value;
	     
	 <%}else if( mode.equals("add")){ %>
	    addRow();
	   /** Backup totalQty for add qty in screen **/
	    document.getElementsByName("totalQtyTemp")[0].value = document.getElementsByName("bean.totalQty")[0].value;
	 <%}%>
	 /** Calc in Page **/
	 calcTotalRow();
	 /** Sum All doc **/
	 sumTotal();
}
function clearForm(path){
	var form = document.scanCheckForm;
	form.action = path + "/jsp/scanCheckAction.do?do=clear";
	form.submit();
	return true;
}
function newBox(path){
	var form = document.scanCheckForm;
	var issueReqNo =$('#issueReqNo').val();
	var storeCode =$('#storeName').val();
	
	//enable list field strut bug no get value from disable field
	 document.getElementsByName("bean.wareHouse")[0].disabled = false;
	 document.getElementsByName("bean.custGroup")[0].disabled = false;
	 
	if(issueReqNo ==""){
		alert("กรุณากรอก issueReqNo");
		return false;
	}
	//validate item
	var barcode = document.getElementsByName("materialMaster");
	//alert("barcode[0]"+barcode[0].value);
	if(barcode[0].value == ""){
		alert("กรุณากรอก ใส่รายการ อย่างน้อย 1 รายการ");
		return false;
	}
	
	if(storeCode ==""){
		alert("ไม่พบข้อมูล รหัสร้านค้า ไม่สามารถทำงานต่อได้  ");
		return false;
	}
	
	form.action = path + "/jsp/scanCheckAction.do?do=newBox";
	form.submit();
	return true;
}
function printReport(path){
	var form = document.scanCheckForm;
	form.action = path + "/jsp/scanCheckAction.do?do=printReport";
	form.submit();
	return true;
}
function cancel(path){
	var form = document.scanCheckForm;
	if(confirm("ยืนยันการยกเลิกรายการนี้")){
		form.action = path + "/jsp/scanCheckAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function back(path){
	var form = document.scanCheckForm;
	form.action = path + "/jsp/scanCheckAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.scanCheckForm;
	var issueReqNo =$('#issueReqNo').val();
	var storeCode =$('#storeName').val();
	
	//enable list field strut bug no get value from disable field
	 document.getElementsByName("bean.wareHouse")[0].disabled = false;
	 document.getElementsByName("bean.custGroup")[0].disabled = false;
	 
	if(issueReqNo ==""){
		alert("กรุณากรอก issueReqNo");
		return false;
	}
	//validate item
	var barcode = document.getElementsByName("materialMaster");
	if(barcode[0].value == ""){
		alert("กรุณากรอก ใส่รายการ อย่างน้อย 1 รายการ");
		return false;
	}
	
	if(storeCode ==""){
		alert("ไม่พบข้อมูล รหัสร้านค้า ไม่สามารถทำงานต่อได้  ");
		return false;
	}
	
	form.action = path + "/jsp/scanCheckAction.do?do=save";
	form.submit();
	return true;
}

function openIssuePopup(path){
    var param = "";
	url = path + "/jsp/searchStockIssuePopupAction.do?do=prepare&act       ion=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=700px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,storeCode,storeName,wareHouse,requestor,issueReqDate,custGroup,remark,totalReqQty,totalQty){

	document.getElementsByName("bean.issueReqNo")[0].value = code;
		
	document.getElementsByName("bean.storeCode")[0].value = storeCode;
	document.getElementsByName("bean.storeName")[0].value = storeName;
	document.getElementsByName("bean.wareHouse")[0].value = wareHouse;
	document.getElementsByName("bean.requestor")[0].value = requestor;
	document.getElementsByName("bean.issueReqDate")[0].value = issueReqDate;
	document.getElementsByName("bean.custGroup")[0].value = custGroup;
	document.getElementsByName("bean.remark")[0].value = remark;
	document.getElementsByName("bean.totalReqQty")[0].value = totalReqQty;
	
	  /** Backup totalQty for add qty in screen **/
	 document.getElementsByName("totalQtyTemp")[0].value = totalQty;
	 document.getElementsByName("bean.totalQty")[0].value = totalQty;
}

function getIssueKeypress(e,code){
	if(e != null && e.keyCode == 13){
		if(code.value ==''){
			form.name.value = '';
		}else{
			var warehouse = document.getElementsByName("bean.wareHouse")[0].value;
			var boxNo = document.getElementsByName("bean.boxNo")[0].value;
			getIssueModel(code.value,warehouse,boxNo);
		}
	}
}

//Return String :jobName|StoreCode|StioreName|StoreNo|subInv
function getIssueModel(code,warehouse,boxNo){
	var form = document.scanCheckForm;
	//alert("code:"+code+",warehouse:"+warehouse+"boxNo:"+boxNo);
	
	 <%if( mode.equals("edit")){ %>
		if(   code =='' || warehouse =='' || boxNo ==''
		   || code =='null' || warehouse =='null' || boxNo =='null'
		){
			return false;
		}
	<%}%>
	var returnString = "";
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoStockIssue.jsp",
			data : "code=" + code+"&warehouse="+warehouse+"&boxNo="+boxNo+"&mode=<%=mode%>",
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	if(returnString !=''){
		var retArr = returnString.split("|");
		
		form.storeCode.value = retArr[0];
		form.storeName.value = retArr[1];
		form.wareHouse.value = retArr[2];
		form.requestor.value = retArr[3];
		form.issueReqDate.value = retArr[4];
		form.custGroup.value = retArr[5];
		form.remark.value = retArr[6];
		form.totalReqQty.value = retArr[7];
		
	     /** Backup totalQty for add qty in screen **/
	    document.getElementsByName("totalQtyTemp")[0].value = retArr[8];
	    document.getElementsByName("bean.totalQty")[0].value = retArr[8];
	}else{
		alert("ไม่พบข้อมูล");
		form.issueReqNo.value = '';
		form.storeCode.value = '';
		form.storeName.value = '';
		form.wareHouse.value = '';
		form.requestor.value = '';
		form.issueReqDate.value = '';
		form.custGroup.value = '';
		form.remark.value = '';
		form.totalReqQty.value ='0';
		
		document.getElementsByName("totalQtyTemp")[0].value = '0';
	    document.getElementsByName("bean.totalQty")[0].value = '0';
	}
	
}

function addRow(path){
	var form = document.scanCheckForm;
	// $('#myTable tr').length;
	var rows = $('#tblProduct tr').length-1;
	var className = 'lineO';
	if(rows%2 !=0){
		className = 'lineE';
	}

	var lineId = rows+1;
	
	//alert("lineId["+lineId+"]");
	
	var rowData ="<tr class='"+className+"'  onmouseover='getRowIndex(this)'> "+
	    "<td class='td_text_center' width='10%'> <input type='checkbox' tabindex ='-1' name='linechk' value='0'/></td>"+
	    "<td class='td_text_center' width='20%'> <input type='text' name='barcode' size='30'  "+
	    " onkeypress='getProductKeypress(event,this,"+lineId+")' "+
	
	    " />  </td>"+
	    "<td class='td_text_right' width='20%'> <input type='text' tabindex ='-1' name='materialMaster' size='25'  onkeypress='getProductKeypressByMat(event,this,"+lineId+")'/></td>"+
	    "<td class='td_text_center' width='10%'> <input type='text' tabindex ='-1' name='groupCode' readonly class='disableText' size='30' /></td>"+
	    "<td class='td_text_center' width='10%'> <input type='text' tabindex ='-1' name='pensItem' readonly class='disableText' size='15' />"+ 
	    "  <input type='hidden' tabindex ='-1' id='status' name='status' value='' /></td>"+
	  /*   "<td class='data_pensItem'> </td>"+  */
	    "</tr>";

    $('#tblProduct').append(rowData);
    //set focus default
    var barcode = document.getElementsByName("barcode");
    barcode[lineId-1].focus();
}

function sumTotal(){
	var totalRow = document.getElementsByName("totalRow");
    var totalQty = document.getElementsByName("bean.totalQty");
    var totalQtyStart = document.getElementsByName("totalQtyTemp");
    
    //alert(totalQtyStart[0].value);
     //reset 
    totalQty[0].value = 0;
    
   // alert("totalQtyStart:"+totalQtyStart[0].value);
   // alert(totalQty[0].value);
   
    totalQty[0].value =  parseInt(totalQtyStart[0].value) + parseInt(totalRow[0].value);
}

/* function calcTotalRow(){
	var rows = $('#tblProduct tr').length-1;//1,head row ,2 blank row
	var barcodeLastRow = document.getElementsByName("barcode")[rows-1]; //alert(barcodeLastRow.value);
	if(barcodeLastRow.value ==''){
		rows = rows-1;// blank row
	}
	//Calc Row
    var totalRow = document.getElementsByName("totalRow");
    totalRow[0].value = rows;
} */

function calcTotalRow(){
	var count= 0;
	var rows = $('#tblProduct tr').length-1;//1,head row ,2 blank row
	for(var r=0;r<rows;r++){
	   var barcodeObj = document.getElementsByName("barcode")[r]; 
	   var statusObj = document.getElementsByName("status")[r]; 
	   //alert(barcodeObj.value+":"+statusObj.value);
	   
       if(barcodeObj.value != '' && statusObj.value != 'AB'){
    	   count++;
       }
	}
	//set total in page
	var totalRow = document.getElementsByName("totalRow");
    totalRow[0].value = count;
	return count;
}

function calcTotalRowByBarcode(barcode){
	var count= 0;
	var rows = $('#tblProduct tr').length-1;//1,head row ,2 blank row
	for(var r=0;r<rows;r++){
	   var barcodeObj = document.getElementsByName("barcode")[r]; 
	   var statusObj = document.getElementsByName("status")[r]; 
	   //alert(barcodeObj.value+":"+statusObj.value);
	   
       if(barcodeObj.value != '' && barcodeObj.value ==barcode && statusObj.value != 'AB'){
    	   count++;
       }
	}
	return count;
}

function getRowIndex(rowTableObj){
   //alert(rowTableObj.rowIndex);
   document.getElementById('currentRowIndex').value = rowTableObj.rowIndex;
}

function removeRow(path){
	//todo play with type
	var tbl = document.getElementById('tblProduct');
	var chk = document.getElementsByName("linechk");
	var drow;
	var bcheck=false;
	for(var i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			drow = tbl.rows[i+1];
			//$(drow).remove();
			bcheck=true;
			
			removeRowByIndex(path,drow,i);
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	 /** Calc in Page **/
	 calcTotalRow();
	 /** Sum All doc **/
	 sumTotal();
}

function removeRowByIndex(path,drow,index){
     //alert(drow);
	//todo play with type	
	drow.style.display ='none';
	//set  staus = AB 
	//alert(index);
	document.getElementsByName("status")[index].value ='AB';
}
	
function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
}

function getProductKeypress(e,barcodeObj,lineId){
	//materialMaster groupCode pensItem wholePriceBF retailPriceBF
	//alert(barcode.value);
	var form = document.scanCheckForm;
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	
	//Get rowIndex
	//lineId = document.getElementById('currentRowIndex').value;
	
	if(e != null && e.keyCode == 13){
	
		if(barcodeObj.value ==''){
			materialMaster[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';
		}else{
			var found = getProductModel(barcodeObj,lineId);
			if(found){
				//Count in screen
				calcTotalRow();
				//count all issue
				sumTotal();
				
				//Add New Row Auto
				addRow();
				
				barcode[lineId].focus();
				
				//Set Prev row readonly 
				barcode[lineId-1].className ="disableText";
				barcode[lineId-1].readOnly = true;
			}
		}
	}
}

function getProductModel(barcodeObj,lineId){
	var found = false;
	var form = document.scanCheckForm;
	var warehouse =document.getElementsByName("bean.wareHouse")[0].value;
	var boxNo = document.getElementsByName("bean.boxNo")[0].value;
	
	if(boxNo ==''){
	   boxNo ='0';
	}
	
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	
	var returnString = "";
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoBarcodeFromStockIssue.jsp",
			data : "itemCode=" + barcodeObj.value+"&issueReqNo="+form.issueReqNo.value+"&warehouse="+warehouse+"&boxNo="+boxNo,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	   // alert("x:"+returnString);
	    
		if(returnString==''){
			alert("ไม่พบข้อมูลสินค้า  "+barcodeObj.value +"ในใบเบิกนี้");
			barcodeObj.focus();
			
			barcode[lineId-1].value = '';
			materialMaster[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';
			
		}else{
			var s = returnString.split("|");
			
			barcode[lineId-1].value = s[0];
			materialMaster[lineId-1].value = s[1];
			groupCode[lineId-1].value = s[2];
			pensItem[lineId-1].value = s[3];
			
			found = true;
			
			//validate remain qty in stock issue
			var countQtyScreen = calcTotalRowByBarcode(barcodeObj.value);
			var remainQtyNotinCurrentBoxNo = s[4];
			
			//alert("remainQtyNotinCurrentBoxNo["+remainQtyNotinCurrentBoxNo+"]countQtyScreen["+countQtyScreen+"]");
			
			if(countQtyScreen > remainQtyNotinCurrentBoxNo){
				found = false;
				alert("ยอดเกินจำนวนที่ได้มีการขอเบิก");
				barcodeObj.focus();
				
				barcode[lineId-1].value = '';
				materialMaster[lineId-1].value = '';
				groupCode[lineId-1].value = '';
				pensItem[lineId-1].value = '';
				
			}else{
			   barcodeObj.className = 'disableText';
			   materialMaster[lineId-1].className = 'disableText';
			}
		}
	return found;
}

function getProductKeypressByMat(e,matObj,lineId){
	//materialMaster groupCode pensItem wholePriceBF retailPriceBF
	//alert(barcode.value);

	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");

	if(e != null && e.keyCode == 13){
		if(matObj.value ==''){
			barcode[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';
		}else{
			var found = getProductModelByMat(matObj,lineId);
			if(found){
			    //Calc In Screen
				calcTotalRow();
				
				//count all issue
				sumTotal();
				
				//Add New Row Auto
				addRow();
				
				barcode[lineId].focus();
				
				//Set Prev row readonly  
				barcode[lineId-1].className= "disableText";
				barcode[lineId-1].readOnly = true;
				
				materialMaster[lineId-1].className ="disableText";
				materialMaster[lineId-1].readOnly = true;
			}
		}
	}
}

function getProductModelByMat(matObj,lineId){
	var found = false;
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	
	var warehouse =document.getElementsByName("bean.wareHouse")[0].value;
	var boxNo = document.getElementsByName("bean.boxNo")[0].value;
	
	var returnString = "";
    var form = document.scanCheckForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoBarcodeFromStockIssue.jsp",
			data : "matCode=" + matObj.value+"&issueReqNo="+form.issueReqNo.value+"&warehouse="+warehouse+"&boxNo="+boxNo,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	   // alert("x:"+returnString);
	    
		if(returnString==''){
			alert("ไม่พบข้อมูลสินค้า  "+matObj.value);
			matObj.focus();
			
			barcode[lineId-1].value = '';
			materialMaster[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';

		}else{
			var s = returnString.split("|");
			
			barcode[lineId-1].value = s[0];
			materialMaster[lineId-1].value = s[1];
			groupCode[lineId-1].value = s[2];
			pensItem[lineId-1].value = s[3];
	
			found = true;
			//validate remain qty in stock issue
			var countQtyScreen = calcTotalRowByBarcode(barcode[lineId-1].value);
			var remainQtyNotinCurrentBoxNo = s[4];
			
			//alert("remainQtyNotinCurrentBoxNo["+remainQtyNotinCurrentBoxNo+"]countQtyScreen["+countQtyScreen+"]");
			
			if(countQtyScreen > remainQtyNotinCurrentBoxNo){
				found = false;
				alert("ยอดเกินจำนวนที่ได้มีการขอเบิก");
				matObj.focus();
				
				barcode[lineId-1].value = '';
				materialMaster[lineId-1].value = '';
				groupCode[lineId-1].value = '';
				pensItem[lineId-1].value = '';
				
			}else{
			   matObj.className = 'disableText';
			   barcode[lineId-1].className = 'disableText';
			}
			
		}
	return found;
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
				<jsp:param name="function" value="scanCheck"/>
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
						<html:form action="/jsp/scanCheckAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td>  Warehouse</td>
									<td>					
										 <html:select property="bean.wareHouse" styleId="wareHouse" styleClass="disableText" >
											<html:options collection="warehouseList_scan" property="key" labelProperty="name"/>
									    </html:select>
									</td>
									
									<td>  กลุ่มร้านค้า </td>
									<td >					
										 <html:select property="bean.custGroup" styleId="custGroup"  styleClass="disableText">
											<html:options collection="custGroupList_scan" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
									<td> 
                                                                                                                                            รหัสร้านค้า    
                                    </td>
									<td >
									   <html:text property="bean.storeCode" styleId="storeCode" size="20" readonly="true" styleClass="disableText"/>
									   -<html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									</td>
								</tr>
								<tr>
                                    <td>Issue request No</td>
                                    <c:choose>
                                      <c:when test="${scanCheckForm.mode == 'add'}">
										     <td >
									             <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20" 
							                               onkeypress="getIssueKeypress(event,this)"/> 
							                      <font color="red">*</font>
							                     <input type="button" name="x1" value="..." onclick="openIssuePopup('${pageContext.request.contextPath}')"/>
											 </td>
									    </c:when>
										<c:otherwise>    
												<td >
									               <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20" readonly="true" styleClass="disableText"/>
												</td>
								        </c:otherwise>
								      </c:choose>
								     <td> ผู้เบิก</td>
									<td >					
										  <html:text property="bean.requestor" styleId="requestor" size="20" readonly="true" styleClass="disableText"/>
									</td>
								     <td> วันที่ Checkout</td>
									<td colspan="1">					
										  <html:text property="bean.checkOutDate" styleId="checkOutDate" size="20" readonly="true" styleClass="disableText"/>
										   จำนวนรวมชิ้นที่เบิก
										    <html:text property="bean.totalReqQty" styleId="totalReqQty" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								<tr>
                                    <td> หมายเหตุ</td>
									<td colspan="3">
						                  <html:text property="bean.remark" styleId="remark" size="90" styleClass="disableText"/>
									</td>
									
									<td>  Issue req date</td>
									<td colspan="2">					
										  <html:text property="bean.issueReqDate" styleId="issueReqDate" size="20" readonly="true" styleClass="disableText"/>
										      เลขที่กล่อง Checkout
										  <html:text property="bean.boxNo" styleId="boxNo" size="20" readonly="true" styleClass="disableText"/>
									</td>	
								</tr>	
				   </table>
                 <!-- Table Data -->
				<c:if test="${scanCheckForm.results != null}">
                  	    <c:if test="${scanCheckForm.bean.canEdit == true}">
	                        <div align="left">
								<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow('${pageContext.request.contextPath}');"/>	
								<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow('${pageContext.request.contextPath}');"/>	
							</div>
				        </c:if> 
										    		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch" width="100%">
						    <tr>
							<!-- 	<th >No</th> -->
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >Barcode</th>
								<th >Wacoal Mat.</th>
								<th >Group Code</th>
								<th >PENS Item</th>
								<!-- <th >Status</th> -->						
							</tr>
							<c:forEach var="results" items="${scanCheckForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>" onmouseover="getRowIndex(this)">
										<%-- <td class="data_no">
										   <input type="text" name="lineId" value ="${results.lineId}" size="5" readonly class="disableText"/>
										</td> --%>
										<td class="td_text_center" width="10%">
										  <input type="checkbox" name="linechk" value="${results.lineId}"/>
										</td>
										<td class="td_text_center"  width="20%">
										    <input type="text" name="barcode" id="barcode" value ="${results.barcode}" size="30" 
											    onkeypress="getProductKeypress(event,this,${results.lineId})"
											    readonly="${results.barcodeReadonly}" class="${results.barcodeStyle}" 
											   <%--  onchange="getProductModel(this,${results.lineId})" --%>
											/>
                                        </td>
										<td class="td_text_center" width="20%">
											<input  onkeypress="getProductKeypressByMat(event,this,${results.lineId})" type="text" name="materialMaster"
											 value ="${results.materialMaster}" class="disableText" readonly="true" size="25"/>
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="groupCode" value ="${results.groupCode}" size="30" readonly class="disableText"/>
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="pensItem" value ="${results.pensItem}" size="15" readonly class="disableText"/>
										   <input type="hidden" name="status" id="status"/>
										</td>
										
										<%-- <td class="td_text_center" width="20%">${results.statusDesc}</td> --%>
									</tr>
							
							  </c:forEach>
					</table>
						 <div align="center">
							<font size="3"> <b>สรุปยอดจำนวนตัว <input type="text" name ="totalRow" class="disableNumber" value="" readonly/></b>	
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							 <b>สรุปยอด Check-out ทั้งใบเบิก <html:text property="bean.totalQty" styleId="totalQty" size="20" readonly="true" styleClass="disableNumber"/></b>	
							  <input type="hidden" name="totalQtyTemp" id="totalQtyTemp" />
							  </font>
						</div>
				</c:if>
						   <!-- Table Data -->
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									
										<%--  <c:if test="${scanCheckForm.bean.canPrint == true}"> --%>
											<a href="javascript:printReport('${pageContext.request.contextPath}')">
											  <input type="button" value="พิมพ์ไปปะหน้ากล่อง" class="newPosBtnLong">
											</a> 
									<%-- 	</c:if> --%>
										
									     <c:if test="${scanCheckForm.bean.canEdit == true}">
											<a href="javascript:newBox('${pageContext.request.contextPath}')">
											  <input type="button" value="   เริ่มกล่องใหม่   " class="newPosBtnLong">
											</a>
										</c:if>
										
										 <c:if test="${scanCheckForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
											 </a>
										 </c:if>
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>	
															
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>	
										</td>
										<td align="right">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										 <c:if test="${scanCheckForm.bean.canCancel == true}">
											<a href="javascript:cancel('${pageContext.request.contextPath}')">
											  <input type="button" value="   ยกเลิก   " class="newPosBtnLong">
											</a>
										</c:if>
									</td>
								</tr>
							</table>
					  </div>
					<!-- ************************Result ***************************************************-->
					<input type="text" name="currentRowIndex" id="currentRowIndex" />
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					<script>
					 //disable list field strut bug no get value from disable field
	                  document.getElementsByName("bean.wareHouse")[0].disabled = true;
	                  document.getElementsByName("bean.custGroup")[0].disabled = true;
					</script>
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