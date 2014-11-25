<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<jsp:useBean id="requisitionProductForm" class="com.isecinc.pens.web.requisitionProduct.RequisitionProductForm" scope="request" />
<%
String role = ((User)session.getAttribute("user")).getType();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/requisitionProduct.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('requestDateFrom'));
	new Epoch('epoch_popup', 'th', document.getElementById('requestDateTo'));
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
				<jsp:param name="function" value="RequisitionProduct"/>
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
						<html:form action="/jsp/requisitionProductAction">
						<jsp:include page="../error.jsp"/>

						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td colspan="2" align="left">
									 <a href="javascript:createNewRequisitionProduct('${pageContext.request.contextPath}')">
								       <img border=0 src="${pageContext.request.contextPath}/icons/user_add.gif" align="absmiddle">สร้างรายการใหม่
								      </a>
								</td>
							</tr>
							<tr>
								<td align="right">จาก วันทีรายการ&nbsp;&nbsp;<html:text property="requisitionProduct.requestDateFrom" styleId="requestDateFrom" readonly="true"/></td>
								<td align="left">ถึง วันที่รายการ&nbsp;&nbsp;  <html:text property="requisitionProduct.requestDateTo" styleId="requestDateTo" readonly="true"/></td>
							</tr>
							<tr>
							    <td align="right">&nbsp;&nbsp;</td>
								<td align="right">&nbsp;&nbsp;</td>
							</tr>
					   </table>
					   
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
								</a>
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="     Clear     " class="newPosBtnLong">
								</a>
							</td>
						</tr>
					</table>
					<!-- RESULT -->
				    
			        <c:if test="${requisitionProductForm.results != null}">
						<display:table id="item" name="requestScope.requisitionProductForm.results" defaultsort="0" defaultorder="descending" class="resultDisp"
						    requestURI="../jsp/requisitionProductAction.do?do=search" sort="list" pagesize="30">	
			
						    <display:column  title="ลำดับที่" property="no"  sortable="false" class="moverOrder.No"/>	
						   	<display:column  title="เลขที่เอกสาร" property="requestNumber"  sortable="false" class="moverOrder.requestNumber"/>	
						   	<display:column  title="วันที่ทำรายการ" property="requestDate"  sortable="false" class="moverOrder.requestDate"/>	
						   	<display:column title ="โอนข้อมูลแล้ว">
						   	     <c:choose>
									<c:when test="${item.exported == 'Y'}">
										<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									</c:when>
									<c:otherwise>
										
									</c:otherwise>
								  </c:choose>	
						   	</display:column>
						   	
						   	<display:column  title="สถานะ" property="statusLabel"  sortable="false" class="moverOrder.status"/>	
						   	<display:column title="แก้ไข/ดู ข้อมูล">
				
						       	<c:if test="${item.canEdit =='true'}">
							   	   
									  <img src="${pageContext.request.contextPath}/icons/process.gif" border="0" onclick="editRequisitionProduct('${pageContext.request.contextPath}','${item.requestNumber}');"/>
									
								</c:if>
								<c:if test="${item.canEdit =='false'}">
							   	   
									  <img src="${pageContext.request.contextPath}/icons/lookup.gif"  border="0" onclick="editRequisitionProduct('${pageContext.request.contextPath}','${item.requestNumber}');"/>
									
								</c:if>
							
						   	</display:column>	
						   		
						</display:table>
                    </c:if>
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