<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<jsp:useBean id="importForm" class="com.isecinc.pens.web.imports.ImportForm" scope="session" />
<%
  ImportDAO importDAO = new ImportDAO();
  List<References> storeTypeList = importDAO.getStoreTypeList();
  pageContext.setAttribute("storeTypeList",storeTypeList,PageContext.PAGE_SCOPE);
  
  List<References> storeList = importDAO.getStoreList();
  pageContext.setAttribute("storeList",storeList,PageContext.PAGE_SCOPE);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script>
function loadMe(){
<% if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
   new Epoch('epoch_popup','th',document.getElementById('countDate'));
<%}if("return_wacoal".equalsIgnoreCase(request.getParameter("page"))) {%>
   new Epoch('epoch_popup','th',document.getElementById('importDate'));
   
   loadStoreList();
   <%if("new".equals(request.getParameter("action"))){%>
       document.getElementsByName('storeType')[0].value = '020047';//default
   <%}%>
<%} %>
}

function loadStoreList(){
	
	var storeCode = document.getElementsByName('storeCode')[0];
	///alert("StoreType:"+document.getElementsByName('storeType')[0].value);
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/storeListByStoreType.jsp",
			data : "refId=" + document.getElementsByName('storeType')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				storeCode.innerHTML=returnString;
			}
		}).responseText;
	});
}

function importExcel(path,noCheckError){
	var form = document.importForm;
	var extension = '';
	if(form.dataFile.value.indexOf(".") > 0){
		extension = form.dataFile.value.substring(form.dataFile.value.lastIndexOf(".") + 1).toLowerCase();
		//alert(extension);
	}
	
	<% if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
	    var countDate = form.countDate;
	    var custCode = form.custCode;
	   // var cond4 = document.getElementsByName("salesBean.condName4")[0];
	    var custName = custCode.options[custCode.selectedIndex].text;
	    
	    if(countDate.value ==''){
	    	alert("��س��к��ѹ��� Count Date");
	    	countDate.focus();
	    	return false;
	    }
	    
	    if(custCode.value ==''){
	    	alert("��س��к� custCode");
	    	custCode.focus();
	    	return false;
	    }
	    
	    var fullFileName = form.dataFile.value;
	    var fileName = fullFileName.substring(fullFileName.lastIndexOf("\\") + 1,fullFileName.length).toLowerCase();
	    //alert(fileName);
	    var returnString = "0";
	    $(function(){
			var getData = $.ajax({
					url: "${pageContext.request.contextPath}/jsp/ajax/validatePhysical.jsp",
					data : "countDate=" +countDate.value + "&custCode=" + custCode.value+"&fileName="+fileName,
					async: false,
					cache: false,
					success: function(getData){
						returnString = jQuery.trim(getData);
					}
				}).responseText;
	    });
	    
	    if(returnString =="-1"){
	    	alert("File Name:"+fileName+"�١ Upload�����  ��س���䢪���������� �ҡ��ͧ��� Upload ");
	    	return false;
	    }else if(returnString =="-2"){
	    	if( !confirm("������ �ѹ���:"+countDate.value+" ��ҹ��� :"+custName+" �١ Upload �����  ��س��׹�ѹ��ҵ�ͧ��� Upload ����������к�")){
	    		return false;
	    	}
	    }
	    
	
	<%}else if("onhand".equalsIgnoreCase(request.getParameter("page"))) {%>
		if(form.dataFile.value == '' || extension != "txt"){
			alert("��س����͡�����ʡ�� .txt");
			return;
		}
		
	<%}else if("return_wacoal".equalsIgnoreCase(request.getParameter("page"))) {%>
		var boxNo = form.boxNo;
	 
	    /* if(boxNo.value ==''){
	    	alert("��س��к� boxNo");
	    	boxNo.focus();
	    	return false;
	    } */
	    if(form.dataFile.value != '' && (extension == "xls" || extension == "xlsx") ){
		}else{
			alert("��س����͡�����ʡ��  xls ����  xlsx ");
			return;
		}
	    
     <%}else{ %>
       if(form.dataFile.value != '' && (extension == "xls" || extension == "xlsx") ){
		}else{
			alert("��س����͡�����ʡ��  xls ����  xlsx ");
			return;
		}
     
     <% } %>
	form.action = path + "/jsp/importAction.do?do=importExcel&page=<%=request.getParameter("page")%>&NO_CHECK_ERROR="+noCheckError;
	form.submit();
	return true;
}

function exportReturnWacoal(path){
	var form = document.importForm;
	form.action = path + "/jsp/importAction.do?do=exportReturnWacoal&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.importForm;
	form.reset();
	form.action = path + "/jsp/importAction.do?do=prepare&action=new&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
}

</script>



</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe(); MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
	    	<%if("onhand".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportBMEFromWacoal"/>
				</jsp:include>
			<%}else if("master".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportBMEMaster"/>
				</jsp:include>
			<%}else if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportBMEPhysical"/>
				</jsp:include>
			<%}else if("bigc".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportBMEFromBigC"/>
				</jsp:include>
			<%}else if("tops".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportBMEFromTops"/>
				</jsp:include>
		   <%}else if("return_wacoal".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportReturnWacoal"/>
				</jsp:include>
			<%}else{%>
				<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ImportBMEFromLotus"/>
				</jsp:include>
			<%} %>
			
			<!-- Div ID Waiting -->
			<DIV id="div_waiting" style="display:none">
			   <B> ��س����ѡ����.......................</B>
			</DIV>
			
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
						<html:form action="/jsp/importAction" enctype="multipart/form-data">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						<% if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
							<tr>
								<td align="right" width="40%">�ѹ���Ѻʵ�͡��ԧ<font color="red">*</font></td>
								<td valign="top" align="left"><html:text property="countDate" styleId="countDate"/></td>
							</tr>
							<tr>
								<td align="right" width="40%">������ҹ���<font color="red">*</font></td>
								<td valign="top" align="left">
								    <html:select property="custCode">
										<html:options collection="storeList" property="key" labelProperty="name"/>
								     </html:select>
								</td>
							</tr>
						<% }else if("return_wacoal".equalsIgnoreCase(request.getParameter("page"))) {%>
						    <input type="hidden" name ="storeCodeTemp" >
							<tr>
								<td align="right" width="40%">��ҧ<font color="red">*</font></td>
								<td valign="top" align="left">
								    <html:select property="storeType" onchange="loadStoreList();">
										<html:options collection="storeTypeList" property="key" labelProperty="name"/>
								     </html:select>
								</td>
							</tr>
							<tr>
								<td align="right" width="40%">������ҹ���<font color="red">*</font></td>
								<td valign="top" align="left">
								    <html:select property="storeCode">
										<html:options collection="storeList" property="key" labelProperty="name"/>
								     </html:select>
								</td>
							</tr>
							<tr>
								<td align="right" width="40%">Box No</td>
								<td valign="top" align="left">
								    <html:text property="boxNo" styleId="boxNo"/>
								</td>
							</tr>
						<%} %>
							<tr>
								<td align="right" width="40%">���͡���&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:file property="dataFile" styleClass="" style="width:300px;height:21px"/>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<input type="button" value="  Upload  " class="newPosBtnLong" onclick="javascript:importExcel('${pageContext.request.contextPath}','')">
									<input type="button" value="  Clear  " class="newPosBtnLong" onclick="javascript:clearForm('${pageContext.request.contextPath}')">
								    <% if("onhand".equalsIgnoreCase(request.getParameter("page"))) {%>
								       <input type="button" value="�׹�ѹ Upload ��������ŷ�� ERROR" class="newPosBtnLong" onclick="javascript:importExcel('${pageContext.request.contextPath}','NO_CHECK_ERROR')">
								    <% }else if("return_wacoal".equalsIgnoreCase(request.getParameter("page"))) {%>
								        <input type="button" value="Export to Excel" class="newPosBtnLong" onclick="javascript:exportReturnWacoal('${pageContext.request.contextPath}')">
								    <%} %>
								</td>
							</tr>
						</table>

						<!-- RESULT -->
						<br/>
						<c:if test="${importForm.imported == true}">
	                        <table align="center" border="0" cellpadding="3" cellspacing="1" width="100%">
								<tr>
									<th colspan="7"  align="left">�ӹǹ��¡�÷�����  ${importForm.totalSize} ��¡��</th>
								</tr>
							</table>
						</c:if>
						
						<!-- ************************* Transaction From Lotus**************************************** -->
						<c:if test="${importForm.summaryLotusErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="7"  align="left"><font color="red">�ӹǹ��¡�÷���������ö import ��   ${importForm.summaryLotusErrorSize} ��¡�� </font></th>
							</tr>
							<tr>
								<th width="5%">Row In Excel</th>
								<th width="10%">Sales Date</th>
								<th width="5%">Store No</th>
								<th width="25%">Store Name</th>
								<th width="30%">Description</th>
								<th width="5%">QTY</th>
								<th width="25%">Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summaryErrorList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>
									<td align="center">${results.salesDate}</td>
									<td align="left">${results.storeNo}</td>
									<td align="left">${results.storeName}</td>
									<td align="left">${results.description}</td>
									<td align="right">${results.qty}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
                        <c:if test="${importForm.summaryLotusSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="7"  align="left">�ӹǹ��¡�÷������ö import ��   ${importForm.summaryLotusSuccessSize} ��¡��</th>
							</tr>
							<tr>
								<th width="5%">Row In Excel</th>
								<th width="10%">Sales Date</th>
								<th width="5%">Store No</th>
								<th width="25%">Store Name</th>
								<th width="30%">Description</th>
								<th width="5%">QTY</th>
								<th width="25%">Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summarySuccessList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>
									<td align="center">${results.salesDate}</td>
									<td align="left">${results.storeNo}</td>
									<td align="left">${results.storeName}</td>
									<td align="left">${results.description}</td>
									<td align="right">${results.qty}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						<!-- ************************* Transaction From Lotus**************************************** -->
						
						<!-- ************************* Transaction From BigC**************************************** -->
						<c:if test="${importForm.summaryBigCErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="7"  align="left"><font color="red">�ӹǹ��¡�÷���������ö import ��   ${importForm.summaryBigCErrorSize} ��¡�� </font></th>
							</tr>
							<tr>
								<th width="5%">Row In Excel</th>
								<th width="10%">Sales Date</th>
								<th width="5%">Store No</th>
								<th width="25%">Store Name</th>
								<th width="30%">Description</th>
								<th width="5%">QTY</th>
								<th width="25%">Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summaryErrorList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>
									<td align="center">${results.salesDate}</td>
									<td align="left">${results.storeNo}</td>
									<td align="left">${results.storeName}</td>
									<td align="left">${results.description}</td>
									<td align="right">${results.qty}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
                        <c:if test="${importForm.summaryBigCSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="7"  align="left">�ӹǹ��¡�÷������ö import ��   ${importForm.summaryBigCSuccessSize} ��¡��</th>
							</tr>
							<tr>
								<th width="5%">Row In Excel</th>
								<th width="10%">Sales Date</th>
								<th width="5%">Store No</th>
								<th width="25%">Store Name</th>
								<th width="30%">Description</th>
								<th width="5%">QTY</th>
								<th width="25%">Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summarySuccessList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>
									<td align="center">${results.salesDate}</td>
									<td align="left">${results.storeNo}</td>
									<td align="left">${results.storeName}</td>
									<td align="left">${results.description}</td>
									<td align="right">${results.qty}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						<!-- ************************* Transaction From BigC**************************************** -->
						
						<!-- ************************* Physical From ******************************************* -->
						<c:if test="${importForm.summaryPhyListErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left"> <font color="red">�ӹǹ��¡�÷���������ö import ��  : ${importForm.summaryPhyListErrorSize} ��¡�� </font> </th>
							</tr>
							<tr>
							    <th width="5%">Row In Excel</th>
								<th width="30%">Description</th>
								<th width="25%">Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summaryErrorList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>		
									<td align="left">${results.description}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<c:if test="${importForm.summaryPhyListSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left">�ӹǹ��¡�÷������ö import ��  : ${importForm.summaryPhyListSuccessSize} ��¡�� </font> </th>
							</tr>
							<tr>
							    <th width="5%">Row In Excel</th>
								<th width="30%">Description</th>
								<th width="25%">Error Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summarySuccessList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>		
									<td align="left">${results.description}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<!-- ************************* Physical From ******************************************* -->
						
						
						<!-- ************************* Wacoal From ******************************************* -->
						<c:if test="${importForm.summaryWacoalListErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="9"  align="left"> <font color="red">�ӹǹ��¡�÷���������ö import ��  : ${importForm.summaryWacoalListErrorSize} ��¡�� </font> </th>
							</tr>
							<tr>
							    <th width="5%">Row</th>
								<th width="8%">materialMaster</th>
								<th width="8%">barcode </th>
								<th width="10%">onhandQty </th>
								<th width="10%">wholePriceBF </th>
								<th width="10%">retailPriceBF </th>
								<th width="10%">item </th>
								<th width="15%">itemDesc </th>
								<th width="24%">Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summaryErrorList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>		
									<td align="left">${results.onhandSummary.materialMaster}</td>
									<td align="left">${results.onhandSummary.barcode}</td>
									<td align="left">${results.onhandSummary.onhandQty}</td>
									<td align="left">${results.onhandSummary.wholePriceBF}</td>
									<td align="left">${results.onhandSummary.retailPriceBF}</td>
									<td align="left">${results.onhandSummary.item}</td>
									<td align="left">${results.onhandSummary.itemDesc}</td>
									<td align="left">
									  <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
									    ${rows2.index}:${results2.message}<br/>
									 </c:forEach>
									</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<c:if test="${importForm.summaryWacoalListSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="9"  align="left">�ӹǹ��¡�÷������ö import ��  : ${importForm.summaryWacoalListSuccessSize} ��¡��  </th>
							</tr>
							<tr>
							    <th width="5%">Row</th>
								<th width="8%">materialMaster</th>
								<th width="8%">barcode </th>
								<th width="10%">onhandQty </th>
								<th width="10%">wholePriceBF </th>
								<th width="10%">retailPriceBF </th>
								<th width="10%">item </th>
								<th width="15%">itemDesc </th>
								<th width="24%">Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summarySuccessList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>		
									<td align="left">${results.onhandSummary.materialMaster}</td>
									<td align="left">${results.onhandSummary.barcode}</td>
									<td align="left">${results.onhandSummary.onhandQty}</td>
									<td align="left">${results.onhandSummary.wholePriceBF}</td>
									<td align="left">${results.onhandSummary.retailPriceBF}</td>
									<td align="left">${results.onhandSummary.item}</td>
									<td align="left">${results.onhandSummary.itemDesc}</td>
									<td align="left">
									 <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
									    ${rows2.index}:${results2.message}<br/>
									 </c:forEach>
									</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<!-- ************************* Wacoal From ******************************************* -->
	
	                   <!-- ************************* Return Wacoal  From ******************************************* -->
						<c:if test="${importForm.summaryReturnWacoalListErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left"> <font color="red">�ӹǹ��¡�÷���������ö import ��  : ${importForm.summaryReturnWacoalListErrorSize} ��¡�� </font> </th>
							</tr>
							<tr>
							    <th width="5%">Row In Excel</th>
								<th width="30%">Description</th>
								<th width="25%">Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summaryErrorList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>		
									<td align="left">${results.onhandSummary.materialMaster}</td>
									<td align="left">
									 <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
									    ${rows2.index}:${results2.message}<br/>
									 </c:forEach>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<c:if test="${importForm.summaryReturnWacoalListSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left">�ӹǹ��¡�÷������ö import ��  : ${importForm.summaryReturnWacoalListSuccessSize} ��¡�� </font> </th>
							</tr>
							<tr>
							    <th width="5%">Row In Excel</th>
								<th width="30%">Description</th>
								<th width="25%">Error Message </th>
							</tr>
							<c:forEach var="results" items="${importForm.summarySuccessList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td>${results.row}</td>		
									<td align="left">${results.onhandSummary.materialMaster}</td>
									<td align="left">
									 <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
									    ${rows2.index}:${results2.message}<br/>
									 </c:forEach>
									</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<!-- ************************* Return Wacoal From ******************************************* -->
						
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<td align="left" class="footer">&nbsp;</td>
							</tr>
						</table>

						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
									<input type="button" value="�Դ˹�Ҩ�" class="newNegBtn">
									</a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
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