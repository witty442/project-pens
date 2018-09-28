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

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />

<style type="text/css">
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">

  	<tr id="framerow">
    	<td >
   
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
						<html:form action="/jsp/tempAction">

						<!-- Progress Bar -->
						<div id="dialog" title=" กรุณารอสักครู่......">
							<!-- PROGRESS BAR-->
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
						</div>
						<br><br>
						
					actionSave:<input type="text" name="actionSave"  id="actionSave" value="<%=Utils.isNull(session.getAttribute("ACTION_SAVE"))%>"/>
					
					
					   <!-- hidden field -->
					   </html:form>
					   <!-- BODY -->
					
				</tr>
    		</table>
    	</td> </tr>
  </table>
  
<script type="text/javascript">

window.onbeforeunload = confirmExit;
function confirmExit() {
    return "XX You have attempted to leave this page. Are you sure?";
}

function loadMe(){
	<% if( "submited".equals(Utils.isNull(request.getParameter("ACTION_SAVE")))){%>
	   startBatch();
	<%}%>
}

function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
}

<!-- ProgressBar -->
<% if( "submited".equals(Utils.isNull(request.getParameter("ACTION_SAVE")))){%>

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

   /** Start Onload Window   */
   function startBatch(){
	   var actionSave = document.getElementsByName("actionSave")[0]; 
        
	 // alert("startBatch activeSave["+actionSave.value+"]");
	   if (actionSave.value != "success" ||  actionSave==null){
		   //alert("checkStatusProcess");
		   setTimeout(function(){checkStatusProcess();}, 1500);
		  
	   }
	  //Update Percent ProgressBar 
	  setTimeout(function(){updateProgress(actionSave.value);}, 200);
   } 
   
   function updateProgress(actionSave){
	   //alert("actionSave:"+actionSave);
    	 if(actionSave != 'success' ){ //Running
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
    		// alert("success actionSave:"+actionSave);
    		 progressCount = 100;
    		 
    		 $("#percent").html("<b>"+progressCount+" %</b>");
    		 document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");

    		//clear session ACTION_SAVE
  		     clearSessionActionSave();
    		 
    		//Close popup
  		     setTimeout(function(){ window.close();}, 1500);
    	 }  
    	  
    	 //var progress = $("#progressbar") .progressbar("option","value");
    	 if (progressCount < 100) { 
    		// alert("progressCount < 100")
	   	      $("#percent").html("<b>"+progressCount+" %</b>");
    		  $("#progress").show();
    		  //set progress count
    		  document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");
	   	 }
    }

    /** Check ActionSave From session active_save */
    function checkStatusProcess(){
    	$(function(){
    		var getData = $.ajax({
    			url: "${pageContext.request.contextPath}/jsp/pick/ajax/getActionSaveAjax.jsp",
    			data : "",
    			success: function(getData){
    				var returnString = jQuery.trim(getData);
    				document.getElementsByName("actionSave")[0].value = returnString;
    			}
    		}).responseText;   
    	});
    	setTimeout("checkStatus()",2000);
    }
    
    /** Check Status Recursive **/
    function checkStatus(){
    	  var actionSave =  document.getElementsByName("actionSave")[0].value;
    	 // alert("checkStatus:"+status);
    	   if(actionSave == 'success'){ //Finish Task
    		   /** Task Success ***/
    		   updateProgress(actionSave);
    	   }else { 
    		   /** Task Not Success  and Re Check Status**/
	    	   updateProgress(actionSave);
               window.setTimeout("checkStatusProcess();", 1200);
           }
    }
    /** Check Status From session active_save */
    function clearSessionActionSave(){
    	$(function(){
    		var getData = $.ajax({
    			url: "${pageContext.request.contextPath}/jsp/pick/ajax/clearActionSaveAjax.jsp",
    			data : "",
    			success: function(getData){
    				var returnString = jQuery.trim(getData);
    				document.getElementsByName("actionSave")[0].value = returnString;
    			}
    		}).responseText;   
    	});
    	setTimeout("checkStatus()",3000);
    }
  <!-- PROGRESS BAR -->
<% } %>

</script>
</body>
</html>