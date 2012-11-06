<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />
<%
User user = (User) session.getAttribute("user");
String role = ((User)session.getAttribute("user")).getType();


List<References> importList = ImportHelper.readConfigTableImport();
pageContext.setAttribute("importList",importList,PageContext.PAGE_SCOPE);

List<References> importSalesUpdateList = ImportHelper.readConfigTableUpdateSalesImport();
pageContext.setAttribute("importUpdateSalesList",importSalesUpdateList,PageContext.PAGE_SCOPE);

List<References> exportList = ExportHelper.readConfigTableExport();
pageContext.setAttribute("exportList",exportList,PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.ImportHelper"%>
<%@page import="com.isecinc.pens.inf.helper.ExportHelper"%>
<jsp:useBean id="userForm" class="com.isecinc.pens.web.user.UserForm" scope="request" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/interfaces.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>

<!-- Calendar -->
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.8.23.custom.css" type="text/css" /> 

<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
	
	#main_progressbar {
	    position:relative;
	}
	#progresstext {
	    position:absolute;
	    margin-top:-20;
	    left:0;
	}

}
</style>


	<!-- ProgressBar -->
	<% if( "submited".equals(request.getAttribute("action"))){ %>
	   <script type="text/javascript" language="javascript">
	   var stepMaxUp = 2;
	   var stepMinUp = 1;
	   var stepHaftMinUp = 0.5;
	   var progressCount = 0;
	   var useTimeMillisecs = 0;
	   var startTime = new Date();
	   
	 /*   var displaytext = function () {
		   var text = $("#progressbar").progressbar('value') + '%';
		   var element = $("<span style='position: absolute;'>" + progressCount + "</span>");
		   $(document.body).append(element);
		   var width = element.width();
		   var height = element.height();
		   var location = $("#progressbar").offset();
		   element.css({ left: location.left + $("#progressbar").width() / 2 - width / 2, top: location.top +    $("#progressbar").height() / 2 - height / 2 });
		} */
	   
	    function update(status){
	    	  if(status != '1' && status != "-1"){ //Running
	    		 /* if (document.getElementById) { // DOM3 = IE5, NS6 
	    			 document.getElementById("progressbar").style.visibility = 'visible'; 
	    		 }else{
	    			 document.getElementById('progressbar').style.visibility='visible'; 
	    		 } 	 */
	    		 
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
	    		/*  if (document.getElementById) { // DOM3 = IE5, NS6 
	    			 document.getElementById("progressbar").style.visibility = 'hidden'; 
	    		 }else{
	    			 document.getElementById('progressbar').style.visibility='hidden';
	    		 } 	  */
	    		 progressCount = 100;
	    		 useTimeMillisecs = (new Date()).getTime()-startTime.getTime();
	    	 }  
	    	  
	    	  var progress = $("#progressbar") .progressbar("option","value");
	    	  if (progress < 100) {
		   	      $("#progressbar").progressbar("option", "value", progressCount);  
		   	      $("#progresstext").html("<b>"+progressCount+" %</b>");
		   	  }
	    	  
	    	//  displaytext();
	    	  
	    }
	    
	     $(function() {
	    	$("#progressbar").progressbar({ value: progressCount });
	    	});

	   /** Onload Window    */
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
	    	   if(status == '1'){ //Finish Task
	    		   /** Task Success ***/
	    		   update(status);
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

<!-- Move for new index. -->
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
						
						<!-- CRITERIA  Submit-->
						<% if( request.getAttribute("action") == null){ %>  

						 <%if( !role.equalsIgnoreCase(User.ADMIN)){ %>
						     <table align="center" border="0" cellpadding="3" cellspacing="10" width="100%">
								<tr>
									<td align="right" width ="50%"><b>Import ข้อมูลจากส่วนกลาง<font color="red">*</font> </b></td>
									<td align="left" width ="50%">
									   <input type="button" name ="import" value="ดึงข้อมูลจากส่วนกลาง" class="newPosBtn" style="height:30px;width: 160px;" onClick="javascript:syschronizeFromOracle('${pageContext.request.contextPath}','sales')">
									</td>
								</tr>
								<tr>
								  <td align="right" width ="50%"><b>Export ข้อมูลไปยังส่วนกลาง<font color="red">*</font> </b></td>
								  <td align="left">
								       <input type="button" name ="export" value="ส่งข้อมูลไปส่วนกลาง" class="newPosBtn" style="height:30px; width: 160px;" onClick="javascript:syschronizeToOracle('${pageContext.request.contextPath}','sales')">
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
										<input type="button" name ="import" value="Import" class="newPosBtn" style="width: 100px;" onClick="javascript:syschronizeFromOracle('${pageContext.request.contextPath}','admin')">	
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
										<input type="button" name ="importTrans" value="Import Update Sales Transaction" class="newPosBtn" style="width: 280px;" onClick="javascript:updateSalesTransaction('${pageContext.request.contextPath}','admin')">
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
										    <input type="button" name ="export" value="Export" class="newPosBtn" style="width: 100px;" onClick="javascript:syschronizeToOracle('${pageContext.request.contextPath}','admin')">
										    
										</td>
									</tr>
							   </table>
							</fieldset>
						   <%} %>
					    <%} %>
				
						<!-- PROGRESS BAR-->
						 <html:hidden property="monitorBean.status"/>
						<% if( "submited".equals(request.getAttribute("action"))){ %>  
						 <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						    <tr>
								<td align="center" width ="100%">
								    <%--  <div id="progressbarXX"  style="width:600px;">
								       <img alt="" src="${pageContext.request.contextPath}/images/progress_bar.gif"">
								   </div> --%>
								   
									 <!-- End demo -->
									   <div style="height:50px;align:center">
									     กรุณารอสักครู่......
									   </div>
									  <div id="main_progressbar" style="height:80px;width:600px;">
			                              <div id="progressbar"></div>
			                              <div id="progresstext"></div>
			                          </div>
			                          <!-- End demo -->
			                          
								 </td>
							</tr>
						</table>
						
                          
					    <%} %>
							
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						   <tr>
								<td align="right" width ="100%"> &nbsp;</td>
								
							</tr>
							<tr>
								<td align="center" width ="100%">
								    <input type="button" value="ตรวจสอบสถานะล่าสุด" class="newPosBtn" style="width: 160px;" onClick="javascript:search('${pageContext.request.contextPath}','admin')" title="<%=com.isecinc.pens.inf.helper.ConvertUtils.genEnvStr() %>"> 
								    &nbsp;&nbsp;&nbsp;<input type="button" value="Clear" class="newPosBtn" style="width: 160px;" onClick="javascript:clearForm('${pageContext.request.contextPath}','admin')">
								    &nbsp;&nbsp;&nbsp;<a href ="javascript:window.open('<%=request.getContextPath()%>/jsp/adminConsole.do?do=process&currentTab=tab_config_info','','width=700px,height=400px')" title="ตรวจสอบ FTP Connection"><b>?</b></a>
								</td>
							</tr>
						</table>

						 
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
									 <c:when test="${interfacesForm.criteria.monitorBean.type == 'IMPORT'}">
											จำนวนไฟล์สำเร็จ / จำนวนไฟล์ทั้งหมด
									 </c:when>
									 <c:otherwise>
											จำนวนไฟล์ 
									  </c:otherwise>
								   </c:choose>
								</th>
				<!--				<th> Remark</th>-->
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
								
								<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}">
								       
						                <td> <c:out value='${rows.index+1}'/></td>
						                <td> ${results.transactionId}</td>
										<td> ${results.type}</td>
										<td>
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
											<c:otherwise>
												Update Transaction Sales
											</c:otherwise>
										</c:choose>
										</td>
										<td> ${results.createUser}</td>
										
										<td> 
										  <c:choose>
											<c:when test="${results.status == 1}">
												<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:when>
											<c:otherwise>
												<img border=0 src="${pageContext.request.contextPath}/icons/uncheck.gif">
											</c:otherwise>
										   </c:choose>
										</td>
										<td> ${results.submitDate}</td>
										<td>
										<c:choose>
												<c:when test="${results.type == 'IMPORT'}">
													${results.successCount}/ ${results.fileCount}
												</c:when>
												<c:otherwise>
													${results.fileCount}
												</c:otherwise>
											</c:choose>
										</td>
										<td align="left"> ${results.errorMsg}</td>
										<td> 
										     <a href="#" onclick="javascript:searchDetail('${pageContext.request.contextPath}','admin','${results.monitorId}');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
										</td>
								</tr>
								</c:forEach>
							</table>
						</c:if>
					
						<br><br>
						<!-- BODY -->
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