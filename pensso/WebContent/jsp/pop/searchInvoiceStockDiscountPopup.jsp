<%@page import="com.pens.util.SIdUtils"%>
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
<%
String index = request.getParameter("index");
String productCode = request.getParameter("productCode");
String userId = request.getParameter("userId");
String customerCode = request.getParameter("customerCode");
String uom2 = request.getParameter("uom2");
String requestNumber = request.getParameter("requestNumber");
System.out.println("requestNumber:"+requestNumber);
%>
<html>
<head>
<title></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/popup_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/field_size.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="request" />

<script type="text/javascript">

function searchPopup(path, type) {
    document.popupForm.action = path + "/jsp/popupAction.do?do=search";
    document.popupForm.submit();
   return true;
}
function selectOneRadio(){
	var chRadio = document.getElementsByName("chRadio");
	var code = document.getElementsByName("code");
	var priAllQty = document.getElementsByName("priAllQty");
	var priQty = document.getElementsByName("priQty");
	var subQty = document.getElementsByName("subQty");
	var price = document.getElementsByName("price");
	var remainAmount = document.getElementsByName("remainAmount");
	for(var i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
        	//alert(i+":"+code[i+1].value);
        	//window.close();
            window.opener.setInvoiceValue(code[i].value,priAllQty[i].value,priQty[i].value,subQty[i].value,price[i].value,remainAmount[i].value,<%=index%>);
        	setTimeout("window.close()",400);
            break;
        }
	}
}
</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/popupAction">
<html:hidden property="page" value="INVOICE_STOCK_DISCOUNT"/>
<input type="hidden" name="index" value="<%=index %>" />
<input type="hidden" name="productCode" value="<%=productCode %>" />
<input type="hidden" name="userId" value="<%=userId %>" />
<input type="hidden" name="customerCode" value="<%=customerCode %>" />
<input type="hidden" name="requestNumber" value="<%=requestNumber %>" />
<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" class="tableHead">
    <tr height="21px" class="headTitle">
		<td width="100%" colspan="2" align="center"><b>ค้นหาข้อมูล จำนวนหีบคงเหลือในบิล (ใบอนุมัติให้ส่วนลดร้านค้า)</b></td>
	</tr>
	<tr height="21px" class="txt1">
		<td width="100%" colspan="2">รหัสร้านค้า : <%=customerCode %>
		&nbsp; รหัสสินค้า :<%=productCode %>
		</td>
	</tr>
	<tr height="21px" class="txt1">
		<td width="15%" ><b>Invoice No</b>  </td>
		<td width="90%" ><html:text property="codeSearch"  size="30" style="height:20px"/>
		<input type="button" name="search" value="  Search  " class="newPosBtnLong"
		onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
	<%-- <tr height="21px" class="txt1">
		<td ><b>รายละเอียด</b></td>
		<td ><html:text property="descSearch"  size="60" style="height:20px"/></td>
	</tr> --%>
</table>
 
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="left">
			<input type="button" name="ok" value="  OK  " onclick="selectOneRadio()" style="width:70px;"  class="newPosBtnLong"/>
			<input type="button" name="close" value=" Close " onclick="javascript:window.close();" style="width:70px;"  class="newPosBtnLong"/>
		</td>
	</tr>
</table>
<!-- RESULT -->
<display:table  id="item" name="requestScope.INVOICE_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="../jsp/popupAction.do?do=search" sort="list" pagesize="20" class="resultDisp">	
    	
    <display:column  title="เลือกข้อมูล"   sortable="false" class="chk">
		<input type ="radio" name="chRadio" />
		<input type ="hidden" name="code" id="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="priAllQty" id="priAllQty" value="<bean:write name="item" property="priAllQty"/>" />
		<input type ="hidden" name="priQty" id="priQty" value="<bean:write name="item" property="priQty"/>" />
		<input type ="hidden" name="subQty" id="subQty" value="<bean:write name="item" property="subQty"/>" />
		<input type ="hidden" name="price" id="price" value="<bean:write name="item" property="price"/>" />
		<input type ="hidden" name="remainAmount" id="remainAmount" value="<bean:write name="item" property="remainAmount"/>" />
	 </display:column>
    											    
    <display:column  title="Invoice No" property="code"   sortable="false" class="code"/>
    <%if( !Utils.isNull(productCode).equals("")) {%>
	<display:column  title="จำนวนหีบ คงเหลือในบิล" property="priAllQty"  sortable="false" class="desc"/>	
	<display:column  title="เต็ม" property="priQty"  sortable="false" class="desc"/>	
	<display:column  title="เศษ" property="subQty"  sortable="false" class="desc"/>	
	<%} %>
   	<display:column  title="จำนวนเงิน คงเหลือใน Invoice" property="remainAmount"  sortable="false" class="desc"/>		
</display:table>	
<!-- RESULT -->

</html:form>
</body>
</html>