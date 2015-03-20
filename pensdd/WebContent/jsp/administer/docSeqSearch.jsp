<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<% 
User user = (User)session.getAttribute("user");
%>
<jsp:useBean id="docSeqForm" class="com.isecinc.pens.web.admin.DocSequenceForm" scope="request" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/docseq.js"></script>
<script language="javascript">
function setResultVisible(value)
{
	var idresult = document.getElementById('result');
	idresult.style.visibility=value;
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="DocumentSeq"/>
				<jsp:param name="code" value=""/>
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
						<html:form action="/jsp/docseqAction">
						<jsp:include page="../error.jsp"/>
						<%if(!user.getType().equalsIgnoreCase(User.ADMIN)) {%>
						<c:if test="${docSeqForm.results == null}">
						<div style="text-align: left;width: 100%;">
						&nbsp;&nbsp;<input type="button" value="สร้างเลขที่เอกสาร" onclick="genseq('${pageContext.request.contextPath}');">
						<br><br>
						</div>
						</c:if>
						<%} %>
						<!-- RESULT -->
						<c:if test="${docSeqForm.results != null}">
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
						&nbsp;<span class="searchResult">${docSeqForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
								<th class="code"><bean:message key="Sales.Code" bundle="sysele"/></th>
								<th><bean:message key="Document.Type" bundle="sysele"/></th>
								<th class="code"><bean:message key="LastYear" bundle="sysele"/></th>
								<th class="code"><bean:message key="LastMonth" bundle="sysele"/></th>
								<th class="code"><bean:message key="NewCount" bundle="sysele"/></th>
								<th class="code"><bean:message key="StartNumber" bundle="sysele"/></th>
								<th class="code"><bean:message key="NextNumber" bundle="sysele"/></th>
								<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
								<th class="status"><bean:message key="Edit"  bundle="sysprop"/></th>
								<%} %>
							</tr>
							<c:forEach var="results" items="${docSeqForm.results}" varStatus="rows">
							<c:choose>
								<c:when test="${rows.index %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>">
								<td><c:out value='${rows.index+1}'/></td>
								<td align="center">${results.salesCode}</td>
								<td align="left">${results.doctypeLabel}</td>
								<td align="center">${results.currentYear}</td>
								<td align="center">${results.currentMonth}</td>
								<td align="center">${results.orderLabel}</td>
								<td align="center">${results.startNo}</td>
								<td align="center">${results.currentNext}</td>
								<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
								<td align="center">
									<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','${results.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/data_edit.gif"></a>
								</td>
								<%} %>
							</tr>
							</c:forEach>
						</table>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="result">	
							<tr>
								<td align="left" colspan="10" class="footer">&nbsp;</td>
							</tr>
						</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn"></a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
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