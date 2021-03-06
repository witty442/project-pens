<%@page import="com.isecinc.pens.dao.ConfirmReturnWacoalDAO"%>
<%@page import="com.isecinc.pens.dao.ReqReturnWacoalDAO"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
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
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="confirmReturnWacoalForm" class="com.isecinc.pens.web.pick.ConfirmReturnWacoalForm" scope="session" />

<%
if(session.getAttribute("statusReqReturnList") == null){
	List<References> billTypeList = new ArrayList();
	References ref = new References("","");
	billTypeList.add(ref);
	billTypeList.addAll(ConfirmReturnWacoalDAO.getRequestStatusW1List());
	session.setAttribute("statusReqReturnList",billTypeList);
}

if(session.getAttribute("statusConfReturnList") == null){
	List<References> billTypeList = new ArrayList();
	References ref = new References("","");
	billTypeList.add(ref);
	billTypeList.addAll(ConfirmReturnWacoalDAO.getConfStatusList());
	session.setAttribute("statusConfReturnList",billTypeList);
}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/pick_confirmReturnWacoal.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('requestDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('returnDate'));
}

function clearForm(path){
	var form = document.confirmReturnWacoalForm;
	form.action = path + "/jsp/confirmReturnWacoalAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.confirmReturnWacoalForm;
	form.action = path + "/jsp/confirmReturnWacoalAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}

function openEdit(path,requestNo,returnNo,mode){
	var form = document.confirmReturnWacoalForm;
	form.action = path + "/jsp/confirmReturnWacoalAction.do?do=prepare&requestNo="+requestNo+"&returnNo="+returnNo+"&mode="+mode;
	form.submit();
	return true;
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
				<jsp:param name="function" value="confirmReturnWacoal"/>
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
						<html:form action="/jsp/confirmReturnWacoalAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Request Date</td>
									<td>					
										 <html:text property="bean.requestDate" styleId="requestDate" size="20"/>
									</td>
									<td> 
									    Request No <html:text property="bean.requestNo" styleId="requestNo" size="20"/>
									</td>
									<td>					
										Request Status   
										<html:select property="bean.requestStatus">
											<html:options collection="statusReqReturnList" property="key" labelProperty="name"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> Retrun Date</td>
									<td>					
										 <html:text property="bean.returnDate" styleId="returnDate" size="20"/>
									</td>
									<td> 
									    Return No <html:text property="bean.returnNo" styleId="returnNo" size="20"/>
									</td>
									<td>Return Status					
										<html:select property="bean.returnStatus">
											<html:options collection="statusConfReturnList" property="key" labelProperty="name"/>
									    </html:select>
									    CN NO <html:text property="bean.cnNo" styleId="cnNo" size="15"/>
									</td>
								</tr>	
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ����      " class="newPosBtnLong"> 
										</a>										
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${confirmReturnWacoalForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >No</th>
									<th >Request Date</th>
									<th >Request No</th>
									<th >Request Status</th>
									<th >Remark</th>
									<th >Return Date</th>
									<th >Return No</th>
									<th >Return Status</th>
									<th >Action</th>						
							   </tr>
							<c:forEach var="results" items="${confirmReturnWacoalForm.resultsSearch}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<td class="search_no">${results.no}</td>
										<td class="search_requestDate">
										   ${results.requestDate}
										</td>
										<td class="search_requestNo">${results.requestNo}</td>
										<td class="search_status">
											${results.requestStatusDesc}
										</td>
										<td class="search_remark">
										    ${results.remark}
										</td>
										<td class="search_returnDate">
										    ${results.returnDate}
										</td>
										<td class="search_returnNo">
										    ${results.returnNo}
										</td>
										<td class="search_returnStatus">
										    ${results.returnStatusDesc}
										</td>
										<td class="search_edit" align="center">
										 <c:if test="${results.canEdit == false}">
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.requestNo}','${results.returnNo}','view')">
											          ��
											  </a>
										  </c:if>
										  <c:if test="${results.canEdit == true}">
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.requestNo}','${results.returnNo}','edit')">
											    <c:choose>
													<c:when test="${results.returnNo == ''}">
														 ���ҧ��¡������
													</c:when>
													<c:otherwise>
														 ���
													</c:otherwise>
											   </c:choose>      
											         
											  </a>
										  </c:if>
										</td>
									</tr>
							
							  </c:forEach>
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