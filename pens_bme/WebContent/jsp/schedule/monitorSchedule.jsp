<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.scheduler.utils.CronExpressionUtil"%>
<%@page import="com.isecinc.pens.scheduler.manager.SchedulerConstant"%>
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
<%

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

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
			        	<jsp:include page="../menu_q.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="searchTask"/>
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
						<html:form action="/jsp/searchTask">
						<jsp:include page="../error.jsp"/>
						
                         <!-- hidden field -->
					     <input type ="hidden" name="pageAction" />
                        <input type ="hidden" name="selectNoDelete" />
                      
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