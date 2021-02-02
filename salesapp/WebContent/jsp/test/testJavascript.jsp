<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
  <script>
  function checkInputKeyNum(e){
		var keynum = e.keyCode;
	
		//alert(e.keyCode);
		document.getElementById("keyNumX").value =keynum;
  }
  </script>
  
  <input type="text" name="inputTest" id="inputTest" onkeydown="checkInputKeyNum(event)"/>
 KeyNume: <input type="text" name="keyNumX" id="keyNumX" />
  
</body>
</html>