<%@page import="com.isecinc.pens.web.customer.CustomerHelper"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="customerNissinForm" class="com.isecinc.pens.web.customernissin.CustomerNissinForm" scope="request" />
<%
String action = (String)request.getParameter("action");
String role = ((User)session.getAttribute("user")).getType();
User user = (User)session.getAttribute("user");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="-1" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
    loadProvince();
	<%if( !"".equals(Utils.isNull(customerNissinForm.getCustomer().getProvinceId()))){ %>
	   document.getElementsByName('customer.provinceId')[0].value = ${customerNissinForm.customer.provinceId};
	<%}%>
	
	loadDistrict();
	<%if( !"".equals(Utils.isNull(customerNissinForm.getCustomer().getDistrictId()))){ %>
	  document.getElementsByName('customer.districtId')[0].value = <%=customerNissinForm.getCustomer().getDistrictId()%>;
	<% } %>
}
function loadProvince(){
	//notInProvinceId=178:Myanmar
	var cboProvince = document.getElementsByName('customer.provinceId')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/ProvinceTerritory.jsp",
			data : "refId=-1&notInProvinceId=178",
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboProvince.innerHTML=returnString;
			}
		}).responseText;
	});
}
function loadDistrict(){
	var cboDistrict = document.getElementsByName('customer.districtId')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/DistrictAjax.jsp",
			data : "refId=" + document.getElementsByName('customer.provinceId')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}
function save(path){
	var form = document.customerNissinForm;
	
	if( $('#customerName').val()==""){
		alert("กรุณาระบุ ร้านค้า");
		$('#customerName').focus();
		return false;
	}
	if( $('#provinceId').val()==""){
		alert("กรุณาระบุ จังหวัด");
		$('#provinceId').focus();
		return false;
	}
	if( $('#districtId').val()==""){
		alert("กรุณาระบุ อำเภอ");
		$('#districtId').focus();
		return false;
	}
	
	if( $('#customerType').val()==""){
		alert("กรุณาระบุ ประเภทร้านค้า");
		$('#customerType').focus();
		return false;
	}

	form.action = path + "/jsp/customerNissinAction.do?do=save";
	form.submit();
	return true;
}
function clearForm(path){
	var form = document.customerNissinForm;
	form.action = path + "/jsp/customerNissinAction.do?do=prepare&action=new";
	form.submit();
}
function backSearch(path){
	var form = document.customerNissinForm;
	form.action = path + "/jsp/customerNissinAction.do?do=search&action=back&rf=Y";
	form.submit();
}
function addNewOrder(path){
	var form = document.customerNissinForm;
	var customerId = document.getElementById("customerId").value;
	if(customerId==""|| customerId=="0"){
		alert("กรุณาบันทึกข้อมูลร้านค้าก่อน จะไปบันทึกคำสั้งซื้อ");
		return false;
	}
	form.action = path + "/jsp/orderNissinAction.do?do=viewDetail&action=new&fromPage=customerNissin&customerId="+customerId;
	form.submit();
	return true;
}
function gotoOrderNissinSearch(path){
	var form = document.customerNissinForm;
	var customerId = document.getElementById("customerId").value;
	if(customerId==""|| customerId=="0"){
		alert("กรุณาบันทึกข้อมูลร้านค้าก่อน จะดู ประวัติรายการขาย");
		return false;
	}
	form.action = path + "/jsp/orderNissinAction.do?do=searchHead&action=newsearch&fromPage=customerNissinDetail&shortCustId="+customerId;
	form.submit();
	return true;
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerSP.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	    	<%if(customerNissinForm.getCustomer().getId() != 0.0){ %>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="NissinCustomer"/>
				</jsp:include>
			<%}else{ %>
			    <jsp:include page="../program.jsp">
					<jsp:param name="function" value="NewNissinCustomer"/>
				</jsp:include>
			<%} %>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/customerNissinAction"  enctype="multipart/form-data">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td colspan="6"></td>
							</tr>
						    <tr>
								<td align="right" width="20%" nowrap>ชื่อร้านค้า<font color="red">*</font></td>
								<td align="left" colspan="3"  width="20%">
									<html:text property="customer.name" size="70" styleId="customerName"  styleClass="\" autoComplete=\"off"/>
								</td>
								<td align="right" width="15%" nowrap>Ref.Cust ID&nbsp;&nbsp;</td>
								<td align="left" width="40%">
									<html:text property="customer.id" size="25" styleId="customerId"
									styleClass="disableText" readonly="true"/>
								</td>
							</tr>
							<tr>
								<td align="right" nowrap>เลขที่/ซอย<font color="red"></font></td>
								<td align="left">
									<html:text property="customer.addressLine1" size="25" styleId="addressLine1"  styleClass="\" autoComplete=\"off"/>
								</td>
								<td align="right">ถนน&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.addressLine2" size="25"  styleClass="\" autoComplete=\"off"/>
								</td>
								<td align="right" nowrap>แขวง/ตำบล<font color="red"></font></td>
								<td align="left">
									<html:text property="customer.addressLine3" size="25"  styleClass="\" autoComplete=\"off"/>
								</td>
							</tr>
							<tr>
								<td align="right">จังหวัด<font color="red">*</font></td>
								<td align="left">
									<html:select property="customer.provinceId" styleId="provinceId" onchange="loadDistrict();">
								   </html:select>
								</td>
								<td align="right">อำเภอ<font color="red">*</font></td>
								<td align="left"  colspan="3">
									<html:select property="customer.districtId" styleId="districtId">
								   </html:select>
								</td>
							</tr>
							<tr>
								<td align="right"  nowrap>เบอร์โทรติดต่อร้านค้า<font color="red"></font></td>
								<td align="left" >
									<html:text property="customer.mobile" size="25" styleId="mobile"  styleClass="\" autoComplete=\"off"/>
								</td>
								<td align="right" nowrap>ประเภทร้านค้า<font color="red">*</font></td>
								<td align="left"  colspan="3">
									<html:select property="customer.customerType" >
									    <html:option value=""></html:option>
										<html:option value="School">School</html:option>
									    <html:option value="Mini">Mini</html:option>
										<html:option value="Shop">Shop</html:option>
									</html:select>
								</td>
							</tr>
							<tr>
								<td></td>
								<td colspan="6"><hr></td>
							</tr>
							
						</table>
						<br />
						
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
							  <%if(customerNissinForm.getCustomer().isCanEdit()) {%>
							      <td align="center" width="80%">
								  <a href="#" onclick="return save('${pageContext.request.contextPath}');">
								    <input type="button" value="บันทึก" class="newPosBtn">
								  </a>
							      
									<a href="#" onclick="clearForm('${pageContext.request.contextPath}');">			
									  <input type="button" value="เคลียร์" class="newNegBtn">
									</a>
									
									</td>
									<td align="right" width="20%">
										 <a href="#" onclick="addNewOrder('${pageContext.request.contextPath}')">
							                 <b>ไปหน้าบันทึกสั่งซื้อ</b> <img src="${pageContext.request.contextPath}/images2/b_order.png" width="32" height="32" border="0" class="newPicBtn">
							             </a> 
						             </td>
								<%} %>
							</tr>
						</table>
							<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						<tr>
						     <td align="left">
								<a href="#" onclick="gotoOrderNissinSearch('${pageContext.request.contextPath}')">
									<input type="button" value="ดูประวัติ รายการขาย ร้านนี้" class="newNegBtn">
								</a>
						    </td>
							<td align="right">
								<a href="#" onclick="backSearch('${pageContext.request.contextPath}')">
								<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
								</a>
							</td>
							<td width="10%">&nbsp;</td>
						</tr>
					</table>
					
						<html:hidden property="customer.exported" value="N"/>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						<div title="Customer">..</div>
						</html:form>
						<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>