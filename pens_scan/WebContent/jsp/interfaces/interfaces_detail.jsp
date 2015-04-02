<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/interfaces.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload=" MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="Interfaces"/>
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
						<html:form action="/jsp/interfacesAction">
						<jsp:include page="../error.jsp"/>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="right">
									  <input type="button" value=" Back " class="newPosBtn" style="width: 150px;" 
									     onClick="javascript:backToInterfaces('${pageContext.request.contextPath}','admin')" />
								</td>
								<td width="20%">&nbsp;</td>
							</tr>
						</table>
						<!-- RESULT -->
			
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
						&nbsp;<span class="searchResult">${interfacesForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
						
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
							<th> 
							 <c:choose>
									 <c:when test="${interfacesForm.criteria.monitorBean.type == 'IMPORT'}">
										สำเร็จ / ทั้งหมด
								 </c:when>
								 <c:otherwise>
										จำวนรายการ
								  </c:otherwise>
							   </c:choose>
							</th>
							<th> รายละเอียดเพิ่มเติม</th>
							<th> Message</th>
							</tr>
							
							<c:forEach var="results" items="${interfacesForm.results}" varStatus="rows">
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
									<td>
									<c:choose>
										<c:when test="${results.transactionType == 'MASTER'}">
											ข้อมูลพื้นฐาน
										</c:when>
										<c:otherwise>
											ข้อมูล Transaction
										</c:otherwise>
									</c:choose>
									</td>
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
												${results.monitorItemBean.errorMsg}
											</c:when>
											<c:otherwise>
											   <a href ="javascript:getLog('${pageContext.request.contextPath}','${results.transactionType}','${results.monitorItemBean.fileName}')">${results.monitorItemBean.errorMsg}</a>
											</c:otherwise>
										</c:choose>
									</td>
							</tr>
							</c:forEach>
						</table>
						
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