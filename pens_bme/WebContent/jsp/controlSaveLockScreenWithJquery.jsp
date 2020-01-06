<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!-- Control Save Lock Screen -->
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript">
/** prevent Back button **/
function preventback(){
	window.history.forward();
}
setTimeout("preventback()",0);
//window.onload=function(){null};

/** prevent alert user press refresh and back alert **/
/* window.onbeforeunload = function() { 
return "กดปุ่มนี้ ข้อมูลอาจเกิดความเสียหาย ต้องกดใช่หรือไม่."; 
};   */


/*** Control Save Lock Screen ***/
   var progressCount = 0;//CountPercent
   function startControlSaveLockScreen(){
	   //To disable f5
	    $(document).bind("keydown", disableF5);
	   
		/** Init progressbar **/
		$(function() {
			///$("#dialog").dialog({ height: 200,width:650,modal:true });
		   $.blockUI({ message: $('#dialog'), css: {left:'20%', right:'20%' ,top: '20%',height: '25%', width: '60%' } }); 
		}); 
		
       window.setTimeout("checkStatus();", 800);
   	   updateProgress(status.value);
    } 
   
   function updateProgress(status){
    	 if(status != '1' && status != "-1"){ //Running
    		 if(progressCount > 90){
	    	   progressCount += 0.1; 
    		 }else if(progressCount > 80){
    		   progressCount += 0.5; 
    		 }else if(progressCount > 50){
    		   progressCount += 1; 
    		 }else{
    		   progressCount += 3;
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
    		 progressCount = Math.round(progressCount * 100) / 100;
    		 
	   	      $("#percent").html("<b>"+progressCount+" %</b>");
    		  $("#progress").show();
    		  //set progress count
    		  document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");
	   	 }
    }
    /** Check Status Recursive **/
    function checkStatus(){
    	var status =  "0";//running
    	/** Task Not Success  and Re Check Status**/
	    updateProgress(status);
        window.setTimeout("checkStatus();", 2000);
    }
    function disableF5(e) {
		if (e.which == 116) e.preventDefault(); 
	}
/*** Control Save Lock Screen ***/
</script>

<!-- Progress Bar -->
<div id="dialog" style="display: none">
 <table style="align:center" border="0" cellpadding="1" cellspacing="0" width="100%">
    <tr>
		<td align="left" width ="100%">
		  <div style="height:50px;align:center">
		     <h3><b>กรุณารอสักครู่ กำลังบันทึกข้อมูล  &nbsp;
		     <font color="red"><u>!!! กรุณาอย่ากดปุ่ม Back หรือ  Reload เพราะอาจทำให้ข้อมูลผิดพลาดได้</u></font> </b></h3>
		  </div>
		  <div id="progress" style="height:50px;width:100%;align:center;">
               <div id="percent" style="align:center"></div>     
		       <div id="bar"></div>  
          </div>   
		 </td>
	</tr>
   </table>   	      
</div>