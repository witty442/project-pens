<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.LockItemOrderBean"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>

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
<jsp:useBean id="lockItemOrderForm" class="com.isecinc.pens.web.lockitem.LockItemOrderForm" scope="session" /> 

<%
User user = (User) request.getSession().getAttribute("user");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	
}
function clearForm(path){
	var form = document.lockItemOrderForm;
	form.action = path + "/jsp/lockItemOrderAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.lockItemOrderForm;
	form.action = path + "/jsp/lockItemOrderAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.lockItemOrderForm;
	form.action = path + "/jsp/lockItemOrderAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function newEmp(path){
	 var form = document.lockItemOrderForm;
	var param ="";
	form.action = path + "/jsp/lockItemOrderAction.do?do=prepare&action=add"+param;
	form.submit();
	return true; 
}

function openEdit(path,groupCode,groupStore,storeCode,lockDate){
	var form = document.lockItemOrderForm;
	var param  ="&groupCode="+groupCode;
	    param +="&groupStore="+groupStore;
	    param +="&storeCode="+storeCode;
	    param +="&lockDate="+lockDate;
	    
	form.action = path + "/jsp/lockItemOrderAction.do?do=prepare&action=edit"+param;
	form.submit();
	return true; 
}

function openAdd(path){
	var form = document.lockItemOrderForm;
	var param ="";
	form.action = path + "/jsp/lockItemOrderAction.do?do=prepare&action=add"+param;
	form.submit();
	return true; 
}

function deleteLockItem(path){
	var form = document.lockItemOrderForm;
	var param ="";
	if(confirm("ยืนยันลบข้อมูล")){
		form.action = path + "/jsp/lockItemOrderAction.do?do=deleteLockItem";
		form.submit();
		return true; 
	}
	return false;
}

function submitSearch(path,e){
	 var key = e.which;
	 if(key == 13)  // the enter key code
	  {
		 search(path);
	  }
	}
function setKeyDelete(index,chkObject){
	if(chkObject.checked){
		document.getElementsByName("keyDelete")[index].value = chkObject.value;
	}else{
		document.getElementsByName("keyDelete")[index].value ="";
	}
}
function openPopup(path,pageName){
	var form = document.lockItemOrderForm;
	var param = "&pageName="+pageName;
	if("StoreCodeBME" == pageName){
        param +="&groupStore="+form.groupStore.value;
	}
	url = path + "/jsp/popupSearchAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.lockItemOrderForm;
	if("StoreCodeBME" == pageName){
		form.storeCode.value = code;
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
				<jsp:param name="function" value="lockItemOrder"/>
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
						<html:form action="/jsp/lockItemOrderAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						
								<tr>
                                    <td align="right"> รหัส Group Code<font color="red"></font></td>
									<td>		
										 <html:text property="bean.groupCode" styleId="groupCode" size="20" onkeypress="submitSearch('${pageContext.request.contextPath}',event)"/>
									</td>
									<td>		
									    กลุ่มร้านค้า	
                                      <html:select property="bean.groupStore" styleId="groupStore">
											<html:options collection="custGroupList" property="pensValue" labelProperty="pensDesc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td align="right"> รหัสร้านค้า<font color="red"></font></td>
									<td>		
										 <html:text property="bean.storeCode" styleId="storeCode" size="20"/>
										 <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','StoreCodeBME')"/>   
									</td>
									<td></td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									  <%--  <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
										  <input type="button" value=" Export To Excel " class="newPosBtnLong"> 
										</a> --%>
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
								         <a href="javascript:openAdd('${pageContext.request.contextPath}')"> 
									            <input type="button" value=" เพิ่มรายการใหม่" class="newPosBtnLong"> 
									      </a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${lockItemOrderForm.resultsSearch != null}">
                        <table id="tblProductDel" align="center" border="0" cellpadding="3" cellspacing="2"  width="100%">
                         <tr><td >
                             <input type="button" name="delete" value="  ลบข้อมูล  " onclick="deleteLockItem('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                           </td></tr>
                        </table>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th ></th>
						            <th >Group Code</th><!-- 0 -->
						            <th >กลุ่มร้านค้า</th><!-- 1 -->
									<th >ชื่อกลุ่มร้านค้า</th><!-- 3 -->
									<th >ร้านค้า</th><!-- 4 -->
									<th >ชื่อร้านค้า</th><!-- 5 -->
									<th >Lock Date</th><!-- 6 -->
									<th >Unlock Date</th><!-- 7 -->
									<th >แก้ไข</th><!-- 7 -->
							   </tr>
							<% 
							String tabclass ="lineE";
							List<LockItemOrderBean> resultList = lockItemOrderForm.getResultsSearch();
							String keyDelete = "";
							for(int n=0;n<resultList.size();n++){
								LockItemOrderBean mc = (LockItemOrderBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								keyDelete =mc.getGroupCode()+","+mc.getGroupStore()+","+mc.getStoreCode()+","+mc.getLockDate();
								%>
								<tr class="<%=tabclass%>"> 
								    <td class="td_text_center" width="2%">
								    <input type="checkbox" name="delChk" value="<%=keyDelete%>" onclick="setKeyDelete(<%=n%>,this)">
								    <input type="hidden" name="keyDelete" id="keyDelete" />
								    </td><!-- 1 -->
									<td class="td_text_center" width="6%"><%=mc.getGroupCode()%></td><!-- 1 -->
									<td class="td_text_center" width="6%"><%=mc.getGroupStore()%></td><!-- 3 -->
								    <td class="td_text_center" width="10%"><%=mc.getGroupStoreName()%></td><!-- 4 -->
								    <td class="td_text_center" width="8%"><%=mc.getStoreCode()%></td><!-- 5 -->
									<td class="td_text_center" width="10%"><%=mc.getStoreName()%></td><!-- 6 -->
									<td class="td_text_center" width="6%"><%=mc.getLockDate()%></td><!-- 7 -->
									<td class="td_text_center" width="6%"><%=mc.getUnlockDate()%></td><!-- 8 -->
									<td class="td_text_center" width="7%">
                                         <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getGroupCode()%>','<%=mc.getGroupStore()%>','<%=mc.getStoreCode()%>','<%=mc.getLockDate()%>')"> 
								             แก้ไข
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