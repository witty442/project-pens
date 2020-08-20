<%@page import="com.isecinc.pens.web.buds.BudsAllForm"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.ConfPickingBean"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="budsAllForm" class="com.isecinc.pens.web.buds.BudsAllForm" scope="session" />

<%
ConfPickingBean bean = ((BudsAllForm)session.getAttribute("budsAllForm")).getBean().getConfPickingBean();
System.out.println("ConfPickingBean:"+bean);
String role = ((User)session.getAttribute("user")).getType();
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
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight")); 
	String hideAll = "true"; 
	String pageName = Utils.isNull(request.getParameter("pageName"));
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- For fix Head and Column Table -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript">
function loadMe(){
	var form = document.pickingAllForm;
}
function search(path){
	var form = document.pickingAllForm;
	form.action = path + "/jsp/pickingAllAction.do?do=search";
	form.submit();
	return true;
}
function exportExcel(path){
	var form = document.pickingAllForm;
	
	form.action = path + "/jsp/pickingAllAction.do?do=export";
	form.submit();
	return true;
}
function clearForm(path){
	var form = document.pickingAllForm;
	form.action = path + "/jsp/pickingAllAction.do?do=prepare&action=new";
	form.submit();
	return true;
}
function confirmPicking(path){
	var form = document.pickingAllForm;
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	form.action = path + "/jsp/pickingAllAction.do?do=confPicking";
	form.submit();
	return true;
}
function rejectPicking(path){
	var form = document.pickingAllForm;
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	form.action = path + "/jsp/pickingAllAction.do?do=rejectPicking";
	form.submit();
	return true;
}
function finishPicking(path){
	var form = document.pickingAllForm;
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	form.action = path + "/jsp/pickingAllAction.do?do=finishPicking";
	form.submit();
	return true;
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
			<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="<%=pageName %>"/>
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
						<html:form action="/jsp/pickingAllAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
				    	<!-- ***** Criteria ******* -->
				    	<table align="center" border="0" cellpadding="3" cellspacing="0" >
					        <tr>
				                <td> Load No</td>
								<td>
								    <html:select property="bean.confPickingBean.loadNo">
								      <html:options collection="LOAD_NO_LIST" property="value" labelProperty="keyName"/>
								   </html:select> 
							    </td>
							 </tr>
						</table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="100%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtnLong"> 
								</a>&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtnLong">
								</a>&nbsp;
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtnLong">
								</a>
								
								<%if(role.equalsIgnoreCase(User.TT)){ %>
								    &nbsp;&nbsp;&nbsp;&nbsp;
									<a href="javascript:confirmPicking('${pageContext.request.contextPath}')">
									  <input type="button" value="Confirm Picking" class="newPosBtnLong">
									</a>
								<%} %>
								
								<%if(role.equalsIgnoreCase(User.BUD_ADMIN)){ %>
								    &nbsp;&nbsp;&nbsp;&nbsp;
									<a href="javascript:rejectPicking('${pageContext.request.contextPath}')">
									  <input type="button" value="Reject Picking" class="newPosBtnLong">
									</a>
									&nbsp;&nbsp;
									<a href="javascript:finishPicking('${pageContext.request.contextPath}')">
									  <input type="button" value="Finish Picking" class="newPosBtnLong">
									</a>
								<%} %> 
							</td>
							 <td align="right" width="40%" nowrap></td>
						</tr>
					</table>
			  </div>
			
			     <!-- ****** RESULT *************************************************** -->
			     <%
			       if(request.getAttribute("pickingAllForm_RESULTS") != null ){
			    	 out.println(((StringBuffer)request.getAttribute("pickingAllForm_RESULTS")).toString());
			    	 %>
			    	 <script>
						//load jquery
						/* $(function() { 
							//Load fix column and Head
							$('#tblProduct').stickyTable({overflowy: true});
						}); */
					 </script>
			    <%} %>
			     <!-- ***************************************************************** -->
					<!-- hidden field -->
					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
					
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
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>

 <!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->