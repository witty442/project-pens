<%@page import="com.pens.util.*"%>
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
String empId= Utils.isNull(request.getParameter("empId"));
String type= Utils.isNull(request.getParameter("type"));
%>

<html>
<head>
<title>Popup</title>
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
	
%>
<script type="text/javascript">

function searchPopup(path, type) {
    document.popupForm.action = path + "/jsp/popupAction.do?do=search";
    document.popupForm.submit();
   return true;
}

function selectOneRadio(){
	var chRadio = document.getElementsByName("chRadio");
	var checkStockDate = document.getElementsByName("checkStockDate");
	
	for(var i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
        	//alert(i+":"+code[i+1].value);
            window.opener.setCheckStockDateMainValue(checkStockDate[i].value );
        	window.close();
            break;
        }
	}
}


</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">

<html:form action="/jsp/popupAction">

<html:hidden property="page" value="searchCountDatePopup"></html:hidden>

	<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" class="tableHead" >
	    <tr height="21px" class="txt1" align="center">
			<th ><b>ค้นหาข้อมูล SA วันที่เข้าตรวจนับ</b></th>
		</tr>
		<tr height="21px" class="txt1" align="left">
			<th><b>Emp ID:</b><%=empId %> &nbsp; <b>Type:</b><%=type %> </th>
		</tr>
	</table>
	
	<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
		<tr>
			<td align="center">
				<input type="button" name="ok" class="newPosBtnLong" value="OK" onclick="selectOneRadio()" style="width:100px;"/>
				<input type="button" name="close" class="newPosBtnLong" value="Close" onclick="javascript:window.close();" style="width:100px;"/>
			</td>
		</tr>
	</table>
<!-- RESULT -->

<display:table style="width:100%;" id="item" name="requestScope.CHECK_STOCK_DATE_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
    	
    <display:column style="text-align:center;" title="เลือกข้อมูล"   sortable="false" class="chk">
		<input type ="radio" name="chRadio" />
		<input type ="hidden" name="checkStockDate" id="checkStockDate" value="<bean:write name="item" property="checkStockDate"/>" />

	 </display:column>					    
    <display:column  title="วันที่เข้าตรวจนับ" property="checkStockDate"   sortable="false" class="code"/>
  
</display:table>	

<!-- RESULT -->

</html:form>

</body>
</html>