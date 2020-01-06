<%@page import="com.isecinc.pens.web.projectc.ProjectCBean"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="projectCForm" class="com.isecinc.pens.web.projectc.ProjectCForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/projectC.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript">
function loadMe(){
}
function backForm(path){
	var form = document.projectCForm;
	form.action = path + "/jsp/projectCAction.do?do=prepareSearch&action=back";
	form.submit();
	return true;
}

function gotoPage(path,currPage){

	var form = document.projectCForm;
	var param  = "&storeCode="+storeCode;
        param += "&branchId="+branchId;
        param += "&currPage="+currPage;
	form.action = path + "/jsp/projectCAction.do?do=prepareCheckStock"+param;
    form.submit();
    return true;
}
function prepareCheckStockDetailView(path,checkDate,id,action){
	var form = document.projectCForm;
	var param ="&action="+action;
	    param +="&checkDate="+checkDate;
	    param +="&branchId="+form.branchId.value;
	    param +="&storeCode="+form.storeCode.value;
	    param +="&idx="+id;
	form.action = path + "/jsp/projectCAction.do?do=prepareCheckStockDetail"+param;
	form.submit();
	return true;
}

function prepareCheckStockDetailNew(path,checkDate,action){
	var form = document.projectCForm;
	var param ="&action="+action;
	    param +="&checkDate="+checkDate;
	    param +="&branchId="+form.branchId.value;
	    param +="&storeCode="+form.storeCode.value;
	form.action = path + "/jsp/projectCAction.do?do=prepareCheckStockDetail"+param;
	form.submit();
	return true;
}
</script>
</head>		
<body onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_login2.png','${pageContext.request.contextPath}/images2/button_forgotpwd2.png')" topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0">

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
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
				<jsp:param name="function" value="ProjectC"/>
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
						<html:form action="/jsp/projectCAction">
						<jsp:include page="../error.jsp"/>
						<div align="center">
						   	<!--  Criteria -->
						 <table align="center" border="0" cellpadding="3" cellspacing="0" >
						  <tr>
			                 <td> วันที่ตรวจนับ<font color="red">*</font></td>
							 <td>			
							     <html:text property="bean.checkDate" styleId="checkDate" size="15" styleClass="disableText"
							     readonly="true"/> 
							 </td>
						   </tr>
							<tr>
			                 <td> ร้านค้า<font color="red">*</font></td>
							 <td>			
							     <html:text property="bean.storeCode" styleId="storeCode" size="15"
                                 styleClass="disableText" readonly="true"/>  
							     <%-- <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerProjectC')"/>    --%>
							      <html:text property="bean.storeName" styleId="storeName" size="35" styleClass="disableText" readonly="true"/>    
							 </td>
						   </tr>
						   <tr>
			                 <td> สาขา<font color="red">*</font></td>
							 <td>			
							     <html:text property="bean.branchId" styleId="branchId" size="15"
                                 styleClass="disableText" readonly="true"/>  
							    <%--  <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','BranchProjectC')"/>    --%>
							     <html:text property="bean.branchName" styleId="branchName" size="35" styleClass="disableText" readonly="true"/>    
							 </td>
						   </tr>
					     </table>
					     <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									
									 <a href="javascript:prepareCheckStockDetailNew('${pageContext.request.contextPath}', '<%=projectCForm.getBean().getCheckDate()%>','new')">
									  <input type="button" value="เข้าทำการบันทึกตรวจนับ " class="newPosBtnLong">
									</a>
									
									<a href="javascript:backForm('${pageContext.request.contextPath}')">
									  <input type="button" value="ปิดหน้าจอ" class="newPosBtnLong">
									</a>			
								</td>
							</tr>
						</table>
						</div>
					 	<!-- ************************Result *************-->
					<div align="left"><font size="2"><b>ประวัติการตรวจเช็ค</b></font></div>
					 <%if(projectCForm.getResultsSearch() !=null){ %>
					  
				        <jsp:include page="../pageing_new.jsp">
					       <jsp:param name="totalPage" value="<%=projectCForm.getTotalPage() %>"/>
					       <jsp:param name="totalRecord" value="<%=projectCForm.getTotalRecord() %>"/>
					       <jsp:param name="currPage" value="<%=projectCForm.getCurrPage() %>"/>
					       <jsp:param name="startRec" value="<%=projectCForm.getStartRec() %>"/>
					       <jsp:param name="endRec" value="<%=projectCForm.getEndRec() %>"/>
				         </jsp:include> 
							<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="1" class="tableSearch">
							       <tr>
										<th >วันที่บันทึก</th>
										<th >ผู้บันทึก</th>
										<!-- <th >พิกัดบันทึก</th> -->
										<th >หมายเหตุ</th>
										<th >รายละอียด</th>	
								   </tr>
								<% 
								String tabclass ="";
								List<ProjectCBean> resultList = projectCForm.getResultsSearch();
								for(int n=0;n<resultList.size();n++){
									ProjectCBean item = (ProjectCBean)resultList.get(n);
									if(n%2==0){ 
										tabclass="lineO";
									}else{
										tabclass ="lineE";
									}
									%>
										<tr class="<%=tabclass%>">
											<td class="td_text_center" width="5%"><%=item.getCheckDate() %></td>
											<td class="td_text_center" width="10%">
											   <%=item.getCheckUser() %>
											</td>
											<%-- <td class="td_text_center" width="10%">
											   <%=item.getChkLatitude() %>,<%=item.getChkLongitude() %>
											</td> --%>
											<td class="td_text_center" width="10%"><%=item.getRemark() %></td>
											<td class="td_text_center" width="4%">
												  <a href="javascript:prepareCheckStockDetailView('${pageContext.request.contextPath}'
												  , '<%=item.getCheckDate()%>',<%=item.getId()%>,'view')">
												      แสดงรายละเอียด
												  </a>
											</td>
										</tr>
								<%}//for %>
						     </table>	
				          <%}else{ %>
				            <div align="left">ไม่พบข้อมูล</div>
				          <%} %>	
					 	
					 	
					 		<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
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