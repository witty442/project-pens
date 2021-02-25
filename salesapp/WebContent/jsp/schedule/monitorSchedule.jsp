<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.scheduler.utils.CronExpressionUtil"%>
<%@page import="com.isecinc.pens.scheduler.manager.SchedulerConstant"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%

%>
<html>
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

function loadMe(){		  
	new Epoch('epoch_popup','th',"searchCreateDateFrom");
	new Epoch('epoch_popup','th',"searchCreateDateTo");
}	

function searchForm(path){
   document.searchTaskForm.action = path + "/jsp/searchTask.do?do=search";
   document.searchTaskForm.submit();
}
function clearForm(path){
   document.searchTaskForm.action = path + "/jsp/searchTask.do?do=prepare&action=new";
   document.searchTaskForm.submit();
}
function submitViewDetail(path,transactionId){
	//alert('checkDate1');
	//document.searchTaskForm.action = path + "/jsp/monitorInterfacesAction.do?do=viewById&transactionId="+transactionId;
	//document.searchTaskForm.submit();   
	
	var url = path + "/jsp/interfaces/monitor_interfacesById.jsp?transactionId="+transactionId;
	PopupCenterFullHeight(url, "MonitorDetail", 800);
}
</script>

</head>		
<body class="sb-nav-fixed" onload="loadMe()">

	   <!-- Include Header Mobile  -->
       <jsp:include page="../header.jsp"  flush="true"/>
       <!-- /Include Header Mobile -->
       
    	<!-- PROGRAM HEADER -->
      	<jsp:include page="../program.jsp">
			<jsp:param name="function" value="searchTask"/>
		</jsp:include>
			
		<!-- BODY -->
		<html:form action="/jsp/searchTask">
		<jsp:include page="../error.jsp"/>
             <!-- Content -->
				<table width="100%" border="0" cellspacing="1" cellpadding="2">
					<tr>
						<td  colspan="2" align="center"><b>Search Task Schedule</b></td>
					</tr>
					<tr align="center">
						<td align="right" width="45%">Select Job&nbsp;&nbsp; </td>
						<td align="left" width="55%"> 
						 <html:select property="programId">
							<html:options collection="jobList" property="key" labelProperty="name"/>
					     </html:select>
						</td>
					</tr>
					<tr align="center">
						<td align="right" width="45%">No </td>
						<td align="left" width="55%"> 
						  <html:text property="searchId"></html:text>
						  Task Name
						  <html:text property="searchTaskName"></html:text>
						</td>
					</tr>
					<tr align="center">
						<td align="right"  width="45%">CreateDate From: </td>
						<td align="left" width="55%">
						  <html:text  property="searchCreateDateFrom"  styleId="searchCreateDateFrom" readonly="true"></html:text>
						   <html:select property="searchTimeFromHour">
							<html:options collection="hourList" property="key" labelProperty="name"/>
					     </html:select>:
					     <html:select property="searchTimeFromMinute">
							<html:options collection="minuteList" property="key" labelProperty="name"/>
					     </html:select>
					     
						  Create Date To :
						   <html:text  property="searchCreateDateTo" styleId="searchCreateDateTo"  readonly="true"></html:text>
						   <html:select property="searchTimeFromHour">
							 <html:options collection="hourList" property="key" labelProperty="name"/>
					       </html:select>:
					       <html:select property="searchTimeFromMinute">
							 <html:options collection="minuteList" property="key" labelProperty="name"/>
					       </html:select>
						</td>
					</tr>
					<tr>
						<td align="center" colspan="2">&nbsp;</td>
					</tr>	
					<tr>
						<td align="center" colspan="2">
					       <html:button property="button2" styleClass="newPosBtn" value="Search Job" onclick="searchForm('${pageContext.request.contextPath}');"/>
					       <html:button property="button2" styleClass="newPosBtn" value="Clear" onclick="clearForm('${pageContext.request.contextPath}');"/>
						 </td>
					</tr>	
				</table>
                 <hr>
                   
		       <!-- Pageing -->
			   <jsp:include page="subScheduleDetail.jsp" />
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