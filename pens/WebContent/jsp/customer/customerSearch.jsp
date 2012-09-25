<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="customerForm" class="com.isecinc.pens.web.customer.CustomerForm" scope="request" />
<%
String role = ((User)session.getAttribute("user")).getType();

List<References> territorys = InitialReferences.getReferenes().get(InitialReferences.TERRITORY);
pageContext.setAttribute("territorys", territorys, PageContext.PAGE_SCOPE);

List<References> actives= InitialReferences.getReferenes().get(InitialReferences.ACTIVE);
pageContext.setAttribute("actives",actives,PageContext.PAGE_SCOPE);


%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%><html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customer.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerTransaction.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">

function loadMe(){
	loadProvince();
	document.getElementsByName('customer.searchProvince')[0].value = ${customerForm.customer.searchProvince};
}

function loadProvince(){
	var cboProvince = document.getElementsByName('customer.searchProvince')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/ProvinceTerritory.jsp",
			data : "refId=" + document.getElementsByName('customer.territory')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboProvince.innerHTML=returnString;
			}
		}).responseText;
	});
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
				<jsp:param name="function" value="CustomerInfo"/>
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
						<html:form action="/jsp/customerAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						<tr>
							<td colspan="2" align="left">
								<%if(role.equalsIgnoreCase(User.VAN)){ %>
								<a href="#" onclick="prepare('${pageContext.request.contextPath}','add')">
								<img border=0 src="${pageContext.request.contextPath}/icons/user_add.gif" align="absmiddle">&nbsp;<bean:message key="CreateNewRecord" bundle="sysprop"/></a>
								<%} %>
							</td>
						</tr>
						<tr>
							<td width="45%" align="right"><bean:message key="Customer.Territory" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left">
								<html:select property="customer.territory" onchange="loadProvince();">
									<html:option value=""></html:option>
									<html:options collection="territorys" property="key" labelProperty="name"/>
								</html:select>
							</td>
						</tr>
						<tr>
							<td align="right"><bean:message key="Address.Province" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left">
								<html:select property="customer.searchProvince">
								</html:select>
							</td>
						</tr>
						<tr>
							<td align="right"><bean:message key="Customer.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left"><html:text property="customer.code"/></td>
						</tr>
						<tr>
							<td align="right"><bean:message key="Customer.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left"><html:text property="customer.name"/></td>
						</tr>
						<tr>
							<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left">
								<html:select property="customer.isActive">
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
								<input type="button" value="����" class="newPosBtn">
								<!-- <img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn"> --></a>
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								<input type="button" value="Clear" class="newNegBtn">
								<!-- <img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn"> --></a>
							</td>
						</tr>
					</table>
					<!-- RESULT -->
					<c:if test="${customerForm.results != null}">
					<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>&nbsp;
					<span class="searchResult">${customerForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
					<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
						<tr>
							<th width="20px;"><bean:message key="No" bundle="sysprop"/></th>
							<th width="50px;"><bean:message key="Customer.Code" bundle="sysele"/></th>
							<th width="70px;"><bean:message key="Customer.Name" bundle="sysele"/></th>
							<th width="70px;"><bean:message key="Customer.SubName" bundle="sysele"/></th>
							<th class="costprice"><bean:message key="Customer.CreditLimit" bundle="sysele"/></th>
							<th class="costprice"><bean:message key="Customer.TotalInvoice" bundle="sysele"/></th>
							<%if(role.equalsIgnoreCase(User.VAN)) {%>
							<th width="40px;"><bean:message key="Exported" bundle="sysele"/></th>
							<th width="40px;"><bean:message key="Interfaces" bundle="sysele"/></th>
							<%} %>
							<th width="40px;"><bean:message key="Status" bundle="sysele"/></th>
						    <th width="40px;">����¡�â��</th>
							<%if( role.equalsIgnoreCase(User.TT)){ %>
							    <th width="40px;">����¡���Ѻ�Թ</th>
							<% } %>
							<%if(role.equalsIgnoreCase(User.ADMIN) || role.equalsIgnoreCase(User.VAN)){ %>
							<th width="40px;">���<br/>�������١���</th>
							<%} %>
                           <%if( !role.equalsIgnoreCase(User.ADMIN)){ %>
							<th width="40px;"><bean:message key="View" bundle="sysprop"/></th>
							<th width="40px;"><bean:message key="Transaction" bundle="sysprop"/></th>
							<%} %>
						</tr>
					<c:forEach var="results" items="${customerForm.results}" varStatus="rows">
						<c:choose>
							<c:when test="${rows.index %2 == 0}">
								<c:set var="tabclass" value="lineO"/>
							</c:when>
							<c:otherwise>
								<c:set var="tabclass" value="lineE"/>
							</c:otherwise>
						</c:choose>
						<tr class="<c:out value='${tabclass}'/>">
							<td ><c:out value='${rows.index+1}'/></td>
							<td align="left" >${results.code}</td>
							<td align="left" >${results.name}</td>
							<td align="left" >${results.name2}</td>
							<td align="right" >
								<fmt:formatNumber pattern="#,##0.00" value="${results.creditLimit}"/>
							</td>
							<td align="right" >
								<fmt:formatNumber pattern="#,##0.00" value="${results.totalInvoice}"/>
							</td>
							<%if(role.equalsIgnoreCase(User.VAN)) {%>
							<td align="center">
								<c:if test="${results.exported=='Y'}">
									<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
								</c:if>
							</td>
							<td align="center">
								<c:if test="${results.interfaces=='Y'}">
									<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
								</c:if>
							</td>
							<%} %>
							<td align="center">${results.activeLabel}</td>

							<td align="center">
							     <a href="#" onclick="toCreateNewOrder('${pageContext.request.contextPath}','add',${results.id})">
							         <img src="${pageContext.request.contextPath}/images2/b_order.png" width="32" height="32" border="0" class="newPicBtn">
							     </a>
							   
							</td>
							<%if( role.equalsIgnoreCase(User.TT)){ %>
								<td align="center">
								      <a href="#" onclick="toCreateNewReceipt('${pageContext.request.contextPath}','add','${results.id}');">
								         <img src="${pageContext.request.contextPath}/images2/b_receipt.jpg" width="32" height="32" border="0" class="newPicBtn"/>
								      </a>
								</td>
							<% } %>
							
							<!-- OLD CODE************** -->
<!--							<%//if(role.equalsIgnoreCase(User.VAN)){ %>-->
<!--							<td align="center" width="50px;">-->
<!--								<c:if test="${results.orderAmount==0}">-->
<!--									<c:if test="${results.exported!='Y'}">-->
<!--										<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit','${results.id}');">-->
<!--										<img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif"></a>-->
<!--									</c:if>-->
<!--								</c:if>-->
<!--							</td>-->
<!--							<%//} %>-->

                        <!-- WIT EDIT:04/08/2554***************** -->
                           <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
								<td>
									<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit','${results.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif"></a>
								</td>
							<%} %>
						    <%if(role.equalsIgnoreCase(User.VAN)){ %>
								<td align="center" width="50px;">
									<c:if test="${results.orderAmount == 0}">
									   <c:if test="${results.interfaces != 'Y'}">
											<c:if test="${results.exported != 'Y'}">
												<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit','${results.id}');">
												<img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif"></a>
											</c:if>
									   </c:if>
									</c:if>
								</td>
							<%} %>
					   <!-- WIT EDIT:04/08/2554***************** -->
						   <%if( !role.equalsIgnoreCase(User.ADMIN)){ %>
								<td align="center" width="48px;">
									<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','${results.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
								</td>
								<td align="center">
									<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','process','${results.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/process.gif"></a>
								</td>
							<%} %>
						</tr>
					</c:forEach>
					<tr>
						<td align="left" class="footer" colspan="12">&nbsp;</td>
					</tr>
				</table>
					</c:if>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						<tr>
							<td align="right">
								<a href="#" onclick="window.location='./mainpage.jsp'">
								<input type="button" value="�Դ˹�Ҩ�" class="newNegBtn">
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