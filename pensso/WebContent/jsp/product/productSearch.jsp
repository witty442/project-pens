<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
List<ProductCategory> cats = new MProductCategory().lookUp(ProductCategory.SEGMENT1,0);
pageContext.setAttribute("cats",cats,PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="productForm" class="com.isecinc.pens.web.product.ProductForm" scope="request" />

<%@page import="com.isecinc.pens.bean.ProductCategory"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%><html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/product.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">

function loadMe(){
	if($('#seg1').val()!='' && $('#seg1').val()!=null){
		getCatSegment(2, $('#seg1').val(), '#seg2');
		$('#seg2').val('${productForm.product.productCategory.segId2}');	
	}
	if($('#seg2').val()!='' && $('#seg2').val()!=null){
		getCatSegment(3, $('#seg2').val(), '#seg3');
		$('#seg3').val('${productForm.product.productCategory.segId3}');	
	}
	if($('#seg3').val()!='' && $('#seg3').val()!=null){
		getCatSegment(4, $('#seg3').val(), '#seg4');
		$('#seg4').val('${productForm.product.productCategory.segId4}');	
	}
	if($('#seg4').val()!='' && $('#seg4').val()!=null){
		getCatSegment(5, $('#seg4').val(), '#seg5');
		$('#seg5').val('${productForm.product.productCategory.segId5}');	
	}
}

function getCatSegment(s,psid,cats){
	if(s==2){
		$('#seg3').html("");
		$('#seg4').html("");
		$('#seg5').html("");
	}
	if(s==3){
		$('#seg4').html("");
		$('#seg5').html("");
	}
	if(s==4){
		$('#seg5').html("");
	}
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/CatSegmentQuery.jsp",
			data : "segment=" + s
			+ "&psegmentId=" + psid,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				$(cats).html("");
				$(returnString).appendTo(cats);
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
				<jsp:param name="function" value="Product"/>
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
						<html:form action="/jsp/productAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="45%" align="right"><bean:message key="Product.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="product.code"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Product.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="product.name" size="40"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Product.Type" bundle="sysele"/>1&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="product.productCategory.segId1" styleId="seg1" onchange="getCatSegment(2,this.value,'#seg2');">
										<html:option value=""></html:option>
										<html:options collection="cats" property="id" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Product.Type" bundle="sysele"/>2&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="product.productCategory.segId2" styleId="seg2" onchange="getCatSegment(3,this.value,'#seg3');">
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Product.Type" bundle="sysele"/>3&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="product.productCategory.segId3" styleId="seg3" onchange="getCatSegment(4,this.value,'#seg4');">
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Product.Type" bundle="sysele"/>4&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="product.productCategory.segId4" styleId="seg4" onchange="getCatSegment(5,this.value,'#seg5');">
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Product.Type" bundle="sysele"/>5&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="product.productCategory.segId5" styleId="seg5">
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
						<c:if test="${productForm.results != null}">
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>&nbsp;
						<span class="searchResult">${productForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
								<th class="code"><bean:message key="Product.Code"  bundle="sysele"/></th>
								<th class="name"><bean:message key="Product.Name"  bundle="sysele"/></th>
								<th class="th"><bean:message key="Product.Type"  bundle="sysele"/></th>
								<th class="code"><bean:message key="Product.UOM"  bundle="sysele"/></th>
								<th class="status"><bean:message key="Product.Price"  bundle="sysele"/></th>
							</tr>	
							<c:forEach var="results" items="${productForm.results}" varStatus="rows">
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
								<td align="center" width="110px;">${results.code}</td>
								<td align="left" width="180px;">${results.name}</td>
								<td align="left" width="297px;">${results.productCategory.name}</td>
								<td align="center" width="110px;">${results.uom.name}</td>
								<td align="center">
									<a href="#" onclick="javascript:viewPrice('${pageContext.request.contextPath}',${results.id});">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
								</td>
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
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
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