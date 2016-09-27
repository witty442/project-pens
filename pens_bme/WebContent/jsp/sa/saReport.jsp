<%@page import="com.isecinc.pens.bean.SAReportBean"%>
<%@page import="com.isecinc.pens.dao.SAEmpDAO"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>

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
<jsp:useBean id="saReportForm" class="com.isecinc.pens.web.sa.SAReportForm" scope="session" /> 

<%
User user = (User) request.getSession().getAttribute("user");
String pageN = Utils.isNull(request.getParameter("page"));

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}
span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('payDate'));
}
function clearForm(path){
	var form = document.saReportForm;
	form.action = path + "/jsp/saReportAction.do?do=clear";
	form.submit();
	return true;
}

function search(path){
	var form = document.saReportForm;
	form.action = path + "/jsp/saReportAction.do?do=search";
	form.submit();
	return true;
}
function exportToExcel(path,empId){
	var form = document.saReportForm;
	form.action = path + "/jsp/saReportAction.do?do=exportToExcel&empId="+empId;
	form.submit();
	return true;
}
function exportToExcelAll(path){
	var form = document.saReportForm;
	form.action = path + "/jsp/saReportAction.do?do=exportToExcelAll";
	form.submit();
	return true;
}

</script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerMC.jsp"/></td>
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
	       <%if(pageN.equals("saStatementReport")){ %>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="saStatementReport"/>
				</jsp:include>
			<% }else{%>
				<jsp:include page="../program.jsp">
					<jsp:param name="function" value="saOrisoftReport"/>
				</jsp:include>
			<%} %>
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
						<html:form action="/jsp/saReportAction">
						<jsp:include page="../error.jsp"/>
						
					 <div align="center">
                       <%if(pageN.equals("saStatementReport")){ %>
                       
						      <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
			                         <td> Employee ID <font color="red"></font></td>
									<td>		
										 <html:text property="bean.empId" styleId="empId" size="20"/>
									</td>
								   <td>Name<font color="red"></font></td>
									<td>		
				                       <html:text property="bean.name" styleId="name" size="20"/>
									    Surname
									      <html:text property="bean.surname" styleId="surname" size="20"/>
									</td>
								</tr>
								<tr>
								    <td  align="right">Region<font color="red"></font></td>    
									<td colspan="2">
									     <html:select property="bean.region" styleId="region">
											<html:options collection="empRegionList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
									<td>
									Group Store
									     <html:select property="bean.groupStore" styleId="groupStore">
											<html:options collection="groupStoreList" property="code" labelProperty="desc"/>
									    </html:select>
									    
									    Branch <html:text property="bean.branch" styleId="branch" size="20"/>
									</td>
								</tr>
								<tr>
								    <td  align="right"> <!-- ปี    --><font color="red"></font></td>    
									<td colspan="2">
									  <%--    <html:select property="bean.year" styleId="year">
											<html:options collection="yearList" property="code" labelProperty="desc"/>
									    </html:select> --%>
									</td>
									<td>
									ณ เดือน
									     <html:select property="bean.month" styleId="month">
											<html:options collection="monthList" property="code" labelProperty="desc"/>
									    </html:select>
									  
									</td>
								</tr>
						      </table>
						     <%}else if(pageN.equals("saOrisoftReport")){ %>
						      
						      <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
								    <td  align="right"> <!-- ปี    --><font color="red"></font></td>    
									<td colspan="2">
									  <%--    <html:select property="bean.year" styleId="year">
											<html:options collection="yearList" property="code" labelProperty="desc"/>
									    </html:select> --%>
									</td>
									<td>
									ณ เดือน <font color="red">*</font>
									     <html:select property="bean.month" styleId="month">
											<html:options collection="monthList" property="code" labelProperty="desc"/>
									    </html:select>
									  
									</td>
								</tr>
						      </table>
						   <%} %>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									    <a href="javascript:exportToExcelAll('${pageContext.request.contextPath}')">
										  <input type="button" value=" Export To Excel " class="newPosBtnLong"> 
										</a>
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>
					  
					   <%if(pageN.equals("saStatementReport")){ %>
						    <jsp:include page="saStatementReport_sub.jsp" />  
						<%}else if(pageN.equals("saOrisoftReport")){ %>
						    <jsp:include page="saOrisoftReport_sub.jsp" />  
		                <%} %>
					
					
					<!-- hidden field -->
					<input type="hidden" name="page" value="<%=pageN %>"/>
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