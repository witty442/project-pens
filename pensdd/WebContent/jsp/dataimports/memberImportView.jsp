<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<jsp:useBean id="memberImportForm" class="com.isecinc.pens.web.dataimports.member.MemberImportForm" scope="request" />
<c:set var="formName" value="memberImportForm"></c:set>
<c:set var="actionName" value="${pageContext.request.contextPath}/jsp/memberImportAction.do"></c:set>

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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/memberImport.js"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png'); doCheckAll(true);" style="height: 100%;">
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
				<jsp:param name="function" value="MemberImport"/>
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
						<html:form action="/jsp/memberImportAction">
						<jsp:include page="../error.jsp"/>
						<!-- paginator@1 -->
						<html:hidden property="resultScreen" styleId="resultScreen"></html:hidden>
						<html:hidden property="pageObjKey" styleId="pageObjKey"></html:hidden>
						<html:hidden property="currentPage" styleId="currentPage"></html:hidden>
						<html:hidden property="orderBy" styleId="orderBy"></html:hidden>
						<html:hidden property="txtDorA" styleId="txtDorA"></html:hidden>
						<html:hidden property="focusFieldId" styleId="focusFieldId"></html:hidden>
						<c:set var="pageKey" value="${PAGINATOR}"></c:set>
						<!-- paginator@1 -->
						<html:hidden property="tempSelectedIds" styleId="tempSelectedIds"></html:hidden>
						<html:hidden property="nextYear" styleId="nextYear"></html:hidden>
						<!-- edit member -->
						<input type="hidden" name="currentId"/>
						<input type="hidden" name="currentOrderLineRemain"/>
						<input type="hidden" name="currentStartNextYear"/>
						<input type="hidden" name="oldId"/>
						<input type="hidden" name="oldOrderLineRemain"/>
						<input type="hidden" name="oldStartNextYear"/>

						<table align="center" width="100%" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr align="center">
								<th width="3%" >
									<c:if test = "${pageKey.totalRows != 0}">
										<html:checkbox property="checkAll" value="Y" onclick="doCheckAll(false);"/>
									</c:if>
									<c:if test = "${pageKey.totalRows == 0}">
										&nbsp;
									</c:if>
								</th>
								<th class="order"><bean:message key="No" bundle="sysprop"/></th>
								<th class="code"><bean:message key="Code" bundle="sysprop"/></th>
								<th class="name"><bean:message key="Member.Name" bundle="sysele"/>-<bean:message key="Member.Surname" bundle="sysele"/></th>
								<th class="th"><bean:message key="Address" bundle="sysele"/></th>
								<th class="status"><bean:message key="RestOfPeriod" bundle="sysele"/></th>
								<th class="status"><bean:message key="StartDate" bundle="sysele"/>&nbsp;<bean:message key="Year" bundle="sysele"/> ${memberImportForm.nextYear}</th>
								<th class="status"><bean:message key="Status" bundle="sysele"/></th>
								<th class="status"><bean:message key="Edit" bundle="sysprop"/></th>
							</tr>
							<c:if test = "${pageKey.totalRows == 0}">
							<tr >
								<td colspan="9"><bean:message key="RecordsFound"  bundle="sysprop"/></td>
							</tr>
							</c:if>
							<c:if test = "${pageKey.totalRows != 0}">
							<c:forEach var="result" items="${pageKey.page}" varStatus="rows">
							<c:choose>
								<c:when test="${rows.index %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr>
								<td align="center">
									<c:if test="${result.errorDesc == null or result.errorDesc == ''}">
									<html:checkbox property="ids" value="${result.id}" onclick="doValidateCheck();"/>
									</c:if>
									<c:if test="${result.errorDesc != null and result.errorDesc != ''}">
									&nbsp;
									</c:if>
								</td>	
								<td align="center">${result.id}</td>
								<td align="center">${result.customer.code}</td>
								<td align="left">${result.customer.name} ${result.customer.name2}</td>
								<td align="left">
									${result.address.line1}
									${result.address.line2}
									${result.address.line3}
									${result.address.district.name}
									${result.address.province.name}
									${result.address.postalCode}
								</td>
								<td align="center">
									<c:if test="${result.errorDesc == null or result.errorDesc == ''}">
										<input type="text" id="orderLineRemain${result.id}" name="orderLineRemain${result.id}" style="border:none; text-align:right;" size="10" maxlength="10" disabled="disabled" onkeydown="return isNum0to9andpoint(this, event);" value="${result.customer.orderLineRemain}"/>
									</c:if>
									<c:if test="${result.errorDesc != null and result.errorDesc != ''}">&nbsp;</c:if>
								</td>
								<td align="center">
									<c:if test="${result.errorDesc == null or result.errorDesc == ''}">
									<input type="text" id="startNextYear${result.id}" name="startNextYear${result.id}" style="border:none; text-align:center;" size="10" maxlength="10" disabled="disabled" readonly="readonly" value="${result.customer.startNextYear}"/>
									<script type="text/javascript" language="javascript">
									new Epoch("epoch_popup","th",document.getElementById("startNextYear${result.id}"));
									</script>
									</c:if>
									<c:if test="${result.errorDesc != null and result.errorDesc != ''}">
									&nbsp;
									</c:if>
								</td>
								<td>
									<c:if test="${result.errorDesc == null or result.errorDesc == ''}">
									<img src="${pageContext.request.contextPath}/icons/check.gif" title="<bean:message key="Success"  bundle="sysprop"/>"/>
									</c:if>
									<c:if test="${result.errorDesc != null and result.errorDesc != ''}">
									<img src="${pageContext.request.contextPath}/icons/uncheck.gif" title="<bean:message key="Fail"  bundle="sysprop"/>"/>
									${result.errorDesc}
									</c:if>
								</td>
								<td align="center">
									<c:if test="${result.errorDesc == null or result.errorDesc == ''}">
									<span id="edit${result.id}" style="display:">
										<img src="${pageContext.request.contextPath}/icons/data_edit.gif" title="<bean:message key="Edit"  bundle="sysprop"/>" onclick="doEditMember(${result.id});" style="cursor: hand;"/>
									</span>
									<span id="save${result.id}" style="cursor:pointer; display:none">
										<img src="${pageContext.request.contextPath}/icons/process.gif" title="<bean:message key="Save"  bundle="sysprop"/>" onclick="doSaveMember(${result.id}, '${pageContext.request.contextPath}');" style="cursor: hand;"/>
										<img src="${pageContext.request.contextPath}/icons/data_inactive.gif" title="<bean:message key="Cancel"  bundle="sysprop"/>" onclick="doCancelMember(${result.id});" style="cursor: hand;"/>
									</span>
									</c:if>
									<c:if test="${result.errorDesc != null and result.errorDesc != ''}">
									&nbsp;
									</c:if>
								</td>	
							</tr>
							</c:forEach>
		
							<!-- paginator@4 -->
							<c:set var="headerCount" value="9"></c:set>
							<%@include file="/jsp/include/navigator.jsp"%>
							<!-- paginator@4 -->
							</c:if>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<input type="button" id="btnImport" name="btnImport" value="Import" class="newPosBtn" onclick="javascript:memberImport('${pageContext.request.contextPath}')">
									<input type="button" id="btnBack" name="btnBack" value="Back" class="newNegBtn" onclick="javascript:clearForm('${pageContext.request.contextPath}')">
								</td>
							</tr>
						</table>

						<!-- RESULT -->

						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<td align="left" class="footer">&nbsp;</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
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