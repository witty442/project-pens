<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.pens.util.SIdUtils"%>
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
<jsp:useBean id="manualStockForm" class="com.isecinc.pens.web.manualstock.ManualStockForm" scope="session" />

<%

 /*clear session form other page */
 SessionUtils.clearSessionUnusedForm(request, "manualStockForm");

 String mode = manualStockForm.getMode();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('transDate'));
	 
	 <%if( !mode.equals("view")){ %>
	    addRow();
	 <%}%>
}
function clearForm(path){
	var form = document.manualStockForm;
	form.action = path + "/jsp/manualStockAction.do?do=clear";
	form.submit();
	return true;
}
function printReport(path){
	var form = document.manualStockForm;
	form.action = path + "/jsp/manualStockAction.do?do=printReport";
	form.submit();
	return true;
}

function cancel(path){
	if(confirm("กรุณายืนยัน การลบข้อมูลขายทั้งเอกสาร")){
		var form = document.manualStockForm;
		form.action = path + "/jsp/manualStockAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function back(path){
	var form = document.manualStockForm;
	form.action = path + "/jsp/manualStockAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.manualStockForm;
	var storeCode =$('#storeName').val();
	var saleDate =$('#saleDate').val();
	var custGroup =$('#custGroup').val();

	//validate item
	var barcode = document.getElementsByName("barcode");
	if(barcode[0].value == ""){
		alert("กรุณากรอก ใส่รายการ อย่างน้อย 1 รายการ");
		return false;
	}
	if(custGroup =="" ){
		alert("กรุณากรอกข้อมูลกลุ่มร้านค้า ");
		return false;
	}
	if(saleDate =="" ){
		alert("กรุณากรอก วันที่ขาย ");
		return false;
	}
	if(storeCode =="" ){
		alert("กรุณากรอก รหัสร้านค้า ");
		return false;
	}
	
	form.action = path + "/jsp/manualStockAction.do?do=save";
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
	var form = document.manualStockForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){

	var form = document.manualStockForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
	
	if(storeNo=='' || subInv==''){
		if(storeNo==''){
			alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
		}
		if(subInv==''){
			alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
		}
		form.storeCode.value = '';
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}else{
	   form.storeNo.value = storeNo;
	   form.subInv.value = subInv;
	}
	
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.manualStockForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("storeCode" == fieldName){
				form.storeCode.value = '';
				form.storeName.value = "";
				form.storeNo.value = "";
				form.subInv.value = "";
			}
		}else{
		  getCustName(custCode,fieldName);
		}
	}
}

function getCustName(custCode,fieldName){
	var returnString = "";
	var form = document.manualStockForm;
	var storeGroup = form.custGroup.value;
		var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameWithSubInvAjax.jsp",
				data : "custCode=" + custCode.value+"&storeGroup="+storeGroup,
				async: false,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		
		if("storeCode" == fieldName){
			if(returnString !=''){
				var retArr = returnString.split("|");
				form.storeName.value = retArr[0];
				
				if(retArr[1]=='' || retArr[2]==''){
					if(retArr[1]==''){
						alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
					}
					if(retArr[2]==''){
						alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
					}
					form.storeCode.value = '';
					form.storeName.value = "";
					form.storeNo.value = "";
					form.subInv.value = "";
				}else{
					form.storeNo.value = retArr[1];
					form.subInv.value = retArr[2];
				}
				
			}else{
				alert("ไม่พบข้อมูล");
				form.storeCode.focus();
				form.storeCode.value ="";
				form.storeName.value = "";
				form.storeNo.value = "";
				form.subInv.value = "";
			}
		}
}

function resetStore(){
	var form = document.manualStockForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}
}

function addRow(){
	// $('#myTable tr').length;
	var rows = $('#tblProduct tr').length-1;
	var className = 'lineO';
	if(rows%2 !=0){
		className = 'lineE';
	}

	var lineId = rows+1;
	//alert("lineId["+lineId+"]");
	
	var rowData ="<tr class='"+className+"'>"+
	    "<td class='td_text_center'> <input type='checkbox' tabindex ='-1' name='linechk' value='0'/>"+
	    "  <input type='hidden' tabindex ='-1' name='lineId' />"+
	    "</td>"+
	   
	    "<td class='td_text_center'> "+
	    "  <input type='text' "+
	    "   onkeypress=getAutoKeypress(event,this,"+lineId+",'BMEProduct','pensItem') "+
	    "   name='pensItem'  size='10' autoComplete='off' />"+
	    "</td>"+
	    "<td class='td_text_center'> "+
	    "  <input type='text' tabindex ='' "+
	    "   onkeypress=getAutoKeypress(event,this,"+lineId+",'BMEProduct','materialMaster') "+
	    "   name='materialMaster' size='15' autoComplete='off' />"+
	    "</td>"+
	    
	    "<td class='td_text_center'> <input type='text' tabindex ='-1' name='groupCode' readonly class='disableText' size='30' /></td>"+
	    "<td class='td_text_center'> <input type='text' name='qty' readonly class='' size='10' autoComplete='off'/></td>"+

	    "</tr>";
 
    $('#tblProduct').append(rowData);
    //set focus default
    var pensItem = document.getElementsByName("pensItem");
    pensItem[lineId-1].focus();
}

function removeRow(path){
	if(confirm("ยืนยันการลบ barcode นี้")){
		//todo play with type
		var tbl = document.getElementById('tblProduct');
		var chk = document.getElementsByName("linechk");
		var bcheck=false;
		var lineId = "";
		for(var i=chk.length-1;i>=0;i--){
			if(chk[i].checked){
				bcheck=true;
				lineId = chk[i].value;
				
				drow = tbl.rows[i+1];
				
				//alert("lineId:"+lineId);
				if(lineId=="" || lineId=="0"){
					removeRowBlank(drow);
				}else{
					removeRowUpdate(path,drow,i);
				}
			}
		}
		if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	}
	return false;
}
	
function removeRowUpdate(path,drow,index){
	//todo play with type	
	drow.style.display ='none';
	//set  staus = AB
	//alert(index);
	document.getElementsByName("status")[index].value ='AB';
	document.getElementsByName("statusDesc")[index].value ='Cancel';
	
	//update db
	save(path);
}
	
function removeRowBlank(drow){
	//todo play with type
	$(drow).remove();
}
	
function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
}

function getAutoKeypress(e,obj,lineId,pageName,inputName){
	var form = document.manualStockForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("BMEProduct" == pageName){
				document.getElementsByName("materialMaster")[lineId-1].value = '';
				document.getElementsByName("groupCode")[lineId-1].value = '';
				document.getElementsByName("pensItem")[lineId-1].value = '';
			}
		}else{
			getAutoDetail(obj,lineId,pageName,inputName);
		}
	}
}

function getAutoDetail(obj,lineId,pageName,inputName){
	var returnString = "";
	var form = document.manualStockForm;
	
	//prepare parameter
	var param = "";
	if("BMEProduct"==pageName){
		
		param  ="pageName="+pageName;
		param +="&custGroup="+form.custGroup.value
		if(inputName=='pensItem'){
		   param +="&pensItem="+obj.value;
		}else{
		   param +="&materialMaster="+obj.value;
		}
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
	 
	if("BMEProduct" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			document.getElementsByName("pensItem")[lineId-1].value = retArr[1];;
			document.getElementsByName("materialMaster")[lineId-1].value = retArr[2];
			document.getElementsByName("groupCode")[lineId-1].value = retArr[3];
			
		}else{
			alert("ไม่พบข้อมูล");
			if(inputName=='pensItem'){
				document.getElementsByName("pensItem")[lineId-1].focus();
			}else{
				document.getElementsByName("materialMaster")[lineId-1].focus();
			}
			document.getElementsByName("pensItem")[lineId-1].value = '';
			document.getElementsByName("materialMaster")[lineId-1].value = '';
			document.getElementsByName("groupCode")[lineId-1].value = '';
		}
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
				<jsp:param name="function" value="ManualStock"/>
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
						<html:form action="/jsp/manualStockAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						     <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Sale Date<font color="red">*</font></td>
									<td>					
									   <html:text property="bean.transDate" styleId="transDate" size="20"/>
									   Doc No
									    <html:text property="bean.docNo" styleId="docNo" size="20" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td> กลุ่มร้านค้า <font color="red">*</font></td>
									<td>		
										 <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
									<td >รหัสร้านค้า<font color="red">*</font>
									</td>
									<td align="left"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									
									  <html:hidden property="bean.subInv" styleId="subInv"/>
									  <html:hidden property="bean.storeNo" styleId="storeNo"/>
									</td>
								</tr>
								<tr>
                                    <td> หมายเหตุ<font color="red"></font></td>
									<td>		
										  <html:text property="bean.remark" styleId="remark" size="40"/>
									</td>
								</tr>
						   </table>
						   
				 <!-- Table Data -->
				<c:if test="${manualStockForm.results != null}">
	                      <div align="left">
							<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow();"/>	
							<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow();"/>	
					    </div>
				       	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
							<!-- 	<th >No</th> -->
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >PENS Item</th>
								<th >Material Master.</th>
								<th >Group Code</th>
								<th >QTY</th>						
							</tr>
							<c:forEach var="results" items="${manualStockForm.results}" varStatus="rows">
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
										   <input onkeypress="getAutoKeypress(event,this,${results.lineId},'BMEProduct','pensItem')"
										   type="text" name="pensItem" value ="${results.pensItem}" size="20" readonly class="disableText"/>
										
										</td>
										<td class="td_text_center" width="10%">
											<input  onkeypress="getAutoKeypress(event,this,${results.lineId},'BMEProduct','materialMaster')" 
											type="text" name="materialMaster" value ="${results.materialMaster}" size="25"/>
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="groupCode" value ="${results.groupCode}" size="30" readonly class="disableText"/>
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="qty" value ="${results.qty}" size="20" readonly class="disableNumber"/>
										</td>
										
									</tr>
							
							  </c:forEach>
					</table>
						 
				</c:if>
						   <!-- Table Data -->
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   <%--  <c:if test="${mttForm.bean.canEdit == true}"> --%>
											<a href="javascript:save('${pageContext.request.contextPath}')">
												  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
											 </a>
										<%-- </c:if> --%>
										
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
										 <c:if test="${manualStockForm.bean.canCancel == true}">
											<a href="javascript:removeRow('${pageContext.request.contextPath}')">
											  <input type="button" value="   ลบรายการ   " class="newPosBtnLong">
											</a>
										</c:if>
									</td>
								</tr>
							</table>
					  </div>
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