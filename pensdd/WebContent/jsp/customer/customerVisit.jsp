<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<jsp:useBean id="customervisitForm" class="com.isecinc.pens.web.customervisit.VisitForm" scope="request" />

<%
	String action = (String)request.getParameter("action");
	User user = ((User)session.getAttribute("user"));
	String role = user.getType();
	
	List<References> actives = InitialReferences.getReferenes().get(InitialReferences.ACTIVE);
	pageContext.setAttribute("actives",actives,PageContext.PAGE_SCOPE);
	
	List<References> unclosereasons = InitialReferences.getReferenes().get(InitialReferences.UNCLOSED_REASON);
	pageContext.setAttribute("unclosereasons", unclosereasons, PageContext.PAGE_SCOPE);
	
%>


<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%><html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerVisit.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" language="javascript">
function loadMe(){
	if(document.getElementsByName('visit.salesClose')[0].value=='Y'){
		document.getElementsByName('salesClose')[0].checked = true;
		document.getElementById('div_require').innerHTML = '&nbsp;&nbsp;';
	}else{
		document.getElementsByName('salesClose')[0].checked = false;
		document.getElementById('div_require').innerHTML = '<font color="red">*</font>';
	}

	if(document.getElementsByName('visit.exported')[0].value=='Y'){
		document.getElementsByName('Exported')[0].checked = true;
	}else{
		document.getElementsByName('Exported')[0].checked = false;
	}	

	changeSalesClose();
	changeInterfaces();
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
				<jsp:param name="code" value="${customervisitForm.visit.code}"/>
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
								<td width="30%" align="right"></td>
								<td align="left"></td>
								<td align="right"><bean:message key="Number" bundle="sysele"/><font color="red">*</font></td>
								<td align="left"><html:text property="visit.code" readonly="true" styleClass="disableText"/></td>
							</tr>
							<tr>
								<td align="right"></td>
								<td align="left" width="15%"></td>
								<td align="right" width="10%">
									<bean:message key="Date" bundle="sysele"/>&nbsp;
									<bean:message key="Time" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:text property="visit.visitDate" size="15" readonly="true" styleClass="disableText"/>
									<html:text property="visit.visitTime" size="10" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Trip.CustomerName" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="visit.customerLabel" readonly="true" styleClass="disableText" size="60"/>
								</td>
							</tr>
							<tr>
								<td align="right"></td>
								<td align="left" colspan="3">
									<input type="checkbox" name="salesClose" onclick="changeSalesClose();"/>&nbsp;<bean:message key="ClosedSales" bundle="sysele"/>
									<html:hidden property="visit.salesClose"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="UnCloseReason" bundle="sysele"/><span id="div_require"></span></td>
								<td align="left" colspan="3">
									<html:select property="visit.unClosedReason" style="width: 380px;">
										<html:option value=""></html:option>
										<html:options collection="unclosereasons" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<div align="left">
										&nbsp;&nbsp;<input type="button" value="เพิ่มสินค้า" onclick="open_product('${pageContext.request.contextPath}');"/>
									</div>
								</td>
							</tr>
							<tr>
								<td colspan="4">
									<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
										<tr>
											<th class="order"><bean:message key="No" bundle="sysprop"/></th>
											<th class="checkBox"><input type="checkbox" name="chkAll"
												onclick="checkSelect(this,document.getElementsByName('ids'));" /></th>
											<th><bean:message key="Product.Name" bundle="sysele"/></th>
											<th class="code"><bean:message key="Product.UOM" bundle="sysele"/></th>
											<th class="code">
												<bean:message key="Balance" bundle="sysele"/><br>
												หน้าร้าน/หลังร้าน
											</th>
											<th class="status"><bean:message key="Edit" bundle="sysprop"/></th>
										</tr>
										<c:forEach var="lines1" items="${customervisitForm.lines}" varStatus="rows1">
										<c:choose>
											<c:when test="${rows1.index %2 == 0}">
												<c:set var="tabclass" value="lineO"/>
											</c:when>
											<c:otherwise>
												<c:set var="tabclass" value="lineE"/>
											</c:otherwise>
										</c:choose>
										<tr class="${tabclass}">
											<td>${lines1.lineNo}</td>
											<td align="center"><input type="checkbox" name="ids" value="${lines1.id}" /></td>
											<td align="left">
												${lines1.product.code}&nbsp;${lines1.product.name}
												<input type='hidden' name='prod.id' value='${lines1.id}'/>
												<input type='hidden' name='prod.product.id' value='${lines1.product.id}'/>
												<input type='hidden' name='prod.row' value='${lines1.lineNo}'>
												<input type='hidden' name='prod.product.code' value='${lines1.product.code}'>
												<input type='hidden' name='prod.product.name' value='${lines1.product.name}'>
												<input type='hidden' name='prod.uomId' value='${lines1.uom.id}'>
												<input type='hidden' name='prod.uomLabel' value='${lines1.uom.code}'>
												<input type='hidden' name='uomLabel' value='${lines1.uom.name}'>
												<input type='hidden' name='prod.orderQty' value='${lines1.amount}'>
												<input type='hidden' name='prod.orderQty2' value='${lines1.amount2}'>
											</td>
											<td align="center">${lines1.uom.code}</td>
											<td align="right">
												<fmt:formatNumber pattern="#,##0" value="${lines1.amount}"/>/<fmt:formatNumber pattern="#,##0" value="${lines1.amount2}"/>
											</td>
											<td align="center">
												<a href="#" onclick="open_product('${pageContext.request.contextPath}',${lines1.lineNo});">
												<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>
											</td>
										</tr>
										</c:forEach>
									</table>
									<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
										<tr>
											<td align="left" colspan="12" class="footer">&nbsp;
												<a href="#" onclick="javascript:deleteProduct('${pageContext.request.contextPath}');"> 
												<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif">&nbsp;
												<bean:message key="Delete" bundle="sysprop"/></a>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td ></td>
								<td align="left" width="25%">
									<input type="checkbox" disabled="disabled" name="Exported" onclick="changeInterfaces();">
									<html:hidden property="visit.exported"/>
									<bean:message key="Exported" bundle="sysele"/>
								</td>
								<td align="right" width="10%"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="visit.isActive">
										<html:options collection="actives" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td></td>
								<td align="left">
									<html:checkbox property="visit.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.SalesRepresent" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="visit.user.name" readonly="true" styleClass="disableText" size="30"/>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="#" onclick="return save('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
									<input type="button" value="บันทึก" class="newPosBtn">
									</a>
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}','${customervisitForm.visit.customerId}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<html:hidden property="visit.id"/>
						<html:hidden property="criteria.from"/>
						<html:hidden property="visit.customerId"/>
						<html:hidden property="deletedId"/>
						<html:hidden property="visit.exported" value="N"/>
						<div id="productList" style="text-align: left;display: none;"></div>
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