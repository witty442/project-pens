<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Job"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="jobForm" class="com.isecinc.pens.web.pick.JobForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('openDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('closeDate'));
}
function clearForm(path){
	var form = document.jobForm;
	form.action = path + "/jsp/jobAction.do?do=clear2";
	form.submit();
	return true;
}
function search(path){
	var form = document.jobForm;
	form.action = path + "/jsp/jobAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.jobForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/jobAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,jobId,mode){
	var form = document.jobForm;
	form.action = path + "/jsp/jobAction.do?do=prepare&jobId="+jobId+"&mode="+mode;
	form.submit();
	return true;
}
function openPopupCustomer(path,types,storeType){
	var form = document.jobForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	//window.open(encodeURI(url),"",
			  // "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	PopupCenterFullHeight(url,"",700);
}
function setStoreMainValue(code,desc,storeNo,subInv,types){
	var form = document.jobForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
	form.storeNo.value = storeNo;
	form.subInv.value = subInv;
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.jobForm;
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
	var form = document.jobForm;
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
				form.storeNo.value = retArr[1];
				form.subInv.value = retArr[2];
			}else{
				alert("ไม่พบข้อมูล");
				form.storeCode.focus();
				form.storeCode.value ="";
				form.storeName.value = "";
				form.storeNo.value = "";
				form.subInv.value = "";
			}
		}
}

function resetStore(){
	var form = document.jobForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
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
				<jsp:param name="function" value="job"/>
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
						<html:form action="/jsp/jobAction">
						<jsp:include page="../error.jsp"/>
						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" width="40%">
						       <tr>
	                               <td colspan="4" align="center" class="criteria_h_style"> <b><u>ค้นหา Job </u></b> </td>		
							   </tr>
						       <tr>
                                    <td align="right" nowrap width="5%" class="criteria_style"> Open Date</td>
									<td  width="5%">						
										  <html:text property="job.openDate" styleId="openDate" size="15" styleClass="\" autoComplete=\"off"/>
									</td >
									<td align="right"  width="5%" nowrap  class="criteria_style"> Job On Year</td>
									<td  width="25%">						
										  <html:select property="job.year" styleId="year" >
											<html:options collection="yearList" property="key" labelProperty="name"/>
									    </html:select> 
									</td>
								</tr>
							 	<tr>
                                    <td align="right" nowrap width="5%"  class="criteria_style"> บันทึกเข้าคลัง</td>
									<td width="5%">						
										 <html:select property="job.wareHouse" styleId="wareHouse" >
											<html:options collection="wareHouseList" property="key" labelProperty="name"/>
									    </html:select>
									</td>
									<td align="right"  width="5%" nowrap  class="criteria_style"> กลุ่มร้านค้า</td>
									<td width="25%" nowrap> 
									    <html:select property="job.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td align="right" nowrap width="5%"  class="criteria_style">รหัสร้านค้า</td>
									<td width="35%" nowrap colspan="3"  class="criteria_style">						
										 <html:text property="job.storeCode" styleId="storeCode" size="15" onkeypress="getCustNameKeypress(event,this,'storeCode')"
										  styleClass="\" autoComplete=\"off" />-
									    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									<html:text property="job.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									    &nbsp;&nbsp;&nbsp;&nbsp; Sub Inv&nbsp;
									     <html:text property="job.subInv" styleId="subInv" size="10" readonly="true" styleClass="disableText"/>
						                &nbsp;&nbsp;&nbsp;&nbsp;Store No&nbsp;
						                 <html:text property="job.storeNo" styleId="storeNo" size="10" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td align="right" nowrap width="5%"  class="criteria_style"> Job Id</td>
									<td width="35%" nowrap  colspan="3"  class="criteria_style">
						               <html:text property="job.jobId" styleId="jobId" size="10" styleClass="\" autoComplete=\"off" />
						                Job Name
						                 <html:text property="job.name" styleId="name" size="40"  styleClass="\" autoComplete=\"off"/>
									    &nbsp;&nbsp;&nbsp;&nbsp;Job Status &nbsp;
						               <html:select property="job.status">
											<html:options collection="jobStatusList" property="key" labelProperty="name"/>
									    </html:select>
									    &nbsp;&nbsp;Close Date
									     <html:text property="job.closeDate" styleId="closeDate" size="15" />
									</td>
								</tr>	
							
								<tr>
                                    <td nowrap width="5%"  class="criteria_style"> เอกสารอ้างอิง</td>
									 <td  nowrap  width="35%" colspan="3"  class="criteria_style"> 
									 <html:text property="job.refDoc" styleId="refDoc" size="40" styleClass="\" autoComplete=\"off"/>
									 RTN No
									  <html:text property="job.rtnNo" styleId="rtnNo" size="15" styleClass="\" autoComplete=\"off"/>
									  &nbsp;&nbsp;&nbsp;
									  <html:checkbox property="job.dispAutoCN">&nbsp;แสดงรายที่ Auto CN/Sub Transfer แล้ว</html:checkbox>
									</td>
								</tr> 
								 
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:openEdit('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   เพิ่มรายการใหม่   " class="newPosBtnLong">
										</a>	
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${jobForm.resultsSearch != null}">
                  	 <% 
					   int totalPage = jobForm.getTotalPage();
					   int totalRecord = jobForm.getTotalRecord();
					   int currPage =  jobForm.getCurrPage();
					   int startRec = jobForm.getStartRec();
					   int endRec = jobForm.getEndRec();
					   int no = startRec;
					%>
					  <%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
					
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >No</th>
									<th >Open Date</th>
									<th >ข้อมูลคลัง</th>
									<th >กลุ่มลูกค้า</th>
									<th >Job ID</th>
									<th >Job Name</th>
									<th> เอกสารอ้างอิง </th>
									<th> จำนวนตัว (ตาม RTN)</th>
									<th> ยอดเงิน (ตาม RTN)</th>
									<th >Job Status</th>
									<th >Job Close Date</th>
									<th >Auto CN/SubTransfer</th>
									<th >แก้ไข</th>						
							   </tr>
							<% 
							String tabclass ="";
							List<Job> resultList = jobForm.getResultsSearch();
							for(int n=0;n<resultList.size();n++){
								Job item = (Job)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}else{
									tabclass ="lineE";
								}
								%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="3%"><%=no %></td>
										<td class="td_text_center" width="8%">
										   <%=item.getOpenDate() %>
										</td>
										<td class="td_text_center" width="5%"><%=item.getWareHouse() %></td>
										<td class="td_text" width="7%"><%=item.getCustGroupDesc() %></td>
										<td class="td_text" width="3%"> <%=item.getJobId() %></td>
										<td class="td_text" width="17%">
											 <%=item.getName() %>
										</td>
										<td class="td_text" width="8%">
											 <%=item.getRefDoc() %>
										</td>
										<td class="td_text" width="4%">
											 <%=item.getRtnQty() %>
										</td>
										<td class="td_text" width="4%">
											 <%=item.getRtnAmt() %>
										</td>
										<td class="td_text"  width="5%">
										     <%=item.getStatusDesc() %>
										</td>
										<td class="td_text_center" width="5%">
										   <%=item.getCloseDate() %>
										</td>
										<td class="td_text_center" width="5%"> 
										  <%if( Utils.isNull(item.getAutoCN()).equalsIgnoreCase("Y")){ %>
										     <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									       <%} %>
										</td>
										<td class="td_text_center" width="5%">
										 <%if(item.isCanEdit()==false){ %>
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=item.getJobId()%>','view')">
											      View
											  </a>
										  <%}%>
										  <%if(item.isCanEdit()==true){ %>
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=item.getJobId()%>','edit')">
											          แก้ไข
											  </a>
										  <%} no++;%>
										</td>
									</tr>
							<%}//for %>
					</table>			
					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
							<tr><td>
														
								</td>
							</tr>
						</table>
					</div>
				</c:if>
					<!-- ************************Result ***************************************************-->
					
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
					<input type="hidden" name="path" id="path" value ="${pageContext.request.contextPath}"/>
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