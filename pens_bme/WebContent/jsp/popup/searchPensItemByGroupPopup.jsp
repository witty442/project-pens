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
String types = request.getParameter("types");
String seqNo = request.getParameter("seqNo");
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
	var seqNo = document.getElementsByName("seqNo")[0].value;// alert(types);
	var types = document.getElementsByName("types")[0].value;// alert(types);
	
	var code = document.getElementsByName("code");
	var desc = document.getElementsByName("desc");
	var price = document.getElementsByName("price");

	for(var i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
        	//alert(i+":"+code[i+1].value);
            window.opener.setProductMainValue(seqNo,types,code[i].value,desc[i].value,price[i].value );
        	window.close();
            break;
        }
	}
}


</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/popupAction">
<html:hidden property="page" value="searchPensItemByGroupPopup"></html:hidden>
<input type="hidden" name="seqNo" value="<%=seqNo %>"/>
<input type="hidden" name="types" value="<%=types %>"/>

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px" class="txt1">
		<td width="25%" >&nbsp;</td>
		<td width="75%" ><b>ค้นหาข้อมูล Pens Item</b></td>
	</tr>
	<tr height="21px" class="txt1">
		<td width="25%" ><b>Group Code</b>  </td>
		<td width="75%" ><html:text property="codeSearch"  size="20" style="height:20px"/>
		<input type="button" name="search" value="Search" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
	<%-- <tr height="21px" class="txt1">
		<td ><b>รายละเอียด</b></td>
		<td ><html:text property="descSearch"  size="60" style="height:20px"/></td>
	</tr> --%>
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
<display:table  id="item" name="requestScope.GROUP_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="../jsp/popupAction.do?do=search" sort="list" pagesize="20" class="resultDisp">	
    	
    <display:column  title="เลือกข้อมูล"   sortable="false" class="chk">

		<input type ="radio" name="chRadio" />
		<input type ="hidden" name="code" id="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="desc" id="desc" value="<bean:write name="item" property="desc"/>" />
		<input type ="hidden" name="price" id="price" value="<bean:write name="item" property="price"/>" />
	 </display:column>
    											    
    <display:column  title="Group Code" property="code"   sortable="false" class="code"/>
   	<display:column  title="Pens Item" property="desc"   sortable="false" class="desc"/>
   	<display:column  title="Price" property="price"   sortable="false" class="desc"/>
   						
</display:table>	
<!-- RESULT -->

</html:form>
</body>
</html>