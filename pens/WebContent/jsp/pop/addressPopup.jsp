<%@page import="util.SessionGen"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%
List<Province> provinces = new ArrayList<Province>();
Province pBlank = new Province();
pBlank.setId(0);
pBlank.setName("");
provinces.add(pBlank);
provinces.addAll(new MProvince().lookUp());

List<District> districts = new MDistrict().lookUp();

String type="";
String row="";
if(request.getParameter("row")!=null){
	row = request.getParameter("row");
}
if(request.getParameter("type")!=null){
	type = request.getParameter("type");
}
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@page import="com.isecinc.pens.model.MDistrict"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">

function loadMe(){
	if('<%=row%>'!='')
	{
		putData(<%=row%>);
	}else{
		changePV($("#province").val());
	}
	
	$("#line1").focus();
}

function putData(rowNo){
	rowNo--;
	
	//alert(rowNo);
	
	var cdoc = window.opener.document;
	var ids=cdoc.getElementsByName('addr.id')[rowNo];
	var rows=cdoc.getElementsByName('addr.row')[rowNo];
	var lines1=cdoc.getElementsByName('addr.line1')[rowNo];
	var lines2=cdoc.getElementsByName('addr.line2')[rowNo];
	var lines3=cdoc.getElementsByName('addr.line3')[rowNo];
	var districts=cdoc.getElementsByName('addr.district')[rowNo];
	var districtsLabel=cdoc.getElementsByName('addr.districtLabel')[rowNo];
	var provinces=cdoc.getElementsByName('addr.province')[rowNo];
	var provincesLabel=cdoc.getElementsByName('addr.provinceLabel')[rowNo];
	var postcodes=cdoc.getElementsByName('addr.postcode')[rowNo];
	var purposes=cdoc.getElementsByName('addr.purpose')[rowNo];
	var purposesLabel=cdoc.getElementsByName('addr.purposeLabel')[rowNo];
	var statuses=cdoc.getElementsByName('addr.status')[rowNo];
	var statusesLabel=cdoc.getElementsByName('addr.statusLabel')[rowNo];

	$("#line1").val(lines1.value);
	$("#line2").val(lines2.value);
	$("#line3").val(lines3.value);
	
	$("#province").val(provinces.value);	
	$("#postalCode").val(postcodes.value);
	$("#purpose").val(purposes.value);
	
	$("#addressId").val(ids.value);
	$("#addressRow").val(rowNo);

	changePV($("#province").val());

	$("#district").val(districts.value);

	if(statuses.value=='Y'){
		document.getElementById('status').checked = true;
	}else{
		document.getElementById('status').checked = false;
	}
}

function addRow(){
	if(Trim($("#line1").val())==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		$("#line1").focus();
		return false;
	}
	if(Trim($("#line3").val())=='')
	{
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		$("#line3").focus();
		return false;
	}
	if(Trim($("#postalCode").val())=='')
	{
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		$("#postalCode").focus();
		return false;
	}

	// check number at postal code.
	if(!checkNum($("#postalCode"))){return false;}
	
	var address = new Object();
	address.line1 = $("#line1").val();
	address.line2 = $("#line2").val();
	address.line3 = $("#line3").val();
	address.district = $("#district").val();
	address.districtLabel = $("#district option:selected").text();
	address.province = $("#province").val();
	address.provinceLabel = $("#province option:selected").text();
	address.postcode = $("#postalCode").val();
	address.purpose = $("#purpose").val();
	address.purposeLabel = $("#purpose option:selected").text();
	if($("#status").attr('checked')){
		address.status = 'Y';
		address.statusLabel = '<bean:message key="Active" bundle="sysprop"/>';
	}else{
		address.status = 'N';
		address.statusLabel = '<bean:message key="Inactive" bundle="sysprop"/>';
	}
	
	var type = '<%=type%>'; 
	if(type=='copy'){
		address.row = '';
		address.id = 0;
	}else{
		address.row = $("#addressRow").val();
		address.id = $("#addressId").val();
	}
	
	if(address.row==''){
		window.opener.addAddress('${pageContext.request.contextPath}',address);
	}else{
		window.opener.setValueToAddress('${pageContext.request.contextPath}',address);
	}
	window.close();
}

//change pv
function changePV(pvid){
	$("#district").html("");
	<%for(District d : districts){%>
	if(pvid == <%=d.getProvinceId()%>){
    	$("<option value=<%=d.getId()%>><%=d.getName()%></option>").appendTo("#district");
	}
	<%}%>
}

</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="ที่อยู่"/>
</jsp:include>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="40%">&nbsp;</td>
		<td></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Address" bundle="sysele"/><font color="red">*</font></td>
		<td align="left"><input type="text" id="line1" name="line1" size="40"/></td>
	</tr>
	<tr>
		<td align="right"></td>
		<td align="left"><input type="text" id="line2" name="line2" size="40"/></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Address.Line3" bundle="sysele"/><font color="red">*</font></td>
		<td align="left"><input type="text" id="line3" name="line3" size="15"/></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Address.Province" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<select id="province" name="province" onchange="changePV(this.value);">
				<%for(Province p : provinces){ %>
				<option value="<%=p.getId() %>"><%=p.getName() %></option>
				<%} %>
			</select>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Address.Line4" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<select id="district" name="district">
			</select>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Address.PostalCode" bundle="sysele"/><font color="red">*</font></td>
		<td align="left"><input type="text" id="postalCode" name="postalCode" maxlength="5" size="10" onkeydown="return inputNum(event);"/></td>
	
	</tr>
	<tr>
		<td align="right"><bean:message key="Address.Purpose" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<select id="purpose" name="purpose" disabled>
				<option value="S">Ship To</option>
				<option value="B">Bill To</option>
			</select>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left"><input id="status" type="checkbox" checked="checked" disabled/>&nbsp;<bean:message key="Active" bundle="sysprop"/></td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td align="center" colspan="2">
			<a href="#" onclick="addRow();">
<!--			<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" value="บันทึก" class="newPosBtn">
			
			<!-- <a href="#" onclick="deleteRow();">
			<input type="button" value="ลบข้อมูลนี้" class="newPosBtn">
			</a> -->
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn">
			</a>
		</td>
	</tr>
</table>
<input type="hidden" id="addressId" name="addressId">
<input type="hidden" id="addressRow" name="addressRow">
<br/>
</body>
</html>