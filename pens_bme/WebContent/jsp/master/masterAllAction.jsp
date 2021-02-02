<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.isecinc.pens.web.master.MasterAllAction"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%
String processType = "";
Map<String,String> param = new HashMap<String,String>();
String action = Utils.isNull(request.getParameter("action"));
if("genBMEProductGroup".equalsIgnoreCase(action)){
	 String preAction = Utils.isNull(request.getParameter("preAction"));
	 String custGroup = Utils.isNull(request.getParameter("custGroup"));
	 
	 param.put("preAction", preAction);
	 param.put("custGroup", custGroup);
	 processType = "genBMEProductGroup";
	 
	 MasterAllAction.precess(request,processType, param);
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){

}
function gen(processType){
	var form = document.tempForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/master/masterAllAction.jsp?action="+processType;
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.tempForm;
	form.action = path + "/jsp/masterAction.do?do=prepare&action=new&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
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
				<jsp:param name="function" value="xx"/>
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
						<html:form action="/jsp/tempAction">
						<jsp:include page="../error.jsp"/>
						
							<!-- BUTTON -->
							<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							   <tr>
									<td align="left">
										<b><u>Gen BME Product Group </u></b>
										<br/>-�ó� 1 PensItem ���� Barcode ���͡ �� Insert 1 record ���� �͡��§ҹ
										<br/>-TABLE : PENSBI.BME_PRODUCT_GROUP 
									</td>
								</tr>
								<tr>
									<td align="center">
									    Pre Action:
									    <select name="preAction" id="preAction">
									       <option value=""></option>
									       <option value="deleteAll">ź��������ҷ�����</option>
									    </select>
									     Cust Group:
									    <select name="custGroup" id="custGroup">
									       <option value="LOTUS">LOTUS</option>
									       <option value="SHOP">SHOP</option>
									    </select>
										<a href="javascript:gen('genBMEProductGroup')">
										  <input type="button" value="GenBMEProductGroup" class="newPosBtn"> 
										</a>
									</td>
								</tr>
								<tr>
									<td align="center"><hr></td>
							</table>
					<!-- RESULT -->
				    
					<!-- hidden field -->
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