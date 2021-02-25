<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.scheduler.utils.CronExpressionUtil"%>
<%@page import="com.isecinc.pens.scheduler.manager.SchedulerConstant"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<%
String autoRefresh = Utils.isNull(request.getParameter("autoRefresh"));
String timeAuto = Utils.isNull(request.getParameter("timeAuto"));
 %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
</style>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js"></script>

<script type="text/javascript">

function hideAll() {
	var runNowArea = document.getElementById("runNowArea");
	var runOnceArea = document.getElementById("runOnceArea");
	var runWeeklyArea1 = document.getElementById("runWeeklyArea1");
	var startTimeArea  = document.getElementById("startTimeArea");
	var runDailyArea  = document.getElementById("runDailyArea");
	var runMonthlyArea  = document.getElementById("runMonthlyArea");
	
	runNowArea.style.display = "none";
	runOnceArea.style.display = "none";
	runWeeklyArea1.style.display = "none";
	runWeeklyArea2.style.display = "none";
	startTimeArea.style.display = "none";	
	runDailyArea.style.display = "none";
	runMonthlyArea.style.display = "none";	
}

function showArea(areaId){
	var element = document.getElementById(areaId);
	element.style.display = "block";
}

function selectRun(){
	selectValue = document.scheduleForm.run.options[document.scheduleForm.run.selectedIndex].value;
	
	 if (selectValue == "<%=SchedulerConstant.SCHEDULE_TYPE_NOW%>"){
			hideAll();
			showArea("runNowArea");
			new Epoch('epoch_popup','th',document.scheduleForm.startDate);
	 }else  if (selectValue == "<%=SchedulerConstant.SCHEDULE_TYPE_ONCE%>"){
			hideAll();
			showArea("runOnceArea");
			showArea("startTimeArea");
	 		new Epoch('epoch_popup','th',document.scheduleForm.startDate);	
	 }else if (selectValue == "<%=SchedulerConstant.SCHEDULE_TYPE_DAILY%>"){
		   hideAll();
		   showArea("runDailyArea");
		   showArea("startTimeArea");
		   new Epoch('epoch_popup','th',document.scheduleForm.startDate);
	 }else if (selectValue == "<%=SchedulerConstant.SCHEDULE_TYPE_WEEKLY%>"){
			hideAll();
			showArea("runWeeklyArea1");
			showArea("runWeeklyArea2");
			showArea("startTimeArea");
			new Epoch('epoch_popup','th',document.scheduleForm.startDate);
	 }else if (selectValue == "<%=SchedulerConstant.SCHEDULE_TYPE_MONTHLY%>"){
			hideAll();
			showArea("runMonthlyArea");
			showArea("startTimeArea");
			new Epoch('epoch_popup','th',document.scheduleForm.startDate);
	 }else {
		hideAll();
	}
}

function changeTaskName() {
	document.location='viewTaskForm.do?taskName=' + taskForm.taskName.value;
}

function cancel(){		
}

function loadMe(){		  
    var timeMilisec = 9999;
	//setInterval("displaytime()", 1000);  
	new Epoch('epoch_popup','th',document.scheduleForm.startDate);
	
	hideAll();
	
	//autoRefresh
	//if( document.scheduleForm.autoRefresh.value !=''){
	  // if(document.scheduleForm.timeAuto.value != ''){
	    //  timeMilisec = 6000* parseFloat(document.scheduleForm.timeAuto.value);
	  // }
	//   alert(timeMilisec);
	  // setTimeout("autoRefreshSearch()",timeMilisec);
	//}
}	

function submitForm(path){
	//alert('checkDate1');
	var valid = true;
	var run = document.getElementById("run");
	if(run.value =='DAILY'){
		if(document.getElementById("startMinute").value ==""){
        	document.getElementById("startMinute").focus();
        	valid = false;
		}
        if(!valid){
        	alert("กรุณากรอกข้อมูลให้ครบ");
        	return false;
        }
	}
	if(confirm("ยืนยันสร้าง Batch Task ")){
      document.scheduleForm.pageAction.value ="run";
      document.scheduleForm.action = path + "/jsp/schedule.do?do=runBatch";
      document.scheduleForm.submit();
   }
}

function submitSearch(path){
	//alert('checkDate1');
	document.scheduleForm.pageAction.value ="search";
	document.scheduleForm.action = path + "/jsp/schedule.do?do=search";
	document.scheduleForm.submit();
}

function submitDelete(path,no){
	//alert('checkDate1');
	if(confirm("ยืนยันลบ Batch Task นี้")){
	   document.scheduleForm.pageAction.value ="delete";
	   document.scheduleForm.selectNoDelete.value = no;
	   document.scheduleForm.action = path + "/jsp/schedule.do?do=deleteBatch";
	   document.scheduleForm.submit();   
	 }
}

function autoRefreshSearchCheck(obj){
   if(obj.checked){
       document.scheduleForm.autoRefresh.value ="autoRefresh";
       document.scheduleForm.action = '${pageContext.request.contextPath}' + "/jsp/schedule.do?do=search&autoRefresh=autoRefresh";
       document.scheduleForm.submit();
   }else{
       document.scheduleForm.autoRefresh.value ="";
       document.scheduleForm.action = '${pageContext.request.contextPath}' + "/jsp/schedule.do?do=search";
       document.scheduleForm.submit();
   }
}

function autoRefreshSearch(){
       document.scheduleForm.autoRefresh.value ="autoRefresh";
       document.scheduleForm.action = '${pageContext.request.contextPath}' + "/jsp/schedule.do?do=search&autoRefresh=autoRefresh";
       document.scheduleForm.submit();
}

function submitRegen(path,programId,paramRegen,type){
	//alert('checkDate1');
	if(type != 'NOW' && type != 'ONCE'){
	   var url = path + "/jsp/schedule/popupRegen.jsp?programId="+programId+"&PARAM_REGEN="+paramRegen;
	   PopupCenter(url,"regen",300,400);
    }
}
</script>
</head>		
<body class="sb-nav-fixed" onload="loadMe()">

	<!-- Include Header Mobile  -->
    <jsp:include page="../header.jsp"  flush="true"/>
    <!-- /Include Header Mobile -->
       
	<!-- PROGRAM HEADER -->
  	<jsp:include page="../program.jsp">
     <jsp:param name="function" value="scheduler"/>
    </jsp:include>
			
	<!-- BODY -->
	<html:form action="/jsp/schedule">
	<jsp:include page="../error.jsp"/>
						

    <%--    <div><input type="checkbox" name="auto" id="auto" onclick="autoRefreshSearchCheck(this)" <%out.print(autoRefresh.equals("")?"":"checked");%>/>
       Auto Refresh Search Job ทุกๆ  <input type="text" name="timeAuto" id="timeAuto" size="1" value="<%=timeAuto %>"/>นาที
       AutoRefresh :<input type="text" name="autoRefresh" id="autoRefresh" value="<%=autoRefresh %>"/>
       </div> --%>
	  <!-- Content -->
	<table width="100%" border="0" cellspacing="1" cellpadding="3">
		<tr>
			<td  colspan="3" align="center"><b>Scheduler Task</b></td>
		</tr>
		<tr align="center">
			<td align="right" width="45%">Select Job </td>
			<td align="left" width="55%" colspan="2"> 
			 <html:select property="programId">
				<html:options collection="jobList" property="key" labelProperty="name"/>
		     </html:select>
		     <font color="red">*</font>
			</td>
		</tr>
		<tr align="center">
			<td  align="right" width="45%">Type Run </td>
			<td  align="left" width="10%" nowrap>
				 <select name="run" onChange="selectRun()" id="run">
					<!-- <option value="" selected>Please select</option> -->
				    <option value="<%=SchedulerConstant.SCHEDULE_TYPE_NOW%>">Schedule Now</option>
				 	<option value="<%=SchedulerConstant.SCHEDULE_TYPE_ONCE%>">Schedule Once</option> 
				    <option value="<%=SchedulerConstant.SCHEDULE_TYPE_DAILY%>">Schedule Daily</option>
					<option value="<%=SchedulerConstant.SCHEDULE_TYPE_WEEKLY%>">Schedule Weekly</option>
				    <option value="<%=SchedulerConstant.SCHEDULE_TYPE_MONTHLY%>">Schedule Monthly</option>
				</select>
				<font color="red">*</font>
			</td>
			<td  align="left" width="45%" >
			   <span id="runNowArea">
			      Task will run now only.
		       </span>
		       <span id="runOnceArea">
			      Task will run once only.
		       </span>
		       <span id="runWeeklyArea1">
			      Task will run every week on the following days.
		       </span>
			</td>
		</tr>
	</table>
	
	<table width="100%" border="0" cellspacing="1" cellpadding="2" align="center">
		<tr id="runWeeklyArea2"  align="center">
			<td  colspan="2">&nbsp;&nbsp;
			<table width="300" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="25"><input type="checkbox" name="day"
						value="<%=CronExpressionUtil.CRON_MONDAY%>"></td>
					<td width="10">&nbsp;</td>
					<td width="120" class="font1">Monday</td>
					<td width="10">&nbsp;</td>
					<td width="25"><input type="checkbox" name="day"
						value="<%=CronExpressionUtil.CRON_FRIDAY%>"></td>
					<td width="110" class="font1">Friday</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="day"
						value="<%=CronExpressionUtil.CRON_TUESDAY%>"></td>
					<td>&nbsp;</td>
					<td class="font1">Tuesday</td>
					<td>&nbsp;</td>
					<td><input type="checkbox" name="day"
						value="<%=CronExpressionUtil.CRON_SATURDAY%>"></td>
					<td class="font1">Saturday</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="day"
						value="<%=CronExpressionUtil.CRON_WEDNESDAY%>"></td>
					<td>&nbsp;</td>
					<td class="font1">Wednesday</td>
					<td>&nbsp;</td>
					<td><input type="checkbox" name="day"
						value="<%=CronExpressionUtil.CRON_SUNDAY%>"></td>
					<td class="font1">Sunday</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="day"
						value="<%=CronExpressionUtil.CRON_THURSDAY%>"></td>
					<td>&nbsp;</td>
					<td class="font1">Thursday</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr id="runDailyArea" align="center">
			<td width="35%" align="right"> Run </td>
			<td width="65%" align="left" nowrap> 
			<html:radio property="everyType" value="MINUTE">Minutes</html:radio>
			ทุกๆ 
			<html:select property="everyMinutes">
			   <html:option value="10">10</html:option>
			   <html:option value="20">20</html:option>
			   <html:option value="30">30</html:option>
			</html:select>
			นาที
			&nbsp;&nbsp;&nbsp;&nbsp;
			<html:radio property="everyType" value="HOURLY">Hourly</html:radio>
			ทุกๆ 
		     <html:text property="everyHourly" styleId="everyTime" size="2" maxlength="3"/>
			ชั่วโมง
			<html:hidden property="everyDay" styleId="everyDay" value="1"/>
		</tr>
		<tr id="runMonthlyArea" align="center">
			<td width="35%" align="left">ทุกๆ วันที่  </td>
			<td width="65%" align="left" nowrap> <html:text property="nDay" styleId="nDay" size="4"></html:text> &nbsp;&nbsp;</td>
		</tr>
		<tr id="startTimeArea"  align="center">
			<td  colspan="2">Start Time &nbsp;
			<html:text property="startDate" styleId="startDate" readonly="true"/>
			<a href="#" onClick="javascript: scheduleForm.startDate.value='';">Clear</a>
			  <html:select property="startHour" styleId="startHour">
				 <html:options collection="hourList" property="key" labelProperty="name"/>
		       </html:select>:
		       <html:select property="startMinute" styleId="startMinute">
				 <html:options collection="minuteList" property="key" labelProperty="name"/>
		       </html:select>
		      
			</td>
		</tr>
		<tr>
			<td align="center" colspan="2">&nbsp;</td>
		</tr>	
		<tr>
			<td align="center" colspan="2">
			<html:button property="button" value="Submit Job" styleClass="newPosBtn" onclick="submitForm('${pageContext.request.contextPath}');"/>
			&nbsp; <html:button property="button2" styleClass="newPosBtn" value="Search Job" onclick="submitSearch('${pageContext.request.contextPath}');"/>
			 </td>
		</tr>	
	  </table>
	 <hr>
	  <!-- Pageing -->
	  <jsp:include page="subScheduleResults.jsp" />
	  <!-- Pageing -->   
	
	    <!-- hidden field -->
	    <input type ="hidden" name="pageAction" />
	   <input type ="hidden" name="selectNoDelete" />
	</html:form>
	
	<!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->	
</body>
</html>