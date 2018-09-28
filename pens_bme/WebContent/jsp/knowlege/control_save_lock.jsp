<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="confFinishForm" class="com.isecinc.pens.web.pick.ConfFinishForm" scope="session" />
<%
 String mode = confFinishForm.getMode();
if(session.getAttribute("typeDispList") == null){
	List<References> billTypeList = new ArrayList();
	References ref = new References("GropCode","GroupCode");
	billTypeList.add(ref);
	ref = new References("Box","Box");
	billTypeList.add(ref);
	session.setAttribute("typeDispList",billTypeList);
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
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>

<script type="text/javascript">
/*** Control Save Lock Screen ***/
function openSaveDialog(){
	 /*  $(document).ready(function() {
	    $("#save-dialog").dialog("open");	    
	 });  */
	
	 //startBatch();
}

   var stepMaxUp = 3;
   var stepMinUp = 1;
   var stepHaftMinUp = 0.5;
   var stepDotOne = 0.1;
   var progressCount = 0;
   function startBatch(){
		/** Init progressbar **/
		$(function() {
			///$("#dialog").dialog({ height: 200,width:650,modal:true });
		   $.blockUI({ message: $('#dialog'), css: {left:'20%', right:'20%' ,top: '40%',height: '20%', width: '60%' } }); 
		}); 
		
   	   var status = document.getElementsByName("status_save")[0]; 
           
   	 // alert(status.value);
   	   if (status.value != "1"){
   		   window.setTimeout("checkStatusProcess();", 800);
   	   }
   	   updateProgress(status.value);
    } 
   
   function updateProgress(status){
    	 if(status != '1' && status != "-1"){ //Running
    		 if(progressCount > 95){
	    	   progressCount += stepDotOne; 
    		 }else if(progressCount > 90){
    		   progressCount += stepHaftMinUp; 
    		 }else if(progressCount > 45){
    		   progressCount += stepMinUp; 
    		 }else{
    		   progressCount += stepMaxUp;
    		 }
    	 }else{ //Success
    		 progressCount = 100;
    		 
    		 $("#percent").html("<b>"+progressCount+" %</b>");
    		 document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");
    		 
    		// $("#progress").hide();
    		 
    		 setTimeout(function(){ $("#progress").hide();}, 3000);
    	 }  
    	  
    	 //var progress = $("#progressbar") .progressbar("option","value");
    	 if (progressCount < 100) {  
	   	      $("#percent").html("<b>"+progressCount+" %</b>");
    		  $("#progress").show();
    		  //set progress count
    		  document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");
	   	 }
    }

    /** Check Status From monitor_id BY AJax */
    function checkStatusProcess(){
    	document.getElementsByName("status_save")[0].value = "0";	
    	setTimeout("checkStatus()",2000);
    }
    
    /** Check Status Recursive **/
    function checkStatus(){
    	  var status =  document.getElementsByName("status_save")[0].value;
    	 // alert(status);
    	   if(status == '1'){ //Finish Task
    		   /** Task Success ***/
    		   updateProgress(status);
    	   }else { //Task Running
    		   /** Task Not Success  and Re Check Status**/
	    	   updateProgress(status);
               window.setTimeout("checkStatusProcess();", 1200);
           }
    }

/* $(function(){
 $('#save-dialog').dialog({
		autoOpen: false,
		modal: true,
	    width: 700,
	    height: 300,
		title:"��س����ѡ������ѧ�ѹ�֡������.........",
		position:'center',
		resizable: false,
		position: ['center',20],
        closeOnEscape: false
	}); 
}); */

/*** Control Save Lock Screen ***/

function loadMe(){
   new Epoch('epoch_popup', 'th', document.getElementById('confirmDate'));
}
function clearForm(path){
	var form = document.confFinishForm;
	form.action = path + "/jsp/confFinishAction.do?do=clear";
	form.submit();
	return true;
}
function cancel(path){
	if(confirm("�׹�ѹ���¡��ԡ��¡�ù��")){
		var form = document.confFinishForm;
		form.action = path + "/jsp/confFinishAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}
function back(path){
	var form = document.confFinishForm;
	form.action = path + "/jsp/confFinishAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}
function viewDisp(path){
	var form = document.confFinishForm;
	form.action = path + "/jsp/confFinishAction.do?do=viewDisp";
	form.submit();
	return true;
}
function printGroupCodeBoxReport(path,requestNo){
	window.open(path + "/jsp/printGroupCodeBoxAction.do?do=prepare&requestNo="+requestNo, "Print GroupCode Box", "width=500,height=400,location=No,resizable=No");
}
function save(path){
	
	var form = document.confFinishForm;
	var requestDate =$('#requestDate').val();
	var confirmDate =$('#confirmDate').val();
	
	 if(confirmDate ==""){
		alert("��سҡ�͡ confirmDate");
		$('#confirmDate').focus();
		return false;
	} 
	 
	if(checkCompareDate(requestDate,confirmDate) ==false){
		alert("�ѹ��� Confirm Date ��ͧ�ҡ���� ������ҡѺ Request Date ");
		$('#confirmDate').focus();
		return false;
	}
	
	if(confirm("�ѹ�ѹ Confirm To Finishing")){
		
		/* open popup save progress lock screen */
		//PopupCenterFullDisableAll(path+"/jsp/pick/control/controlSavePopup.jsp?ACTION_SAVE=submited","Control Save");
		
		//open Control Save Dialog
		openSaveDialog();
		
		form.action = path + "/jsp/confFinishAction.do?do=save";
		form.submit();
		return true;
	}
	return false;
}

function checkCompareDate(DateFrom, DateTo){
	if(DateFrom=='' || DateTo==''){return true;}
	DateFrom = DateFrom.split("/");
	starttime = new Date(DateFrom[2],DateFrom[1]-1,DateFrom[0]);

	DateTo = DateTo.split("/");
	endtime = new Date(DateTo[2],DateTo[1]-1,DateTo[0]);
	if((endtime-starttime) < 0){
		return false;
	}else{
		return true;
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
				<jsp:param name="function" value="confFinish"/>
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
						<html:form action="/jsp/confFinishAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Request Date</td>
									<td>					
										<html:text property="bean.requestDate" styleId="requestDate" size="20" styleClass="disableText" readonly="true"/>
									</td>
									<td> 
									    Request No <html:text property="bean.requestNo" styleId="requestNo" size="20" styleClass="disableText"/>
									</td>
									<td> 
									    Confirm Date
									      <c:if test="${confFinishForm.bean.canEdit == true}">
									          <font color="red">*</font><html:text property="bean.confirmDate" styleId="confirmDate" size="20" styleClass=""/>
									      </c:if>
									     <c:if test="${confFinishForm.bean.canEdit == false}">
									         <html:text property="bean.confirmDate" styleId="confirmDate" size="20" styleClass="disableText"/>
									      </c:if>
									</td>
									<td nowrap>					
										ʶҹ�   
									  <html:text property="bean.statusDesc" styleId="status" size="15" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td colspan="3">����ʴ������� 
                                       <html:select property="bean.typeDisp">
											<html:options collection="typeDispList" property="key" labelProperty="name"/>
									    </html:select>
									    
                                    
                                        <a href="javascript:viewDisp('${pageContext.request.contextPath}')">
										  <input type="button" value="   �ʴ�   " class="newPosBtnLong">
										</a>
                                    </td>
                                    <td align="left">
                                        Warehouse
										      <html:select property="bean.wareHouse" styleId="wareHouse" disabled="true">
												<html:options collection="wareHouseList2" property="key" labelProperty="name"/>
										    </html:select>
                                    </td>
                                </tr>
						   </table>
						   

				 <!-- Table Data -->
				<c:if test="${confFinishForm.results != null}">
		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width="80%">
						    <tr>
								<th >�ӴѺ���</th>
								<c:choose>
									<c:when test="${confFinishForm.bean.typeDisp == 'Box'}">
										<th >Box </th>
									</c:when>
									<c:otherwise>
										<th >Group Code</th>
									</c:otherwise>
							      </c:choose>  
								<th >Qty</th>
							</tr>
							<c:forEach var="results" items="${confFinishForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
									<tr class="<c:out value='${tabclass}'/>">
										
										<td class="td_text_center" width="10%"> ${results.no}</td>
										
										 <c:choose>
											<c:when test="${confFinishForm.bean.typeDisp == 'Box'}">
												<td class="td_text_center" width="15%"> ${results.boxNo}</td>
											</c:when>
											<c:otherwise>
												<td class="td_text_center" width="15%"> ${results.groupCode}</td>
											</c:otherwise>
									      </c:choose>      
										
										<td class="td_text_right" width="10%">
										   ${results.qty}
										   <input type="hidden" name="qty" value ="${results.qty}" size="20" readonly class="disableText"/>
										</td>
									</tr>
							  </c:forEach>
					</table>
					<br/>
						<div align="left"><b>
							 ����ӹǹ���ͧ      &nbsp;&nbsp;:&nbsp;  <html:text property="bean.totalBox" styleId="totalBox" size="10" styleClass="disableText"/>
							 ���ͧ
						   </b>
						</div><br/>
						 <div align="left"><b>
							 ����ӹǹ��� &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp; <html:text property="bean.totalQty" styleId="totalQty" size="10" styleClass="disableText"/> ���
						    </b>
						</div>
						
				</c:if>
					   <!-- Table Data -->
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr><td align="left">
						     <c:if test="${confFinishForm.bean.canPrint == true}">
								<a href="javascript:printGroupCodeBoxReport('${pageContext.request.contextPath}','${confFinishForm.bean.requestNo}')">
								  <input type="button" value=" �����㺻С��ͧ " class="newPosBtnLong"> 
								 </a>
							 </c:if>	
							 <c:if test="${confFinishForm.bean.canEdit == true}">
								<a href="javascript:save('${pageContext.request.contextPath}')">
								  <input type="button" value="    �ѹ�֡      " class="newPosBtnLong"> 
								 </a>
							 </c:if>
							<a href="javascript:back('${pageContext.request.contextPath}','','add')">
							  <input type="button" value="   �Դ˹�Ҩ�   " class="newPosBtnLong">
							</a>
						   </td></tr>
						</table>
					</div>
					<!-- ************************Result ***************************************************-->
					
					<!-- hidden field -->
					<input type="hidden" id="status_save" name="status_save"/>
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
<!-- <div id="save-dialog">
     <h1>���ѧ�ѹ�֡������ ��س����ѡ����</h1>
</div> -->
<!-- Progress Bar -->
<div id="dialog" style="display: none">
<!-- PROGRESS BAR-->
 <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
    <tr>
		<td align="left" width ="100%">
		  <div style="height:50px;align:center">
		     <b>��س����ѡ���� ���ѧ�ѹ�֡������ ...... <br/><font color="red">!��س����ҡ����� back ����  Refresh </font> </b>
		  </div>
		  <div id="progress" style="height:40px;width:100%;">
               <div id="percent"></div>     
		       <div id="bar"></div>  
          </div>   
		 </td>
	</tr>
   </table>   	      
</div>
