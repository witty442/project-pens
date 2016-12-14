<%@page import="java.util.Date"%>
<%@page import="com.isecinc.pens.inf.helper.InterfaceUtils"%>
<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@page import="com.isecinc.pens.inf.manager.external.process.ControlInterfaceICC"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.inf.bean.MonitorBean"%>
<%@page import="com.isecinc.pens.inf.helper.Constants"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.web.interfaces.InterfacesForm"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.InterfaceHelper"%>

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />
<%
User user = (User) session.getAttribute("user");
String role = ((User)session.getAttribute("user")).getType();
String pageName = Utils.isNull(request.getParameter("pageName"));
String productType = "";

if(session.getAttribute("productTypeList") == null){
	List<References> productTypeList = new ArrayList<References>();
	productTypeList.add(new References("",""));
	productTypeList.addAll(GeneralDAO.getProductTypeListInterfaceICC());
	session.setAttribute("productTypeList",productTypeList); 
}

//Update Transtion Type by product;
if(  Constants.TYPE_GEN_ORDER_EXCEL.equalsIgnoreCase(pageName)
	|| Constants.TYPE_IMPORT_BILL_ICC.equalsIgnoreCase(pageName)
 	|| Constants.TYPE_EXPORT_BILL_ICC.equalsIgnoreCase(pageName)
 	|| Constants.TYPE_GEN_ITEM_MASTER_HISHER.equalsIgnoreCase(pageName)
 		|| Constants.TYPE_GEN_HISHER.equalsIgnoreCase(pageName)
 	) {
	productType= ControlInterfaceICC.getCurrentTransInterfaceICC();
}

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/interfaces.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>

<!-- Calendar -->
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
#progress {
 width: 500px;   
 border: 1px solid black;
 position: relative;
 padding: 3px;
}
#percent {
 position: absolute;   
 left: 50%;
}
#bar {
 height: 40px;
 background-color: green;
 width: 10%;
}
.errorLine {
   color: red;
}
</style>

<Script>
    function loadme(){
    	<%if( Constants.TYPE_IMPORT_BILL_ICC.equalsIgnoreCase(pageName)
    		|| Constants.TYPE_EXPORT_BILL_ICC.equalsIgnoreCase(pageName)
    		|| Constants.TYPE_GEN_ORDER_EXCEL.equalsIgnoreCase(pageName)
    		) {%>
    	   new Epoch('epoch_popup','th',document.getElementById('transactionDate'));
    	   /** Set For product type **/
    	   var productType = document.getElementsByName("bean.productType")[0];
    	   productType.value ='<%=productType%>';
    	<%}%>
    	
    	<%if( Constants.TYPE_IMPORT_BILL_ICC.equalsIgnoreCase(pageName)
        	|| Constants.TYPE_EXPORT_BILL_ICC.equalsIgnoreCase(pageName)
        	|| Constants.TYPE_GEN_ORDER_EXCEL.equalsIgnoreCase(pageName)
        	) {%>
        	
        	 // var productType = document.getElementsByName("bean.productType")[0];
       	     // productType.disabled =true;
        	<%}%>
    }
    
    
	function disableF5(e) {
		if (e.which == 116) e.preventDefault(); 
	}
	//To re-enable f5
	$(document).unbind("keydown", disableF5);
	
	//clear cach
	$.ajaxSetup({cache: false});
	
	function runBatch(path) {
			var confirmText = "ยืนยัน Process ";
			var pageName = document.getElementsByName("pageName")[0];
			if("<%=Constants.TYPE_GEN_ITEM_MASTER_HISHER%>" == pageName.value){
				var transDate = document.getElementsByName("bean.transactionDate")[0];
				var productType = document.getElementsByName("bean.productType")[0];
				if(productType.value ==""){
					alert("กรุณาระบุ กลุ่มสินค้า");
					productType.focus();
					return false;
				}
				if(transDate.value ==""){
					alert("กรุณาระบุ Transaction Date");
					transDate.focus();
					return false;
				}
			}
			if("<%=Constants.TYPE_GEN_HISHER%>" == pageName.value){
				var transDate = document.getElementsByName("bean.transactionDate")[0];
				var productType = document.getElementsByName("bean.productType")[0];
				if(productType.value ==""){
					alert("กรุณาระบุ กลุ่มสินค้า");
					productType.focus();
					return false;
				}
				if(transDate.value ==""){
					alert("กรุณาระบุ Transaction Date");
					transDate.focus();
					return false;
				}
			}
			if("<%=Constants.TYPE_GEN_ORDER_EXCEL%>" == pageName.value){
				//var transDate = document.getElementsByName("bean.transactionDate")[0];
				var productType = document.getElementsByName("bean.productType")[0];
				if(productType.value ==""){
					alert("กรุณาระบุ กลุ่มสินค้า");
					productType.focus();
					return false;
				}
				/* if(transDate.value ==""){
					alert("กรุณาระบุ Transaction Date");
					transDate.focus();
					return false;
				} */
			}
			
			if("<%=Constants.TYPE_IMPORT_BILL_ICC%>" == pageName.value){
				var transDate = document.getElementsByName("bean.transactionDate")[0];
				if(transDate.value ==""){
					alert("กรุณาระบุ Transaction Date");
					transDate.focus();
					return false;
				}
			}
			
			
			if("<%=Constants.TYPE_EXPORT_BILL_ICC%>" == pageName.value){
				var transDate = document.getElementsByName("bean.transactionDate")[0];
				if(transDate.value ==""){
					alert("กรุณาระบุ Transaction Date");
					transDate.focus();
					return false;
				}
			}
			
			if("<%=Constants.TYPE_IMPORT_TRANSACTION_LOTUS%>" == pageName.value){
				var form = document.interfacesForm;
				var fileNameObj = document.getElementsByName("bean.formDataFile")[0];
				var extension = '';
				var startFileName = '';
				//alert(form.formDataFile.value);
				if(fileNameObj.value.indexOf(".") > 0){
					extension = fileNameObj.value.substring(fileNameObj.value.lastIndexOf(".") + 1).toLowerCase();
					//alert(extension);
				}
				if(fileNameObj.value.indexOf("_") > 0){
					var pathFileName = fileNameObj.value;
					//alert(pathFileName +","+pathFileName.lastIndexOf("\\"));
					startFileName = pathFileName.substring(pathFileName.lastIndexOf("\\")+1,pathFileName.indexOf("_")).toLowerCase();
					//alert(startFileName);
				}
				 if(fileNameObj.value != '' && (extension == "xls" || extension == "xlsx") ){
				 }else{
					alert("กรุณาเลือกไฟล์นามสกุล  xls หรือ  xlsx ");
					return;
				}
			}
			
			if(confirm(confirmText)){
				document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=runBatch&action=submited";
				document.interfacesForm.submit();
				return true;
			}
			return false;
	}
</Script>

	<!-- ProgressBar -->
	<% if( "submited".equals(request.getAttribute("action"))){ %>
	   <script type="text/javascript" language="javascript">
	   
	   //To disable f5
	   $(document).bind("keydown", disableF5);
	 
	   $(function() {
			///$("#dialog").dialog({ height: 200,width:650,modal:true });
		   $.blockUI({ message: $('#dialog'), css: {left:'20%', right:'20%' ,top: '40%',height: '20%', width: '60%' } }); 
		});
	   
	   var stepMaxUp = 3;
	   var stepMinUp = 1;
	   var stepHaftMinUp = 0.5;
	   var stepDotOne = 0.1;
	   var progressCount = 0;
	   var useTimeMillisecs = 0;
	   var startTime = new Date();
	   
	   function update(status){
	    	 if(status != '1' && status != "-1"){ //Running
	    		 if(progressCount > 95){
		    	   progressCount += stepDotOne; 
	    		 }else if(progressCount > 90){
	    		   progressCount += stepHaftMinUp; 
	    		 }else if(progressCount > 45){
	    		   progressCount += stepMinUp; 
	    		 }else{
	    		   progressCount += stepMaxUp;
	    		 }
	    		 useTimeMillisecs = (new Date()).getTime()-startTime.getTime();
	    	 }else{ //Success
	    		 
	    		 progressCount = 100;
	    		 useTimeMillisecs = (new Date()).getTime()-startTime.getTime();
	    		 
	    		 $("#percent").html("<b>"+progressCount+" %</b>");
	    		 document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");
	    		 
	    		// $("#progress").hide();
	    		 
	    		 setTimeout(function(){ $("#progress").hide();}, 3000);
	    	 }  
	    	  
	    	 //var progress = $("#progressbar") .progressbar("option","value");
	    	 if (progressCount < 100) {  
		   	      $("#percent").html("<b>"+progressCount+" %</b>");
	    		  $("#progress").show();
	    		  //set progress count
	    		  document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");
		   	 }
	    }
	
	   /** Onload Window    */
	   var startDate = new Date();
	   
	   window.onload=function(){
    	   var status = document.getElementsByName("monitorBean.status")[0]; 
    
    	   if (status.value != "1" && status.value != "-1"){
    		   window.setTimeout("checkStatusProcess();", 800);
    	   }
    	   update(status.value);
	    } 

	    /** Check Status From monitor_id BY AJax */
	    function checkStatusProcess(){
	    	$(function(){
	    		var getData = $.ajax({
	    			url: "${pageContext.request.contextPath}/jsp/ajax/interfacesAjax.jsp",
	    			data : "id=<%=request.getAttribute("id")%>&transaction_count=<%=request.getAttribute("transaction_count")%>&imp=<%=request.getAttribute("import")%>",
	    			success: function(getData){
	    				var returnString = jQuery.trim(getData);
	    				document.getElementsByName("monitorBean.status")[0].value = returnString;
	    			}
	    		}).responseText;   
	    	});
	    	setTimeout("checkStatus()",3000);
	    }
        /** Check Status Recursive **/
	    function checkStatus(){
	    	  var status =  document.getElementsByName("monitorBean.status")[0].value;
	    	 // alert(status);
	    	   if(status == '1'){ //Finish Task
	    		   //Calc Time thred use
	    		   try{
		    		   var endDate = new Date();
		    		   var dif = endDate.getTime() - startDate.getTime();
	
		    		   var Seconds_from_T1_to_T2 = dif / 1000;
		    		   //var Seconds_Between_Dates = Math.abs(Seconds_from_T1_to_T2);
		    		   
		    		   document.getElementsByName("monitorBean.timeInUse")[0].value = Seconds_from_T1_to_T2;//Seconds_Between_Dates; 
		    		   
		    		  // alert( document.getElementsByName("monitorBean.timeInUse")[0].value);
	    		   }catch(e){}
	    		   /** Task Success ***/
	    		   update(status);
	    		   
	    		   <%if( Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS.equals(pageName) ){  %>
	    		     //  if(status != '-1')
	    		        //window.close();
	    		      //search display
	    		       search('<%=request.getContextPath()%>', 'admin');
	    		   <%}else{ %>
	    		       //search display
	    		       search('<%=request.getContextPath()%>', 'admin');
                   <%} %>
	    	   }else { //Task Running
	    		   /** Task Not Success  and Re Check Status**/
		    	   update(status);
	               window.setTimeout("checkStatusProcess();", 1200);
	           }
	    }
	    </script>
	    <!-- PROGRESS BAR -->
	<% }else{ 
		//Show Message In Parent window
	    if( Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS.equals(pageName) 
	    	|| Constants.TYPE_GEN_STOCK_REPORT_ENDDATE_LOTUS.equals(pageName) ){  
	    	
	    	MonitorBean resultsBean = interfacesForm.getResults()[0];
	    	String message = "Gen ข้อมูลเรียบร้อย";
	    	
	    	if(Utils.isNull(resultsBean.getErrorMsg()).equals("")){
	  %>
		    <script>
		       window.opener.div_message.innerHTML = "<%=message %>";
		       window.opener.div_error_message.innerHTML ="";
		       window.close();
		     </script>
	    <% }else{ 
	         message = Utils.isNull(resultsBean.getErrorMsg());
	    %>
	         <script>
		       window.opener.div_error_message.innerHTML = "<%=message %>";
		       window.opener.div_message.innerHTML  ="";
		       window.close();
		     </script>
	 
	 <%   }
	    }
	} %>

</head>

<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;"  style="height: 100%;" onload="loadme()">

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
	    	<%if(Constants.TYPE_GEN_HISHER.equalsIgnoreCase(pageName)) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="InterfaceHisHer"/>
				</jsp:include>
			<%}else if(Constants.TYPE_IMPORT_BMESCAN.equalsIgnoreCase(pageName)) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportBarcodeScan"/>
					<jsp:param name="code" value=""/>
				</jsp:include>
		    <%}else if(Constants.TYPE_IMPORT_BILL_ICC.equalsIgnoreCase(pageName)) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportBillICC"/>
				</jsp:include>
			 <%}else if(Constants.TYPE_EXPORT_BILL_ICC.equalsIgnoreCase(pageName)) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ExportBillICC"/>
				</jsp:include>
			<%}else if(Constants.TYPE_GEN_ORDER_EXCEL.equalsIgnoreCase(pageName)) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="GenOrderExcel"/>
		   		</jsp:include>
		     <%}else if(Constants.TYPE_GEN_ITEM_MASTER_HISHER.equalsIgnoreCase(pageName)) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="GenerateItemMasterHisHer"/>
		 		</jsp:include>
		    <%}else if(Constants.TYPE_IMPORT_TRANSACTION_LOTUS.equalsIgnoreCase(pageName)) {%>
		     	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportBMEFromLotus"/>
				</jsp:include>
			 <%}else if(Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS.equalsIgnoreCase(pageName)) {%>
		     	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="GenStockEndDateLotus"/>
				</jsp:include>
			<%}else if(Constants.TYPE_GEN_STOCK_REPORT_ENDDATE_LOTUS.equalsIgnoreCase(pageName)) {%>
		     	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="GenStockReportEndDateLotus"/>
				</jsp:include>
			<%} %>
			
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
						<html:form action="/jsp/interfacesAction"  enctype="multipart/form-data">
			
						<jsp:include page="../error.jsp"/>
						
						<!-- CRITERIA  Submit-->
						<% if( Constants.TYPE_IMPORT_BMESCAN.equals(pageName)){ %>  
						     <table align="center" border="0" cellpadding="3" cellspacing="10" width="100%">
								<tr>
									<td align="center" width ="100%">
									   <input type="button" name ="import" value="ดึงข้อมูล File Scan Barcode" class="newPosBtnLong"  
									   onClick="javascript:importData('${pageContext.request.contextPath}','sales')">
									</td>
								</tr>
							</table>

					    <%}else if( Constants.TYPE_GEN_HISHER.equals(pageName) || Constants.TYPE_GEN_ITEM_MASTER_HISHER.equals(pageName)){  %>
					       <table align="center" border="0" cellpadding="2" cellspacing="3" width="100%">
					          <tr>
								    <td align="right" width="40%">กลุ่มสินค้า<font color="red">*</font></td>
								    <td valign="top" align="left">
								       <html:select property="bean.productType" styleId="productType">
											<html:options collection="productTypeList" property="key" labelProperty="name"/>
									    </html:select>
								    </td>
						        </tr>
					           <tr>
							    <td align="right" width="40%">กลุ่มร้านค้า<font color="red"></font></td>
							    <td valign="top" align="left">
							      <html:text property="bean.custGroupDesc" styleId="custGroupDesc" styleClass="disableText" readonly="true" size="30" />
							      <html:hidden property="bean.custGroup" styleId="custGroup"/>
							    </td>
						      </tr>
						       <tr>
							    <td align="right" width="40%">Text File Name<font color="red"></font></td>
							    <td valign="top" align="left"><html:text property="bean.textFileName" styleId="textFileName" size="40" readonly="true" styleClass="disableText"/></td>
						      </tr>
							  <tr>
							    <td align="right" width="40%">Output path of text file<font color="red"></font></td>
							    <td valign="top" align="left"><html:text property="bean.outputPath" styleId="outputPath"  size="40"  readonly="true" styleClass="disableText" /></td>
						      </tr>
						       <tr>
							    <td align="right" width="40%">Transaction Date<font color="red">*</font></td>
							    <td valign="top" align="left"><html:text property="bean.transactionDate" styleId="transactionDate" size="30"/></td>
						      </tr>
							</table>
							
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						   <tr>
								<td align="right" width ="100%"> &nbsp;</td>
							</tr>
							<tr>
								<td align="center" width ="100%">
								   <input type="button" value="Generate File" class="newPosBtnLong" 
								    style="width: 200px;" onClick="javascript:runBatch('${pageContext.request.contextPath}')">
								    <input type="button" value="ปิดหน้าจอ" class="newPosBtnLong" style="width: 100px;" onClick="javascript:backToMainpage('${pageContext.request.contextPath}','admin')">  
								</td>
							</tr>
						</table>
						
					    <%}else if( Constants.TYPE_GEN_ORDER_EXCEL.equals(pageName)){  %>
					       <table align="center" border="0" cellpadding="3" cellspacing="10" width="100%">
					            <tr>
								    <td align="right" width="40%">กลุ่มสินค้า<font color="red"></font></td>
								    <td valign="top" align="left">
								       <html:text property="bean.productType" styleId="productType" styleClass="disableText" readonly="true"/>
								    </td>
						        </tr>
	
					           <tr>
							    <td align="right" width="40%">กลุ่มร้านค้า<font color="red"></font></td>
							    <td valign="top" align="left">
							      <html:text property="bean.custGroupDesc" styleId="custGroupDesc" styleClass="disableText" readonly="true" size="30" />
							      <html:hidden property="bean.custGroup" styleId="custGroup"/>
							    </td>
						      </tr>
						       <tr>
							    <td align="right" width="40%">Text File Name<font color="red"></font></td>
							    <td valign="top" align="left"><html:text property="bean.textFileName" styleId="textFileName" size="40" readonly="true" styleClass="disableText"/></td>
						      </tr>
							  <tr>
							    <td align="right" width="40%">Output path of text file<font color="red"></font></td>
							    <td valign="top" align="left"><html:text property="bean.outputPath" styleId="outputPath"  size="40"  readonly="true" styleClass="disableText" /></td>
						      </tr>
						       <tr>
							    <td align="right" width="40%">Transaction Date<font color="red">*</font></td>
							    <td valign="top" align="left"><html:text property="bean.transactionDate" styleId="transactionDate" size="30"/></td>
						      </tr>
							</table>
							
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						   <tr>
								<td align="right" width ="100%"> &nbsp;</td>
							</tr>
							<tr>
								<td align="center" width ="100%">
								   <input type="button" value="Generate File" class="newPosBtnLong" 
								    style="width: 200px;" onClick="javascript:runBatch('${pageContext.request.contextPath}')">
								    <input type="button" value="ปิดหน้าจอ" class="newPosBtnLong" style="width: 100px;" onClick="javascript:backToMainpage('${pageContext.request.contextPath}','admin')">
								 
								</td>
							</tr>
						</table>
						
					  <%}else if( Constants.TYPE_IMPORT_BILL_ICC.equals(pageName)){  %>
					       <table align="center" border="0" cellpadding="3" cellspacing="10" width="100%">
					           <tr>
								    <td align="right" width="40%">กลุ่มสินค้า<font color="red"></font></td>
								    <td valign="top" align="left">
								      <html:text property="bean.productType" styleId="productType" styleClass="disableText" readonly="true"/>
								    </td>
						        </tr>
						       <tr>
							    <td align="right" width="40%">Transaction Date<font color="red">*</font></td>
							    <td valign="top" align="left"><html:text property="bean.transactionDate" styleId="transactionDate" size="20"/></td>
						      </tr>
							</table>
							
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						   <tr>
								<td align="right" width ="100%"> &nbsp;</td>
							</tr>
							<tr>
								<td align="center" width ="100%">
								   <input type="button" value="Import File" class="newPosBtnLong" 
								    style="width: 200px;" onClick="javascript:runBatch('${pageContext.request.contextPath}')">
								     <input type="button" value="ปิดหน้าจอ" class="newPosBtnLong" style="width: 100px;" onClick="javascript:backToMainpage('${pageContext.request.contextPath}','admin')">
								   
								</td>
							</tr>
						</table>
					   
					   	
					  <%}else if( Constants.TYPE_EXPORT_BILL_ICC.equals(pageName)){  %>
					       <table align="center" border="0" cellpadding="3" cellspacing="10" width="100%">
					           <tr>
								    <td align="right" width="40%">กลุ่มสินค้า<font color="red"></font></td>
								    <td valign="top" align="left">
								      <html:text property="bean.productType" styleId="productType" styleClass="disableText" readonly="true"/>
								    </td>
						        </tr>
						       <tr>
							    <td align="right" width="40%">ระบุวันที่ icc สรุปบิล (Bill Date)<font color="red">*</font></td>
							    <td valign="top" align="left"><html:text property="bean.transactionDate" styleId="transactionDate" size="20"/></td>
						      </tr>
							</table>
							
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						   <tr>
								<td align="right" width ="100%"> &nbsp;</td>
							</tr>
							<tr>
								<td align="center" width ="100%">
								   <input type="button" value="Export ข้อมูล" class="newPosBtnLong" 
								    style="width: 200px;" onClick="javascript:runBatch('${pageContext.request.contextPath}')">
								    <input type="button" value="ปิดหน้าจอ" class="newPosBtnLong" style="width: 100px;" onClick="javascript:backToMainpage('${pageContext.request.contextPath}','admin')">
								
								</td>
							</tr>
						</table>
					       	
					  <%}else if( Constants.TYPE_IMPORT_TRANSACTION_LOTUS.equals(pageName)){  %>
					       <table align="center" border="0" cellpadding="3" cellspacing="10" width="100%">
						      <tr>
								<td align="right" width="40%">เลือกไฟล์&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:file property="bean.formDataFile" styleClass="" style="width:300px;height:21px"/>
								</td>
							</tr>
							</table>
							
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						   <tr>
								<td align="right" width ="100%"> &nbsp;</td>
							</tr>
							<tr>
								<td align="center" width ="100%">
								   <input type="button" value="Import ข้อมูล" class="newPosBtnLong" 
								    style="width: 200px;" onClick="javascript:runBatch('${pageContext.request.contextPath}')">
								    <input type="button" value="ปิดหน้าจอ" class="newPosBtnLong" style="width: 100px;" onClick="javascript:backToMainpage('${pageContext.request.contextPath}','admin')">
								
								</td>
							</tr>
						</table>
					   
					    <%} %>

                          <!-- BUTTON -->
							 <table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							   <tr>
									<td align="center" width ="100%"> &nbsp;</td>
								</tr>
								<tr>
									<td align="center" width ="100%">
									    <input type="button" value="ตรวจสอบสถานะ ล่าสุด" class="newPosBtnLong" style="width: 200px;" onClick="javascript:search('${pageContext.request.contextPath}','admin')"> 
									    <%if( Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS.equals(pageName)){  %>
									      <input type="button" value="ปิดหน้าต่างนี้" class="newPosBtnLong" style="width: 100px;" onClick="javascript:window.close()">
									    <%}else{ %>
									       <input type="button" value="Clear" class="newPosBtnLong" style="width: 100px;" onClick="javascript:clearForm('${pageContext.request.contextPath}','admin')">
									    <%} %>
									</td>
								</tr>
							</table>   
							
                        <!-- Monitor Batch Task -->
                        <% if( Constants.TYPE_IMPORT_BMESCAN.equals(pageName)){ %> 
                            
							 <jsp:include page="monitor.jsp"></jsp:include>
							<p></p>
						 <% }else if( Constants.TYPE_GEN_HISHER.equals(pageName) || Constants.TYPE_GEN_ITEM_MASTER_HISHER.equals(pageName)){ %> 
						    <p></p>
						    <jsp:include page="monitor_short.jsp"></jsp:include>
							<p></p>
							 <!-- BME Scan Result -->
							<jsp:include page="interfacesResult.jsp"></jsp:include>
						<% }else if( Constants.TYPE_GEN_ORDER_EXCEL.equals(pageName)){ %> 
						 <!-- BUTTON -->
						    <p></p>
						    <jsp:include page="monitor_order_excel.jsp"></jsp:include>
							<p></p>
							 <!-- BME Scan Result -->
							<jsp:include page="interfacesResult.jsp"></jsp:include>
						  <%}else if(Constants.TYPE_IMPORT_BILL_ICC.equals(pageName)){  %>
						     <p></p>
						    <jsp:include page="monitor_short.jsp"></jsp:include>
							<p></p>
							 <!-- BME Scan Result -->
							 <jsp:include page="interfacesResultImportBillICC.jsp"></jsp:include>
							 
					       <%}else if(Constants.TYPE_EXPORT_BILL_ICC.equals(pageName)){  %>
						      <!-- BUTTON -->
						     <p></p>
						     <jsp:include page="monitor_short.jsp"></jsp:include>
							 <p></p>
							 <!-- BME Scan Result -->
							 <jsp:include page="interfacesResultExportBillICC.jsp"></jsp:include>
					      <% }else if( Constants.TYPE_IMPORT_TRANSACTION_LOTUS.equals(pageName)){ %> 
						    <p></p>
						    <jsp:include page="monitor_short.jsp"></jsp:include>
							<p></p>
							 <!-- BME Scan Result -->
							<jsp:include page="interfacesImportTransResult.jsp"></jsp:include>
						   <% }else if( Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS.equals(pageName)){ %> 
							    <p></p>
							    <jsp:include page="monitor_short.jsp"></jsp:include>
								<p></p>
							<%} %>
					     
						<div id="dialog" title=" กรุณารอสักครู่......">
							<!-- PROGRESS BAR-->
							  <% if( "submited".equals(request.getAttribute("action"))){ %>  
							 <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							    <tr>
									<td align="left" width ="100%">
									   <div style="height:50px;align:center">
									     กรุณารอสักครู่......
									   </div>
									 <div id="progress" style="height:40px;width:100%;">
						                    <div id="percent"></div>     
											<div id="bar"></div>  
						              </div>   
									 </td>
								</tr>
							   </table>   
						    <%} %>			      
						</div>
						<br><br>
						
						<div align="left">
							<!-- Status:  --><html:hidden property="monitorBean.status"  styleClass="disableText"/>
							<table class="test">
							<tr>
							    <td align="left" width="50%">
								  <b> Time Process Use: <html:text property="monitorBean.timeInUse" readonly="true"  styleClass="disableText"/> Seconds</b>
								</td>
								<td align="right"  width="50%">
								   <b>
									   <%if(Constants.TYPE_IMPORT_BILL_ICC.equals(pageName)){ 
										   String path = EnvProperties.getInstance().getProperty("path.icc.hisher.import.dlyr")+"/";
										   out.print("    Example file :");
										   out.print(path+InterfaceUtils.getImportNameICC("PENSBME_ICC_HEAD",Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)));
										   out.print("      &nbsp;&nbsp;,"+path+InterfaceUtils.getImportNameICC("PENSBME_ICC_DLYR",Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)));
									   } %>
								   </b>
								 </td>
							 </tr>
							</table>
						</div>
						<input type="hidden" name="pageName" value="<%=pageName%>"/>
						
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