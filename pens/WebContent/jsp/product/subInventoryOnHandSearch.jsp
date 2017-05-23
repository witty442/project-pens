<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%

%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="inventoryForm" class="com.isecinc.pens.web.inventory.InventoryForm" scope="request" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inventory.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
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
	$('#pCode').val(code);
	$('#pName').val(name);
	loadProduct(null);
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="939" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr style="height: 137px;">
	    <td background="${pageContext.request.contextPath}/images2/page1_topbar.png" valign="top">
	    	<!-- HEADER -->
	    	<jsp:include page="../header.jsp"/>
	    </td>
  	</tr>
  	<tr id="framerow">
    	<td background="${pageContext.request.contextPath}/images2/page1_bgcontent.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="900" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="SubInventoryOnhand"/>
				<jsp:param name="code" value="${user.subInventory.name}"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="900" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="9"><img src="${pageContext.request.contextPath}/images2/boxcont1_1.gif" width="9" height="9" /></td>
		            <td width="832"><img src="${pageContext.request.contextPath}/images2/boxcont1_5.gif" width="100%" height="9" /></td>
		            <td width="9"><img src="${pageContext.request.contextPath}/images2/boxcont1_2.gif" width="9" height="9" /></td>
	      		</tr>
	      		<tr>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"><img src="${pageContext.request.contextPath}/images2/boxcont1_8.gif" width="9" height="1" /></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/inventoryAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td width="40%" align="right"><bean:message key="Product.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
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
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="javascript:searchSub('${pageContext.request.contextPath}')">
									<!--<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ค้นหา" class="newPosBtn">
									</a>
									<a href="javascript:clearFormSub('${pageContext.request.contextPath}')">
									<!--<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">-->
									<input type="button" value="Clear" class="newNegBtn">
									</a>
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
								<th class="code"><bean:message key="Product.Code"  bundle="sysele"/></th>
								<th><bean:message key="Product.Name"  bundle="sysele"/></th>
								<th class="status"><bean:message key="Product.Balance"  bundle="sysele"/></th>
								<th class="status"><bean:message key="Product.UOM"  bundle="sysele"/></th>
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
								<td align="center">${results.product.code}</td>
								<td align="left">${results.product.name}</td>
								<td align="right"><fmt:formatNumber pattern="#,#00" value="${results.availableQty}"/>&nbsp;&nbsp;</td>
								<td align="center">${results.uom.name}</td>
							</tr>
							</c:forEach>
						</table>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="result">	
							<tr>
								<td align="left" colspan="10" class="footer">&nbsp;</td>
							</tr>
						</table>
						<br>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
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
						<br><br>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						</html:form>
						<!-- BODY -->
					</td>
					<td background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"><img src="${pageContext.request.contextPath}/images2/boxcont1_8.gif" width="9" height="1" /></td>
				</tr>
				<tr style="height: 9px;">
		            <td><img src="${pageContext.request.contextPath}/images2/boxcont1_4.gif" width="9" height="9" /></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"><img src="${pageContext.request.contextPath}/images2/boxcont1_7.gif" width="1" height="9" /></td>
		            <td><img src="${pageContext.request.contextPath}/images2/boxcont1_3.gif" width="9" height="9" /></td>
	          	</tr>
    		</table>
    	</td>
    </tr>
    <tr>
    	<td background="${pageContext.request.contextPath}/images2/page1_bgcontent.png" valign="top">
   			<!-- FOOTER -->
    		<table width="900" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
    			<tr>
	        		<td colspan="2"><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="15" /></td>
	      		</tr>
        		<tr>
        			<td align="left"><img src="${pageContext.request.contextPath}/images2/footer.jpg" width="454" height="38" /></td>
        			<td align="right"><a href="#top"><img src="${pageContext.request.contextPath}/images2/but_top.gif" width="59" height="18" border="0" /></a></td>
        		</tr>
        	</table>
        </td>
    </tr>
    <tr style="height: 62px;">
    	<td valign="top">
    		<img src="${pageContext.request.contextPath}/images2/page1_footer.png" width="939" height="62" />
    	</td>
  	</tr>
</table>
</body>
</html>