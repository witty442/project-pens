<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
List<Customer> customerList = new MCustomer().getCustomerLocation();
String locations = "";
String zoom = "1";

if(customerList != null && customerList.size() >0){ 
      for(int r=0;r<customerList.size();r++){
	   	   Customer c = customerList.get(r);
	   	   //System.out.println("Location:"+c.getLocation());
	   	   
	   	   if(Utils.isLocationValid(c.getLocation())){
		   	   if(r==customerList.size()-1){
		   	      locations +="['"+c.getName()+"', "+c.getLocation()+", "+r+"] \n";
		   	   }else{
		   		  locations +="['"+c.getName()+"', "+c.getLocation()+", "+r+"],\n";  
		   	   }
	   	   }//if
     }
 }
%>
<html>
  <head>
    <title>แสดงร้านค้าทั่วประเทศ</title>
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
     /**** Method 2 ***/
     //init thailand 13.811561, 100.414124
     function initMap() {
   	  var map = new google.maps.Map(document.getElementById('map'), {
   	    zoom: 6,
   	    center: {lat: 13.81, lng: 100.41}
   	  });

   	  setMarkers(map);
   	}
     
  // Data for the markers consisting of a name, a LatLng and a zIndex for the
  // order in which these markers should display on top of each other.
  var beaches = [<%=locations%>
  ];
  

  function setMarkers(map) {
    // Adds markers to the map.

    // Marker sizes are expressed as a Size of X,Y where the origin of the image
    // (0,0) is located in the top left of the image.

    // Origins, anchor positions and coordinates of the marker increase in the X
    // direction to the right and in the Y direction down.
    var image = {
      url: 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png',
      // This marker is 20 pixels wide by 32 pixels high.
      size: new google.maps.Size(20, 32),
      // The origin for this image is (0, 0).
      origin: new google.maps.Point(0, 0),
      // The anchor for this image is the base of the flagpole at (0, 32).
      anchor: new google.maps.Point(0, 32)
    };
    // Shapes define the clickable region of the icon. The type defines an HTML
    // <area> element 'poly' which traces out a polygon as a series of X,Y points.
    // The final coordinate closes the poly by connecting to the first coordinate.
    var shape = {
      coords: [1, 1, 1, 20, 18, 20, 18, 1],
      type: 'poly'
    };
    for (var i = 0; i < beaches.length; i++) {
      var beach = beaches[i];
      var marker = new google.maps.Marker({
        position: {lat: beach[1], lng: beach[2]},
        map: map,
       /*  icon: image, */
        shape: shape,
        title: beach[0],
        zIndex: beach[3]
        });
      
      google.maps.event.addListener(marker, 'click', (function(marker, i) {
          return function() {
            infowindow.setContent(beaches[i][0]);
            infowindow.open(map, marker);
          }
        })(marker, i));
    }
  }

    </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>&callback=initMap&region=TH&language=th">
    </script>
  </body>
</html>