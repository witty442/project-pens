<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.bean.MCBean"%>
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
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
<jsp:useBean id="mcForm" class="com.isecinc.pens.web.mc.MCForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");

if(session.getAttribute("staffTypeList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(MCDAO.searchStaffTypeList(new PopupForm(),""));
	
	session.setAttribute("staffTypeList",billTypeList);
}

if(session.getAttribute("areaList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(MCDAO.searchAreaList(new PopupForm(),""));
	
	session.setAttribute("areaList",billTypeList);
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 
	document.getElementsByName('bean.mcArea')[0].value = '${mcForm.bean.mcArea}';
	
	loadRoute();
	<%if( !"".equals(mcForm.getBean().getMcRoute())){ %>
	  document.getElementsByName('bean.mcRoute')[0].value = <%=mcForm.getBean().getMcRoute()%>;
	<% } %>
}
function clearForm(path){
	var form = document.mcForm;
	form.action = path + "/jsp/mcAction.do?do=clearMCStaff";
	form.submit();
	return true;
}

function search(path){
	var form = document.mcForm;
	if( $('#mcArea').val()==""){
		alert("��س��к� ࢵ��鹷��");
		return false;
	}
	/* if( $('#staffType').val()==""){
		alert("��س��к� ������");
		return false;
	} */
	
	
	form.action = path + "/jsp/mcAction.do?do=searchMCStaff&action=newsearch";
	form.submit();
	return true;
}

function newStaff(path){
	 var form = document.mcForm;
	var param ="";
	form.action = path + "/jsp/mcAction.do?do=prepareMCStaffDetail&action=add"+param;
	form.submit();
	return true; 
}

function openEdit(path,staffId){
	 var form = document.mcForm;
	var param ="&staffId="+staffId;
	form.action = path + "/jsp/mcAction.do?do=prepareMCStaffDetail&action=edit"+param;
	form.submit();
	return true; 
}

function openPopupCustomer(path){
	var form = document.mcForm;
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
	var form = document.mcForm;
	//alert(form);
	form.staffId.value = code;
	form.name.value = desc;

	if(staffId==''){
	  alert("��辺������  staffId");
	  form.staffId.value = '';
	  form.name.value = "";
	}
} 

function getStaffNameKeypress(e,custCode){
	var form = document.mcForm;
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
	var form = document.mcForm;
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
		alert("��辺������");
		form.staffId.focus();
		form.staffId.value ="";
		form.name.value = "";
	}
}

function loadRoute(){
	var cboDistrict = document.getElementsByName('bean.mcRoute')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/RouteAjax.jsp",
			data : "mcArea=" + document.getElementsByName('bean.mcArea')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
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
				<jsp:param name="function" value="mcStaff"/>
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
						<html:form action="/jsp/mcAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td> ࢵ��鹷�� <font color="red">*</font></td>
									<td>		
										 <html:select property="bean.mcArea" styleId="mcArea" onchange="loadRoute();">
											<html:options collection="areaList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> ������<font color="red"></font></td>
									<td>		
										 <html:select property="bean.staffType" styleId="staffType">
											<html:options collection="staffTypeList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> Route ��鹷ҧ <font color="red"></font></td>
									<td>		
										 <html:select property="bean.mcRoute" styleId="mcArea"> </html:select>
									</td>
								</tr>
								<tr>
									<td >���˹�ҷ��  Staff
									</td>
									<td align="left"> 
									  <html:text property="bean.staffId" styleId="staffId" size="20" onkeypress="getStaffNameKeypress(event,this)"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}')"/>
									  <html:text property="bean.name" styleId="name" readonly="true" styleClass="disableText" size="50"/>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ����      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:newStaff('${pageContext.request.contextPath}')">
										  <input type="button" value="    ������¡������      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${mcForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearch">
						       <tr>
						            <th >No.</th>
									<th >ࢵ��鹷��</th>
									<th >������</th>
									<th >Route ��鹷ҧ</th>
									<th >Staff ID</th>
									<th >����</th>
									<th >���ʡ��</th>
									<th >������Ͷ��</th>
									<th >���</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<MCBean> resultList = mcForm.getResultsSearch();
							
							for(int n=0;n<resultList.size();n++){
								MCBean mc = (MCBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
								
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="5%"><%=mc.getNo() %></td>
										<td class="td_text" width="15%"><%=mc.getMcArea()+":"+mc.getMcAreaDesc()%></td>
										<td class="td_text" width="5%"><%=mc.getStaffType()%></td>
									    <td class="td_text" width="15%"><%=mc.getMcRoute() %>:<%=mc.getMcRouteDesc()%></td>
									    <td class="td_text" width="5%"><%=mc.getStaffId() %></td>
										<td class="td_text" width="10%"><%=mc.getName() %></td>
										<td class="td_text" width="10%"><%=mc.getSureName()%></td>
										<td class="td_text" width="10%"><%=mc.getMobile()%></td>
										<td class="td_text_center" width="25%">
											 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getStaffId()%>')">
											             ���
											 </a>
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