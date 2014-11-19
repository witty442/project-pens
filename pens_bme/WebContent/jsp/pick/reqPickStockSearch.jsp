<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
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
<jsp:useBean id="reqPickStockForm" class="com.isecinc.pens.web.pick.ReqPickStockForm" scope="session" />
<% 
if(session.getAttribute("statusIssueReqList") == null){
	List<References> billTypeList = new ArrayList();
	References ref = new References("","");
	billTypeList.add(ref);
	billTypeList.addAll(PickConstants.getRequestStatusW2ListInPageReqPickStock());
	session.setAttribute("statusIssueReqList",billTypeList);
}

if(session.getAttribute("pickTypeList") == null){
	List<References> pickTypeList = new ArrayList();
	References ref = new References("","");
	pickTypeList.add(ref);
	pickTypeList.addAll(PickConstants.getPickTypeList());
	session.setAttribute("pickTypeList",pickTypeList);
}

if(session.getAttribute("custGroupList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(GeneralDAO.searchCustGroup( new PopupForm()));
	
	session.setAttribute("custGroupList",billTypeList);
}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/req_pick_stock.css" type="text/css" />
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
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('issueReqDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('confirmIssueDate'));
}
function clearForm(path){
	var form = document.reqPickStockForm;
	form.action = path + "/jsp/reqPickStockAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.reqPickStockForm;
	var issueReqDate =$('#issueReqDate').val();
	//var storeCode =$('#storeCode').val();
	/* if(issueReqDate ==""){
		alert("กรุณากรอกวันที่  Issue Req Date");
		return false;
	} */
	form.action = path + "/jsp/reqPickStockAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}

function openEdit(path,documentNo,issueReqStatus){
	var form = document.reqPickStockForm;
	form.action = path + "/jsp/reqPickStockAction.do?do=prepare&mode=save&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}

function openView(path,documentNo,issueReqStatus){
	var form = document.reqPickStockForm;
	form.action = path + "/jsp/reqPickStockAction.do?do=prepare&mode=view&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}

function openConfirm(path,documentNo,issueReqStatus){
	var form = document.reqPickStockForm;
	form.action = path + "/jsp/reqPickStockAction.do?do=prepare&mode=confirm&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
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
	      	
				<jsp:param name="function" value="reqPickStock"/>
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
						<html:form action="/jsp/reqPickStockAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td>
                                      Issue request Date <html:text property="bean.issueReqDate" styleId="issueReqDate" size="20" />
                                     </td>
									<td>						
									 Issue request No <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20" />	  
									</td>
								</tr>
								 <tr>
                                    <td>
                                      Issue request status
                                      <html:select property="bean.status">
											<html:options collection="statusIssueReqList" property="key" labelProperty="name"/>
									    </html:select>
                                     </td>
									<td>						
									 ผู้เบิก <html:text property="bean.requestor" styleId="requestor" size="20" />	  
									</td>
								</tr>
								<tr>
                                    <td colspan="2"> หมายเหตุ
						               <html:text property="bean.remark" styleId="remark" size="50" />
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:openEdit('${pageContext.request.contextPath}','','')">
										  <input type="button" value=" เพิ่มรายการใหม่ " class="newPosBtnLong">
										</a>	
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>
									</td>
									</tr>
							</table>
					  </div>

            <c:if test="${reqPickStockForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >Issue Req Date</th>
									<th >Issue Req No</th>
									<th >Status</th>
									<th >หมายเหตุ</th>
									<th >Action</th>	
									<th >ยืนยัน</th>						
							   </tr>
							<c:forEach var="results" items="${reqPickStockForm.resultsSearch}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<td class="search_issueReqDate">
										   ${results.issueReqDate}
										</td>
										<td class="search_issueReqNo">${results.issueReqNo}</td>
										<td class="search_issueReqStatus">
											${results.statusDesc}
										</td>
										
									    <td class="search_remark">
										  ${results.remark}
										</td>
										<td class="search_edit">
											 <c:if test="${results.canEdit == false}">
												  <a href="javascript:openView('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}')">
												          ดู
												  </a>
											  </c:if>
											  <c:if test="${results.canEdit == true}">
												  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}')">
												          แก้ไข
												  </a>
											  </c:if>
		
											</td>
											<td class="search_edit2">
												<c:if test="${results.canConfirm == true}">
												  <a href="javascript:openConfirm('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}')">
												          ยืนยัน
												  </a>
											  </c:if>	
										</td>
									</tr>
							
							  </c:forEach>
					</table>
								
								
					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
							<tr><td>
														
								</td>
							</tr>
						</table>
					</div>
				</c:if>
				
				<!-- ************************Result ***************************************************-->
					
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