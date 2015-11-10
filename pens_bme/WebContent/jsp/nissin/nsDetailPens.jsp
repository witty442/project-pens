<%@page import="com.isecinc.pens.dao.RTDAO"%>
<%@page import="com.isecinc.pens.bean.RTBean"%>
<%@page import="com.isecinc.pens.web.nissin.NSForm"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="nsForm" class="com.isecinc.pens.web.nissin.NSForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}

span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}

fieldset
{
    border: 1px solid #ddd;
    padding: 0 1.4em 1.4em 1.4em;
    margin: 0 0 1.5em 0;
    border-radius: 8px;
    margin: 0 5px;
    height: 180px;
}


</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('invoiceDate'));
	
	document.getElementsByName('bean.channelId')[0].value = ${nsForm.bean.channelId};
	loadProvince();
	document.getElementsByName('bean.provinceId')[0].value = ${nsForm.bean.provinceId};
}
function loadProvince(){
	var cboDistrict = document.getElementsByName('bean.provinceId')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/provinceListBoxAjax.jsp",
			data : "channelId=" + document.getElementsByName('bean.channelId')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}
function clearForm(path){
	var form = document.nsForm;
	form.action = path + "/jsp/nsAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.nsForm;
	form.action = path + "/jsp/nsAction.do?do=prepare2&action=back&page=pens";
	form.submit();
	return true;
}

function savePens(path){
	var form = document.nsForm;

	
	if( $('#status').val()=="P"){
		if( $('#customerCode').val() !="" || $('#saleCode').val() !="" || $('#invoiceDate').val() !=""){
			alert("มีการระบุข้อมูล รหัสร้านค้า รหัส Sale วันที่ Invoice Date ไม่สามารถ เปลี่ยนสถานะเป็น PENDING ได้");
			return false;
		}
		if( $('#pendingReason').val() ==""){
			alert("กรุณาระบุ เหตุผลในการ Pending");
			$('#pendingReason').focus();
			return false;
		}
		if(confirm("กรุณายืนยันการ PENDING ข้อมูล  ")){
			form.action = path + "/jsp/nsAction.do?do=savePens&action=pending";
			form.submit();
			return true;
		}
		
	}else{
		if( $('#customerCode').val()==""){
			alert("กรุณาระบุ รหัสร้านค้า");
			$('#customerCode').focus();
			return false;
		}
		if( $('#saleCode').val()==""){
			alert("กรุณาระบุ รหัส SaleCode");
			$('#saleCode').focus();
			return false;
		}
		if( $('#invoiceDate').val()==""){
			alert("กรุณาระบุ Invoice Date");
			$('#invoiceDate').focus();
			return false;
		}
		if(confirm("กรุณายืนยันการ บันทึกข้อมูล")){
			form.action = path + "/jsp/nsAction.do?do=savePens&action=newsearch";
			form.submit();
			return true;
		}
	}
	
	return false
}
function cancelAction(path){
	var form = document.nsForm;
	if(confirm("กรุณายืนยันการ ยกเลิก Authorize Return no นี้")){
		form.action = path + "/jsp/nsAction.do?do=cancelAction";
		form.submit();
		return true;
	}
	return false;
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
	    
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="nsPens"/>
			</jsp:include>
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
						<html:form action="/jsp/nsAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0">
						       <tr>
                                    <td  align="center" colspan="2">
                                    <c:if test="${nsForm.bean.mode == 'add'}">
                                        <b>เพิ่มรายการใหม่</b>
                                     </c:if>
                                     <c:if test="${nsForm.bean.mode == 'edit'}">
                                         <b>แก้ไขรายการ  </b>
                                     </c:if>
									</td>
								</tr>
							<tr>
                                 <td align="right"> 
                                     ID
                                 </td>
								<td align="left">
								   <html:text property="bean.orderId" styleClass="disableText" styleId="orderId"/>
								</td>
							</tr>
						     <tr>
                                    <td align="right"> วันที่บันทึก
                                    </td>
									<td align="left"><html:text property="bean.orderDate" styleClass="disableText" styleId="orderDate"></html:text>
									    ประเภท
										 <html:select property="bean.customerType" styleId="customerType" disabled="true" >
										    <html:option value=""></html:option>
											<html:option value="School">School</html:option>
										    <html:option value="Mini">Mini</html:option>
											<html:option value="Shop">Shop</html:option>
										   </html:select>
										      ภาค 
										  <html:select property="bean.channelId" styleId="channelId" disabled="true">
											<html:options collection="channelList" property="code" labelProperty="desc"/>
									    </html:select>
									    
									    จังหวัด  
									      <html:select property="bean.provinceId" styleId="provinceId" disabled="true">
									    </html:select> 
									</td>
								</tr>
								<tr>
                                    <td  align="right"> ชื่อร้านค้า <font color="red">*</font>
									</td>
									<td align="left">
									    <html:text property="bean.customerName" styleClass="normalText" styleId="customerName"  size="150" maxlength="200"></html:text>
									</td>
								</tr>
								<tr>
                                    <td align="right"> ที่อยู่ Line1 
									</td>
									<td align="left">
									  <html:text property="bean.addressLine1" styleClass="normalText" styleId="addressLine1"  size="150" maxlength="200"></html:text>
									</td>
								</tr>
								<tr>
                                    <td  align="right"> ที่อยู่ Line2  
									</td>
									<td align="left">
									  <html:text property="bean.addressLine2" styleClass="normalText" styleId="addressLine2"  size="150" maxlength="200"></html:text>
									</td>
								</tr>
								<tr>
                                    <td  align="right"> เบอร์โทรศัพท์
									</td>
									<td align="left">
									  <html:text property="bean.phone" styleClass="disableText" styleId="phone" size="20"></html:text>
									  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
									    &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
									    &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;
									
									   
									</td>
								</tr>
								<tr>
                                    <td  align="right"> Remark1 </td>
									<td align="left">
									   <html:text property="bean.remark" styleClass="disableText" styleId="remark" size="60" maxlength="200"></html:text>
									   &nbsp; &nbsp;
									  
									   
									</td>
								</tr>
								 <tr>
                                    <td  align="center" colspan="2"><hr> </td>
								</tr> 
								 
								<tr>
                                    <td  align="left" colspan="2">
                                      
	                                     <table align="left" border="0" cellpadding="3" cellspacing="2" >
	                                       <tr>
	                                         <td>
	                                          <fieldset>
		                                      <table align="left" border="0" cellpadding="3" cellspacing="2" >
		                                         <tr><td>
				                                                                                                                                    รหัสร้านค้า<font color="red">*</font>  
													   <html:text property="bean.customerCode" styleClass="" styleId="customerCode" size="20" readonly="" ></html:text>
													       รหัส Sale <font color="red">*</font> 
													    <html:text property="bean.saleCode" styleClass="" styleId="saleCode" size="20" ></html:text>
												 </td></tr>
												 <tr><td>
												   
												   Invoice Date<font color="red">*</font> 
												   <html:text property="bean.invoiceDate" styleClass="" styleId="invoiceDate" size="20" readonly="true" ></html:text>
												   Invoice No
												   <html:text property="bean.invoiceNo" styleClass="" styleId="invoiceNo" size="30" maxlength="30"></html:text>
												    </td></tr>
											   </table>
											   
		                                      <table align="left" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="50%">
			                                    <tr>
			                                      <th colspan="2">CUP 72</th>
			                                      <th colspan="2">ซอง</th>
			                                      <th colspan="2">POOH 72</th>
			                                    </tr>
			                                    <tr>
			                                      <th>หีบ</th>
			                                      <th>ถ้วย</th>
			                                      <th>หีบ</th>
			                                      <th>ซอง</th>
			                                      <th>หีบ</th>
			                                      <th>ถ้วย</th>
			                                    </tr>
			                                    <tr>
			                                       <td><html:text property="bean.cupQty" styleClass="" styleId="cupQty" size="10" onkeydown="return inputNum(event);"/> </td>
			                                       <td><html:text property="bean.cupNQty" styleClass="" styleId="cupNQty" size="10" onkeydown="return inputNum(event);"/> </td>
			                                       <td><html:text property="bean.pacQty" styleClass="" styleId="pacQty" size="10" onkeydown="return inputNum(event);"/> </td>
			                                       <td><html:text property="bean.pacNQty" styleClass="" styleId="pacNQty" size="10" onkeydown="return inputNum(event);"/> </td>
			                                       <td><html:text property="bean.poohQty" styleClass="" styleId="poohQty" size="10" onkeydown="return inputNum(event);"/> </td>
			                                       <td><html:text property="bean.poohNQty" styleClass="" styleId="poohNQty" size="10" onkeydown="return inputNum(event);"/> </td>
			                                    </tr>
		                                      </table>
		                                    </fieldset>
		                                </td>
		                                 
	                                    <td align="right">
	                                       <fieldset >
			                                    <table align="right" border="0" cellpadding="3" cellspacing="2" >
				                                    <tr>
				                                       <td>เปลี่ยนสถานะเป็น PENDING 
					                                       <html:select property="bean.status" styleId="status" >
					                                          <html:option value=""></html:option>
					                                       	   <html:option value="P">PENDING</html:option>
														   </html:select></td>
				                                    </tr>
				                                    <tr>
				                                       <td nowrap>เหตุผลที่ PENDING
				                                        <html:text property="bean.pendingReason" styleClass="" styleId="pendingReason" size="60" maxlength="200"></html:text>
				                                       </td>
				                                      
				                                    </tr>
			                                    </table>
		                                     </fieldset>
	                                     </td>
	                                    </tr>
                                     </table>
                                   
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<c:if test="${nsForm.bean.canSave == true}">
											<a href="javascript:savePens('${pageContext.request.contextPath}')">
											  <input type="button" value="    บันทึก   " class="newPosBtnLong"> 
											</a>
										</c:if>
										
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>		
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<%-- <c:if test="${nsForm.bean.canCancel == true}">
											 <a href="javascript:cancelAction('${pageContext.request.contextPath}')">
											  <input type="button" value="   ยกเลิก   " class="newPosBtnLong">
											</a>		
										</c:if>	 --%>		
									</td>
								</tr>
							</table>
					  </div>
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
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