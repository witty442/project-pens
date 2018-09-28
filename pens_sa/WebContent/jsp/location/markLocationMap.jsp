<%@page import="util.GoogleMapJavaScriptAPI"%>
<%@page import="util.Utils"%>
<%@page import="com.isecinc.pens.web.location.LocationBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String contextPath = request.getContextPath();
List<LocationBean> customerList = (List<LocationBean>)session.getAttribute("CUST_LOC_LIST");
String locations = "";
String zoom = "1";
String label = "";
String latDefault = "";
String lngDefault = "";
int totalRec = 0;
if(customerList != null && customerList.size() >0){ 
	totalRec = customerList.size();
	
      for(int r=0;r<customerList.size();r++){
	   	   LocationBean c = customerList.get(r);
	   	   //System.out.println("Location:"+c.getLat()+","+c.getLng());
	   	  // System.out.println("CustomerCode:"+c.getCustomerCode());
	   	   if(r==0){//xx Case NoOrder get location from customer_master
	   		 if(Utils.isNull(c.getLat()).equals("")){
	   			latDefault = c.getCustLat();
		   		lngDefault	= c.getCustLng();
	   		 }else{
	   		    latDefault = c.getLat();
	   		    lngDefault	= c.getLng();
	   		 }
	   	   }
   		   label = "ร้านค้า : "+c.getCustomerCode()+"-"+c.getCustomerName()+"<br/>";
   		   label += "พนักงานขาย: "+c.getSalesrepCode()+"-"+c.getSalesrepName()+"<br/>";
   		   label += "Trip :"+Utils.isNull(c.getTrip())+"<br/>";
   		   if("order".equals(c.getLocationType())){
   		     label += "วันที่ขาย :"+c.getOrderDate()+"<br/>";
   		     label += "เลขที่ Order No: "+c.getOrderNo()+"<br/>";
   		   }
   		   if("visit".equals(c.getLocationType())){
   		     label += "วันที่เยี่ยม: "+c.getVisitDate()+"<br/>";
   		   }
   		 
	   	   if(r==customerList.size()-1){
	   		   //Case Noorder get location from master
		   		if("noorder".equals(c.getLocationType())){
		   	       locations +="['"+label+"', "+c.getCustLat()+","+c.getCustLng()+", "+r+",'"+c.getLocationType()+"','"+c.getCustomerType()+"'] \n";		   	      
		   		}else{
		   		   locations +="['"+label+"', "+c.getLat()+","+c.getLng()+", "+r+",'"+c.getLocationType()+"','"+c.getCustomerType()+"'] \n";		   	      
		   		}
	   		}else{
		   		if("noorder".equals(c.getLocationType())){
		   		   locations +="['"+label+"', "+c.getCustLat()+","+c.getCustLng()+", "+r+",'"+c.getLocationType()+"','"+c.getCustomerType()+"'],\n";  
		   		}else{
		   		   locations +="['"+label+"', "+c.getLat()+","+c.getLng()+", "+r+",'"+c.getLocationType()+"','"+c.getCustomerType()+"'],\n";  
		   	    }
	   	  }
     }//for
 }//if custList
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
   	    zoom: 8,
   	   /* mapTypeId: 'satellite', */
   	    center: {lat: <%=latDefault%>, lng: <%=lngDefault%>} 
   	  });

   	  setMarkers(map);
   	}
     
  // Data for the markers consisting of a name, a LatLng and a zIndex for the
  // order in which these markers should display on top of each other.
  var locationArr = [<%=locations%>];
  
  function setMarkers(map) {
	  var infowindow = new google.maps.InfoWindow();
    // Adds markers to the map.

    // Marker sizes are expressed as a Size of X,Y where the origin of the image
    // (0,0) is located in the top left of the image.

    // Origins, anchor positions and coordinates of the marker increase in the X
    // direction to the right and in the Y direction down.
    var imageCustomer = {
        url: '<%=contextPath%>/icons/generalshop.png',
        // This marker is 20 pixels wide by 32 pixels high.
        size: new google.maps.Size(32, 32),
        // The origin for this image is (0, 0).
        origin: new google.maps.Point(0, 0),
        // The anchor for this image is the base of the flagpole at (0, 32).
        anchor: new google.maps.Point(0, 32)
    };
    var imageCustomerBlue = {
            url: '<%=contextPath%>/icons/blueflagicon.png',
            // This marker is 20 pixels wide by 32 pixels high.
            size: new google.maps.Size(40, 32),
            // The origin for this image is (0, 0).
            origin: new google.maps.Point(0, 0),
            // The anchor for this image is the base of the flagpole at (0, 32).
            anchor: new google.maps.Point(0, 32)
      };
    //Normal
    var imageNoOrder = {
        	  url: '<%=contextPath%>/icons/generalshop_noorder.png',
       	      size: new google.maps.Size(32, 40),
       	      origin: new google.maps.Point(0, 0),
       	      anchor: new google.maps.Point(2, 32)
         };
    var imageOrder = {
      	  url: '<%=contextPath%>/icons/generalshop_order.png',
     	      size: new google.maps.Size(32, 40),
     	      origin: new google.maps.Point(0, 0),
     	      anchor: new google.maps.Point(2, 32)
       };
    var imageVisit = {
    	  url: '<%=contextPath%>/icons/generalshop_visit.png',
   	      size: new google.maps.Size(36, 40),
   	      origin: new google.maps.Point(0, 0),
   	      anchor: new google.maps.Point(2, 36)
   	    };
    
    /** blueflag **/
    var imageNoOrderBlue = {
      	  url: '<%=contextPath%>/icons/blueflagicon_noorder.png',
     	      size: new google.maps.Size(50, 36),
     	      origin: new google.maps.Point(0, 0),
     	      anchor: new google.maps.Point(2, 40)
       };
    var imageOrderBlue = {
    	  url: '<%=contextPath%>/icons/blueflagicon_order.png',
   	      size: new google.maps.Size(50, 40),
   	      origin: new google.maps.Point(0, 0),
   	      anchor: new google.maps.Point(2, 32)
     };
    var imageVisitBlue = {
  	  url: '<%=contextPath%>/icons/blueflagicon_visit.png',
 	      size: new google.maps.Size(50, 36),
 	      origin: new google.maps.Point(0, 0),
 	      anchor: new google.maps.Point(4, 36)
 	    };
  
    // Shapes define the clickable region of the icon. The type defines an HTML
    // <area> element 'poly' which traces out a polygon as a series of X,Y points.
    // The final coordinate closes the poly by connecting to the first coordinate.
    var shape = {
      coords: [1, 1, 1, 20, 18, 20, 18, 1],
      type: 'poly'
    };
    for (var i = 0; i < locationArr.length; i++) {
      var location = locationArr[i];
      var type = location[4];
      var storeType = location[5];
    
      if(type=='customer'){
    	  if(storeType=='B'){
    		  image = imageCustomerBlue;
    	  }else{
    		  image = imageCustomer; 
    	  }
      }else if(type=='order'){
    	  if(storeType=='B'){
             image = imageOrderBlue;
    	  }else{
    		 image = imageOrder;
    	  }
      }else if(type=='visit'){
    	  if(storeType=='B'){
             image = imageVisitBlue;
    	  }else{
    		 image = imageVisit;
    	  }
      }else if(type=='noorder'){
    	  if(storeType=='B'){
             image = imageNoOrderBlue;
    	  }else{
    		 image = imageNoOrder;
    	  }
      }
      var marker = new google.maps.Marker({
        position: {lat: location[1], lng: location[2]},
        map: map,
        icon: image,
       /*  shape: shape,  */
       /*  title: location[0], */
        zIndex: location[3]
        });
      
      // Add Msg To Info Windown
       var content = location[0];
  
      //Add Event Click
      google.maps.event.addListener(marker,'click', (function(marker,content,infowindow){ 
         return function() {
           infowindow.setContent(content);
           infowindow.open(map,marker);
         };
     })(marker,content,infowindow)); 
 
    }//for
  }
  </script>
  <script async defer
  src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>&callback=initMap&region=TH&language=th">
  </script>
  </body>
</html>