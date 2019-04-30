<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script>

function searchInPage(path){
	var form = document.summaryForm;
	var pensCustCodeFrom = form.pensCustCodeFrom;
	var pensItemFrom = form.pensItemFrom;
	var pensItemTo = form.pensItemTo;
	var group = form.group.value;
	
	if(pensCustCodeFrom.value =="" && pensItemFrom.value =="" && group.value ==""){
		alert("กรุณาระบุข้อมูลค้นหา อย่างน้อย 1 รายการ");
		return false;
	}
	/* 
    if(pensCustCodeFrom.value ==""){ 
		 alert("กรุณากรอกข้อมูลรหัสร้านค้า");
		 return false;
	 } 
      */
	form.action = path + "/jsp/summaryAction.do?do=search&page=onhandBigCOracle";
	form.submit();
	return true;
}
function setStoreMainValueOnhandBigCOracle(code,desc,subInv,types){
	var form = document.summaryForm;
	form.pensCustCodeFrom.value = code;
	form.pensCustNameFrom.value = desc;
	form.subInv.value = subInv;
}
</script>
<%
String storeType="bigc";
%>
 <table  border="0" cellpadding="3" cellspacing="0" class="body" width="65%">
     <tr>
		<td align="right"  width="10%" nowrap>รหัสร้านค้า<font color="red"></font>
		</td>
		<td align="left" width="60%"  nowrap> 
		   <html:text property="onhandSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="20" 
		    onkeypress="getCustNameWithSubInvKeypress('${pageContext.request.contextPath}',event,this,'bigc','pensCustNameFrom')"
		    styleClass="\" autoComplete=\"off"/>
		    <input type="button" name="x1" value="..." 
		    onclick="openPopupCustomerWithSubInv('${pageContext.request.contextPath}','from','<%=storeType%>','onhandBigCOracle')"/>
		    <html:text property="onhandSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="50"/>
		</td>
	</tr>
	<tr>
		<td align="right" width="10%">
		     Sub Inv&nbsp;&nbsp; 
		</td>
		<td align="left" width="60%">
		<html:text property="onhandSummary.subInv" styleId="subInv" styleClass="disableText" readonly="true"/>
		</td>
   </tr>
	<tr>
		<td align="right" width="10%">
		     Pens Item From
		</td>
		<td align="left" width="60%">
	        <html:text property="onhandSummary.pensItemFrom" styleId="pensItemFrom" styleClass="\" autoComplete=\"off"/>
		     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from')"/>
		     &nbsp;&nbsp;&nbsp;&nbsp;
		     Pens Item To&nbsp;&nbsp; 
		     <html:text property="onhandSummary.pensItemTo" styleId="pensItemTo" styleClass="\" autoComplete=\"off"/>
		     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to')"/>    
		</td>
	</tr>
	<tr>
		<td align="right" width="10%">Group </td>
		<td align="left" width="60%">
		     <html:text property="onhandSummary.group" styleId="group"  styleClass="\" autoComplete=\"off"/>
		    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}')"/>
		     <html:hidden property="onhandSummary.groupDesc" styleId="groupDesc" />
			</td>
		</tr>
</table>