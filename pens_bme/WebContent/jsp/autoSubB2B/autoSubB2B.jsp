<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.web.autosubb2b.AutoSubB2BBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Calendar"%>
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
<style>
fieldset {
    border:0;
    outline: 1px solid gray;
    width: 70%;
    align:center;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<script type="text/javascript">
function loadMe(){
}
function saveAction(path){
	var form = document.autoSubB2BForm;
	/** set enable for get Value By Struts is Object is disabled **/
	var refTypeObj = document.getElementsByName('bean.refType');
	refTypeObj[0].disabled = false;
	refTypeObj[1].disabled = false;
	
	if(form.refNo.value ==""){
		alert("กรุณาระบุ Order Lot No/Request No");
		form.refNo.focus();
		return false;
	}
	if(form.toStoreCode.value ==""){
		alert("กรุณาระบุร้านค้าใหม่ ");
		form.toStoreCode.focus();
		return false;
	}
	if(form.toStoreName.value ==""){
		alert("รหัสร้านค้าใหม่  ไม่ถูกต้อง");
		form.toStoreCode.focus();
		return false;
	}
	if(form.fromStoreCode.value == form.toStoreCode.value){
		alert("รหัสร้านใหม่ ต้องไม่เท่ากับร้านค้าเก่า");
		form.toStoreCode.value = '';
		form.toStoreName.value = '';
		//form.storeNo.value = '';
		form.toSubInv.value = '';
		return false;
	}
	if(form.forwarder.value==""){
		alert("กรุณาระบุ ขนส่ง ");
		form.forwarder.focus();
		return false;
	}
	if(form.forwarderBox.value==""){
		alert("กรุณาระบุ จำนวนกล่อง");
		form.forwarderBox.focus();
		return false;
	}
	if(form.reason.value==""){
		alert("กรุณาระบุ เหตุผลในการขอย้าย");
		form.reason.focus();
		return false;
	}
	if(confirm("ยืนยันบันทึกข้อมูล")){
		/**  Control Save Lock Screen **/
		startControlSaveLockScreen();
		
	    form.action = path + "/jsp/autoSubB2BAction.do?do=save";
	    form.submit();
	    return true;
	}
	return false;
}
function clearAction(path){
	var form = document.autoSubB2BForm;
	 form.action = path + "/jsp/autoSubB2BAction.do?do=prepare&action=new";
	 form.submit();
	 return true;
}
function approveAction(path){
	var form = document.autoSubB2BForm;
	/** set enable for get Value By Struts is Object is disabled **/
	var refTypeObj = document.getElementsByName('bean.refType');
	refTypeObj[0].disabled = false;
	refTypeObj[1].disabled = false;
	
	if(confirm("ยืนยัน Approve ")){
		/**  Control Save Lock Screen **/
		startControlSaveLockScreen();
		
	    form.action = path + "/jsp/autoSubB2BAction.do?do=approveAction";
	    form.submit();
	    return true;
	}
	return false;
}
function backAction(path){
	var form = document.autoSubB2BForm;
	form.action = path + "/jsp/autoSubB2BAction.do?do=search2&action=back";
	form.submit();
	return true;
}
function openPopup(path,pageName){
	var form = document.autoSubB2BForm;
	var param = "&pageName="+pageName;
	 if("CustomerAutoSub" == pageName){
		param +="&hideAll=true&selectone=true";
	}
	url = path + "/jsp/popupAction.do?do=prepareAll&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}

function setDataPopupValue(code,desc,desc2,desc3,pageName){
	var form = document.autoSubB2BForm;
	if("CustomerAutoSub" == pageName){
		//alert(code);
		form.toStoreCode.value = code;
		form.toStoreName.value = desc;
		form.toStoreNo.value = desc2;
		form.toSubInv.value = desc3;
	}
} 
function getAutoOnblur(e,obj,pageName){
	var form = document.autoSubB2BForm;
	if(obj.value ==''){
		if("CustomerAutoSub" == pageName){
			form.toStoreCode.value = '';
			form.toStoreName.value = '';
			form.toStoreNo.value = '';
			form.toSubInv.value = '';
		}
	}else{
		getAutoDetail(obj,pageName);
	}
}
function getAutoKeypress(e,obj,pageName){
	var form = document.autoSubB2BForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("CustomerAutoSub" == pageName){
				form.toStoreCode.value = '';
				form.toStoreName.value = '';
				form.toStoreNo.value = '';
				form.toSubInv.value = '';
			}
		}else{
			getAutoDetail(obj,pageName);
		}
	}
}
function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.autoSubB2BForm;
	
	//prepare parameter
	var param = "";
	if("CustomerAutoSub"==pageName){
		param  ="pageName="+pageName;
		param +="&toStoreCode="+obj.value;
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
	if("CustomerAutoSub" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.toStoreCode.value = retArr[1];
			form.toStoreName.value = retArr[2];
			form.toStoreNo.value= retArr[3];
			form.toSubInv.value = retArr[4];
		}else{
			alert("ไม่พบข้อมูล");
			form.toStoreCode.focus();
			form.toStoreCode.value = '';
			form.toStoreName.value = '';
			form.toStoreNo.value = '';
			form.toSubInv.value = '';
		}
	}
}
function getRefNoInfoBtn(){
	var form = document.autoSubB2BForm;
	getRefInfo(form.refNo.value);
}
function getRefNoInfoKeypress(e,refNo){
	var form = document.autoSubB2BForm;
	if(e != null && e.keyCode == 13){
		if(refNo.value ==''){
			form.refNo.value = '';
			form.fromStoreCode.value = "";
			form.fromStoreName.value = "";
			form.fromSubInv.value = "";
			form.totalQty.value = "";
		}else{
			getRefInfo(refNo.value);
		}
	}
}
function getRefInfo(refNo){
	var returnString = "";
	var form = document.autoSubB2BForm;
	var refTypeObj = document.getElementsByName('bean.refType');
	var refType ="PIC";
	//alert(refTypeObj[0].checked);
	if( refTypeObj[0].checked==true){
		refType = 'WACOAL';
	}
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/autoSubB2B/getRefNoInfoAjax.jsp",
			data : "refNo=" + refNo+"&refType="+refType,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if(returnString !=''){
		var retArr = returnString.split("|");
		form.fromStoreCode.value = retArr[0];
		form.fromStoreName.value = retArr[1];
		form.fromSubInv.value = retArr[2];
		form.totalQty.value = retArr[3];
		form.totalBox.value = retArr[4];
	}else{
		alert("ไม่พบข้อมูล");
		form.refNo.focus();
		form.refNo.value ="";
		form.toStoreCode.value ="";
		form.toStoreName.value = "";
		form.toSubInv.value = "";
		form.toStoreNo.value ="";
		form.totalQty.value = "";
		form.totalBox.value = "";
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
                    <!-- head -->
                    <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0"  width="100%">
								<tr>
                                    <td align="right">  อ้างถึงการโอน	</td>
									<td>	
									<%if( autoSubB2BForm.getBean().getMode().equalsIgnoreCase("new")){ %>	
									    <html:radio property="bean.refType" value="WACOAL">&nbsp;&nbsp; เปิดจากโรงงาน</html:radio>
									    &nbsp;&nbsp;&nbsp;&nbsp;
									    <html:radio property="bean.refType" value="PIC"> &nbsp;&nbsp;เบิกจาก PIC</html:radio>
									<%}else{ %>
									    <html:radio property="bean.refType" value="WACOAL" disabled="true">&nbsp;&nbsp; เปิดจากโรงงาน</html:radio>
									    &nbsp;&nbsp;&nbsp;&nbsp;
									    <html:radio property="bean.refType" value="PIC" disabled="true"> &nbsp;&nbsp;เบิกจาก PIC</html:radio>
									<%} %>
									</td>
								</tr>
								<tr>
                                    <td align="right">อ้างถึง Order Lot No/Request No<font color="red">*</font></td>
									<td>	
									<%if( autoSubB2BForm.getBean().getMode().equalsIgnoreCase("new")){ %>	
										<html:text property="bean.refNo" styleId="refNo" size="20" 
										styleClass="\" autoComplete=\"off"   onkeypress="getRefNoInfoKeypress(event,this)"/>
										  <input type="button" name="x1" value="ดึงข้อมูลเดิม" onclick="getRefNoInfoBtn()" class="newPosBtnLong"/>
									<%}else{ %>
									    <html:text property="bean.refNo" styleId="refNo" size="20" readonly="true" styleClass="disableText \" autoComplete=\"off"/>
									<%} %>
									</td>
								</tr>
								<!-- ***************************** -->
								<tr><td colspan="2" align="center">
								   <fieldset>
								
								   <table align="center" border="0" cellpadding="3" cellspacing="0" width="80%">
								   <tr>
									  <td align="left" colspan="2" width="80%"><b><u>ข้อมูลเดิม</u></b></td>
									</tr>
									<tr>
									  <td align="right" width="20%">รหัสร้านค้าเก่า</td>
									  <td align="left" width="60%">
	                                      <html:text property="bean.fromStoreCode" styleId="fromStoreCode" size="15"  styleClass="disableText \" autoComplete=\"off"/>
										  
										  <html:text property="bean.fromStoreName" styleId="fromStoreName" size="45"  styleClass="disableText" readonly="true"/>
									 </td>
									</tr>
									<tr>
	                                    <td align="right"> Sub Inventory  </td>
										<td>	
									       <html:text property="bean.fromSubInv" styleId="fromSubInv" size="10" styleClass="disableText"  readonly="true"/>
										</td>
									</tr>
									<tr>
	                                    <td align="right"> รวมจำนวนชื้นทั้งหมด </td>
										<td>	
									       <html:text property="bean.totalQty" styleId="totalQty" size="10" styleClass="disableText"  readonly="true"/>
										    &nbsp;&nbsp;&nbsp;
										          จำนวนกล่อง &nbsp;&nbsp;
										  <html:text property="bean.totalBox" styleId="totalBox" size="10" styleClass="disableText"  readonly="true"/>
										</td>
									</tr>
								    </table>
								    </fieldset>
								 </td></tr>
								<!-- ***************************** -->
								<tr><td colspan="2" align="center">
								 <fieldset>
								   <table align="center" border="0" cellpadding="3" cellspacing="0" width="80%">
									   <tr>
										  <td align="left" colspan="2" width="80%"><b><u>เปลี่ยนไปที่ใหม่ คือ</u></b></td>
										</tr>
										<tr>
										  <td align="right" width="20%">รหัสร้านค้าใหม่<font color="red">*</font></td>
										  <td align="left" width="60%">
		                                       <html:text property="bean.toStoreCode" styleId="toStoreCode" size="15"  styleClass="\" autoComplete=\"off"
		                                        onkeypress="getAutoKeypress(event,this,'CustomerAutoSub')"
					                            onblur="getAutoOnblur(event,this,'CustomerAutoSub')"/> 
											   <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerAutoSub')"/>
											  <html:text property="bean.toStoreName" styleId="toStoreName" size="40"  styleClass="disableText" readonly="true"/>
										 </td>
										</tr>
										<tr>
		                                    <td align="right"> Sub Inventory  </td>
											<td>	
										        <html:text property="bean.toSubInv" styleId="toSubInv" size="10" styleClass="disableText"  readonly="true"/>
										        <html:hidden property="bean.toStoreNo" styleId="toStoreNo"/>
											</td>
										</tr>
										<tr>
		                                    <td align="right">ขนส่งโดย<font color="red">*</font> </td>
											<td>		
											    <html:select property="bean.forwarder" styleId="forwarder" >
											      <html:options collection="forwarderList" property="code" labelProperty="desc"/>
											    </html:select>
											</td>
										</tr>
										<tr>
		                                    <td align="right"> จำนวนกล่อง<font color="red">*</font> </td>
											<td>	
										       <html:text property="bean.forwarderBox" styleId="forwarderBox" size="10" styleClass="\" autoComplete=\"off"
										        onkeydown="return inputNum(event);"/>
											</td>
										</tr>
										<tr>
		                                    <td align="right" > เหตุผลในการขอย้าย<font color="red">*</font></td>
											<td>		
										       <html:text property="bean.reason" styleId="reason" size="70" styleClass="\" autoComplete=\"off"/>
											</td>
										</tr>
									</table>
									</fieldset>
								</td></tr>
						   </table>
                    <!-- table data -->
           
           <!-- WAIT NEXT phase -->
                    <c:if test="${autoSubB2BForm.resultsSearch != null && 1==2}">
                   
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th >RMA Order</th><!-- 0 -->
						            <th >CN NO</th><!-- 1 -->
									<th >CN Date</th><!-- 3 -->
									<th >Ref.Inv</th><!-- 3 -->
									<th >Seq</th><!-- 4 -->
									<th >Item</th><!-- 5 -->
									<th >Item Desc</th><!-- 6 -->
									<th >Qty</th><!-- 7 -->
									<th >List Price</th><!-- 7 -->
									<th >Total Amount</th><!-- 7 -->
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
									<td class="td_text_center" width="6%"><%=mc.getRmaOrder()%></td><!-- 1 -->
									<td class="td_text_center" width="6%"><%=mc.getCnNo()%></td><!-- 3 -->
								    <td class="td_text_center" width="6%"><%=mc.getCnDate()%></td><!-- 4 -->
								    <td class="td_text_center" width="6%"><%=mc.getRefInv()%></td><!-- 4 -->
								    <td class="td_text_center" width="6%"><%=mc.getSeq()%></td><!-- 5 -->
									<td class="td_text_center" width="6%"><%=mc.getPensItem()%></td><!-- 6 -->
									<td class="td_text" width="6%"><%=mc.getItemName()%></td><!-- 7 -->
									<td class="td_number" width="6%"><%=mc.getQty()%></td><!-- 8 -->
									<td class="td_number" width="6%"><%=mc.getUnitPrice()%></td><!-- 8 -->
									<td class="td_number" width="6%"><%=mc.getAmount()%></td><!-- 8 -->
								</tr>
							<%} %> 
					</table>
				</c:if>
		        <!-- ************************Result ***************************************************-->
	            <table id="tblProductBtn" align="center" border="0" cellpadding="3" cellspacing="2"  width="65%">
                         <tr><td align="center">
                            <%
                            if(Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN})){
                              if(autoSubB2BForm.getBean().isCanApprove()) { %>
                                <input type="button" name="add" value="Approve to Transfer" onclick="approveAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                            <%} 
                             }%>
                            &nbsp;
                             <%
                             if(Utils.userInRole(user,new String[]{User.ADMIN,User.SALE})){
                               if(autoSubB2BForm.getBean().isCanSave()) { 
                             %>
                                <input type="button" name="add" value="   บันทึก   " onclick="saveAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                            <% }
                              } %>
                             &nbsp;
                            <input type="button" name="clear" value="   Clear   " onclick="clearAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                           
                            &nbsp;
                            <input type="button" name="delete" value="  ปิดหน้าจอ  " onclick="backAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                           
                           
                           </td></tr>
                   </table>
               </div>
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
<jsp:include page="../controlSaveLockScreen.jsp"/>
