<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.isecinc.pens.web.stock.StockForm"%>
<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="com.pens.util.*"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<%
User user = (User) session.getAttribute("user");
%>
<script type="text/javascript">
window.onload = function(){
	  var path = document.getElementById("path").value;
	loadMe();
 	<%if( "submitedImport".equals(request.getAttribute("action"))){%>
 	    //lockscreen
 	    
 	    /** Init progressbar **/
		$(function() {
			// update the block message 
            $.blockUI({ message: "<h2>กำลังทำรายการ     กรุณารอสักครู่......</h2>" }); 
		}); 
 	    
 	    //import file Excel
 	    var url = path+'/jsp/batchTaskAction.do?do=prepare&pageAction=new&initBatchAction=initBatchFromPageByPopup&pageName=<%=BatchTaskConstants.IMPORT_B2B_MAKRO_FROM_EXCEL%>';
 	     popupFull(url,'Import Excel');
	<%}%>
	
	<%if( "submitedExport".equals(request.getAttribute("action"))){%>
	    //lockscreen
	    
	    /** Init progressbar **/
		$(function() {
			// update the block message 
	        $.blockUI({ message: "<h2>กำลังทำรายการ     กรุณารอสักครู่......</h2>" }); 
		}); 
		    
		  //Export file Excel
		  var url = path+'/jsp/batchTaskAction.do?do=prepare&pageAction=new&initBatchAction=initBatchFromPageByPopup&pageName=<%=BatchTaskConstants.EXPORT_B2B_MAKRO_TO_EXCEL%>';
		  popupFull(url,'Export Excel');

    <%}%>
    
	<%if(request.getAttribute("LOAD_EXPORT_FILE_NAME") != null){%>
	   var pageName = document.getElementsByName("pageName")[0].value;
	   document.b2bForm.action = path + "/jsp/b2bAction.do?do=loadExcel&pageName="+pageName+"&fileName=<%=Utils.isNull(request.getAttribute("LOAD_EXPORT_FILE_NAME"))%>";
	   document.b2bForm.submit();
	<%}%>
}

function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.b2bForm;
}

function clearForm(path){
	var form = document.b2bForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/b2bAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function importExcel(path){
	var form = document.b2bForm;
	if(validate()){
		if(confirm("ยืนยันทำรายการ")){
		  form.action = path + "/jsp/b2bAction.do?do=importExcel";
		  form.submit();
		}
	}
	return false;
}

function exportExcel(path){
	var form = document.b2bForm;
	if(confirm("ยืนยันทำรายการ")){
		form.action = path + "/jsp/b2bAction.do?do=exportExcel";
		form.submit();
		return true;
	}
}
function searchBatch(path){
	//unlockScreen
	 setTimeout($.unblockUI, 100); 
	 
	var form = document.b2bForm;
	form.action = path + "/jsp/b2bAction.do?do=searchBatch";
	form.submit();
	return true;
}

function validate(){
   var form = document.b2bForm;
   if(form.dataType.value ==''){
     alert('กรุณา ระบุประเภทไฟล์');
     form.dataType.focus();
     return false;
   }
   var extension = '';
   var startFileName = '';
   if(form.dataFormFile.value.indexOf('.') > 0){
       extension = form.dataFormFile.value.substring(form.dataFormFile.value.lastIndexOf('.') + 1).toLowerCase();
   }
   if(form.dataFormFile.value.indexOf('_') > 0){
       var pathFileName = form.dataFormFile.value;
       startFileName = pathFileName.substring(pathFileName.lastIndexOf('\\\\')+1,pathFileName.indexOf('_')).toLowerCase();
   }
   if(form.dataFormFile.value != '' && (extension == 'xls' || extension == 'xlsx') ){
   }else{
       alert('กรุณาเลือกไฟล์นามสกุล  xls หรือ  xlsx ');
       return false;
   }
   return true
 }
</script>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
           
       <tr>
               <td> ระบุประเภทไฟล์ <font color="red">*</font></td>
			<td>					
				 <html:select property="bean.dataType" styleId="dataType" >
					<html:options collection="TYPE_FILE_LIST" property="value" labelProperty="keyName"/>
			    </html:select>
			</td>
			
		</tr>
		<tr>
               <td> เลือกไฟล์<font color="red">*</font></td>
			<td >
			    <html:file property="dataFormFile" styleId="dataFormFile" size="30"/> 
			</td>
		</tr>	
   </table>
   <table  border="0" cellpadding="3" cellspacing="0" >
		<tr>
			<td align="left">
				<a href="javascript:importExcel('${pageContext.request.contextPath}')">
				  <input type="button" value=" Import " class="newPosBtnLong"> 
				</a>&nbsp;
				 <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
				  <input type="button" value="  Export  " class="newPosBtnLong"> 
				</a>
				&nbsp;
				<a href="javascript:clearForm('${pageContext.request.contextPath}')">
				  <input type="button" value="   Clear   " class="newPosBtnLong">
				</a>			
			</td>
		</tr>
	</table>
	
  <!-- Batch Task Result--> 
<jsp:include page="/jsp/batchtask/batchTaskPopupResult.jsp"></jsp:include>

 <%if(user.getUserName().equalsIgnoreCase("admin")){ %>
      <br/>
	  <table border="0"  width ="100%" cellpadding="3" cellspacing="1" bgcolor="#000000">
	    <tr bgcolor="#FFFFFF">
	      <td width="10%" align="center"><b>Process Name</b></td>
	      <td width="40%" align="center"><b>Process Description</b></td>
	    </tr>
	    <tr bgcolor="#FFFFFF">
	       <td>  B2B Makro</td>
	       <td> Import Excel to Oracle Temp and Call Procedure and Export </td>
	    </tr>
		 <tr bgcolor="#FFFFFF">
		     <td> Action</td>
		     <td> <b>Import EXCEL</b>
		     <br/>  1)B2B_ITEM :APPS.XXPENS_OM_PUSH_ORDER_ITEM
		     <br/>  2)SALES_BY_ITEM: APPS.XXPENS_OM_PUSH_ORDER_TEMP
		     <br/> <b>Export EXCEL</b>
		     <br/>  1) call apps.xxpens_om_push_order_pkg.b2b();
		     <br/>  2) Export sheet_data 
		     <br/>    2.1)sheet DATA : apps.xxpens_om_push_order_vl 
		     <br/>    2.2)sheet_PO_UPLOAD2: apps.xxpens_om_push_order_v1
		     </td>
		  </tr>
	  </table>
	<%}else{ %>
	   <table border="0" width ="50%" cellpadding="3" cellspacing="1" bgcolor="#000000">
	    <tr  bgcolor="#FFFFFF">
	      <td width="10%" align="center"><b>Process Name</b></td>
	      <td width="40%" align="center"><b>Process Description</b></td>
	    </tr>
	    <tr  bgcolor="#FFFFFF">
	        <td> B2B Makro</td>
	       <td> Import Excel to Oracle Temp ,Calculate and Export to Excel </td>
	    </tr>
	  </table>
	<%} %>
