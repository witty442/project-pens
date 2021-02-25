<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!-- Control Save Lock Screen No Jquery-->
<style>

/* Add Animation */
@-webkit-keyframes animatetop {
  from {top:-300px; opacity:0} 
  to {top:0; opacity:1}
}

@keyframes animatetop {
  from {top:-300px; opacity:0}
  to {top:0; opacity:1}
}

/* The Close Button */
.close {
  color: white;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: #000;
  text-decoration: none;
  cursor: pointer;
}

</style>

<script>
<!--Progress Bar -->
var progressCount = 0;//CountPercent
function startControlSaveLockScreenProgressBar(){
	//To disable f5
	// $(document).bind("keydown", disableF5);
	
    /** Init progressbar **/
    progressCount =1;
    document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");
    
    window.setTimeout("checkStatus();", 500);
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
 		 
 		// $("#percent").html("<b>"+progressCount+" %</b>");
 		document.getElementById('percent').html = "<b>"+progressCount+" %</b>";
 		
 		document.getElementById('bar').setAttribute("style"," height: 40px;background-color: green;width:"+progressCount+"%");
 		 
 	 }  
 	  
 	 //var progress = $("#progressbar") .progressbar("option","value");
 	 if (progressCount < 100) {  
 		  progressCount = Math.round(progressCount * 100) / 100;
 	
	   	  //$("#percent").html("<b>"+progressCount+" %</b>");
 		 // $("#progress").show();
 		  
 		 document.getElementById('percent').html = "<b>"+progressCount+" %</b>";
 		 document.getElementById('progress').style.visibility = 'visible';
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
 
</script>
<!-- The Modal -->
<div id="myModal" class="modal">

  <!-- Modal content -->
  <div class="modal-content">
    <div class="modal-header">
      <p>...</p>
      <p>...</p>
       <b><font color="black" size="3">กรุณารอสักครู่ กำลังทำรายการ  &nbsp;</font></b>
    </div>
    <div class="modal-body">
      <p><font color="red" size="2"><b>!!! กรุณาอย่ากดปุ่ม Back หรือ  Reload เพราะอาจทำให้ข้อมูลผิดพลาดได้ </b></font></p>
       <div id="progress" style="height:50px;width:100%;align:center;">
            <div id="percent" style="align:center"></div>     
		    <div id="bar"></div>  
       </div>  
       <p>&nbsp;&nbsp;</p>
       <p>&nbsp;&nbsp;</p>
    </div>
  </div>
</div>

<script>
//Get the modal
var modal = document.getElementById("myModal");

function startControlSaveLockScreen(){
	//Get the modal
	var modal = document.getElementById("myModal");
	modal.style.display = "block";
	
	//init progress bar
	startControlSaveLockScreenProgressBar();
}

// Get the button that opens the modal
var btn = document.getElementById("save");

// Get the <span> element that closes the modal
/* var span = document.getElementsByClassName("close")[0];
 */
 
// When the user clicks on <span> (x), close the modal
/* span.onclick = function() {
  modal.style.display = "none";
} */

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == modal) {
   // modal.style.display = "none";
  }
}
</script>