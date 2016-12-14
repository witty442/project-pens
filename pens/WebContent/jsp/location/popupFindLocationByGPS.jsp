<%@page import="com.pens.gps.GPSReader"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
  /** Clear Temp Data **/
  new GPSReader().boforeStartClearData();
 /** Start Thread **/
  GPSReader.startProcess();
%>
<html>
  <head>
    <title>ค้นหาตำแหน่ง</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
 
    <!-- ProgressBar -->

	   <script type="text/javascript" language="javascript">
	   var useTimeMillisecs = 0;
	   var startTime = new Date();
	   
	   function update(status){
	    	 if(status != '1' && status != "-1"){ //Running
	    		 useTimeMillisecs = (new Date()).getTime()-startTime.getTime();
	    	     
	    	 }else{ //Success
	    		 useTimeMillisecs = (new Date()).getTime()-startTime.getTime();
	    		 document.getElementById("div_loading").style.display = 'none';          // Show
	    	 }  
	    }
	
	   /** Onload Window    */
	   var startDate = new Date();
	   
	   window.onload=function(){
    	   var status = document.getElementsByName("status")[0]; 
    	   if (status.value != "1" && status.value != "-1"){
    		   window.setTimeout("checkStatusProcess();", 800);
    	   }
    	   update(status.value);
	    } 

	    /** Check Status From monitor_id BY AJax */
	    function checkStatusProcess(){
	    	$(function(){
	    		var getData = $.ajax({
	    			url: "${pageContext.request.contextPath}/jsp/ajax/getStatusLocationAjax.jsp",
	    			data : "",
	    			success: function(getData){
	    				var returnString = jQuery.trim(getData);
	    				document.getElementsByName("status")[0].value = returnString;
	    			}
	    		}).responseText;   
	    	});
	    	setTimeout("checkStatus()",3000);
	    }
	    
        /** Check Status Recursive **/
	    function checkStatus(){
	    	  var status =  document.getElementsByName("status")[0].value;
	    	   if(status == '1'){ //Finish Task
	    		   
	    		   //Calc Time thred use
	    		   try{
		    		   var endDate = new Date();
		    		   var dif = endDate.getTime() - startDate.getTime();
	
		    		   var Seconds_from_T1_to_T2 = dif / 1000;
		    		   var Seconds_Between_Dates = Math.abs(Seconds_from_T1_to_T2);
		    		   
		    		   document.getElementsByName("timeInUse")[0].value = Seconds_from_T1_to_T2;//Seconds_Between_Dates; 
		    		   
		    		  // alert( document.getElementsByName("monitorBean.timeInUse")[0].value);
		    		   
	    		   }catch(e){}
	    		   
	    		   /** Task Success ***/
	    		   update(status);
	    		   
	    		   //search display
	    		   var getData = $.ajax({
		    			url: "${pageContext.request.contextPath}/jsp/ajax/getDataLocationAjax.jsp",
		    			data : '',
		    			cache: false,
		    			async: false,
		    			success: function(getData){
		    				var returnString = jQuery.trim(getData);
		    				
		    				document.getElementById("lat").value = returnString.split('|')[0];
		    				document.getElementById("lng").value = returnString.split('|')[1];
		    				
		    			    window.opener.setLocationData(returnString.split('|')[0],returnString.split('|')[1]);
		    			    window.setTimeout("window.close();", 1000);
		    			}
		    		}).responseText;   

	    	   }else { //Task Running
	    		   /** Task Not Success  and Re Check Status**/
		    	   update(status);
	               window.setTimeout("checkStatusProcess();", 600);
	           }
	    }
	    </script>

<!-- PROGRESS BAR -->
 </head>
  <body>
    <form>
       <div id="div_loading" align="center">
           <b><span id="disp">...................กำลังค้นหาตำแหน่ง กรุณาโปรดรอสักครู่ .................................</span></b><br/>
           
          <img src="${pageContext.request.contextPath}/images/loading.gif" width="100" height="100" />
           <input type="hidden" name="status" id ="status" />
	       <input type="hidden" id="location" />
		   <input type="hidden" id="lat" readonly size="15" class="disableText"/>
		   <input type="hidden" id="lng" readonly size="15" class="disableText" />
		   <input type="hidden" name="timeInUse"/>
       </div>
    </form>
  </body>
</html>