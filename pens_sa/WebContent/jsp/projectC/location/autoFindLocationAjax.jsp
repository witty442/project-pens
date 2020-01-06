<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
 String index = Utils.isNull(request.getParameter("index"));
 String storeCode = request.getParameter("storeCode");
 String storeName = "ร้าน "+storeCode+"-"+new String(Utils.isNull(request.getParameter("storeName")).getBytes("ISO8859_1"), "TIS-620"); 
 String branchId = request.getParameter("branchId");
 String branchName = "สาขา "+branchId+"-"+new String(Utils.isNull(request.getParameter("branchName")).getBytes("ISO8859_1"), "TIS-620"); 

%>
<html>
  <head>
    <title>Find Location</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
 </head>
  <body>
  
    <script>
    
      // Note: This example requires that you consent to location sharing when
      // prompted by your browser. If you see the error "The Geolocation service
      // failed.", it means you probably did not give permission for the browser to
      // locate you.
     function initMap() {
    	 var timeoutCount = 10000;
    	 navigator.geolocation.getCurrentPosition(foundLocation,noLocation,{timeout:timeoutCount});
	 }
    
     function foundLocation(position) {
   	    document.getElementById("lat").value = position.coords.latitude;
   	    document.getElementById("lng").value = position.coords.longitude;
     }
     
    function noLocation() {
    	document.getElementById("location").value="not found";
   		//$('#spnWait').hide();
    }
    
    </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey()%>&callback=initMap&region=TH&language=th">
    </script>
    
  </body>
</html>