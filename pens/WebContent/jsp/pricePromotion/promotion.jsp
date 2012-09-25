<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="modifierForm" class="com.isecinc.pens.web.modifier.ModifierForm" scope="request" />
<%

%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
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
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script language = "javascript" src = "${pageContext.request.contextPath}/js/javascript.js"></script>
<script language = "javascript" src = "${pageContext.request.contextPath}/js/promotion.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
function loadMe()
{
	
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
				<jsp:param name="function" value="Promotion"/>
				<jsp:param name="code" value="${modifierForm.modifier.code}"/>
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
						<html:form action="/jsp/modifierAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="30%"></td>
								<td width="15%"></td>
								<td width="15%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="ModifierType" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="modifier.type" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"></td>
								<td align="left"></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Code" bundle="sysprop"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="modifier.code" readonly="true" styleClass="disableText" />
								</td>
								<td align="right"><bean:message key="Member.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="modifier.name" size="35" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="DateFrom" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="modifier.startDate" readonly="true" styleClass="disableText"  size="15"/>
								</td>
								<td align="right"><bean:message key="DateTo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="modifier.endDate" readonly="true" styleClass="disableText" size="15"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Description" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="modifier.description" readonly="true" styleClass="disableText"  size="65"/>
								</td>
							</tr>
						</table>
						<br/>
						<c:if test="${modifierForm.lines != null}">
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th class="order"><bean:message key="No" bundle="sysprop"/></th>
								<th class="status"><bean:message key="Modifier.No" bundle="sysele"/></th>
								<th class="code"><bean:message key="Modifier.Level" bundle="sysele"/></th>
								<th class="status"><bean:message key="Modifier.Type" bundle="sysele"/></th>
								<th class="code"><bean:message key="DateFrom" bundle="sysele"/>-<bean:message key="DateTo" bundle="sysele"/></th>
								<th class="status"><bean:message key="Modifier.AttrItem" bundle="sysele"/></th>
								<th class="code"><bean:message key="Modifier.AttrItemVal" bundle="sysele"/></th>
								<th class="code"><bean:message key="Modifier.BreakType" bundle="sysele"/></th>
								<th class="status"><bean:message key="Modifier.Operator" bundle="sysele"/></th>
								<th class="code"><bean:message key="Modifier.AttrUOM" bundle="sysele"/></th>
								<!-- <th>Application Method</th> -->
								<th class="order"><bean:message key="Modifier.ValFrom" bundle="sysele"/></th>
								<th class="order"><bean:message key="Modifier.ValTo" bundle="sysele"/></th>
								<!-- <th>Value</th> -->
								<th style="width: 50px;"><bean:message key="View" bundle="sysprop"/></th>
							</tr>	
						<c:forEach var="results" items="${modifierForm.lines}" varStatus="rows">
						<c:choose>
							<c:when test="${rows.index %2 == 0}">
								<c:set var="tabclass" value="lineO"/>
							</c:when>
							<c:otherwise>
								<c:set var="tabclass" value="lineE"/>
							</c:otherwise>
						</c:choose>	
						<tr class="<c:out value='${tabclass}'/>">
							<td width="36px;"><c:out value='${rows.index + 1}'/></td>
							<td align="center" width="57px;">${results.id}</td>
							<td align="center" width="75px;">${results.levels}</td>
							<td align="center" width="53px;">${results.type}</td>
							<td align="center" width="77px;">
								${results.startDate} - ${results.endDate}
							</td>
							<td align="center" width="50px;">${results.attr.productAttribute}</td>
							<td align="left" width="84px;">${results.attr.attributeValueLabel}</td>
							<td align="center" width="64px;">${results.breakType}</td>
							<td align="center">${results.attr.operator}</td>
							<td align="center">${results.attr.productUOM.code}</td>
							<td align="right">
								<c:if test="${results.attr.valueFrom!=0}">
								${results.attr.valueFrom}
								</c:if>
							</td>
							<td align="right">
								<c:if test="${results.attr.valueTo!=0}">
								${results.attr.valueTo}
								</c:if>
							</td>
							<td align="center">
								<span class="formInfo">
									<a href="#" onclick="javascript:showDetail('${pageContext.request.contextPath}','${results.type}',${results.id})" >
										<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif">
									</a>
								</span>
							</td>
						</tr>
						</c:forEach>
							<tr>
								<td align="left" colspan="15" class="footer">&nbsp;</td>
							</tr>
						</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									</a>
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