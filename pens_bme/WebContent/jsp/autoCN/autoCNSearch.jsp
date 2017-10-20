<%@page import="com.isecinc.pens.web.autocn.AutoCNBean"%>
<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="autoCNForm" class="com.isecinc.pens.web.autocn.AutoCNForm" scope="session" /> 
<%
User user = (User) request.getSession().getAttribute("user");
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

<style type="text/css"></style>
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
	var form = document.autoCNForm;
	form.action = path + "/jsp/autoCNAction.do?do=clear2";
	form.submit();
	return true;
}
function search(path){
	var form = document.autoCNForm;
	form.action = path + "/jsp/autoCNAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.autoCNForm;
	form.action = path + "/jsp/autoCNAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,action,jobId,rtnNo,custGroup,storeCode){
	var form = document.autoCNForm;
	var param  ="&action="+action;
	    param +="&jobId="+jobId;
	    param +="&rtnNo="+rtnNo;
	    param +="&custGroup="+custGroup;
	    param +="&storeCode="+storeCode;
	    
	form.action = path + "/jsp/autoCNAction.do?do=prepare"+param;
	form.submit();
	return true; 
}
function submitSearch(path,e){
 var key = e.which;
 if(key == 13)  // the enter key code
  {
	 search(path);
  }
}
function openPopup(path,pageName,multipleCheck){
	var form = document.autoCNForm;
	var param = "&pageName="+pageName;
	if("StoreCodeBME" == pageName){
        param +="&groupStore="+form.custGroup.value;
        param +="&multipleCheck="+multipleCheck;
	}
	url = path + "/jsp/popupSearchAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.autoCNForm;
	if("StoreCodeBME" == pageName){
		form.storeCode.value = code;
		form.storeName.value = desc;
	}
} 
function getJobNameKeypress(e,code){
	var form = document.autoCNForm;
	if(e != null && e.keyCode == 13){
		if(code.value ==''){
			form.name.value = '';
		}else{
			getJobNameModel(code);
		}
	}
}
//Return String :jobName
function getJobNameModel(code){
	var returnString = "";
	var form = document.autoCNForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoJob.jsp",
			data : "code=" + code.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	if(returnString !=''){
		form.jobName.value = returnString;
	}else{
		alert("ไม่พบข้อมูล");
		form.jobId.value = "";	
		form.jobName.value = "";
	}
}

function getCustNameKeypress(path,e,storeCode,fieldName){
	var form = document.autoCNForm;
	var storeType = "";
	if(e != null && e.keyCode == 13){
		if(storeCode.value ==''){
			if("storeName" == fieldName){
				form.storeName.value = '';
			}	
		}else{
		   getCustName(path,storeCode,fieldName,storeType);
		}
	}
}
function getCustName(path,storeCode,fieldName,storeType){
	var returnString = "";
	var form = document.autoCNForm;
	var getData = $.ajax({
			url: path+"/jsp/ajax/getCustNameAjax.jsp",
			data : "custCode=" + storeCode.value+"&storeType="+storeType,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if("storeName" == fieldName){
		if(returnString != ''){
		   form.storeName.value = returnString;
		}else{
			storeCode.value ='';
			storeCode.focus();
			alert("ไม่พบข้อมูล");
		}
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
				<jsp:param name="function" value="AutoCN"/>
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
						<html:form action="/jsp/autoCNAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td align="right">  กลุ่มร้านค้า	</td>
									<td>	
									    <html:select property="bean.custGroup" styleId="custGroup">
											<html:options collection="custGroupList" property="pensValue" labelProperty="pensDesc"/>
									    </html:select>	
									</td>
									<td>		
									   รหัสร้านค้า<font color="red"></font>
                                       <html:text property="bean.storeCode" styleId="storeCode" size="15"  onkeypress="getCustNameKeypress('${pageContext.request.contextPath}',event,this,'storeName')"/>
								       <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','StoreCodeBME','true')"/>   
									  <html:text property="bean.storeName" styleId="storeName" size="30"  styleClass="disableText" readonly="true"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">Job Id </td>
									<td colspan="2">		
										 <html:text property="bean.jobId" styleId="jobId" size="10"  onkeypress="getJobNameKeypress(event,this)"/>
									
									    <html:text property="bean.jobName" styleId="jobName" size="40" styleClass="disableText"  readonly="true"/>
									 &nbsp;&nbsp;RTN No  &nbsp;&nbsp; &nbsp;&nbsp;
									 <html:text property="bean.rtnNo" styleId="rtnNo" size="20"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">Job Status </td>
									<td>		
										 <html:text property="bean.jobStatus" styleId="jobStatus" size="10" styleClass="disableText"  readonly="true"/>
									</td>
									<td>
									   Cut off Job data  &nbsp;&nbsp;
									    <html:text property="bean.cuttOffDate" styleId="cuttOffDate" size="10" styleClass="disableText"  readonly="true"/>
									 
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
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

            <c:if test="${autoCNForm.resultsSearch != null}">
                   <% 
					   int totalPage = autoCNForm.getTotalPage();
					   int totalRecord = autoCNForm.getTotalRecord();
					   int currPage =  autoCNForm.getCurrPage();
					   int startRec = autoCNForm.getStartRec();
					   int endRec = autoCNForm.getEndRec();
					%>
					   
					<div align="left">
					   <span class="pagebanner">รายการทั้งหมด  <%=totalRecord %> รายการ, แสดงรายการที่  <%=startRec %> ถึง  <%=endRec %>.</span>
					   <span class="pagelinks">
						หน้าที่ 
						 <% 
							 for(int r=0;r<totalPage;r++){
								 if(currPage ==(r+1)){
							 %>
			 				   <strong><%=(r+1) %></strong>
							 <%}else{ %>
							    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  
							       title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
						 <% }} %>				
						</span>
					</div>
					
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th >Job Id</th><!-- 0 -->
						            <th >Job Name</th><!-- 1 -->
									<th >Store Code</th><!-- 3 -->
									<th >Store Name</th><!-- 3 -->
									<th >RTN No</th><!-- 4 -->
									<th >Total Box</th><!-- 5 -->
									<th >Total Qty</th><!-- 6 -->
									<th >AutoCN Status</th><!-- 7 -->
									<th >Action</th><!-- 7 -->
									<th >View CN</th><!-- 7 -->
							   </tr>
							<% 
							String tabclass ="lineE";
							List<AutoCNBean> resultList = autoCNForm.getResultsSearch();
							for(int n=0;n<resultList.size();n++){
								AutoCNBean mc = (AutoCNBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								%>
								<tr class="<%=tabclass%>"> 
									<td class="td_text_center" width="6%"><%=mc.getJobId()%></td><!-- 1 -->
									<td class="td_text" width="8%"><%=mc.getJobName()%></td><!-- 3 -->
								    <td class="td_text" width="6%"><%=mc.getStoreCode()%></td><!-- 4 -->
								    <td class="td_text" width="8%"><%=mc.getStoreName()%></td><!-- 4 -->
								    <td class="td_text_center" width="6%"><%=mc.getRtnNo()%></td><!-- 5 -->
									<td class="td_text_center" width="6%"><%=mc.getTotalBox()%></td><!-- 6 -->
									<td class="td_text_center" width="6%"><%=mc.getTotalQty()%></td><!-- 7 -->
									<td class="td_text_center" width="6%"><%=mc.getStatus()%></td><!-- 8 -->
									<td class="td_text_center" width="6%">
									  <%if(mc.isCanSave()){ %>
                                         <a href="javascript:openEdit('${pageContext.request.contextPath}'
                                         ,'edit'
                                         ,'<%=mc.getJobId()%>'
                                         ,'<%=mc.getRtnNo()%>'
                                         ,'<%=mc.getCustGroup()%>'
                                         ,'<%=mc.getStoreCode()%>')"> 
								               Verify
								         </a>
								      <%}else{ %>
								         <a href="javascript:openEdit('${pageContext.request.contextPath}'
								          ,'view'
                                          ,'<%=mc.getJobId()%>'
                                          ,'<%=mc.getRtnNo()%>'
                                          ,'<%=mc.getCustGroup()%>'
                                          ,'<%=mc.getStoreCode()%>')"> 
								               View
								          </a>
								      <%} %>
                                    </td>
                                    <td class="td_text_center" width="6%">
									  <%if( !mc.isCanSave()){ %>
                                         <a href="javascript:openEdit('${pageContext.request.contextPath}'
                                         ,'viewCN'
                                         ,'<%=mc.getJobId()%>'
                                         ,'<%=mc.getRtnNo()%>'
                                         ,'<%=mc.getCustGroup()%>'
                                         ,'<%=mc.getStoreCode()%>')"> 
								               View CN
								         </a>
								        <%} %>
								     </td>
								</tr>
							<%} %>
							 
					</table>
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