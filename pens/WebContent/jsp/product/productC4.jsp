<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%

String customerId=""; 

if(request.getParameter("type")!=null)
	pageContext.setAttribute("type",request.getParameter("type"),PageContext.PAGE_SCOPE);

if(request.getParameter("customerId")!=null){
	customerId = request.getParameter("customerId");
	pageContext.setAttribute("customerId",request.getParameter("customerId"),PageContext.PAGE_SCOPE);
}

if(request.getParameter("key")!=null){
	pageContext.setAttribute("key",request.getParameter("key"),PageContext.PAGE_SCOPE);
	session.setAttribute("CMSearchKey",request.getParameter("key"));
}

List<ProductC4> pos = new ProductC4Process().getAllItem((User)session.getAttribute("user"),customerId);
pageContext.setAttribute("pos",pos,PageContext.PAGE_SCOPE);



%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.ProductC4"%>
<%@page import="com.isecinc.pens.process.product.ProductC4Process"%>
<%@page import="com.isecinc.pens.bean.User"%><html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/productc4.js"></script>
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
				<jsp:param name="function" value="ProductC4"/>
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
						<jsp:include page="../error.jsp"/>
						<html:form action="/jsp/saleOrderAction">
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th width="250px;"><bean:message key="Product.Name" bundle="sysele"/></th>
								<!-- 
								<th class="costprice"><bean:message key="PricePerUnit" bundle="sysele"/></th>
								 -->
								<th class="costprice"><bean:message key="PricePerPack" bundle="sysele"/></th>
								<th class="status"><bean:message key="Contain" bundle="sysele"/></th>
								<th><bean:message key="Promotion" bundle="sysele"/></th>
								<th><bean:message key="Customer.Territory" bundle="sysele"/></th>
								<c:if test="${customerId!=null}">
									<th class="status">
										<input type="checkbox" name="chkAll" onclick="checkSelect(this,document.getElementsByName('productids'));" />
									</th>
								</c:if>
							</tr>
							<c:forEach var="results" items="${pos}" varStatus="rows">
							<c:choose>
								<c:when test="${rows.index %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<c:if test="${results.descriptionSize==1}">
							<tr class="<c:out value='${tabclass}'/>">
								<td align="left" width="250px;" rowspan="${results.descriptionSize}">${results.product.code} ${results.product.name}</td>
								<!-- 
								<td align="right" class="costprice">
									<fmt:formatNumber pattern="#,##0.00" value="${results.subUnitPrice}"/>
								</td>
								 -->
								<td align="right" class="costprice" rowspan="${results.descriptionSize}">
									<fmt:formatNumber pattern="#,##0.00" value="${results.baseUnitPrice}"/>
								</td>
								<td align="right" class="status" rowspan="${results.descriptionSize}">
									<fmt:formatNumber pattern="#,##0" value="${results.capacity}"/>
								</td>
								<c:if test="${results.listDescription!=null}">
								<!-- One Line Description -->
								<c:forEach var="rr" items="${results.listDescription}" varStatus="rrs">
								<td align="left">${rr.lineDescription}</td>
								<td align="left">${rr.lineTerritory}</td>
								<c:if test="${customerId!=null}">
								<td align="center" class="status">
									<input type="checkbox" name="productids" value="${results.product.id}">
								</td>
								</c:if>
								</c:forEach>
								</c:if>
								<!-- No Line Description -->
								<c:if test="${results.listDescription==null}">
								<td align="left"></td>
								<td align="left">${results.salesChannel}</td>
								<c:if test="${customerId!=null}">
								<td align="center" class="status">
									<input type="checkbox" name="productids" value="${results.product.id}">
								</td>
								</c:if>
								</c:if>
							</tr>
							</c:if>
							<c:if test="${results.descriptionSize!=1}">
							<tr class="<c:out value='${tabclass}'/>">
								<td align="left" width="250px;" rowspan="${results.descriptionSize}">${results.product.code} ${results.product.name}</td>
								<!-- 
								<td align="right" class="costprice">
									<fmt:formatNumber pattern="#,##0.00" value="${results.subUnitPrice}"/>
								</td>
								 -->
								<td align="right" class="costprice" rowspan="${results.descriptionSize}">
									<fmt:formatNumber pattern="#,##0.00" value="${results.baseUnitPrice}"/>
								</td>
								<td align="right" class="status" rowspan="${results.descriptionSize}">
									<fmt:formatNumber pattern="#,##0" value="${results.capacity}"/>
								</td>
								<!-- One Line Description -->
								<c:forEach var="rr" items="${results.listDescription}" varStatus="rrs">
								<c:if test="${rrs.index==0}">
									<td align="left">${rr.lineDescription}</td>
									<td align="left">${rr.lineTerritory}</td>
									<c:if test="${customerId!=null}">
									<td align="center" class="status">
										<input type="checkbox" name="productids" value="${results.product.id}">
									</td>
									</c:if>
								</c:if>
								</c:forEach>
							</tr>
							</c:if>
							<c:forEach var="rr" items="${results.listDescription}" varStatus="rrs">
							<c:if test="${rrs.index!=0}">
							<tr class="<c:out value='${tabclass}'/>">
								<td align="left">${rr.lineDescription}</td>
								<td align="left">${rr.lineTerritory}</td>
								<c:if test="${customerId!=null}">
								<td align="center" class="status">
									<input type="checkbox" name="productids" value="${results.product.id}">
								</td>
								</c:if>
							</tr>
							</c:if>
							</c:forEach>
							</c:forEach>
						</table>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<td align="left" class="footer">&nbsp;</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<c:if test="${customerId!=null}">
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="#" onclick="toOrder('${pageContext.request.contextPath}','${customerId}','${type}');">
									<input type="button" value="ทำรายการขาย" class="newPosBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_salesorder.gif" border="1" class="newPicBtn"> --></a>
									<a href="#" onclick="backToCusotmer('${pageContext.request.contextPath}','${customerId}','${type}');">
									<input type="button" value="ยกเลิก" class="newNegBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn"> --></a>
								</td>
							</tr>
						</table>
						</c:if>
						<c:if test="${customerId==null}">
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"> --></a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						</c:if>
						<input type="hidden" name="custtype" value="${type}"/>
						<input type="hidden" name="custid" value="${customerId}"/>
						<input type="hidden" name="criteria.searchKey" value="${key}"/>
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