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

 <%-- <c:if test="${summaryForm.onhandSummaryMonthEndLotusResults != null}">
						<br/>
							 <display:table id="item" name="sessionScope.summaryForm.onhandSummaryMonthEndLotusResults" defaultsort="0" defaultorder="descending" width="100%" class="resultDisp"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="td_text"/>
							    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="td_text"/>	 
							    <display:column  title="Group" property="group"  sortable="false" class="td_text"/>	
							    <display:column  title="Begining Qty" property="beginingQty"  sortable="false" class="td_text"/>	
							    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="td_text"/>	
							    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="td_text"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="td_text"/>	
							    <display:column  title="Adjust QTY " property="adjustQty"  sortable="false" class="td_text"/>	
							    <display:column  title="Stock Short QTY " property="stockShortQty"  sortable="false" class="td_text"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="td_text"/>	
							    				
							</display:table> 
                    </c:if> --%>
                    
</head>
     <c:if test="${summaryForm.page == 'monthEndLotus'}">
		<c:if test="${summaryForm.results != null}">
		<c:if test="${summaryForm.summaryType == 'PensItem'}">
		<br/>
			<display:table id="item" name="sessionScope.summaryForm.results" defaultsort="0"  width="100%" defaultorder="descending" class="resultDisp"
			    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="50">	
			    
			     <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="td_text"/>
			    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="td_text"/>	 
			    <display:column  title="Group" property="group"  sortable="false" class="td_text"/>	
			    <display:column  title="Begining Qty" property="beginingQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="td_text"/>
			    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Adjust QTY " property="adjustQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Stock Short QTY " property="stockShortQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="td_text"/>	
			    				
			</display:table>
			</c:if>
			
			<c:if test="${summaryForm.summaryType == 'GroupCode'}">
		    <br/>
			<display:table id="item" name="sessionScope.summaryForm.results" defaultsort="0"  width="100%" defaultorder="descending" class="resultDisp"
			    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="50">	
			    
			   <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="td_text"/> 
			    <display:column  title="Group" property="group"  sortable="false" class="td_text"/>	
			    <display:column  title="Begining Qty" property="beginingQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="td_text"/>
			    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Adjust QTY " property="adjustQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Stock Short QTY " property="stockShortQty"  sortable="false" class="td_text"/>	
			    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="td_text"/>	
			    				
			</display:table>
			</c:if>
	   </c:if>
 </c:if>	 

</body>
</html>