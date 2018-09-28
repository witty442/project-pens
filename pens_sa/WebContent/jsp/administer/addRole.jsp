<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@page import="util.Utils"%>
<%@page import="com.isecinc.pens.model.MRole"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.web.role.RoleForm"%>
<jsp:useBean id="roleForm" class="com.isecinc.pens.web.role.RoleForm" scope="request" />

<%
MRole.initailRoleList(request);
String mode =  "add";
	if( request.getAttribute("roleForm") != null){
		RoleForm roleForm1 =(RoleForm)request.getAttribute("roleForm");
		mode = roleForm1.getMode();
	}
%>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/role.js"></script>
<script>

function addRow(path) {
	document.roleForm.action = path + "/jsp/roleAddAction.do?do=addRowTable";
	document.roleForm.submit();
	return true;
}
function backToRolePage(path) {
	document.roleForm.action = path + "/jsp/roleAddAction.do?do=backToRolePage";
	document.roleForm.submit();
	return true;
}

function removeRow(path) {
	var obj = document.getElementsByName("ids");
	if( !isChkOne(obj)){
		alert("โปรดเลือกข้อมูลอย่างน้อยหนึ่งรายการ");
		return false;
	}
	
	document.roleForm.action = path + "/jsp/roleAddAction.do?do=removeRowTable";
	document.roleForm.submit();
	return true;
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

function saveRow(path) {
	if (document.getElementsByName('role.roleName')[0].value == '') {
		 document.getElementsByName('role.roleName')[0].focus();
        alert('กรุณาระบุ Role');
        return false;
    }

    /** validate Role In Table **/
    var errorList = new Array();
    var index = 0;
    var valid = true;
	var roleData =  document.getElementsByName("roleDataAccess");
	var roleDataDesc = document.getElementsByName("roleDataAccessDesc");
	for(i=0;i<roleData.length;i++){
		if(roleData[i].value == ''){
			errorList[index] = roleDataDesc[i];
			index++;
		}
	}
   if(errorList != null && errorList.length >0){
	   for(i=0;i<errorList.length;i++){
		   errorList[i].className = "bgValidateError";
	   }
	   alert("กรุณาระบุประเภทข้อมูลย่อย");
	   return false;
   }
	 
	document.roleForm.action = path + "/jsp/roleAddAction.do?do=saveRowTable";
	document.roleForm.submit();
	return true;
}

function searchRole(path) {
	var roleDup = 'false';
	if (document.getElementsByName('role.roleName')[0].value == '') {
		 document.getElementsByName('role.roleName')[0].focus();
         alert('กรุณาระบุ Role');
         return false;
    }
    if( !validateRoleName()){
    	 roleDup = 'true';
    	 alert("Role นี้มีในระบบแล้ว กรุณาเปลี่ยนชื่อ Role ใหม่");
    }
    document.roleForm.action = path + "/jsp/roleAddAction.do?do=searchRole&roleDup="+roleDup;
	document.roleForm.submit();
	return true;
	
}


/** new  **/
function validateRoleName(){
	//alert("listDataPath:"+path);
	var obj  = document.getElementsByName("role.roleName")[0];
	var returnString= "";
	if(obj.value != '0'){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/validateRoleNameQuery.jsp",
				data : "roleName="+obj.value,
				async:false,
				cache: true,
				success: function(getData){
					returnString = jQuery.trim(getData);
				}
			}).responseText;   
		});
	}
    //alert(returnString);
	if(returnString=='true'){
       return false;
	}
	return true;
}




function showSearchValuePopup(path,index){
	
	var code = new Object();
	var text = new Object();
   // alert("index:"+(index-1));
	code = document.getElementsByName("roleColumnAccess")[index-1].value;
	text = document.getElementsByName("roleColumnAccess")[index-1].options[document.getElementsByName("roleColumnAccess")[index-1].selectedIndex].text;
    //alert("code:"+code+",text:"+text);
	
	if(code =='ALL'){
		document.getElementsByName("roleDataAccess")[index-1].value = "ALL";
		document.getElementsByName("roleDataAccessDesc")[index-1].value = "ดูข้อมูลได้ทั้งหมด";
	}else{
		var url = path + "/jsp/searchRolePopupAction.do?do=prepare&action=new&condNo="+index+"&condNameValue="+code+"&condNameText="+text+"&searchType=ROLE";
		
		window.open(encodeURI(url),"",
				   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=500px,status=no,left="+ 50 + ",top=" + 0);
	}
}

function setMainValue(code,key,desc,index){
	 document.getElementsByName("roleDataAccess")[index-1].value = code;
	 document.getElementsByName("roleDataAccessDesc")[index-1].value = desc;
}
function clearText(index){
	var code = new Object();
	var text = new Object();
   // alert("index:"+(index-1));
	code = document.getElementsByName("roleColumnAccess")[index-1].value;
	text = document.getElementsByName("roleColumnAccess")[index-1].options[document.getElementsByName("roleColumnAccess")[index-1].selectedIndex].text;
    //alert("code:"+code+",text:"+text);
	
	if(code =='ALL'){
		document.getElementsByName("roleDataAccess")[index-1].value = "ALL";
		document.getElementsByName("roleDataAccessDesc")[index-1].value = "ดูข้อมูลได้ทั้งหมด";
	}else{
	    document.getElementsByName("roleDataAccess")[index-1].value = "";
	    document.getElementsByName("roleDataAccessDesc")[index-1].value = "";
	}
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
				<jsp:param name="function" value="Role"/>
				<jsp:param name="code" value="<%=mode%>"/>
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
						<html:form action="/jsp/roleAddAction">
						<html:hidden property="role.returnStrAjax"/>
						<html:hidden property="mode"/>
						
						<jsp:include page="../error.jsp"/>
						<!-- CRITERIA -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="45%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right">Role<font color="red">*</font>&nbsp;&nbsp;</td>
								<td align="left">
								    <c:choose>
										<c:when test="${roleForm.mode =='Edit'}">
											<html:text property="role.roleName" size="40"/>
										</c:when>
										<c:otherwise>
											<html:text property="role.roleName" size="40"/>
										</c:otherwise>
									</c:choose>
									<html:text property="role.roleId"/>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								      <c:choose>
										<c:when test="${roleForm.mode =='Edit'}">
										</c:when>
										<c:otherwise>
											<a href="javascript:searchRole('${pageContext.request.contextPath}')">
											<input type="button" value="เพิ่ม" class="newPosBtn">
										</c:otherwise>
									  </c:choose>
									</a>
								</td>
							</tr>
						</table>
				         
				        <!-- RESULT -->
				       <c:if test="${ROLE_DETAIL_LIST != null}">
				        <display:table style="width:70%;"  id="item" name="sessionScope.ROLE_DETAIL_LIST"  
						    defaultsort="0" defaultorder="descending" requestURI="../jsp/roleAddAction.do?do=search" sort="list" pagesize="10"
						    class ="resultDisp" border="0" cellpadding="1" cellspacing="1" align="center" >	
						    
				
						    	
						     <display:column align="left" title="No." property ="index" style="width=20;align:left;white-space:nowrap" sortable="false" />
						    
							 <display:column align="left" title="ประเภทข้อมูล"   nowrap="false" style="width=100;align:left;white-space:nowrap"" >
						        <select name="roleColumnAccess" onchange="clearText('${item.index}')">
							          <c:forEach items='${sessionScope.roleColumnAccessList}' var='p'>
								             <c:choose>
												<c:when test="${p.key == item.roleColumnAccess}">
													<option value="<c:out value='${p.key}'/>" selected><c:out value='${p.name}'/></option>
												</c:when>
												<c:otherwise>
													<option value="<c:out value='${p.key}'/>"><c:out value='${p.name}'/></option>
												</c:otherwise>
											</c:choose>
									  </c:forEach>
								  </select>
						    </display:column>
						    <display:column align="left" title="ประเภทข้อมูลย่อย" style="width=100;align:left;white-space:nowrap" sortable="false" >
								    <input type="text" name="roleDataAccess" value="${item.roleDataAccess}"/>
								    <textarea  name="roleDataAccessDesc" rows="2" cols="60">${item.roleDataAccessDesc} </textarea >
								   &nbsp;   
									 <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','${item.index}');">
							           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" />
							        </a>
							         <a href="javascript:clearText('${item.index}');">
							          <img border=0 src="${pageContext.request.contextPath}/icons/data_del.gif"/>
							        </a>                                        
							               
						   </display:column>	
	
						</display:table>	
						<!-- BUTTON ADD -->
						<!--
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
						-->
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
							
								<td align="center">
								     <a href="#" onclick="saveRow('${pageContext.request.contextPath}');">
									<input type="button" value="บันทึก" class="newPosBtn">
									</a>
									<a href="#" onclick="backToRolePage('${pageContext.request.contextPath}')">
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
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