<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.web.itmanage.ITManageForm"%>
<%@page import="com.isecinc.pens.web.itmanage.ITManageBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="itManageForm" class="com.isecinc.pens.web.itmanage.ITManageForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
List<ITManageBean> itemNameList = (List<ITManageBean>)session.getAttribute("ITEM_NAME_LIST");
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
<style type="text/css">

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(path){
	 new Epoch('epoch_popup', 'th', document.getElementById('docDate'));
	
	//popup print case print and save
	<%if(request.getAttribute("saveAndPrint") != null) {%>
	  // printReportPopup(path);
	<%}%>
}
function printReportPopup(path){
	var id = document.getElementsByName("bean.id")[0].value;
	var url = path+"/jsp/popup/printPayPopup.jsp?report_name=PayInReport&docNo="+docNo;
	PopupCenter(url,'Printer',800,350);
}
function clearForm(path){
	var form = document.itManageForm;
	form.action = path + "/jsp/itManageAction.do?do=prepare&id=0&mode=add";
	form.submit();
	return true;
}
function clearForm(path){
	var form = document.itManageForm;
	form.action = path + "/jsp/itManageAction.do?do=prepare&id=0&mode=add";
	form.submit();
	return true;
}
function deleteAction(path,id){
	if(confirm("ยืนยันลบข้อมูล")){
		var form = document.itManageForm;
		form.action = path + "/jsp/itManageAction.do?do=deleteAction&ids="+id;
		form.submit();
		return true;
	}
	return false;
}

function back(path){
	var form = document.itManageForm;
	form.action = path + "/jsp/itManageAction.do?do=prepareHead&action=back";
	form.submit();
	return true;
}
function save(path){
	var form = document.itManageForm;
    var docType = document.getElementsByName("bean.docType");
	//alert(docType.value);
	if(docType[0].checked != true && docType[1].checked != true){
		alert("กรุณาระบุ ประเภทเอกสาร");
		docType[0].focus();
		return false;
	}
	if( $('#salesrepCode').val()==""){
		alert("กรุณาระบุ พนักงานขาย");
		$('#salesrepCode').focus();
		return false;
	}
	
	if(isSelectOne()){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/itManageAction.do?do=save&action=newsearch";
		form.submit();
		return true;
	}
	return false;
}
function isSelectOne(){
	//todo play with type
	var itemName = document.getElementsByName("itemName");
	//alert(chk.length);
	
	var bcheck=false;
	for(var i=itemName.length-1;i>=0;i--){
		if(itemName[i].value != ""){
			bcheck=true;
		}
	}
	if(!bcheck){alert('กรุณากรอกข้อมูล อย่างน้อย 1 แถว');return false;}
	
	return true;
}
function printReport(path){
	var form = document.itManageForm;
    var docType = document.getElementsByName("bean.docType");
	//alert(docType.value);
	if(docType[0].checked != true && docType[1].checked != true){
		alert("กรุณาระบุ ประเภทเอกสาร");
		docType[0].focus();
		return false;
	}
	if( $('#salesrepCode').val()==""){
		alert("กรุณาระบุ พนักงานขาย");
		$('#salesrepCode').focus();
		return false;
	}
	var param ="";
	if(isSelectOne()){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/itManageAction.do?do=printReport"+param;
		form.submit();
	}
}
function openPopup(path,pageName){
	var form = document.itManageForm;
	var param = "&hideAll=true&pageName="+pageName;
	 if("SalesrepSales" == pageName){
		param += "&selectone=true";
	}
	url = path + "/jsp/popupAction.do?do=prepareAll&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,desc2,desc3,pageName){
	var form = document.itManageForm;
	if('SalesrepSales' == pageName){
		form.salesrepCode.value = code;
		form.salesrepFullName.value = desc;
		form.zone.value = desc2;
		form.zoneName.value = desc3;
	}
} 
function getAutoOnblur(e,obj,pageName){
	var form = document.itManageForm;
	if(obj.value ==''){
		if("SalesrepSales" == pageName){
			form.salesrepCode.value = '';
			form.salesrepFullName.value = "";
			form.zone.value = "";
			form.zoneName.value = "";
		}
	}else{
		getAutoDetail(obj,pageName);
	}
}
function getAutoKeypress(e,obj,pageName){
	var form = document.itManageForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("SalesrepSales" == pageName){
				form.salesrepCode.value = '';
				form.salesrepFullName.value = "";
				form.zone.value = "";
				form.zoneName.value = "";
			}
		}else{
			getAutoDetail(obj,pageName);
		}
	}
}

function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.itManageForm;
	
	//prepare parameter
	var param = "";
	if("SalesrepSales"==pageName){
		param  ="pageName="+pageName;
		param +="&salesrepCode="+obj.value;
	}
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getAutoKeypressAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if("SalesrepSales" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.salesrepCode.value = retArr[1];
			form.salesrepFullName.value = retArr[2];
			form.zone.value = retArr[3];
			form.zoneName.value = retArr[4];
		}else{
			alert("ไม่พบข้อมูล");
			form.salesrepCode.focus();
			form.salesrepCode.value = '';
			form.salesrepFullName.value = "";
			form.zone.value = "";
			form.zoneName.value = "";
		}
	}
}

</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe('${pageContext.request.contextPath}');MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerDoc.jsp"/></td>
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
				<jsp:param name="function" value="ITStock"/>
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
						<html:form action="/jsp/itManageAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						    <tr>
					            <td align="right"></td>
								<td>
								    <html:radio property="bean.docType" value="Requisition"  />&nbsp;ใบเบิก &nbsp;&nbsp;
								    <html:radio property="bean.docType" value="Return" />&nbsp;ใบคืน
								</td>
							 </tr>	
						      <tr>
					            <td align="right">พนักงานขาย <font color="red">*</font></td>
								<td>
									<html:text property="bean.salesrepCode" styleId="salesrepCode" size="10" 
								    styleClass="\" autoComplete=\"off" 
								    onkeypress="getAutoKeypress(event,this,'SalesrepSales')"
								    onblur="getAutoOnblur(event,this,'SalesrepSales')"/>
								     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','SalesrepSales')"/>   
								     <html:text property="bean.salesrepFullName" styleId="salesrepFullName" styleClass="\" autoComplete=\"off" size="40"/>
								   &nbsp;&nbsp;&nbsp;
								  วันที่ทำรายการ 
								  <html:text property="bean.docDate" styleId ="docDate" styleClass="\" autoComplete=\"off"  size="10"/>
								</td>
							 </tr>	
							 <tr>
				                 <td> ภาคตามการดูแล<font color="red"></font></td>
								<td>		
									<html:text property="bean.zoneName" styleId ="zoneName" styleClass="disableText" readonly="true" size="50"></html:text>
								   <html:hidden property="bean.zone" styleId ="zone" />
								</td>
							</tr>
							<tr>
				                 <td> หมายเหตุ<font color="red"></font></td>
								<td>		
									<html:text property="bean.remark" styleId ="remark" size="70" styleClass="\" autoComplete=\"off" />
								  
								</td>
							</tr>
						   </table>
						  
						   <!-- Items -->
						   <table id="tblProduct" align="center" width="75%" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth">
						       <tr>
						            <th >ลำดับ</th>
									<th >อุปกรณ์  
									 
									</th>
									<th >Serial No</th>
									<th >จำนวน</th>
									<th >หมายุหตุ</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							//System.out.println("results:"+session.getAttribute("results"));
							ITManageForm aForm = (ITManageForm) session.getAttribute("itManageForm");
							List<ITManageBean> items = aForm.getBean() !=null?aForm.getBean().getItems():null;
							int tabindex = 1;
							if(items == null){
								
								for(int n=0;n<5;n++){
									if(n%2==0){
										tabclass="lineO";
									}
									%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="5%">
										  <input type="hidden" name="lineId" id="lineId"  value="">
										  <input type="text" name="no" id="no" size="1" readonly value="<%=(n+1) %>" tabindex="-1" class="disableText">
										</td>
										<td class="td_text" width="15%">
										   <input type="text" name="itemName" id="itemName" size="50" maxlength="60" tabindex="<%out.print(tabindex);tabindex++;%>" autoComplete='off'> 
										  <%--  <select name="itemName" id="itemName">
										     <option></option>
										     <%for(int i=0;i<itemNameList.size();i++){ 
										    	 ITManageBean item = itemNameList.get(i);
										         out.println("<option value='"+item.getItemName()+"'>"+item.getSeq()+"-"+item.getItemName()+"</option>"); 
										     }
										     %>
										   </select> --%>
										</td>
										<td class="td_text" width="15%">
										<input type="text" name="serialNo" id="serialNo" size="30" maxlength="30" tabindex="<%out.print(tabindex);tabindex++;%>" autoComplete='off'>
										</td>
										<td class="td_text_right" width="10%"> 
										   <input type="text" name="qty" id="qty" size="10" tabindex="<%out.print(tabindex);tabindex++;%>"
										    onblur="isNum(this);"  class="enableNumber" autoComplete='off'>
										</td>
										<td class="td_text" width="15%">
										<input type="text" name="remark" id="remark" size="40" maxlength="60" tabindex="<%out.print(tabindex);tabindex++;%>" autoComplete='off'>
										</td>
									</tr>
							<% } //for
							}else{
                                 for(int n=0;n<5;n++){
                                	 ITManageBean item = new ITManageBean();
                                	if( n<items.size()){
                                	   item = items.get(n);
                                	}
									if(n%2==0){
										tabclass="lineO";
									}
									%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="5%">
										  <input type="hidden" name="lineId" id="lineId"  value="<%=item.getLineId()%>">
										  <input type="text" name="no" id="no" size="1" readonly value="<%=(n+1) %>"  tabindex="-1" class='disableText'>
										</td>
										<td class="td_text" width="15%">
										  <input type="text" name="itemName" id="itemName" size="50" maxlength="60" 
										  tabindex="<%out.print(tabindex);tabindex++;%>" 
										  autoComplete='off' value="<%=Utils.isNull(item.getItemName())%>"/> 
										  <%-- <select name="itemName" id="itemName">
										     <option></option>
											 <%for(int i=0;i<itemNameList.size();i++){ 
										    	 ITManageBean itemL = itemNameList.get(i);
										    	 if(itemL.getItemName().equalsIgnoreCase(item.getItemName())){
										    	   out.println("<option value='"+itemL.getItemName()+"' selected>"+itemL.getSeq()+"-"+itemL.getItemName()+"</option>");
										    	 }else{
										           out.println("<option value='"+itemL.getItemName()+"'>"+itemL.getSeq()+"-"+itemL.getItemName()+"</option>");
										    	 }
										     }
										     %>
										    </select> --%>
										</td>
										<td class="td_text" width="15%">
										<input type="text" name="serialNo" value="<%=Utils.isNull(item.getSerialNo()) %>" id="serialNo" 
										   size="20" maxlength="30" tabindex="<%out.print(tabindex);tabindex++;%>" autoComplete='off'>
										</td>
										<td class="td_text_right" width="10%"> 
										   <input type="text" name="qty" value="<%=Utils.isNull(item.getQty())%>" id="qty" size="10" tabindex="<%out.print(tabindex);tabindex++;%>"
										    onblur="isNum(this);"  class="enableNumber" autoComplete='off'>
										</td>
										<td class="td_text" width="15%">
										<input type="text" name="remark" value="<%=Utils.isNull(item.getRemark())%>" id="remark" size="40" maxlength="60" tabindex="<%out.print(tabindex);tabindex++;%>" autoComplete='off'>
										</td>
									</tr>
						    <%}//for
						    }//if %>
							 
					</table>
					
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
								     <%if(itManageForm.getBean().getId() != 0){ %>
							             <a href="javascript:deleteAction('${pageContext.request.contextPath}',<%=itManageForm.getBean().getId()%>)">
										  <input type="button" value="ลบรายการ " class="newPosBtnLong">
										</a>
										&nbsp;
									 <% } %>
						            <a href="javascript:printReport('${pageContext.request.contextPath}')">
									  <input type="button" value="   พิมพ์   " class="newPosBtnLong">
									</a>
									&nbsp;
									
									<a href="javascript:save('${pageContext.request.contextPath}')">
									  <input type="button" value="    บันทึก   " class="newPosBtnLong"> 
									</a>
									&nbsp;
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>
									&nbsp;&nbsp;&nbsp;
									<a href="javascript:back('${pageContext.request.contextPath}','','add')">
									  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
									</a>							
								</td>
							</tr>
						</table>
					 </div>
					<!-- hidden field -->
					<html:hidden property="bean.id"/>
					<html:hidden property="pageName"/>
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

 <!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->