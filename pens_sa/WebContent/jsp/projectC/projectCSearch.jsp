
<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="com.isecinc.pens.web.projectc.ProjectCBean"%>
<%@page import="com.pens.util.PageVisit"%>
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
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "projectCForm");

String pageName = Utils.isNull(request.getParameter("pageName")); 

/** Count Visit Page */
PageVisit.processPageVisit(request,"ProjectC");

%>
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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/projectc_google_maps.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<%-- <script async defer src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>" type="text/javascript"></script>
 --%>
<script type="text/javascript">
function loadMe(){
}
function clearForm(path){
	var form = document.projectCForm;
	form.action = path + "/jsp/projectCAction.do?do=prepareSearch&action=new";
	form.submit();
	return true;
}
function search(path){
	var form = document.projectCForm;
	if( form.storeCode.value ==""&& form.storeCode.value==""){
		alert("กรุณาระบุ ร้านค้า");
		form.storeCode.focus();
		return false;
	}
	form.action = path + "/jsp/projectCAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function openCheckStock(path,branchId){
	var form = document.projectCForm;
	var storeCode = form.storeCode.value;
	var param = "&storeCode="+storeCode;
	    param += "&branchId="+branchId;
	    param += "&action=newsearch";
	form.action = path + "/jsp/projectCAction.do?do=prepareCheckStock"+param;
	form.submit();
	return true;
}
function setLocationStoreValue(index,lat,lng){
	document.getElementsByName("store_lat")[index].value = lat;
	document.getElementsByName("store_long")[index].value = lng;
}
</script>
</head>		
<!-- <body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;"> -->
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
			                 <td> ร้านค้า<font color="red">*</font></td>
							<td>			
							     <html:text property="bean.storeCode" styleId="storeCode" size="15"
							     styleClass="\" autoComplete=\"off"
							     onkeypress="getAutoKeypress(event,this,'CustomerProjectC')"
                                 onblur="getAutoOnblur(event,this,'CustomerProjectC')"/> 
							     <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerProjectC')"/>   
							      <html:text property="bean.storeName" styleId="storeName" size="35"/>    
							 </td>
						</tr>
					    </table>
					     <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									<a href="javascript:search('${pageContext.request.contextPath}')">
									  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
									</a>&nbsp;
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>			
								</td>
							</tr>
						</table>
					 	<!-- ************************Result *************-->
					 <c:if test="${projectCForm.resultsSearch != null}">
				       
							<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="1" class="tableSearch">
							       <tr>
										<th >รหัสสาขา</th>
										<th >ชื่อสาขา</th>
										<th >ประเภท</th>
										<th >Action</th>
										<th >ที่อยู่</th>
										<th >อำเภอ</th>
										<th >จังหวัด</th>
										<th >มีประวัติ การตรวจนับ</th>
										<!-- <th> กำหนดพิกัด</th>
										<th> พิกัดร้านค้า</th> -->				
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
											<td class="td_text_center" width="2%"><%=item.getBranchId() %></td>
											<td class="td_text_center" width="10%">
											   <%=item.getBranchName() %>
											</td>
											<td class="td_text_center" width="3%">
											   <%=item.getBranchSize() %>
											</td>
											<td class="td_text_center" width="7%">
												  <a href="javascript:openCheckStock('${pageContext.request.contextPath}', '<%=item.getBranchId()%>','view')">
												      <b>เข้าบันทึก</b>
												  </a>
											</td>
											<td class="td_text_center" width="10%"><%=item.getAddress() %></td>
											<td class="td_text" width="10%"> <%=item.getAmphor() %></td>
											<td class="td_text_center" width="7%">
												 <%=item.getProvince() %>
											</td>
											<td class="td_text_center" width="3%">
												 <%if(item.isFoundCheck()){ %>
										               <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									             <%} %>
											</td>
											<%-- <td class="td_text_center" width="4%">
												  <a href="javascript:getLocation('${pageContext.request.contextPath}',<%=n %> 
												  ,'<%=item.getStoreCode()%>','<%=item.getStoreName()%>'
												  ,'<%=item.getBranchId()%>','<%=item.getBranchName()%>')">
												      แก้ไขพิกัดจุด
												  </a>
											</td>
											<td class="td_text_center" width="4%">
											     <input type="text" name ="storeLat" value="<%=item.getStoreLat() %>"/>
											     <input type="text" name ="storeLong" value="<%=item.getStoreLong() %>"/>
												  <a href="javascript:gotoMap('${pageContext.request.contextPath}',<%=n %> 
												  ,'<%=item.getStoreCode()%>','<%=item.getStoreName()%>'
												  ,'<%=item.getBranchId()%>','<%=item.getBranchName()%>')">
												      แสดงตำแหน่ง
												  </a>
											</td> --%>
										</tr>
								<%}//for %> 
						     </table>	
				          </c:if>		

					 	</div>
					 	
					 	<!-- INPUT HIDDEN -->
					 	<input type="hidden" name="pageName" value="<%=pageName %>"/>
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