<%@page import="com.isecinc.pens.bean.TransferBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<jsp:useBean id="transferForm" class="com.isecinc.pens.web.transfer.TransferForm" scope="session" />
<%
String role = ((User)session.getAttribute("user")).getType();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('transferDateFrom'));
	new Epoch('epoch_popup', 'th', document.getElementById('transferDateTo'));
}
function search(path) {
	document.transferForm.action = path + "/jsp/transferAction.do?do=search"+"&action=newsearch";
	document.transferForm.submit();
}
function gotoPage(path,currPage){
	var form = document.transferForm;
	form.action = path + "/jsp/transferAction.do?do=search&currPage="+currPage;
    form.submit();
    return true;
}
function actionTransfer(path,createDate,action) {
	document.transferForm.action = path + "/jsp/transferAction.do?do=prepare"+"&action="+action+"&createDate="+createDate; 
	document.transferForm.submit();
}
function backsearch(path) {
	document.transferForm.action = path + "/jsp/transferAction.do?do=prepare"+"&action=back";
	document.transferForm.submit();
}
function clearForm(path) {
	document.transferForm.action = path + "/jsp/transferAction.do?do=prepareSearch"+"&action=new";//clearForm
	document.transferForm.submit();
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
				<jsp:param name="function" value="Transfer"/>
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
						<html:form action="/jsp/transferAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						   <tr>
						        <td align="right" width="45%">จากวันที่โอน</td>
								<td align="left" width="5%">
							     <html:text property="bean.transferDateFrom" size="10" readonly="true" styleId="transferDateFrom" styleClass=""/>
							    </td>
							     <td align="right" width="5%">ถึงวันที่โอน</td>
							     <td align="left" width="45%">
							     <html:text property="bean.transferDateTo" size="10" readonly="true" styleId="transferDateTo"  styleClass=""/>
							    </td>
							</tr>
					   </table>
					   
					<br>
					<!-- BUTTON -->
					 <table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								  <input type="button" value="   ค้นหา      " class="newPosBtnLong" onclick="search('${pageContext.request.contextPath}')"> 
								  <input type="button" value="เพิ่มรายการโอนเงิน " class="newPosBtnLong" onclick="actionTransfer('${pageContext.request.contextPath}','','new')"> 
								  <input type="button" value="   Clear  " class="newPosBtnLong" onclick="clearForm('${pageContext.request.contextPath}')">								
							</td>
						</tr>
					</table> 
					<!-- RESULT -->
				    
			         <c:if test="${transferForm.results != null}">
				         <jsp:include page="../pageing.jsp">
					       <jsp:param name="totalPage" value="<%=transferForm.getTotalPage() %>"/>
					       <jsp:param name="totalRecord" value="<%=transferForm.getTotalRecord() %>"/>
					       <jsp:param name="currPage" value="<%=transferForm.getCurrPage() %>"/>
					       <jsp:param name="startRec" value="<%=transferForm.getStartRec() %>"/>
					       <jsp:param name="endRec" value="<%=transferForm.getEndRec() %>"/>
				         </jsp:include>
			             <table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
						            <th >No</th>	
						            <th >ประเภทการโอน</th>	
									<th >ธนาคาร PENS</th>
									<th >วันที่โอน</th>
									<th >เวลาที่โอน</th>
									<th >ยอดเงินโอน</th>
									<th >เลขที่เช็ค</th>
									<th >วันที่หน้าเช็ค</th>
									<th >วันที่บันทึกข้อมูล</th>
									<th >สถานะโอนข้อมูล</th>
									<th >แก้ไข/ดู ข้อมูล</th>	
							   </tr>
						<c:forEach var="results" items="${transferForm.results}" varStatus="rows">
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
										<td class="td_text_center" width="5%">${results.transferTypeLabel}</td>
										<td class="td_text" width="25%">${results.transferBankLabel}</td>
										<td class="td_text_center" width="8%">${results.transferDate}</td>
										<td class="td_text_center" width="8%">${results.transferTime}</td>
										<td class="td_number" width="10%">${results.amount}</td>
										<td class="td_text_center" width="10%">${results.chequeNo}</td>
										<td class="td_text_center" width="8%">${results.chequeDate}</td>
										<td class="td_text_center" width="8%">${results.createDate}</td>
										<td class="td_text_center" width="5%">
										   <c:choose>
											<c:when test="${results.exported == 'Y'}">
												<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:when>
											<c:otherwise>
											</c:otherwise>
										  </c:choose>	
										</td>
										<td class="td_text_center" width="10%">
										   <c:if test="${results.canEdit =='true'}">
										   	    <a href="#" onclick="actionTransfer('${pageContext.request.contextPath}','${results.createDate}','edit');">
												  <img src="${pageContext.request.contextPath}/icons/process.gif" border="0"/>
												</a>
											</c:if>
											<c:if test="${results.canEdit =='false'}">
										   	    <a href="#" onclick="actionTransfer('${pageContext.request.contextPath}','${results.createDate}','view');">
												  <img src="${pageContext.request.contextPath}/icons/lookup.gif"  border="0"/>
												</a>
											</c:if>
										</td>
									</tr>
							  </c:forEach>
							  <%if(transferForm.getCurrPage()==transferForm.getTotalPage()) {
							      TransferBean summary = transferForm.getSummary();
							  %>
								  <tr class="hilight_text">
									   <td class="hilight_text" colspan="5" align="right">
										  <B> ยอดรวม </B>
										</td>
										<td class="hilight_text" align="right">
										 <B> <%=summary.getAmount() %></B>
										</td>
										<td class="" colspan="5"></td>
								</tr>
							<%} %>
					</table>
						
                    </c:if> 
                   
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
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