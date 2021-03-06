<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.dao.ConfirmReturnWacoalDAO"%>
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
<jsp:useBean id="confirmReturnWacoalForm" class="com.isecinc.pens.web.pick.ConfirmReturnWacoalForm" scope="session" />
<%
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
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('requestDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('returnDate'));
}

function clearForm(path){
	var form = document.confirmReturnWacoalForm;
	form.action = path + "/jsp/confirmReturnAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.confirmReturnWacoalForm;
	form.action = path + "/jsp/confirmReturnAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.confirmReturnWacoalForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/confirmReturnAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,requestNo,returnNo,mode){
	var form = document.confirmReturnWacoalForm;
	form.action = path + "/jsp/confirmReturnAction.do?do=prepare&requestNo="+requestNo+"&returnNo="+returnNo+"&mode="+mode;
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
						<html:form action="/jsp/confirmReturnAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Request Date</td>
									<td>					
										 <html:text property="bean.requestDate" styleId="requestDate" size="20" styleClass="\" autoComplete=\"off"/>
									</td>
									<td> 
									    Request No <html:text property="bean.requestNo" styleId="requestNo" size="20" styleClass="\" autoComplete=\"off"/>
									</td>
									<td>					
										Status   
										<html:select property="bean.status">
											<html:options collection="statusConfReturnList" property="key" labelProperty="name"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> Return Date</td>
									<td>					
										 <html:text property="bean.returnDate" styleId="returnDate" size="20" styleClass="\" autoComplete=\"off"/>
									</td>
									<td> 
									    Return No <html:text property="bean.returnNo" styleId="returnNo" size="20" styleClass="\" autoComplete=\"off"/>
									</td>
									<td>
									    CN NO <html:text property="bean.cnNo" styleId="cnNo" size="15" styleClass="\" autoComplete=\"off"/>
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
                  	<% 
					   int totalPage = confirmReturnWacoalForm.getTotalPage();
					   int totalRecord = confirmReturnWacoalForm.getTotalRecord();
					   int currPage =  confirmReturnWacoalForm.getCurrPage();
					   int startRec = confirmReturnWacoalForm.getStartRec();
					   int endRec = confirmReturnWacoalForm.getEndRec();
					   int no = confirmReturnWacoalForm.getStartRec();
					%>
					<%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
					  
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >No</th>
									<th >Request Date</th>
									<th >Request No</th>
									<th >Status</th>
									<th >Remark</th>
									<th >Return Date</th>
									<th >Return No</th>
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
										<td class="td_text_center" width="5%">${results.no}</td>
										<td  class="td_text_center" width="10%">
										   ${results.requestDate}
										</td>
										<td  class="td_text_center" width="10%">${results.requestNo}</td>
										<td  class="td_text_center" width="10%">
											${results.statusDesc}
										</td>
										<td  class="td_text" width="15%">
										    ${results.remark}
										</td>
										<td  class="td_text_center" width="10%">
										    ${results.returnDate}
										</td>
										<td  class="td_text_center" width="10%">
										    ${results.returnNo}
										</td>
										<td  class="td_text_center" width="10%">
										 <c:if test="${results.canEdit == false}">
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.requestNo}','${results.returnNo}','view')">
											         <font size="2">  ��</font>
											  </a>
										  </c:if>
										  <c:if test="${results.canEdit == true}">
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.requestNo}','${results.returnNo}','edit')">
											     <font size="2">   �׹�ѹ </font>
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
					<input type="hidden" id="path" name="path" value ="${pageContext.request.contextPath}"/>
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