<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="com.isecinc.pens.web.stock.StockForm"%>
<%@page import="com.pens.util.PageVisit"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stock.StockConstants"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockForm" class="com.isecinc.pens.web.stock.StockForm" scope="session" />
<%
StockBean bean = ((StockForm)session.getAttribute("stockForm")).getBean();
User user = (User) request.getSession().getAttribute("user");
String role = user.getRoleSalesTarget();
String pageName = Utils.isNull(request.getParameter("pageName"));
String popup = Utils.isNull(request.getParameter("popup"));
if(pageName.equals("")){
	pageName = stockForm.getPageName();
}
String pageNameTemp = pageName;

/** Count Visit Page */
PageVisit.processPageVisit(request,pageNameTemp);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script>
/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}
</script>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.stockForm;
	//alert("onload");
	
	<%if(session.getAttribute("GEN_PDF_SUCCESS") != null){%>
		// alert(path);
		form.action = "${pageContext.request.contextPath}/jsp/stockAction.do?do=loadPDFReport";
		form.submit();
		return true;
	<%}%>
}

function clearForm(path){
	var form = document.stockForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function searchReport(path){
	var form = document.stockForm;
	//alert(form.startDate.value);
	if( $('#period').val()==""){
		alert("กรุณาระบุ เดือน");
		return false;
	} 

	form.action = path + "/jsp/stockAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}

function exportReport(path,e){
	var form = document.stockForm;
	 if( $('#period').val()==""){
		alert("กรุณาระบุ เดือน");
		return false;
	} 
	 
	//To disable f5
	$(document).bind("keydown", disableF5);
	 
	//document.getElementById("progress").style.display = "block";
	//document.getElementById("control_btn").style.display = "none";
	
	/**Control Save Lock Screen **/
    startControlSaveLockScreen();
	
	// alert(path);
	form.action = path + "/jsp/stockAction.do?do=exportReport&action=newsearch";
	form.submit();
	return true;
}

function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
}
//To re-enable f5
$(document).unbind("keydown", disableF5);
</script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">

			<!-- Hidden Field -->
		    <html:hidden property="pageName" value="<%=pageName %>"/>
		    <html:hidden property="popup" value="<%=popup %>"/>
		    <input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
	
				<!-- BODY -->
				<html:form action="/jsp/stockAction">
				<jsp:include page="../../error.jsp"/>

				<div align="center">
				<table align="center" border="0" cellpadding="3" cellspacing="0" >
				  <tr>
					 <td colspan="1" nowrap> 
					 <font size="3"><b><bean:message key="<%=pageNameTemp %>" bundle="sysprop"/></b></font>
					 </td>
				  </tr>
			       <tr><td colspan="1" nowrap> 
					   <span id="div_month">
						        เดือน <font color="red">*</font>
						     <html:select property="bean.period" styleId="period">
								<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
						    </html:select>
					    </span>
					</td></tr>
					<tr><td colspan="1" nowrap> 
					   <span id="div_month">
						        ปิดรอบของ PD <font color="red">*</font>
						     <html:select property="bean.pdCode" styleId="pdCode">
								<html:options collection="PD_LIST" property="pdCode" labelProperty="pdDesc"/>
						    </html:select>
					    </span>
					</td></tr>
			   </table>
			   <br/>
			   <div id="control_btn" title=" "  style="display:block">
			   <table  border="0" cellpadding="3" cellspacing="0" >
					<tr>
						<td align="left">
							 <a href="javascript:exportReport('${pageContext.request.contextPath}')">
							  <input type="button" value="  พิมพ์  " class="newPosBtnLong"> 
							</a>
							
							<%-- <a href="javascript:downloadDocument('${pageContext.request.contextPath}')">
							  <input type="button" value="  พิมพ์(ajax)  " class="newPosBtnLong"> 
							</a> --%>
							&nbsp;
							<a href="javascript:clearForm('${pageContext.request.contextPath}')">
							  <input type="button" value="   Clear   " class="newPosBtnLong">
							</a>
							&nbsp;
							<a href="javascript:window.close()">
							  <input type="button" value=" ปิดหน้าจอนี้  " class="newPosBtnLong">
							</a>			
						</td>
					</tr>
				</table>
				</div>

			    </div>
			  
			   <!-- ************************Result ***************************************************-->
			  <%
			 // System.out.println("Results:"+request.getSession().getAttribute("RESULTS"));
			  if(request.getSession().getAttribute("stockForm_RESULTS") != null) {
				  if(   StockConstants.PAGE_STOCK_CALLC_CREDIT.equalsIgnoreCase(pageName)){
			   %>
					<div id ="scroll" align="center">
						<% out.println(request.getSession().getAttribute("stockForm_RESULTS")); %>
					</div>
			  <% 
				  }else{
			          out.println(request.getSession().getAttribute("stockForm_RESULTS"));
				  }
			  }
			  %>
			<!-- ************************Result ***************************************************-->
			</html:form>
			<!-- BODY -->
</body>
</html>

 <!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->
