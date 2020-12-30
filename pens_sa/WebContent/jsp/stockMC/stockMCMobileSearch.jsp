<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCAction"%>
<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/stockMC.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

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

function openEditMobile(path,action,id){
	var form = document.stockMCForm;
    var param = "&id="+id;
        param +="&action="+action;
        param +="&pageName="+document.getElementsByName("pageName")[0].value;
        
	form.action = path + "/jsp/stockMCAction.do?do=viewDetailMobile"+param;
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
/* set Chechbox Page **/
function saveSelectedInPage(no){
	//alert(no);
	var chk = document.getElementsByName("chCheck");
	var currentPage = document.getElementsByName("currentPage")[0].value;
	var code = document.getElementsByName("code_temp");
	var retCode = '';
	var codesAllNew = "";

	//alert("no:"+no);
	if(no > <%=StockMCAction.pageSize%>){
	   no = no - ( (currentPage-1) *<%=StockMCAction.pageSize%>);
	}
	//alert("no["+no+"]checked:"+chk[no].checked);
    if(chk[no].checked){
    	//Add 
        retCode = code[no].value;
		
		//alert("code["+no+"]="+retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		
	    var found = chekCodeDupInCodesAll(retCode);
	    if(found == false){
	  	   codesAllNew += retCode +",";
	    }
	    document.getElementsByName("codes")[0].value =  codesAllNew;
	    //alert(no+":found["+found+"] codes["+document.getElementsByName("codes")[0].value+"]");
    }else{
    	//remove
    	retCode = code[no].value;
		
		//alert(retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		
	    removeUnSelected(retCode);
    }
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/stockMC/ajax/saveValueSelected.jsp",
			data : "codes=" + encodeURIComponent(document.getElementsByName("codes")[0].value),
			//async: false,
			cache: true,
			success: function(){
			}
		}).responseText;
	});
}
function chekCodeDupInCodesAll(codeCheck){
	var codesAll = document.getElementsByName("codes")[0].value;
	var codesAllArray = codesAll.split(",");
	var found = false;;
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] == codeCheck){
   			found = true;
   			break;
   		}//if
	}//for
	return found;
}
function removeUnSelected(codeCheck){
	var codesAll = document.getElementsByName("codes")[0].value;
	var codesAllArray = codesAll.split(",");
	var codesAllNew  = "";
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] != codeCheck){
   		   codesAllNew += codesAllArray[i] +",";
   		}//if
	}//for
	codesAllNew = codesAllNew.substring(0,codesAllNew.length-1);
	document.getElementsByName("codes")[0].value =  codesAllNew;
}

</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">
       
		   <h1 class="h3 mb-3 font-weight-normal"><b>SALES ANALYSIS SYSTEM</b>
		    &nbsp;&nbsp;&nbsp;
		    <a href="${pageContext.request.contextPath}/?logoff=T" onmouseout="MM_swapImgRestore()" 
        	  onmouseover="MM_swapImage('Image14','','${pageContext.request.contextPath}/images2/button_logout2.png',1)">
        	  <img src="${pageContext.request.contextPath}/images2/button_logout1.png" 
        	  name="Image14" width="60" height="60" border="0" id="Image14" />
        	</a>
		    </h1>
		     
			<a href="javascript:window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
			  <input type="button" value="กลับหน้าหลัก " class="btn btn-primary"> 
			</a>
					
			<!-- BODY -->
			<html:form action="/jsp/stockMCAction">
			<jsp:include page="../error.jsp"/>
			<div class="container">
			  <div class="form-group row">
				   <div class="col-xs-2">
	                   <label for="stockDate"> วันที่ตรวจนับสต๊อก :</label>
					       <html:text property="bean.stockDate" styleId="stockDate" 
		                   readonly="true"  />	
				    </div>
				    <div class="col-xs-6">
	                   <label for="customerCode"> ห้าง  :</label>
					        <html:text property="bean.customerCode" styleId="customerCode" 
							     styleClass=" \" autoComplete=\"off"
								onkeypress="getCustNameKeypress(event,this)"/>
							<input type="button" name="x1" value="..." class="btn btn-primary"
							    onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC')"/>
				    </div>
				    <div class="col-xs-12">
	                   <label for="customerName"> ชื่อห้าง :</label>
					         <html:text property="bean.customerName" styleId="customerName"  readonly="true"
							     styleClass="" />
				    </div>
				   
			 </div>
		</div>
		
			<div class="row mb-3">
		      <div class="col-12 themed-grid-col-detail" align="center">
		          <p>
		           <a href="javascript:search('${pageContext.request.contextPath}')">
					  <input type="button" value="ค้นหา  " class="btn btn-primary"> 
					</a>
					<%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
						<a href="javascript:openEditMobile('${pageContext.request.contextPath}','add','')">
						  <input type="button" value="เพิ่มรายการใหม่ " class="btn btn-primary"> 
						</a>
					<%}%>
					<a href="javascript:clearForm('${pageContext.request.contextPath}')">
					  <input type="button" value="Clear" class="btn btn-primary">
					</a>
				  </p>
				  <p>
					<%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
					  <input type="button" value="Export To Excel" class="btn btn-primary">
					</a> &nbsp; --%>
										
				 </p>
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
						<table id="tblProduct" class="table">
						      <thead>
						       <tr>
						            <th scope="col" class="bg-info">แก้ไข/ดู</th>
									<th scope="col" class="bg-info">No</th>
									<th scope="col" class="bg-info">วันที่ตรวจนับสต๊อก</th>
									<th scope="col" class="bg-info">ห้าง</th>
									<th scope="col" class="bg-info">ชื่อห้าง</th>
									<th scope="col" class="bg-info">สาขา</th>
									<th scope="col" class="bg-info">ผู้บันทึกตรวจเช็คสต๊อก</th>
									<th scope="col" class="bg-info">พนักงาน PC</th>			
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
									<td class="td_text_center" width="4%">
										 <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
											  
											   <a href="javascript:openEditMobile('${pageContext.request.contextPath}','edit', '<%=item.getId()%>')">
											      แก้ไข
											  </a>
											   
										  <%}else{ %>
										      <a href="javascript:openEdit('${pageContext.request.contextPath}','view', '<%=item.getId()%>')">
											      VIEW
											  </a>
										  <%} %>
										 
										</td>
										<td class="td_text_center" width="2%"><%=no %></td>
										<td class="td_text_center" width="5%">
										   <%=item.getStockDate() %>
										</td>
										<td class="td_text" width="5%">
										   <%=item.getCustomerCode() %>
										</td>
										<td class="td_text" width="10%">
										  <%=item.getCustomerName()%>
										</td>
										<td class="td_text" width="7%"> <%=item.getStoreName()%></td>
										<td class="td_text_center" width="5%">
											 <%=item.getCreateUser() %>
										</td>
										<td class="td_text_center" width="10%">
											 <%=item.getMcName() %>
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
		</html:form>
		<!-- BODY -->
					
</body>
</html>