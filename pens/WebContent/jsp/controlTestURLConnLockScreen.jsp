<%@page import="com.isecinc.pens.process.testconn.TestURLConnection"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!-- Control Save Lock Screen -->
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script> --%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript">

//Limit check TimeOut fro try connect
var timeOutLimit = 28000 ; //1 minute
var countTimeOut = 0;

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
    			 if("stockVanPDReport"==document.getElementById("nextActionName").value){
    			   setTimeout($.unblockUI, 100); 
    			   
    		       linkSub();
    			 }else  if("synchronizeFromOracle"==document.getElementById("nextActionName").value){
    				setTimeout($.unblockUI, 100); 
    				 
    			    synchronizeFromOracleModel(document.getElementById("path").value,"sales");
    			 
    			 }else  if("synchronizeToOracle"==document.getElementById("nextActionName").value){
     				setTimeout($.unblockUI, 100); 
     				 
     			    synchronizeToOracleModel(document.getElementById("path").value,"sales");
     			 }
    		 }else{
    			 setTimeout($.unblockUI, 100); 
    			 ///alert("ไม่สามารถเชื่อมต่อ VPN ได้ โปรดตรวจสอบ VPN ");
    			 
    			//Show Dialog cannot connect Server 
    			 $(function() {
    				$("#dialog_warn").dialog({ height: 250,width:500,modal:true });
    			 });
    			
    			//close auto 1 minute = 60000
    			 //setTimeout(function(){ $("#dialog_warn").dialog('close');},30000);
    			
    			 //setTimeout(function(){ $("#dialog_warn").dialog('destroy');},10000);
    		 }
    	 
    	 }//if  
    }
    /** Check Status Recursive **/
    function checkThreadStatus(){
    	var status =  document.getElementById("threadStatus").value;
    	if(status == 'success' || status == 'fail'){ //Finish Task
    		
    		/** Clear Thread status in table c_thread_control **/
	   		clearThreadStatus();
    	
    	   /** Task Success || Fail***/
 		   updateThreadProgress(status);
    	}else{
    		
    		if(countTimeOut >= timeOutLimit ){
    			document.getElementById("threadStatus").value = 'fail';
    			
    			/** Clear Thread status in table c_thread_control **/
    	   		clearThreadStatus();
    			
    			updateThreadProgress('fail'); 
    			countTimeOut = 0;
    		}else{
	    		/** Task Not Success  and Re Check Status**/
	    	    updateThreadProgress(status); 
	            window.setTimeout("checkThreadStatusProcess();", 1500);
	            
	            /** countTimeOut **/
	            countTimeOut += 1500;
    		}
    	}
    }
   
    /** Check Status From monitor_id BY AJax */
    function checkThreadStatusProcess(){
    	$(function(){
    		var getData = $.ajax({
    			url: document.getElementById("path").value+"/jsp/ajax/checkThreadStatusAjax.jsp",
    			data : "threadName=<%=TestURLConnection.THREAD_PENSA%>",
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
    function clearThreadStatus(){
    	/** Clear Thread status in table c_thread_control **/
		 $(function(){
    		var getData = $.ajax({
    			url: document.getElementById("path").value+"/jsp/ajax/clearThreadStatusAjax.jsp",
    			data : "threadName=<%=TestURLConnection.THREAD_PENSA%>",
    			success: function(getData){
    				var returnString = jQuery.trim(getData);
    				document.getElementById("threadStatus").value = returnString;
    			}
    		}).responseText;   
	    });
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
