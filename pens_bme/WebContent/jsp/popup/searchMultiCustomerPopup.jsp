<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.*"%>
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
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
input[type=checkbox]
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<%
    String storeGroup = Utils.isNull(request.getParameter("storeGroup"));
    String storeCodeArr = Utils.isNull(request.getParameter("storeCodeArr"));

    System.out.println("storeGroup:"+storeGroup);
    System.out.println("storeCodeArr:"+storeCodeArr);
%>
<script type="text/javascript">

function selectAll(obj){
	var chk = document.getElementsByName("chCheck");
	for(var i=0;i<chk.length;i++){
        chk[i].checked =obj.checked;
	}
}

function selectMultiple(){
	var chk = document.getElementsByName("chCheck");
	var code = document.getElementsByName("code_temp");
    var retCode = "";
    var retDesc = "";
	var idx = 2;
	var storeGroup = document.getElementsByName("storeGroup")[0].value;
	
	for(var i=0;i<chk.length;i++){
        if(chk[i].checked){
        	retCode += code[i].value+",";
        }
	}
	retCode = retCode.substring(0,retCode.length-1);
	//retDesc = retDesc.substring(0,retDesc.length-1);
	
	//alert("idx:"+idx);
	if(idx ==1){
		//alert(currCondNo+","+retCode+":"+retKey+":"+retDesc);
		window.opener.setStoreMainValue(storeGroup,retCode,retDesc);
	}else{
		//alert(currCondNo+":"+retCode+":"+retKey+":"+retDesc);
		window.opener.setStoreMainValue(storeGroup,retCode,retDesc);
	}
	window.close();
}

</script>

</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/searchCustomerPopupAction">
<input type="hidden" name="storeGroup" value="<%=storeGroup %>"/>

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px" class="txt1">
		<td width="15%" >&nbsp;</td> 
		<td width="90%" ><b>ร้านค้า กลุ่มร้านค้า <%=storeGroup %></b></td>
	</tr>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<input type="button" name="ok" value="    OK    " onclick="selectMultiple()"  class="newPosBtnLong"/>
			&nbsp;&nbsp;&nbsp;<input type="button" name="close" value="  Close  " onclick="javascript:window.close();"  class="newPosBtnLong"/>
		</td>
	</tr>
</table>

	  <table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="90%">
       <tr>
            <th width="15%">
            <input type ="checkbox" name="chCheckAll" id="chCheckAll" onclick="selectAll(this);"  />
            </th>
            <th width="15%">รหัสร้านค้า</th>
			<th width="40%">ชืื่อร้านค้า</th>
	   </tr>
	   <%
	   if(request.getAttribute("CUSTOMER_LIST") != null){
		   String tabclass ="lineE";
		   String checkedStr= "";
		   List<PopupForm> dataList = (List)request.getAttribute("CUSTOMER_LIST") ;
		   for(int i=0;i<dataList.size();i++){
			   PopupForm m = dataList.get(i);
			   if(i%2==0){ 
					tabclass="lineO";
				 }
			   if(Utils.stringInStringArr(m.getCode(),storeCodeArr.split("\\,") )){
				   checkedStr = "checked";
			   }else{
				   checkedStr = "";
			   }
	   %>
	     <tr class="<%=tabclass%>">
            <td >
           
             <input type ="checkbox" name="chCheck" id="chCheck" <%=checkedStr%>/>
		     <input type ="hidden" name="code_temp" value="<%=m.getCode() %>" />
		     <input type ="hidden" name="desc" value="<%=m.getDesc() %>" />
            </td>
            <td><%=m.getCode() %></td>
			<td align="left"><%=m.getDesc() %></td>
	   </tr>
	   <%}} %>
</table>			   
<!-- RESULT -->

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<input type="button" name="ok" value="    OK    " onclick="selectMultiple()"  class="newPosBtnLong"/>
			&nbsp;&nbsp;&nbsp;<input type="button" name="close" value="  Close  " onclick="javascript:window.close();"  class="newPosBtnLong"/>
		</td>
	</tr>
</table>

</html:form>
</body>
</html>