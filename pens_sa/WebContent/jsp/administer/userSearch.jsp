<%@page import="util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.model.MGroupRole"%>

<jsp:useBean id="userForm" class="com.isecinc.pens.web.user.UserForm" scope="request" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/user.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
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
				<jsp:param name="function" value="User"/>
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
						<html:form action="/jsp/userAction">
						<jsp:include page="../error.jsp"/>
						<!-- CRITERIA -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="45%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right">ชื่อ&nbsp;&nbsp;</td>
								<td align="left"><html:text property="user.name" size="30"/></td>
							</tr>
							<tr>
								<td align="right">รหัสผู้ใช้&nbsp;&nbsp;</td>
								<td align="left"><html:text property="user.userName" size="20"/></td>
							</tr>
							<tr>
								<td align="right">กลุ่มผู้ใช้&nbsp;&nbsp;</td>
								<td align="left">
								    <html:select property="user.userGroupId">
									    <html:options collection="critriaGroupList" property="key" labelProperty="name"/>
							        </html:select>
								</td>
							</tr>
							<tr>
								<td align="right">สถานะ&nbsp;&nbsp;</td>
								<td align="left" valign="top">
									<html:select property="user.active">
										<html:option value=""></html:option>
										<html:option value="Y"><bean:message key="Active" bundle="sysprop"/></html:option>
										<html:option value="N"><bean:message key="Inactive" bundle="sysprop"/></html:option>
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
									<!--<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ค้นหา" class="newPosBtnLong">
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<!--<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">-->
									<input type="button" value="Clear" class="newPosBtnLong">
									</a>
								</td>
							</tr>
						</table>
						<!-- RESULT -->
						<c:if test="${userForm.results != null}">

						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
						&nbsp;<span class="searchResult">${userForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
						
						
						<!-- <table align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch"> -->
						<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
							<tr>
								<th><bean:message key="No"  bundle="sysprop"/></th>
								<th>
									<input type="checkbox" name="chkAll" onclick="checkSelect(this,document.getElementsByName('ids'));" />
								</th>
								<th><bean:message key="User.Name"  bundle="sysele"/></th>
								<th><bean:message key="User.UserName"  bundle="sysele"/></th>
								<th><bean:message key="User.Group"  bundle="sysele"/></th>
								<th>วันที่เริ่มต้น</th>
								<th>วันที่สิ้นสุด</th>
								<th><bean:message key="Status"  bundle="sysele"/></th>
								<th><bean:message key="Edit"  bundle="sysprop"/></th>
							</tr>	
							<c:forEach var="results" items="${userForm.results}" varStatus="rows">
							<c:choose>
								<c:when test="${rows.index %2 == 0}">
									<c:set var="tabclass" value="lineE"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineO"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>">
								<td  class="td_text_center"><c:out value='${rows.index+1}'/></td>
								<td  class="td_text_center"><input type="checkbox" name="ids" value="${results.id}"/></td>
								<td  class="td_text_center">${results.name}</td>
								<td  class="td_text_center">${results.userName}</td>
								<td  class="td_text_center">${results.userGroupName}</td>
								<td  class="td_text_center">${results.startDate}</td>
								<td  class="td_text_center">${results.endDate}</td>
								<td  class="td_text_center">${results.activeLabel}</td>
								<td  class="td_text_center">
									<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','${results.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif"></a>
								</td>
							</tr>
							</c:forEach>
							</table>
							
							<table align="center" border="0" cellpadding="3" cellspacing="0" class="result">	
								<tr>
									<td align="left" colspan="9" class="footer">&nbsp; 
									    <a href="javascript:addNewUser('${pageContext.request.contextPath}');"> 
										<img border=0 src="${pageContext.request.contextPath}/icons/user_add.gif">&nbsp; เพิ่ม</a>
										&nbsp; 
										<a href="javascript:changeActive('${pageContext.request.contextPath}','Y',document.getElementsByName('ids'));"> 
										<img border=0 src="${pageContext.request.contextPath}/icons/user_active.gif"> <bean:message key="Active" bundle="sysprop"/></a> &nbsp; 
										<a href="javascript:changeActive('${pageContext.request.contextPath}','N',document.getElementsByName('ids'));"> 
										<img border=0 src="${pageContext.request.contextPath}/icons/user_inactive.gif"> ยกเลิก</a>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									     เลือกข้อมูลกลุ่มผู้ใช้
									     <html:select property="user.changeUserGroup">
									         <html:options collection="groupList" property="key" labelProperty="name"/>
							            </html:select>
							            <a href="javascript:changeUserGroup('${pageContext.request.contextPath}',document.getElementsByName('ids'))">
											<input type="button" value="เปลี่ยนกลุ่มผู้ใช้  " class="newNegBtn">
										</a>
									</td>
									<td align="right" class="footer"></td>
								</tr>
							</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="backToMain('${pageContext.request.contextPath}')">
									<input type="button" value="ปิดหน้าจอ" class="newPosBtnLong">
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