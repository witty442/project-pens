<%@page import="com.isecinc.pens.dao.SAEmpDAO"%>
<%@page import="com.isecinc.pens.bean.SAEmpBean"%>
<%@page import="java.util.Calendar"%>
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
<jsp:useBean id="saEmpForm" class="com.isecinc.pens.web.sa.SAEmpForm" scope="session" />

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
	PopupForm ref1 = new PopupForm("",""); 
	billTypeList1.add(ref1);
	billTypeList1.addAll(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","EMPtype"));
	
	session.setAttribute("empTypeList",billTypeList1);
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	sumTotal();
}

function sumTotal(){
	var totalDamageTemp = document.getElementsByName("totalDamageTemp");
	var totalPaymentTemp = document.getElementsByName("totalPaymentTemp");
	var totalDelayPaymentTemp = document.getElementsByName("totalDelayPaymentTemp");
	
	var totalDamage = 0;
	var totalPayment = 0;
	var totalDelayPayment = 0;
	for(var i=0;i<totalDamageTemp.length;i++){
		totalDamage += parseFloat(totalDamageTemp[i].value.replace(/\,/g,''));
		totalPayment += parseFloat(totalPaymentTemp[i].value.replace(/\,/g,''));
		totalDelayPayment += parseFloat(totalDelayPaymentTemp[i].value.replace(/\,/g,''));
	}
	document.getElementById("totalDamage").innerHTML =addCommas(Number(toFixed(totalDamage,2)).toFixed(2));
	document.getElementById("totalPayment").innerHTML =addCommas(Number(toFixed(totalPayment,2)).toFixed(2));
	document.getElementById("totalDelayPayment").innerHTML =addCommas(Number(toFixed(totalDelayPayment,2)).toFixed(2));
}

function clearForm(path){
	var form = document.saEmpForm;
	form.action = path + "/jsp/saEmpAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.saEmpForm;
	form.action = path + "/jsp/saEmpAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.saEmpForm;
	form.action = path + "/jsp/saEmpAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function newEmp(path){
	 var form = document.saEmpForm;
	var param ="";
	form.action = path + "/jsp/saEmpAction.do?do=prepare&action=add"+param;
	form.submit();
	return true; 
}

function openEdit(path,empRefId){
	 var form = document.saEmpForm;
	var param ="&empRefId="+empRefId;
	form.action = path + "/jsp/saEmpAction.do?do=prepare&action=edit"+param;
	form.submit();
	return true; 
}

function openPopupCustomer(path){
	var form = document.saEmpForm;
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
	var form = document.saEmpForm;
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
	var form = document.saEmpForm;
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
	var form = document.saEmpForm;
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
				<jsp:param name="function" value="saEmp"/>
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
						<html:form action="/jsp/saEmpAction">
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
									     <html:select property="bean.empType" styleId="empType">
											<html:options collection="empTypeList" property="code" labelProperty="desc"/>
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
										 <html:text property="bean.surName" styleId="surName" size="20"/>
									</td>
								</tr>
								<tr>
								    <td  align="right">Region<font color="red"></font></td>    
									<td colspan="2">
									     <html:select property="bean.region" styleId="region">
											<html:options collection="empRegionList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
									<td>
									Group Store
									     <html:select property="bean.groupStore" styleId="groupStore">
											<html:options collection="groupStoreList" property="code" labelProperty="desc"/>
									    </html:select>
									    
									    Branch <html:text property="bean.branch" styleId="branch" size="20"/>
									</td>
								</tr>
								<tr>
									<td colspan="4" align="center">
									 <html:checkbox property="bean.dispDamage"></html:checkbox>  แสดงยอดค่าเสียหาย และ ยอดค้างชำระ 
									</td>
									
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
										  <input type="button" value=" Export To Excel " class="newPosBtnLong"> 
										</a>
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										  <c:if test="${saEmpForm.bean.canEdit == true}">
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

            <c:if test="${saEmpForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <c:if test="${saEmpForm.bean.canEdit == true}">
						               <th >แก้ไข</th><!-- 0 -->
						            </c:if>
						            <c:if test="${saEmpForm.bean.canEdit == false}">
						               <th > View  </th><!-- 0 -->
						            </c:if>
						            <th >Employee ID</th><!-- 1 -->
									<!-- <th >Title</th> --><!-- 2 -->
									<th >Name</th><!-- 3 -->
									<th >Surname</th><!-- 4 -->
									<th nowrap>รหัสในOracle</th><!-- 5 -->
									<th >Type</th><!-- 6 -->
									<th >Region</th><!-- 7 -->
									<th >Group Store</th><!-- 8 -->
									<th >Branch</th><!-- 9 -->
									<%if( !"".equals(Utils.isNull(saEmpForm.getBean().getDispDamage()))){ %>
										<th >ค่าความเสียหาย </th><!-- 10 -->
										<th >ยอดชำระแล้ว </th><!-- 10 -->
										<th >ยอดค้างชำระ </th><!-- 10 -->
									<%} %>
									<th >Mobile No </th><!-- 10 -->
									<th >Email</th><!-- 11 -->
									<th >Bank Account</th><!-- 12 -->
									<th >ID Card</th><!-- 13 -->
									<th >Start Working Date</th><!-- 14 -->
									<th >Leave Date</th><!-- 15 -->
									<th >Leave Reason </th><!-- 16 -->
									<th >ค่าเเฝ้าตู้ BME</th><!-- 17 -->
									<th >วันที่เริ่มให้ค่าเเฝ้าตู้ BME</th><!-- 18 -->
									<th >ค่าเเฝ้าตู้ Wacoal</th><!-- 1 9-->
									<th >วันที่เริ่มให้ค่าเเฝ้าตู้ Wacoal</th><!-- 20 -->
									<th >Surety Bond</th><!-- 21 -->
									<th >วันที่เริ่มให้ Surety Bond</th><!-- 22 -->
							   </tr>
							<% 
							String tabclass ="lineE";
							List<SAEmpBean> resultList = saEmpForm.getResultsSearch();
							
							for(int n=0;n<resultList.size();n++){
								SAEmpBean mc = (SAEmpBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>"> 
									   <td class="td_text_center" width="8%">
									      <c:if test="${saEmpForm.bean.canEdit == true}">
											 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getEmpRefId()%>')">แก้ไข</a><!-- 0 -->
										</c:if>
										<c:if test="${saEmpForm.bean.canEdit == false}">
										   <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getEmpRefId()%>')"> VIEW </a><!-- 0 -->
										</c:if>
										</td>
										<td class="td_text" width="6%"><%=mc.getEmpId()%></td><!-- 1 -->
										<%-- <td class="td_text" width="5%"><%=mc.getTitle()%></td> --%><!-- 2 -->
										<td class="td_text" width="12%" nowrap><%=mc.getName()%></td><!-- 3 -->
									    <td class="td_text" width="12%" nowrap><%=mc.getSurName()%></td><!-- 4 -->
									    <td class="td_text" width="12%" nowrap><%=mc.getOracleRefId() %></td><!-- 5 -->
										<td class="td_text" width="8%"><%=mc.getEmpType()%></td><!-- 6 -->
										<td class="td_text" width="8%"><%=mc.getRegionDesc()%></td><!-- 7 -->
										<td class="td_text" width="4%"><%=mc.getGroupStore()%></td><!-- 8 -->
										<td class="td_text" width="7%"><%=mc.getBranch()%></td><!-- 9 -->
										<%if( !"".equals(Utils.isNull(saEmpForm.getBean().getDispDamage()))){ %>
											<td class="td_text_right" width="8%">
											<%=mc.getTotalDamage()%>
											<input type="hidden" id="totalDamageTemp" name="totalDamageTemp" value="<%=mc.getTotalDamage()%>" />
											</td><!-- 10 -->
											<td class="td_text_right" width="8%">
											   <%=mc.getTotalPayment()%>
											   <input type="hidden" id="totalPaymentTemp" name="totalPaymentTemp" value="<%=mc.getTotalPayment()%>" />
											</td><!-- 10 -->
											<td class="td_text_right" width="8%">
											  <%=mc.getTotalDelayPayment()%>
											   <input type="hidden" id="totalDelayPaymentTemp" name="totalDelayPaymentTemp" value="<%=mc.getTotalDelayPayment()%>" />
											</td><!-- 10 -->
										<%} %>
										<td class="td_text" width="8%"><%=mc.getMobile()%></td><!-- 10 -->
										<td class="td_text" width="8%"><%=mc.getEmail()%></td><!-- 11 -->
										<td class="td_text" width="8%"><%=mc.getBankAccount()%></td><!-- 12 -->
										<td class="td_text" width="8%"><%=mc.getIdCard()%></td><!-- 13 -->
										<td class="td_text" width="8%"><%=mc.getStartDate()%></td><!-- 14 -->
										<td class="td_text" width="8%"><%=mc.getLeaveDate()%></td><!-- 15 -->
										<td class="td_text" width="8%"><%=mc.getLeaveReason()%></td><!-- 16 -->
										<td class="td_text" width="8%"><%=mc.getRewardBme()%></td><!-- 17 -->
										<td class="td_text" width="6%"><%=mc.getStartRewardBmeDate()%></td><!-- 18 -->
										<td class="td_text" width="6%"><%=mc.getRewardWacoal()%></td><!-- 19 -->
										<td class="td_text" width="6%"><%=mc.getStartRewardWacoalDate()%></td><!-- 20 -->
										<td class="td_text" width="6%"><%=mc.getSuretyBond()%></td><!-- 21 -->
										<td class="td_text" width="6%"><%=mc.getStartSuretyBondDate()%></td><!-- 22 -->
									</tr>
							<%} %>
							
							<%if( !"".equals(Utils.isNull(saEmpForm.getBean().getDispDamage()))){ %>
								<tr class="<%=tabclass%>"> 
								  <td colspan="9">
								        <b> รวม</b>
								  </td>
								  <td nowrap>  <b><span id="totalDamage"></span></b></td>
								  <td nowrap>  <b><span id="totalPayment"></span></b></td>
								  <td nowrap>  <b><span id="totalDelayPayment"></span></b></td>
								   <td colspan="13">
								       
								  </td>
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