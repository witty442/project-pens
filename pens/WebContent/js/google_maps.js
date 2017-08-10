/********************************************** Google Map ***************************************/
function getLocation(path){
	var customerName = $("#customerCode").val()+"-"+$("#customerName").val();
	//window.open(path+"/jsp/location/findLocation.jsp?customerName="+customerName);
	var width= window.innerWidth;
	var height= window.innerHeight;
	//alert(width+","+height);
	PopupCenter(path+"/jsp/location/findLocationByGPS.jsp?run=new&customerName="+customerName, "Print",width,height);
	//window.open(path+"/jsp/location/findLocation.jsp?run=new&customerName="+customerName, "Find Location", "width="+width+",height="+height+",location=No,resizable=No");
}

 function setLocationValue(location,lat,lng){
	 $("#location").val(location);
	 $("#lat").val(lat);
	 $("#lng").val(lng);
     //alert(lat+","+lng);
 }

 function gotoMap(path){
	 var location= $("#location").val();
		//alert(lat+","+lng);
		if(location != "" ){
			var locationArr = location.split(",");
			var lat = locationArr[0];
			var lng = locationArr[1];
		    var customerName = $("#customerCode").val()+"-"+$("#customerName").val();
		    var width= window.innerWidth-100;
			var height= window.innerHeight-100;
			
			PopupCenter(path+"/jsp/location/showMapDetail.jsp?lat="+lat+"&lng="+lng+"&customerName="+customerName, "แสดงแผนที่",width,height);
	        // window.open("https://www.google.co.th/maps/place/"+location);//version 1
	}else{
		alert("ยังไม่ได้ระบุตำแหน่งร้านค้านี้  กรุณา 'กดค้นหาตำแหน่ง'");
	}
}
 
 /********************************************** Google Map ***************************************/