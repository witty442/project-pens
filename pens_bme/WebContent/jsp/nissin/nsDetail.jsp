<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.NSBean"%>
<%@page import="com.isecinc.pens.dao.NSDAO"%>
<%@page import="com.isecinc.pens.web.nissin.NSForm"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.pens.util.*"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="nsForm" class="com.isecinc.pens.web.nissin.NSForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
String role = user.getRole().getKey();

NSBean bean = ((NSForm)session.getAttribute("nsForm")).getBean();
System.out.println("Bean:"+bean.getChannelId());

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	//new Epoch('epoch_popup', 'th', document.getElementById('orderDate'));
	new Epoch('epoch_popup', 'th', document.getElementById('nissinOrderDate'));
	
	<%if( !"".equals(bean.getChannelId())) { %>
       document.getElementsByName('bean.channelId')[0].value = <%=bean.getChannelId()%>;
	   loadProvince();
	   document.getElementsByName('bean.provinceId')[0].value = <%=bean.getProvinceId()%>;
	<% } %>
}
function clearForm(path){
	var form = document.nsForm;
	form.action = path + "/jsp/nsAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.nsForm;
	form.action = path + "/jsp/nsAction.do?do=prepare2&action=back&page=nissin";
	form.submit();
	return true;
}

function save(path){
	var form = document.nsForm;
	if( $('#nissinOrderDate').val()==""){
		alert("��س��к� �ѹ��� Nissin �� Line ��");
		$('#nissinOrderDate').focus();
		return false;
	}
	if( $('#customerType').val()==""){
		alert("��س��к� ��������ҹ���");
		$('#customerType').focus();
		return false;
	}
	//alert($('#channelId').val());
	if( $('#channelId').val()==""){
		alert("��س��к� �Ҥ");
		$('#channelId').focus();
		return false;
	}
	if( $('#provinceId').val()==""){
		alert("��س��к� �ѧ��Ѵ");
		return false;
	}
	if( $('#customerName').val()==""){
		alert("��س��к� ��ҹ���");
		$('#customerName').focus();
		return false;
	}
	if( $('#orderDate').val()==""){
		alert("��س��к� �ѹ�պѹ�֡");
		$('#orderDate').focus();
		return false;
	}
	if( $('#phone').val()==""){
		alert("��س��к� �����Ţ���Ѿ��");
		$('#phone').focus();
		return false;
	}
	
	form.action = path + "/jsp/nsAction.do?do=save&action=newsearch";
	form.submit();
	return true;
}
function cancelAction(path){
	var form = document.nsForm;
	if(confirm("��س��׹�ѹ��� ¡��ԡ Authorize Return no ���")){
		form.action = path + "/jsp/nsAction.do?do=cancelAction";
		form.submit();
		return true;
	}
	return false;
}
function completeAction(path){
	var form = document.nsForm;
	if(confirm("��س��׹�ѹ��� Confirm ������  �����������ա����")){
		form.action = path + "/jsp/nsAction.do?do=completeAction";
		form.submit();
		return true;
	}
	return false;
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
				<jsp:param name="function" value="ns"/>
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
                                       <%if( !Utils.userInRole(user,new String[]{User.NISSINVIEW}) ){%>
		                                    <c:if test="${nsForm.bean.mode == 'add'}">
		                                        <font size="2"><b>��������������</b></font>
		                                     </c:if>
		                                     <c:if test="${nsForm.bean.mode == 'edit'}">
		                                         <font size="2"><b>��䢢����� </b></font>
		                                     </c:if>
		                               <%}else{ %>
		                                                                                                                            
		                               <%} %>
									</td>
								</tr>
							<tr>
                                <td align="right">ID</td>
								<td align="left">
								   <html:text property="bean.orderId" styleClass="disableText" styleId="orderId"/>
								    &nbsp;&nbsp;
								   �ѹ��� Nissin �� Line ��(Nissin Ordered date)<font color="red">*</font>&nbsp;
								   <html:text property="bean.nissinOrderDate" styleClass="" size='10' styleId="nissinOrderDate" readonly="true"/>
								</td>
								</tr>
						     <tr>
                                    <td align="right"> Date </td>
									<td align="left"><html:text property="bean.orderDate" styleClass="disableText" readonly="true" styleId="orderDate"/>
									
									    Catagory <font color="red">*</font>
										 <html:select property="bean.customerType" styleId="customerType" >
										    <html:option value=""></html:option>
											<html:option value="School">School</html:option>
										    <html:option value="Mini">Mini</html:option>
											<html:option value="Shop">Shop</html:option>
										   </html:select>
								    Region <font color="red">*</font>
										  <html:select property="bean.channelId" styleId="channelId" onchange="loadProvince();">
											<html:options collection="channelList" property="code" labelProperty="desc"/>
									     </html:select>
									    
									    Province <font color="red">*</font>
									      <html:select property="bean.provinceId" styleId="provinceId" >
									    </html:select> 
									    &nbsp;&nbsp;
									     Type <font color="red"></font>
									      <html:select property="bean.customerSubType" styleId="customerSubType" >
									        <html:options collection="customerSubTypeList" property="conValue" labelProperty="conDisp"/>
									    </html:select> 
									</td>
								</tr>
								<tr>
                                    <td  align="right"> Customer Name<font color="red">*</font>
									</td>
									<td align="left">
									    <html:text property="bean.customerName" styleClass="\" autoComplete=\"off" styleId="customerName"  size="150" maxlength="200"></html:text>
									</td>
								</tr>
								<tr>
                                    <td align="right"> Address Line1 
									</td>
									<td align="left">
									  <html:text property="bean.addressLine1" styleClass="\" autoComplete=\"off" styleId="addressLine1"  size="150" maxlength="200"></html:text>
									</td>
								</tr>
								<tr>
                                    <td  align="right"> Address Line2  
									</td>
									<td align="left">
									  <html:text property="bean.addressLine2" styleClass="\" autoComplete=\"off" styleId="addressLine2"  size="150" maxlength="200"></html:text>
									</td>
								</tr>
								<tr>
                                    <td  align="right"> Phone Number<font color="red">*</font>
									</td>
									<td align="left">
									  <html:text property="bean.phone" styleClass="\" autoComplete=\"off" styleId="phone" size="20"></html:text>
									</td>
								</tr>
								<tr>
                                    <td  align="right"> Remark1 </td>
									<td align="left">
									   <html:text property="bean.remark" styleClass="\" autoComplete=\"off" styleId="remark" size="150" maxlength="200"></html:text>
									</td>
								</tr>
								
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									    <%if( !Utils.userInRole(user,new String[]{User.NISSINVIEW}) ){%>
											<c:if test="${nsForm.bean.canSave == true}">
												<a href="javascript:save('${pageContext.request.contextPath}')">
												  <input type="button" value="    Save   " class="newPosBtnLong"> 
												</a>
											</c:if>
										<%} %>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   Close   " class="newPosBtnLong">
										</a>		
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<%-- <c:if test="${nsForm.bean.canCancel == true}">
											 <a href="javascript:cancelAction('${pageContext.request.contextPath}')">
											  <input type="button" value="   ¡��ԡ   " class="newPosBtnLong">
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