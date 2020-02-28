<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="pickStockGroupForm" class="com.isecinc.pens.web.pick.PickStockForm" scope="session" />
<% 
if(session.getAttribute("statusIssueReqList") == null){
	List<References> billTypeList = new ArrayList();
	References ref = new References("","");
	billTypeList.add(ref);
	billTypeList.addAll(PickConstants.getIssueReqStatusW3List());
	session.setAttribute("statusIssueReqList",billTypeList);
}

if(session.getAttribute("pickTypeList") == null){
	List<References> pickTypeList = new ArrayList();
	pickTypeList.add(new References(PickConstants.PICK_TYPE_GROUP, PickConstants.getStatusDesc(PickConstants.PICK_TYPE_GROUP)));
	session.setAttribute("pickTypeList",pickTypeList);
}

if(session.getAttribute("custGroupList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(GeneralDAO.searchCustGroup( new PopupForm()));
	
	session.setAttribute("custGroupList",billTypeList);
}
if(session.getAttribute("forwarderList") == null){
	//forwarder
	List<PopupForm> forwarderList = new ArrayList<PopupForm>();
	PopupForm refP = new PopupForm("",""); 
	forwarderList.add(refP);
	forwarderList.addAll(GeneralDAO.searchForwarderList( new PopupForm()));
	request.getSession().setAttribute("forwarderList",forwarderList);
}
String pageName = pickStockGroupForm.getBean().getPage();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	<%if("req".equalsIgnoreCase(pageName)){ %>
	   new Epoch('epoch_popup', 'th', document.getElementById('issueReqDate'));
	   new Epoch('epoch_popup', 'th', document.getElementById('confirmIssueDate'));
	<%}else{ %>
	   new Epoch('epoch_popup', 'th', document.getElementById('issueReqDateFrom'));
	   new Epoch('epoch_popup', 'th', document.getElementById('issueReqDateTo'));
    <%}%>
}
function clearForm(path){
	var form = document.pickStockGroupForm;
	form.action = path + "/jsp/pickStockGroupAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.pickStockGroupForm;
	var issueReqDate =$('#issueReqDate').val();
	//var storeCode =$('#storeCode').val();
	/* if(issueReqDate ==""){
		alert("��سҡ�͡�ѹ���  Issue Req Date");
		return false;
	} */
	form.action = path + "/jsp/pickStockGroupAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.pickStockGroupForm;
	form.action = path + "/jsp/pickStockGroupAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,documentNo,issueReqStatus){
	var form = document.pickStockGroupForm;
	form.action = path + "/jsp/pickStockGroupAction.do?do=prepareByGroup&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}

function openConfirm(path,documentNo,issueReqStatus){
	var form = document.pickStockGroupForm;
	form.action = path + "/jsp/pickStockGroupAction.do?do=prepareByGroup&process=confirm&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}
function openComplete(path,documentNo,issueReqStatus){
	var form = document.pickStockGroupForm;
	form.action = path + "/jsp/pickStockGroupAction.do?do=prepareByGroup&process=complete&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
	var form = document.pickStockGroupForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
        param += "&methodName=pickStockGroup";
        
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValuePickStockGroup(code,desc,storeNo,subInv,types){
	var form = document.pickStockGroupForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;

	form.storeNo.value = storeNo;
	form.subInv.value = subInv;
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.pickStockGroupForm;
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
	var form = document.pickStockGroupForm;
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
			    form.storeNo.value = retArr[1];
				form.subInv.value = retArr[2];
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
	var form = document.pickStockGroupForm;
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
	    	  <%if("complete".equalsIgnoreCase(pageName)){ %>
	      	     <jsp:include page="../program.jsp">
				   <jsp:param name="function" value="pickStockGroupComplete"/>
			     </jsp:include>
			 <%}else{ %>
				 <jsp:include page="../program.jsp">
				   <jsp:param name="function" value="pickStockGroup"/>
			     </jsp:include>
			 <%} %>
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
						<html:form action="/jsp/pickStockGroupAction">
						<jsp:include page="../error.jsp"/>
                        <div align="center">
						<%if("req".equalsIgnoreCase(pageName)){ %>
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td align="right">Issue request Date </td>
                                    <td><html:text property="bean.issueReqDate" styleId="issueReqDate" size="20" styleClass="\" autoComplete=\"off"/></td>
									<td align="right"> Issue request No </td>
									<td> <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20" styleClass="\" autoComplete=\"off"/>	</td>
								</tr>
								 <tr>
                                    <td  align="right">Issue request status</td>
                                     <td>
                                      <html:select property="bean.issueReqStatus">
											<html:options collection="statusIssueReqList" property="key" labelProperty="name"/>
									    </html:select>
                                     </td>
									<td  align="right">	 ����ԡ </td>
									<td> <html:text property="bean.pickUser" styleId="pickUser" size="20" styleClass="\" autoComplete=\"off"/></td>
								</tr>
								<tr>
                                    <td align="right"> Confrim Issue Date</td>
									<td > 
						               <html:text property="bean.confirmIssueDate" styleId="confirmIssueDate" size="20"styleClass="\" autoComplete=\"off"/>
									</td>
									<td align="right"> Pick Type </td>
									 <td> 
									    <html:select property="bean.pickType">
											<html:options collection="pickTypeList" property="key" labelProperty="name"/>
									    </html:select>
									  </td>
								</tr>
								<tr>
                                    <td align="right"> �������ҹ���  </td>
                                    <td>
										<html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
						           </td >
						           <td align="right">Invoice No </td>
						           <td > <html:text property="bean.invoiceNo" styleId="invoiceNo" size="20" styleClass="\" autoComplete=\"off"/></td>
								</tr>
								<tr>
									<td  align="right">������ҹ��� </td>
									<td colspan="3">
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')" styleClass="\" autoComplete=\"off"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="60"/>
									
									 <html:hidden property="bean.subInv" styleId="subInv" />
						             <html:hidden property="bean.storeNo" styleId="storeNo" />
									</td>
								</tr>
								<tr>
                                    <td align="right"> �����˵�</td>
									<td colspan="3" > 
						               <html:text property="bean.remark" styleId="remark" size="50" />
									</td>
								</tr>
						   </table>
						 <%} %>
						 <%if("complete".equalsIgnoreCase(pageName)){ %>
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td>
                                      Issue request Date From
                                     </td>
                                     <td>
                                      <html:text property="bean.issueReqDateFrom" styleId="issueReqDateFrom" size="20" />
                                     </td>
									<td>						
									 Issue request Date To<html:text property="bean.issueReqDateTo" styleId="issueReqDateTo" size="20" />	  
									</td>
								</tr>
								<tr>
                                    <td>Issue request No From</td>
                                     <td>
                                      <html:text property="bean.issueReqNoFrom" styleId="issueReqNoFrom" size="20" />
                                     </td>
									<td>						
									 Issue request No To<html:text property="bean.issueReqNoTo" styleId="issueReqNoTo" size="20" />	  
									</td>
								</tr>
								<tr>
                                    <td> �������ҹ���</td>
                                     <td> 
                                       <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
                                    </td>		
								    <td>
									    ������ҹ���  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									  
									   <html:hidden property="bean.subInv" styleId="subInv" />
						               <html:hidden property="bean.storeNo" styleId="storeNo"/>
						           </td>
								</tr>
								<tr>
                                    <td > ����ԡ </td>
                                     <td>
						               <html:text property="bean.pickUser" styleId="pickUser" size="20"/>
									</td>
									<td> Work Step
									    <html:select property="bean.workStep">
											<html:option value="<%=PickConstants.WORK_STEP_POST_BYSALE%>"><%=PickConstants.WORK_STEP_POST_BYSALE %></html:option>
											<html:option value="<%=PickConstants.WORK_STEP_PICK_COMPLETE%>"><%=PickConstants.WORK_STEP_PICK_COMPLETE %></html:option>
									    </html:select>
									  </td>
								</tr>
								
						   </table>
						 <%} %>
						 
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ����      " class="newPosBtnLong"> 
										</a>
										
										<%-- <a href="javascript:openEdit('${pageContext.request.contextPath}','','')">
										  <input type="button" value="   ������¡������ (�ԡ��µ��)  " class="newPosBtnLong">
										</a>	 --%>
										  <%if("req".equalsIgnoreCase(pageName)){ %>
											<a href="javascript:openEdit('${pageContext.request.contextPath}','','')">
											  <input type="button" value="   ������¡������ (�����)  " class="newPosBtnLong">
											</a>
										   <%} %>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>	
									</td>
									</tr>
									<tr>
									 <td align="left">
										<%-- <a href="javascript:openEditAllPartBox('${pageContext.request.contextPath}','','')">
										  <input type="button" value="������¡������ (�ԡ��駡��ͧ (੾�С��ͧ����ա���ԡ�ҧ��ǹ))" class="newPosBtnLong">
										</a> --%>
															
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${pickStockGroupForm.resultsSearch != null}">
                  	<% 
					   int totalPage = pickStockGroupForm.getTotalPage();
					   int totalRecord = pickStockGroupForm.getTotalRecord();
					   int currPage =  pickStockGroupForm.getCurrPage();
					   int startRec = pickStockGroupForm.getStartRec();
					   int endRec = pickStockGroupForm.getEndRec();
					%>
					    
					<div align="left">
					   <span class="pagebanner">��¡�÷�����  <%=totalRecord %> ��¡��, �ʴ���¡�÷��  <%=startRec %> �֧  <%=endRec %>.</span>
					   <span class="pagelinks">
						˹�ҷ�� 
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
                  	<%if("req".equalsIgnoreCase(pageName)){ %>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width="100%">
						       <tr>
									<th >Issue Req Date</th>
									<th >Issue Req No</th>
									<th >Issue Req Status</th>
									<th >Confirm Issue Date</th>
								 	<th >Work Step</th>
									<th >Qty �����ԡ</th>
									<th >Qty ����ԡ���ԧ</th>
									<th >�� Auto-Trans ����</th>
									<th >�����˵�</th>
									<th >���</th>	
									 <th >�׹�ѹ</th>				
							   </tr>
							<c:forEach var="results" items="${pickStockGroupForm.resultsSearch}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text_center">
										   ${results.issueReqDate}
										</td>
										<td class="td_text_center">${results.issueReqNo}</td>
										<td class="td_text_center">
											${results.issueReqStatusDesc}
										</td>
										<td class="td_text_center">
										    ${results.confirmIssueDate}
										</td>
										 <td class="td_text">
										  ${results.workStep}
										</td>
									
										<td class="td_text_center">
										  ${results.totalQty}
										</td>
										<td class="td_text_center">
										  ${results.totalIssueQty}
										</td>
										<td class="td_text_center" width="5%">
								         <c:if test="${results.autoTrans == true}">
								            <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
								         </c:if>
                                        </td>
									    <td class="td_text">
										  ${results.remark}
										</td>
										<td class="td_text_center">
										   <font size="2">
										    <c:if test="${results.pickType == 'GROUP'}">
											    <c:if test="${results.canEdit == false}">
													  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
													          ��
													  </a>
												  </c:if>
												  <c:if test="${results.canEdit == true}">
													  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
													          ���
													  </a>
												  </c:if>
										    </c:if>  
										    </font>
										</td>
										<td class="td_text_center">
										   <font size="2">
										    <c:if test="${results.pickType == 'GROUP'}">			    
											     <c:if test="${results.canConfirm == true}">
													<a href="javascript:openConfirm('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
														   �׹�ѹ
													</a>
												  </c:if>
										    </c:if> 
										    </font>
										</td>
									</tr>
							
							  </c:forEach>
					</table>		
					<%} %>
					
					<%if("complete".equalsIgnoreCase(pageName)){ %>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th >Issue Req No</th>
									<th >Issue Req Date</th>
								 	<th >Work Step</th>
								 	<th >����ԡ</th>
								 	<th >������ҹ���</th>
								 	<th >������ҹ���</th>
									<th >Qty �����ԡ</th>
									<th >Qty ����ԡ���ԧ</th>
									<th >�����˵�</th>
									<th >���</th>			
							   </tr>
							<c:forEach var="results" items="${pickStockGroupForm.resultsSearch}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
									    <td class="td_text_center" width="8%">${results.issueReqNo}</td>
										<td class="td_text_center" width="8%">
										   ${results.issueReqDate}
										</td>
										 <td class="td_text" width="10%">
										  ${results.workStep}
										</td>
										<td class="td_text_center" width="10%" >
										  ${results.pickUser}
										</td>
										<td class="td_text_center" width="10%">
										  ${results.storeCode}
										</td>
										<td class="td_text" width="15%">
										  ${results.storeName}
										</td>
										<td class="td_text_center" width="8%">
										  ${results.totalQty}
										</td>
										<td class="td_text_center" width="8%">
										  ${results.totalIssueQty}
										</td>
									    <td class="td_text" width="15%">
										  ${results.remark}
										</td>
										<td class="td_text_center" width="10%">
										    <font size="2">
									           <c:if test="${results.canComplete == false}">
												  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
												          ��
												  </a>
											  </c:if>
											  <c:if test="${results.canComplete == true}">
												  <a href="javascript:openComplete('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
												          ���
												  </a>
											  </c:if>
											 </font>
										</td>
									</tr>
							
							  </c:forEach>
					</table>		
					<%} %>
				</c:if>
				
				<!-- ************************Result ***************************************************-->
					
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