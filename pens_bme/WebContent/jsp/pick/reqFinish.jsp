<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
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
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="reqFinishForm" class="com.isecinc.pens.web.pick.ReqFinishForm" scope="session" />
<%
 String mode = reqFinishForm.getMode();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('requestDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('confirmDate'));
	 sumTotal();
}
function clearForm(path){
	var form = document.reqFinishForm;
	form.action = path + "/jsp/reqFinishAction.do?do=clear";
	form.submit();
	return true;
}

function search(path){
	var form = document.reqFinishForm;
   var requestDate =$('#wareHouse').val();
	
	if(requestDate ==""){
		alert("��سҡ�͡ wareHouse");
		return false;
	}
	form.action = path + "/jsp/reqFinishAction.do?do=searchItem";
	form.submit();
	return true;
}

function cancel(path){
	if(confirm("�׹�ѹ���¡��ԡ��¡�ù��")){
		var form = document.reqFinishForm;
		form.action = path + "/jsp/reqFinishAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function back(path){
	var form = document.reqFinishForm;
	form.action = path + "/jsp/reqFinishAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.reqFinishForm;
	var requestDate =$('#requestDate').val();
	
	if(requestDate ==""){
		alert("��سҡ�͡ requestDate");
		return false;
	}
	
	if( !checkOneSelected()){
		alert("��س����͡��¡�����ҧ���� 1 ��¡��");
		return false;
	}
	
	if(confirm("�ѹ�ѹ��úѹ�֡������")){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
	   form.action = path + "/jsp/reqFinishAction.do?do=save";
	   form.submit();
	   return true;
	}
	return false;
}
function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
	
	sumTotal();
}
function checkOneSelected(){
	var chk = document.getElementsByName("linechk");
	var chkOne = false;
	for(var i=0;i<chk.length;i++){
		 if(chk[i].checked){
			 chkOne = true;
			 break;
		 }
	}
	return chkOne;
}

function sumTotal(chkObj){
	var chk = document.getElementsByName("linechk");
	var lineId = document.getElementsByName("lineId");
	var qty = document.getElementsByName("qty");
	
	var totalBoxTemp =0;
	var totalQtyTemp =0;
	for(var i=0;i<chk.length;i++){
		 if(chk[i].checked){
			 totalBoxTemp = totalBoxTemp+1;
			 totalQtyTemp += parseInt(qty[i].value);
			 
			 lineId[i].value = ""+(i+1);
		 } else{
			 lineId[i].value = "";
		 }
	}
	//alert(totalBoxTemp+","+totalQtyTemp);
	
	 $('#totalBox').val(totalBoxTemp);
	 $('#totalQty').val(totalQtyTemp);
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
				<jsp:param name="function" value="reqFinish"/>
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
						<html:form action="/jsp/reqFinishAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Request Date<font color="red">*</font></td>
									<td>					
										<c:choose>
											<c:when test="${reqFinishForm.bean.canEdit == true}">
												 <html:text property="bean.requestDate" styleId="requestDate" size="20"/>
											</c:when>
											<c:otherwise>
												 <html:text property="bean.requestDate" styleId="requestDateX" size="20" styleClass="disableText" readonly="true"/>
											</c:otherwise>
									   </c:choose>
									</td>
									<td> 
									    Request No <html:text property="bean.requestNo" styleId="requestNo" size="20" styleClass="disableText"/>
									</td>
									<td></td>
								</tr>
								  <tr>
                                    <td> ʶҹ�  </td>
									<td>					
										 <html:text property="bean.statusDesc" styleId="status" size="10" styleClass="disableText"/>
									</td>
									<td> 
									     Warehouse<font color="red">*</font>
									      <c:if test="${reqFinishForm.mode == 'add'}">
										      <html:select property="bean.wareHouse" styleId="wareHouse" >
												<html:options collection="wareHouseList2" property="key" labelProperty="name"/>
										    </html:select>
										  </c:if>
										    <c:if test="${reqFinishForm.mode != 'add'}">
										      <html:select property="bean.wareHouse" styleId="wareHouse" disabled="true">
												<html:options collection="wareHouseList2" property="key" labelProperty="name"/>
										    </html:select>
										  </c:if>
									</td>
									<td></td>
								</tr>
								<tr>
                                    <td> �����˵�</td>
									<td colspan="3">
									<c:choose>
											<c:when test="${reqFinishForm.bean.canEdit == true}">
												 <html:text property="bean.remark" styleId="remark" size="80"  styleClass="\" autoComplete=\"off"/>
											</c:when>
											<c:otherwise>
												 <html:text property="bean.remark" styleId="remark" size="80" styleClass="disableText" readonly="true"/>
											</c:otherwise>
									   </c:choose>
						              
									</td>
								</tr>	
								
								<tr>
                                    <td></td>
									<td colspan="3">
									   <c:if test="${reqFinishForm.mode == 'add'}">
										    <a href="javascript:search('${pageContext.request.contextPath}')">
											  <input type="button" value="    ����      " class="newPosBtnLong"> 
											</a>
									  </c:if>
									</td>
								</tr>	
								
						   </table>
						   
				 <!-- Table Data -->
				<c:if test="${reqFinishForm.results != null}">
		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >�Ţ�����ͧ</th>
								<th >Qty</th>
								<th >�Ѻ�׹�ҡ</th>
											
							</tr>
							<c:forEach var="results" items="${reqFinishForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										
										<td class="td_text_center" width="5%">
											<c:choose>
												<c:when test="${results.selected == 'true'}">
													 <input type="checkbox" name="linechk"  onclick="sumTotal()" checked/>		
												</c:when>
												<c:otherwise>
													 <input type="checkbox" name="linechk"  onclick="sumTotal()"/>		
												</c:otherwise>
											</c:choose>
													  
										  <input type="hidden" name="lineId" value="${results.lineId}" />
										</td>
										<td class="td_text_center" width="10%">${results.boxNo}
											<input type="hidden" name="boxNo" value ="${results.boxNo}" size="40" readonly class="disableText"/>
										</td>
										<td class="td_text_right" width="10%">${results.qty}
										   <input type="hidden" name="qty" value ="${results.qty}" size="20" readonly class="disableText"/>
										</td>
										<td class="td_text" width="15%">${results.jobId}-${results.jobName}
										   <input type="hidden" name="jobId" value ="${results.jobId}" size="20" readonly class="disableText"/>
										</td>
									</tr>
							  </c:forEach>
					</table>
					
						 <div align="left"><b>
							 ����ӹǹ���ͧ      &nbsp;&nbsp;:&nbsp;<input type="text" size="10" id ="totalBox" name ="bean.totalBox" class="disableNumber" value="" readonly/> ���ͧ
						   </b>
						</div><br/>
						 <div align="left"><b>
							 ����ӹǹ��� &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;<input type="text" size="10" id ="totalQty" name ="bean.totalQty" class="disableNumber" value="" readonly/> ���
						    </b>
						</div>
						
				</c:if>
						   <!-- Table Data -->
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   	<c:if test="${reqFinishForm.results != null}">
											 <c:if test="${reqFinishForm.bean.canEdit == true}">
												<a href="javascript:save('${pageContext.request.contextPath}')">
												  <input type="button" value="    �ѹ�֡      " class="newPosBtnLong"> 
												 </a>
											 </c:if>	
											<c:if test="${reqFinishForm.bean.canEdit == true}">
											   <c:if test="${reqFinishForm.mode == 'edit'}">
												 <a href="javascript:cancel('${pageContext.request.contextPath}')">
												   <input type="button" value="    ¡��ԡ     " class="newPosBtnLong"> 
												 </a>
											   </c:if>
											 </c:if>	
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
<!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->