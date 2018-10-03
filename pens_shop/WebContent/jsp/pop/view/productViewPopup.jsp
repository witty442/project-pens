<!--
Product Popup for Sales Order 
 -->
<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.UOM"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.model.MUOM"%>
<%
  String pBrand = request.getParameter("pBrand");
  if(pBrand!=null)
  	pBrand = new String(pBrand.getBytes("ISO8859_1"), "UTF-8");
  else
	  pBrand = "";
  System.out.println("Product Popup pBrand="+pBrand);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
function loadMe(){
	searchProduct(null,'<%=pBrand%>');
}
function searchProduct(e,pBrand){

	//alert("kkk"+pBrand);
	
	if(pBrand = 'undefined'){
		//alert("xxx-");
		pBrand = ''
	}
	
	var pBrandText = $('#pBrand').val();
	if(pBrandText !='' && pBrandText != 'undefined'){
	   pBrand = pBrandText;
	}
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/view/productViewQuery.jsp",
				data : "pName=" + encodeURIComponent($('#productName').val())+"&pBrand=" + encodeURIComponent(pBrand),
				success: function(getData){
					var returnString = jQuery.trim(getData);
					document.getElementById('results').innerHTML = returnString;
				}
			}).responseText;
		});
	}
	
}

function selectProduct(code, name){
	window.opener.setProduct(code, name);
	window.close();
}
</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<input type ="hidden" name ="pBrand" id="pBrand" value ="<%=pBrand %>"/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="35%">&nbsp;</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td align="right">แบรนด์&nbsp;</td>
		<td align="left" width="50%">
			<%=pBrand %> 
		</td>
		<td align="left"></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Product.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left" width="30%">
			<input type="text" id="productName" name="productName" size="15" onkeypress="searchProduct(event)"/>
		</td>
		<td align="left">
			<input type="button" value="Search" class="newNegBtn" onclick="searchProduct(null);"/>
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<div id="results"></div>
		</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td colspan="2">&nbsp;</td>
		<td align="left">
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
			</a>
		</td>
	</tr>
</table>
<br/>
</body>
</html>