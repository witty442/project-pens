<%@page import="com.isecinc.pens.bean.OnhandSummary"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<jsp:useBean id="summaryForm" class="com.isecinc.pens.web.summary.SummaryForm" scope="session" />
<%

%>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script>
	function gotoPage(path,pageNumber){
		var form = document.reqPickStockForm;
	   // form.action = path + "/jsp/reqPickStockAction.do?do=search&pageNumber="+pageNumber+"&prevPageNumber="+form.pageNumber.value+"&totalQtyCurPage="+document.getElementsByName("totalQtyCurPage")[0].value;
		form.action = path + "/jsp/reqPickStockAction.do?do=search&pageNumber="+pageNumber;
		
	    form.submit();
	    return true;
	}
</script>
                    
</head>
     <c:if test="${summaryForm.page == 'sizeColorLotus'}">
		<c:if test="${summaryForm.results != null}">
		<c:if test="${summaryForm.summaryType == 'PensItem' || summaryForm.summaryType ==null}">
		<br/>
			<display:table style="width:100%;" id="item" name="sessionScope.summaryForm.results" defaultsort="0" defaultorder="descending" class="resultDisp"
			    requestURI="#" sort="list" pagesize="50">	
			    
			   <display:column  title="รหัสร้านค้า(Bme)" property="storeCode"  sortable="false" />
			    <display:column  title="SubInv" property="subInv"  sortable="false" />
			    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="false"/>
			    <display:column  title="Group" property="group"  sortable="false"/>	
			    <display:column  title="PensItem" property="pensItem"  sortable="false" />
			    <display:column  title="Material Master" property="materialMaster"  sortable="false" />
			    <display:column  title="Barcode" property="barcode"  sortable="false" />
			    <display:column  title="Initial Stock" property="initSaleQty"  sortable="false" />	
			    <display:column  title="Trans In Qty" property="transInQty"  sortable="false" />	
			    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false"/>	
			    <display:column  title="Return Qty" property="saleReturnQty"  sortable="false" />
			    <display:column  title="Adjust Qty" property="adjustSaleQty"  sortable="false" />
			    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" />	
			    				
			</display:table>
			</c:if>
			
			<c:if test="${summaryForm.summaryType == 'GroupCode'}">
		    <br/>
			<display:table style="width:100%;" id="item" name="sessionScope.summaryForm.results" defaultsort="0" defaultorder="descending" class="resultDisp"
			    requestURI="#" sort="list" pagesize="50">	
			    
			   <display:column  title="รหัสร้านค้า(Bme)" property="storeCode"  sortable="false" />
			    <display:column  title="SubInv" property="subInv"  sortable="false" />
			    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="false"/>
			    <display:column  title="Group" property="group"  sortable="false"/>	
			    
			    <display:column  title="Initial Stock" property="initSaleQty"  sortable="false" />	
			    <display:column  title="Trans In Qty" property="transInQty"  sortable="false" />	
			    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false"/>	
			    <display:column  title="Return Qty" property="saleReturnQty"  sortable="false" />
			    <display:column  title="Adjust Qty" property="adjustSaleQty"  sortable="false" />
			    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" />	
			    				
			</display:table>
			</c:if>
	   </c:if>
 </c:if>	 

</body>
</html>