<%@page import="com.isecinc.pens.web.b2b.B2BAction"%>
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
<jsp:useBean id="b2bForm" class="com.isecinc.pens.web.b2b.B2BForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "b2bForm");
		
User user = (User) request.getSession().getAttribute("user");
String role = user.getRoleSalesTarget();
String pageName = Utils.isNull(request.getParameter("pageName"));
String popup = Utils.isNull(request.getParameter("popup"));
if(pageName.equals("")){
	pageName = b2bForm.getPageName();
}

/** Count Visit Page */
PageVisit.processPageVisit(request,pageName);
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script>
/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}

</script>

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
				<jsp:param name="function" value="<%=pageName%>"/>
			</jsp:include>
			<!-- Hidden Field -->
		    <html:hidden property="pageName" value="<%=pageName %>"/>
		    <html:hidden property="popup" value="<%=popup %>"/>
		        
		    <input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
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
						<html:form action="/jsp/b2bAction" enctype="multipart/form-data">
						<jsp:include page="../error.jsp"/>
						
                      <!-- ************************Criteria *******************************************-->
						<div align="center">
						   <%if(B2BAction.PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){ %>
						       <jsp:include page="criteria/b2bMakroCriteria.jsp" flush="true" /> 
						   <%}else if(B2BAction.PAGE_Q_B2B_MAKRO_SALESBYITEM.equalsIgnoreCase(pageName)){ %>
						       <jsp:include page="criteria/queryB2BMakroSalesByItemCriteria.jsp" flush="true" /> 
						   <% } %>
					    </div>
					  
					    <!-- Result -->
				 	    <%if(B2BAction.PAGE_Q_B2B_MAKRO_SALESBYITEM.equalsIgnoreCase(pageName)){   
				 	          if(request.getAttribute("b2bForm_RESULT") != null){
				 	        	 out.println( ((StringBuffer)request.getAttribute("b2bForm_RESULT")).toString());
				 	          }
				 	     }
				 	    %>
					   
					   <!-- Hidden field -->
					   <input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
					   <input type="hidden" name="pageName" id="pageName" value="<%=pageName%>"/>
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
