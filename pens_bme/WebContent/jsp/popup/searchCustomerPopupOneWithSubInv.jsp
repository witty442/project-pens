<%@page import="util.Constants"%>
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
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/popup_style.css" type="text/css" />
<style type="text/css">
input[type=checkbox]
{
  /* Double-sized Checkboxes */
  -ms-transform: scale(2); /* IE */
  -moz-transform: scale(2); /* FF */
  -webkit-transform: scale(2); /* Safari and Chrome */
  -o-transform: scale(2); /* Opera */
  padding: 10px;
}

input[type=radio]
{
  /* Double-sized Checkboxes */
  -ms-transform: scale(2); /* IE */
  -moz-transform: scale(2); /* FF */
  -webkit-transform: scale(2); /* Safari and Chrome */
  -o-transform: scale(2); /* Opera */
  padding: 10px;
}
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
    String methodName = Utils.isNull(request.getParameter("methodName"));
    System.out.println("methodName="+methodName);
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
        	//alert(i+":"+storeNo[i+1].value);
        	<% if(methodName.equals("pickStockGroup")){ %>
        	   window.opener.setStoreMainValuePickStockGroup(code[i].value,desc[i].value,storeNo[i].value,subInv[i].value ,types);
        	<%}else{ %>
               window.opener.setStoreMainValue(code[i].value,desc[i].value,storeNo[i].value,subInv[i].value ,types);
            <%}%>
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
<input type="hidden" name="methodName" value="<%=methodName %>"/>

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" class="tableHead">
    <tr height="21px" class="txt1">
		<th width="15%" >&nbsp;</th>
		<th width="90%" ><b>ค้นหาข้อมูลร้านค้า ( <%=Constants.getStoreGroupName(storeGroup)%>)</b></th>
	</tr>
	<tr height="21px" class="txt1">
		<td width="20%" ><b>รหัส</b>  </td>
		<td width="90%" ><html:text property="codeSearch"  size="30" style="height:20px"/>
		<input type="button" name="search" value="Search" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
	<tr height="21px" class="txt1">
		<td width="20%" ><b>รายละเอียด</b></td>
		<td width="90%" ><html:text property="descSearch"  size="60" style="height:20px"/></td>
	</tr>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<input type="button" name="ok" value="OK" onclick="selectOneRadio()" style="width:80px;"  class="newPosBtnLong" />
			<input type="button" name="close" value="Close" onclick="javascript:window.close();" style="width:80px;"  class="newPosBtnLong" />
		</td>
	</tr>
</table>
<!-- RESULT -->
<display:table style="width:100%;" id="item" name="requestScope.CUSTOMER_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
    	
    <display:column style="text-align:center;" title="เลือกข้อมูล"  sortable="false" class="chk">

		<input type ="radio" name="chRadio" />
		<input type ="hidden" name="code" id="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="desc" id="desc" value="<bean:write name="item" property="desc"/>" />
		<input type ="hidden" name="storeNo" id="storeNo" value="<bean:write name="item" property="storeNo"/>" />
		<input type ="hidden" name="subInv" id="subInv" value="<bean:write name="item" property="subInv"/>" />
	 </display:column>
    											    
    <display:column title="รหัส" property="code"   sortable="false" class="code"/>
    <display:column title="รายละเอียด" property="desc" sortable="false" class="desc"/>	
    <display:column  title="StoreNo" property="storeNo"  sortable="false" class="desc"/>	
    <display:column  title="SubInv" property="subInv"  sortable="false" class="desc"/>								
</display:table>	
<!-- RESULT -->



</html:form>
</body>
</html>