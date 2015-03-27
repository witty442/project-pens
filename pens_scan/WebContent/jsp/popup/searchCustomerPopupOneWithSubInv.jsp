<%@page import="com.isecinc.pens.inf.helper.Utils"%>
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
	String types = Utils.isNull(request.getParameter("types"));
    String storeType = Utils.isNull(request.getParameter("storeType"));
    String storeGroup = Utils.isNull(request.getParameter("storeGroup"));
%>
<script type="text/javascript">

function searchPopup(path, type) {
    document.popupForm.action = path + "/jsp/searchCustomerPopupAction.do?do=search3";
    document.popupForm.submit();
   return true;
}

function selectOneRadio(){
	var chRadio = document.getElementsByName("chRadio");
	var types = document.getElementsByName("types")[0].value;// alert(types);
	var code = document.getElementsByName("code");
	var desc = document.getElementsByName("desc");
	var storeNo = document.getElementsByName("storeNo");
	var subInv = document.getElementsByName("subInv");
	
	for(var i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
        	//alert(i+":"+code[i+1].value);
            window.opener.setStoreMainValue(code[i].value,desc[i].value,storeNo[i].value,subInv[i].value ,types);
        	window.close();
            break;
        }
	}
}


</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/searchCustomerPopupAction">
<input type="hidden" name="types" value="<%=types %>"/>
<input type="hidden" name="storeType" value="<%=storeType %>"/>
<input type="hidden" name="storeGroup" value="<%=storeGroup %>"/>

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px" class="txt1">
		<td width="15%" >&nbsp;</td>
		<td width="90%" ><b>���Ң�������ҹ���</b></td>
	</tr>
	<tr height="21px" class="txt1">
		<td width="15%" ><b>����</b>  </td>
		<td width="90%" ><html:text property="codeSearch"  size="30" style="height:20px"/>
		<input type="button" name="search" value="Search" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
	<tr height="21px" class="txt1">
		<td ><b>��������´</b></td>
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
<display:table width="100%" id="item" name="requestScope.CUSTOMER_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="../jsp/searchCustomerPopupAction.do?do=search3" sort="list" pagesize="20" class="resultDisp">	
    	
    <display:column align="left" title="���͡������"  nowrap="true" sortable="false" class="chk">

		<input type ="radio" name="chRadio" />
		<input type ="hidden" name="code" id="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="desc" id="desc" value="<bean:write name="item" property="desc"/>" />
		<input type ="hidden" name="storeNo" id="storeNo" value="<bean:write name="item" property="storeNo"/>" />
		<input type ="hidden" name="subInv" id="subInv" value="<bean:write name="item" property="subInv"/>" />
	 </display:column>
    											    
    <display:column align="left" title="����" property="code"  nowrap="false" sortable="false" class="code"/>
    <display:column align="left" title="��������´" property="desc" nowrap="false" sortable="false" class="desc"/>	
    <display:column align="left" title="StoreNo" property="storeNo" nowrap="false" sortable="false" class="desc"/>	
    <display:column align="left" title="SubInv" property="subInv" nowrap="false" sortable="false" class="desc"/>								
</display:table>	
<!-- RESULT -->



</html:form>
</body>
</html>