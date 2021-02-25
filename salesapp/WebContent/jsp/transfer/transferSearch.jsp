<%@page import="com.isecinc.pens.bean.TransferBean"%>
<%@page import="util.SessionGen"%>
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
<%@page import="com.pens.util.Utils"%>
<%@page import="java.util.ArrayList"%>
<jsp:useBean id="transferForm" class="com.isecinc.pens.web.transfer.TransferForm" scope="session" />
<%
String role = ((User)session.getAttribute("user")).getType();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

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
<body class="sb-nav-fixed" onload="loadMe()">
     <!-- Include Header Mobile  -->
     <jsp:include page="../header.jsp"  flush="true"/>
     <!-- /Include Header Mobile -->
     
   	<!-- PROGRAM HEADER -->
     	<jsp:include page="../program.jsp">
		<jsp:param name="function" value="Transfer"/>
	</jsp:include> 
	
	
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
             <div class="table-responsive">
              <table class="table table-bordered table-striped table-light"
                   id="dataTable" width="100%" cellspacing="0">
                 <thead class="thead-dark">
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
				 </thead>
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
		</div>
			
        </c:if> 
                
	<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
	</html:form>
		
	<!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->			
</body>
</html>