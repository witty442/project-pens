<%@page import="com.isecinc.pens.web.interfaces.InterfacesCriteria"%>
<%@page import="com.isecinc.pens.inf.dao.InterfaceDAO"%>
<%@page import="com.isecinc.pens.inf.bean.MonitorBean"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.ImportHelper"%>
<%@page import="com.isecinc.pens.inf.helper.ExportHelper"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />
<%-- <jsp:useBean id="userForm" class="com.isecinc.pens.web.user.UserForm" scope="request" /> --%>
<%
User user = (User) session.getAttribute("user");
String role = ((User)session.getAttribute("user")).getType();

InterfaceDAO dao = new InterfaceDAO(); 
BigDecimal transactionId = new BigDecimal(request.getParameter("transactionId"));
MonitorBean[] resultsHead = dao.findMonitorList(transactionId);

/*if (results != null && results.length > 0) {
	pageContext.setAttribute("resultsHead",results,PageContext.PAGE_SCOPE);
} */

/** Set Condition Search **/
MonitorBean[] results = dao.findMonitorDetailList(user,transactionId,"");
System.out.println("results:"+results.length);
if (results != null && results.length > 0) {
	pageContext.setAttribute("results",results,PageContext.PAGE_SCOPE);
} 

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/interfaces.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>

<!-- Calendar -->
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}

</style>

</head>

<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;"  style="height: 100%;">

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
		
	   <!-- RESULT -->
	   <c:if test="${results != null}">
			
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" width="90%">
							<tr>
			                <th> ลำดับที่.</th>
			                <th> เลขที่รายการ</th>
							<th> ประเภท</th>
							<th> ประเภทข้อมูล </th>
							<th> ชื่อ รายการ </th>
							<th> ชื่อ ไฟล์</th>
							<th> ขนาด ไฟล์</th>
							<th> ต้นทาง</th>
							<th> ปลายทาง</th>
							<th> ผู้สร้าง</th>
							<th> สถานะ</th>
							<th> วันที่สร้างรายการ</th>
							<th> จำวนรายการ</th>
							<th> รายละเอียดเพิ่มเติม</th>
							<th> Message</th>
							</tr>
							
							<c:forEach var="results" items="${results}" varStatus="rows">
							<c:choose>
								<c:when test="${rows.index %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							
							<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}">
					                <td> <c:out value='${rows.index+1}'/></td>
					                <td>  ${results.transactionId}</td>
									<td>  ${results.type}</td>
									<td>ข้อมูล Transaction</td>
									<td align="left"> ${results.monitorItemBean.tableName}</td>
									<td align="left">
									    <a href ="javascript:getTextFile('${pageContext.request.contextPath}','${results.transactionType}','${results.monitorItemBean.fileName}','${results.monitorItemBean.status}','${results.type}')">
									    ${results.monitorItemBean.fileName}  </a>
									</td>
									<td align="left"> ${results.monitorItemBean.fileSize}</td>
									<td> ${results.monitorItemBean.source}</td>
									<td> ${results.monitorItemBean.destination}</td>
									<td> ${results.createUser}</td>
									<td> 
									  <c:choose>
										<c:when test="${results.monitorItemBean.status == 1}">
											<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
										</c:when>
										<c:otherwise>
											<img border=0 src="${pageContext.request.contextPath}/icons/uncheck.gif">
										</c:otherwise>
									   </c:choose>
									</td>
									<td> ${results.submitDate}</td>
									<td>
										<c:choose>
											<c:when test="${results.type == 'IMPORT'}">
												${results.monitorItemBean.successCount}/${results.monitorItemBean.dataCount}
											</c:when>
											<c:otherwise>
												${results.monitorItemBean.dataCount}
											</c:otherwise>
										</c:choose>
									</td>
									<td>  
									   <c:choose>
											<c:when test="${results.type == 'EXPORT'}">
												<a href ="javascript:showItemExport('${pageContext.request.contextPath}','${results.monitorItemBean.id}','${results.monitorItemBean.tableName}')">
									             <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
											</c:when>
											<c:otherwise>
											</c:otherwise>
										</c:choose>
									</td>
									<td align="left"> 
									    <c:choose>
											<c:when test="${results.monitorItemBean.errorMsg == ''}">
												 <a href ="javascript:getLog('${pageContext.request.contextPath}','${results.transactionType}','${results.monitorItemBean.fileName}')">
												 Logs
												 </a>
											</c:when>
											<c:otherwise>
											   <a href ="javascript:getLog('${pageContext.request.contextPath}','${results.transactionType}','${results.monitorItemBean.fileName}')">
											   ${results.monitorItemBean.errorMsg} Logs
											   </a>
											</c:otherwise>
										</c:choose>
									</td>
							</tr>
							</c:forEach>
						</table>
		</c:if>

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
    	
</body>
</html>
