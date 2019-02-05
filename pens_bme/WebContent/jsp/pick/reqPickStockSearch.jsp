<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="reqPickStockForm" class="com.isecinc.pens.web.pick.ReqPickStockForm" scope="session" />
<% 
String wareHouse = Utils.isNull(request.getParameter("wareHouse"));
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>"" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>"" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>"" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>""></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>""></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>""></script>
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
function gotoPage(path,currPage){
	var form = document.reqPickStockForm;
	form.action = path + "/jsp/reqPickStockAction.do?do=search2&currPage="+currPage;
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

	      	   <%if("W2".equalsIgnoreCase(wareHouse)){ %>
	      	     <jsp:include page="../program.jsp">
				    <jsp:param name="function" value="reqPickStockW2"/>
				 </jsp:include>
			   <%}else if("W3".equalsIgnoreCase(wareHouse)){ %>
	      	     <jsp:include page="../program.jsp">
				    <jsp:param name="function" value="reqPickStockW3"/>
				 </jsp:include>
				<%}else if("W4".equalsIgnoreCase(wareHouse)){ %>
				  <jsp:include page="../program.jsp">
				    <jsp:param name="function" value="reqPickStockW4"/>
				 </jsp:include>
			  <%}else if("W5".equalsIgnoreCase(wareHouse)){ %>
	      	     <jsp:include page="../program.jsp">
				    <jsp:param name="function" value="reqPickStockW5"/>
				 </jsp:include>
		     <%}else if("W7".equalsIgnoreCase(wareHouse)){ %>
	      	     <jsp:include page="../program.jsp">
				    <jsp:param name="function" value="reqPickStockW7"/>
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
						<html:form action="/jsp/reqPickStockAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						         <tr>
                                    <td colspan="2" align="center"> 
                                      <font size="3"><b> Request เบิกสินค้าจากคลัง  ${reqPickStockForm.bean.wareHouse}</b></font>
                                     </td>
								</tr>
						       <tr>
                                    <td> ${reqPickStockForm.bean.wareHouse}
                                      Issue request Date 
                                     </td>
									<td>	
									<html:text property="bean.issueReqDate" styleId="issueReqDate" size="20" styleClass="\" autoComplete=\"off" />		
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;			
									 Issue request No <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20" styleClass="\" autoComplete=\"off" />	  
									</td>
								</tr>
								 <tr>
                                    <td>
                                      Issue request status
                                     </td>
									<td>	
									 <html:select property="bean.status">
											<html:options collection="statusIssueReqList" property="key" labelProperty="name"/>
									  </html:select>	
									  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;				
									 ผู้เบิก <html:text property="bean.requestor" styleId="requestor" size="20"  styleClass="\" autoComplete=\"off"/>	  
									</td>
								</tr>
								<tr>
                                    <td> หมายเหตุ </td>
                                    <td>
						               <html:text property="bean.remark" styleId="remark" size="50"  styleClass="\" autoComplete=\"off"/>
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
                  	<% 
					   int totalPage = reqPickStockForm.getTotalPage();
					   int totalRecord = reqPickStockForm.getTotalRecord();
					   int currPage =  reqPickStockForm.getCurrPage();
					   int startRec = reqPickStockForm.getStartRec();
					   int endRec = reqPickStockForm.getEndRec();
					%>
					   
					<div align="left">
					   <span class="pagebanner">รายการทั้งหมด  <%=totalRecord %> รายการ, แสดงรายการที่  <%=startRec %> ถึง  <%=endRec %>.</span>
					   <span class="pagelinks">
						หน้าที่ 
						 <% 
							 for(int r=0;r<totalPage;r++){
								 if(currPage ==(r+1)){
							 %>
			 				   <strong><%=(r+1) %></strong>
							 <%}else{ %>
							    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  
							       title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
						 <% }} %>				
						</span>
					</div>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >Issue Req Date</th>
									<th >Issue Req No</th>
									<th >Status</th>
									<th >Cust No</th>
									<th >Cust Name</th>
									<th >Total Request</th>
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
										<td class="td_text_center" width="8%">
										   ${results.issueReqDate}
										</td>
										<td class="td_text_center" width="8%">${results.issueReqNo}</td>
										<td class="td_text_center" width="10%">
											${results.statusDesc}
										</td>
										<td class="td_text" width="8%">
											${results.storeCode}
										</td>
										<td class="td_text" width="20%">
											${results.storeName}
										</td>
										<td class="td_text_right" width="8%">
											${results.totalReqQty}
										</td>
									    <td class="td_text_center" width="20%">
										  ${results.remark}
										</td>
										<td class="td_text_center"  width="8%">
											 <c:if test="${results.canEdit == false}">
												  <a href="javascript:openView('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}')">
												       <font size="2">   ดู</font>
												  </a>
											  </c:if>
											  <c:if test="${results.canEdit == true}">
												  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}')">
												         <font size="2"> แก้ไข</font>
												  </a>
											  </c:if>
		
											</td>
											<td class="td_text_center"  width="8%">
												<c:if test="${results.canConfirm == true}">
												  <a href="javascript:openConfirm('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}')">
												       <font size="2">  ยืนยัน</font>
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