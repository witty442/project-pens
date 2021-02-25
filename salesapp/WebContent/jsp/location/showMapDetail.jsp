<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
 String lat = Utils.isNull(request.getParameter("lat"));
 String lng = Utils.isNull(request.getParameter("lng"));
 String customerName = new String(Utils.isNull(request.getParameter("customerName")).getBytes("ISO8859_1"), "TIS-620"); 
 System.out.println("customerName:"+customerName);
%>
<html>
  <head>
    <title>แผนที่ร้าน <%=customerName %></title>
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
    </style>
  </head>
  <body>
    <div id="map"></div>
    <script>
      // Note: This example requires that you consent to location sharing when
      // prompted by your browser. If you see the error "The Geolocation service
      // failed.", it means you probably did not give permission for the browser to
      // locate you.

     function initMap() {
		  var myLatLng = {lat: <%=lat%>, lng: <%=lng%>};
		
		  var map = new google.maps.Map(document.getElementById('map'), {
		    zoom: 18,
		    center: myLatLng
		  });
		
		  var marker = new google.maps.Marker({
		    position: myLatLng,
		    map: map,
		    title: '<%=customerName%>'
		  });
		}

    </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI	.getInstance().getAPIKey() %>&callback=initMap&region=TH&language=th">
    </script>
  </body>
</html>