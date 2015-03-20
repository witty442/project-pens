<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%

%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<jsp:useBean id="pricelistForm" class="com.isecinc.pens.web.pricelist.PriceListForm" scope="request" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pricelist.js"></script>
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
				<jsp:param name="function" value="PriceList"/>
				<jsp:param name="code" value="${pricelistForm.priceList.name}"/>
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
						<html:form action="/jsp/pricelistAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="32%"></td>
								<td width="15%"></td>
								<td width="15%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="PriceList.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="priceList.name" readonly="true" styleClass="disableText"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="PriceList.EffectiveDate" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="priceList.effectiveDate" readonly="true" styleClass="disableText" size="15"/>
								</td>
								<td align="right"><bean:message key="PriceList.EffectiveToDate" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="priceList.effectiveToDate" readonly="true" styleClass="disableText" size="15"/>
								</td>
							</tr>
						</table>
						<br/><br/>
						<!-- RESULT -->
						<c:if test="${pricelistForm.priceList.productPrices != null}">
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
								<th class="code"><bean:message key="Product.Code"  bundle="sysele"/></th>
								<th><bean:message key="Product.Name"  bundle="sysele"/></th>
								<th class="name"><bean:message key="Product.UOM"  bundle="sysele"/></th>
								<th class="code"><bean:message key="Product.Price"  bundle="sysele"/></th>
								<th class="status"><bean:message key="Status"  bundle="sysele"/></th>
							</tr>	
						<c:forEach var="results" items="${pricelistForm.priceList.productPrices}" varStatus="rows">
						<c:choose>
							<c:when test="${rows.index %2 == 0}">
								<c:set var="tabclass" value="lineO"/>
							</c:when>
							<c:otherwise>
								<c:set var="tabclass" value="lineE"/>
							</c:otherwise>
						</c:choose>	
						<tr class="<c:out value='${tabclass}'/>">
							<td width="40px;"><c:out value='${rows.index+1}'/></td>
							<td align="left" width="110px;">${results.product.code}</td>
							<td align="left" width=297px;">${results.product.name}</td>
							<td align="center" width="180px;">${results.uom.name}</td>
							<td align="right" width="110px;">
								<fmt:formatNumber pattern="#,#00.00" value="${results.price}"/>
							</td>
							<td align="center">${results.activeLabel}</td>
						</tr>
						</c:forEach>
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
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}')">
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"> --></a>
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