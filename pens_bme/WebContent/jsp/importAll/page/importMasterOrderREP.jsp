<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="importAllForm" class="com.isecinc.pens.web.importall.ImportAllForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));
	String pageName = Utils.isNull(request.getParameter("pageName"));
	String hideAll = "true";
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<script type="text/javascript">
function loadMe(){
}
function importExcel(refCode){
	var form = document.importAllForm;
	var extension1 = '';
	var extension2 = '';
	var extension3 = '';
	 if(form.dataFile.value.indexOf(".") > 0){
		extension = form.dataFile.value.substring(form.dataFile.value.lastIndexOf(".") + 1).toLowerCase();
	}
	
	if(form.dataFile2.value.indexOf(".") > 0){
		extension2 = form.dataFile2.value.substring(form.dataFile2.value.lastIndexOf(".") + 1).toLowerCase();
	} 
	if(form.dataFile3.value.indexOf(".") > 0){
		extension3 = form.dataFile3.value.substring(form.dataFile3.value.lastIndexOf(".") + 1).toLowerCase();
	}
	if('replenishment_minmax' ==refCode){
		if( form.dataFile.value != ''){
			if(extension != 'xls' && extension != 'xlsx'){
				alert("กรุณาเลือกไฟล์นามสกุล .xls ,xlsx");
				return false;
			}
		}else{
			alert("กรุณาเลือกไฟล์ เพื่อทำการ Upload");
			return false;
		}
	 }
	if('replenishment_priority' ==refCode){
		if( form.dataFile2.value != ''){
			if(extension2 != 'xls' && extension2 != 'xlsx'){
				alert("กรุณาเลือกไฟล์นามสกุล .xls ,xlsx");
				return false;
			}
		}else{
			alert("กรุณาเลือกไฟล์ เพื่อทำการ Upload");
			return false;
		}
	 }
	if('ref_config' ==refCode){
		if( form.dataFile3.value != ''){
			if(extension3 != 'xls' && extension3 != 'xlsx'){
				alert("กรุณาเลือกไฟล์นามสกุล .xls ,xlsx");
				return false;
			}else{
				var custGroup = document.getElementById("custGroup");
				if(custGroup.value ==""){
					alert("กรุณาระบุ กลุ่มร้านค้า");
					custGroup.focus();
					return false;
				}
			}
		}else{
			alert("กรุณาเลือกไฟล์ เพื่อทำการ Upload");
			return false;
		}
	}
	
	/**  Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	form.action = form.path.value + "/jsp/importAllAction.do?do=importExcel&refCode="+refCode;
	form.submit();
	return true;
}
function clearForm(){
	var form = document.importAllForm;
	form.action = form.path.value + "/jsp/importAllAction.do?do=prepare&action=new";
	form.submit();
	return true;
}
function viewData(reportType){
	var form = document.importAllForm;
	form.action = form.path.value + "/jsp/importAllAction.do?do=view&reportType="+reportType;
	form.submit();
	return true;
}
function exportData(reportType){
	var form = document.importAllForm;
	form.action = form.path.value + "/jsp/importAllAction.do?do=export&reportType="+reportType;
	form.submit();
	return true;
}
function saveDataAjax(refCode){
	var form = document.importAllForm;
	var custGroup = "";
	var inputQty = "";
	if("replenishment_backsales"==refCode){
		if(form.custGroupBackDay.value=="" || form.inputQtyBackDay.value==""){
			alert("กรุณาระบุข้อมูลให้ครบ");
			if(form.custGroupBackDay.value==""){
				form.custGroupBackDay.focus();
				return false;
			}
			if(form.inputQtyBackDay.value==""){
				form.inputQtyBackDay.focus();
				return false;
			}
		}
		custGroup =form.custGroupBackDay.value;
		inputQty = form.inputQtyBackDay.value;
	}else if("replenishment_coverday"==refCode){
		if(form.custGroupCoverDay.value=="" || form.inputQtyCoverDay.value==""){
			alert("กรุณาระบุข้อมูลให้ครบ");
			if(form.custGroupCoverDay.value==""){
				form.custGroupCoverDay.focus();
				return false;
			}
			if(form.inputQtyCoverDay.value==""){
				form.inputQtyCoverDay.focus();
				return false;
			}
		}
		custGroup =form.custGroupCoverDay.value;
		inputQty = form.inputQtyCoverDay.value;
	}
	if(custGroup !=="" && inputQty !=""){
		//validate is OrderDate is Generated
		var returnString= "";
		var param = "refCode="+refCode+"&custGroup="+custGroup+"&inputQty="+inputQty;
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/autoOrder/ajax/saveMasterConfigAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
		
		//alert(returnString);
		if(returnString == "-1"){ //Return StoreCode No Confirm
			alert("ไม่สามารถบันทึกได้");
			return false;
		}else{
			alert("บันทึกข้อมูลเรียบร้อยแล้ว");
			return false;
		}
	}
}
function loadDataAjax(refCode){
	var form = document.importAllForm;
	var custGroup = "";
	var inputQty = "";
	if("replenishment_backsales"==refCode){
		custGroup =form.custGroupBackDay.value;
		inputQty = form.inputQtyBackDay;
	}else if("replenishment_coverday"==refCode){
		custGroup =form.custGroupCoverDay.value;
		inputQty = form.inputQtyCoverDay;
	}
	if(custGroup !==""){
		//validate is OrderDate is Generated
		var returnString= "";
		var param = "refCode="+refCode+"&custGroup="+custGroup;
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/autoOrder/ajax/loadMasterConfigAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
		
		//alert(returnString);
		if(returnString != ""){ 
			inputQty.value=returnString;
		}
	}
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
			<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="<%=pageName %>"/>
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
						<html:form action="/jsp/importAllAction" enctype="multipart/form-data">
						<jsp:include page="../../error.jsp"/>
						<!-- Criteria -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						    <tr>
						      <td align="left" width="25%"></td>
						      <td align="left"><font size="2"><b><u>ข้อมูล Replenishment Config</u></b></font></td>
						    </tr> 
							 <tr>
								<td align="right" width="25%">เลือกไฟล์ (Excel)&nbsp;&nbsp;</td>
								<td valign="top" align="left">
								    
									<html:file property="dataFile3" styleId="dataFile3" styleClass="" style="width:300px;height:21px"/>
								     &nbsp;
								     กลุ่มร้านค้า <font color="red">*</font> &nbsp; 
								   <html:select property="bean.custGroup" styleId="custGroup">
								     <html:option value=""></html:option>
								     <html:option value="020047">020047-Lotus</html:option>
								     <html:option value="020049">020049-BIGC</html:option>
								   </html:select>
								   
								</td>
							</tr> 
							<tr>
							    <td align="left" width="25%"></td>
								<td align="left" >
									<font color="red">*ลบข้อมูลเดิมออกทั้งหมด ตามกลุ่มร้านค้า (PENSBI.BME_CONFIG_REP)</font> 
								</td>
							</tr> 
							 <tr>
								<td align="right" width="25%">Filter แสดงข้อมูล</td>
								<td valign="top" align="left">
								         รหัสสาขา:<html:text property="bean.storeCode" styleId="storeCode" size="10" styleClass="\" autoComplete=\"off"/>
								    Group:<html:text property="bean.group" styleId="group" size="10" styleClass="\" autoComplete=\"off"/>
								    &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								     <input type="button" value="  Upload  " class="newPosBtnLong" onclick="javascript:importExcel('ref_config')">
								     <input type="button" value="แสดงข้อมูล " class="newPosBtnLong" onclick="javascript:viewData('rep_config')">
								     <input type="button" value="Export " class="newPosBtnLong" onclick="javascript:exportData('rep_config')">
								     <input type="button" value="  Clear  " class="newPosBtnLong" onclick="javascript:clearForm()">
								</td>
							</tr> 
							<tr>
							    <td colspan="2"><hr></td>
							</tr>
							 <tr>
							  <td align="left" width="25%"></td>
						      <td align="left"><font size="2"><b><u>ข้อมูล Min - Max  ตามกลุ่มสินค้า ,Priority</u></b></font></td>
						    </tr>
						      <tr>
								<td align="right" width="20%">เลือกไฟล์(Excel)&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:file property="dataFile"  styleId="dataFile" styleClass="" style="width:300px;height:21px"/>	
									 &nbsp;&nbsp;
									 <input type="button" value="  Upload  " class="newPosBtnLong" onclick="javascript:importExcel('replenishment_minmax')">
								     <input type="button" value="แสดงข้อมูล " class="newPosBtnLong" onclick="javascript:viewData('rep_minmax')">
								     <input type="button" value="Export " class="newPosBtnLong" onclick="javascript:exportData('rep_minmax')">
								     <input type="button" value="  Clear  " class="newPosBtnLong" onclick="javascript:clearForm()">		
								</td>
							</tr> 
							<tr>
							    <td align="left" width="25%"></td>
								<td align="left">
									<font color="red">*ลบข้อมูลเดิมออกทั้งหมด (pensbi.pensbme_mst_reference where reference_code ='replenishment_minmax')</font>
								</td>
							</tr>
							<tr>
							    <td colspan="2"><hr></td>
							</tr>
							 <tr>
							  <td align="left" width="25%"></td>
						      <td align="left"><font size="2"><b><u>ข้อมูล Priority ตามรหัสร้านค้า</u></b></font></td>
						    </tr>
							 <tr>
								<td align="right" width="20%">เลือกไฟล์ (Excel)&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:file property="dataFile2"  styleId="dataFile2" styleClass="" style="width:300px;height:21px"/>
								    &nbsp;&nbsp;
								    <input type="button" value="  Upload  " class="newPosBtnLong" onclick="javascript:importExcel('replenishment_priority')">
								    <input type="button" value="แสดงข้อมูล " class="newPosBtnLong" onclick="javascript:viewData('rep_priority')">
								    <input type="button" value="Export " class="newPosBtnLong" onclick="javascript:exportData('rep_priority')">
								     <input type="button" value="  Clear  " class="newPosBtnLong" onclick="javascript:clearForm()">
								</td>
							</tr> 
							<tr>
							    <td align="left" width="25%"></td>
								<td  align="left">
									<font color="red">*ลบข้อมูลเดิมออกทั้งหมด (pensbi.pensbme_mst_reference where reference_code ='replenishment_priority')</font>
								</td>
							</tr>
							<tr>
							    <td colspan="2"><hr></td>
							</tr>
							<tr>
							  <td align="left" width="25%"></td>
						      <td align="left"><font size="2"><b><u>ข้อมูล ยอดขายย้อนหลัง (replenishment_backsales)</u></b></font></td>
						    </tr>
							 <tr>
								<td align="right" width="20%">ประเภทร้าน<font color="red">*</font></td>
								<td valign="top" align="left">
									<select id="custGroupBackDay" onchange="loadDataAjax('replenishment_backsales')">
									   <option value=""></option>
									   <option value="020047">LOTUS</option>
									   <option value="020049">BIGC</option>
									</select>
									&nbsp;จำนวน<font color="red">*</font>
									<input type="text" name="inputQtyBackDay" id="inputQtyBackDay" 
									size="3" autoComplete="off" onkeydown="return inputNum(event);"/>(วัน)
									<input type="button" value="   บันทึก   " class="newPosBtnLong" 
									onclick="javascript:saveDataAjax('replenishment_backsales')">
								</td>
							</tr> 
							<tr>
							    <td colspan="2"><hr></td>
							</tr>
							<tr>
							  <td align="left" width="25%"></td>
						      <td align="left"><font size="2"><b><u>ข้อมูล CoverDay(replenishment_coverday)</u></b></font></td>
						    </tr>
							 <tr>
								<td align="right" width="20%">ประเภทร้าน<font color="red">*</font></td>
								<td valign="top" align="left">
									<select id="custGroupCoverDay" onchange="loadDataAjax('replenishment_coverday')">
									   <option value=""></option>
									   <option value="020047">LOTUS</option>
									   <option value="020049">BIGC</option>
									</select>
									&nbsp;จำนวน<font color="red">*</font>
									<input type="text" name="inputQtyCoverDay" id="inputQtyCoverDay" 
									size="3" autoComplete="off" onkeydown="return inputNum(event);"/>(วัน)
									<input type="button" value="   บันทึก   " class="newPosBtnLong" 
									onclick="javascript:saveDataAjax('replenishment_coverday')">
								</td>
							</tr> 
							<tr>
							    <td colspan="2"><hr></td>
							</tr>
						</table>
						
						<!-- Button -->
						<!-- <br/>
						<div align="center">
						   <input type="button" value="  Upload  " class="newPosBtnLong" onclick="javascript:importExcel()">
						   <input type="button" value="  Clear  " class="newPosBtnLong" onclick="javascript:clearForm()">
						</div> -->
						
						
						<!-- Result -->
						<%
						try{
						boolean foundData = false;
						boolean foundError = false;
						String[] columnHeadArr = null;
						List<String> successList = null;
						System.out.println("1:");
						List<String> failList = session.getAttribute("ERROR_LIST")!=null?(List)session.getAttribute("ERROR_LIST"):null;
						System.out.println("2:failList:"+failList);
						if( failList==null ||( failList != null && failList.size() ==0)){
							successList = session.getAttribute("SUCCESS_LIST") !=null?(List)session.getAttribute("SUCCESS_LIST"):null;
							if(successList != null && successList.size() >0){
								System.out.println("successList:"+successList.size());
								foundData = true;
							}
						}else{
							foundError = true;
							foundData = true;
							System.out.println("failList:"+failList.size());
						}
						System.out.println("foundData:"+foundData);
						
						if(foundData){
							%>
							<p></p>
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<%if(foundError){ 
								//Gen Column Head Table (list[0] = head table)
								columnHeadArr = failList.get(0).split("\\|");
							%>
							<tr>
							  <th colspan="<%=columnHeadArr.length+1%>"><font color="#921F06">จำนวน Row ที่ไม่สามารถ Import ได้  <%=failList.size()-1 %> Row </font></th>
							</tr>
							<%}else{ 
								//Gen Column Head Table (list[0] = head table)
								columnHeadArr = successList.get(0).split("\\|");
							%>
							<tr>
							  <th colspan="<%=columnHeadArr.length+1%>"><font color="">จำนวน Row สามารถ Import ได้  <%=successList.size()-1 %> Row </font></th>
							</tr>
							<%} %>
							<tr>
							   <th width="5%">No</th>
								<% 
								  for(int c=0;c<columnHeadArr.length;c++){
								%>
								  <th width="5%"><%=columnHeadArr[c]%></th>
								<% } //for %>
							</tr>
							<%
							 String[] lineArr = null;
							 List<String> dataList = null;
							 String tabclass ="lineE";
							 if(foundError){
								 dataList = failList;
							 }else{
								 dataList = successList;
							 }
							 //Case SuccessList more than 1000 record not show
							 if( foundError ==true || (dataList.size() <= 1000 && foundError ==false)){
								 int no = 0;
								 for(int i=1;i<dataList.size();i++){
									 no++;
									 lineArr = dataList.get(i).split("\\|");
									 tabclass = i%2==0?"lineO":"lineE";
								%>
									<tr class="<%=tabclass%>">
									    <td><%=no%></td>
									    <%for(int c=0;c<lineArr.length;c++){ %>
									        <%if(c==lineArr.length-1){ %>
									          <!-- Show Message Error -->
									          <%if(foundError){ %>
										        <td align="left" width="10%"><font color="red"><%=lineArr[c] %></font></td>
										      <%}else{ %>
										         <td align="left" width="10%"><font color="green"><%=lineArr[c] %></font></td>
										      <%} %>
										    <%}else{ %>
											   <td><%=lineArr[c] %></td>
											<%} %>
										<% }%>
									</tr>
									<%}//FOR %>
								</table>
							<%}else{//if 
								lineArr = dataList.get(1).split("\\|");
							%>
							     <tr class="lineE">
									 <td colspan="<%=lineArr.length+1%>"> เนื่องจากข้อมูลที่แสดงมีจำนวนมาก มากกว่า 1 พันรายการ  จะแสดงเฉพาะจำนวน Row ที่เอาเข้าได้
									 จำนวน Row ที่ import สำเร็จ :<%=dataList.size()-1 %> Row
									 </td>
								</tr>
								</table>
							<%} //if
						  }//if
						}catch(Exception e){
							e.printStackTrace();
						}
						
						%>
						<br/>
						<!-- View Data -->
						<%
						if(session.getAttribute("VIEW_DATA") != null){ %>
							<div align='center'><font size="2"><b><%=(String)session.getAttribute("TOPIC_NAME")%></b></font></div>
						<%
						  out.println(((StringBuffer)session.getAttribute("VIEW_DATA")).toString()); 
						}
						%>
						
						<!-- hidden field -->
						<input type="hidden" name="pageName" value="<%=pageName %>"/>
						<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
					
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
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>
<!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreen.jsp"/>