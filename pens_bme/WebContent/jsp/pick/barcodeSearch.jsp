<%@page import="com.isecinc.pens.bean.Barcode"%>
<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="barcodeForm" class="com.isecinc.pens.web.pick.BarcodeForm" scope="session" />
<%
if(session.getAttribute("barcodeStatusList") == null){
	List<References> billTypeList = new ArrayList();
	References ref = new References("","");
	billTypeList.add(ref);
	billTypeList.addAll(JobDAO.getBarcodeStatusList());
	session.setAttribute("barcodeStatusList",billTypeList);
}
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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
}
function clearForm(path){
	var form = document.barcodeForm;
	form.action = path + "/jsp/barcodeAction.do?do=clear2";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.barcodeForm;
	
	if( $('#transactionDate').val()=="" && $('#boxNo').val()==""
		&& $('#jobId').val()=="" && $('#status').val()=="" 
		&& $('#remark').val()=="" && $('#storeCode').val()==""){
		alert("กรุณากรอก ข้อมูลค้นหาอย่างน้อย 1 รายการ");
		return false;
	}
	
	form.action = path + "/jsp/barcodeAction.do?do=exportExcel";
	form.submit();
	return true;
}

function search(path){
	var form = document.barcodeForm;
	if( $('#transactionDate').val()=="" && $('#boxNo').val()==""
		&& $('#jobId').val()=="" && $('#status').val()=="" 
		&& $('#remark').val()=="" && $('#storeCode').val()==""){
		alert("กรุณากรอก ข้อมูลค้นหาอย่างน้อย 1 รายการ");
		return false;
	}
	
	form.action = path + "/jsp/barcodeAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.barcodeForm;
	form.action = path + "/jsp/barcodeAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,jobId,boxNo,mode){
	var form = document.barcodeForm;
	form.action = path + "/jsp/barcodeAction.do?do=prepare&jobId="+jobId+"&boxNo="+boxNo+"&mode="+mode;
	form.submit();
	return true;
}

function openJobPopup(path){
    var param = "";
	url = path + "/jsp/searchJobPopupAction.do?do=prepare3&action=new"+param;
	//window.open(encodeURI(url),"",
	//		   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	PopupCenterFullHeight(url,"",500);
}

function setStoreMainValue(code,desc,storeCode,storeName,storeNo,subInv,wareHouse,wareHouseDesc){
	//alert(types+":"+desc);
	document.getElementsByName("job.jobId")[0].value = code;
	document.getElementsByName("job.name")[0].value = desc;
	
	document.getElementsByName("job.storeCode")[0].value = storeCode;
	document.getElementsByName("job.storeName")[0].value = storeName;
	document.getElementsByName("job.storeNo")[0].value = storeNo;
	document.getElementsByName("job.subInv")[0].value = subInv;
	document.getElementsByName("job.wareHouse")[0].value = wareHouse;
	document.getElementsByName("job.wareHouseDesc")[0].value = wareHouseDesc;
}

function getJobNameKeypress(e,code){
	var form = document.barcodeForm;
	if(e != null && e.keyCode == 13){
		if(code.value ==''){
			form.name.value = '';
		}else{
			getJobNameModel(code);
		}
	}
}

//Return String :jobName|StoreCode|StioreName|StoreNo|subInv|wareHouse|wareHouseDesc
function getJobNameModel(code){
	var returnString = "";
	var form = document.barcodeForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoJobWithStoreDetail.jsp",
			data : "code=" + code.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	if(returnString !=''){
		var retArr = returnString.split("|");
		form.storeName.value = retArr[0];

		form.name.value = retArr[0];	
		form.storeCode.value = retArr[1];
		form.storeName.value = retArr[2];
		form.storeNo.value = retArr[3];
		form.subInv.value = retArr[4];
		form.wareHouse.value = retArr[5];
		form.wareHouseDesc.value = retArr[6];

	}else{
		alert("ไม่พบข้อมูล");
		
		form.jobId.value = "";	
		form.name.value = "";
		form.storeCode.value ="";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
		form.wareHouse.value = "";
		form.wareHouseDesc.value = "";
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
				<jsp:param name="function" value="scanBarcode"/>
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
						<html:form action="/jsp/barcodeAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Transaction Date</td>
									<td>					
										 <html:text property="job.transactionDate" styleId="transactionDate" size="20"/>
									</td>
									<td> 
									           เลขที่กล่อง <html:text property="job.boxNo" styleId="boxNo" size="20"  styleClass="\" autoComplete=\"off"/>
									           สถานะ   
										<html:select property="job.status" styleId="status">
											<html:options collection="barcodeStatusList" property="key" labelProperty="name"/>
									    </html:select>
									</td>
									<td>					
										
									</td>
								</tr>
								<tr>
                                    <td>รับคืนจาก </td>
									<td colspan="2">
						                <html:text property="job.jobId" styleId="jobId" size="20" 
						                  onkeypress="getJobNameKeypress(event,this)"  styleClass="\" autoComplete=\"off"
						                  />					    
									     <input type="button" name="x1" value="..." onclick="openJobPopup('${pageContext.request.contextPath}')"/>
									    <html:text property="job.name" styleId="name" readonly="true" styleClass="disableText" size="50"/>
									      บันทึกเข้าคลัง
						               <html:text property="job.wareHouse" styleId="wareHouse" size="10" readonly="true" styleClass="disableText"/>
						               <html:text property="job.wareHouseDesc" styleId="wareHouseDesc" size="30" readonly="true" styleClass="disableText"/>
									</td>
								  
								</tr>	
								<tr>
                                    <td> 
                                                                                                                                            รหัสร้านค้า    
                                    </td>
									<td colspan="2">
									   <html:text property="job.storeCode" styleId="storeCode" size="20" readonly="true" styleClass="disableText"/>
									   -<html:text property="job.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="20"/>
									   Sub Inventory
						               <html:text property="job.subInv" styleId="subInv" size="10" readonly="true" styleClass="disableText"/>
						               Store No
						               <html:text property="job.storeNo" styleId="storeNo" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								
								<tr>
                                    <td> หมายเหตุ</td>
									<td colspan="3">
						               <html:text property="job.remark" styleId="remark" size="80"  styleClass="\" autoComplete=\"off"/>
						              Scan By User
						               <html:text property="job.createUser" styleId="createUser" size="20"  styleClass="\" autoComplete=\"off"/>
						               &nbsp;&nbsp;
						               <html:checkbox property="job.includeCancel">&nbsp; แสดงรายการที่ยกเลิกด้วย</html:checkbox>
									</td>
								</tr>	
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   	<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
										  <input type="button" value=" Export To Excel " class="newPosBtnLong">
										</a>
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:openEdit('${pageContext.request.contextPath}','','','add')">
										  <input type="button" value="   เพิ่มรายการใหม่   " class="newPosBtnLong">
										</a>	
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${barcodeForm.resultsSearch != null}">
                  	<% 
					   int totalPage = barcodeForm.getTotalPage();
					   int totalRecord = barcodeForm.getTotalRecord();
					   int currPage =  barcodeForm.getCurrPage();
					   int startRec = barcodeForm.getStartRec();
					   int endRec = barcodeForm.getEndRec();
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
						
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >No</th>
									<th >เลขที่กล่อง</th>
									<th >Transaction Date</th>
									<th >Job ID</th>
									<th >Job Name</th>
									<th >รหัสร้านค้า</th>
									<th >Sub Inventory</th>
									<th >Store No</th>
									<th >จำนวน</th>
									<th >Job Status</th>
									<th >User Created</th>
									<th >Action</th>						
							   </tr>
								<% 
								int no = startRec-1;
								String tabclass ="";
								List<Barcode> resultList = barcodeForm.getResultsSearch();
								for(int n=0;n<resultList.size();n++){
									no++;
									Barcode mc = (Barcode)resultList.get(n);
									if(n%2==0){ 
									   tabclass="lineO";
									}else{
									   tabclass ="lineE";
									}
								%>
								
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="3%"><%=no %></td>
										<td class="td_text" width="5%"><%=mc.getBoxNo() %></td>
										<td class="td_text" width="5%"><%=mc.getTransactionDate() %></td>
										<td class="td_text" width="5%"><%=mc.getJobId() %></td>
										<td class="td_text" width="13%"><%=mc.getName() %></td>
										<td class="td_text" width="8%"><%=mc.getStoreCode() %></td>
										<td class="td_text" width="5%">
											<%=mc.getSubInv() %>
										</td>
										<td class="td_text" width="5%">
											<%=mc.getStoreNo() %>
										</td>
										<td class="td_text_right" width="5%">
										    <%=mc.getQty() %>
										</td>
										<td class="td_text_center" width="5%">
										   <%=mc.getStatusDesc() %>
										</td>
										<td class="td_text_center" width="5%">
										    <%=mc.getCreateUser() %>
										</td>
										<td class="td_text_center" width="5%">
										 <% if(mc.isCanEdit()==false){ %>
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=mc.getJobId() %>','<%=mc.getBoxNo() %>','view')">
											         <font size="2"><b>ดู </b></font>
											  </a>
										 <%} %>
										  <% if(mc.isCanEdit()==true){ %>
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=mc.getJobId() %>','<%=mc.getBoxNo() %>','edit')">
											        <font size="2"><b> แก้ไข</b></font>
											  </a>
										 <%} %>
										</td>
									</tr>
								<%}//for %>
						   <!-- Summary -->
							  <tr class="">
							       <td class=""></td> 
								   <td class=""></td> 
								   <td class=""></td>
							       <td class=""></td>
							       <td class=""></td>
							       <td class=""></td>
							       <td class=""></td>
								   <td class="hilight_text" align="right">
									  <B> Total </B>
									</td>
									<td class="hilight_text" align="right">
									 <B>  ${barcodeForm.job.totalQtyDisp}</B>
									</td>
									<td class=""></td>
									<td class=""></td>
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