<%@page import="com.isecinc.pens.bean.SATranBean"%>
<%@page import="com.isecinc.pens.dao.SAEmpDAO"%>
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
<jsp:useBean id="saTranForm" class="com.isecinc.pens.web.sa.SATranForm" scope="session" /> 

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

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('payDate'));
}
function clearForm(path){
	var form = document.saTranForm;
	form.action = path + "/jsp/saTranAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.saTranForm;
	form.action = path + "/jsp/saTranAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.saTranForm;
	form.action = path + "/jsp/saTranAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function newEmp(path){
	 var form = document.saTranForm;
	var param ="";
	form.action = path + "/jsp/saTranAction.do?do=prepare&action=add"+param;
	form.submit();
	return true; 
}

function openEdit(path,empId){
	 var form = document.saTranForm;
	 var payDate = document.getElementById("payDate");
	 if(payDate.value ==""){
		 alert("กรุณากรอกวันที่ ส่งเงิน(+)");
		 payDate.focus();
		 return false;
	 }
	var param ="&empId="+empId+"&payDate="+payDate.value;
	form.action = path + "/jsp/saTranAction.do?do=prepare&action=edit"+param;
	form.submit();
	return true; 
}

function openView(path,empId){
	var form = document.saTranForm;
	var param ="&empId="+empId+"&payDate=";
	form.action = path + "/jsp/saTranAction.do?do=prepare&action=view"+param;
	form.submit();
	return true; 
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
				<jsp:param name="function" value="saTran"/>
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
						<html:form action="/jsp/saTranAction">
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
									     <html:select property="bean.type" styleId="type">
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
										 <html:text property="bean.surname" styleId="surname" size="20"/>
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
								
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${saTranForm.resultsSearch != null}">
                      <table>
                             	<tr>
                                    <td  align="right">วันที่ส่งเงิน (+)<font color="red">*</font></td>
									<td>		
										 <html:text property="bean.payDate" styleId="payDate" size="30" readonly="true"> </html:text>
									</td>
									 <td  align="right"></td>
									<td>		
									</td>
								</tr>
						</table>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th >บันทึกค่าเฝ้าตู้</th><!-- 0 -->
						            <th >Employee ID</th><!-- 1 -->
									<th >Name</th><!-- 3 -->
									<th >Surname</th><!-- 4 -->
									<th >รหัสใน Oracle</th><!-- 5 -->
									<th >Type</th><!-- 6 -->
									<th >Region</th><!-- 7 -->
									<th >Group Store</th><!-- 8 -->
									<th >Branch</th><!-- 9 -->
							   </tr>
							<% 
							String tabclass ="lineE";
							List<SATranBean> resultList = saTranForm.getResultsSearch();
							
							for(int n=0;n<resultList.size();n++){
								SATranBean mc = (SATranBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>"> 
									   <td class="td_text_center" width="10%">
									       <c:if test="${saTranForm.bean.canEdit == true}">
									         <a href="javascript:openView('${pageContext.request.contextPath}','<%=mc.getEmpId()%>')"> 
									            <img  src="${pageContext.request.contextPath}/icons/lookup.gif"/>
									          </a>
									         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getEmpId()%>')">Action</a><!-- 0 -->
											</c:if>
											<c:if test="${saTranForm.bean.canEdit == false}">
											   <a href="javascript:openView('${pageContext.request.contextPath}','<%=mc.getEmpId()%>')"> 
									             <img  src="${pageContext.request.contextPath}/icons/lookup.gif"/>
									           </a>
											</c:if>
										</td>
										<td class="td_text" width="6%"><%=mc.getEmpId()%></td><!-- 1 -->
										<td class="td_text" width="10%"><%=mc.getName()%></td><!-- 3 -->
									    <td class="td_text" width="10%"><%=mc.getSurname()%></td><!-- 4 -->
									    <td class="td_text" width="8%"><%=mc.getOracleRefId() %></td><!-- 5 -->
										<td class="td_text" width="8%"><%=mc.getType()%></td><!-- 6 -->
										<td class="td_text" width="8%"><%=mc.getRegionDesc()%></td><!-- 7 -->
										<td class="td_text" width="4%"><%=mc.getGroupStore()%></td><!-- 8 -->
										<td class="td_text" width="7%"><%=mc.getBranch()%></td><!-- 9 -->
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