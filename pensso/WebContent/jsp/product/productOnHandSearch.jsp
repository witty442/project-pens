<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
User user = (User) session.getAttribute("user");

List<ProductCategory> cates = new MProductCategory().lookUp();
pageContext.setAttribute("cates",cates,PageContext.PAGE_SCOPE);

List<SubInventory> subinvs = new MSubInventory().lookUpVAN(user.getCode());

List<SalesInventory> salesinvs = new MSalesInventory().lookUp(user.getId());
List<SubInventory> uses = new ArrayList<SubInventory>();
for(SubInventory ss : subinvs){
	uses.add(ss);
}
for(SalesInventory s : salesinvs){
	uses.add(new MSubInventory().find(String.valueOf(s.getSubInventoryId())));
}
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="inventoryForm" class="com.isecinc.pens.web.inventory.InventoryForm" scope="request" />

<%@page import="com.isecinc.pens.bean.ProductCategory"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%>
<%@page import="com.isecinc.pens.bean.SubInventory"%>
<%@page import="com.isecinc.pens.model.MSubInventory"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.bean.SalesInventory"%>
<%@page import="com.isecinc.pens.model.MSalesInventory"%>
<%@page import="java.util.ArrayList"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inventory.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script language="javascript">
//call ajax
function loadProduct(e){
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/productQuery.jsp",
				data : "pCode=" + $('#pCode').val(),
				success: function(getData){
					var returnString = jQuery.trim(getData);
					$('#productId').val(returnString.split('||')[0]);
					$('#pName').val(returnString.split('||')[1] + ' ' + returnString.split('||')[2]);
				}
			}).responseText;
		});
	}
}

function showProduct(path){
	window.open(path + "/jsp/pop/view/productViewPopup.jsp", "Product List", "width=500,height=350,location=No,resizable=No");
}
function setProduct(code, name){
	$('#pCode').val(Trim(code));
	$('#pName').val(Trim(name));
	loadProduct(null);
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
				<jsp:param name="function" value="InventoryOnhand"/>
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
						<html:form action="/jsp/inventoryAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right" width="40%"><bean:message key="User.SubInv"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="inventory.subInventory.id">
										<html:option value=""></html:option>
										<%for(SubInventory s : uses){ %>
										<html:option value="<%=String.valueOf(s.getId()) %>"><%=s.getName()%>-<%=s.getDescription()%></html:option>
										<%} %>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Product.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="inventory.product.code" onkeyup="loadProduct(event);" styleId="pCode"/>
									<a href="#" onclick="showProduct('${pageContext.request.contextPath}');" id="lookProduct">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
									<html:hidden property="inventory.product.id" styleId="productId"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Product.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="inventory.product.name" styleId="pName" size="45"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Product.Type" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="inventory.product.productCategory.id">
										<html:option value=""></html:option>
										<html:options collection="cates" property="id" labelProperty="name"/>
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
									<!-- 
									<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn"> --></a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<input type="button" value="Clear" class="newNegBtn">
									<!-- 
									<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn"> --></a>
								</td>
							</tr>
						</table>
						<!-- RESULT -->
						<c:if test="${inventoryForm.results != null}">
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
						&nbsp;<span class="searchResult">${inventoryForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
						<table align="center" border="0" cellpadding="3" cellspacing="1"
							class="result">
							<tr>
								<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
								<th class="name"><bean:message key="User.SubInv"  bundle="sysele"/></th>
								<th><bean:message key="Product"  bundle="sysele"/></th>
								<th><bean:message key="Product.Type"  bundle="sysele"/></th>
								<th class="code">Stock ตั้งต้น</th>
								<th class="code"><bean:message key="Sold" bundle="sysele" /></th>
								<th class="code"><bean:message key="Product.Balance"  bundle="sysele"/></th>
								<!-- <th class="status"><bean:message key="Product.UOM"  bundle="sysele"/></th> -->
							</tr>
							<c:forEach var="results" items="${inventoryForm.results}" varStatus="rows">
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
								<td align="left">${results.subInventory.name}-${results.subInventory.description}</td>
								<td align="left">${results.product.code} ${results.product.name}</td>
								<td align="left">${results.product.productCategory.name}</td>
								<td align="right">
									<fmt:formatNumber pattern="#,##0" value="${results.availableQty1}"/>/<fmt:formatNumber pattern="#,##0" value="${results.availableQty2}"/>&nbsp;&nbsp;
								</td>
								<td align="right">
									<fmt:formatNumber pattern="#,##0" value="${results.salesQty1}"/>/<fmt:formatNumber pattern="#,##0" value="${results.salesQty2}"/>&nbsp;&nbsp;
								</td>
								<td align="right">
									<fmt:formatNumber pattern="#,##0" value="${results.remainQty1}"/>/<fmt:formatNumber pattern="#,##0" value="${results.remainQty2}"/>&nbsp;&nbsp;
								</td>
								<!-- 
								<td align="center">
									${results.uom1.code}/${results.uom2.code}
								</td>
								 -->
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