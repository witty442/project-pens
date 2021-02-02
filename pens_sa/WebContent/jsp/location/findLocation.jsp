
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
%>
<html>
  <head>
    <title>Find Location</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
   
 </head>
 
  <body>
    <script>
		 // check for Geolocation support
		    if (navigator.geolocation) {
		      console.log('Geolocation is supported!');
		    }
		    else {
		      console.log('Geolocation is not supported for this Browser/OS version yet.');
		    }
      
    </script>
  
    
    <form>
	    <div id="tripmeter">
		  <p>
		    Starting Location (lat, lon):<br/>
		    <span id="startLat">???</span>°, <span id="startLon">???</span>°
		  </p>
		  <p>
		    Current Location (lat, lon):<br/>
		    <span id="currentLat">???</span>°, <span id="currentLon">???</span>°
		  </p>
		  <p>
		    Distance from starting location:<br/>
		    <span id="distance">0</span> km
		  </p>
		</div>
    </form>
    
    <script>
        window.onload = function() {
    	  var startPos;
    	  navigator.geolocation.getCurrentPosition(function(position) {
    	    startPos = position;
    	    document.getElementById('startLat').innerHTML = startPos.coords.latitude;
    	    document.getElementById('startLon').innerHTML = startPos.coords.longitude;
    	  });
    	};
    </script>
  </body>
</html>