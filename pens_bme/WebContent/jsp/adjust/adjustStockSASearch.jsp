<%@page import="com.isecinc.pens.bean.AdjustStockSA"%>
<%@page import="com.pens.util.SIdUtils"%>
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
<jsp:useBean id="adjustStockSAForm" class="com.isecinc.pens.web.adjuststock.AdjustStockSAForm" scope="session" />

<%

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
}
function clearForm(path){
	var form = document.adjustStockSAForm;
	form.action = path + "/jsp/adjustStockSAAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.adjustStockSAForm;
	/* var transactionDate =$('#transactionDate').val();
	var storeCode =$('#storeCode').val();
	if(transactionDate ==""){
		alert("กรุณากรอกวันที่  Transaction Date");
		return false;
	}
	if(storeCode ==""){
		alert("กรุณากรอก ร้านค้า");
		return false;
	} */
	
	form.action = path + "/jsp/adjustStockSAAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.adjustStockSAForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/adjustStockSAAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,documentNo,mode){
	var form = document.adjustStockSAForm;
	form.action = path + "/jsp/adjustStockSAAction.do?do=prepare&documentNo="+documentNo+"&mode="+mode;
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
    var param = "&types="+types;
        param += "&storeType="+storeType;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare2&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,types){
	//alert(types+":"+desc);
	if("from" == types){
		document.getElementsByName("adjustStockSA.storeCode")[0].value = code;
		document.getElementsByName("adjustStockSA.storeName")[0].value = desc;
	}
}

function getStoreNameKeypress(e,storeCode){
	var form = document.adjustStockSAForm;
	if(e != null && e.keyCode == 13){
		if(storeCode.value ==''){
			form.storeName.value = '';
		}else{
			getStoreNameModel(storeCode);
		}
	}
}

function getStoreNameModel(storeCode){
	var returnString = "";
	var form = document.adjustStockSAForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameAjax.jsp",
			data : "custCode=" + storeCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	form.storeName.value = returnString;	
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
				<jsp:param name="function" value="adjustStockSA"/>
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
						<html:form action="/jsp/adjustStockSAAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Transaction Date</td>
									<td>						
										  <html:text property="adjustStockSA.transactionDate" styleId="transactionDate" size="20" />
									</td>
								</tr>
								 <tr>
                                    <td> รหัสร้านค้า</td>
									<td >
						                 <html:text property="adjustStockSA.storeCode" styleId="storeCode" size="20" 
						                  onkeypress="getStoreNameKeypress(event,this)"
						                  />					    
									     <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','all')"/>
									    <html:text property="adjustStockSA.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="40"/>
									</td>
								</tr>
								<tr>
                                    <td> Document No</td>
									<td >
						               <html:text property="adjustStockSA.documentNo" styleId="documentNo" size="20" />
									</td>
								</tr>	
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:openEdit('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   เพิ่มรายการใหม่   " class="newPosBtnLong">
										</a>	
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${adjustStockSAForm.resultsSearchSize != 0}">
                  	 <% 
					   int totalPage = adjustStockSAForm.getTotalPage();
					   int totalRecord = adjustStockSAForm.getTotalRecord();
					   int currPage =  adjustStockSAForm.getCurrPage();
					   int startRec = adjustStockSAForm.getStartRec();
					   int endRec = adjustStockSAForm.getEndRec();
					   int no = startRec;
					%> 
					<%= PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %> 
					
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width="100%">
						       <tr>
									<th >No</th>
									<th >Document No</th>
									<th >Trasnaction Date</th>
									<th >Store Code</th>
									<th >Store Name</th>
									<th >Status</th>
									<th >แก้ไข</th>						
							   </tr>
							   <%							
							String tabclass ="";
							List<AdjustStockSA> resultList = adjustStockSAForm.getResultsSearch();
							for(int n=0;n<resultList.size();n++){
								AdjustStockSA item = (AdjustStockSA)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}else{
									tabclass ="lineE";
								}
								%>
								<tr class="<%=tabclass%>">
									<td class="td_text_center"><%=no %></td>
									<td class="td_text_center"><%=item.getDocumentNo() %></td>
									<td class="td_text_center"><%=item.getTransactionDate()%></td>
									<td class="td_text_center"><%=item.getStoreCode() %></td>
									<td class="td_text">
									   <%=item.getStoreName() %>
									</td>
									<td class="td_text_center">
									     <%=item.getStatusDesc() %>
									</td>
									<td class="td_text_center">
									 <c:if test="${results.canEdit == false}">
										  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=item.getDocumentNo()%>','view')">
										          ดู
										  </a>
									  </c:if>
									  <c:if test="${results.canEdit == true}">
										  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=item.getDocumentNo()%>','edit')">
										          แก้ไข
										  </a>
									  </c:if>
									</td>
								</tr>
							  <%no++;
							  } %>
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
					
				<%-- 	<jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
					<input type="hidden" name="path" id="path" value ="${pageContext.request.contextPath}"/>
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