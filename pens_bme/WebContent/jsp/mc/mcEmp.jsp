<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.bean.MCBean"%>
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
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
<jsp:useBean id="mcEmpForm" class="com.isecinc.pens.web.mc.MCEmpForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");

if(session.getAttribute("empTitleList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(MCDAO.searchMCRefList(new PopupForm(),"","Title"));
	
	session.setAttribute("empTitleList",billTypeList);
}

if(session.getAttribute("empRegionList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(MCDAO.searchMCRefList(new PopupForm(),"","MCarea"));
	
	session.setAttribute("empRegionList",billTypeList);
}

if(session.getAttribute("reasonLeaveList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(MCDAO.searchMCRefList(new PopupForm(),"","LeaveReason"));
	
	session.setAttribute("reasonLeaveList",billTypeList);
}
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

.day {
  width: 14%;
}
.holiday {
  width: 14%;
  background-color: #F78181;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	   new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	   new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
}
function clearForm(path){
	var form = document.mcEmpForm;
	form.action = path + "/jsp/mcEmpAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.mcEmpForm;
	form.action = path + "/jsp/mcEmpAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.mcEmpForm;
	if( $('#empType').val()==""){
		alert("กรุณาระบุ  Type");
		return false;
	}
	if( $('#status').val()==""){
		alert("กรุณาระบุ Status");
		return false;
	}
	if( $('#title').val()==""){
		alert("กรุณาระบุ  Title");
		return false;
	}
	if( $('#name').val()==""){
		alert("กรุณาระบุ  Name");
		return false;
	}
	if( $('#surName').val()==""){
		alert("กรุณาระบุ  surName");
		return false;
	}
	if( $('#mobile1').val()==""){
		alert("กรุณาระบุ  Mobile No 1");
		return false;
	}
	if( $('#region').val()==""){
		alert("กรุณาระบุ  Region");
		return false;
	}
	if( $('#startDate').val()==""){
		alert("กรุณาระบุ  Start Date");
		return false;
	}
	form.action = path + "/jsp/mcEmpAction.do?do=save&action=newsearch";
	form.submit();
	return true;
}

function genDummyStaffID(){
	var returnString = "";
	var form = document.mcEmpForm;
	
    var param  = "";
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/genDummyStaffIdAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	form.staffId.value = returnString;
}

function validateDate(DateFrom, DateTo){
	if(DateFrom=='' || DateTo==''){return true;}
	DateFrom = DateFrom.split("/");
	starttime = new Date(DateFrom[2],DateFrom[1]-1,DateFrom[0]);

	DateTo = DateTo.split("/");
	endtime = new Date(DateTo[2],DateTo[1]-1,DateTo[0]);
	
	if(starttime.getTime() > endtime.getTime()){
		return false;
	}else{
		return true;
	}
}

</script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerMC.jsp"/></td>
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
				<jsp:param name="function" value="mcEmp"/>
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
						<html:form action="/jsp/mcEmpAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
									<td colspan="6" align="center">	
									   <font size="3" ><b>
										 <%if("add".equals(mcEmpForm.getMode())){ %>
										   เพิ่มข้อมูลพนักงานใหม่
										 <%}else{ %>
										 แก้ไขขู้มูลพนักงาน
										 <%} %>
										 </b>
										 </font>	
									</td>
									
								</tr>
								<tr>
                                    <td align="right">Employee ID<font color="red"></font></td>
									<td>	
									     <html:hidden property="bean.orgEmpId"/> 
										 <html:text property="bean.empId" styleId="empId" size="20"> </html:text>
									</td>
									 <td  align="right"> Type<font color="red">*</font></td>
									<td>		
										 <html:select property="bean.empType" styleId="empType">
											<html:options collection="empTypeList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
									<td  align="right">Status<font color="red">*</font></td>    
									<td>
									     <html:select property="bean.status" styleId="status">
											<html:options collection="empStatusList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td  align="right">Title<font color="red">*</font></td>
									<td>		
										 <html:select property="bean.title" styleId="title">
											<html:options collection="empTitleList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
									 <td  align="right"> Name<font color="red">*</font></td>
									<td>		
										  <html:text property="bean.name" styleId="name" size="20"> </html:text>
									</td>
									<td  align="right">Surname<font color="red">*</font></td>    
									<td>
									    <html:text property="bean.surName" styleId="surName" size="20"> </html:text>
									</td>
								</tr>
								<tr>
                                    <td  align="right">Mobile No 1<font color="red">*</font></td>
									<td>		
										 <html:text property="bean.mobile1" styleId="mobile1" size="20"> </html:text>
									</td>
									 <td  align="right">Mobile No 2<font color="red"></font></td>
									<td>		
										  <html:text property="bean.mobile2" styleId="mobile2" size="20"> </html:text>
									</td>
									<td  align="right">Region<font color="red">*</font></td>    
									<td>
									   
									     <html:select property="bean.region" styleId="region">
											<html:options collection="empRegionList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td  align="right">Start Date<font color="red">*</font></td>
									<td>		
										 <html:text property="bean.startDate" styleId="startDate" size="20" readonly="true"> </html:text>
									</td>
									 <td  align="right">End Date<font color="red"></font></td>
									<td colspan ="3">		
										  <html:text property="bean.endDate" styleId="endDate" size="20"> </html:text>
									</td>
									
								</tr>
								<tr>
                                    <td  align="right">Reason Leave<font color="red"></font></td>
									<td colspan="5">	
										   <html:select property="bean.reasonLeave" styleId="title">
											<html:options collection="reasonLeaveList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
									
								</tr>
								<tr>
                                    <td  align="right">Note<font color="red"></font></td>
									<td colspan="5">		
										 <html:text property="bean.note" styleId="note" size="80"> </html:text>
									</td>
									
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
							
										<a href="javascript:save('${pageContext.request.contextPath}')">
										  <input type="button" value="    บันทึก   " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>							
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