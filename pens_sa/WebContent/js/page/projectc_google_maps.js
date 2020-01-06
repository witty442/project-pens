/********************************************** Google Map ***************************************/
function getLocation(path,index,storeCode,storeName,branchId,branchName){
	var param   = "&storeCode="+storeCode;
	    param  += "&storeName="+storeName;
	    param  += "&branchId="+branchId;
	    param  += "&branchName="+branchName;
	    param  += "&index="+index;
	var width= window.innerWidth;
	var height= window.innerHeight;
	//alert(width+","+height);
	PopupCenter(path+"/jsp/projectC/location/findLocation.jsp?run=new"+param, "Print",width,height);
}

 function setLocationValue(location,lat,lng){
	 $("#location").val(location);
	 $("#lat").val(lat);
	 $("#lng").val(lng);
     //alert(lat+","+lng);
 }

 function gotoMap(path,index,storeCode,storeName,branchId,branchName){
	 var storeLat = document.getElementsByName("storeLat")[index].value;
	 var storeLong = document.getElementsByName("storeLong")[index].value;
	 
		//alert(lat+","+lng);
		if(storeLat != "" && storeLong != ""){
			var param   = "storeCode="+storeCode;
		    param  += "&storeName="+storeName;
		    param  += "&branchId="+branchId;
		    param  += "&branchName="+branchName;
		    param  += "&index="+index;
		    param  += "&storeLat="+storeLat;
		    param  += "&storeLong="+storeLong;
		    
		    var branch = branchId+"-"+branchName;
		    var width= window.innerWidth;
			var height= window.innerHeight;
			
			PopupCenter(path+"/jsp/projectC/location/showMapDetail.jsp?"+param, "แสดงแผนที่",width,height);
	        // window.open("https://www.google.co.th/maps/place/"+location);//version 1
	}else{
		alert("ยังไม่ได้ระบุตำแหน่งร้านค้านี้  กรุณา 'กดค้นหาตำแหน่ง'");
	}
}
 
 /********************************************** Google Map ***************************************/