<%@page import="com.isecinc.pens.report.salesanalyst.helper.SecurityHelper"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<title></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag_screen.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<%@page import="com.isecinc.pens.report.salesanalyst.SAInitial"%>
<jsp:useBean id="searchValuePopupForm" class="com.isecinc.pens.web.popup.SearchValuePopupForm" scope="request" />
<%
 String condNo = request.getParameter("condNo");
 String condNameValue = request.getParameter("condNameValue");
 String condNameText = (String)SecurityHelper.COLUMN_KEY_MAP.get(request.getParameter("condNameValue"));
 String searchType = request.getParameter("searchType");
%>
<script type="text/javascript">
var path1="";
function searchPopup(path, type) {
   path1=path;
   document.searchRolePopupForm.action = path + "/jsp/searchRolePopupAction.do?do=search&condType=<%=condNameValue%>&searchType=<%=searchType%>";
   document.searchRolePopupForm.submit();
   return true;
}


function selectChkRole(){
	var chk = document.getElementsByName("chk");
	var code = document.getElementsByName("code");
	var key = document.getElementsByName("key");
	var desc = document.getElementsByName("desc");

    var codes = "";
	var descs = "";
	var no = 0;
	for(i=0;i<chk.length;i++){
        if(chk[i].checked){
            no++;
        	codes += code[i].value+",";
        	descs += desc[i].value+" ,";
        }
	}
	if(codes != ""){
	   codes = codes.substr(0,codes.length-1);
	   descs = descs.substr(0,descs.length-1);
	   window.opener.setMainValue(codes,codes,descs ,'<%=condNo%>' );
 	   window.close();
	}else{
      alert("กรุณาเลือกอย่างน้อยหนึ่งรายการ");
	}
}


</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/searchRolePopupAction">

<input type="hidden" name="condNo" value ="<%=condNo%>" />
<input type="hidden" name="condNameValue" value ="<%=condNameValue%>" />
<input type="hidden" name="condNameText" value ="<%=condNameText%>" />
<input type="hidden" name="searchType" value ="<%=searchType%>" />
<input type="hidden" name="selectcode" id="selectcode" value ="" />

<table align="center" border="0" cellpadding="1" cellspacing="2" width="100%">
    <tr>
		<td width="15%" >&nbsp;</td>
		<td width="90%" class="h1"><b>ค้นหาข้อมูล :<%=condNameText %></b></td>
	</tr>
	<tr>
		<td width="15%" >รหัส  </td>
		<td width="90%" ><html:text property="salesBean.code"  size="30"/>
		<input type="button" name="search" value="Search" onclick="searchPopup('<%=request.getContextPath()%>','')"/>
		</td>
	</tr>
	<tr>
		<td >รายละเอียด</td>
		<td ><html:text property="salesBean.desc"  size="60"/></td>
	</tr>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="center">
			<input type="button" name="ok" value="OK" onclick="selectChkRole()"/>
			<input type="button" name="close" value="Close" onclick="javascript:window.close();"/>
		</td>
	</tr>
</table>
<!-- RESULT -->
<display:table style="width:100%;" id="item" name="sessionScope.VALUE_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="../jsp/searchValuePopupAction.do?do=search" sort="list" pagesize="20">	
    	
    <display:column title="เลือกข้อมูล" style="width:20;align:left;white-space:nowrap" sortable="true">
		<input type ="checkbox" name="chk" /> 
		<input type ="hidden" name="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="key" value="<bean:write name="item" property="key"/>" />
		<input type ="hidden" name="desc" value="<bean:write name="item" property="name"/>" />
	 </display:column>
    											    
    <display:column  title="รหัส" property="key" style="width:80;align:left;white-space:nowrap" sortable="true"/>
    <display:column  title="รายละเอียด" property="name" style="width:100;align:left;white-space:nowrap" sortable="true"/>								
</display:table>	
<!-- RESULT -->


</html:form>
</body>
</html>