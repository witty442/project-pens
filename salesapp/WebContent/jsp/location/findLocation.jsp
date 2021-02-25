
<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
 //String lat = Utils.isNull(request.getParameter("lat"));
 //String lng = Utils.isNull(request.getParameter("lng"));
 String customerName = "ร้าน "+new String(Utils.isNull(request.getParameter("customerName")).getBytes("ISO8859_1"), "TIS-620"); 
 //System.out.println("customerName:"+customerName);
%>
<html>
  <head>
    <title>Find Location</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
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
    
      // Note: This example requires that you consent to location sharing when
      // prompted by your browser. If you see the error "The Geolocation service
      // failed.", it means you probably did not give permission for the browser to
      // locate you.
     function initMap() {
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
    	document.getElementById("location").value="not found";
   		//$('#spnWait').hide();
    }
    
    function saveLocation(){
    	 var location = document.getElementById("location").value;
    	 var lat = document.getElementById("lat").value;
    	 var lng = document.getElementById("lng").value;
    	    
    	 window.opener.setLocationValue(location,lat,lng);
    	 window.close();
    }

    </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>&callback=initMap&region=TH&language=th">
    </script>
    
    <form>
	    <div id="div" align="center">
		    <table border="0" width="100%" class="result" cellspacing="3" cellpadding="1">
		    <tr>
		        <th>
			        <input type="hidden" id="location" />
				    <b> Latitude:<input type="text" id="lat" readonly size="15" class="disableText"/>
				    Longitude:<input type="text" id="lng" readonly size="15" class="disableText" /></b>
				            จำนวนครั้งที่ค้นหา :<input type="text" id="count" readonly size="3"  class="disableText"/> &nbsp;&nbsp;
				    <input type="button" value="ค้นหาตำแหน่ง " onclick="initMap()" class="newPosBtnLong"/>
				    <input type="button" value="ยืนยันตำแหน่งร้านค้านี้ "  onclick="saveLocation() " class="newPosBtnLong"/>
		        </th>
		     </tr>
		    </table>
	     </div>
    </form>
    
    <div id="map"></div>
  </body>
</html>