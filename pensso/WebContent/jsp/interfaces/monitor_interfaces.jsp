<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.Constants"%>

<jsp:useBean id="userForm" class="com.isecinc.pens.web.user.UserForm" scope="session" />
<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />
<%
String role = ((User)session.getAttribute("user")).getType();
User user = (User)session.getAttribute("user");
%>
<html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/monitor_interfaces.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablednd_0_5.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript" language="javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('submitDateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('submitDateTo'));
	//loadId();
}
</script>

<!-- Move for new index. -->
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe(); MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="MonitorInterfaces"/>
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
						<html:form action="/jsp/monitorInterfacesAction">
			            <jsp:include page="../error.jsp"/>	
			            
			                <!-- ******************Criteria ****************-->
			                <table width="60%" border="0" align="center" cellpadding="3" cellspacing="0" class="body">
							 <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
							 		<!-- Criteria For Admin-->
										<tr>
										  <td width="27%" align="left">ชื่อรายการ</td>
											<td width="36%" align="left"><html:text property="monitorBean.requestTable" size="15"/></td>
											<td width="19%">ผู้สร้าง</td>
											<td width="18%"><html:text property="monitorBean.userName" size="15"/></td>
										</tr>
										<tr>
										  <td align="left">วันที่สร้างตั้งแต่</td>
										  <td align="left"><html:text property="monitorBean.submitDateFrom" styleId="submitDateFrom" readonly="true" size="15"/></td>
										  <td>วันที่สร้างถึง</td>
										  <td><html:text property="monitorBean.submitDateTo" styleId="submitDateTo" readonly="true" size="15"/></td>
									    </tr>
									   

								<%}else{ %>
									 <!-- Criteria For Sales-->
										<tr>
										   <td align="left">วันที่สร้างตั้งแต่</td>
										  <td align="left"><html:text property="monitorBean.submitDateFrom" styleId="submitDateFrom" readonly="true" size="15"/></td>
										  <td>วันที่สร้างถึง</td>
										  <td><html:text property="monitorBean.submitDateTo" styleId="submitDateTo" readonly="true" size="15"/></td>
									    </tr>
									  
				                      <!-- Criteria For Sales-->
							   <%}  %>
						           <tr>
									  <td width="27%" align="left">ประเภทข้อมูล</td>
										<td width="36%" align="left">
										 <html:select property="monitorBean.transactionType">     
									            <html:option value="">ALL</html:option>
											    <html:option value="<%=Constants.TRANSACTION_MASTER_TYPE%>">ข้อมูลพื้นฐาน</html:option>
											    <html:option value="<%=Constants.TRANSACTION_TRANS_TYPE%>">ข้อมูล Transaction</html:option>
											    <html:option value="<%=Constants.TRANSACTION_UTS_TRANS_TYPE%>">ข้อมูล Update Sales Transaction</html:option>
											    <html:option value="<%=Constants.TRANSACTION_WEB_MEMBER_TYPE%>">ข้อมูล Web Member</html:option>
								         </html:select>             
	                                   </td>
										<td width="19%">สถานะ</td>
										<td width="18%">
										     <html:select property="monitorBean.status">     
									            <html:option value="0">ALL</html:option>
											    <html:option value="<%=String.valueOf(Constants.STATUS_SUCCESS)%>">สำเร็จ</html:option> 
											    <html:option value="<%=String.valueOf(Constants.STATUS_FAIL)%>">ไม่สำเร็จ</html:option> 
								             </html:select>  
								         </td>
									</tr>
								  <tr>
									  <td width="27%" align="left">ประเภท</td>
										<td width="36%" align="left">
										 <html:select property="monitorBean.type">     
									            <html:option value="">ALL</html:option>
											    <html:option value="<%=Constants.TYPE_IMPORT%>">IMPORT</html:option>
											    <html:option value="<%=Constants.TYPE_EXPORT%>">EXPORT</html:option>
											  
								         </html:select>             
	                                   </td>
										<td width="19%"></td>
										<td width="18%"></td>
									</tr>
						      </table>
						     <!-- ******************Criteria ****************-->
							<br>
							<!-- BUTTON -->
							<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
								<tr>
									<td align="right" nowrap>
									     <input type="button" value=" Search " class="newPosBtn" style="width: 150px;" 
										     onClick="javascript:search('${pageContext.request.contextPath}','admin')" />
										  <input type="button" value=" Clear " class="newPosBtn" style="width: 150px;" 
										     onClick="javascript:clearForm('${pageContext.request.contextPath}','admin')" />
									</td>
									<td width="20%">&nbsp;</td>
								</tr>
							</table>
							
							<c:if test="${interfacesForm.results != null}">
								<!-- RESULT -->
					
								<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
								&nbsp;<span class="searchResult">${interfacesForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
								
									<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" width="40%">
										<tr>
						                <th> ลำดับที่.</th>
										<th> ประเภท</th>
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
										 <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
										    <th> Message</th>
										 <%} %>
										  <th>
										             รายการ Export
										  </th>
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
								                <td width="3%"> <c:out value='${rows.index+1}'/></td>
								              
												<td width="3%">  ${results.type}</td>
												<td align="left" width="7%"> ${results.monitorItemBean.tableName}</td>
												<td align="left" width="7%">
												    <a href ="javascript:getTextFile('${pageContext.request.contextPath}','${results.transactionType}','${results.monitorItemBean.fileName}','${results.monitorItemBean.status}','${results.type}')">
												     ${results.monitorItemBean.fileName}
												    </a>
												</td>
												<td align="left" width="3%"> ${results.monitorItemBean.fileSize}</td>
												<td width="3%"> ${results.monitorItemBean.source}</td>
												<td width="3%"> ${results.monitorItemBean.destination}</td>
												<td width="3%"> ${results.createUser}</td>
												<td width="3%"> 
												  <c:choose>
													<c:when test="${results.monitorItemBean.status == 1}">
														<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
													</c:when>
													<c:otherwise>
														<img border=0 src="${pageContext.request.contextPath}/icons/uncheck.gif">
													</c:otherwise>
											      </c:choose>
												</td>
												<td width="3%"> ${results.submitDate}</td>
												<td width="3%">
													<c:choose>
														<c:when test="${results.type == 'IMPORT'}">
															${results.monitorItemBean.successCount}/${results.monitorItemBean.dataCount}
														</c:when>
														<c:otherwise>
															${results.monitorItemBean.dataCount}
														</c:otherwise>
													</c:choose>
												</td>
												 <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
													<td align="left" width="3%"> 
													    <c:choose>
															<c:when test="${results.monitorItemBean.errorMsg == ''}">
																${results.monitorItemBean.errorMsg}
															</c:when>
															<c:otherwise>
															   <a 
															    href ="javascript:getLog('${pageContext.request.contextPath}','${results.transactionType}','${results.monitorItemBean.fileName}' ,'${results.createUser}')">
															   ${results.monitorItemBean.errorMsg}</a>
															</c:otherwise>
														</c:choose>
													</td>
												<%} %>
												
											  <td align="center" width="3%">  
												   <c:choose>
														<c:when test="${results.type == 'EXPORT'}">
															<a href ="javascript:showItemExport('${pageContext.request.contextPath}','${results.monitorItemBean.id}','${results.monitorItemBean.tableName}')">
												             <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
														</c:when>
														<c:otherwise>
														</c:otherwise>
													</c:choose>
											   </td>
										</tr>
										</c:forEach>
									</table>
					    </c:if>
							<br><br>
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