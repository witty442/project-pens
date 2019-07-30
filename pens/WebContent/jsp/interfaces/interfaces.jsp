<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="util.SessionGen"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.ImportHelper"%>
<%@page import="com.isecinc.pens.inf.helper.ExportHelper"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />
<%-- <jsp:useBean id="userForm" class="com.isecinc.pens.web.user.UserForm" scope="request" /> --%>
<%
User user = (User) session.getAttribute("user");
String role = ((User)session.getAttribute("user")).getType();

List<References> importList = ImportHelper.readConfigTableImport();
pageContext.setAttribute("importList",importList,PageContext.PAGE_SCOPE);

List<References> importSalesUpdateList = ImportHelper.readConfigTableUpdateSalesImport();
pageContext.setAttribute("importUpdateSalesList",importSalesUpdateList,PageContext.PAGE_SCOPE);

List<References> exportList = ExportHelper.readConfigTableExport();
pageContext.setAttribute("exportList",exportList,PageContext.PAGE_SCOPE);

String screenWidth= Utils.isNull(session.getAttribute("screenWidth"));
String screenHeight= Utils.isNull(session.getAttribute("screenHeight"));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/interfaces.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>

<!-- Calendar -->
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
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
.msgError {
	font-size: 30px;
	font-weight: bold;
	color: #EDBB99;
}
.msgSuccess {
	font-size: 30px;
	font-weight: bold;
	color: #27AE60;
}
</style>

<Script>
	function disableF5(e) {
		if (e.which == 116) e.preventDefault(); 
	}
	//To re-enable f5
	$(document).unbind("keydown", disableF5);
	
	//clear cach
	$.ajaxSetup({cache: false});
	
	// Show Option Run Import Trans (Case Error)
	function setReimportUpdateTrans(){
		document.getElementById("reimportUpdateTransDiv").style.display = "block";
	}
	function openPopupAdminConsole(path){
		
		var h= <%=screenHeight%>-50;
		var w = <%=screenWidth%>-50;
		PopupCenter(path+"/jsp/adminConsole.do?do=process&currentTab=tab_config_info", "Configuration",w,h); 
		//window.open(path+'/jsp/adminConsole.do?do=process&currentTab=tab_config_info','Configuration','width='+screen_width+',height='+screen_height+"'");
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
	   
	   var stepMaxUp = 4;
	   var stepMinUp = 2;
	   var stepHaftMinUp = 0.5;
	   var progressCount = 0;
	   var useTimeMillisecs = 0;
	   var startTime = new Date();
	   
	   function update(status){
		  // alert(status);
	    	 if(status != '1' && status != "-1"){ //Running
	    		 if(progressCount > 98){
		    	   progressCount += 0; 
	    		 }else if(progressCount > 95){
	    		   progressCount += stepHaftMinUp; 
	    		 }else if(progressCount > 80){
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
    	   //alert(status.value);
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
	    	   if(status == '1'){ //Finish Task
	    		   
	    		   //Calc Time thred use
	    		   try{
		    		   var endDate = new Date();
		    		   var dif = endDate.getTime() - startDate.getTime();
	
		    		   var Seconds_from_T1_to_T2 = dif / 1000;
		    		   var Seconds_Between_Dates = Math.abs(Seconds_from_T1_to_T2);
		    		   
		    		   document.getElementsByName("monitorBean.timeInUse")[0].value = Seconds_from_T1_to_T2;//Seconds_Between_Dates; 
		    		   
		    		  // alert( document.getElementsByName("monitorBean.timeInUse")[0].value);
		    		   
	    		   }catch(e){
	    			 
	    		   }
	    		   
	    		   /** Task Success ***/
	    		   update(status);
	    		   //search display
	    		   search('<%=request.getContextPath()%>', 'admin');

	    	   }else { //Task Running
	    		   /** Task Not Success  and Re Check Status**/
		    	   update(status);
	               window.setTimeout("checkStatusProcess();", 1200);
	           }
	    }
	    </script>
	<% } %>
<!-- PROGRESS BAR -->
</head>

<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;"  style="height: 100%;">

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
				<jsp:param name="function" value="Interfaces"/>
				<jsp:param name="code" value=""/>
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
						<html:form action="/jsp/interfacesAction">
			
						<jsp:include page="../error.jsp"/>
						
						<html:hidden property="monitorBean.status"/>
						
						<!-- CRITERIA  Submit-->
						<% if( request.getAttribute("action") == null){ %>  

						 <%if( !role.equalsIgnoreCase(User.ADMIN)){ %>
						     <table align="center" border="0" cellpadding="3" cellspacing="10" width="100%">
								<tr>
									<td align="right" width ="50%"><b>Import ข้อมูลจากส่วนกลาง<font color="red">*</font> </b></td>
									<td align="left" width ="50%" nowrap>
									   <input type="button" name ="import"  id="import" value="ดึงข้อมูลจากส่วนกลาง" class="newPosBtnLong"  onClick="javascript:synchronizeFromOracle('${pageContext.request.contextPath}','sales')">
									   &nbsp;&nbsp;<a href="javascript: void(0)" onclick="setReimportUpdateTrans()"><b>[...]</b></a>
									</td>
								</tr>
								<tr>
									<td align="right" width ="50%"></td>
									<td align="left" width ="50%" nowrap>
									   <div id="reimportUpdateTransDiv" style="display: none">
									     <b> <input type="checkbox" name="reimportUpdateTransChk" />Run Re Import Transaction(Case Error)</b>
									   </div>
									</td>
								</tr>
								<tr>
								  <td align="right" width ="50%"><b>Export ข้อมูลไปยังส่วนกลาง<font color="red">*</font> </b></td>
								  <td align="left">
								       <input type="button" name ="export"  id="export" value="ส่งข้อมูลไปส่วนกลาง   " class="newPosBtnLong"  onClick="javascript:synchronizeToOracle('${pageContext.request.contextPath}','sales')">
				                  </td>
							  </tr>
							</table>
						   <%}else{ %>
						     <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						        <tr>
									<td align="right" width ="50%"> </td>
									<td align="left" width ="50%"><html:checkbox property="monitorBean.importAll">นำเข้าใหม่ทั้งหมด</html:checkbox></td>
								</tr>
							</table>
							 <fieldset>
                             <legend>Import</legend>
								 <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							        <tr>
										<td align="right" width ="50%">เลือกรายการนำเข้าข้อมูล (Master and  Transaction)<font color="red">*</font></td>
										<td align="left" width ="50%">
										    <html:select property="monitorBean.requestTable">
											   <html:options collection="importList" property="key" labelProperty="name"/>
										    </html:select>
										</td>
									</tr>
									<tr>
										<td align="right" width ="50%"> โปรดระบุ User Name ที่ต้องการ Import:<font color="red">*</font></td>
										<td align="left" width ="50%">
										   <html:text property="monitorBean.requestImportUserName"></html:text>(Ex.,SOMCHAI,LERTCHAI, SOMBOON)
										</td>
									</tr>
									<tr>
										<td align="right" width ="50%"></td>
										<td align="left" width ="50%">
										<input type="button" name ="import"  id="import" value="Import" class="newPosBtnLong" style="width: 100px;" onClick="javascript:synchronizeFromOracle('${pageContext.request.contextPath}','admin')">	
										</td>
									</tr>
								</table>
							</fieldset>
							
							 <fieldset>
                             <legend>Import Update Sales Transaction</legend>
								 <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
									<tr>
										<td align="right" width ="50%">เลือกรายการนำเข้าข้อมูล (Update Sales Transaction)<font color="red">*</font></td>
										<td align="left" width ="50%">
										    <html:select property="monitorBean.requestUpdateSalesTable">
											   <html:options collection="importUpdateSalesList" property="key" labelProperty="name"/>
										    </html:select>
										</td>
									</tr>
									<tr>
										<td align="right" width ="50%"> โปรดระบุ User Name ที่ต้องการ Import:<font color="red">*</font></td>
										<td align="left" width ="50%">
										   <html:text property="monitorBean.requestImportUpdateUserName"></html:text>(Ex.,SOMCHAI,LERTCHAI, SOMBOON)
										</td>
									</tr>
									<tr>
										<td align="right" width ="50%"></td>
										<td align="left" width ="50%">
										<input type="button" name ="importTrans" value="Import Update Sales Transaction" class="newPosBtnLong" style="width: 280px;" onClick="javascript:updateSalesTransaction('${pageContext.request.contextPath}','admin')">
										</td>
									</tr>
								</table>
							</fieldset>
							
							 <fieldset>
                             <legend>Export</legend>
								 <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
									 <tr>
										<td align="right" width ="50%">เลือกรายการนำออกข้อมูล <font color="red">*</font></td>
										<td align="left" width ="50%">
										    <html:select property="monitorBean.requestExportTable">
											   <html:options collection="exportList" property="key" labelProperty="name"/>
										    </html:select>   
									</tr>
									<tr>
										<td align="right" width ="50%"> โปรดระบุ User Name ที่ต้องการ Export:<font color="red">*</font></td>
										<td align="left" width ="50%">
										   <html:text property="monitorBean.requestExportUserName"></html:text>(Ex.,SOMCHAI,LERTCHAI, SOMBOON)
										</td>
									</tr>
									<tr>
										<td align="right" width ="50%"></td>
										<td align="left" width ="50%">
										    <input type="button" name ="export" id="export" value="Export" class="newPosBtnLong" style="width: 100px;" onClick="javascript:synchronizeToOracle('${pageContext.request.contextPath}','admin')">
										    
										</td>
									</tr>
							   </table>
							</fieldset>
						   <%} %>
					    <%} %>
				
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						   <tr>
								<td align="right" width ="100%"> &nbsp;</td>
								
							</tr>
							<tr>
								<td align="center" width ="100%">
								    <input type="button" value="ตรวจสอบสถานะล่าสุด" class="newPosBtnLong" style="width: 180px;" onClick="javascript:search('${pageContext.request.contextPath}','admin')" title="<%=com.isecinc.pens.inf.helper.ConvertUtils.genEnvStr() %>"> 
								    &nbsp;&nbsp;&nbsp;
								    <input type="button" value="Clear" class="newPosBtnLong" style="width: 160px;" onClick="javascript:clearForm('${pageContext.request.contextPath}','admin')">
								    &nbsp;&nbsp;&nbsp;
								   
								    <a href ="javascript:openPopup('${pageContext.request.contextPath}')" 
								    title="ตรวจสอบ FTP Connection"><b>?</b>
								    </a>
								</td>
							</tr>
						</table>
						 <div align="center">
						   <font color="blue" size="6"><b>!!! เชื่อมต่อ VPN ก่อน ส่งข้อมูล หรือดึงข้อมูล ทุกครั้ง  !!!</b></font>
						  </div>
                        <br/>
						<div id="dispMsg" style="" align="center">
						  ..
						</div>  
					   <!-- RESULT -->
					   <c:if test="${interfacesForm.results != null}">
							<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
							&nbsp;<span class="searchResult">${interfacesForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
							
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
								<tr>
				                <th> No.</th>
				                <th> เลขที่รายการ</th>
								<th> ประเภท</th>
								<th> ประเภทข้อมูล</th>
								<th> ผู้สร้าง</th>
								<th> สถานะ</th>
								<th> วันที่ทำรายการล่าสุด</th>
								<th> 
								   <c:choose>
									 <c:when test="${interfacesForm.criteria.monitorBean.type == 'EXPORT'}">
											   จำนวนไฟล์
									 </c:when>
									 <c:otherwise>
										จำนวนไฟล์สำเร็จ / จำนวนไฟล์ทั้งหมด
									  </c:otherwise>
								   </c:choose>
								</th>
								<th> Message</th>
								<th> รายละเอียด</th>
								</tr>
								<c:forEach var="results" items="${interfacesForm.results}" varStatus="rows">
								
							 	<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose> 
	
								<c:choose>
									<c:when test="${results.status == 1}"></c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineError2"/>
									</c:otherwise>
								</c:choose>
								
								<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}"> 
						                <td width="3%"> <c:out value='${rows.index+1}'/></td>
						                <td width="5%"> ${results.transactionId}</td>
										<td width="8%"> 
										 ${results.type}
										 <input type="hidden" name="transTypeText" value="${results.type}"/>
										</td>
										<td width="10%">
										  <input type="hidden" name="transactionTypeText" value="${results.transactionType}"/>
										   <c:choose>
												<c:when test="${results.transactionType == 'MASTER'}">
													ข้อมูลพื้นฐาน
												</c:when>
												<c:when test="${results.transactionType == 'TRANSACTION'}">
													ข้อมูล Transaction
												</c:when>
												<c:when test="${results.transactionType == 'WEB-MEMBER'}">
													ข้อมูล WEB MEMBER
												</c:when>
												<c:when test="${results.transactionType == 'UPDATE-RETRANS-SALES'}">
													ReImport Update Transaction Sale
												</c:when>
												<c:otherwise>
													Update Transaction Sales
												</c:otherwise>
										   </c:choose>
										</td>
										<td width="8%"> ${results.createUser}</td>
										<td width="8%"> 
										    <input type="hidden" name="statusText" value="${results.status}"/>
										    <c:choose>
												<c:when test="${results.status == 1}">
													<img border=0 src="${pageContext.request.contextPath}/icons/check.gif" width="30px" height="30px" >
												</c:when>
												<c:otherwise>
													<img border=0 src="${pageContext.request.contextPath}/icons/uncheck.gif" width="30px" height="30px" >
												</c:otherwise>
										    </c:choose>
										</td>
										<td width="10%"> ${results.submitDate}</td>
										<td width="16%">
										  <b>
											   <c:choose>
													<c:when test="${results.type == 'EXPORT'}">
														${results.fileCount}
													</c:when>
													<c:otherwise>
														${results.successCount}/ ${results.fileCount}
													</c:otherwise>
												</c:choose>
											</b>
										</td>
										<td align="left" width="17%"> ${results.errorMsg}</td>
										<td width="10%"> 
										     <a href="#" onclick="javascript:searchDetail('${pageContext.request.contextPath}','admin','${results.monitorId}');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
										</td>
								</tr>
								</c:forEach>
							</table>
						</c:if>

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
					
					 <c:if test="${interfacesForm.results != null}">
					<script>
					dispMsg();
					 function dispMsg(){
						 if(document.getElementsByName("transTypeText")[0].value == "EXPORT"){
							 if(document.getElementsByName("statusText")[0].value == "-1"
							   || document.getElementsByName("statusText")[1].value == "-1"){
								 
								 document.getElementById("dispMsg").innerHTML = "ไม่สามารถ Export ข้อมูลบางส่วน ได้";
								 document.getElementById("dispMsg").className = "msgError";
							 }else{
								 document.getElementById("dispMsg").innerHTML = "Export เรียบร้อยแล้ว"; 
								 document.getElementById("dispMsg").className = "msgSuccess";
							 }
						 }else{
							 if(document.getElementsByName("statusText")[0].value == "-1"
							 || document.getElementsByName("statusText")[1].value == "-1"
							 || document.getElementsByName("statusText")[2].value == "-1"){ 
								 
							    if(document.getElementsByName("statusText")[1].value == "-1"){
							       document.getElementById("dispMsg").innerHTML = "ไม่สามารถ Import ข้อมูล Masterได้  ";
								   document.getElementById("dispMsg").className = "msgError";
							    }else{
								   document.getElementById("dispMsg").innerHTML = "ไม่สามารถ Import ข้อมูลบางส่วน ได้";
								   document.getElementById("dispMsg").className = "msgError";
							    }
							 }else{
								 document.getElementById("dispMsg").innerHTML = "Import เรียบร้อยแล้ว"; 
								 document.getElementById("dispMsg").className = "msgSuccess";
							 }
						 }
						 
					 }
					
					</script>
					</c:if>
						<br><br>
						<!-- BODY -->
						<div align="left">
						    <input type="button" value="Clear(ControlImporExport)" class="newPosBtnLong" style="width: 250px;" onClick="javascript:clearControlForm('${pageContext.request.contextPath}','admin')">
						   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						    Time Process Use: <html:text property="monitorBean.timeInUse" readonly="true"></html:text> Seconds
						</div>
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
 <!-- Control Save Lock Screen -->
<jsp:include page="../controlTestURLConnLockScreen.jsp" flush="true"/> 
<!-- Control Save Lock Screen -->