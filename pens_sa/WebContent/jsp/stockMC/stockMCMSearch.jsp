<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCAction"%>
<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockMCForm" class="com.isecinc.pens.web.stockmc.StockMCForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "stockMCForm");
User user = (User)session.getAttribute("user");
String pageName = Utils.isNull(request.getParameter("pageName")); 
String mobile = Utils.isNull(request.getParameter("mobile")); 
String codes = Utils.isNull(session.getAttribute("stock_mc_codes"));
%>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_mobile_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/stockMCMobile.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- Calendar -->
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/calendar/jquery.calendars.picker.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.plugin.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.plus.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai-th.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker-th.js"></script>
  
<!-- Bootstrap -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css">
<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
  
<style type="text/css">
</style>

<script type="text/javascript">

/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}

window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	$('#stockDate').calendarsPicker({calendar: $.calendars.instance('thai','th')});
	
	 document.getElementById("stockDate").readonly = true;
}
function clearForm(path){
	var form = document.stockMCForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockMCAction.do?do=prepareSearch&action=back&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.stockMCForm;
/* 	if( form.startDate.value ==""&& form.endDate.value==""){
		alert("กรุณาระบุวันที่");
		form.startDate.focus();
		return false;
	} */
	
	form.action = path + "/jsp/stockMCAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}

//Goto stockMCMStep4 (list product by brand)
function openSearchDetail(path,action,id){
	var form = document.stockMCForm;
    var param = "&headerId="+id;
        param +="&action="+action;
        param +="&fromPage=stockMCMSearch";
        
	form.action = path + "/jsp/stockMCAction.do?do=viewSearchDetail"+param;
	form.submit();
	return true;
}

function exportToExcel(path){
	var form = document.stockMCForm;
	var codes = document.getElementsByName("codes")[0].value;
	if(codes == ''){
		alert("กรุณาระบุ รายการที่ต้องการ Export ก่อน");
		return false;
	}
	form.action = path + "/jsp/stockMCAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.stockMCForm;
	form.action =  "${pageContext.request.contextPath}/jsp/stockMCAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}

</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">
       
           <!-- Include Header Mobile  -->
            <jsp:include page="../templates/headerM.jsp"/>
           <!-- /Include Header Mobile -->
	
			<!-- BODY -->
			<html:form action="/jsp/stockMCAction">
			<jsp:include page="../error.jsp"/>
			
			 <span title="stockMCMSearch(new)">...</span>
			  
			 <div class="card-header">
			          วันที่ :
			       <html:text property="bean.stockDate" styleId="stockDate" 
		                styleClass=" \" autoComplete=\"off"/> 
			    
			  </div>
			 <div class="card-header">
				<input type="button" name="x1" value="เลือกห้าง" class="btn btn-primary"
					   onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC','false')"/>
			 </div>
			  <div class="card-header">
		           <html:text property="bean.customerCode" styleId="customerCode" size="3" 
					     styleClass=" \" autoComplete=\"off"/>-
				   <html:text property="bean.customerName" styleId="customerName" 
					     styleClass=" \" autoComplete=\"off" />
		      </div>
	
	         <div class="card-header">
		           <input type="button" name="x2" value="   เลือกสาขา   " class="btn btn-primary"
						  onclick="openPopup('${pageContext.request.contextPath}','BranchStockMC','false')"/>
		      </div>
			  <div class="card-header">
		           <html:text property="bean.storeCode" styleId="storeCode" size="3" 
					     styleClass=" \" autoComplete=\"off"/>-
				   <html:text property="bean.storeName" styleId="storeName"  
					     styleClass=" \" autoComplete=\"off" />
		      </div>
			   <div class="text-center" >
			      <div class="card-header">
			         <a href="javascript:search('${pageContext.request.contextPath}')">
					  <input type="button" value="ค้นหา  " class="btn btn-primary"> 
					</a>&nbsp;
					<a href="javascript:clearForm('${pageContext.request.contextPath}')">
					  <input type="button" value="Clear" class="btn btn-primary">
					</a>
				 </div>
			  </div>
			
			  <div class="card mb-1 shadow-sm" align="center">
				     <div class="card-header">
					     <a href="javascript:window.location='${pageContext.request.contextPath}/jsp/stockMC/stockMCMStep1.jsp'">
						    <input type="button" value="กลับหน้าหลัก " class="btn btn-primary"> 
						</a>
				     </div>
			 </div>
		
		 	    <!-- ************************Result *************-->
		 	    <c:if test="${stockMCForm.results != null}">
		 	     <% 
				   int totalPage = stockMCForm.getTotalPage();
				   int totalRecord = stockMCForm.getTotalRecord();
				   int currPage =  stockMCForm.getCurrPage();
				   int startRec = stockMCForm.getStartRec();
				   int endRec = stockMCForm.getEndRec();
				   int no = startRec;
				   boolean mobileFlage = true;
				%>
			        <%=PageingGenerate.genPageing(user,totalPage, totalRecord, currPage, startRec, endRec, no) %>

						<div class="table-responsive">
						<table id="tblProduct" class="table" width="50%">
						    <thead>
						       <tr>
						            <th scope="col" class="bg-info">Action</th>
									<th scope="col" class="bg-info">No</th>
									<th scope="col" class="bg-info">วันที่ตรวจนับสต๊อก</th>
									<th scope="col" class="bg-info">ห้าง</th>
									<th scope="col" class="bg-info">ชื่อห้าง</th>
									<th scope="col" class="bg-info">สาขา</th>
									<th scope="col" class="bg-info">ผู้บันทึกตรวจเช็คสต๊อก</th>
									<!-- <th scope="col" class="bg-info">พนักงาน PC</th>		 -->	
							   </tr>
							</thead>
							<tbody>
							<% 
							String tabclass ="";
							List<StockMCBean> resultList = stockMCForm.getResults();
							for(int n=0;n<resultList.size();n++){
								StockMCBean item = (StockMCBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="bg-light";
								}else{
									tabclass ="";
								} 
								%>
									<tr class="<%=tabclass%>">
									<td class="td_text_center" width="5%">
										 <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
											   <a href="javascript:openSearchDetail('${pageContext.request.contextPath}','edit', '<%=item.getId()%>')">
											      <% out.println(item.isCanEdit()?"แก้ไข":"View"); %>
											  </a>
										  <%}else{ %>
										      <a href="javascript:openSearchDetail('${pageContext.request.contextPath}','view', '<%=item.getId()%>')">
											        View
											  </a>
										  <%} %>
										</td>
										<td class="td_text_center" width="5%"><%=no %></td>
										<td class="td_text_center" width="5%">
										   <%=item.getStockDate() %>
										</td>
										<td class="td_text" width="5%">
										   <%=item.getCustomerCode() %>
										</td>
										<td class="td_text" width="10%">
										  <%=item.getCustomerName()%>
										</td>
										<td class="td_text" width="10%">
										   <%=item.getStoreCode()%>-
										   <%=item.getStoreName()%>
										 </td>
										<td class="td_text_center" width="10%">
											 <%=item.getCreateUser() %>
										</td>
									</tr>
									 <% no++;%>
							<%}//for %>
							</tbody>
					  </table>
					  </div>	
			    </c:if>		
		 
		 	
		 	<!-- INPUT HIDDEN -->
		 	<input type="hidden" name="pageName" value="<%=pageName %>"/>
		 	<input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
		 	<input type="hidden" name="currentPage"  value ="<%=stockMCForm.getCurrPage()%>" />
            <input type="hidden" name="codes" value ="<%=codes%>" />
            <input type="hidden" id="nextStep" name="nextStep"/>
		</html:form>
		<!-- BODY -->
	    <!-- Include Footer Mobile  -->
          <jsp:include page="../templates/footerM.jsp"/>
        <!-- /Include Footer Mobile -->	
</body>
</html>