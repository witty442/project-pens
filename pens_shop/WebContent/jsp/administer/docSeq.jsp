<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<% 
List<References> docRun = InitialReferences.getReferenes().get(InitialReferences.DOC_RUN);
pageContext.setAttribute("docRun",docRun,PageContext.PAGE_SCOPE);
%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<jsp:useBean id="docSeqForm" class="com.isecinc.pens.web.admin.DocSequenceForm" scope="request" />

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script language = "javascript" src = "${pageContext.request.contextPath}/js/docseq.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script language = "javascript" src = "${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
</head>

<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="939" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr style="height: 137px;">
	    <td background="${pageContext.request.contextPath}/images2/page1_topbar.png" valign="top">
	    	<!-- HEADER -->
	    	<jsp:include page="../header.jsp"/>
	    </td>
  	</tr>
  	<tr id="framerow">
    	<td background="${pageContext.request.contextPath}/images2/page1_bgcontent.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="900" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="DocumentSeq"/>
				<jsp:param name="code" value="<%=docSeqForm.getDocSequence().getDoctypeLabel() %>"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="900" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="9"><img src="${pageContext.request.contextPath}/images2/boxcont1_1.gif" width="9" height="9" /></td>
		            <td width="832"><img src="${pageContext.request.contextPath}/images2/boxcont1_5.gif" width="100%" height="9" /></td>
		            <td width="9"><img src="${pageContext.request.contextPath}/images2/boxcont1_2.gif" width="9" height="9" /></td>
	      		</tr>
	      		<tr>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"><img src="${pageContext.request.contextPath}/images2/boxcont1_8.gif" width="9" height="1" /></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/docseqAction">
						<jsp:include page="../error.jsp"/>
						<!-- CRITERIA -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td width="30%"></td>
								<td width="20%"></td>
								<td width="15%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Document.Type" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="docSequence.doctypeLabel" size="15" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Sales.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="docSequence.salesCode" size="15" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="NewCount" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="docSequence.orderType">
										<html:options collection="docRun" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="StartNumber" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="docSequence.startNo" size="15" style="text-align: right;" onkeydown="return inputNum(event);"/>
								</td>
								<td align="right"><bean:message key="NextNumber" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="docSequence.currentNext" size="15" style="text-align: right;" onkeydown="return inputNum(event);"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="LastYear" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="docSequence.currentYear" size="15" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="LastMonth" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="docSequence.currentMonth" size="15" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="#" onclick="save('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
									<input type="button" value="บันทึก" class="newPosBtn">	
									</a>
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<html:hidden property="docSequence.id"/>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						</html:form>
						<!-- BODY -->
					</td>
					<td background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"><img src="${pageContext.request.contextPath}/images2/boxcont1_8.gif" width="9" height="1" /></td>
				</tr>
				<tr style="height: 9px;">
		            <td><img src="${pageContext.request.contextPath}/images2/boxcont1_4.gif" width="9" height="9" /></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"><img src="${pageContext.request.contextPath}/images2/boxcont1_7.gif" width="1" height="9" /></td>
		            <td><img src="${pageContext.request.contextPath}/images2/boxcont1_3.gif" width="9" height="9" /></td>
	          	</tr>
    		</table>
    	</td>
    </tr>
    <tr>
    	<td background="${pageContext.request.contextPath}/images2/page1_bgcontent.png" valign="top">
   			<!-- FOOTER -->
    		<table width="900" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
    			<tr>
	        		<td colspan="2"><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="15" /></td>
	      		</tr>
        		<tr>
        			<td align="left"><img src="${pageContext.request.contextPath}/images2/footer.jpg" width="454" height="38" /></td>
        			<td align="right"><a href="#top"><img src="${pageContext.request.contextPath}/images2/but_top.gif" width="59" height="18" border="0" /></a></td>
        		</tr>
        	</table>
        </td>
    </tr>
    <tr style="height: 62px;">
    	<td valign="top">
    		<img src="${pageContext.request.contextPath}/images2/page1_footer.png" width="939" height="62" />
    	</td>
  	</tr>
</table>
</body>
</html>