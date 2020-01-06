<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.web.batchtask.BatchTaskListBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.web.batchtask.BatchTaskInfo"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.MonitorBean"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="batchTaskForm" class="com.isecinc.pens.web.batchtask.BatchTaskForm" scope="session" />
<%
try{
User user = (User) session.getAttribute("user");
String pageName = Utils.isNull(request.getParameter("pageName"));
String buttonName = "";
BatchTaskInfo taskInfo = batchTaskForm.getTaskInfo();
Map<String,BatchTaskInfo> paramMap = null;
List<BatchTaskInfo> paramList = null;
if(taskInfo != null){
	paramMap = taskInfo.getParamMap();
	paramList = taskInfo.getParamList();
	buttonName = taskInfo.getButtonName();
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>

<!-- Calendar -->
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
#progress {
 width: 500px;   
 border: 1px solid black;
 position: relative;
 padding: 3px;
}
#percent {
 position: absolute;   
 left: 50%;
}
#bar {
 height: 40px;
 background-color: green;
 width: 10%;
}
.errorLine {
   color: red;
}
</style>
 
<%out.println(taskInfo.getValidateScript()); %>

<Script>
    function loadme(){
       	<%if( "submited".equals(request.getAttribute("action"))){%>
       	   startBatch();
       	<%}%>
       	
       	//load calendar parameter date
       	<%if( !paramMap.isEmpty()){
			 Iterator its = paramMap.keySet().iterator();
			 while(its.hasNext()){
		        String key = (String)its.next();
		        BatchTaskInfo task = paramMap.get(key);    
		        if(task.getParamType().equalsIgnoreCase("DATE")){%> 
		           new Epoch('epoch_popup', 'th', document.getElementById('<%=task.getParamName()%>'));
		   <%   }//if
		     }//while
		   }//if 
		   %>
    }
    
	function disableF5(e) {
		if (e.which == 116) e.preventDefault(); 
	}
	//To re-enable f5
	$(document).unbind("keydown", disableF5);
	
	//clear cach
	$.ajaxSetup({cache: false});
	
	/** Start Batch**/
	function runBatch(path) {
		var confirmText = "ยืนยัน Process ";
		if(validate()){
		  if(confirm(confirmText)){
			document.batchTaskForm.action = path + "/jsp/batchTaskAction.do?do=runBatch&action=submited";
			document.batchTaskForm.submit();
			return true;
		  }
		}
		return false; 
	}
	function search(path, type) {
		document.batchTaskForm.action = path + "/jsp/batchTaskAction.do?do=search";
		document.batchTaskForm.submit();
		return true;
	}
	
	function clearForm(path, type) {
		document.batchTaskForm.action = path + "/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName="+document.getElementById("pageName").value;
		document.batchTaskForm.submit();
		return true;
	}
</Script>

	<!-- ProgressBar -->
	<% if( "submited".equals(request.getAttribute("action"))){%>
	   <script type="text/javascript" language="javascript">
	   
	    //To disable f5
	    $(document).bind("keydown", disableF5);
	 
	    $(function() {
			///$("#dialog").dialog({ height: 200,width:650,modal:true });
		   $.blockUI({ message: $('#dialog'), css: {left:'20%', right:'20%' ,top: '40%',height: '20%', width: '60%' } }); 
		}); 
	   
	   var stepMaxUp = 3;
	   var stepMinUp = 1;
	   var stepHaftMinUp = 0.5;
	   var stepDotOne = 0.1;
	   var progressCount = 0;
	   var useTimeMillisecs = 0;
	   var startTime = new Date();

	   /** Onload Window    */
	   var startDate = new Date();
	   function startBatch(){
    	   var status = document.getElementsByName("monitorBean.status")[0]; 
            
    	 // alert(status.value);
    	   if (status.value != "1" && status.value != "-1"){
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
	    		 useTimeMillisecs = (new Date()).getTime()-startTime.getTime();
	    	 }else{ //Success
	    		 
	    		 progressCount = 100;
	    		 useTimeMillisecs = (new Date()).getTime()-startTime.getTime();
	    		 
	    		 $("#percent").html("<b>"+progressCount+" %</b>");
	    		 document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");
	    		 
	    		// $("#progress").hide();
	    		 
	    		 setTimeout(function(){ $("#progress").hide();}, 3000);
	    		 document.getElementsByName("monitorBean.status")[0].value = "0";//reset status
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
	    	$(function(){
	    		var getData = $.ajax({
	    			url: "${pageContext.request.contextPath}/jsp/batchtask/getStatusBatchTaskAjax.jsp",
	    			data : "id=<%=request.getAttribute("id")%>&transaction_count=<%=request.getAttribute("transaction_count")%>&imp=<%=request.getAttribute("import")%>",
	    			success: function(getData){
	    				var returnString = jQuery.trim(getData);
	    				document.getElementsByName("monitorBean.status")[0].value = returnString;
	    			}
	    		}).responseText;   
	    	});
	    	setTimeout("checkStatus()",3000);
	    }
	    
        /** Check Status Recursive **/
	    function checkStatus(){
	    	  var status =  document.getElementsByName("monitorBean.status")[0].value;
	    	 // alert(status);
	    	   if(status == '1'){ //Finish Task
	    		   //Calc Time thred use
	    		   try{
		    		   var endDate = new Date();
		    		   var dif = endDate.getTime() - startDate.getTime();
	
		    		   var Seconds_from_T1_to_T2 = dif / 1000;
		    		   //var Seconds_Between_Dates = Math.abs(Seconds_from_T1_to_T2);
		    		   
		    		   document.getElementsByName("monitorBean.timeInUse")[0].value = Seconds_from_T1_to_T2;//Seconds_Between_Dates; 
		    		   
		    		  // alert( document.getElementsByName("monitorBean.timeInUse")[0].value);
	    		   }catch(e){}
	    		   
	    		   /** Task Success ***/
	    		   updateProgress(status);
	    		   
	    		   //search display
	    		   search('<%=request.getContextPath()%>', 'admin');
                 
	    	   }else { //Task Running
	    		   /** Task Not Success  and Re Check Status**/
		    	   updateProgress(status);
	               window.setTimeout("checkStatusProcess();", 1200);
	           }
	    }
	    </script>
	    <!-- PROGRESS BAR -->
	<% } %>
</head>

<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;"  style="height: 100%;" onload="loadme()">

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
				<jsp:param name="function" value="<%=pageName%>"/>
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
						<html:form action="/jsp/batchTaskAction"  enctype="multipart/form-data">
			
						<jsp:include page="../error.jsp"/>
						
						<!-- Generate Parameter By Task Config -->
					     <table align="center" border="0" cellpadding="3" cellspacing="10" width="100%">
							<%
							if( paramList != null && paramList.size() >0){
								for(int p=0;p<paramList.size();p++){
								     BatchTaskInfo task = paramList.get(p);
							%> 
								<tr>
								<%if(task.getParamType().equalsIgnoreCase("DATE")|| task.getParamType().equalsIgnoreCase("STRING") ){ %>
									<td width ="40%" align="right">
									    <%=task.getParamLabel() %>
									    <%if(task.getParamValid().equalsIgnoreCase("VALID")){%>
									       <font color="red">*</font>
									    <%} %>
									</td>
									<td width ="60%" align="left">
									    <input type="text" name="<%=task.getParamName() %>" id="<%=task.getParamName() %>" 
									     value="<%=task.getParamValue() %>"/>
									</td>
								<%}else if(task.getParamType().indexOf("LIST") != -1){ %>
									<td width ="40%" align="right">
									    <%=task.getParamLabel() %>
									    <%if(task.getParamValid().equalsIgnoreCase("VALID")){%>
									       <font color="red">*</font>
									    <%} %>
									</td>
									<td width ="60%" align="left">
									<%
									String keySessionName = task.getParamType();
									System.out.println("keySessionName:"+keySessionName);
									 if(session.getAttribute(keySessionName) != null){
										 List<BatchTaskListBean> listBox = (List<BatchTaskListBean>)session.getAttribute(keySessionName);
									%>
									    <select name="<%=task.getParamName() %>" id="<%=task.getParamName() %>">
									    
										    <%for(int i=0;i<listBox.size();i++){ 
										    	BatchTaskListBean data = listBox.get(i);
										    %>
									           <option value="<%=data.getValue() %>"><%=data.getDisp() %></option>
									       <%} %>
                                        </select>
									 <%} %>
									</td>
								<%}else if(task.getParamType().equalsIgnoreCase("FROMFILE")){ %>
									<td width ="40%" align="right">
									   <%=task.getParamLabel() %>
									    <%if(task.getParamValid().equalsIgnoreCase("VALID")){%>
									       <font color="red">*</font>
									    <%} %>
									</td>
									<td width ="60%" align="left">
									   <input type="file" name="<%=task.getParamName() %>" 
									     id="<%=task.getParamName() %>"/>
									</td>
								<%} %>
								</tr>
						<% 
						   }//for
						}//if
						%>
						</table>

                        <!-- Generate BUTTON By Task Config-->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						    <tr>
								<td align="center" width ="100%"> &nbsp;</td>
							</tr>
							<tr>
								<td align="center" width ="100%">
								      <input type="button" name ="import" value="<%=buttonName%>" class="newPosBtnLong" onClick="javascript:runBatch('${pageContext.request.contextPath}','sales')">
								       <input type="button" value="ตรวจสอบสถานะ ล่าสุด" class="newPosBtnLong" style="width: 200px;" onClick="javascript:search('${pageContext.request.contextPath}','admin')">
								      <input type="button" value="Clear" class="newPosBtnLong" style="width: 100px;" onClick="javascript:clearForm('${pageContext.request.contextPath}','admin')">
								</td>
							</tr>
						</table>   
							
                        <!-- Batch Task Result--> 
					    <jsp:include page="batchTaskResult.jsp"></jsp:include>
					  
						<!-- Progress Bar -->
						<div id="dialog" title=" กรุณารอสักครู่......">
							<!-- PROGRESS BAR-->
							  <% if( "submited".equals(request.getAttribute("action"))){ %>  
							 <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							    <tr>
									<td align="left" width ="100%">
									  <div style="height:50px;align:center">
									     กรุณารอสักครู่......
									  </div>
									  <div id="progress" style="height:40px;width:100%;">
						                <div id="percent"></div>     
									    <div id="bar"></div>  
						              </div>   
									 </td>
								</tr>
							   </table>   
						     <%} %>			      
						</div>
						<br><br>
						
						<html:hidden property="monitorBean.status"  styleClass="disableText"/>
						<input type="hidden" name="pageName"  id="pageName" value="<%=pageName%>"/>
					
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
<%}catch(Exception e){
	e.printStackTrace();
} %>

</html>