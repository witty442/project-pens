
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">

function loadMe(){
	var isPC = isWindowPC();
	//alert("isPC:"+isPC);
	
	//for test mobile
	//isPC = false;
	
	if( isPC==false){
		 // alert("mobile");
		 window.location = "${pageContext.request.contextPath}/indexMobile.jsp";
	}else{
		 window.location = "${pageContext.request.contextPath}/indexPC.jsp";
	}
}

function isWindowPC(){
	//alert(navigator.platform.toUpperCase());
	var navText = navigator.platform.toUpperCase();
	//alert("navText:"+navText);
	if(navText.indexOf("WIN") != -1){
		return true;
	}
	return false;
}
function isMobile(){
	var mobile = false;
	if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)){
		 // alert("mobile");
		mobile = true;
	}
	return mobile;
}

</script> 
</head>
<body onload="loadMe();">
</body>
</html>
