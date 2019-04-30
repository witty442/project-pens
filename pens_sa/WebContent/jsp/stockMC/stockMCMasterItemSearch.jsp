<%@page import="com.isecinc.pens.web.stockmc.StockMCAction"%>
<%@page import="util.UserUtils"%>
<%@page import="util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="util.Utils"%>
<%@page import="util.SIdUtils"%>
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

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	new Epoch('epoch_popup','th',document.getElementById('stockDate'));
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
function openEdit(path,action,customerCode,barcode){
	var form = document.stockMCForm;
	var pageName = document.getElementsByName("pageName")[0].value;
    var param = "&customerCode="+customerCode;
        param +="&barcode="+barcode;
        param +="&action="+action;
        param +="&pageName="+pageName;
	
	form.action = path + "/jsp/stockMCAction.do?do=viewDetail"+param;
	form.submit();
	return true;
}

function exportToExcel(path){
	var form = document.stockMCForm;
	form.action = path + "/jsp/stockMCAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.stockMCForm;
	form.action = path + "/jsp/stockMCAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}
function getCustNameKeypress(e,customerCode){
	var form = document.stockMCForm;
	if(e != null && e.keyCode == 13){
		//alert(customerCode.value);
		if(customerCode.value ==''){
			form.customerName.value = '';
		}else{
			getCustNameModel(customerCode);
		}
	}
}
function getCustNameModel(customerCode){
	var returnString = "";
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	var getData = $.ajax({
			url: path+"/jsp/stockMC/ajax/getCustNameAjax.jsp",
			data : "customerCode=" + customerCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	if(returnString !=''){
		//var retArr = returnString.split("|");
		form.customerName.value =returnString;	
	}else{
		alert("ไม่พบข้อมูล");
		form.customerCode.value = "";	
		form.customerName.value = "";
	}
}
function openPopup(path,pageName){
	var form = document.stockMCForm;
	var param = "&pageName="+pageName;
	 if("CustomerStockMC" == pageName){
		param +="&hideAll=true&customerCode="+form.customerCode.value;
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.stockMCForm;
	if("CustomerStockMC" == pageName){
		form.customerCode.value = code;
		form.customerName.value = desc;
	}
} 
</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">
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
				<jsp:param name="function" value="MasterItemStockMC"/>
			</jsp:include>
			<!-- Hidden Field -->
		 <%--    <html:hidden property="pageName" value="<%=pageName %>"/> --%>
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
						<html:form action="/jsp/stockMCAction">
						<jsp:include page="../error.jsp"/>
						<div align="center">
						   	<!--  Criteria -->
						   <table align="center" border="0" cellpadding="3" cellspacing="0" >
							<tr>
				                <td align="right"> ห้าง  </td>
								<td colspan="2">
								  <html:text property="bean.customerCode" styleId="customerCode" 
								    size="10"  styleClass="\" autoComplete=\"off"
									onkeypress="getCustNameKeypress(event,this)"/>
								    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC')"/>   
								   <html:text property="bean.customerName" styleId="customerName" size="40" readonly="true" styleClass="disableText"/>
								</td>
							</tr>	
					   </table>
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									<a href="javascript:search('${pageContext.request.contextPath}')">
									  <input type="button" value="   ค้นหา     " class="newPosBtnLong"> 
									</a>
									<a href="javascript:openEdit('${pageContext.request.contextPath}','add','','')">
									  <input type="button" value=" เพิ่มรายการใหม่  " class="newPosBtnLong"> 
									</a>
									<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="Export To Excel" class="newPosBtnLong">
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>						
								</td>
							</tr>
						</table>
					    
					 	    <!-- ************************Result *************-->
					 	    <c:if test="${stockMCForm.results != null}">
						        <% 
								   int totalPage = stockMCForm.getTotalPage();
								   int totalRecord = stockMCForm.getTotalRecord();
								   int currPage =  stockMCForm.getCurrPage();
								   int startRec = stockMCForm.getStartRec();
								   int endRec = stockMCForm.getEndRec();
								   int no = startRec;
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
									<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="1" class="tableSearch">
									       <tr>
									           <!--  <th >Selected</th> -->
												<th >No</th>
												<th >ห้าง</th>
												<th >ชื่อห้าง</th>
												<th >รหัสสินค้า Pens</th>
												<th >รหัสสินค้าห้าง</th>
												<th >Barcode</th>
												<th >Description</th>
												<th >บรรจุ</th>
												<th >อายุสินค้า</th>
												<th >ราคาปลีก</th>
												<th >Action</th>						
										   </tr>
										<% 
										String tabclass ="";
										List<StockMCBean> resultList = stockMCForm.getResults();
										for(int n=0;n<resultList.size();n++){
											StockMCBean item = (StockMCBean)resultList.get(n);
											if(n%2==0){ 
												tabclass="lineO";
											}else{
												tabclass ="lineE";
											}
											%>
												<tr class="<%=tabclass%>">
												    <%-- <td class="td_text_center" width="2%">
												     <input type ="checkbox" name="chCheck" id="chCheck" onclick="saveSelectedInPage(<%=n%>)"  />
												      <input type ="hidden" name="code_temp" value="<%=item.getId() %>" />
													</td> --%>
													<td class="td_text_center" width="2%"><%=no %></td>
													<td class="td_text" width="5%">
													   <%=item.getCustomerCode() %>
													</td>
													<td class="td_text" width="10%">
													  <%=item.getCustomerName()%>
													</td>
													<td class="td_text_center" width="5%"> <%=item.getProductCode()%></td>
													<td class="td_text_center" width="5%">
														 <%=item.getItemCust() %>
													</td>
													<td class="td_text_center" width="10%">
														 <%=item.getBarcode() %>
													</td>
													<td class="td_text" width="15%">
														 <%=item.getProductName() %>
													</td>
													<td class="td_text_center" width="5%">
														 <%=item.getProductPackSize() %>
													</td>
													<td class="td_text_center" width="5%">
														 <%=item.getProductAge() %>
													</td>
													<td class="td_text_center" width="5%">
														 <%=item.getRetailPriceBF() %>
													</td>
													<td class="td_text_center" width="4%">
														 <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
															   <a href="javascript:openEdit('${pageContext.request.contextPath}','edit','<%=item.getCustomerCode()%>', '<%=item.getBarcode()%>')">
															      แก้ไข
															  </a>
														  <%}else{ %>
														      <a href="javascript:openEdit('${pageContext.request.contextPath}','view','<%=item.getCustomerCode()%>', '<%=item.getBarcode()%>')">
															      VIEW
															  </a>
														  <%} %>
													  <% no++;%>
													</td>
												</tr>
										<%}//for %>
								  </table>	
						    </c:if>		
					 	</div>
					 	
					 	<!-- INPUT HIDDEN -->
					 	<input type="hidden" name="pageName" value="<%=pageName %>"/>
					 	<input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
					 	<input type="hidden" name="currentPage"  value ="<%=stockMCForm.getCurrPage()%>" />
                    
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