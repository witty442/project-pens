<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
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
<jsp:useBean id="stockQueryForm" class="com.isecinc.pens.web.pick.StockQueryForm" scope="session" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/pick_stockPickQuery.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	// new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));

	if(   document.getElementsByName('bean.wareHouse')[0].checked==false 
	   && document.getElementsByName('bean.wareHouse')[1].checked ==false 
	   && document.getElementsByName('bean.wareHouse')[2].checked ==false
	   && document.getElementsByName('bean.wareHouse')[3].checked ==false
	   && document.getElementsByName('bean.wareHouse')[4].checked ==false
	   && document.getElementsByName('bean.wareHouse')[5].checked ==false){
		
		  document.getElementsByName('bean.wareHouse')[0].checked =true;//default W1
	}
	
	swithWareHouse();
	
	<%if( !"".equals(Utils.isNull(stockQueryForm.getBean().getStatus()))){ %>
	     document.getElementsByName('bean.status')[0].value = '<%=stockQueryForm.getBean().getStatus()%>';
	<% }else{ %>
	     document.getElementsByName('bean.status')[0].value = 'ONHAND';
	<%}%>
	
	<%if( !"".equals(Utils.isNull(stockQueryForm.getBean().getSummaryType()))){ %>
         document.getElementsByName('bean.summaryType')[0].value = '<%=stockQueryForm.getBean().getSummaryType()%>';
   <% }else{ %>
         document.getElementsByName('bean.summaryType')[0].value = 'Detail';
	<%}%>
}

function clearForm(path){
	var form = document.stockQueryForm;
	form.action = path + "/jsp/stockQueryAction.do?do=clear";
	form.submit();
	return true;
}

function search(path){
	var form = document.stockQueryForm;
	form.action = path + "/jsp/stockQueryAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.stockQueryForm;
	form.action = path + "/jsp/stockQueryAction.do?do=exportExcel";
	form.submit();
	return true;
}

function openJobPopup(path){
    var param = "";
	url = path + "/jsp/searchJobPopupAction.do?do=prepare&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}
function setStoreMainValue(code,desc){
	//alert(types+":"+desc);
	document.getElementsByName("bean.jobId")[0].value = code;
	document.getElementsByName("bean.name")[0].value = desc;
}

function getJobNameKeypress(e,code){
	var form = document.stockQueryForm;
	if(e != null && e.keyCode == 13){
		if(code.value ==''){
			form.name.value = '';
		}else{
			getJobNameModel(code);
		}
	}
}

function getJobNameModel(code){
	var returnString = "";
	var form = document.stockQueryForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoJob.jsp",
			data : "code=" + code.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	form.name.value = returnString;	
}

function showDiv(id) {
	 id.style.display = 'block';
 }

 function hideDiv(id) {
	 id.style.display = 'none';
 }
 
function swithWareHouse(){
	//alert(document.getElementsByName('bean.wareHouse')[0].checked);
	//alert(document.getElementsByName('bean.wareHouse')[0].value);
	
	if(document.getElementsByName('bean.wareHouse')[0].checked){//W1
		loadStatusList('W1');
		loadSummaryTypeList('W1');
	}else if(document.getElementsByName('bean.wareHouse')[1].checked){//W2{
		loadStatusList('W2');
		loadSummaryTypeList('W2');
	}else if(document.getElementsByName('bean.wareHouse')[2].checked){//W3{
		loadStatusList('W3');
		loadSummaryTypeList('W3');
	}else if(document.getElementsByName('bean.wareHouse')[3].checked){//W4{
		loadStatusList('W4');
		loadSummaryTypeList('W4');
	}else if(document.getElementsByName('bean.wareHouse')[4].checked){//W5{
		loadStatusList('W5');
		loadSummaryTypeList('W5');
	}else if(document.getElementsByName('bean.wareHouse')[5].checked){//W5{
		loadStatusList('W6');
		loadSummaryTypeList('W6');
	}
	
	loadSummaryTypeListW2(document.getElementsByName('bean.status')[0]);
}

function loadStatusList(wareHouse){
	var cboProvince = document.getElementsByName('bean.status')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/statusListAjax.jsp",
			data : "wareHouse=" +wareHouse ,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboProvince.innerHTML=returnString;
			}
		}).responseText;
	});
}

function loadSummaryTypeList(wareHouse){
	var cboProvince = document.getElementsByName('bean.summaryType')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/summaryTypeListAjax.jsp",
			data : "wareHouse=" +wareHouse ,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboProvince.innerHTML=returnString;
			}
		}).responseText;
	});
}

function loadSummaryTypeListW2(status){
	if(document.getElementsByName('bean.wareHouse')[1].checked ==true){
		
		var cboProvince = document.getElementsByName('bean.summaryType')[0];
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/summaryTypeListAjax.jsp",
				data : "wareHouse=W2&status="+status.value ,
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					cboProvince.innerHTML=returnString;
				}
			}).responseText;
		});
	}
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
				<jsp:param name="function" value="stockQuery"/>
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
						<html:form action="/jsp/stockQueryAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                    <td> Warehouse</td>
									<td colspan="3">					
										<html:radio property="bean.wareHouse" value="W1" onclick="swithWareHouse()">W1-<%=PickConstants.getWareHouseDesc("W1") %></html:radio>
										<html:radio property="bean.wareHouse" value="W2" onclick="swithWareHouse()">W2-<%=PickConstants.getWareHouseDesc("W2") %></html:radio>
										<html:radio property="bean.wareHouse" value="W3" onclick="swithWareHouse()">W3-<%=PickConstants.getWareHouseDesc("W3") %></html:radio>
										<html:radio property="bean.wareHouse" value="W4" onclick="swithWareHouse()">W4-<%=PickConstants.getWareHouseDesc("W4") %></html:radio>
										<html:radio property="bean.wareHouse" value="W5" onclick="swithWareHouse()">W5-<%=PickConstants.getWareHouseDesc("W5") %></html:radio>
										<html:radio property="bean.wareHouse" value="W6" onclick="swithWareHouse()">W6-<%=PickConstants.getWareHouseDesc("W6") %></html:radio>
									</td>
								</tr>
						       <tr>
                                    <td> PensItem From</td>
									<td>					
										 <html:text property="bean.pensItemFrom" styleId="pensItemFrom" size="20"/>
									</td>
									<td> 
									    PensItem To    <html:text property="bean.pensItemTo" styleId="pensItemTo" size="20"/>   
									</td>
									<td></td>
								</tr>
								 <tr>
                                    <td> Group Code From</td>
									<td>					
										 <html:text property="bean.groupCodeFrom" styleId="GroupCodeFrom" size="20"/>
									</td>
									<td> 
									    Group Code To  <html:text property="bean.groupCodeTo" styleId="GroupCodeTo" size="20"/>	   
									</td>
									<td></td>
								</tr>
								<tr>
                                    <td> จากเลขที่กล่อง</td>
									<td>					
										 <html:text property="bean.boxNoFrom" styleId="GroupCodeFrom" size="20"/>
									</td>
									<td> 
									    ถึงเลขที่กล่อง    <html:text property="bean.boxNoTo" styleId="GroupCodeTo" size="20"/>	 
									</td>
									<td></td>
								</tr>
								<tr>
                                    <td>รับคืนจาก </td>
									<td colspan="3">
						                <html:text property="bean.jobId" styleId="jobId" size="20" 
						                  onkeypress="getJobNameKeypress(event,this)"
						                  />					    
									     <input type="button" name="x1" value="..." onclick="openJobPopup('${pageContext.request.contextPath}')"/>
									    <html:text property="bean.name" styleId="name" readonly="true" styleClass="disableText" size="50"/>
									</td>
								</tr>	
								<tr>
                                    <td> สถานะ</td>
									<td colspan="3">
										<html:select property="bean.status" styleId="statusListW1" onchange="loadSummaryTypeListW2(this)">
										 </html:select>
									</td>
								</tr>
								<tr>
                                    <td> แสดงตาม  </td>
									<td colspan="3">			
										 <html:select property="bean.summaryType" styleId="summaryTypeListW1">	
									    </html:select>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
										  <input type="button" value=" Export To Excel " class="newPosBtnLong">
										</a>	
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${stockQueryForm.results != null}">
                  	 <c:if test="${stockQueryForm.bean.summaryType =='Detail'}">
							<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
							       <tr>
										<th >No.</th>
										<th >Warehouse.</th>
										<th >Group Code.</th>
										<th >Pens Item</th>
										<th >Wacoal Mat.</th>
										<th >Barcode</th>
										<th >เลขที่กล่อง</th>
										<th >Job Id</th>	
										<th >รับคืนจาก</th>	
									<c:if test="${stockQueryForm.bean.wareHouse =='W3'}">
										<th >หมายเหตุ</th>	
									</c:if>
										<th >Status</th>	
								   </tr>
								  <c:forEach var="results" items="${stockQueryForm.results}" varStatus="rows">
									<c:choose>
										<c:when test="${rows.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									<tr class="<c:out value='${tabclass}'/>">
										    <td class="search_groupCode">${results.no}</td>
											<td class="search_groupCode">${results.wareHouse}</td>
											<td class="search_groupCode">${results.groupCode}</td>
											<td class="search_pensItem">
												${results.pensItem}
											</td>
											<td class="search_materialMaster">
											   ${results.materialMaster}
											</td>
											<td class="search_barcode">
											    ${results.barcode}
											</td>
								            <td class="search_boxNo">${results.boxNo}</td>
								            <td class="search_status">
											  ${results.jobId}
											</td>
											<td class="search_jobname">
											  ${results.name}
											</td>
											<c:if test="${stockQueryForm.bean.wareHouse =='W3'}">
												<td class="search_jobname">
												  ${results.remark}
												</td>
											</c:if>
											<td class="search_status">
											  ${results.statusDesc}
											</td>
									</tr>
								</c:forEach>
								 <tr>
								    <c:if test="${stockQueryForm.bean.wareHouse !='W3'}">
										<td align="right" colspan="9"><b>Total QTY</b></td>	
									</c:if>
									
									<c:if test="${stockQueryForm.bean.wareHouse =='W3'}">
										<td align="right" colspan="10"><b>Total QTY</b></td>	
									</c:if>
									<c:if test="${stockQueryForm.bean.wareHouse =='W4'}">
										<td align="right" colspan="10"><b>Total QTY</b></td>	
									</c:if>
								    <td align="center"><b>${stockQueryForm.bean.totalQty}</b></td>					
								 </tr>
						</table>
					</c:if>
					
					<c:if test="${stockQueryForm.bean.summaryType =='SummaryByBox'}">
							<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch2">
							       <tr>
							            <th >Warehouse.</th>
										<th >เลขที่กล่อง</th>
										<th >รับคืนจาก</th>	
										<th >จำนวน</th>					
								   </tr>
								  <c:forEach var="results" items="${stockQueryForm.results}" varStatus="rows">
									<c:choose>
										<c:when test="${rows.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									
										<tr class="<c:out value='${tabclass}'/>">
										    <td class="search_groupCode">${results.wareHouse}</td>
											<td class="search2_boxNo">${results.boxNo}</td>
											<td class="search2_jobname">
											  ${results.name}
											</td>
											<td class="search2_qty">
											  ${results.onhandQty}
											</td>
										</tr>
								</c:forEach>
							       <tr>
							            <td ></td>
										<td ></td>
										<td class="search2_boxNo"><b>Total QTY</b></td>	
										<td class="search3_groupCode" align="center"><b>${stockQueryForm.bean.totalQty}</b></td>					
								   </tr>
						</table>
						
					</c:if>
					
					<c:if test="${stockQueryForm.bean.summaryType =='SummaryByPensItem'}">
							<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch3">
							       <tr>
							            <th >Warehouse.</th>
										<th >Pens Item</th>
										<th >Group Code</th>	
										<th >จำนวน</th>					
								   </tr>
								  <c:forEach var="results" items="${stockQueryForm.results}" varStatus="rows">
									<c:choose>
										<c:when test="${rows.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									
										<tr class="<c:out value='${tabclass}'/>">
										    <td class="search_groupCode">${results.wareHouse}</td>
											<td class="search3_pensItem">${results.pensItem}</td>
											<td class="search3_groupCode">
											  ${results.groupCode}
											</td>
											<td class="search3_qty">
											  ${results.onhandQty}
											</td>
										</tr>
								</c:forEach>
								 <tr>
										<td ></td>
										<td ></td>
										<td class="search3_pensItem"><b>Total QTY</b></td>	
										<td class="search3_groupCode" align="center"><b>${stockQueryForm.bean.totalQty}</b></td>					
								   </tr>
						</table>
					</c:if>
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