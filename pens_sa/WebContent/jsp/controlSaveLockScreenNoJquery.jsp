<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!-- Control Save Lock Screen No Jquery-->
<style>

/* The Modal (background) */
.modal {
  display: none; /* Hidden by default */
  position: fixed; /* Stay in place */
  z-index: 1; /* Sit on top */
  padding-top: 100px; /* Location of the box */
  left: 0;
  top: 0;
  width: 100%; /* Full width */
  height: 100%; /* Full height */
  overflow: auto; /* Enable scroll if needed */
  background-color: rgb(0,0,0); /* Fallback color */
  background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
}

/* Modal Content */
.modal-content {
  position: relative;
  background-color: #fefefe;
  margin: auto;
  padding: 0;
  border: 1px solid #888;
  width: 80%;
  box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19);
  -webkit-animation-name: animatetop;
  -webkit-animation-duration: 0.4s;
  animation-name: animatetop;
  animation-duration: 0.4s
}

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

.modal-header {
  padding: 2px 16px;
  background-color: #fefefe;
  color: white;
}
.modal-body {padding: 2px 16px;}

</style>

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
      <p>............................</p>
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