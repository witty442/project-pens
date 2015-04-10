<%@page import="com.isecinc.pens.web.rt.RTConstant"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.bean.RTBean"%>
<%@page import="com.isecinc.pens.bean.PayBean"%>
<%@page import="java.util.Calendar"%>
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
<jsp:useBean id="rtForm" class="com.isecinc.pens.web.rt.RTForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
String role = user.getRole().getKey();

if(session.getAttribute("custGroupList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(GeneralDAO.searchCustGroup( new PopupForm()));
	
	session.setAttribute("custGroupList",billTypeList);
}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
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
	 new Epoch('epoch_popup', 'th', document.getElementById('dateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('dateTo'));
}
function clearForm(path){
	var form = document.rtForm;
	form.action = path + "/jsp/rtAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.rtForm;
	if( $('#mcArea').val()==""){
		alert("กรุณาระบุ เขตพื้นที่");
		return false;
	}
	/* if( $('#staffType').val()==""){
		alert("กรุณาระบุ ประเภท");
		return false;
	} */
	
	
	form.action = path + "/jsp/rtAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}

function newDoc(path){
	 var form = document.rtForm;
	var param ="";
	form.action = path + "/jsp/rtAction.do?do=prepare&mode=add"+param;
	form.submit();
	return true; 
}

function openEdit(path,docNo){
	 var form = document.rtForm;
	var param ="&docNo="+docNo;
	form.action = path + "/jsp/rtAction.do?do=prepare&mode=edit"+param;
	form.submit();
	return true; 
}

function openPicEdit(path,docNo){
	 var form = document.rtForm;
	var param ="&docNo="+docNo;
	form.action = path + "/jsp/rtAction.do?do=preparePic&mode=edit"+param;
	form.submit();
	return true; 
}

function openCopy(path,docNo){
	 var form = document.rtForm;
	var param ="&docNo="+docNo;
	form.action = path + "/jsp/rtAction.do?do=prepare&mode=copy"+param;
	form.submit();
	return true; 
}

function printReport(path,docNo){
    window.open(path + "/jsp/popup/printPopup.jsp?report_name=PayInReport&docNo="+docNo, "Print2", "width=200,height=200,location=No,resizable=No");
}

function openPopupCustomer(path,types,storeType){
	var form = document.rtForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){
	var form = document.rtForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.rtForm;
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
	var form = document.rtForm;
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

			}else{
				alert("ไม่พบข้อมูล");
				form.storeCode.focus();
				form.storeCode.value ="";
				form.storeName.value = "";
				
			}
		}
	
}
function resetStore(){
	var form = document.rtForm;
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
	        <%if("PIC".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="picRT"/>
				</jsp:include>
			<%}else  {%>
		      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="rt"/>
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
						<html:form action="/jsp/rtAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td> วันที่<font color="red"></font></td>
									<td>		
										 <html:text property="bean.docDate" styleClass="" styleId="docDate"></html:text>
										 	 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										 <font size="2"><b>Authorize Return No</b></font>
										 <html:text property="bean.docNo" styleClass="normalText" styleId="docNo"></html:text>
									</td>
								</tr>
								<tr>
                                    <td> กลุ่มร้านค้า </td>
									<td>		
										 <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
									<td >รหัสร้านค้า
									</td>
									<td align="left"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									</td>
								</tr>
								<tr>
                                    <td> เล่ม/เลขที่<font color="red"></font></td>
									<td>		
										<html:text property="bean.refDoc" styleId="refDoc" styleClass="normalText" size="30"></html:text>
									</td>
								</tr>
								<tr>
                                    <td> RTN No ของห้าง<font color="red"></font></td>
									<td>		
										<html:text property="bean.rtnNo" styleId="rtnNo" styleClass="normalText" size="30"></html:text>
										
										<html:checkbox property="bean.noPicRcv">แสดงเฉพาะรายการทาง PIC ยังไม่ได้รับคืน</html:checkbox>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:newDoc('${pageContext.request.contextPath}')">
										  <input type="button" value="    เพิ่มรายการใหม่      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${rtForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearch">
						       <tr>
						            <th >Authorize return No</th>
									<th >เล่มที่/เลขที่</th>
									<th >วันที่บันทึก</th>
									<th >กลุ่มร้านค้า</th>
									<th >รหัสร้านค้า</th>
									<th >ชื่อร้านค้า</th>
									<th >RTN NO</th>
									<th >จำนวนหีบใน RTN</th>
									<th >จำนวนชิ้นใน RTN</th>
									<th >วันที่ PIC รับสินค้า</th>
									<th >จำนวนหีบ ที่ PIC รับ</th>
									<th >จำนวนชิ้นที่ Scan จริง</th>
									<th >Status</th>
									<th >Action</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<RTBean> resultList = rtForm.getResultsSearch();
							
							for(int n=0;n<resultList.size();n++){
								RTBean mc = (RTBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="10%"><%=mc.getDocNo() %></td>
										<td class="td_text_center" width="10%"><%=mc.getRefDoc() %></td>
										<td class="td_text_center" width="10%"><%=mc.getDocDate()%></td>
									    <td class="td_text" width="10%"><%=mc.getCustGroup() %>:<%=mc.getCustGroupName()%></td>
									    <td class="td_text" width="10%"><%=mc.getStoreCode() %></td>
									    <td class="td_text" width="15%"><%=mc.getStoreName()%></td>
										<td class="td_text_center" width="10%"><%=mc.getRtnNo()%></td>
										<td class="td_text_center" width="10%"><%=mc.getRtnQtyCTN()%></td>
										<td class="td_text_center" width="10%"><%=mc.getRtnQtyEA()%></td>
										<td class="td_text_center" width="10%"><%=mc.getPicRcvDate()%></td>
										<td class="td_text_center" width="10%"><%=mc.getPicRcvQtyCTN()%></td>
										<td class="td_text_center" width="10%"><%=mc.getPicRcvQtyEA()%></td>
										<td class="td_text_center" width="10%"><%=mc.getStatusDesc()%></td> 
										
										<td class="td_text_center" width="10%">
										<%if ( Utils.isNull(request.getParameter("page")).equalsIgnoreCase("PIC") ){%>
										    <% if(mc.getStatus().equals(RTConstant.STATUS_COMPLETE)){
										     %>
											 <a href="javascript:openPicEdit('${pageContext.request.contextPath}','<%=mc.getDocNo()%>')">
											             แก้ไข
											 </a>
											 <%}else{ %>
											   <a href="javascript:openPicEdit('${pageContext.request.contextPath}','<%=mc.getDocNo()%>')">
											             ดู
											   </a>
									        <% } %>
									    
										<%}else{ 
										    if(mc.getStatus().equals(RTConstant.STATUS_OPEN)){
										     %>
											 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getDocNo()%>')">
											             แก้ไข
											 </a>
											 <%}else{ %>
											   <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getDocNo()%>')">
											             ดู
											   </a>
									    <%    } 
									      }
									     %>
										</td>
										
									</tr>
							<%} %>
							 
					</table>
				</c:if>
				
		<!-- ************************Result ***************************************************-->	
					<!-- hidden field -->
					<input type="hidden" name="page" value="<%=request.getParameter("page") %>"/>
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