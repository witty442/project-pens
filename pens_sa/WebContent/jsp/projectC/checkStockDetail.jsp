<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="com.isecinc.pens.web.projectc.ProjectCImageBean"%>
<%@page import="com.isecinc.pens.web.projectc.ProjectCBean"%>
<%@page import="com.pens.util.PageVisit"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<jsp:useBean id="projectCForm" class="com.isecinc.pens.web.projectc.ProjectCForm" scope="session" />
<%
String maxFileSizeUpload = "20000";//Get from config
System.out.println("projectCForm:"+projectCForm);
ProjectCBean bean = projectCForm.getBean();

if(session.getAttribute("projectCBeanServlet") != null){
	System.out.println("Get from projectCBeanServlet");
	bean = (ProjectCBean)session.getAttribute("projectCBeanServlet");
	System.out.println("bean.items:"+bean.getItems().size());
}
System.out.println("bean:"+bean);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/projectC.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript">

function loadMe(){
}
function backForm(path){
	var form = document.projectCForm;
	var param ="&action=back";
    param +="&checkDate="+form.checkDate.value;
    param +="&branchId="+form.branchId.value;
    param +="&storeCode="+form.storeCode.value;

	form.action = path + "/jsp/projectCAction.do?do=prepareCheckStock"+param;
	form.submit();
	return true;
}
function save(path){
	var form = document.projectCForm;
	if( form.checkUser.value ==""&& form.checkUser.value==""){
		alert("กรุณาระบุ ชื้อผู้บันทึก");
		form.checkUser.focus();
		return false;
	}
	
	form.action = path + "/jsp/projectCServlet";
	form.submit();
	return true;
}
/** Validate Display image after upload **/
function validateFile(input,imageId) {
  var file;
  var fileSize =0;
  //validate file type
   var extension = '';
    if(input.value != '' && input.value.indexOf('.') > 0){
       extension = input.value.substring(input.value.lastIndexOf('.') + 1).toLowerCase();
    }
    if(input.value != '' && (extension == 'png' 
    || extension == 'jpeg') || extension == 'jpg' || extension == 'bmp'  ){
    }else{
       alert('กรุณาเลือกไฟล์รูปภาพเท่านั้น นามสกุล  png ,jpeg ,jpg ,bmp เท่านั้น');
       input.value = "";
       return false;
    }
  
  //validate fileSize
  if (input.files && input.files[0]) {
	  var bytes = input.files[0].size;
	  if (bytes == 0) return '0 Byte';
	  //convert bytes to kb
	  fileSize =  Math.round(bytes / Math.pow(1024, 1), 2) ;
     // alert(fileSize);
      if(fileSize > <%=maxFileSizeUpload%>){
    	  alert("ไม่สามารถอัพโหลดรูปภาพขนาดเกิน  <%=maxFileSizeUpload%> KB ได้ กรุณาเลือกรูปใหม่");
    	  input.value = "";
    	  return false;
      }else{
    	 //display image
    	  showImage(input,imageId);
      }
  }
}
function bytesToSize(bytes) {
	var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
   if (bytes == 0) return '0 Byte';
   var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
   return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
};

function showImage(input,imageId) {
   if (input.files && input.files[0]) {
       var reader = new FileReader();
       var image = $("#image_pic_"+imageId);
       //alert("#image_pic" + picNo+"_"+rowId +":"+imageBlah);
       
       reader.onload = function (e) {
       	image
               .attr('src', e.target.result)
               .width(50)
               .height(30);
       };
       reader.readAsDataURL(input.files[0]);
       
       //set statusImage to =''
       var imageStatus = $("#status_pic_"+imageId);
       imageStatus.val("");
   }
}
function delImage(fileNameDB,imageId) {
	var imagePicStatus = $("#status_pic_"+imageId);
	var imagePic = $("#image_pic_" +imageId);
	
   //Case insert file new clear onscreen
    imagePic.attr('src', '');
   
    //clear imageShow and set status=CANCEL
    //Case Found DB mark status =DEL
    if(fileNameDB != "0"){
    	imagePicStatus.val("DEL");
    	//alert("delImage status_pic" + picNo+"_"+rowId+":"+imagePicStatus.val());
    }
}
/******** get current Location ***********************/
function initMap() {
	 //var timeoutCount = 30000;
   	 //navigator.geolocation.getCurrentPosition(foundLocation,noLocation,{timeout:timeoutCount});
 }
   
 function foundLocation(position) {
  	 // document.getElementById("chkLatitude").value = position.coords.latitude;
  	 // document.getElementById("chkLongitude").value = position.coords.longitude;
  }
    
 function noLocation() {
   	//document.getElementById("location").value="not found";
  	//$('#spnWait').hide();
 }
  
</script>

<%--  <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey()%>&callback=initMap&region=TH&language=th">
 </script> --%>
 
</head>		
<!-- <body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;"> -->
<body onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_login2.png','${pageContext.request.contextPath}/images2/button_forgotpwd2.png')" topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0">

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
				<jsp:param name="function" value="ProjectC"/>
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
					<form name="projectCForm" action="/jsp/projectCServlet" enctype="multipart/form-data" method = "POST">
						<div align="center">
					<!-- 	chkLatitude:<input type="text" name="chkLatitude" id="chkLatitude"/><br/>
						chkLongitude:<input type="text" name="chkLongitude" id="chkLongitude"/> -->
						
						<!-- Message  -->
						 <% String modeMsg = "";
                          System.out.println("Message:"+request.getAttribute("Message"));
                          String message  = Utils.isNull(session.getAttribute("Message"));
                          String errorMessage  = Utils.isNull(session.getAttribute("ERROR_Message"));
						        /* if(prodShowBean.getMode().equalsIgnoreCase("add")){
						        	modeMsg = "เพิ่ม";
						        }else if(prodShowBean.getMode().equalsIgnoreCase("edit")){
						        	modeMsg ="แก้ไข";
						        }else{
						        	modeMsg ="แสดง";
						        } */
						    %>
						    <div align="center">
						       <font size="2" color="blue"><b><%=message %></b> </font>
						       <font size="2" color="red"><b> <%=errorMessage %></b> </font>
						    </div>
						  <!--  Criteria -->
						 <table align="center" border="0" cellpadding="3" cellspacing="0" >
						  <tr>
			                 <td> วันที่ตรวจนับ<font color="red">*</font></td>
							 <td>			
							     <%-- <html:text property="bean.checkDate" styleId="checkDate" size="15"/>  --%>
							     <input type="text" name ="checkDate" size="15" class="disableText" readonly value="<%=Utils.isNull(bean.getCheckDate()) %>"/>
							    &nbsp;&nbsp;&nbsp;
							     ชื่อ-นามสกุล ผู้บันทึก<font color="red">*</font>
							    <%--  <html:text property="bean.checkUser" styleId="checkUser" size="15"/> --%>
							     <input type="text" name ="checkUser" size="30" class="" value="<%=Utils.isNull(bean.getCheckUser()) %>" autoComplete='off'/>
							     <input type="hidden" name ="idHead" value="<%=Utils.isNull(bean.getId()) %>"/>
							 </td>
						   </tr>
							<tr>
			                 <td> ร้านค้า<font color="red">*</font></td>
							 <td>			
							     <%-- <html:text property="bean.storeCode" styleId="storeCode" size="15"/>  
							      <html:text property="bean.storeName" styleId="storeName" size="35"/>   --%>  
							      <input type="text" name ="storeCode" size="15" class="disableText" readonly value="<%=Utils.isNull(bean.getStoreCode()) %>"/>
							      <input type="text" name ="storeName" size="35" class="disableText" readonly value="<%=Utils.isNull(bean.getStoreName()) %>"/>
							 </td>
						   </tr>
						   <tr>
			                 <td> สาขา<font color="red">*</font></td>
							 <td>			
							   <%--   <html:text property="bean.branchId" styleId="branchId" size="15"/>  
							      <html:text property="bean.branchName" styleId="branchName" size="35"/>    --%> 
							      <input type="text" name ="branchId" size="15" class="disableText" readonly value="<%=Utils.isNull(bean.getBranchId()) %>"/>
							      <input type="text" name ="branchName" size="15" class="disableText" readonly value="<%=Utils.isNull(bean.getBranchName()) %>"/>
							 </td>
						   </tr>
						   <tr>
			                 <td> หมายเหตุ<font color="red"></font></td>
							 <td>			
							     <%-- <html:text property="bean.remark" styleId="remark" size="15"/>  --%> 
							     <input type="text" name ="remark" size="55" class="" value="<%=Utils.isNull(bean.getRemark()) %>"  autoComplete='off'/>  
							 </td>
						   </tr>
					     </table>
					     <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
								    <%if(bean.isCanSave()){ %>
										<a href="javascript:save('${pageContext.request.contextPath}')">
										  <input type="button" value="  บันทึก    " class="newPosBtnLong"> 
										</a>&nbsp;
									<%} %>
									<a href="javascript:backForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
									</a>			
								</td>
							</tr>
						</table>
						<!-- ************************Result Image **************************************-->
						<table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left" colspan="2">
									<b><u>แนบรูปประกอบ</u></b>
								</td>
							</tr>
							<%
							  int maxPic = 10;
							  ProjectCImageBean imgBean = bean.getImageBean();
							  System.out.println("imgBean:"+imgBean);
							  String imageId="";
							  String imageName="";
							  String imageNameDB = "";
							 for(int rowId=1;rowId <= maxPic;rowId++){ 
								 //odd
							   if(rowId==1){
								   imageId = "1";
								   imageName = imgBean.getImageName1();
								   imageNameDB = imgBean.getImageNameDB1();
							   }else if(rowId==3){
								   imageId = "3";
								   imageName = imgBean.getImageName3();
								   imageNameDB = imgBean.getImageNameDB3();
							   }else if(rowId==5){
								   imageId = "5";
								   imageName = imgBean.getImageName5();
								   imageNameDB = imgBean.getImageNameDB5();
							   }else if(rowId==7){
								   imageId = "7";
								   imageName = imgBean.getImageName7();
								   imageNameDB = imgBean.getImageNameDB7();
							   }else if(rowId==9){
								   imageId = "9";
								   imageName = imgBean.getImageName9();
								   imageNameDB = imgBean.getImageNameDB9();
							   }
							%>
							<tr>
								<td align="left" valign="bottom">รูปที่ <%=imageId %> &nbsp;&nbsp;
								     <input type="hidden" name="status_pic_<%=imageId%>" id="status_pic_<%=imageId%>"/>
							         <input type="hidden" name="db_pic_<%=imageId %>" id="db_pic_<%=imageId %>" 
							                value="<%=imageNameDB%>"/> 
							         <%if( !Utils.isNull(imageNameDB).equals("")){ %>
								         <img id="image_pic_<%=imageId%>" width="50" height="30"
								          src="${pageContext.request.contextPath}/photoServlet?pageName=ProjectC&fileName=<%=imageNameDB%>" />
								     <%}else{ %>
								         <img id='image_pic_<%=imageId%>' width="50" height="30"/>
								     <%} %>
								     <input type='file' name='pic_<%=imageId%>' id='pic_<%=imageId%>' style="width:150px" accept='image/*'
							           onchange='validateFile(this,<%=imageId%>)'/> 
							         <input type="button" value="Clear" name="del_image_pic_<%=imageId%>"  class="newPosBtnLong"
							           onclick="delImage('<%=imageName%>',<%=imageId%>)"/>
								</td>
								<%
								 //Column even
								 rowId++; 
								 if(rowId==2){
									   imageId = "2";
									   imageName = imgBean.getImageName2();
									   imageNameDB = imgBean.getImageNameDB2();
								   }else if(rowId==4){
									   imageId = "4";
									   imageName = imgBean.getImageName4();
									   imageNameDB = imgBean.getImageNameDB4();
								   }else if(rowId==6){
									   imageId = "6";
									   imageName = imgBean.getImageName6();
									   imageNameDB = imgBean.getImageNameDB6();
								   }else if(rowId==8){
									   imageId = "8";
									   imageName = imgBean.getImageName8();
									   imageNameDB = imgBean.getImageNameDB8();
								   }else if(rowId==10){
									   imageId = "10";
									   imageName = imgBean.getImageName10();
									   imageNameDB = imgBean.getImageNameDB10();
								   }
								%>
								<td align="left" valign="bottom">รูปที่ <%=imageId %> &nbsp;&nbsp;
								     <input type="hidden" name="status_pic_<%=imageId%>" id="status_pic_<%=imageId%>"/>
							         <input type="hidden" name="db_pic_<%=imageId %>" id="db_pic_<%=imageId %>" 
							                value="<%=imageNameDB%>"/> 
							         <%if( !Utils.isNull(imageNameDB).equals("")){ %>
								         <img id="image_pic_<%=imageId%>" width="50" height="30"
								          src="${pageContext.request.contextPath}/photoServlet?pageName=ProjectC&fileName=<%=imageNameDB%>" />
								     <%}else{ %>
								         <img id='image_pic_<%=imageId%>' width="50" height="30"/>
								     <%} %>
								     <input type='file' name='pic_<%=imageId%>' id='pic_<%=imageId%>' style="width:150px" accept='image/*'
							           onchange='validateFile(this,<%=imageId%>)'/> 
							         <input type="button" value="Clear" name="del_image_pic_<%=imageId%>"  class="newPosBtnLong"
							           onclick="delImage('<%=imageName%>',<%=imageId%>)"/>
								</td>
							</tr>
							<%}//for %>
						</table>
						
						
								           
					 	<!-- ************************Result Product *************-->
					<%if(bean.getItems() != null){ %>
							<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="1" class="tableSearch">
							       <tr>
										<th >รหัสสินค้า</th>
										<th >ชื่อสินค้า</th>
										<th >มี/ไม่มี สินค้าเพนส์</th>
										<th >จำนวนขา</th>
										<th >หมายเหตุ</th>	
								   </tr>
								<% 
								String tabclass ="";
								List<ProjectCBean> resultList = bean.getItems();
								for(int n=0;n<resultList.size();n++){
									ProjectCBean item = (ProjectCBean)resultList.get(n);
									//System.out.println("item["+item.getProductCode()+"]:found["+item.getFound()+"]");
									if(n%2==0){ 
										tabclass="lineO";
									}else{
										tabclass ="lineE";
									}
									%>
										<tr class="<%=tabclass%>">
											<td class="td_text_center" width="3%">
											  <input type="text" name="productCode" size="4" 
											  readonly class="disableText" value="<%=item.getProductCode() %>"/>
											</td>
											<td class="td_text_center" width="15%">
											   <input type="text" name="productName" size="45" 
											    readonly class="disableText" value="<%=item.getProductName() %>"/>
											</td>
											<td class="td_text_center" width="3%">
											   <input type="checkbox" name="found" value="Y" <%="Y".equalsIgnoreCase(item.getFound())?"checked":"" %>/>
											</td>
											<td class="td_text_center" width="3%">
											    <input type="text" name="leg" size="2" 
											     class="enableNumber" value="<%=item.getLeg()%>"  autoComplete='off' onkeydown="return inputNum(event);"/>
											 </td>
											 <td class="td_text_center" width="15%">
											    <input type="text" name="lineRemark" size="25" 
											     class="" value="<%=item.getLineRemark()%>"  autoComplete='off'/>
											 </td>
										</tr>
								<%}//for %>
						     </table>	
				          <%} %>	
				          
				           <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
								<%if(bean.isCanSave()){ %>
									<a href="javascript:save('${pageContext.request.contextPath}')">
									  <input type="button" value="  บันทึก    " class="newPosBtnLong"> 
									</a>&nbsp;
								<%} %>
							
									<a href="javascript:backForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
									</a>			
								</td>
							</tr>
						</table>
					 	</div>
					</form>
					<!-- BODY -->
					<!-- Input Hidden Field -->
					<input type="hidden" name="mode" id="mode" value="<%=bean.getMode() %>"/>
					
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