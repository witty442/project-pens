<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>Restart Tomcat</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>

<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<style>
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
</style>
<script type="text/javascript">
function restartTomcat(){
	if(confirm("ยืนยัน รีสตาร์ท Tomcat")){
		 $(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/stopTomcatAjax.jsp",
				data : "",
				success: function(getData){
					var returnString = jQuery.trim(getData);
				}
			}).responseText;   
	     });
		 
		 setTimeout("startTomcat()",5000);
	}
}

function startTomcat(){
	 $(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/startTomcatAjax.jsp",
			data : "",
			success: function(getData){
				var returnString = jQuery.trim(getData);
			}
		}).responseText;   
  });
	 
	 setTimeout("window.close()",1000);
}

</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<!-- BODY -->

<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
   <tr>
		<th colspan="2"><b>เนื่องจาก ไม่สามรถใช้งานอุปกรณื GPSได้    ต้องทำการ รีสตาร์ท Tomcat อีกครั้ง  (โปรดตรวจสอบว่าได้เสียบอุปกรณ์ GPS)</b></th>
	</tr>
	<tr>
		<th>
			<a href="#" onclick="restartTomcat();">
			   <input type="button" value="รีสตาร์ท Tomcat" class="newPosBtnLong">
			</a>
		</th>
		<th>
			<a href="#" onclick="window.close();">
			<input type="button" value="ปิดหน้าต่างนี้" class="newPosBtnLong">
			</a>
		</th>
	
	</tr>
</table>

<!-- BODY -->
</body>
</html>