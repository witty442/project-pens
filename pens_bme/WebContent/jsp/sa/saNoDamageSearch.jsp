<%@page import="com.isecinc.pens.bean.SADamageBean"%>
<%@page import="com.isecinc.pens.dao.SAEmpDAO"%>
<%@page import="com.isecinc.pens.bean.SAEmpBean"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
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
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="saDamageForm" class="com.isecinc.pens.web.sa.SADamageForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");

//if(session.getAttribute("empRegionList") == null){
	List<PopupForm> billTypeList2 = new ArrayList();
	PopupForm ref2 = new PopupForm("",""); 
	billTypeList2.add(ref2);
	billTypeList2.addAll(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","Region"));
	
	session.setAttribute("empRegionList",billTypeList2);
//}

//if(session.getAttribute("empTypeList") == null){
	List<PopupForm> billTypeList1 = new ArrayList();
	billTypeList1.add(new PopupForm("",""));
	billTypeList1.add(new PopupForm("BME","BME"));
	billTypeList1.add(new PopupForm("WACOAL","WACOAL"));
	session.setAttribute("typeList",billTypeList1);
//}

//if(session.getAttribute("groupStoreList") == null){
	List<PopupForm> billTypeList3 = new ArrayList();
	PopupForm ref3 = new PopupForm("",""); 
	billTypeList3.add(ref3);
	billTypeList3.addAll(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","Group_store"));
	
	session.setAttribute("groupStoreList",billTypeList3);
//}
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
.day {
  width: 14%;
}
.holiday {
  width: 14%;
  background-color: #F78181;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('tranDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('checkStockDate'));
	 
	 sumTotal();
}
function clearForm(path){
	var form = document.saDamageForm;
	form.action = path + "/jsp/saDamageAction.do?do=clearNoDamageSearch";
	form.submit();
	return true;
}

function sumTotal(){
	var totalDamageTemp = document.getElementsByName("totalDamageTemp");
	var totalDamage = 0;
	for(var i=0;i<totalDamageTemp.length;i++){
		totalDamage += parseFloat(totalDamageTemp[i].value.replace(/\,/g,''));
	}
	document.getElementById("totalDamage").innerHTML =addCommas(Number(toFixed(totalDamage,2)).toFixed(2));
}

function search(path){
	var form = document.saDamageForm;
	form.action = path + "/jsp/saDamageAction.do?do=searchNoDamage&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.saDamageForm;
	form.action = path + "/jsp/saDamageAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function newEmp(path){
	 var form = document.saDamageForm;
	var param ="";
	form.action = path + "/jsp/saDamageAction.do?do=prepareNoDamage&action=add"+param;
	form.submit();
	return true; 
}

function openEdit(path,empId,type,invRefwal){
	 var form = document.saDamageForm;
	var param ="&empId="+empId;
	    param +="&type="+type;
	    param +="&invRefwal="+invRefwal;
	form.action = path + "/jsp/saDamageAction.do?do=prepareNoDamage&action=edit"+param;
	form.submit();
	return true; 
}
function openEditNoDamage(path,empId,type,invRefwal){
	 var form = document.saDamageForm;
	var param ="&empId="+empId;
	    param +="&type="+type;
	    param +="&invRefwal="+invRefwal;
	form.action = path + "/jsp/saDamageAction.do?do=prepareNoDamage&action=edit"+param;
	form.submit();
	return true; 
}

function openPopupCustomer(path){
	var form = document.saDamageForm;
	var mcArea = document.getElementsByName('bean.mcArea')[0].value;
	var mcRoute = document.getElementsByName('bean.mcRoute')[0].value;
	var staffType = document.getElementsByName('bean.staffType')[0].value;
	
    var param = "&mcArea="+mcArea;
        param += "&mcRoute="+mcRoute;
        param += "&staffType="+staffType;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepareSearchMC&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc){
	var form = document.saDamageForm;
	//alert(form);
	form.staffId.value = code;
	form.name.value = desc;

	if(staffId==''){
	  alert("ไม่พบข้อมูล  staffId");
	  form.staffId.value = '';
	  form.name.value = "";
	}
} 

function getStaffNameKeypress(e,custCode){
	var form = document.saDamageForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			form.staffId.value = '';
			form.name.value = "";
		}else{
		  getStaffName(custCode);
		}
	}
}

function getStaffName(custCode){
	var returnString = "";
	var form = document.saDamageForm;
	var mcArea = document.getElementsByName('bean.mcArea')[0].value;
	var mcRoute = document.getElementsByName('bean.mcRoute')[0].value;
	var staffType = document.getElementsByName('bean.staffType')[0].value;
	
    var param  = "mcArea="+mcArea;
        param += "&mcRoute="+mcRoute;
        param += "&custCode=" + custCode.value;
        param += "&staffType="+staffType;
        
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getStaffMCAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	if(returnString !=''){
		var retArr = returnString.split("|");
		form.name.value = retArr[0];
	}else{
		alert("ไม่พบข้อมูล");
		form.staffId.focus();
		form.staffId.value ="";
		form.name.value = "";
	}
}

</script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerMC.jsp"/></td>
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
				<jsp:param name="function" value="saNoDamage"/>
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
						<html:form action="/jsp/saDamageAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td> Employee ID <font color="red"></font></td>
									<td>		
										 <html:text property="bean.empId" styleId="empId" size="20"/>
									</td>
								   <td> รหัสร้านค้าใน Oracle<font color="red"></font></td>
									<td>		
				                        <html:text property="bean.oracleRefId" styleId="oracleRefId" size="20"/>
									    ประเภท
									     <html:select property="bean.type" styleId="empType">
											<html:options collection="typeList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> Name <font color="red"></font></td>
									<td>		
										 <html:text property="bean.name" styleId="name" size="20"/>
									</td>
								   <td> Surname<font color="red"></font></td>
									<td>		
										 <html:text property="bean.surname" styleId="surname" size="20"/>
										 
										 วันที่บันทึก
										  <html:text property="bean.tranDate" styleId="tranDate" size="20" readonly="true"/>
									</td>
								</tr>
								<tr>
								    <td> Invoice No / Ref Wacoal <font color="red"></font></td>
									<td>		
										 <html:text property="bean.invRefwal" styleId="invRefwal" size="20"/>
									</td>
									<td colspan="2">
								              วันที่เข้าตรวจนับ
										  <html:text property="bean.checkStockDate" styleId="checkStockDate" size="20" readonly="true"/>
										  
										Branch <html:text property="bean.branch" styleId="branch" size="20"/>
									</td>
								</tr>
						   </table>  
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   <%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
										  <input type="button" value=" Export To Excel " class="newPosBtnLong"> 
										</a> --%>
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<c:if test="${saDamageForm.bean.canEdit == true}">
											<a href="javascript:newEmp('${pageContext.request.contextPath}')">
											  <input type="button" value="    เพิ่มรายการใหม่      " class="newPosBtnLong"> 
											</a>
										</c:if>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${saDamageForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						           <c:if test="${saDamageForm.bean.canEdit == true}">
						               <th >แก้ไข</th><!-- 0 -->
						            </c:if>
						            <c:if test="${saDamageForm.bean.canEdit == false}">
						               <th > Action  </th><!-- 0 -->
						            </c:if>
						            <th >วันที่บันทึก</th><!-- 1 -->
						            <th >Employee ID</th><!-- 2 -->
						            <th >Type</th><!-- 3 -->
						            <th >Invoice No / Ref Wacoal</th><!-- 4 -->
						            <th >Invoice Date</th><!-- 5 -->
									<th >Name</th><!-- 5 -->
									<th >Surname</th><!-- 6 -->
									<th >รหัสใน Oracle</th><!-- 7 -->
									<th >Group Store</th><!-- 8 -->
									<th >Branch</th><!-- 9 -->
									<th >วันที่เข้าตรวจนับ </th><!-- 10 -->
									<th >ค่าความเสียหาย</th><!-- 11 -->
							   </tr>
							<% 
							String tabclass ="lineE";
							List<SADamageBean> resultList = saDamageForm.getResultsSearch();
							
							for(int n=0;n<resultList.size();n++){
								SADamageBean mc = (SADamageBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>"> 
									   <td class="td_text_center" width="10%">
										  <c:if test="${saDamageForm.bean.canEdit == true}">
							                    <a href="javascript:openEditNoDamage('${pageContext.request.contextPath}','<%=mc.getEmpId()%>','<%=mc.getType()%>','<%=mc.getInvRefwal()%>')">แก้ไข</a><!-- 0 -->
							              </c:if>

							              <c:if test="${saDamageForm.bean.canEdit == false}">
							               <c:choose>
							                 <c:when test="${saDamageForm.bean.oracleRefId == ''}">
							                    <a href="javascript:openEditNoDamage('${pageContext.request.contextPath}','<%=mc.getEmpId()%>','<%=mc.getType()%>','<%=mc.getInvRefwal()%>')"> VIEW </a><!-- 0 -->
							                 </c:when>
							                 <c:otherwise>
							                     <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getEmpId()%>','<%=mc.getType()%>','<%=mc.getInvRefwal()%>')"> VIEW </a><!-- 0 -->
							                 </c:otherwise>
							               </c:choose>
							             
							              </c:if>
											
										</td>
										<td class="td_text_center" width="6%"><%=mc.getTranDate()%></td><!-- 1 -->
										<td class="td_text_center" width="6%"><%=mc.getEmpId()%></td><!-- 1 -->
										<td class="td_text_center" width="6%"><%=mc.getType()%></td><!-- 6 -->
										<td class="td_text_center" width="8%"><%=mc.getInvRefwal()%></td><!-- 1 -->
										<td class="td_text_center" width="5%"><%=mc.getInvoiceDate()%></td>
										<td class="td_text_center" width="10%"><%=mc.getName()%></td><!-- 3 -->
									    <td class="td_text_center" width="10%"><%=mc.getSurname()%></td><!-- 4 -->
									    <td class="td_text_center" width="8%"><%=mc.getOracleRefId() %></td><!-- 5 -->
										<td class="td_text_center" width="8%"><%=mc.getGroupStore()%></td><!-- 6 -->
										
										<td class="td_text_center" width="7%"><%=mc.getBranch()%></td><!-- 9 -->
										<td class="td_text_center" width="6%"><%=mc.getCheckStockDate()%></td><!-- 1 -->
										<td class="td_text_right" width="6%"><%=mc.getTotalDamage()%>
										<input type="hidden" name="totalDamageTemp" id="totalDamageTemp" value="<%=mc.getTotalDamage()%>"/>
										</td><!-- 1 -->
									</tr>
							<%} %>	 
							<tr class="<%=tabclass%>"> 
								<td colspan="12" class="td_text_right" width="6%"><b> ยอดรวมค่าเสียหาย : </b></td><!-- 1 -->
								<td class="td_text_right" width="6%">
								    <b><span id="totalDamage"></span></b>
								</td><!-- 1 -->
							</tr>
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