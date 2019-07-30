<%@page import="util.SessionUtils"%>
<%@page import="util.UserUtils"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="util.Utils"%>
<%@page import="util.SIdUtils"%>
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
<jsp:useBean id="salesTargetForm" class="com.isecinc.pens.web.salestarget.SalesTargetForm" scope="session" />
<%
//for test clear session
//SIdUtils.getInstance().clearInstance();
		 
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "salesTargetForm");

User user = (User) request.getSession().getAttribute("user");
String role = user.getRoleSalesTarget();
String pageName = Utils.isNull(request.getParameter("pageName"));
if(pageName.equals("")){
	pageName = salesTargetForm.getPageName();
}
String subPageName = Utils.isNull(request.getParameter("subPageName"));
if(subPageName.equals("")){
	subPageName = salesTargetForm.getSubPageName();
}
/* System.out.println("pageName:"+pageName);
System.out.println("subPageName:"+subPageName);
System.out.println("salesTargetForm Bean:"+salesTargetForm.getBean());
 */
String pageNameTemp = "";
if(SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "MKT_SalesTarget";
}else if(SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){ 
	if(role.equalsIgnoreCase(User.DD_SALES)){
	   pageNameTemp = "DD_SalesTarget";
	}else if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MT_SALES}) ){
	   pageNameTemp = "MT_SalesTarget";
	}
}else if(SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "MTMGR_SalesTarget";
}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName)){ 
	if("TT".equals(subPageName)){
	   pageNameTemp = "ReportSalesTargetTT";
	}else{
	   pageNameTemp = "ReportSalesTargetMT";	
	}
}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "ReportSalesTargetAll";
}else if(SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "MKT_SalesTarget_TT";
}else if(SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "TTSUPER_SalesTarget";
}else if(SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "TTMGR_SalesTarget";
}else if(SalesTargetConstants.PAGE_TTADMIN.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "TTADMIN_SalesTarget";
}
//System.out.println("pageNameTemp:"+pageNameTemp);
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
		    <html:hidden property="subPageName" value="<%=subPageName %>"/>
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
						        <jsp:include page="criteria/MKTCriteria.jsp" flush="true"/> 
						    <%}else if(SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){ %>
						        <jsp:include page="criteria/MTCriteria.jsp" flush="true" />  
						    <%}else if(SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){ %>
						        <jsp:include page="criteria/MTMGRCriteria.jsp" flush="true" /> 
						    <%}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName)){ %>
						        <%if(!"TT".equals(subPageName)){ %>
						           <jsp:include page="criteria/ReportSalesTargetCriteria.jsp" flush="true" />  
						        <%}else{ %>
						           <jsp:include page="criteria/ReportSalesTargetTTCriteria.jsp" flush="true" />  
						        <%} %>
						    <%}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL.equalsIgnoreCase(pageName)){ %>
						        <jsp:include page="criteria/ReportSalesTargetAllCriteria.jsp" flush="true" /> 
						    <%}else if(SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName)){ %>
						        <jsp:include page="criteria/MKT_TTCriteria.jsp" flush="true" />
						    <%}else if(SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName)){ %>
						        <jsp:include page="criteria/TTSUPER_Criteria.jsp" flush="true" />   
						    <%}else if(SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName)){ %>
						        <jsp:include page="criteria/TTMGR_Criteria.jsp" flush="true" />   
						      <%}else if(SalesTargetConstants.PAGE_TTADMIN.equalsIgnoreCase(pageName)){ %>
						        <jsp:include page="criteria/TTADMIN_Criteria.jsp" flush="true" />   
						    <%} %>
					    </div>
					  
					   <!-- ************************Result ***************************************************-->
					  <%
					 // System.out.println("Results:"+request.getSession().getAttribute("RESULTS"));
					  if(request.getSession().getAttribute("RESULTS") != null) {
					     out.println(request.getSession().getAttribute("RESULTS"));
					  
					  %>
						<!-- ************************Result ***************************************************-->
						  <%if(SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){ %>
							  <div align="center">
								   <table  border="0" cellpadding="3" cellspacing="0" >
									<tr>
										<td align="center" >
										      <c:if test="${salesTargetForm.bean.canFinish == true}">
												<a href="javascript:salesManagerFinish('${pageContext.request.contextPath}')">
												  <input type="button" value=" อนุมัติเป้าหมาย " class="newPosBtnLong">
												</a>
											 </c:if>
											<a href="javascript:backToMainPage('${pageContext.request.contextPath}')">
											  <input type="button" value=" ปิดหน้าจอ  " class="newPosBtnLong">
											</a>
										</td>	
									 </tr>
									</table>
							    </div>
						  <%}else if(SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName)){  %>
							  <div align="center">
								   <table  border="0" cellpadding="3" cellspacing="0" >
									<tr>
										<td align="center" >
										     <c:if test="${salesTargetForm.bean.canFinish == true}">
												<a href="javascript:salesManagerFinish('${pageContext.request.contextPath}')">
												  <input type="button" value=" อนุมัติเป้าหมายรวมทุกแบรนด์ " class="newPosBtnLong">
												</a>
											 </c:if>
											<a href="javascript:backToMainPage('${pageContext.request.contextPath}')">
											  <input type="button" value=" ปิดหน้าจอ  " class="newPosBtnLong">
											</a>
										</td>	
									 </tr>
									</table>
							    </div>
						 <%}else if(SalesTargetConstants.PAGE_TTADMIN.equalsIgnoreCase(pageName)){  %>
							  <div align="center">
								   <table  border="0" cellpadding="3" cellspacing="0" >
									<tr>
										<td align="center" >
											<a href="javascript:changeStatusTTByAdmin('${pageContext.request.contextPath}')">
												 <input type="button" value=" บันทึก  " class="newPosBtnLong">
											</a>
											<a href="javascript:backToMainPage('${pageContext.request.contextPath}')">
											  <input type="button" value=" ปิดหน้าจอ  " class="newPosBtnLong">
											</a>
										</td>	
									 </tr>
									</table>
							    </div>
					 <% } }%>
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