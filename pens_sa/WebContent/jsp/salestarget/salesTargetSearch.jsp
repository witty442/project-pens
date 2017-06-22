
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.Utils"%>
<%@page import="util.SIdUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
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
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="salesTargetForm" class="com.isecinc.pens.web.salestarget.SalesTargetForm" scope="session" />
<%
//for test clear session
//SIdUtils.getInstance().clearInstance();
		
User user = (User) request.getSession().getAttribute("user");
String role = user.getRoleSalesTarget();
String pageName = Utils.isNull(request.getParameter("pageName"));
if(pageName.equals("")){
	pageName = salesTargetForm.getPageName();
}
String pageNameTemp = "";
if(SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "MKT_SalesTarget";
}else if(SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){ 
	if(role.equalsIgnoreCase(User.DD_SALES)){
	   pageNameTemp = "DD_SalesTarget";
	}else if ( Utils.userInRole(user,new String[]{User.MT_SALES}) ){
	   pageNameTemp = "MT_SalesTarget";
	}
}else if(SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "MTMGR_SalesTarget";
}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "ReportSalesTarget";
}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "ReportSalesTargetAll";
}
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/salesTarget.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">
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
				<jsp:param name="function" value="<%=pageNameTemp%>"/>
			</jsp:include>
			<!-- Hidden Field -->
		    <html:hidden property="pageName" value="<%=pageName %>"/>
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
						<html:form action="/jsp/salesTargetAction">
						<jsp:include page="../error.jsp"/>

						<div align="center">
						   	<%if(SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){ %>
						       <jsp:include page="criteria/MKTCriteria.jsp" /> 
						    <%}else if(SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){ %>
						       <jsp:include page="criteria/MTCriteria.jsp" /> 
						    <%}else if(SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){ %>
						       <jsp:include page="criteria/MTMGRCriteria.jsp" /> 
						    <%}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName)){ %>
						       <jsp:include page="criteria/ReportSalesTargetCriteria.jsp" /> 
						    <%}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL.equalsIgnoreCase(pageName)){ %>
						       <jsp:include page="criteria/ReportSalesTargetAllCriteria.jsp" />  
						    <%} %>
					    </div>
					  
					   <!-- ************************Result ***************************************************-->
					  <%
					 // System.out.println("Results:"+request.getSession().getAttribute("RESULTS"));
					  if(request.getSession().getAttribute("RESULTS") != null) {
					    out.println(request.getSession().getAttribute("RESULTS"));
					  
					  %>
					<!-- ************************Result ***************************************************-->
					  <%if(User.MTMGR.equalsIgnoreCase(pageName)){ %>
					  <div align="center">
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="center" >
									     <c:if test="${salesTargetForm.bean.canFinish == true}">
											<a href="javascript:salesManagerFinish('${pageContext.request.contextPath}')">
											  <input type="button" value=" ͹��ѵ�������� " class="newPosBtnLong">
											</a>
										 </c:if>
										<a href="javascript:backToMainPage('${pageContext.request.contextPath}')">
										  <input type="button" value=" �Դ˹�Ҩ�  " class="newPosBtnLong">
										</a>
									</td>
									
								</tr>
							</table>
					</div>
					  <%} }%>
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