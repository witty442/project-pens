<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<jsp:useBean id="customervisitForm" class="com.isecinc.pens.web.customervisit.VisitForm" scope="request" />

<%
String role = ((User)session.getAttribute("user")).getType();

List<References> actives = InitialReferences.getReferenes().get(InitialReferences.ACTIVE);
pageContext.setAttribute("actives",actives,PageContext.PAGE_SCOPE);

%>

<html>
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
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerVisit.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('dateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('dateTo'));
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
				<jsp:param name="function" value="CustomerVisit"/>
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
						<html:form action="/jsp/customervisitAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td colspan="2">
									<a href="#" onclick="prepare('${pageContext.request.contextPath}','add')">
									<img border=0 src="${pageContext.request.contextPath}/icons/doc_add.gif" align="absmiddle">&nbsp;
									<bean:message key="CreateNewRecord" bundle="sysprop"/></a>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Trip.CustomerName" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td colspan="3">
									<html:text property="visit.customerLabel" readonly="true" styleClass="disableText" size="60"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Number" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td colspan="3">
									<html:text property="visit.code"/>
								</td>
							</tr>
							<tr>
								<td width="35%" align="right"><bean:message key="DateFrom" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td width="15%">
									<html:text property="visit.dateFrom" maxlength="10" size="15" styleId="dateFrom" readonly="true"/>
								</td>
								<td align="right" width="5%"><bean:message key="DateTo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td>
									<html:text property="visit.dateTo" maxlength="10" size="15" styleId="dateTo" readonly="true"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td colspan="3">
									<html:select property="visit.isActive">
										<html:option value=""></html:option>
										<html:options collection="actives" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="javascript:search('${pageContext.request.contextPath}')">
									<!--<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ค้นหา" class="newPosBtn">
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<!--<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">-->
									<input type="button" value="Clear" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<c:if test="${customervisitForm.results != null}">
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
						&nbsp;<span class="searchResult">${customervisitForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th class="order"><bean:message key="No" bundle="sysprop"/></th>
								<th class="name"><bean:message key="CustomerVisit.No" bundle="sysele"/></th>
								<th class="name"><bean:message key="Date" bundle="sysele"/></th>
								<th class="status"><bean:message key="Time" bundle="sysele"/></th>
								<th class="status"><bean:message key="CloseSales" bundle="sysele"/></th>
								<th class="name"><bean:message key="UnCloseReason" bundle="sysele"/></th>
								<th class="status"><bean:message key="Exported" bundle="sysele"/></th>
								<th class="status"><bean:message key="Interfaces" bundle="sysele"/></th>
								<th class="status"><bean:message key="Status" bundle="sysele"/></th>
								<th class="status"><bean:message key="Edit" bundle="sysprop"/></th>
								<th class="status"><bean:message key="View" bundle="sysprop"/></th>
							</tr>	
						<c:forEach var="results" items="${customervisitForm.results}" varStatus="rows">
							<c:choose>
								<c:when test="${rows.index %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>">
								<td width="37px;"><c:out value='${rows.index+1}'/></td>
								<td align="left" width="126px;">${results.code}</td>
								<td align="center" width="120px;">${results.visitDate}</td>
								<td align="center" width="48px;">${results.visitTime}</td>
								<td align="center" width="48px;">
									<c:if test="${results.salesClose=='Y'}">
										<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									</c:if> 
								</td>
								<td align="left" width="129px;">${results.unclosedReasonLabel}</td>
								<td align="center" width="51px;">
									<c:if test="${results.exported=='Y'}">
										<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									</c:if>
								</td>
								<td align="center" width="51px;">
									<c:if test="${results.interfaces=='Y'}">
										<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									</c:if>
								</td>
								<td align="center" width="53px;">${results.activeLabel}</td>
								<td align="center" width="51px;">
									<c:choose>
										<c:when test="${results.exported!='Y'}">
											<c:if test="${results.isActive!='C'}">
											<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit','${results.id}');">
											<img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif"></a>
											</c:if>
										</c:when>
										<c:otherwise>
											<img border=0 src="${pageContext.request.contextPath}/icons/user_edit_dis.gif">
										</c:otherwise>
									</c:choose>
								</td>
								<td align="center">
									<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','${results.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
								</td>
							</tr>
						</c:forEach>
							<tr>
								<td align="left" class="footer" colspan="11">&nbsp;</td>
							</tr>
						</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="backToCustomer('${pageContext.request.contextPath}','${customervisitForm.visit.customerId}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									</a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<html:hidden property="criteria.from"/>
						<html:hidden property="visit.customerId"/>
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