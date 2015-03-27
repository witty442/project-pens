<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="masterForm" class="com.isecinc.pens.web.master.MasterForm" scope="session" />
<%
ImportDAO importDAO = new ImportDAO();
if(session.getAttribute("referenceCodeList") ==null){
   List<References> importList = importDAO.getReferenceCodeList();
   session.setAttribute("referenceCodeList",importList);
}

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />


<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	
}

function search(path){
	var form = document.masterForm;
	form.action = path + "/jsp/masterAction.do?do=search&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.masterForm;
	form.action = path + "/jsp/masterAction.do?do=prepare&action=new&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
}
function addMaster(path,action){
	var w = 500;
	var h = 300;
	var left = (screen.width/2)-(w/2);
    var top = (screen.height/2)-(h/2);
    var param = "action="+action;
	url = path + "/jsp/popup/masterPopup.jsp?"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width="+w+",height="+h+",status=no,width="+w+", height="+h+", top="+top+", left="+left);
	
}
function actionMaster(path,action,interfaceValue,interfaceDesc,pensValue,pensDesc,pensDesc2,pensDesc3,sequence,status){
	var w = 500;
	var h = 400;
	var left = (screen.width/2)-(w/2);
    var top = (screen.height/2)-(h/2);
    
    var param ="referenceCode="+$("#referenceCode option:selected").text();
    param +="&interfaceValue="+interfaceValue;
    param +="&interfaceDesc="+interfaceDesc;
    param +="&pensValue="+pensValue;
    param +="&pensDesc="+pensDesc;
    param +="&pensDesc2="+pensDesc2;
    param +="&pensDesc3="+pensDesc3;
    param +="&sequence="+sequence;
    param +="&status="+status;
    param +="&action="+action;
    
	url = path + "/jsp/popup/masterPopup.jsp?"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width="+w+",height="+h+",status=no,width="+w+", height="+h+", top="+top+", left="+left);
	
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
	    	<%if("master".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="MaintainMaster"/>
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
						<html:form action="/jsp/masterAction">
						<jsp:include page="../error.jsp"/>
						
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="65%">
						
						<%if("master".equalsIgnoreCase(request.getParameter("page"))) {%>
						      <tr>
								<td align="right" width="40%">Reference Code</td>
								<td valign="top" align="left">
								    <html:select property="master.referenceCode" styleId="referenceCode">
										<html:options collection="referenceCodeList" property="key" labelProperty="name"/>
								     </html:select>
								</td>
							</tr>
						    <tr>
								<td align="right" width="40%">Order by</td>
								<td valign="top" align="left">
								    <html:select property="master.orderBy" styleId="referenceCode">
										<html:option value="INTERFACE_VALUE">INTERFACE_VALUE</html:option>
										<html:option value="INTERFACE_DESC">INTERFACE_DESC</html:option>
										<html:option value="PENS_VALUE">PENS_VALUE</html:option>
										<html:option value="PENS_DESC">PENS_DESC</html:option>
										<html:option value="PENS_DESC2">PENS_DESC2</html:option>
										<html:option value="PENS_DESC3">PENS_DESC3</html:option>
										<html:option value="SEQUENCE">SEQUENCE</html:option>
								     </html:select>
								</td>
								
							</tr>
						<%} %>
						
					   </table>
					   
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtn"> 
								</a>
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtn">
								</a>
							</td>
						</tr>
					</table>
					<!-- RESULT -->
				    
			        <c:if test="${masterForm.masterResults != null}">
				       
						<br/>
							<display:table id="item" name="requestScope.masterForm.masterResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="../jsp/masterAction.do?do=search" sort="list" pagesize="30">	
							    
							    <display:column  title="InterfaceValue" property="interfaceValue"  sortable="false" class="interfaceValue"/>
							    <display:column  title="InterfaceDesc" property="interfaceDesc"  sortable="false" class="interfaceDesc"/>	
							    <display:column  title="PensValue" property="pensValue"  sortable="false" class="pensValue"/>
							    <display:column  title="PensDesc" property="pensDesc"  sortable="false" class="pensDesc"/>	
							    <display:column  title="PensDesc2" property="pensDesc2"  sortable="false" class="pensDesc2"/>	
							    <display:column  title="PensDesc3" property="pensDesc3"  sortable="false" class="pensDesc3"/>	
							    <display:column  title="CreateUser" property="createUser"  sortable="false" class="m_createUser"/>
							    <display:column  title="Sequence" property="sequence"  sortable="false" class="m_sequence"/>
							    <display:column  title="Status" property="status"  sortable="false" class="m_status"/>
							    <display:column title="Edit & Delete" class="m_edit">
							      <a href="javascript:actionMaster('${pageContext.request.contextPath}','edit','${item.interfaceValue}'
							                                      ,'${item.interfaceDesc}','${item.pensValue}','${item.pensDesc}','${item.pensDesc2}'
							                                      ,'${item.pensDesc3}','${item.sequence}','${item.status}')">
							        <img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif">
							      </a>
							    </display:column>
							  		
							</display:table>
                    </c:if>
                    
                    
					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
					<!-- hidden field -->
					<input type="hidden" name="page" value="<%=request.getParameter("page") %>"/>
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