
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!-- Control Save Lock Screen -->
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script> --%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript">

/*** Control Test URL Conn Lock Screen ***/
   function startControlTestURLConnLockScreen(nextActionName){
	   document.getElementById("nextActionName").value =nextActionName;
	 
		/** Init progressbar **/
		$(function() {
			// update the block message 
            $.blockUI({ message: "<h2>กำลังตรวจสอบการเชื่อมต่อส่วนกลาง ผ่าน VPN     กรุณารอสักครู่......</h2>" }); 
		}); 
		
       window.setTimeout("checkThreadStatus();", 800);
    } 
   
   function updateThreadProgress(status){
	   if(status != 'success' && status != "fail"){ //Running
    		
    	 }else{ //Success or fail
    		 
    		 //submit nextFunction
    		 if(status == 'success' ){
    			 //alert("nextActionName:"+document.getElementById("nextActionName").value);
    			 setTimeout($.unblockUI, 100); 
     			 synchronizeToOracleModel(document.getElementById("path").value,"sales");
     			 
    		 }else{
    			 setTimeout($.unblockUI, 100); 
    			
    			//Show Dialog cannot connect Server 
    			 $(function() {
    				$("#dialog_warn").dialog({ height: 250,width:500,modal:true });
    			 });
    			//close auto 1 minute = 60000
    			 //setTimeout(function(){ $("#dialog_warn").dialog('close');},30000);
    			
    			 setTimeout(function(){ $("#dialog_warn").dialog('destroy');},10000);
    		 }
    	 
    	    /** Clear Thread status in table c_thread_control **/
    		 $(function(){
    	    		var getData = $.ajax({
    	    			url: document.getElementById("path").value+"/jsp/ajax/clearThreadStatusAjax.jsp",
    	    			data : "threadName=",
    	    			success: function(getData){
    	    				var returnString = jQuery.trim(getData);
    	    				document.getElementById("threadStatus").value = returnString;
    	    			}
    	    		}).responseText;   
    	    });
    	 }//if  
    }
    /** Check Status Recursive **/
    function checkThreadStatus(){
    	var status =  document.getElementById("threadStatus").value;
    	if(status == 'success' || status == 'fail'){ //Finish Task
    		
    	   /** Task Success || Fail***/
 		   updateThreadProgress(status);
    	}else{
    		/** Task Not Success  and Re Check Status**/
    	    updateThreadProgress(status); 
            window.setTimeout("checkThreadStatusProcess();", 1000);
    	}
    }
   
    /** Check Status From monitor_id BY AJax */
    function checkThreadStatusProcess(){
    	$(function(){
    		var getData = $.ajax({
    			url: document.getElementById("path").value+"/jsp/ajax/checkThreadStatusAjax.jsp",
    			data : "threadName=",
    			success: function(getData){
    				var returnString = jQuery.trim(getData);
    				document.getElementById("threadStatus").value = returnString;
    			}
    		}).responseText;   
    	});
    	setTimeout("checkThreadStatus()",1000);
    }

    function closeDialog(){
    	//$("#dialog_warn").dialog('close');
    	 setTimeout(function(){ $("#dialog_warn").dialog('destroy');},100);
    }
/*** Control Save Lock Screen ***/
</script>
<!-- Hidden field -->
ThreadStatus:<input type="hidden" name="threadStatus" id="threadStatus" />
path:<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
nextActionName:<input type="hidden" name="nextActionName" id="nextActionName"/>

<div id="dialog_warn" title="คำเตือน" style="display:none">
    <p align="center"><font color="red" size="3"><b> ไม่สามารถเชื่อมต่อส่วนกลางได้ กรุณาตรวจสอบ การเชื่อมต่อ VPN อีกครั้ง</b></font></p>
    <br/>
    <p align="center">
    <a href="javascript:closeDialog();">
      <input type="submit" onclick="close();" value="   ปิดหน้าจอ   " class="newPosBtnLong" style="width: 250px;"/>
    </a></p>
</div>
