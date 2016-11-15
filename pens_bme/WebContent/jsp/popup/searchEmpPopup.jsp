<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
%>

<html>
<head>
<title></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>

<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="request" />

<%
	
%>
<script type="text/javascript">

function searchPopup(path, type) {
    document.popupForm.action = path + "/jsp/popupAction.do?do=search";
    document.popupForm.submit();
   return true;
}

function selectOneRadio(){
	var chRadio = document.getElementsByName("chRadio");

	var code = document.getElementsByName("code");
	var name = document.getElementsByName("name");
	var surname = document.getElementsByName("surname");
	var branch = document.getElementsByName("branch");
	var groupStore = document.getElementsByName("groupStore");
	
	for(var i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
        	//alert(i+":"+code[i+1].value);
            window.opener.setEmpMainValue(code[i].value,name[i].value,surname[i].value,branch[i].value,groupStore[i].value  );
        	window.close();
            break;
        }
	}
}


</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/popupAction">
<html:hidden property="page" value="searchEmpPopup"></html:hidden>

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px" class="txt1">
		<td width="25%" >&nbsp;</td>
		<td width="75%" ><b>ค้นหาข้อมูล SA Employee</b></td>
	</tr>
	<tr height="21px" class="txt1">
		<td width="25%" ><b>Emp ID</b>  </td>
		<td width="75%" ><html:text property="codeSearch"  size="20" style="height:20px"/>
		<input type="button" name="search" value="Search" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
	 <tr height="21px" class="txt1">
		<td ><b>Name</b></td>
		<td ><html:text property="descSearch"  size="60" style="height:20px"/></td>
	</tr> 
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<input type="button" name="ok" value="OK" onclick="selectOneRadio()" style="width:60px;"/>
			<input type="button" name="close" value="Close" onclick="javascript:window.close();" style="width:60px;"/>
		</td>
	</tr>
</table>

<!-- RESULT -->
<display:table style="width:100%;" id="item" name="requestScope.EMP_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
    	
    <display:column style="text-align:center;" title="เลือกข้อมูล"   sortable="false" class="chk">
		<input type ="radio" name="chRadio" />
		<input type ="hidden" name="code" id="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="name" id="name" value="<bean:write name="item" property="name"/>" />
		<input type ="hidden" name="surname" id="surname" value="<bean:write name="item" property="surname"/>" />
		<input type ="hidden" name="groupStore" id="groupStore" value="<bean:write name="item" property="groupStore"/>" />
		<input type ="hidden" name="branch" id="branch" value="<bean:write name="item" property="branch"/>" />
	 </display:column>
    											    
    <display:column  title="Emp ID" property="code"   sortable="false" class="code"/>
   	<display:column  title="Name" property="name"   sortable="false" class="desc"/>
    <display:column  title="Surname" property="surname"   sortable="false" class="desc"/>
    <display:column  title="Group Store" property="groupStore"   sortable="false" class="desc"/>
   	<display:column  title="branch" property="branch"   sortable="false" class="desc"/>
</display:table>	
<!-- RESULT -->

</html:form>
</body>
</html>