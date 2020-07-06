<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.bean.User"%>
<jsp:useBean id="generalForm" class="com.isecinc.pens.web.general.GeneralForm" scope="session" />
 <%
 String pageName = Utils.isNull(request.getParameter("pageName"));
 %>
<html>
<head>
<title>General App</title>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
</style>

<script>
function loadMe(){
	
}
function submitExecute(path){
	//var reportName = document.getElementById("reportName").value;
	                               path + "/jsp/reportAllAction.do?do=search";
	document.generalForm.action = path + "/jsp/generalAction.do?do=submitExecute";
	document.generalForm.submit();
}
function submitClear(path){
	//var reportName = document.getElementById("reportName").value;
	document.generalForm.action = path + "/jsp/generalAction.do?do=prepare&action=new";
	document.generalForm.submit();
}
</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();" style="height: 100%;">
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
						<html:form action="/jsp/generalAction">
						<jsp:include page="../../error.jsp"/>
						   <table style="width:100%" border="0" align="center" class="text">
							  <tr>
							    <td width="30%" align="right"> ระบุ 12 หลักแรก:</td>
							    <td width="70%">
							       <html:text property="bean.productCode" styleClass="\" autoComplete=\"off" maxlength="12" onblur="isNum(this)"></html:text>
							       <INPUT TYPE="button" class="newPosBtnLong" name ="B_QUERY" VALUE="GenBarcode" onclick="submitExecute('<%=request.getContextPath()%>');">
							     </td> 
							  </tr>
							   <tr>
							    <td width="30%" align="right"> Barcode ที่ได้คือ:</td>
							    <td width="70%">
							       <html:text property="bean.barcode" readonly="true" styleClass="disableText"></html:text>
							       <INPUT TYPE="button" class="newPosBtnLong" name ="B_CLEAR" VALUE="Clear" onclick="submitClear('<%=request.getContextPath()%>');">
							     </td> 
							  </tr>
							  <tr>
							    <td width="100%" colspan="2" align="center">
							    
							    </td>
							  </tr>
							</table>
					        
					        <%
					       /*  if( request.getAttribute("DATA_JOJO") != null) {
					           out.println((String)request.getAttribute("DATA_JOJO"));
					        } */
					        %>
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
