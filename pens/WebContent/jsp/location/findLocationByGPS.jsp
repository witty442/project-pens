<%@page import="com.pens.gps.GPSReader"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
 //String lat = Utils.isNull(request.getParameter("lat"));
 //String lng = Utils.isNull(request.getParameter("lng"));
 String customerName = "ร้าน "+new String(Utils.isNull(request.getParameter("customerName")).getBytes("ISO8859_1"), "TIS-620"); 
 if(session.getAttribute("customerName") != null){
	 customerName = (String)session.getAttribute("customerName");
 }else{
	 session.setAttribute("customerName",customerName);
 }

 String runNew = Utils.isNull(request.getParameter("run"));
 String lat = Utils.isNull(request.getParameter("lat")).equals("")?"0": Utils.isNull(request.getParameter("lat"));
 String lng = Utils.isNull(request.getParameter("lng")).equals("")?"0": Utils.isNull(request.getParameter("lng"));
  lat = Utils.isNull(request.getParameter("lat")).equals("null")?"0": Utils.isNull(request.getParameter("lat"));
  lng = Utils.isNull(request.getParameter("lng")).equals("null")?"0": Utils.isNull(request.getParameter("lng"));
 
 String location = Utils.isNull(request.getParameter("location")).equals("")?"0": Utils.isNull(request.getParameter("location"));
 String action = Utils.isNull(request.getParameter("action"));
 String timeInUse = Utils.isNull(request.getParameter("timeInUse"));
 
boolean isInternetConn = true;
if("new".equalsIgnoreCase(runNew)){
	 isInternetConn = Utils.isInternetConnect();
	 session.setAttribute("isInternetConn",isInternetConn);
}else{
	 if(session.getAttribute("isInternetConn") == null){
		 isInternetConn = Utils.isInternetConnect();
		 session.setAttribute("isInternetConn",isInternetConn);
	 }else{
		 isInternetConn =  (Boolean)session.getAttribute("isInternetConn");
	 }
}
 System.out.println("isInternetConn:"+isInternetConn);
 System.out.println("customerName:"+customerName);
 System.out.println("action:"+action);
 System.out.println("location:"+location);
 System.out.println("lat:"+lat);
 System.out.println("lng:"+lng);
%>
<html>
  <head>
    <title>ค้นหาตำแหน่ง <%=customerName %> V2.0</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js"></script>
    <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" type="text/css" />
    <style>
      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
      }
      #map {
        height: 100%;
      }
      /** newPosBtnLong **/
.newPosBtnLong {
	-moz-box-shadow: 3px 4px 0px 0px #899599;
	-webkit-box-shadow: 3px 4px 0px 0px #899599;
	box-shadow: 3px 4px 0px 0px #899599;
	background:-webkit-gradient(linear, left top, left bottom, color-stop(0.05, #ededed), color-stop(1, #bab1ba));
	background:-moz-linear-gradient(top, #ededed 5%, #bab1ba 100%);
	background:-webkit-linear-gradient(top, #ededed 5%, #bab1ba 100%);
	background:-o-linear-gradient(top, #ededed 5%, #bab1ba 100%);
	background:-ms-linear-gradient(top, #ededed 5%, #bab1ba 100%);
	background:linear-gradient(to bottom, #ededed 5%, #bab1ba 100%);
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#ededed', endColorstr='#bab1ba',GradientType=0);
	background-color:#ededed;
	border:1px solid #d6bcd6;
	display:inline-block;
	cursor:pointer;
	color:#3a8a9e;
	font-family:arial;
	font-size:16px;
	font-weight:bold;
	padding:2px 5px;
	text-decoration:none;
	text-shadow:0px 1px 0px #e1e2ed;
}
.newPosBtnLong:hover {
	background:-webkit-gradient(linear, left top, left bottom, color-stop(0.05, #bab1ba), color-stop(1, #ededed));
	background:-moz-linear-gradient(top, #bab1ba 5%, #ededed 100%);
	background:-webkit-linear-gradient(top, #bab1ba 5%, #ededed 100%);
	background:-o-linear-gradient(top, #bab1ba 5%, #ededed 100%);
	background:-ms-linear-gradient(top, #bab1ba 5%, #ededed 100%);
	background:linear-gradient(to bottom, #bab1ba 5%, #ededed 100%);
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#bab1ba', endColorstr='#ededed',GradientType=0);
	background-color:#bab1ba;
}
.newPosBtnLong:active {
	position:relative;
	top:1px;
}
.disableText {
	background-color: #DCDCDC;
	border: 1px solid;
	border-color: #BEBEBE;
	color: #000000;
}
.result {
	background-color: #D3D3D3;
	COLOR: #FFFFFF;
	text-align: center;
	width: 98%;
	min-height: 50px;
	max-height: 700px;
	overflow: scroll;
}
TABLE.result TH {
	background-color: #03A4B6;
	text-align: center;
	height: 30px;
}

TABLE.result TD {
	color: #000000;
	height: 25px;
	font-size: 13px;
}
 </style>
 </head>
 <body>
   <script>
	   
      function initMap(){
    	 // maxWindow(); 
    	 // alert("lat:"+document.getElementById("lat").value);
    	  <%if("foundGPS".equalsIgnoreCase(action)){ %>
    		  document.getElementById("lat").value ='<%=lat%>';
     	      document.getElementById("lng").value ='<%=lng%>';
     	      document.getElementById("location").value ='<%=location%>';
     	     // alert(document.getElementById("location").value);
    	      <%if(isInternetConn){ %>
    	         initMapModelByGPS();
    	      <%}%>
    	  <%}else {%>
    	    /** Not found GPS by device and get current Location By Google map */
    	     <%if(!"new".equalsIgnoreCase(runNew)){%>
    	        <%if(isInternetConn){ %>
    	          initMapModel();
    	        <%}%>
    	     <%}%>
    	<%}%>
      }
      
      function initMapModelByGPS() {
     	 var timeoutCount = 10000; //default
     	 navigator.geolocation.getCurrentPosition(foundLocationByGPS,noLocation,{timeout:timeoutCount});
 	  }
     
      function foundLocationByGPS(position) {
    		//Show Map
    	  <%if( !Utils.isNull(lat).equals("")) {%>
 	   	     var myLatLng = {lat:<%=lat%>, lng:<%=lng%> };
 	   	  <%}else{%>
 	   	     var myLatLng = {lat:0, lng:0 };
 	   	 <%}%>
 		  var map = new google.maps.Map(document.getElementById('map'), {
 		    zoom: 16,
 		    center: myLatLng
 		  });
 		   var marker = new google.maps.Marker({
 		    position: myLatLng,
 		    map: map,
 		    title: '<%=customerName%>'
 		  }); 
 		  
 		  marker.addListener('click', function() {
 			   map.setZoom(8);
 			   map.setCenter(marker.getPosition());
 	       });
      }
      
      // Note: This example requires that you consent to location sharing when
      // prompted by your browser. If you see the error "The Geolocation service
      // failed.", it means you probably did not give permission for the browser to
      // locate you.
     function initMapModel() {
    	  //Check internet connection
    	  if(!<%=isInternetConn%>){
    		  alert("กรุณาเชื่อมต่อ อินเตอร์เน็ต");
    		  return false;
    	  }
    	  
    	 var count = 1;
    	 var timeoutCount = 10000; //default
         var stepTimecount1 = 2000;
         var stepTimecount2 = 4000;
         var stepTimecount3 = 6000;
         
    	 if(document.getElementById("count").value==''){
    		 count = 1;
    	 }else{
    		 count = parseInt(document.getElementById("count").value) +1;
    	 }
    	 
    	 if(count>=5 ){
    		 timeoutCount += count * stepTimecount2;
    	 }else if(count>=10){
        	 timeoutCount += count * stepTimecount3;
    	 }else{
    		 timeoutCount += count * stepTimecount1; 
    	 }
    	// alert(timeoutCount);
    		 
    	 navigator.geolocation.getCurrentPosition(foundLocation,noLocation,{timeout:timeoutCount});
    	 
    	 document.getElementById("count").value = count;
	 }
    
     function foundLocation(position) {
    		
   	   // Get address
   	   /* var map = new google.maps.Map(document.getElementById('map'), {
   	    zoom: 8,
   	    center: {lat:10 , lng: 150.644}
   	    });
   	   var geocoder = new google.maps.Geocoder();
   	   geocodeAddress(geocoder, map); */
   		   
   	    document.getElementById("location").value= position.coords.latitude+","+position.coords.longitude;
   	    document.getElementById("lat").value = position.coords.latitude;
   	    document.getElementById("lng").value = position.coords.longitude;
 
   		//Show Map
	   	  var myLatLng = {lat:position.coords.latitude, lng: position.coords.longitude};
		  var map = new google.maps.Map(document.getElementById('map'), {
		    zoom: 16,
		    center: myLatLng
		  });
		  
		   var marker = new google.maps.Marker({
		    position: myLatLng,
		    map: map,
		    title: '<%=customerName%>'
		  });
		  
		  marker.addListener('click', function() {
			   map.setZoom(8);
			   map.setCenter(marker.getPosition());
	       });
     }
     
    function noLocation() {
   		$("#location").val("not found");
   		//$('#spnWait').hide();
    }
    
    function saveLocation(){
    	 var location = document.getElementById("location").value;
    	
    	 //alert(location);
    	 window.opener.setLocationValue(location);
    	 window.close();
    }
    </script>
    
    <%if("foundGPS".equalsIgnoreCase(action) && isInternetConn){ %>
        <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA1vZ7pnm-fm1dttRBhXwEpUO2iCqduTgg&callback=initMap&region=TH&language=th"></script>
     <%}else{ %>
      <script>
         //Case No Internet connection or not found gps Location
         window.onload =function(){
        	 initMap(); 
         }
      </script>
     <%} %>
    <form name="locationForm"  enctype="multipart/form-data">
       	<div id="div_loading" title=" กรุณารอสักครู่......" style="display:none" align="center" >
	    	  <b><span id="disp"> กำลังค้นหาตำแหน่ง กรุณาโปรดรอสักครู่  </span></b><br/>
	          <img src="${pageContext.request.contextPath}/images/loading.gif" width="100" height="100" />
        </div>
        <div id="div_error" title=" " style="display:none;color:red;font-size:30px" align="center" ></div>
        
        <div id="dialog_restart_tomcat" title="คำแนะนำ" style="display:none">
			<p align="center"> <a href="javascript:close();">
			<input class="freeBtn"  type="submit" onclick="restartTomcat();" value="Restart Tomcat"/></a>&nbsp;&nbsp;
			 <a href="javascript:close();"><input class="freeBtn"  type="submit" onclick="close();" value="ปิดหน้าจอ"/></a></p>
		</div>
	    <div id="div" align="center">
		    <table border="0" width="100%" class="result" cellspacing="3" cellpadding="1">
		    <tr>
		        <th colspan="2">
			        <input type="text" id="location" />
				    <b> Latitude:<input type="text" id="lat" readonly size="14" class="disableText"/>
				    Longitude:<input type="text" id="lng" readonly size="14" class="disableText" /></b>
				            จำนวนครั้ง:<input type="text" id="count" readonly size="3"  class="disableText"/> &nbsp;&nbsp;
				    Time Use:<input type="text" name="timeInUse" readonly size="10"  class="disableText" value="<%=timeInUse%>"/>&nbsp;Seconds
				</th>
			</tr>
			<tr>
				<th>
				    <input type="button" value="ค้นหาตำแหน่ง (By GPS Device)" onclick="findMapByGPS()" class="newPosBtnLong"/>&nbsp;&nbsp;
				    <input type="button" value="ค้นหาตำแหน่ง (By Google Map)" onclick="initMapModel()" class="newPosBtnLong"/>
				 </th>
				 <th>
				    <input type="button" value="ยืนยันตำแหน่งร้านค้านี้ "  onclick="saveLocation() " class="newPosBtnLong"/>
		        </th>
		     </tr>
		    </table>
	     </div>
	     
	     <input type="hidden" id="test" value="test"/>
	     <input type="hidden" id="errorMsg" value=""/>
	     <input type="hidden" name="status" id ="status" />
	     
    </form>
    <script>
    /********* Find Locak screen **************************/
      var useTimeMillisecs = 0;
	  var startTime = new Date();
	  var startDate = new Date();
	   
	  function findMapByGPS(){
		  document.getElementById("div_error").innerHTML= "";
		  document.getElementById("errorMsg").value ="";
	      startprocess();
	   }
	  <%if("new".equalsIgnoreCase(runNew)){ %>
	       startprocess();
	  <%}%>
	  
	  function startprocess(){
			 /** Start Get GPS **/
			 $(function(){
	    		var getData = $.ajax({
	    			url: "${pageContext.request.contextPath}/jsp/ajax/startFindLocationAjax.jsp",
	    			data : "",
	    			async: true,
	    			success: function(getData){
	    				var returnString = jQuery.trim(getData);
	    				document.getElementsByName("status")[0].value = returnString;
	    			}
	    		}).responseText;   
	    	 });
			  
			  document.getElementById("div_loading").style.display = 'block';  
	    	  var status = document.getElementsByName("status")[0]; 
	    	  if (status.value != "1" && status.value != "-1"){
	    		  window.setTimeout("checkStatusProcess();", 800);
	    	  }
	    	  update(status.value);
	   } 

	   function update(status){
	    	 if(status != '1'){ //Running
	    		 useTimeMillisecs = (new Date()).getTime()-startTime.getTime();
	    	     
	    	 }else{ //Success
	    		 useTimeMillisecs = (new Date()).getTime()-startTime.getTime();
	    		 document.getElementById("div_loading").style.display = 'none';          // Show
	    	 }  
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
	    	  //alert(status);
	    	   if(status == '1' || status == '-1' || status == '-2'){ //Finish Task   
	    		   //Calc Time thred use
	    		   try{
		    		   var endDate = new Date();
		    		   var dif = endDate.getTime() - startDate.getTime();
		    		   var Seconds_from_T1_to_T2 = dif / 1000;
		    		   var Seconds_Between_Dates = Math.abs(Seconds_from_T1_to_T2);
		    		   document.getElementsByName("timeInUse")[0].value = Seconds_from_T1_to_T2;//Seconds_Between_Dates; 
		    		 
	    		   }catch(e){}
	    		   
	    		   /** Task Success ***/
	    		   update(status);
	    		   
	    		   //** Search display data **/
	    		   searchDisplayData();
	    		
	    	   }else { //Task Running
	    		   var endDate = new Date();
	    		   var dif = endDate.getTime() - startDate.getTime();
	    		   var Seconds_from_T1_to_T2 = dif / 1000;
	    		   var Seconds_Between_Dates = Math.abs(Seconds_from_T1_to_T2);
	    		   document.getElementsByName("timeInUse")[0].value = Seconds_from_T1_to_T2;//Seconds_Between_Dates; 
	    		   
	    		   /** Task Not Success  and Re Check Status**/
		    	   update(status);
	               window.setTimeout("checkStatusProcess();", 600);
	           }
	    }
		 
       function searchDisplayData(){
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
    				document.getElementById("location").value= returnString.split('|')[0]+","+returnString.split('|')[1];
    				
    				if(returnString.split('|')[2] != ""  || "null" ==returnString.split('|')[1]){
    					document.getElementById("errorMsg").value =returnString.split('|')[2];
    				
    					document.getElementById("div_loading").style.display = 'none';
    					document.getElementById("div_error").style.display = 'block';
    					if("null" ==returnString.split('|')[1]){
    						//alert(returnString.split('|')[1]);
    						document.getElementById("div_error").innerHTML = "ไม่สามารถเข้าถึงอุปกรณ์ GPS กรุณาปิด Tomcat แล้วเปิดใหม่อีกครั้ง";
	        				showRestartTomcatPopup("${pageContext.request.contextPath}");
	        				//alert("");
    					}else{
	    					if("ErrorNotFoundGPSDevice" ==returnString.split('|')[2]){
	    						document.getElementById("div_error").innerHTML = "ไม่พบอุปกรณ์ GPS กรุณาตรวจสอบว่าได้เชื่อมต่ออุปกรณ์ GPS หรือไม่";
	    					}else if("ErrorNotFoundLocation" ==returnString.split('|')[2]){
	    						document.getElementById("div_error").innerHTML = "ไม่พบข้อมูล ตำแหน่ง GPS กรูณากดคนหาใหม่ หรือเดินออกไปนอกอาคาร";
	    					}if("ErrorNotLoadGPSDevice" ==returnString.split('|')[2]){
	        				   document.getElementById("div_error").innerHTML = "ไม่สามารถเข้าถึงอุปกรณ์ GPS กรุณาปิด Tomcat แล้วเปิดใหม่อีกครั้ง";
	        				   
	        				   showRestartTomcatPopup("${pageContext.request.contextPath}");
	    					}
    					}
    				}else{
	    				var param  ="&lat="+returnString.split('|')[0];
	    				    param +="&lng="+returnString.split('|')[1];
	    				    param +="&location="+returnString.split('|')[0]+","+returnString.split('|')[1];
	    				    param +="&customerName=<%=customerName%>";
	    				    param +="&timeInUse="+document.getElementsByName("timeInUse")[0].value;
	    				//submit refresh Maps
	    		     	window.location = "${pageContext.request.contextPath}/jsp/location/findLocationByGPS.jsp?action=foundGPS"+param;
    				}
    			}
    		}).responseText;   
       }
       
       <%if("foundGPS".equalsIgnoreCase(action)  && !isInternetConn){ %>
            document.getElementById("div_error").style.display = 'block';
            document.getElementById("div_error").style.color = "Green";
            document.getElementById("div_error").innerHTML = "ค้นเจอตำแหน่งแล้ว <br> แต่ไม่สามารถแสดงแผนที่ได้เนื่องจาก ไม่ได้เชื่อมต่อ อินเทอร์เน็ต <br>" +document.getElementById("div_error").innerHTML ;
        <%} %>
        
      /** Display dialog restart Tomcat **/
		  function showRestartTomcatPopup(path){
			  if(document.getElementById("errorMsg").value =="ErrorNotLoadGPSDevice"){
				  var width = "600";
				  var height ="200";
			   //  window.open(path+"/jsp/pop/restartTomcatPopup.jsp", "Restart Tomcat", "width=50,height=50,location=No,resizable=No");
				  PopupCenter(path+"/jsp/pop/restartTomcatPopup.jsp?", "Restart Tomcat",width,height);
			  }
         }
		
		
   /******* Find Location screen ****************************/   
   </script>
   <div id="map"></div>
  </body>
</html>