<%@page import="com.isecinc.pens.web.manualstock.ManualStockBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="manualStockForm" class="com.isecinc.pens.web.manualstock.ManualStockForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "manualStockForm");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/DateUtils.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('transDateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('transDateTo'));
}
function clearForm(path){
	var form = document.manualStockForm;
	form.action = path + "/jsp/manualStockAction.do?do=clear2";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.manualStockForm;
	form.action = path + "/jsp/manualStockAction.do?do=exportReport&action=newsearch";
	form.submit();
	return true;
}

function search(path){
	var form = document.manualStockForm;

	form.action = path + "/jsp/manualStockAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.manualStockForm;
	form.action = path + "/jsp/manualStockAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}

function openEdit(path,docNo){
	var form = document.manualStockForm;
	var param ="&docNo="+docNo
	form.action = path + "/jsp/manualStockAction.do?do=prepare"+param;
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
	var form = document.manualStockForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){
	var form = document.manualStockForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.manualStockForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("storeCode" == fieldName){
				form.storeCode.value = '';
				form.storeName.value = "";
			}
		}else{
		   getCustName(custCode,fieldName);
		}
	}
}
function getCustName(custCode,fieldName){
	var returnString = "";
	var form = document.manualStockForm;
	var storeGroup = form.custGroup.value;
		var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameWithSubInvAjax.jsp",
				data : "custCode=" + custCode.value+"&storeGroup="+storeGroup,
				async: false,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		
		if("storeCode" == fieldName){
			if(returnString !=''){
				var retArr = returnString.split("|");
				form.storeName.value = retArr[0];
				
				if(retArr[1]=='' || retArr[2]==''){
					if(retArr[1]==''){
						alert("��辺������ Store no  �������ö�ӧҹ�����");
					}
					if(retArr[2]==''){
						alert("��辺������ Sub Inventory  �������ö�ӧҹ�����");
					}
					form.storeCode.value = '';
					form.storeName.value = "";
					
				}
			}else{
				alert("��辺������");
				form.storeCode.focus();
				form.storeCode.value ="";
				form.storeName.value = "";
			}
		}
}
function resetStore(){
	var form = document.manualStockForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
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
				<jsp:param name="function" value="ManualStock"/>
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
						<html:form action="/jsp/manualStockAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						      <tr>
                                    <td> Transaction Date </td>
									<td>		
										 <html:text property="bean.transDateFrom" styleId="createDateFrom"/>
										-
										 <html:text property="bean.transDateTo" styleId="createDateTo"/>
									</td>
								</tr>
								<tr>
                                    <td> �������ҹ��� </td>
									<td>		
										 <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
									<td >������ҹ���
									</td>
									<td align="left"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									
									  <html:hidden property="bean.subInv" styleId="subInv"/>
									  <html:hidden property="bean.storeNo" styleId="storeNo"/>
									</td>
								</tr>
								<tr>
                                    <td> Group Code </td>
									<td>		
										 <html:text property="bean.groupCode" styleId="groupCode"/>
									</td>
								</tr>
								
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ����      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:openEdit('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ������¡������   " class="newPosBtnLong">
										</a>	
										<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
						                   <input type="button" value="  Export  " class="newPosBtnLong"> 
						                </a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${manualStockForm.results != null}">
                  	 <jsp:include page="../pageing.jsp">
				       <jsp:param name="totalPage" value="<%=manualStockForm.getTotalPage() %>"/>
				       <jsp:param name="totalRecord" value="<%=manualStockForm.getTotalRecord() %>"/>
				       <jsp:param name="currPage" value="<%=manualStockForm.getCurrPage() %>"/>
				       <jsp:param name="startRec" value="<%=manualStockForm.getStartRec() %>"/>
				       <jsp:param name="endRec" value="<%=manualStockForm.getEndRec() %>"/>
			         </jsp:include>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
						           <th >Action</th>	
									<th >No</th>
									<th >�ѹ�����</th>
									<th >�ѹ��� Scan</th>
									<th >Doc No</th>
									<th >�����</th>
									<th >���ʡ����</th>
									<th >������ҹ���</th>
									<th >������ҹ���</th>
									<th >Barcode</th>
									<th >Group Code</th>
									<th >Material Master</th>
									<th >Pens Item</th>
									<th >�ӹǹ��鹷����</th>		
									<th >�Ҥһ�ա��͹ Vat</th>	
									<th >Remark</th>					
							   </tr>
							<c:forEach var="results" items="${manualStockForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
									<tr class="<c:out value='${tabclass}'/>">
									<td class="td_text_center" width="7%">
											 <c:if test="${results.status != 'AB'}">
												 <a href="javascript:openEdit('${pageContext.request.contextPath}','${results.docNo}','edit')">
												         ���
												 </a>
											</c:if>
											<c:if test="${results.status == 'AB'}">
												 <a href="javascript:openEdit('${pageContext.request.contextPath}','${results.docNo}','view')">
												         ��
												 </a>
											</c:if>
										</td>
										<td class="td_text_center" width="5%">${results.no}</td>
										<td class="td_text_center" width="5%">${results.saleDate}
										   <input type="hidden" name ="saleDate" id ="saleDate" value="${results.saleDate}" class="disableText" size="10">
										</td>
										<td class="td_text_center" width="5%">${results.createDate}</td>
										<td class="td_text_center" width="7%">${results.docNo}
										   <input type="hidden" name ="docNo" id ="docNo" value="${results.docNo}" class="disableText" size="15">
										</td>
										<td class="td_text_center" width="5%">${results.custGroup}
										   <input type="hidden" name ="custGroup" id ="custGroup"  value="${results.custGroup}" class="disableText" size="20">
										</td>
										<td class="td_text_center" width="5%">${results.custGroupName}</td>
										<td class="td_text_center" width="7%">${results.storeCode}
										   <input type="hidden" name ="storeCode" id ="storeCode"  value="${results.storeCode}" class="disableText" size="15">
										</td>
										<td class="td_text" width="10%">${results.storeName}</td>
										<td class="td_text_center" width="8%">${results.barcode}
										   <input type="hidden" name ="barcode" id ="barcode"  value="${results.barcode}" class="disableText" size="15">
										</td>
										<td class="td_text_center" width="7%">${results.groupCode}
										   <input type="hidden" name ="groupCode" id ="groupCode"  value="${results.groupCode}" class="disableText" size="10">
										</td>
										<td class="td_text"  width="8%">${results.materialMaster}
										   <input type="hidden" name ="materialMaster" id ="materialMaster"  value="${results.materialMaster}" class="disableText" size="15">
										</td>
										<td class="td_text" width="8%">${results.pensItem}
										   <input type="hidden" name ="pensItem" id ="pensItem"  value="${results.pensItem}" class="disableText" size="10">
										</td>
										<td class="td_text_right"  width="5%">
										    ${results.qty}
										</td>
										<td class="td_number"  width="10%">
										    ${results.retailPriceBF}
										</td>
										<td class="td_text"  width="5%">
										    ${results.remark}
										</td>
									</tr>
							  </c:forEach>
							  <%if(manualStockForm.getCurrPage()==manualStockForm.getTotalPage()) {
							      ManualStockBean summary = manualStockForm.getSummary();
							  %>
								  <tr class="">
								       <td class=""></td> 
									   <td class=""></td> 
									   <td class=""></td>
								       <td class=""></td>
								       <td class=""></td>
								       <td class=""></td>
								       <td class=""></td>
								       <td class=""></td>
								       <td class=""></td>
								       <td class=""></td>
								       <td class=""></td>
								       <td class=""></td>
									   <td class="hilight_text" align="right">
										  <B> Total </B>
										</td>
										<td class="hilight_text" align="right">
										 <B> <%=summary.getQty() %></B>
										</td>
										<td class=""></td>
								</tr>
							<%} %>
					</table>
							
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