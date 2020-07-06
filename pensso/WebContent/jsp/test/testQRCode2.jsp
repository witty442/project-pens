<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<html>
<head>
<title>Demo page TEST</title>

</head>
<body>
<form name="temp">
<p>
	TODO make a nice looking pure client qrcode generator
	even allow download of the image
</p>

<div id="output"></div>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/qrcode/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qrcode/jquery.qrcode.min.js"></script>
<script>
jQuery(function(){
	jQuery('#output').qrcode("http://jetienne.comsssss");
})
</script>
</form>
</body>
</html>