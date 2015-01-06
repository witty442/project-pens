<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
<%@page import="com.isecinc.pens.web.order.OrderAction"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
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
<jsp:useBean id="pickStockForm" class="com.isecinc.pens.web.pick.PickStockForm" scope="session" />
<%

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/pick_stock.css" type="text/css" />

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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
	 sumQty();
}
function clearForm(path){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=clear";
	form.submit();
	return true;
}
function back(path){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=exportExcel";
	form.submit();
	return true;
}

function save(path){
	var form = document.pickStockForm;
	var pickUser =$('#pickUser').val();
	var storeCode =$('#storeName').val();
	var storeNo =$('#storeNo').val();
	var subInv =$('#subInv').val();
	
	if(pickUser ==""){
		alert("��سҡ�͡ ����ԡ");
		return false;
	}
	if(storeCode ==""){
		alert("��سҡ�͡ ������ҹ���");
		return false;
	}
	if(subInv ==""){
		alert("��辺������ Sub Inventory  �������ö�ӧҹ�����");
		return false;
	}
	if(storeNo ==""){
		alert("��辺������ Store no  �������ö�ӧҹ�����");
		return false;
	}
	if(confirm("�ѹ�ѹ��úѹ�֡������")){
	   form.action = path + "/jsp/pickStockAction.do?do=save";
	   form.submit();
	   return true;
	}
	return false;
}

function cancel(path){
	if(confirm("�׹�ѹ���¡��ԡ��¡�ù��")){
		var form = document.pickStockForm;
		form.action = path + "/jsp/pickStockAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function confirmAction(path){
	var form = document.pickStockForm;
	if(confirm("�ѹ�ѹ��� Confirm ������")){
		 form.action = path + "/jsp/pickStockAction.do?do=confirmAction";
		 form.submit();
		 return true;
	}
	return false;
}


function search(path){
	var form = document.pickStockForm;
	var transactionDate =$('#transactionDate').val();
	var storeCode =$('#storeCode').val();
	if(transactionDate ==""){
		alert("��سҡ�͡�ѹ���  Transaction Date");
		return false;
	}
	if(storeCode ==""){
		alert("��سҡ�͡ ��ҹ���");
		return false;
	}
	
	form.action = path + "/jsp/pickStockAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function gotoPage(path,pageNumber){
	var form = document.pickStockForm;
   // form.action = path + "/jsp/pickStockAction.do?do=search&pageNumber="+pageNumber+"&prevPageNumber="+form.pageNumber.value+"&totalQtyCurPage="+document.getElementsByName("totalQtyCurPage")[0].value;
	form.action = path + "/jsp/pickStockAction.do?do=search&pageNumber="+pageNumber;
	
   form.submit();
    return true;
}



function openPopupProduct(path,seqNo,types){
	var param = "&types="+types+"&seqNo="+seqNo;
	url = path + "/jsp/searchProductPopupAction.do?do=prepare2&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}
function isNum(obj){
  if(obj.value != ""){
	var newNum = parseInt(obj.value);
	if(isNaN(newNum)){
		alert('����͡��੾�е���Ţ��ҹ��');
		obj.value = "";
		obj.focus();
		return false;
	}else{return true;}
   }
  return true;
}

function chkQtyKeypress(obj,e,row){
	//alert(obj.value);
	if(e != null && e.keyCode == 13){
		return validateQty(obj,row);
	}
}

function validateQty(obj,row){
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	
	var r = isNum(obj);
	var onhandQtyObj = document.getElementsByName("onhandQty");
	//alert(onhandQtyObj[row].value);
	if(r){
		//validate Onhand Qty
		var onhandQty = parseInt(onhandQtyObj[row].value);
		var currQty = parseInt(obj.value);
		if(currQty > onhandQty){
			alert("�ӹǹ��� QTY("+currQty+") ���ҡ���  Onhand QTY("+onhandQty+")");
			//rows[row+1].className ="lineError";
			obj.value = "";
			obj.focus();
			return false;
		}
		
		sumQty();
	}
	return true;
}

function sumQty(){
	var qtyObj = document.getElementsByName("qty");
	var totalQtyNotInCurPage = parseInt(document.getElementsByName("totalQtyNotInCurPage")[0].value);
	//alert(totalQtyNotCurPage);
	var sumCurPageQty = 0;
	for(var i=0;i<qtyObj.length;i++){
		if(qtyObj[i].value != '')
			sumCurPageQty = sumCurPageQty + parseInt(qtyObj[i].value);
	}
	//cur Page
	document.getElementsByName("curPageQty")[0].value = sumCurPageQty;
	//total All show
	document.getElementsByName("bean.totalQty")[0].value = totalQtyNotInCurPage + sumCurPageQty ;
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/pick/setSumQtyByPageAjax.jsp",
			data : "pageNumber=" + document.getElementsByName("pageNumber")[0].value +
			       "&curPageQty=" + document.getElementsByName("curPageQty")[0].value +
			       "&totalQtyNotInCurPage=" + document.getElementsByName("totalQtyNotInCurPage")[0].value,
			//async: false,
			cache: true,
			success: function(){
			}
		}).responseText;
	});
}

function openPopupCustomer(path,types,storeType){
	var form = document.pickStockForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){

	var form = document.pickStockForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
	
	if(storeNo=='' || subInv==''){
		if(storeNo==''){
			alert("��辺������ Store no  �������ö�ӧҹ�����");
		}
		if(subInv==''){
			alert("��辺������ Sub Inventory  �������ö�ӧҹ�����");
		}
		form.storeCode.value = '';
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}else{
	   form.storeNo.value = storeNo;
	   form.subInv.value = subInv;
	}
	
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.pickStockForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("storeCode" == fieldName){
				form.storeCode.value = '';
				form.storeName.value = "";
				form.storeNo.value = "";
				form.subInv.value = "";
			}
		}else{
		  getCustName(custCode,fieldName);
		}
	}
}

function getCustName(custCode,fieldName){
	var returnString = "";
	var form = document.pickStockForm;
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
					form.storeNo.value = "";
					form.subInv.value = "";
				}else{
					form.storeNo.value = retArr[1];
					form.subInv.value = retArr[2];
				}
				
			}else{
				alert("��辺������");
				form.storeCode.focus();
				form.storeCode.value ="";
				form.storeName.value = "";
				form.storeNo.value = "";
				form.subInv.value = "";
			}
		}
	
}
function resetStore(){
	var form = document.pickStockForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
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
				<jsp:param name="function" value="pickStock"/>
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
						<html:form action="/jsp/pickStockAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                  <td colspan="3" align="center"><font size="3"><b>�ԡ��µ��</b></font></td>
							   </tr>
						       <tr>
                                    <td>Issue request Date</td>
                                     <td>
                                       <html:text property="bean.issueReqDate" styleId="issueReqDate" size="20"  readonly="true" styleClass="disableText"/>
                                     </td>
									<td align="right">Issue request No </td>
									<td align="left">
									 <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20"  readonly="true" styleClass="disableText"/>	  
									</td>
								</tr>
								 <tr>
                                    <td> Issue request status</td>
                                      <td>
                                        <html:text property="bean.issueReqStatusDesc" styleId="issueReqStatusDesc" size="20" readonly="true" styleClass="disableText"/>
                                     </td>
									<td align="right">	 ����ԡ  </td>
									<td align="left">
									  <html:text property="bean.pickUser" styleId="pickUser" size="20" /><font color="red">*</font>	  
									</td>
								</tr>
								<tr>
                                    <td> �������ҹ��� <font color="red">*</font></td>		
								    <td>
										 <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
						           </td>
								<td></td><td></td>
								</tr>
								<tr>
									<td >������ҹ���<font color="red">*</font>
									</td>
									<td align="left" colspan="2"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									</td>
								</tr>
								<tr>
                                    <td> Sub Inventory</td>
									<td colspan="2">
						               <html:text property="bean.subInv" styleId="subInv" size="10" readonly="true" styleClass="disableText"/>
						               Store No <html:text property="bean.storeNo" styleId="storeNo" size="20" readonly="true" styleClass="disableText"/>
									</td>
									
								</tr>	
								
								<tr>
                                    <td > Confrim Issue Date</td>
                                    <td>
						               <html:text property="bean.confirmIssueDate" styleId="confirmIssueDate" size="20" readonly="true" styleClass="disableText"/>
									</td>
									<td></td><td></td>
								</tr>
								<tr>
                                    <td > �����˵� </td>
                                    <td colspan="2"> 
                                      <html:text property="bean.remark" styleId="remark" size="60" />
                                      </td>
									
								</tr>	
						   </table>
						   
					  </div>

				  <!-- Page -->
				    <% 
					    int start = 0;
					    int end = 0;
					    int pageNumber = 1;
					  
				    %>
					<% if(session.getAttribute("totalPage") != null){ 
					
					   int totalPage = ((Integer)session.getAttribute("totalPage")).intValue();
					   int totalRow = ((Integer)session.getAttribute("totalRow")).intValue();
					   int pageSize = PickConstants.ONHAND_PAGE_SIZE;
					   if(Utils.isNull(request.getAttribute("action")).equalsIgnoreCase("newsearch")){
						  pageNumber = 1;
					   }else{
					      pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
					   }
					   
					   start = ((pageNumber-1)*pageSize)+1;
					   end = (pageNumber * pageSize);
					   if(end > totalRow){
						   end = totalRow;
					   }
					%>
					   
					<div align="left">
					   <span class="pagebanner">��¡�÷�����  <%=totalRow %> ��¡��, �ʴ���¡�÷��  <%=start %> �֧  <%=end %>.</span>
					   
					   <span class="pagelinks">
						˹�ҷ�� 
						 <% 
							 for(int r=0;r<totalPage;r++){
								 if(pageNumber ==(r+1)){
							 %>
			 				   <strong><%=(r+1) %></strong>
							 <%}else{ %>
							 
							    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
							    
						 <% }} %>				
						</span>
					</div>
					<%} %>
					
				  <%if(session.getAttribute("results") != null) {%>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
				                    <th >Job ID</th>
				                    <th >Job Name</th>
									<th >�Ţ�����ͧ</th>
									<th >Wacoal Mat.</th>
									<th >Group Code</th>
									<th >PensItem</th>
									<th >Onhand-Qty</th>
									<th >Qty �����ԡ</th>
							</tr>
							<%
							List<PickStock> resultList =(List<PickStock>) session.getAttribute("results");
							int tabindex = 0;
							int no= start;
							String titleDisp ="";
							for(int i=0;i<resultList.size();i++){
							   PickStock o = (PickStock) resultList.get(i);
							 
							   String classStyle = (i%2==0)?"lineO":"lineE";
							%>
							      <tr class="<%=classStyle%>"> 
							            <td class="data_jobId" nowrap><%=o.getJobId() %></td>
							            <td class="data_jobName" nowrap><%=o.getJobName() %></td>
										<td class="data_boxNo" nowrap><%=o.getBoxNo() %></td>
										<td class="data_materialMaster"><%=o.getMaterialMaster() %></td>
										<td class="data_groupCode">
										 <%=o.getGroupCode()%>
										</td>
										<td class="data_pensItem">
										  <%=o.getPensItem() %>
                                         </td>
										<td class="data_onhandQty">
										    <input tabindex="-1" type="hidden" name="onhandQty" value ="<%=o.getOnhandQty() %>"/>
										    <%=o.getOnhandQty() %>
										</td>
										<td class="data_qty">
										  <c:if test="${pickStockForm.bean.canEdit == true}">
											  <input tabindex="1" type="text" name="qty" value ="<%=Utils.isNull(o.getQty()) %>" size="20"  class="enableNumber"
											   onkeypress="chkQtyKeypress(this,event,<%=i%>)"
							                   onchange="validateQty(this,<%=i%>)"
											  />
										  </c:if>
										  
										  <c:if test="${pickStockForm.bean.canEdit == false}"><%=Utils.isNull(o.getQty())  %></c:if>
										</td>
								</tr>
							<%} %>
					</table>
				<%} %>		
					<div align="right">
						<table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="right">	 <span class="pagelinks">��������� :
								<html:text property="bean.totalQty" styleId="totalQty" size="30" styleClass="disableNumber"/>
								<br/>
								<input type="hidden" name="totalQtyNotInCurPage" id="totalQtyNotInCurPage" value="${pickStockForm.bean.totalQtyNotInCurPage}"/>
								<input type="hidden" name = "curPageQty" id="curPageQty"/>
								</span>			
								</td>
							</tr>
						</table>
					</div>	
					
					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
									<tr>
										<td align="left">
					 
									      <c:if test="${pickStockForm.bean.canConfirm == true}">
											<a href="javascript:confirmAction('${pageContext.request.contextPath}')">
											  <input type="button" value="�׹�ѹ issue" class="newPosBtnLong"> 
											 </a>
										 </c:if>	
										 
										  <c:if test="${pickStockForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="�ѹ�֡ request issue" class="newPosBtnLong"> 
											 </a>
										 </c:if>
										 
										 <c:if test="${pickStockForm.bean.canCancel == true}">
											 <a href="javascript:cancel('${pageContext.request.contextPath}')">
											   <input type="button" value="    ¡��ԡ     " class="newPosBtnLong"> 
											 </a>  
										 </c:if>
										 
										 <c:if test="${pickStockForm.bean.issueReqStatus == 'I'}">
											 <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
											   <input type="button" value="    Export     " class="newPosBtnLong"> 
											 </a>  
										 </c:if>	
										 <c:if test="${pickStockForm.bean.issueReqStatus == 'O'}">
											 <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
											   <input type="button" value="    Export     " class="newPosBtnLong"> 
											 </a>  
										 </c:if>	
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   �Դ˹�Ҩ�   " class="newPosBtnLong">
										</a>	
																
										</td>
									</tr>
						</table>
					</div>
		
					<!-- ************************Result ***************************************************-->
					
					<!-- hidden field -->
					<input type="hidden" name="pageNumber" id="pageNumber" value="<%=pageNumber%>"/>
					
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