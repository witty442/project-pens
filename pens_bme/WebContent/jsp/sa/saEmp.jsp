<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.isecinc.pens.bean.SAEmpBean"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>

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
<jsp:useBean id="saEmpForm" class="com.isecinc.pens.web.sa.SAEmpForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
String mode = saEmpForm.getMode();
SAEmpBean bean = saEmpForm.getBean();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	<%if("add".equals(mode)){%>
	    //new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	<%}else{%>
	    setStyletextField();
	 <%}%>
	   new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	   new Epoch('epoch_popup', 'th', document.getElementById('leaveDate'));
	   new Epoch('epoch_popup', 'th', document.getElementById('startRewardBmeDate'));
	   new Epoch('epoch_popup', 'th', document.getElementById('startRewardWacoalDate'));
	   new Epoch('epoch_popup', 'th', document.getElementById('startSuretyBondDate'));
}
function setStyletextField(){
	document.getElementById('empId').className= "disableText";
	document.getElementById('empId').readOnly= true;
	//document.getElementById('startDate').className= "disableText";
}
function clearForm(path){
	var form = document.saEmpForm;
	form.action = path + "/jsp/saEmpAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.saEmpForm;
	form.action = path + "/jsp/saEmpAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	
	var form = document.saEmpForm;
	if( $('#empId').val()==""){
		alert("กรุณาระบุ  Employee ID");
		$('#empId').focus();
		return false;
	}else{
		var empIdObj  = $('#empId').val();
		//alert(empIdObj.length);
		if( empIdObj.length !=6){
			alert("กรุณาระบุ  Employee ID ให้ครบ 6 ตำแหน่ง");
			$('#empId').focus();
			return false;
		}
	}
	if( $('#name').val()==""){
		alert("กรุณาระบุ  Name");
		$('#name').focus();
		return false;
	}
	if( $('#surName').val()==""){
		alert("กรุณาระบุ  surName");
		$('#surName').focus();
		return false;
	}
	
	if( $('#startDate').val()==""){
		alert("กรุณาระบุ  Start Working Date");
		$('#startDate').focus();
		return false;
	}
	if( $('#rewardBme').val()!="" && $('#startRewardBmeDate').val()==""){
		alert("กรุณาใส่วันที่เริ่มต้นคิด ค่าเฝ้าตู้ B'me");
		$('#startRewardBmeDate').focus();
		return false;
	}
	if( $('#rewardWacoal').val()!="" && $('#startRewardWacoalDate').val()==""){
		alert("กรุณาใส่วันที่เริ่มต้นคิด ค่าเฝ้าตู้  Wacoal");
		$('#startRewardWacoalDate').focus();
		return false;
	}
	if( $('#suretyBond').val()!="" && $('#startSuretyBondDate').val()==""){
		alert("กรุณาใส่วันที่เริ่มต้นคิด  Surety Bond");
		$('#startSuretyBondDate').focus();
		return false;
	}
	
	//validate leaveDate
	if( $('#leaveDate').val() !=""){
		if( !validateDate($('#startDate').val(),$('#leaveDate').val())){
			alert("กรุณากรอกวันที่ leave date มากกว่า หรือ เท่ากับวันที่  start working date");
			$('#leaveDate').val("");
			$('#leaveDate').focus();
			return false;
		}
	}
	
	form.action = path + "/jsp/saEmpAction.do?do=save&action=newsearch";
	form.submit();
	return true;
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

function isNum(obj){
	//alert("isNum");
	  if(obj.value != ""){
		if(isNaN(obj.value)){
			alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
			obj.value = "";
			obj.focus();
			return false;
		}else{return true;}
	   }
	  return true;
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
				<jsp:param name="function" value="saEmp"/>
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
						<html:form action="/jsp/saEmpAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
									<td colspan="6" align="center">	
									   <font size="3" ><b>
										 <%if("add".equals(saEmpForm.getMode())){ %>
										   เพิ่มข้อมูลพนักงานใหม่
										 <%}else{ %>
										 แก้ไขข้อมูลพนักงาน
										 <%} %>
										 </b>
										 </font>	
									</td>
									
								</tr>
								<tr>
                                    <td align="right">Employee ID<font color="red">*</font></td>
									<td>	
									     <html:hidden property="bean.orgEmpId"/> 
										 <html:text property="bean.empId" styleId="empId" size="30" maxlength="6"> </html:text>
									</td>
									 <td  align="right">รหัสร้านค้าใน Oracle <font color="red"></font></td>
									<td>		
										 <html:text property="bean.oracleRefId" styleId="oracleRefId" size="30"> </html:text>
									</td>
									<td  align="right">Type<font color="red"></font></td>    
									<td>
									     <html:select property="bean.empType" styleId="empType">
											<html:options collection="empTypeList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td  align="right">Name<font color="red">*</font></td>
									<td>		
										  <html:text property="bean.name" styleId="name" size="30"> </html:text>
									</td>
									 <td  align="right">Surname <font color="red">*</font></td>
									<td>		
										  <html:text property="bean.surName" styleId="surName" size="30"> </html:text>
									</td>
									<td  align="right"><font color="red"></font></td>    
									<td></td>
								</tr>
								<tr>
                                    <td  align="right">Mobile No<font color="red"></font></td>
									<td>		
										 <html:text property="bean.mobile" styleId="mobile" size="30"> </html:text>
									</td>
									 <td  align="right">Email<font color="red"></font></td>
									<td>		
										  <html:text property="bean.email" styleId="email" size="30"> </html:text>
									</td>
									<td  align="right"><font color="red"></font></td>    
									<td></td>
								</tr>
								<tr>
                                    <td  align="right">Bank Account<font color="red"></font></td>
									<td>		
										 <html:text property="bean.bankAccount" styleId="bankAccount" size="30"> </html:text>
									</td>
									 <td  align="right">ID Card<font color="red"></font></td>
									<td>		
										  <html:text property="bean.idCard" styleId="idCard" size="30"> </html:text>
									</td>
									<td  align="right"></td>    
									<td>   
									</td>
								</tr>
								<tr>
                                    <td  align="right">Region<font color="red"></font></td>
									<td>	
									     <html:select property="bean.region" styleId="region">
											<html:options collection="empRegionList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
									 <td  align="right">Group Store<font color="red"></font></td>
									<td>		
									     <html:select property="bean.groupStore" styleId="region">
											<html:options collection="groupStoreList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
									<td  align="right">Branch<font color="red"></font></td>    
									<td>
									    <html:text property="bean.branch" styleId="branch" size="30"> </html:text>
									</td>
								</tr>
								<tr>
                                    <td  align="right">Start Working Date<font color="red">*</font></td>
                                    
									<td>		
										 <html:text property="bean.startDate" styleId="startDate" size="30"  readonly="true"> </html:text>
									</td>
									 <td  align="right">Leave Date<font color="red"></font></td>
									<td align="right">		
										  <html:text property="bean.leaveDate" styleId="leaveDate" size="30"> </html:text>
									</td>
									 <td  align="right">Leave Reason<font color="red"></font></td>
									<td align="right">		
										  <html:text property="bean.leaveReason" styleId="leaveReason" size="50"> </html:text>
									</td>
								</tr>
								<tr>
                                    <td  align="right">ค่าเฝ้าตู้ b'me /เดือน<font color="red"></font></td>
									<td>		
										 <html:text property="bean.rewardBme" styleId="rewardBme" size="30" onblur="isNum(this)" styleClass="enableNumber"> </html:text>
									</td>
									 <td  align="right">วันที่เริ่มให้ ค่าเฝ้าตู้ b'me<font color="red"></font></td>
									<td align="right">		
										  <html:text property="bean.startRewardBmeDate" styleId="startRewardBmeDate" size="30"  readonly="true"> </html:text>
									</td>
									 <td  align="right"></td>
									<td align="right">	
									</td>
								</tr>
								<tr>
                                    <td  align="right">ค่าเฝ้าตู้ wacoal /เดือน<font color="red"></font></td>
									<td>		
										 <html:text property="bean.rewardWacoal" styleId="rewardWacoal" size="30" onblur="isNum(this)"  styleClass="enableNumber"> </html:text>
									</td>
									 <td  align="right">วันที่เริ่มให้ ค่าเฝ้าตู้ wacoal<font color="red"></font></td>
									<td align="right">		
										  <html:text property="bean.startRewardWacoalDate" styleId="startRewardWacoalDate" size="30"  readonly="true"> </html:text>
									</td>
									 <td  align="right"></td>
									<td align="right">		
									</td>
								</tr>
								<tr>
                                    <td  align="right">Surety Bond/เดือน<font color="red"></font></td>
									<td>		
										 <html:text property="bean.suretyBond" styleId="suretyBond" size="30" onblur="isNum(this)"  styleClass="enableNumber"> </html:text>
									</td>
									 <td  align="right">วันที่เริ่มให้ Surety Bond<font color="red"></font></td>
									<td align="right">		
										  <html:text property="bean.startSuretyBondDate" styleId="startSuretyBondDate" size="30" readonly="true"> </html:text>
									</td>
									 <td  align="right"></td>
									<td align="right">	</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
							            <c:if test="${saEmpForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="    บันทึก   " class="newPosBtnLong"> 
											</a>
										
											<a href="javascript:clearForm('${pageContext.request.contextPath}')">
											  <input type="button" value="   Clear   " class="newPosBtnLong">
											</a>
										</c:if>
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