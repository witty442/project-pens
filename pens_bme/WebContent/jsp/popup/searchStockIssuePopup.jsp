<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/popup_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />

<style type="text/css">

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>""></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>""></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>""></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>

<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="request" />
<%
  String status = Utils.isNull(request.getParameter("status"));
  String mode = Utils.isNull(request.getParameter("mode"));
%>
<script type="text/javascript">

function searchPopup(path, type) {
    document.popupForm.action = path + "/jsp/searchStockIssuePopupAction.do?do=search";
    document.popupForm.submit();
   return true;
}

function selectOneRadio(){
	var chRadio = document.getElementsByName("chRadio");
	
	var code = document.getElementsByName("code");
	var storeCode = document.getElementsByName("storeCode");
	var storeName = document.getElementsByName("storeName");
	var custGroup = document.getElementsByName("custGroup");
	var issueReqDate = document.getElementsByName("issueReqDate");
	var wareHouse = document.getElementsByName("wareHouse");
	var requestor = document.getElementsByName("requestor");
	var remark = document.getElementsByName("remark");
	var totalReqQty = document.getElementsByName("totalReqQty");
	var totalQty = document.getElementsByName("totalQty");
	
	for(var i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
        	//alert(i+":"+code[i+1].value);
            //window.opener.setStoreMainValue(code[i].value,storeCode[i].value,storeName[i].value,wareHouse[i].value,requestor[i].value,issueReqDate[i].value,custGroup[i].value);
            
             window.opener.setStoreMainValue(
            		code[i].value,storeCode[i].value,storeName[i].value
            		,wareHouse[i].value,requestor[i].value,issueReqDate[i].value
            		,custGroup[i].value,remark[i].value,totalReqQty[i].value
            		,totalQty[i].value); 
        	window.close();
            break;
        }
	}
}

</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/searchStockIssuePopupAction">
<input type="hidden" name ="status" value="<%=status %>" />
<input type="hidden" name ="mode" value="<%=mode %>" />

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px" class="txt1">
		<td width="25%" >&nbsp;</td>
		<td width="75%" ><b>ค้นหาข้อมูล Stock Issue</b></td>
	</tr>
	 <tr>
		<td  class="txt1"> Warehouse </td>
		<td >					
		<html:select property="descSearch" styleId="descSearch">
		    <html:options collection="warehouseList_scan" property="key" labelProperty="name"/>
		</html:select>
	</td>
    </tr>
	<tr height="21px" class="txt1">
		<td width="20%" ><b>Issue Request No.</b>  </td>
		<td width="80%" ><html:text property="codeSearch" size="20" style="height:20px"/>
		<input type="button" name="search" value="Search" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
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
<display:table style="width:100%;" id="item" name="requestScope.JOB_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
    	
    	
    <display:column style="text-align:center;" title="เลือกข้อมูล"  sortable="false" class="chk">

		<input type ="radio" name="chRadio" />
		<input type ="hidden" name="code" id="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="storeCode" id="storeCode" value="<bean:write name="item" property="storeCode"/>" />
		<input type ="hidden" name="storeName" id="storeName" value="<bean:write name="item" property="storeName"/>" />
		<input type ="hidden" name="custGroup" id="custGroup" value="<bean:write name="item" property="custGroup"/>" />
		<input type ="hidden" name="issueReqDate" id="issueReqDate" value="<bean:write name="item" property="issueReqDate"/>" />
		<input type ="hidden" name="wareHouse" id="wareHouse" value="<bean:write name="item" property="wareHouse"/>" />
		<input type ="hidden" name="requestor" id="requestor" value="<bean:write name="item" property="requestor"/>" />
		<input type ="hidden" name="remark" id="remark" value="<bean:write name="item" property="remark"/>" />
		<input type ="hidden" name="totalReqQty" id="totalReqQty" value="<bean:write name="item" property="totalReqQty"/>" />
		<input type ="hidden" name="totalQty" id="totalQty" value="<bean:write name="item" property="totalQty"/>" />
	 </display:column>
	 
    <display:column title="คลัง" property="wareHouse" sortable="false" class="code"/>						    
    <display:column title="IssueReqNo" property="code"   sortable="false" class="code"/>
    <display:column title="custGroup" property="custGroup"  sortable="false" class="desc"/>
    <display:column title="Store Code" property="storeCode" sortable="false" class="code"/>
    <display:column title="Store Name" property="storeName"  sortable="false" class="desc"/>
							
</display:table>	
<!-- RESULT -->



</html:form>
</body>
</html>