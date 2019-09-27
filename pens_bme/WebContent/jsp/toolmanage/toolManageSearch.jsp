<%@page import="com.isecinc.pens.web.toolmanage.ToolManageBean"%>
<%@page import="com.isecinc.pens.web.autocn.AutoCNBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.List"%>
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
<jsp:useBean id="toolManageForm" class="com.isecinc.pens.web.toolmanage.ToolManageForm" scope="session" /> 
<%
User user = (User) request.getSession().getAttribute("user");
List<ToolManageBean> itemList = (List<ToolManageBean>)request.getSession().getAttribute("itemList");
%>
<html>
<head>
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('docDate'));
}
function clearForm(path){
	var form = document.toolManageForm;
	form.action = path + "/jsp/toolManageAction.do?do=prepareSearch&action=clear";
	form.submit();
	return true;
}
function search(path){
	var form = document.toolManageForm;
	 <%if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageReport")){ %>
	   if($('#docDate').val()==""){
			alert("กรูณาระบุ ณ วันที่เบิกคืน(As Of date)");
			$('#docDate').focus();
			return false;
		}
	    form.action = path + "/jsp/toolManageAction.do?do=searchReport&action=exportToHTML";
	<%}else{%>
	    form.action = path + "/jsp/toolManageAction.do?do=searchHead&action=newsearch";
	 <%}%>
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.toolManageForm;
	 <%if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageReport")){ %>
	   if($('#docDate').val()==""){
			alert("กรูณาระบุ ณ วันที่เบิกคืน(As Of date)");
			$('#docDate').focus();
			return false;
		}
	    form.action = path + "/jsp/toolManageAction.do?do=searchReport&action=exportToExcel";
	<%}else{%>
	     form.action = path + "/jsp/toolManageAction.do?do=exportToExcel";
	 <%}%>
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.toolManageForm;
	form.action = path + "/jsp/toolManageAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,action,docNo,docDate,custGroup,storeCode){
	var form = document.toolManageForm;
	var param  ="&action="+action;
	    param +="&docNo="+docNo;
	    
	form.action = path + "/jsp/toolManageAction.do?do=prepare"+param;
	form.submit();
	return true; 
}
function openAdd(path,action){
	var form = document.toolManageForm;
	var param  ="&action="+action;
	form.action = path + "/jsp/toolManageAction.do?do=prepare"+param;
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
	var form = document.toolManageForm;
	var param = "&pageName="+pageName;
	if("StoreCodeBME" == pageName){
        param +="&groupStore="+form.custGroup.value;
        param +="&multipleCheck="+multipleCheck;
	}
	url = path + "/jsp/popupSearchAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.toolManageForm;
	if("StoreCodeBME" == pageName){
		form.storeCode.value = code;
		form.storeName.value = desc;
	}
} 
function getCustNameKeypress(path,e,storeCode,fieldName){
	var form = document.toolManageForm;
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
	var form = document.toolManageForm;
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
	        <%if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageOut")){ %>
		        <jsp:include page="../program.jsp">
					<jsp:param name="function" value="ToolManageOut"/>
				</jsp:include>
	        <%} else if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageIn")){ %>
		        <jsp:include page="../program.jsp">
					<jsp:param name="function" value="ToolManageIn"/>
				</jsp:include>
	        <%} else if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageReport")){ %>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ToolManageReport"/>
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
						<html:form action="/jsp/toolManageAction">
						<jsp:include page="../error.jsp"/>
						   <div align="center">  
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                    <td align="center" colspan="3">
                                      <%if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageOut")){ %>
                                           <font size ="3" color="red"><b> <bean:message bundle="sysprop" key="ToolManageOut"/></b></font>
                                      <%} else if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageIn")){ %>
                                          <font size ="3"  color="blue"><b> <bean:message bundle="sysprop" key="ToolManageIn"/></b></font>
                                      <%} else if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageReport")){ %>
                                          <font size ="3"><b> <bean:message bundle="sysprop" key="ToolManageReport"/></b></font>
                                      <%} %>
                                    </td>
								</tr>
							<%if( !toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageReport")){ %>
						       <tr>
                                    <td align="right">  วันที่	</td>
									<td>	
									     <html:text property="bean.docDate" styleId="docDate" size="15" readonly="true"/>	
									</td>
									<td>		
									   เลขที่เอกสาร <font color="red"></font>
                                      <html:text property="bean.docNo" styleId="docNo" size="15" />	
                                      &nbsp;&nbsp;
                                                                                              อ้างอิง Authorize Return no
                                        <html:text property="bean.refRtn" styleId="refRtn" size="15" />	                                               
									</td>
								</tr>
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
							<%}else{ %>
							   <tr>
                                    <td align="right">  ณ วันที่เบิกคืน(As of date)<font color="red">*</font></td>
									<td>	
									     <html:text property="bean.docDate" styleId="docDate" size="15" readonly="true"/>	
									</td>
									<td></td>
								</tr>
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
                                    <td align="right">  Item From	</td>
									<td>	
									     <html:select property="bean.itemFrom" styleId="itemFrom">
											<html:options collection="itemList" property="item" labelProperty="itemName"/>
									    </html:select>	
									</td>
									<td>		
									  Item To &nbsp;
									   <html:select property="bean.itemTo" styleId="itemTo">
											<html:options collection="itemList" property="item" labelProperty="itemName"/>
									    </html:select>	
									</td>
								</tr>
								<tr>
                                    <td align="right">  รูปแบบ</td>
									<td>	
									     <html:select property="bean.reportType" styleId="reportType">
											<html:option value="Detail">Detail</html:option>
											<html:option value="Summary">Summary</html:option>
									    </html:select>	
									</td>
									<td>		
									 
									</td>
								</tr>
							<%} %>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
								     	<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
										  <input type="button" value="ExportToExcel" class="newPosBtnLong"> 
										</a>
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<%if( !toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageReport")){ %>
											<a href="javascript:openAdd('${pageContext.request.contextPath}','add')">
											  <input type="button" value="  เพิ่มรายการใหม่    " class="newPosBtnLong"> 
											</a>
										<%} %>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>
		 <%if( toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageReport")){ 
			   if( session.getAttribute("resultReport") != null){  %>
			    <div align="center">
			       <% out.print( ((StringBuffer)session.getAttribute("resultReport")).toString()); %>
			   </div>
			  
	    <%       } 
		 }else{ %>
            <c:if test="${toolManageForm.resultsSearch != null}">
                   <% 
					   int totalPage = toolManageForm.getTotalPage();
					   int totalRecord = toolManageForm.getTotalRecord();
					   int currPage =  toolManageForm.getCurrPage();
					   int startRec = toolManageForm.getStartRec();
					   int endRec = toolManageForm.getEndRec();
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
						            <th >เลขที่เอกสาร</th><!-- 0 -->
						            <th >วันที่</th><!-- 1 -->
									<th >รหัสร้านค้า</th><!-- 3 -->
									<th >ชื่อร้านค้า</th><!-- 3 -->
									<th >อ้างอิง RTN</th><!-- 4 -->
									<th >หมายเหตุ</th><!-- 5 -->
									<th >Action</th><!-- 7 -->
							   </tr>
							<% 
							String tabclass ="lineE";
							List<ToolManageBean> resultList = toolManageForm.getResultsSearch();
							for(int n=0;n<resultList.size();n++){
								ToolManageBean mc = (ToolManageBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								%>
								<tr class="<%=tabclass%>"> 
									<td class="td_text_center" width="6%"><%=mc.getDocNo()%></td><!-- 1 -->
									<td class="td_text_center" width="5%"><%=mc.getDocDate()%></td><!-- 3 -->
								    <td class="td_text" width="6%"><%=mc.getStoreCode()%></td><!-- 4 -->
								    <td class="td_text" width="11%"><%=mc.getStoreName()%></td><!-- 4 -->
								    <td class="td_text_center" width="6%"><%=mc.getRefRtn()%></td><!-- 5 -->
									<td class="td_text_center" width="15%"><%=mc.getRemark()%></td><!-- 6 -->
									<td class="td_text_center" width="6%">
									  <%if(mc.isCanSave()){ %>
                                         <a href="javascript:openEdit('${pageContext.request.contextPath}'
                                         ,'edit'
                                         ,'<%=mc.getDocNo()%>'
                                         ,'<%=mc.getDocDate()%>'
                                         ,'<%=mc.getCustGroup()%>'
                                         ,'<%=mc.getStoreCode()%>')"> 
								             <font size="2"> แก้ไข</font>
								         </a>
								      <%}else{ %>
								         <a href="javascript:openEdit('${pageContext.request.contextPath}'
								          ,'view'
                                          ,'<%=mc.getDocNo()%>'
                                          ,'<%=mc.getDocDate()%>'
                                          ,'<%=mc.getCustGroup()%>'
                                          ,'<%=mc.getStoreCode()%>')"> 
								             <font size="2">ดู</font>  
								          </a>
								      <%} %>
                                    </td>
								</tr>
							<%} %>
							 
					</table>
				</c:if>
			<%} %>
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