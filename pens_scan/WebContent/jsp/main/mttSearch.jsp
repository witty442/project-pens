<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.HashMap"%> 
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>

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
<jsp:useBean id="mttForm" class="com.isecinc.pens.web.mtt.MTTForm" scope="session" />

<% 
//if(session.getAttribute("custGroupList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("","");  
	billTypeList.add(ref);
	billTypeList.addAll(GeneralDAO.searchCustGroupForScan( new PopupForm()));
	
	session.setAttribute("custGroupList",billTypeList);
//}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>Scan Barcode</title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('docDate'));
}
function clearForm(path){
	var form = document.mttForm;
	form.action = path + "/jsp/mttAction.do?do=clear2";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.mttForm;
	form.action = path + "/jsp/mttAction.do?do=exportReport&action=newsearch";
	form.submit();
	return true;
}

function search(path){
	var form = document.mttForm;
	/*  if( $('#saleDateFrom').val()=="" && $('#saleDateTo').val()==""
		&& $('#custGroup').val()=="" && $('#storeCode').val()==""
	    && $('#groupCode').val()==""){
		alert("��سҡ�͡ �����Ť������ҧ���� 1 ��¡��");
		return false;
	} */
	
	form.action = path + "/jsp/mttAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}

function openEdit(path,docNo,mode,pageType){
	var form = document.mttForm;
	/* var param ="&docNo="+$('#docNo').val();
	param +="&custGroup="+$('#custGroup').val();
	param +="&storeCode="+$('#storeCode').val();
	param +="&barcode="+$('#barcode').val();
	param +="&groupCode="+$('#groupCode').val();
	param +="&materialMaster="+$('#materialMaster').val();
	param +="&pensItem="+$('#pensItem').val(); */
	
	var param ="&docNo="+docNo+"&mode="+mode+"&pageType="+pageType;
	form.action = path + "/jsp/mttAction.do?do=prepare"+param;
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
	var form = document.mttForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){

	var form = document.mttForm;
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
	var form = document.mttForm;
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
	var form = document.mttForm;
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
	var form = document.mttForm;
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
				<jsp:param name="function" value="Scan"/>
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
						<html:form action="/jsp/mttAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> �ѹ������¡��</td>
									<td>					
									   <html:text property="bean.docDate" styleId="docDate" size="20"/>
									   �Ţ����͡���				
									   <html:text property="bean.docNo" styleId="docNo" size="40"/>
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
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="40"/>
									
									  <html:hidden property="bean.subInv" styleId="subInv"/>
									  <html:hidden property="bean.storeNo" styleId="storeNo"/>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ����      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:openEdit('${pageContext.request.contextPath}','','add','')">
										  <input type="button" value="   ������¡������   " class="newPosBtnLong">
										</a>
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${mttForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >No</th>
									<th >Doc No</th>
									<th >�ѹ������¡��</th>
									<th >�������ҹ���</th>
									<th >������ҹ���</th>
									<th> ����ӹǹ���</th>
									<th >Status</th>		
									<th >ʶҹ��觢�����</th>
									<th >Action</th>					
							   </tr>
							<c:forEach var="results" items="${mttForm.resultsSearch}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text" width="5%">${results.no}</td>
										<td class="td_text"  width="15%">${results.docNo}
										   <input type="hidden" name ="docNo" id ="docNo" value="${results.docNo}" class="disableText" size="15">
										</td>
										<td class="td_text" width="10%">${results.docDate}
										   <input type="hidden" name ="saleDate" id ="docDate" value="${results.docDate}" class="disableText" size="10">
										</td>
										
										<td class="td_text" width="15%">${results.custGroup}&nbsp;${results.custGroupName}
										   <input type="hidden" name ="custGroup" id ="custGroup"  value="${results.custGroup}" class="disableText" size="20">
										</td>
										<td class="td_text" width="25%">${results.storeCode}&nbsp;${results.storeName}
										   <input type="hidden" name ="storeCode" id ="storeCode"  value="${results.storeCode}" class="disableText" size="15">
										</td>
										<td class="td_text_center" width="10%">${results.totalQty}</td>
					                 	<td class="td_text_center" width="10%">${results.statusDesc}</td>
										<td class="td_text_center" width="10%">${results.exportDesc}</td>
										
										<td class="td_text_center" width="10%">
											 <c:if test="${results.status == 'O'}">
												 <a href="javascript:openEdit('${pageContext.request.contextPath}','${results.docNo}','edit')">
												         ���
												 </a>
											</c:if>
											<c:if test="${results.status == 'AB'}">
												 <a href="javascript:openEdit('${pageContext.request.contextPath}','${results.docNo}','view')">
												         ��
												 </a>
											</c:if>
											<c:if test="${results.status == 'C'}">
												 <a href="javascript:openEdit('${pageContext.request.contextPath}','${results.docNo}','view')">
												         ��
												 </a>
											</c:if>
										</td>
									</tr>
							  </c:forEach>
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