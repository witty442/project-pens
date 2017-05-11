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
</style>>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>

<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="request" />
<%
	String types = request.getParameter("types");
    String seqNo = request.getParameter("seqNo");
%>
<script type="text/javascript">

function searchPopup(path, type) {
    document.popupForm.action = path + "/jsp/searchProductPopupAction.do?do=search2";
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
<html:form action="/jsp/searchProductPopupAction">
<input type="hidden" name="seqNo" value="<%=seqNo %>"/>
<input type="hidden" name="types" value="<%=types %>"/>

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px" class="txt1">
		<td width="15%" >&nbsp;</td>
		<td width="90%" ><b>ค้นหาข้อมูล Item Adjust Stock</b></td>
	</tr>
	<tr height="21px" class="txt1">
		<td width="15%" ><b>รหัส</b>  </td>
		<td width="90%" ><html:text property="codeSearch"  size="30" style="height:20px"/>
		<input type="button" name="search" value="Search" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
	<tr height="21px" class="txt1">
		<td ><b>รายละเอียด</b></td>
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
<display:table style="width:100%;" id="item" name="requestScope.PRODUCT_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
    	
    <display:column style="text-align:center;" title="เลือกข้อมูล"  sortable="false" class="chk">

		<input type ="radio" name="chRadio" />
		<input type ="hidden" name="code" id="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="desc" id="desc" value="<bean:write name="item" property="desc"/>" />
		<input type ="hidden" name="price" id="price" value="<bean:write name="item" property="price"/>" />
	 </display:column>
    											    
    <display:column title="item" property="code"   sortable="false" class="code"/>
    <display:column title="ItemDesc" property="desc"  sortable="false" class="desc"/>		
     <display:column title="ราคา" property="price"  sortable="false" class="desc"/>								
</display:table>
	
<!-- RESULT -->
</html:form>
</body>
</html>