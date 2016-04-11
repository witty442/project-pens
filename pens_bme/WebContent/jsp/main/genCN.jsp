<%@page import="com.isecinc.pens.bean.MoveWarehouse"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
<%@page import="com.isecinc.pens.bean.ReqPickStock"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>

<jsp:useBean id="genCNForm" class="com.isecinc.pens.web.cn.GenCNForm" scope="session" />
<%

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />

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
	 new Epoch('epoch_popup', 'th', document.getElementById('openDate'));
	// sumTotal();
}

function clearForm(path){
	var form = document.genCNForm;
	form.action = path + "/jsp/genCNAction.do?do=clear";
	form.submit();
	return true;
}
function back(path){
	var form = document.genCNForm;
	form.action = path + "/jsp/genCNAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}
function searchKeypress(path,e){
	if(e != null && e.keyCode == 13){
		search(path);
	}
}
function search(path){
	var form = document.genCNForm;
	var cnNo =$('#cnNo').val();
	
	 if(cnNo ==""){
		alert("กรุณากรอก เลขที่ CN");
		$('#cnNo').focus();
		return false;
	} 
	
	form.action = path + "/jsp/genCNAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function save(path){
	var form = document.genCNForm;
	var openDate =$('#jobName').val();

	if(openDate ==""){
		alert("กรุณากรอก Job Name");
		$('#jobName').focus();
		return false;
	}
	
	form.action = path + "/jsp/genCNAction.do?do=save";
	form.submit();
	return true;
}

function isNum(obj){
  if(obj.value != ""){
	var newNum = parseInt(obj.value);
	if(isNaN(newNum)){
		alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
		obj.value = "";
		obj.focus();
		return false;
	}else{return true;}
   }
  return true;
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
				<jsp:param name="function" value="genCN"/>
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
						<html:form action="/jsp/genCNAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                  <td colspan="3" align="center"><font size="3"><b></b></font></td>
							   </tr>
							   <c:if test="${genCNForm.results == null}">
							    <tr>
                                    <td align="right"> อ้างอิงเลขที่ CN <font color="red">*</font>
                                       	<html:text property="bean.cnNo" styleId="cnNo" size="20" onkeypress="searchKeypress('${pageContext.request.contextPath}',event)" />
                                     </td>
									<td align="left">
									     <a href="javascript:search('${pageContext.request.contextPath}')">
										     <input type="button" value=" ค้นหา   " class="newPosBtnLong"> 
										 </a>
								   </td>
								</tr>  
								</c:if>
								
							<c:if test="${genCNForm.results != null}">
								<tr>
                                    <td align="right"> อ้างอิงเลขที่ CN <font color="red">*</font>
                                       	<html:text property="bean.cnNo" styleId="cnNo" size="20"  readonly ="true" styleClass="disableText"/>
                                     </td>
									<td align="left">
										 รหัสร้านค้า
										 	<html:text property="bean.storeCode" styleId="storeCode" size="20"  readonly ="true" styleClass="disableText"/> 
										  บันทึกเข้าคลัง
										 	<html:text property="bean.warehouse" styleId="warehouse" size="3"  readonly ="true" styleClass="disableText"/> 
								   </td>
								</tr>     		
								<tr>
									<td align="left">Job Id &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									&nbsp;&nbsp;
									<html:text property="bean.jobId" styleId="jobId" size="10"  readonly ="true" styleClass="disableText"/> 
									</td>
									<td align="left" >Job Name
									 <font color="red">*</font>
									 <html:text property="bean.jobName" styleId="jobName" size="50" /> 
									</td>
								</tr>    
								<tr>
                                    <td align="left" colspan="1">BoxNo&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    &nbsp;&nbsp;
										<html:text property="bean.boxNo" styleId="boxNo" size="20" readonly ="true" styleClass="disableText"/>
									</td>
									<td align="left" >
									</td>
								</tr>	
							</c:if>
							
						   </table>
					  </div>
					  
						  <!-- BUTTON ACTION-->
						<div align="center">
							<table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
														
									</td>
								</tr>
							</table>
						</div>
					  
				       <!-- Table Data -->
				<c:if test="${genCNForm.results != null}">
		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch" width="60%">
						    <tr>
								<th >LineNo</th>
								<th >Pens Item</th>
								<th >Group Code</th>
								<th >Return Qty</th>	
							</tr>
							<c:forEach var="results" items="${genCNForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text_center" width="10%">
										 <input type="text" name="lineId" value="${results.lineId}" readonly class="disableText" size="5"/>
										</td>
										<td class="td_text_center"  width="15%">
											<input type="text" name="pensItem" value ="${results.pensItem}" size="8" readonly class="disableText"/>
										</td>
										<td class="td_text_center"  width="15%">
											<input type="text" name="groupCode" value ="${results.groupCode}" size="8" readonly class="disableText"/>
										</td>
										<td class="td_text_center" width="5%">
										   <input type="text" name="qty" value ="${results.qty}" size="20" readonly class="disableNumber"/>
										    
										</td>
										
									</tr>
							  </c:forEach>
					</table>
					     <br/>
						 <div align="right">
							<b> รวมจำนวนชิ้น </b> :<html:text property="bean.totalQty" styleId="totalQty" size="20"  styleClass="disableNumber" />  ชิ้น
						</div>
						 <div align="left">
						</div> 
						
					<div align="center">
						   <!-- Table Data -->
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="center">
									   
										 <c:if test="${genCNForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="    Generate      " class="newPosBtnLong"> 
											 </a>
										 </c:if>	
									     
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
											  <input type="button" value="    Clear      " class="newPosBtnLong"> 
									    </a>
											
									</td>
								</tr>
							</table>
				</div>
				
				</c:if>
		
					<!-- ************************Result ***************************************************-->
					
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