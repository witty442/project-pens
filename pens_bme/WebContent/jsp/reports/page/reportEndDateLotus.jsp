<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<jsp:useBean id="reportsForm" class="com.isecinc.pens.web.reports.ReportsForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<%
	String storeType ="lotus";
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));
	String pageName = Utils.isNull(request.getParameter("pageName"));
	String hideAll = "true";
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/summary.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('salesDate'));
	
	 /** for popup BatchTask in page **/
	 <%if( "submitedGenStockOnhandTemp".equals(request.getAttribute("action"))){%>
	    //lockscreen
	    var path = document.getElementById("path").value;
	    /** Init progressbar **/
		$(function() {
			// update the block message 
	        $.blockUI({ message: "<h2>���ѧ����¡��     ��س����ѡ����......</h2>" }); 
		}); 
		    
		//submitedGenStockOnhandTemp
		var url = path+'/jsp/batchTaskAction.do?do=prepare&pageAction=new&initBatchAction=initBatchFromPageByPopup&pageName=<%=BatchTaskConstants.GEN_STOCK_ONHAND_TEMP%>';
		popupFull(url,'submitedGenStockOnhandTemp');
   <%}%>
}

function search(path){
	var form = document.reportsForm;
	   var asOfDateFrom = form.salesDate.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
	   if(asOfDateFrom ==""){ 
		   alert("��سҡ�͡�������ѹ��� As Of");
		   return false;
	   }
	    if(pensCustCodeFrom ==""){ 
		   alert("��سҡ�͡������������ҹ���");
		   return false;
	   } 
	 
	form.action = path + "/jsp/reportsAction.do?do=search";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.reportsForm;
	form.action = path + "/jsp/reportsAction.do?do=export";
	form.submit();
	return true;
}
function genStockOnhandTemp(path){
   var form = document.reportsForm;
   var asOfDateFrom = form.salesDate.value;
   var pensCustCodeFrom = form.pensCustCodeFrom.value;
   
   if(confirm("��س��׹�ѹ��� Gen Stock Onhand Temp")){
	   if(asOfDateFrom ==""){ 
		   alert("��سҡ�͡�������ѹ��� As Of");
		   asOfDateFrom.focus();
		   return false;
	   }
	    if(pensCustCodeFrom ==""){ 
		   alert("��سҡ�͡������������ҹ���");
		   pensCustCodeFrom.focus();
		   return false;
	   } 
		form.action = path + "/jsp/reportsAction.do?do=genStockOnhandTemp&storeType="+storeType;
		form.submit();
		return true;
   }
}

function clearForm(path){
	var form = document.reportsForm;
	form.action = path + "/jsp/reportsAction.do?do=prepare&action=new";
	form.submit();
	return true;
}
function searchBatch(path){
	//unlockScreen
	setTimeout($.unblockUI, 100); 
	 
	var form = document.reportsForm;
	form.action = path + "/jsp/reportsAction.do?do=searchBatch";
	form.submit();
	return true;
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
			<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="<%=pageName %>"/>
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
						<html:form action="/jsp/reportsAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
				    	<table  border="0" cellpadding="3" cellspacing="0" class="body" width="65%">
					        <tr>
							<td align="right"  nowrap>�ҡ �ѹ����� (As Of)<font color="red">*</font> 
							&nbsp;&nbsp;
							<html:text property="bean.salesDate" styleId="salesDate" readonly="true"/>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							</td>
							<td align="left" width="50%" nowrap>
							   <% if("reportEndDateLotus".equalsIgnoreCase(request.getParameter("page"))){ %>
							      �Դ�ͺʵ�͡����ش�ѹ���:
							   	  <html:text property="bean.endDate" styleId="endDate" size="20" styleClass="disableText" readonly="true"/> 
							   	  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							   	       �����Ţ�¶֧�ѹ���:
							   	  <html:text property="bean.endSaleDate" styleId="endSaleDate" size="20" styleClass="disableText" readonly="true"/> 
							   <%} %>
							</td>
							</tr>
					       <tr>
								<td align="right"  nowrap>������ҹ���<font color="red">*</font>
								  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								   <html:text property="bean.pensCustCodeFrom" styleId="pensCustCodeFrom" 
								    size="20" onkeypress="getCustNameKeypress('${pageContext.request.contextPath}',event,this,'pensCustNameFrom')"
								    styleClass="\" autoComplete=\"off"/>
								    &nbsp;
								    <input type="button" name="x1" value="..." onclick="openPopupCustomerAll('${pageContext.request.contextPath}','from','<%=storeType%>','<%=hideAll%>')"/>
								</td>
								<td align="left" width="30%"  nowrap> 
								    <html:text property="bean.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="50"/>
								</td>
							</tr>
							<tr>
								<td align="right" width="30%"  nowrap>
								     Pens Item From &nbsp;&nbsp;<html:text property="bean.pensItemFrom" styleId="pensItemFrom" 
								     styleClass="\" autoComplete=\"off"/>
								     &nbsp;
								    <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from','<%=storeType %>')"/>
								</td>
								<td align="left" width="30%"  nowrap>
								     Pens Item To&nbsp;&nbsp; <html:text property="bean.pensItemTo" styleId="pensItemTo" 
								     styleClass="\" autoComplete=\"off"/>
								     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to','<%=storeType %>')"/>   
								</td>
							</tr>
							<tr>
								<td align="right" width="30%"  nowrap>Group &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								    <html:text property="bean.group" styleId="group" styleClass="\" autoComplete=\"off"/>
								    &nbsp;
								    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}','selectOne','<%=storeType%>')"/>
								     <html:hidden property="bean.groupDesc" styleId="groupDesc" />
								  </td>
								<td align="left" width="30%">  
								      <html:checkbox property="bean.dispHaveQty" />�ʴ�੾����¡�÷���ըӹǹ
								 </td>
						   </tr>
							   <tr>
									<td align="right" width="30%"  nowrap>�ʴ���� &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									    <html:select property="bean.summaryType">
										   <html:option value="GroupCode">Group Code</html:option>
										   <html:option value="PensItem">Pens Item</html:option>
									     </html:select>
									 </td>
									<td align="left" width="30%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </td>    
							   </tr>
						  </table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="60%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="����" class="newPosBtn"> 
								</a>&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtn">
								</a>&nbsp;
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtn">
								</a>
							</td>
							 <td align="right" width="40%" nowrap>
								<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN}) ){%>
									<a href="javascript:genReportEndDate('${pageContext.request.contextPath}')">
									  <input type="button" value="Gen Data ���º��º�Ѻʵ�͡" class="newPosBtn">
									</a>
									<a href="javascript:genStockOnhandTemp('${pageContext.request.contextPath}')">
									  <input type="button" value="Gen Stock Onhand" class="newPosBtn">
									</a>
								<%} %>
							</td>
						</tr>
					</table>
			</div>
			
		<!-- ****** RESULT ***************************************************************** -->
				<%
				String totalPage = "";
				String currentPage = "";
				if(reportsForm.getResults() != null){
					totalPage = String.valueOf(Utils.calcTotalPage(reportsForm.getResults().size(),50));
					 
					String queryStr= request.getQueryString();
					if(queryStr.indexOf("d-") != -1){
						queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
						System.out.println("queryStr:"+queryStr);
						currentPage = request.getParameter(queryStr);
					}
				}//if
				
				/** Case ToatlPage ==1 set currentPage=1 ***/
				if(totalPage.equals("1")){
					currentPage ="1";
				}
				System.out.println("totalPage:"+totalPage);
				System.out.println("currentPage:"+currentPage);
				%>
				     <c:if test="${reportsForm.results != null}">
				
						<c:if test="${reportsForm.bean.summaryType == 'PensItem'}">
						<br/>
							<display:table style="width:100%;" id="item" name="sessionScope.reportsForm.results" defaultsort="0"  defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="�����Ң�" property="storeCode"  sortable="false" class="td_text" style="width:5%"/>
							    <display:column  title="�����Ң�" property="storeName"  sortable="false" class="td_text" style="width:10%"/>
							    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="td_text"  style="width:5%"/>	 
							    <display:column  title="Group" property="group"  sortable="false" class="td_text_center"  style="width:6%"/>	
							    <display:column  title="Begining Qty" property="beginingQty"  sortable="false" class="td_number"  style="width:8%"/>	
							    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="td_number"  style="width:8%"/>	
							    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="td_number"  style="width:8%"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="td_number"  style="width:8%"/>	
							    <display:column  title="Adjust QTY " property="adjustQty"  sortable="false" class="td_number"  style="width:8%"/>	
							    <display:column  title="Stock Short QTY " property="stockShortQty"  sortable="false" class="td_number"  style="width:8%"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="td_number"  style="width:8%"/>	
							    <display:column  title="Price List " property="retailPriceBF"  sortable="false" class="td_number"  style="width:8%"/>	
							    <display:column  title="Amount " property="onhandAmt"  sortable="false" class="td_number"  style="width:8%"/>	
							</display:table>
							 <%if(currentPage.equals(totalPage)){ %>
								<table width="100%" class="resultDisp">		
								    <tr>
								      <td width="5%">&nbsp;</td>
								      <td width="10%">&nbsp;</td>
								      <td width="5%">&nbsp;</td>
								      <td width="6%"><b>Total</b></td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="beginingQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleInQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleReturnQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleOutQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="adjustQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="stockShortQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="onhandQty"/> </td>
								      <td class="td_number_bold" width="8%">&nbsp;</td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="onhandAmt"/> </td>
								    </tr>
								</table>
							 <%} %>
							</c:if>
							
							<c:if test="${reportsForm.bean.summaryType == 'GroupCode'}">
						    <br/>
							<display:table style="width:100%;" id="item" name="sessionScope.reportsForm.results" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="�����Ң�" property="storeCode"  sortable="false" class="td_text"  style="width:8%"/> 
							    <display:column  title="�����Ң�" property="storeName"  sortable="false" class="td_text"  style="width:12%"/> 
							    <display:column  title="Group" property="group"  sortable="false" class="td_text_center" style="width:8%"/>	
							    <display:column  title="Begining Qty" property="beginingQty"  sortable="false" class="td_number" style="width:8%"/>	
							    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="td_number" style="width:8%"/>	
							    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="td_number" style="width:8%"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="td_number" style="width:8%"/>	
							    <display:column  title="Adjust QTY " property="adjustQty"  sortable="false" class="td_number" style="width:8%"/>	
							    <display:column  title="Stock Short QTY " property="stockShortQty"  sortable="false" class="td_number" style="width:8%"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="td_number" style="width:8%"/>	
							    <display:column  title="Price List " property="retailPriceBF"  sortable="false" class="td_number"  style="width:8%"/>	
							    <display:column  title="Amount " property="onhandAmt"  sortable="false" class="td_number"  style="width:8%"/>	
							</display:table>
							<%if(currentPage.equals(totalPage)){ %>
								<table width="100%" class="resultDisp">		
								    <tr>
								      <td width="8%">&nbsp;</td>
								      <td width="12%">&nbsp;</td>
								      <td width="8%"><b>Total</b></td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="beginingQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleInQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleReturnQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleOutQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="adjustQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="stockShortQty"/> </td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="onhandQty"/> </td>
								      <td class="td_number_bold" width="8%">&nbsp;</td>
								      <td class="td_number_bold" width="8%"><bean:write name="summary" property="onhandAmt"/> </td>
								    </tr>
								</table>
							 <%} %>
							</c:if>
					   </c:if>
			
					<!-- hidden field -->
					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="storeType" id="storeType" value="<%=storeType%>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
					
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
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>