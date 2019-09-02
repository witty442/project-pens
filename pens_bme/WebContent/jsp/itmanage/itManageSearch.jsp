
<%@page import="com.isecinc.pens.web.itmanage.ITManageAction"%>
<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="itManageForm" class="com.isecinc.pens.web.itmanage.ITManageForm" scope="session" />
<%
User user = (User) request.getSession().getAttribute("user");
String pageName = itManageForm.getPageName();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('dateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('dateTo'));
}

function clearForm(path){
	var form = document.itManageForm;
	form.action = path + "/jsp/itManageAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.itManageForm;
	if( $('#mcArea').val()==""){
		alert("กรุณาระบุ เขตพื้นที่");
		return false;
	}
	/* if( $('#staffType').val()==""){
		alert("กรุณาระบุ ประเภท");
		return false;
	} */
	
	form.action = path + "/jsp/itManageAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.itManageForm;
	form.action = path + "/jsp/itManageAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function newDoc(path){
	 var form = document.itManageForm;
	var param ="";
	form.action = path + "/jsp/itManageAction.do?do=prepare&mode=add"+param;
	form.submit();
	return true; 
}
function openEdit(path,docNo){
	 var form = document.itManageForm;
	var param ="&docNo="+docNo;
	form.action = path + "/jsp/itManageAction.do?do=prepare&mode=edit"+param;
	form.submit();
	return true; 
}
function openCopy(path,docNo){
	 var form = document.itManageForm;
	var param ="&docNo="+docNo;
	form.action = path + "/jsp/itManageAction.do?do=prepare&mode=copy"+param;
	form.submit();
	return true; 
}
function printReport(path,docNo){
   var url = path+"/jsp/popup/printPayPopup.jsp?report_name=PayInReport&docNo="+docNo;
   //, "Print2", "width=800,height=400,location=No,resizable=No");
	PopupCenter(url,'Printer',800,350);
}
function openPopup(path,pageName){
	var form = document.itManageForm;
	var param = "&hideAll=true&pageName="+pageName;
	 if("SalesrepSales" == pageName){
		param += "&selectone=true";
	}
	url = path + "/jsp/popupAction.do?do=prepareAll&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,desc2,desc3,pageName){
	var form = document.itManageForm;
	if('SalesrepSales' == pageName){
		form.salesrepCode.value = code;
		form.salesrepFullName.value = desc;
		form.zone.value = desc2;
		form.zoneName.value = desc3;
	}
} 
</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerDoc.jsp"/></td>
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
						<html:form action="/jsp/itManageAction">
						<jsp:include page="../error.jsp"/>

						  <!-- ***********************Criteria******************************-->
						  <%if(pageName.equalsIgnoreCase(ITManageAction.PAGE_IT_STOCK)){ %>
						    <jsp:include page="criteria/itStockItemCri.jsp" flush="true"></jsp:include>	
				          <%} %>
				          
		                  <!-- ************************Result ******************************-->	
		                   <%if(pageName.equalsIgnoreCase(ITManageAction.PAGE_IT_STOCK)){ %>
		                     <jsp:include page="result/itStockItemResult.jsp" flush="true"></jsp:include>	
		                   <%} %>
		                   
					    <!-- hidden field -->
					   <html:hidden property="pageName"/>
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
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->