<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%
String row="";
if(request.getParameter("row")!=null)
{
	row = request.getParameter("row");
}
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
function loadMe(){
	if('<%=row%>'!='')
	{
		putData(<%=row%>);
	}
	
	$("#contactTo").focus();
}

function putData(rowNo){
	rowNo--;
	
	var cdoc = window.opener.document;
	var ids=cdoc.getElementsByName('cont.id')[rowNo];
	var rows=cdoc.getElementsByName('cont.row')[rowNo];
	var contacts=cdoc.getElementsByName('cont.contactTo')[rowNo];
	var relations=cdoc.getElementsByName('cont.relation')[rowNo];
	var phones=cdoc.getElementsByName('cont.phone')[rowNo];
	var faxs=cdoc.getElementsByName('cont.fax')[rowNo];
	var statuses=cdoc.getElementsByName('cont.status')[rowNo];
	var statusesLabel=cdoc.getElementsByName('cont.statusLabel')[rowNo];

	var phones2=cdoc.getElementsByName('cont.phone2')[rowNo];
	var mobiles=cdoc.getElementsByName('cont.mobile')[rowNo];
	var mobiles2=cdoc.getElementsByName('cont.mobile2')[rowNo];
	
	//sub phone
	var phoneSub1=cdoc.getElementsByName('cont.phoneSub1')[rowNo];
	var phoneSub2=cdoc.getElementsByName('cont.phoneSub2')[rowNo];
	

	$("#contactTo").val(contacts.value);
	$("#relation").val(relations.value);
	// set phone
	$("#phone").val(phones.value);
	if(phones.value!=''){
		$("#phone_1").val(phones.value);
	}
	// set phone2
	$("#phone2").val(phones2.value);
	if(phones2.value!=''){
		$("#phone_2").val(phones2.value);
	}
	// set mobile
	$("#mobile").val(mobiles.value);
	if(mobiles.value!=''){
		$("#mobile_1").val(mobiles.value);
	}
	// set mobile2
	$("#mobile2").val(mobiles2.value);
	if(mobiles2.value!=''){
		$("#mobile_2").val(mobiles2.value);
	}
	// set fax
	$("#fax").val(faxs.value);
	if(faxs.value!=''){
		$("#fax_1").val(faxs.value);
	}
	$("#contactId").val(ids.value);
	$("#contactRow").val(rowNo);

	//sub phone
	$("#phoneSub1").val(phoneSub1.value);
	$("#phoneSub2").val(phoneSub2.value);
	
    //status checked == true alway
	document.getElementById('status').checked = true;
	
}

function set_phone(){
	$('#phone').val($('#phone_1').val());
	$('#phone2').val($('#phone_2').val());
	$('#mobile').val($('#mobile_1').val());
	$('#mobile2').val($('#mobile_2').val());
	$('#fax').val($('#fax_1').val() );
	return true;
}

function addRow(){
	$("#phone_1").val(jQuery.trim($("#phone_1").val())); 
	$("#phone_2").val(jQuery.trim($("#phone_2").val()));
	$("#mobile_1").val(jQuery.trim($("#mobile_1").val())); 
	$("#mobile_2").val(jQuery.trim($("#mobile_2").val()));
	$("#fax_1").val(jQuery.trim($("#fax_1").val())); 
	
	if(!set_phone()){
		return false;
	} 
	
	if(jQuery.trim($("#contactTo").val())==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		$("#contactTo").focus();
		return false;
	}

	var phone = jQuery.trim($("#phone").val()); 
	var phone2 = jQuery.trim($("#phone2").val());
	var mobile = jQuery.trim($("#mobile").val()); 
	var mobile2 = jQuery.trim($("#mobile2").val());

	if(phone+phone2+mobile+mobile2==''){
		alert('กรุณาระบุโทรศัพท์อย่างน้อย 1 เบอร์');
		$("#phone_1").focus();
		return false;
	}
	
	var fax = jQuery.trim($("#fax").val()); 
	var contact = new Object();
	contact.contactTo = $("#contactTo").val();
	contact.relation = $("#relation").val();
	contact.phone = $("#phone").val();
	contact.fax = $("#fax").val();
	if($("#status").attr('checked')){
		contact.status = 'Y';
		contact.statusLabel = '<bean:message key="Active" bundle="sysprop"/>';
	}else{
		contact.status = 'N';
		contact.statusLabel = '<bean:message key="Inactive" bundle="sysprop"/>';
	}
	if($("#contactId").val()==0){
		contact.id = 0;
	}else{
		contact.id = $("#contactId").val();
	}
	contact.row = $("#contactRow").val();

	contact.phone2 = $("#phone2").val();
	contact.mobile = $("#mobile").val();
	contact.mobile2 = $("#mobile2").val();

	//sub phone
	contact.phoneSub1 = $("#phoneSub1").val();
	contact.phoneSub2 = $("#phoneSub2").val();

	if(contact.row==''){
		window.opener.addContact('${pageContext.request.contextPath}',contact);
	}else{
		window.opener.setValueToContact('${pageContext.request.contextPath}',contact);
	}
	window.close();
}

function validNumber(e,obj){
	var r = false;
	if(obj.value != '' && e != null && e.keyCode == 13) {
		if(obj =='phone_1'){
		   $('#phoneSub1').focus();	
		}else{
		   $('#phoneSub2').focus();	
		}
		r = true;
	}else{
	    r = true;
	}
	return r;
}

</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="ผู้ติดต่อ"/>
</jsp:include>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="45%">&nbsp;</td>
		<td></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Contact.Name" bundle="sysele"/><font color="red">*</font></td>
		<td align="left"><input id="contactTo" name="contactTo" type="text" size="26"/></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Contact.Relation" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left"><input id="relation" name="relation" type="text" size="26"/></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Contact.Phone" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input id="phone_1" name="phone_1" type="text" maxlength="25" size="15" onkeydown="return validNumber(event,'phone_1');">ต่อ
			<input id="phone" name="phone" type="hidden"/>
	
			<input id="phoneSub1" name="phoneSub1" type="text" maxlength="3" size="3" />
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Contact.Phone" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input id="phone_2" name="phone_2" type="text" maxlength="25" size="15" onkeydown="return validNumber(event,'phone_2');" >ต่อ
			<input id="phone2" name="phone2" type="hidden"/>
		
			<input id="phoneSub2" name="phoneSub2" maxlength="3" size="3" />
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Contact.Mobile" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input id="mobile_1" name="mobile_1" type="text" maxlength="25" size="15">
			<input id="mobile" name="mobile" type="hidden"/>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Contact.Mobile" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input id="mobile_2" name="mobile_2" type="text" maxlength="25" size="15">
			<input id="mobile2" name="mobile2" type="hidden"/>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Contact.Fax" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input id="fax_1" name="fax_1" type="text" maxlength="25" size="15">
			<input id="fax" name="fax" type="hidden"/>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left"><input id="status" name="status" type="checkbox" checked="checked" disabled/>&nbsp;<bean:message key="Active" bundle="sysprop"/></td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td align="center" colspan="2">
			<a href="#" onclick="addRow();">
<!--			<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" value="บันทึก" class="newPosBtn">
			</a>
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn">
			</a>
		</td>
	</tr>
</table>
<input type="hidden" id="contactId" name="contactId">
<input type="hidden" id="contactRow" name="contactRow">
<br/>
</body>
</html>