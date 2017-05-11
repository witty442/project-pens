
<%@page import="com.isecinc.pens.bean.Master"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.bean.LockItemOrderBean"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>

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


Map<String,String> StoreCodeToMap = new HashMap<String,String>();
if(session.getAttribute("StoreCodeToMap") != null){
	StoreCodeToMap = (Map<String,String>) session.getAttribute("StoreCodeToMap");
}

Map<String,String> groupStoreMapError = new HashMap<String,String>();
if(session.getAttribute("groupStoreMapError") != null){
	groupStoreMapError = (Map<String,String>) session.getAttribute("groupStoreMapError");
	System.out.println("groupStoreMapError:"+groupStoreMapError);
}

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

input[type=checkbox]
{
  /* Double-sized Checkboxes */
  -ms-transform: scale(2); /* IE */
  -moz-transform: scale(2); /* FF */
  -webkit-transform: scale(2); /* Safari and Chrome */
  -o-transform: scale(2); /* Opera */
  padding: 10px;
}

input[type=radio]
{
  /* Double-sized Checkboxes */
  -ms-transform: scale(2); /* IE */
  -moz-transform: scale(2); /* FF */
  -webkit-transform: scale(2); /* Safari and Chrome */
  -o-transform: scale(2); /* Opera */
  padding: 10px;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/DateUtils.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
   <%if("add".equals(lockItemOrderForm.getBean().getMode())){ %>
      new Epoch('epoch_popup', 'th', document.getElementById('lockDate'));
   <%}%>
   
   new Epoch('epoch_popup', 'th', document.getElementById('unlockDate'));
}

function setStyletextField(){
	document.getElementById('empId').className= "disableText";
	document.getElementById('empId').readOnly= true;
	document.getElementById('startDate').className= "disableText";
}

function clearForm(path){
	var form = document.lockItemOrderForm;
	form.action = path + "/jsp/lockItemOrderAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.lockItemOrderForm;
	form.action = path + "/jsp/lockItemOrderAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function validateTable(){
	var r = true;
	var custGroup = null;
	var custGroupName = null;
	var chkStore = null;
	var storeCode = null;
	var btn = null;
<%
	  List<Master> custGroupList = (List)session.getAttribute("custGroupList");
	  for(int i=0;i<custGroupList.size();i++){
		  Master m = custGroupList.get(i);	 
%>
         chkStore =document.getElementsByName("chkStore_<%=m.getPensValue()%>") ;
         storeCode =document.getElementsByName("store_<%=m.getPensValue()%>") ;
         btn = document.getElementsByName("btn_<%=m.getPensValue()%>") ;
         custGroup = document.getElementsByName("custGroup_<%=m.getPensValue()%>") ;
         custGroupName = document.getElementsByName("custGroupName_<%=m.getPensValue()%>") ;
         
         if(chkStore[1].checked && storeCode[0].value ==""){
        	// alert(chkStore[1].checked +":"+storeCode[0].value);
        	 alert("กรูณา ระบุร้านค้า หากเลือกข้อมูลเป็นบางร้านค้า");
        	 custGroup[0].className="storeError";
        	 custGroupName[0].className="storeError";
        	 storeCode[0].className ="storeError";
        	 r = false;
         }else{
        	 custGroup[0].className="";
        	 custGroupName[0].className="";
        	 storeCode[0].className ="disableText";
         }
<%
	  }//for
	  
%>
	  return r;
}

function save(path){
	var form = document.lockItemOrderForm;

	if( $('#groupCode').val()==""){
		alert("กรุณาระบุ  Group Code");
		$('#groupCode').focus();
		return false;
	}
	if( $('#lockDate').val()==""){
		alert("กรุณาระบุ  lockDate");
		$('#lockDate').focus();
		return false;
	}
	
	if( $('#unlockDate').val() !=""){
		//unlockDate must > lockDate
		var lockDate = thaiDateToChristDate($('#lockDate').val());
		var unlockDate = thaiDateToChristDate($('#unlockDate').val());
		
		//alert(lockDate+":"+unlockDate);
		if(lockDate >  unlockDate){
			alert("วันที่ unlock date ต้องมากกว่า หรือเท่ากับ Lock Date");
			$('#unlockDate').focus();
			return false;
		}
	}
	if(validateTable()==false){
		return false;
	}
	
	if(confirm("กรุณายืนยันการบันทึกข้อมูล")){
		form.action = path + "/jsp/lockItemOrderAction.do?do=save";
		form.submit();
		return true;
	}
	return false;
}

function isNum(obj){
	//alert("isNum");
	  if(obj.value != ""){
		if(isNaN(obj.value)){
			alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
			obj.value = "";
			obj.focus();
			return false;
		}else{return true;}
	   }
	  return true;
}

function openPopupGroupCode(path){
    var param = "&selectOne=selectOne";
	url = path + "/jsp/searchGroupPopupAction.do?do=prepare&action=new"+param;
	//window.open(encodeURI(url),"",
		//	   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	var w = "650px";
	PopupCenterFullHeight(url, "ค้นหาข้อมูล กลุ่มสินค้า", w) ;
}

function setGroupMainValue(code,desc,types){
	var form = document.lockItemOrderForm;
	form.groupCode.value = code;
	//form.groupDesc.value = desc;
}

function getStore(path,storeGroup){
	var chkStore =document.getElementsByName("chkStore_"+storeGroup) ;
	//alert(chkStore[0].checked+":"+chkStore[1].checked);
	
	if(chkStore[1].checked){
		var storeCodeArr = document.getElementsByName("storeCodeArr_"+storeGroup)[0].value
	    var param = "&storeGroup="+storeGroup+"&storeCodeArr="+storeCodeArr;
	     
		url = path + "/jsp/searchCustomerPopupAction.do?do=searchMultiCustomer"+param;
		//window.open(encodeURI(url),"",
				 //  "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
		var w = "650px";
		PopupCenterFullHeight(url, "ร้านค้า", w) ;
	}else{
		alert("กรุณาเลือกข้อมูลเป็น เฉพาะบางร้าน");
		//chkStore.focus();
	}
}

function setStoreMainValue(storeGroup,code,desc){
    document.getElementsByName("store_"+storeGroup)[0].value = code;
}

function uncheck(name,index){
	 document.getElementsByName("chkStore_"+name)[index].checked=false ;
	 if(index ==1){ //clear store code
		 document.getElementsByName("store_"+name)[0].value ="";
		 document.getElementsByName("storeCodeArr_"+name)[0].value ="";
		 
		/*  var groupStore = document.getElementsByName("custGroup_"+name)[0].value; 
		 
		// alert($('#groupCode').val()+":"+groupStore);
	     //remove data in StoreCodeToMap session
			$(function(){
				var getData = $.ajax({
					url: "${pageContext.request.contextPath}/jsp/ajax/clearSessionLockItem.jsp",
					data : "groupCode=" + $('#groupCode').val() +
					       "&groupStore=" + groupStore,
					async: false,
					cache: true,
					success: function(){
					}
				}).responseText;
			}); */
	 }else{
		 document.getElementsByName("store_"+name)[0].value ="";
		 document.getElementsByName("storeCodeArr_"+name)[0].value ="";
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
						<html:form action="/jsp/lockItemOrderAction"  enctype="multipart/form-data">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
									<td colspan="6" align="center">	
									   <font size="3" ><b>
										 <%if("add".equals(lockItemOrderForm.getBean().getMode())){ %>
									                                เพิ่มข้อมูล
										 <%}else{ %>
									                              แก้ไขข้อมูล
										 <%} %>
										 </b>
										 </font>	
									</td>
								</tr>
								<tr>
                                    <td  align="right">Group Code<font color="red">*</font></td>
									<td colspan="3">		
								       <html:text property="bean.groupCode" styleId="groupCode" size="30"> </html:text>	   
								        <input type="button" name="x1" value="..." onclick="openPopupGroupCode('${pageContext.request.contextPath}')"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">วันที่เริ่ม Lock<font color="red">*</font></td>
									<td colspan="3">	
									 <%if("add".equals(lockItemOrderForm.getBean().getMode())){ %>
									    <html:text property="bean.lockDate" styleId="lockDate"  size="30"  readonly="true"/>
									<%}else{ %>
									   <html:text property="bean.lockDate" styleId="lockDate"  size="30"  readonly="true" styleClass="disableText"/>
									<%} %>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									วันที่ปลด Lock
									    <html:text property="bean.unlockDate" styleId="unlockDate" size="30"  readonly="true"/>
									 </td>
								</tr>
						   </table>
						   <p></p>
						  <table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="80%">
						       <tr>
                                    <th width="10%">กลุ่มร้านค้า</th>
									<th width="20%">ชื่อกลุ่มร้านค้า</th>
									<th width="10%">ทั้งกลุ่มร้านค้า</th>
									<th width="10%">เฉพาะบางร้านค้า</th>
									<th width="10%">ระบุรายร้านค้า</th>
								</tr>
								<%
								String tabclass ="lineE";
								String key = "";
								String storeCodeArr = "";
								String allStoreFlag = "";
								String someStoreFlag = "";
								if(custGroupList != null){ 
								  for(int i=0;i<custGroupList.size();i++){
									  tabclass ="lineE";
									  Master m = custGroupList.get(i);	  
									  if(i%2==0){ 
										tabclass="lineO";
									  }
									 key = lockItemOrderForm.getBean().getGroupCode()+"_"+m.getPensValue();
									 storeCodeArr = Utils.isNull(StoreCodeToMap.get(key));
									  
									 allStoreFlag=  (storeCodeArr.indexOf("ALL") != -1?"checked":"");
									 someStoreFlag= ((storeCodeArr.indexOf("ALL") == -1 && !Utils.isNull(storeCodeArr).equals(""))?"checked":"") ;
									 
									 //display error tr
									 if(groupStoreMapError.get(m.getPensValue()) != null){
										 tabclass = "storeError";
									 }
									 
								%>
								 <tr class="<%=tabclass%>"> 
                                    <td width="10%"><input type="text" name="custGroup_<%=m.getPensValue()%>" readonly value="<%=m.getPensValue()%>" size="10"  class="disableText"/></td>
									<td width="15%"><input type="text" name="custGroupName_<%=m.getPensValue()%>" readonly value="<%=m.getPensDesc()%>" size="40"  class="disableText"/></td>
									<td width="8%">
									<input type="checkbox" name="chkStore_<%=m.getPensValue()%>" value="allStore"  <%=allStoreFlag %> onclick="uncheck('<%=m.getPensValue()%>',1)">
									</td>
									<td width="8%">
									<input type="checkbox" name="chkStore_<%=m.getPensValue()%>" value="someStore"  <%=someStoreFlag%> onclick="uncheck('<%=m.getPensValue()%>',0)">
									</td>
									<td width="30%"> 
									   <input type="button" name="btn_<%=m.getPensValue() %>" value="เลือกร้านค้า" class="newPosBtnLong" onclick="getStore('${pageContext.request.contextPath}','<%=m.getPensValue()%>')">
									   <input type="text" name ="store_<%=m.getPensValue()%>" size="40" readonly value="<%=storeCodeArr%>" class="disableText"/>
									   <input type="hidden" name ="storeCodeArr_<%=m.getPensValue()%>" value="<%=storeCodeArr%>"/>
									</td>
								</tr>
							  <%
								 }//for
								}//if
							  %>
						   </table>
	
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									<a href="javascript:save('${pageContext.request.contextPath}')">
									  <input type="button" value="    บันทึก   " class="newPosBtnLong"> 
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>
									<a href="javascript:back('${pageContext.request.contextPath}','','add')">
									  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
									</a>							
								</td>
							</tr>
					  </table>
				  </div>
				
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
					Mode:${lockItemOrderForm.bean.mode}
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