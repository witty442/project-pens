<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.bean.MCBean"%>
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>


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
<jsp:useBean id="mcTimeForm" class="com.isecinc.pens.web.mc.MCTimeForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");

/** For diable save or disableText **/
boolean readonly = false;

if(Utils.userInRole(user,new String[]{User.MCQUERY}) ){
	 readonly = true;
}

if(session.getAttribute("empTypeList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(MCDAO.searchMCRefList(new PopupForm(),"","StaffType"));
	
	session.setAttribute("empTypeList",billTypeList);
}
if(session.getAttribute("monthList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(MCDAO.searchMCRefList(new PopupForm(),"","Month"));
	
	session.setAttribute("monthList",billTypeList);
}

if(session.getAttribute("empStatusList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(MCDAO.searchMCRefList(new PopupForm(),"","EmpStatus"));
	
	session.setAttribute("empStatusList",billTypeList);
}

if(session.getAttribute("areaList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(MCDAO.searchMCRefList(new PopupForm(),"","MCarea"));
	
	session.setAttribute("areaList",billTypeList);
}

if(session.getAttribute("yearList") == null){
	List<PopupForm> yearList = new ArrayList();
	yearList.addAll(MCDAO.searchYearList(5));
		
	session.setAttribute("yearList",yearList);
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
	 
}
function clearForm(path){
	var form = document.mcTimeForm;
	form.action = path + "/jsp/mcTimeAction.do?do=clear";
	form.submit();
	return true;
}

function exportExcelGroup(path){
	var form = document.mcTimeForm;
	var param ="";
	
	if( $('#staffYear').val()==""){
		alert("กรุณาระบุ ปี");
		return false;
	}
	if( $('#staffMonth').val()==""){
		alert("กรุณาระบุ เดือน");
		return false;
	}else{
		 var month = $('#staffMonth').val();
		if(month >12){
			alert("กรุณาระบุ เดือนให้ถูกต้อง");
			return false;
		}
	}
	if( $('#mcArea').val()==""){
		alert("กรุณาระบุ เขตพื้นที่");
		return false;
	}
	form.action = path + "/jsp/mcTimeAction.do?do=exportExcelGroup"+param;
	form.submit();
	return true;
}

function search(path){
	var form = document.mcTimeForm;
	if( $('#staffYear').val()==""){
		alert("กรุณาระบุ ปี");
		return false;
	}
	if( $('#staffMonth').val()==""){
		alert("กรุณาระบุ เดือน");
		return false;
	}else{
		 var month = $('#staffMonth').val();
		if(month >12){
			alert("กรุณาระบุ เดือนให้ถูกต้อง");
			return false;
		}
	}
	if( $('#mcArea').val()==""){
		alert("กรุณาระบุ เขตพื้นที่");
		return false;
	}

	form.action = path + "/jsp/mcTimeAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function openNew(path,empRefId){
	var form = document.mcTimeForm;
    var param ="&empRefId="+empRefId;
        param +="&staffYear="+form.staffYear.value;
        param +="&staffMonth="+form.staffMonth.value;
        
	form.action = path + "/jsp/mcTimeAction.do?do=prepare&action=add"+param;
	form.submit();
	return true; 
}

function openEdit(path,empRefId){
	 var form = document.mcTimeForm;
	var param ="&empRefId="+empRefId;
	    param +="&staffYear="+form.staffYear.value;
	    param +="&staffMonth="+form.staffMonth.value;
	    
	form.action = path + "/jsp/mcTimeAction.do?do=prepare&action=edit"+param;
	form.submit();
	return true; 
}

function openPopupCustomer(path){
	var form = document.mcTimeForm;
	var mcArea = document.getElementsByName('bean.mcArea')[0].value;
	var mcRoute = document.getElementsByName('bean.mcRoute')[0].value;
	var staffType = document.getElementsByName('bean.staffType')[0].value;
	
    var param = "&mcArea="+mcArea;
        param += "&mcRoute="+mcRoute;
        param += "&staffType="+staffType;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepareSearchMC&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc){
	var form = document.mcEmpForm;
	//alert(form);
	form.staffId.value = code;
	form.name.value = desc;

	if(staffId==''){
	  alert("ไม่พบข้อมูล  staffId");
	  form.staffId.value = '';
	  form.name.value = "";
	}
} 

function getStaffNameKeypress(e,custCode){
	var form = document.mcEmpForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			form.staffId.value = '';
			form.name.value = "";
		}else{
		  getStaffName(custCode);
		}
	}
}

function getStaffName(custCode){
	var returnString = "";
	var form = document.mcEmpForm;
	var mcArea = document.getElementsByName('bean.mcArea')[0].value;
	var mcRoute = document.getElementsByName('bean.mcRoute')[0].value;
	var staffType = document.getElementsByName('bean.empType')[0].value;
	
    var param  = "mcArea="+mcArea;
        param += "&mcRoute="+mcRoute;
        param += "&custCode=" + custCode.value;
        param += "&staffType="+staffType;
        
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getStaffMCAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	if(returnString !=''){
		var retArr = returnString.split("|");
		form.name.value = retArr[0];
	}else{
		alert("ไม่พบข้อมูล");
		form.staffId.focus();
		form.staffId.value ="";
		form.name.value = "";
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
				<jsp:param name="function" value="mcTime"/>
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
						<html:form action="/jsp/mcTimeAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						      <tr>
                                    <td align="right"> ปี <font color="red">*</font></td>
									<td>		
										 
										 <html:select property="bean.staffYear" styleId="staffYear">
											<html:options collection="yearList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								   <td align="right"> เดือน<font color="red">*</font></td>
									<td>	
									    <html:select property="bean.staffMonth" styleId="staffMonth">
											<html:options collection="monthList" property="code" labelProperty="desc"/>
									    </html:select>	
										
									</td>
								</tr>
								<tr>
                                    <td align="right"> ภาค <font color="red">*</font></td>
									<td>		
										 <html:select property="bean.mcArea" styleId="mcArea" onchange="loadRoute();">
											<html:options collection="areaList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								   <td align="right"> ประเภท<font color="red"></font></td>
									<td>		
										 <html:select property="bean.empType" styleId="empType">
											<html:options collection="empTypeList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td align="right"> รหัสพนักงาน <font color="red"></font></td>
									<td>		
										 <html:text property="bean.empId" styleId="empId" size="20"/>
									</td>
								   <td align="right"> ชื่อพนักงาน<font color="red"></font></td>
									<td>		
										 <html:text property="bean.name" styleId="name" size="40"/>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<%-- <a href="javascript:newEmp('${pageContext.request.contextPath}')">
										  <input type="button" value="    เพิ่มรายการใหม่      " class="newPosBtnLong"> 
										</a> --%>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>
											
										<a href="javascript:exportExcelGroup('${pageContext.request.contextPath}')">
									     <input type="button" value="   ExportToExcel   " class="newPosBtnLong">
									    </a> 
														
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${mcTimeForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th >Employee ID</th>
									<th >Title</th>
									<th >Name</th>
									<th >Surname</th>
									<th >Mobile No 1</th>
									<th >Mobile No 2</th>
									<th >Type</th>
									<th >บันทึกเวลา</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<MCBean> resultList = mcTimeForm.getResultsSearch();
							
							for(int n=0;n<resultList.size();n++){
								MCBean mc = (MCBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>">
									 	<td class="td_text_center" width="6%"><%=mc.getEmpId()%></td>
										<td class="td_text" width="5%"><%=mc.getTitle()%></td>
										<td class="td_text" width="10%"><%=mc.getName()%></td>
									    <td class="td_text" width="10%"><%=mc.getSurName()%></td>
									    <td class="td_text" width="8%"><%=mc.getMobile1() %></td>
										<td class="td_text" width="8%"><%=mc.getMobile2() %></td>
										<td class="td_text_center" width="3%"><%=mc.getEmpTypeDesc()%></td>
										<td class="td_text_center" width="15%">
										<%if(mc.isStaffTimeExist()){
											if(readonly ==true){
											%>
											    <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getEmpRefId()%>')">
												   ดูข้อมูล
												 </a>
											<%}else{ %>
												 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getEmpRefId()%>')">
												             แก้ไข
												 </a>
											<%} %>
										<%}else{ 
										    if(readonly ==true){
											%>
											    <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getEmpRefId()%>')">
												   ยังไม่มีการบันทึกเวลา
												 </a>
											<%}else{ %>
												  <a href="javascript:openNew('${pageContext.request.contextPath}','<%=mc.getEmpRefId()%>')">
											                    เพิ่มข้อมูลใหม่
											      </a>
											<%} %>
										<%} %>
										</td>
									</tr>
							<%} %>
							 
					</table>
				</c:if>
				
		<!-- ************************Result ***************************************************-->
					
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