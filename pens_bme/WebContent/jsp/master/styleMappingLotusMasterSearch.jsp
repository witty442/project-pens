<%@page import="com.isecinc.pens.bean.StyleMappingLotusMasterBean"%>
<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.pens.util.*"%>
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
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<jsp:useBean id="styleMappingLotusMasterForm" class="com.isecinc.pens.web.master.StyleMappingLotusMasterForm" scope="session" />
<%%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript">
function loadMe(){
}
function clearForm(path){
	var form = document.styleMappingLotusMasterForm;
	form.action = path + "/jsp/styleMappingLotusMasterAction.do?do=clearSearch";
	form.submit();
	return true;
}
function search(path){
	var form = document.styleMappingLotusMasterForm;
	form.action = path + "/jsp/styleMappingLotusMasterAction.do?do=search";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.styleMappingLotusMasterForm;
	form.action = path + "/jsp/styleMappingLotusMasterAction.do?do=exportToExcel";
	form.submit();
	return true;
}
function back(path){
	var form = document.styleMappingLotusMasterForm;
	form.action = path + "/jsp/styleMappingLotusMasterAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}
function openEdit(path,styleNo,groupCode,pensItem){
	var form = document.styleMappingLotusMasterForm;
	var param  ="&styleNo="+styleNo;
	    param +="&groupCode="+groupCode;
	    param +="&pensItem="+pensItem;
	    param +="&mode=edit";
	form.action = path + "/jsp/styleMappingLotusMasterAction.do?do=searchDetail"+param;
	form.submit();
	return true;
}
function openAdd(path){
	var form = document.styleMappingLotusMasterForm;
	var param ="&mode=add";
	form.action = path + "/jsp/styleMappingLotusMasterAction.do?do=searchDetail"+param;
	form.submit();
	return true;
}
function openPopup(path,pageName,multipleCheck){
	var form = document.styleMappingLotusMasterForm;
	var param = "&pageName="+pageName;
	if("FIND_GroupCode_IN_StyleMappingLotus" == pageName){
        //param +="&groupCode="+form.groupCode.value; 
	}else if("FIND_StyleNo_IN_StyleMappingLotus" == pageName){
        //param +="&styleNo="+form.styleNo.value; 
	}else if("FIND_PensItem_IN_StyleMappingLotus" == pageName){
	    //param +="&styleNo="+form.styleNo.value; 
	 }
	
	param +="&multipleCheck="+multipleCheck;
	url = path + "/jsp/popupSearchAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.styleMappingLotusMasterForm;
	if("FIND_GroupCode_IN_StyleMappingLotus" == pageName){
		form.groupCode.value = code;
	}else if("FIND_StyleNo_IN_StyleMappingLotus" == pageName){
		form.styleNo.value = code;
	}else if("FIND_PensItem_IN_StyleMappingLotus" == pageName){
		form.pensItem.value = code;
	}
} 
</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="StyleMappingLotusMaster"/>
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
						<html:form action="/jsp/styleMappingLotusMasterAction">
						<jsp:include page="../error.jsp"/>
						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
							     <!-- <tr>
	                                 <td colspan="2" align="center"><font size="2"><b>ค้นหา Price List Master</b> </font> </td>		
								</tr> -->
								<tr>
                                    <td> Group Code</td>
									<td >
						               <html:text property="bean.groupCode" styleId="groupCode" size="20"/>
						               <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_GroupCode_IN_StyleMappingLotus','false')"/>   
									</td>
								</tr>	
								<tr>
                                    <td> Article</td>
									<td >
						               <html:text property="bean.styleNo" styleId="styleNo" size="20"/>
						               <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_StyleNo_IN_StyleMappingLotus','false')"/>   
									</td>
								</tr>	
								<tr>
                                    <td> Pens Item</td>
									<td >
						               <html:text property="bean.pensItem" styleId="pensItem" size="20"/>
						               <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_PensItem_IN_StyleMappingLotus','false')"/>   
									</td>
								</tr>	
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:openAdd('${pageContext.request.contextPath}')">
										  <input type="button" value="  เพิ่มรายการใหม่    " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>	
										<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
										  <input type="button" value="   Export   " class="newPosBtnLong">
										</a>					
									</td>
								</tr>
							</table>
					  </div>
					  
					 <c:if test="${styleMappingLotusMasterForm.results != null}">
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="50%">
					        <tr>
								<th >Group Code</th>
								<th >Article</th>
								<th >Pens Item</th>
								<th >แก้ไข </th>
							 </tr>
							<% 
							try{
							String tabclass ="lineE";
							List<StyleMappingLotusMasterBean> resultList = styleMappingLotusMasterForm.getResults();
							
							for(int n=0;n<resultList.size();n++){
								StyleMappingLotusMasterBean mc = (StyleMappingLotusMasterBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="8%"><%=mc.getGroupCode()%></td>
										<td class="td_text_center" width="8%"><%=mc.getStyleNo()%></td>
									    <td class="td_text_center" width="8%"><%=mc.getPensItem()%></td>
										<td class="td_text_center" width="10%">
										<%if(mc.isCanEdit()){ %>
											 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getStyleNo()%>','<%=mc.getGroupCode()%>','<%=mc.getPensItem()%>')">
											             แก้ไข
											 </a>
										<%} %>
										</td>
									</tr>
							<%}
							}catch(Exception e){
								e.printStackTrace();
							}
							%>
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