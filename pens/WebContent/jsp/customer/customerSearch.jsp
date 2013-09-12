<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>

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
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
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
								<input type="button" value="ค้นหา" class="newPosBtn">
								<!-- <img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn"> --></a>
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								<input type="button" value="Clear" class="newNegBtn">
								<!-- <img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn"> --></a>
							</td>
						</tr>
					</table>				
                    
					<!-- RESULT -->
					
					<display:table id="item" name="requestScope.customerForm.results" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="../jsp/customerAction.do?do=search" sort="list" pagesize="50">	
							    
					            <display:column  title="No" property="no"  sortable="false" class="cust_no" media=""/>
							    <display:column  title="หมายเลขลูกค้า" property="code"  sortable="false" class="cust_code"/>
							    <display:column  title="ชื่อ" property="name"  sortable="false" class="cust_name"/>	
							    <display:column  title="ชื่อรอง" property="name2"  sortable="false" class="cust_name2"/>	
							    <display:column  title="วงเงินสินเชื่อ" sortable="false" class="cust_creditLimit">
							        <fmt:formatNumber pattern="#,##0.00" value="${item.creditLimit}"/>
							    </display:column>	
							    <display:column  title="ยอดบิลค้างชำระ " sortable="false" class="cust_totalInvoice">
							        <fmt:formatNumber pattern="#,##0.00" value="${item.totalInvoice}"/>
							    </display:column>		
							    	
							    <display:column  title="โอนข้อมูลแล้ว" sortable="false" class="cust_exported" >	
								    <c:if test="${item.exported=='Y'}">
										<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									</c:if>
							    </display:column>
							    
							    <display:column  title="สร้างข้อมูลที่ระบบกลางแล้ว"  sortable="false" class="cust_interfaces">
								    <c:if test="${item.interfaces=='Y'}">
										<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									</c:if>
							    </display:column>
							    <display:column  title="สถานะ" property ="activeLabel" sortable="false" class="cust_status"/>
							    
							    <display:column  title="ทำรายการขาย"  sortable="false" class="cust_actionOrder">
							    	 <a href="#" onclick="toCreateNewOrder('${pageContext.request.contextPath}','add',${item.id})">
							           <img src="${pageContext.request.contextPath}/images2/b_order.png" width="32" height="32" border="0" class="newPicBtn">
							        </a> 
							    </display:column>
							    <display:column  title="ทำรายการรับเงิน" sortable="false" class="cust_actionReceipt" media="${item.displayActionReceipt}">
							       <a href="#" onclick="toCreateNewReceipt('${pageContext.request.contextPath}','add','${item.id}');">
								         <img src="${pageContext.request.contextPath}/images2/b_receipt.jpg" width="32" height="32" border="0" class="newPicBtn"/>
								    </a>
							    </display:column>	
							    <display:column  title="แก้ไข ข้อมูลลูกค้า" sortable="false" class="cust_actionEditCust" media="${item.displayActionEditCust}">
							       <c:if test="${item.canActionEditCust=='true'}">
							          <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit','${item.id}');">
									     <img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif">
									  </a>
									</c:if>
							    </display:column>		
							    
							    <display:column  title="แสดง" sortable="false" class="cust_actionView">
							        <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','${item.id}');">
									   <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif">
									</a>
							    </display:column>	
							    <display:column  title="ทำรายการ"  sortable="false" class="cust_actionEdit">
							       <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','process','${item.id}');">
									  <img border=0 src="${pageContext.request.contextPath}/icons/process.gif">
								   </a>
							    </display:column>		
					</display:table>
					
				<!-- Result -->	
				
				
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						<tr>
							<td align="right">
								<a href="#" onclick="window.location='./mainpage.jsp'">
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