<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.Utils" %>
<%@page import="com.isecinc.pens.model.MGroupRole"%>
<%@page import="com.isecinc.core.bean.References"%>

<%
MGroupRole.initailRoleList(request);
%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="roleForm" class="com.isecinc.pens.web.role.RoleForm" scope="request" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablednd_0_5.js"></script>

<script>

function search(path) {
	document.groupRoleForm.action = path + "/jsp/groupRoleAction.do?do=search";
	document.groupRoleForm.submit();
	return true;
}

function addRow(path) {
	document.groupRoleForm.action = path + "/jsp/groupRoleAction.do?do=addRowTable";
	document.groupRoleForm.submit();
	return true;
}
function backToMain(path) {
	document.groupRoleForm.action = path + "/jsp/groupRoleAction.do?do=backToMain";
	document.groupRoleForm.submit();
	return true;
}

function removeRow(path) {
	var obj = document.getElementsByName("ids");
	if( !isChkOne(obj)){
		alert("โปรดเลือกข้อมูลอย่างน้อนหนึ่งรายการ");
		return false;
	}
	document.groupRoleForm.action = path + "/jsp/groupRoleAction.do?do=removeRowTable";
	document.groupRoleForm.submit();
	return true;
}

function saveRow(path) {
	document.groupRoleForm.action = path + "/jsp/groupRoleAction.do?do=save";
	document.groupRoleForm.submit();
	return true;
}

function viewRoleDetail(path,roleId) {
	document.location = path + "/jsp/roleAction.do?do=viewRole&roleId="+roleId;

}

function isChkOne(obj){
	for(i=0;i<obj.length;i++){
		var chk = obj[i].checked;
		if(chk){
		  return true;
		}
	}
	return false;
}
</script>

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
				<jsp:param name="function" value="Group"/>
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
						<html:form action="/jsp/groupRoleAction">
						<html:hidden property="groupRole.returnStrAjax"/>
						<jsp:include page="../error.jsp"/>
						<!-- CRITERIA -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="45%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right">รหัสกลุ่มผู้ใช้&nbsp;&nbsp;</td>
								<td align="left">
								       <html:text property="groupRole.userGroupName"/>
								       <html:hidden property="groupRole.userGroupId"/>
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
									<input type="button" value="ค้นหา" class="newPosBtn">
									</a>
								</td>
							</tr>
						</table>
				         
				         <c:if test="${GROUP_ROLE_LIST != null}">
				         
				        <!-- RESULT -->
				        <display:table width="70%" id="item" name="sessionScope.GROUP_ROLE_LIST"  
						    defaultsort="0" defaultorder="descending" requestURI="../jsp/groupRoleAction.do?do=search" sort="list" pagesize="50"
						    class ="resultDisp" border="0" cellpadding="3" cellspacing="1" align="center" >	
						    
						    <display:column align="center" title="No." property ="index" width="20" nowrap="false" sortable="true"/>
						     <display:column align="center" title="เลือก"  width="30" nowrap="false" sortable="true">	
							   <input type="checkbox" name="ids" value="${item.index}" <c:out value='${item.disabled}'/>/>
							 </display:column>	
						     
							 <display:column align="left" title="รหัสกลุ่มผู้ใช้"  width="100" nowrap="false" sortable="true">
							      <input type="hidden" name="userGroupId"  value ="${item.userGroupId}" />
							      <input type="text" name="userGroupName"  value ="${item.userGroupName}" size="50" />
							 </display:column>   
						     <display:column align="left" title="Role"  width="100" nowrap="false" sortable="true">
						        <select name="roleId">
							          <c:forEach items='${sessionScope.roleList}' var='p'>
		                                   <c:choose>
												<c:when test="${p.key == item.roleId}">
													<option value="<c:out value='${p.key}'/>" selected><c:out value='${p.name}'/></option>
												</c:when>
												<c:otherwise>
													<option value="<c:out value='${p.key}'/>"><c:out value='${p.name}'/></option>
												</c:otherwise>
											</c:choose>
									  </c:forEach>
								  </select>
						     </display:column>
						  
						</display:table>
							
						<!-- BUTTON ADD -->
						<table align="center" width="70%" border="0" cellpadding="3" cellspacing="0" class="resultDisp">	
								<tr>
									<td align="left" colspan="10" class="footer">&nbsp; 
										<a href="javascript:addRow('${pageContext.request.contextPath}','');"> 
										<img border=0 src="${pageContext.request.contextPath}/icons/data_add.gif"> 
										Add</a> &nbsp; 
										<a href="javascript:removeRow('${pageContext.request.contextPath}','');"> 
										<img border=0 src="${pageContext.request.contextPath}/icons/data_add.gif"> 
										Remove</a> &nbsp; 
									</td>
								</tr>
						</table>
						
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="#" onclick="saveRow('${pageContext.request.contextPath}');">
									<input type="button" value="บันทึก" class="newPosBtn">
									</a>
								</td>
							</tr>
						</table>
					</c:if>
					  <!-- BUTTON -->
					  <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="backToMain('${pageContext.request.contextPath}')">
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
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