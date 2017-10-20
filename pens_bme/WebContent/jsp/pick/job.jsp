<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
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
<jsp:useBean id="jobForm" class="com.isecinc.pens.web.pick.JobForm" scope="session" />
<%
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/pick_job.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('openDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('closeDate'));
}
function clearForm(path){
	var form = document.jobForm;
	form.action = path + "/jsp/jobAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.jobForm;
	form.action = path + "/jsp/jobAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function cancel(path){
	var form = document.jobForm;
	if(confirm("�׹�ѹ���¡��ԡ��¡�ù��")){
	   form.action = path + "/jsp/jobAction.do?do=cancel";
	   form.submit();
	   return true;
	}
	return false;
}

function save(path){
	var form = document.jobForm;
	var openDate =$('#openDate').val();
	var closeDate =$('#closeDate').val();
	var name =$('#name').val();
	var storeCode =$('#storeName').val();
	var storeNo =$('#storeNo').val();
	var subInv =$('#subInv').val();
	var custGroup =$('#custGroup').val();
	var wareHouse =$('#wareHouse').val();
	
	if(openDate ==""){
		alert("��سҡ�͡�ѹ���  Open Date");
		return false;
	}
	if(wareHouse ==""){
		alert("��سҡ�͡ �����Ť�ѧ�Թ���");
		return false;
	}
	if(custGroup ==""){
		alert("��سҡ�͡ �����š������ҹ���");
		return false;
	}
	if(name ==""){
		alert("��سҡ�͡ ���� job");
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
	
	if(closeDate != ""){
		var chkDate  = checkCloseDate(openDate,closeDate);
		if(chkDate==false){
			alert("�ѹ��� CloseDate ��ͧ�ҡ������ҡѺ Open Date ");
			$('#closeDate').focus();
			return false;
		}
	}
	if(confirm("�ѹ�ѹ��úѹ�֡������")){
		form.action = path + "/jsp/jobAction.do?do=save";
		form.submit();
		return true;
	}
	return false;
}

function saveRefDoc(path){
	var form = document.jobForm;
	if(confirm("�ѹ�ѹ��úѹ�֡������")){
		form.action = path + "/jsp/jobAction.do?do=saveRefDoc";
		form.submit();
		return true;
	}
	return false;
}

function checkCloseDate(DateFrom, DateTo){
	if(DateFrom=='' || DateTo==''){return true;}
	DateFrom = DateFrom.split("/");
	starttime = new Date(DateFrom[2],DateFrom[1]-1,DateFrom[0]);

	DateTo = DateTo.split("/");
	endtime = new Date(DateTo[2],DateTo[1]-1,DateTo[0]);
	if((endtime-starttime) < 0){
		return false;
	}else{
		return true;
	}
}

function openPopupCustomer(path,types,storeType){
	var form = document.jobForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){
	var form = document.jobForm;
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
	var form = document.jobForm;
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
	var form = document.jobForm;
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

function validRtnNoInJob(obj){
	var returnString = "";
	var form = document.rtForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/validRTRtnNoInJobAjax.jsp",
			data : "rtnNo=" + obj.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	if(returnString =='false'){
		alert("RTN NO �ͧ��ҧ ���ա�úѹ�֡�����ҹ�����");
	    obj.value ='';
		obj.focus();
	}	
}

function resetStore(){
	var form = document.jobForm;
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
				<jsp:param name="function" value="job"/>
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
						<html:form action="/jsp/jobAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
							     <tr>
	                                 <td colspan="1" align="center"> <b>��������´ Job </b> </td>		
								</tr>
						       <tr>
                                    <td> Open Date<font color="red">*</font></td>
									<td>					
									    <c:if test="${jobForm.job.canEdit == true}">	
										  <html:text property="job.openDate" styleId="openDate" size="20" />
										 </c:if>
										 <c:if test="${jobForm.job.canEdit == false}">
										     <html:text property="job.openDate" styleId="openDate" size="20" readonly="true" styleClass="disableText"/>
										 </c:if>
										 
									</td>
								</tr>
								<tr>
                                    <td> �ѹ�֡��Ҥ�ѧ <font color="red">*</font></td>
									<td>				
									    <c:choose>
									       <c:when test="${jobForm.job.canEdit == false}">
										       <html:select property="job.wareHouse" styleId="wareHouse" disabled="true">
												    <html:options collection="wareHouseList" property="key" labelProperty="name"/>
										         </html:select>
										    </c:when>
									       <c:otherwise>
									          <html:select property="job.wareHouse" styleId="wareHouse" >
											    <html:options collection="wareHouseList" property="key" labelProperty="name"/>
									         </html:select>
									      </c:otherwise>
									    </c:choose>		
										
									</td>
								</tr>
								<tr>
                                    <td> �������ҹ��� <font color="red">*</font></td>
									<td>			
									   <c:if test="${jobForm.job.canEdit == true}">			
										 <html:select property="job.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									   </c:if>
									   <c:if test="${jobForm.job.canEdit == false}">			
										 <html:select property="job.custGroup" disabled="true" styleClass="disableText">
											<html:options collection="custGroupList" property="code" labelProperty="desc" />
									    </html:select>
									   </c:if>
									</td>
								</tr>
								<tr>
									<td >������ҹ���<font color="red">*</font>
									</td>
									<td align="left"> 
									 <c:if test="${jobForm.job.canEdit == true}">	
									       <html:text property="job.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									       <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  </c:if>
									   <c:if test="${jobForm.job.canEdit == false}">	
									   <html:text property="job.storeCode" styleId="storeCode" size="20" styleClass="disableText" disabled="true"/>-
									   </c:if>
									<html:text property="job.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									</td>
								</tr>
								<tr>
                                    <td> Sub Inventory</td>
									<td >
						               <html:text property="job.subInv" styleId="subInv" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								<tr>
                                    <td> Store No</td>
									<td >
						               <html:text property="job.storeNo" styleId="storeNo" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								<tr>
                                    <td> Job Id</td>
									<td >
						               <html:text property="job.jobId" styleId="jobId" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								<tr>
                                    <td> Job Name</td>
									<td >
									 <c:if test="${jobForm.job.canEdit == true}">
						                 <html:text property="job.name" styleId="name" size="50" /><font color="red">*</font>
						             </c:if>
						            <c:if test="${jobForm.job.canEdit == false}">
						                 <html:text property="job.name" styleId="name" size="50" readonly="true" styleClass="disableText" />
						             </c:if>
									</td>
								</tr>	
								<tr>
                                    <td> Job Status</td>
									<td >
						               <html:text property="job.statusDesc" styleId="statusDesc" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								<tr>
                                    <td> Close Date</td>
									<td>				
									   <c:if test="${jobForm.job.canEdit == true}">
										  <html:text property="job.closeDate" styleId="closeDate" size="20" />
									   </c:if>
									    <c:if test="${jobForm.job.canEdit == false}">
									      <html:text property="job.closeDate" styleId="closeDate" size="20" readonly="true" styleClass="disableText"/>
									    </c:if>
									</td>
								</tr>
								<tr>
                                    <td> �͡�����ҧ�ԧ</td>
									<td>				
										  <html:text property="job.refDoc" styleId="refDoc" size="40" />
									</td>
								</tr>
								<tr>
                                    <td> �ӹǹ��� (��� RTN)</td>
									<td>				
										  <html:text property="job.rtnQty" styleId="rtnQty" size="15" onblur="isNum(this)"/>
									</td>
								</tr>
								<tr>
                                    <td> �ʹ�Թ (��� RTN) </td>
									<td>				
										  <html:text property="job.rtnAmt" styleId="rtnAmt" size="15"  onblur="isNum2Digit(this)"/>
									</td>
								</tr>
								<%if(jobForm.getMode().equals("edit") || jobForm.getMode().equals("view")){ %>
									<tr>
	                                    <td> RTN No </td>
										<td>	
										<%if(jobForm.getMode().equals("edit")){ %>			
											<html:text property="job.rtnNo" styleId="rtnNo" size="15" onblur="validRtnNoInJob(this)"/>
									    <%}else{ %>
									        <html:text property="job.rtnNo" styleId="rtnNo" size="15" readonly="true" styleClass="disableText"/>
									    <%} %>
										</td>
									</tr>
							   <%} %>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									    <c:if test="${jobForm.job.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="    �ѹ�֡      " class="newPosBtnLong"> 
											</a>
										</c:if>
										
										 <c:if test="${jobForm.job.canEdit == false}">
											<a href="javascript:saveRefDoc('${pageContext.request.contextPath}')">
											  <input type="button" value="    �ѹ�֡      " class="newPosBtnLong"> 
											</a>
										</c:if>
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>	
										<c:if test="${jobForm.job.canCancel == true}">
											<a href="javascript:cancel('${pageContext.request.contextPath}')">
											  <input type="button" value="   ¡��ԡ   " class="newPosBtnLong">
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