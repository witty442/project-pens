<%@page import="com.isecinc.pens.web.autosubb2b.AutoSubB2BBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.List"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="autoSubB2BForm" class="com.isecinc.pens.web.autosubb2b.AutoSubB2BForm" scope="session" /> 

<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "autoSubB2BForm");

User user = (User) request.getSession().getAttribute("user");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
}
function clearForm(path){
	var form = document.autoSubB2BForm;
	form.action = path + "/jsp/autoSubB2BAction.do?do=clear2";
	form.submit();
	return true;
}
function search(path){
	var form = document.autoSubB2BForm;
	form.action = path + "/jsp/autoSubB2BAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.autoSubB2BForm;
	form.action = path + "/jsp/autoSubB2BAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.autoSubB2BForm;
	form.action = path + "/jsp/autoSubB2BAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function addNew(path){
	var form = document.autoSubB2BForm;
	var param  ="&action=new";
	form.action = path + "/jsp/autoSubB2BAction.do?do=prepare"+param;
	form.submit();
	return true; 
}
function openEdit(path,action,refNo,refType,fromSubInv,fromCustGroup,fromStoreCode){
	var form = document.autoSubB2BForm;
	var param  ="&action="+action;
	    param +="&refNo="+refNo;
	    param +="&refType="+refType;
	    param +="&fromSubInv="+fromSubInv;
	    param +="&fromCustGroup="+fromCustGroup;
	    param +="&fromStoreCode="+fromStoreCode;
	    
	form.action = path + "/jsp/autoSubB2BAction.do?do=prepare"+param;
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
				<jsp:param name="function" value="AutoSubB2B"/>
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
						<html:form action="/jsp/autoSubB2BAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td align="right"> อ้างถึงการโอน	</td>
									<td>	
									    <html:select property="bean.refType" styleId="refType">
											<html:option value=""></html:option>
											<html:option value="WACOAL">เปิดจากโรงงาน</html:option>
											<html:option value="PIC">เบิกจาก PIC</html:option>
									    </html:select>	
									</td>
									
								</tr>
								<tr>
                                    <td align="right">อ้างถึง Order Lot No/Request No</td>
									<td>		
										<html:text property="bean.refNo" styleId="refNo" size="20" styleClass="\" autoComplete=\"off"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">Status </td>
									<td>		
										 <html:select property="bean.status" styleId="status">
											<html:option value=""></html:option>
											<html:option value="APPROVED">APPROVED</html:option>
											<html:option value="SUCCESS">SUCCESS</html:option>
											<html:option value="ERROR">ERROR</html:option>
									    </html:select>	
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:addNew('${pageContext.request.contextPath}')">
										  <input type="button" value="เพิ่มรายการใหม่ " class="newPosBtnLong"> 
										</a>
										<%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
										  <input type="button" value="    Export      " class="newPosBtnLong"> 
										</a> --%>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${autoSubB2BForm.resultsSearch != null}">
                     <jsp:include page="../pageing_new.jsp">
				       <jsp:param name="totalPage" value="<%=autoSubB2BForm.getTotalPage() %>"/>
				       <jsp:param name="totalRecord" value="<%=autoSubB2BForm.getTotalRecord() %>"/>
				       <jsp:param name="currPage" value="<%=autoSubB2BForm.getCurrPage() %>"/>
				       <jsp:param name="startRec" value="<%=autoSubB2BForm.getStartRec() %>"/>
				       <jsp:param name="endRec" value="<%=autoSubB2BForm.getEndRec() %>"/>
			         </jsp:include>
					
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
					       <tr>
					            <th>ประเภท</th>
					            <th>อ้างถึง</th>
								<th>รหัสร้านค้าเดิม</th>
								<th>ชื่อร้านค้าเดิม</th>
								<th>Sub Inv เดิม</th>
								<th>รหัสร้านค้าใหม่</th>
								<th>ชื่อร้านค้าใหม่</th>
								<th>Sub Inv ใหม่</th>
								<th>Status</th>
								<th>Interface</th>
								<th>Action</th>
						   </tr>
							<% 
							String tabclass ="lineE";
							List<AutoSubB2BBean> resultList = autoSubB2BForm.getResultsSearch();
							for(int n=0;n<resultList.size();n++){
								AutoSubB2BBean mc = (AutoSubB2BBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								%>
								<tr class="<%=tabclass%>"> 
									<td class="td_text_center" width="6%"><%=mc.getRefTypeDesc()%></td>
									<td class="td_text_center" width="10%"><%=mc.getRefNo()%></td>
								    <td class="td_text" width="6%"><%=mc.getFromStoreCode()%></td>
								    <td class="td_text" width="10%"><%=mc.getFromStoreName()%></td>
								    <td class="td_text_center" width="5%"><%=mc.getFromSubInv()%></td>
								    
								    <td class="td_text" width="6%"><%=mc.getToStoreCode()%></td>
								    <td class="td_text" width="10%"><%=mc.getToStoreName()%></td>
								    <td class="td_text_center" width="5%"><%=mc.getToSubInv()%></td>
								    <td class="td_text_center" width="6%"><%=mc.getStatus()%></td>
									<td class="td_text_center" width="6%"><%=mc.getIntMessage()%></td>
									 <td class="td_text_center" width="6%">
									  <%if(mc.isCanSave()){ %>
                                         <a href="javascript:openEdit('${pageContext.request.contextPath}'
                                         ,'edit'
                                         ,'<%=mc.getRefNo()%>'
                                         ,'<%=mc.getRefType()%>'
                                         ,'<%=mc.getFromSubInv()%>'
                                         ,'<%=mc.getFromCustGroup()%>'
                                         ,'<%=mc.getFromStoreCode()%>')"> 
								               Edit
								         </a>
								      <%}else{ %>
								         <a href="javascript:openEdit('${pageContext.request.contextPath}'
								          ,'view'
                                          ,'<%=mc.getRefNo()%>'
                                          ,'<%=mc.getRefType()%>'
                                          ,'<%=mc.getFromSubInv()%>'
                                          ,'<%=mc.getFromCustGroup()%>'
                                          ,'<%=mc.getFromStoreCode()%>')"> 
								               View
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