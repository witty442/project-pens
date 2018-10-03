<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<title>Test</title>
</head>
<body>

<form name="form1" action="progressBar.jsp">

<div id="progressbar" style="width:600px"></div>

<input type="hidden" name="status" size="2"/><br/>
 <table border=0 width=300>
  <tr>
 <td>
    <div id="TimeBar"></div>
  </td>
  </tr>
</table>
</form>
<script type="text/javascript" language="javascript">

var maxmips=600;
var mips=1;
ID = window.setTimeout("update();", 1000);
window.focus();
function update(comp) {
	mips = mips+20;
	//update progress bar
	progressTimeLength= mips;
	
	if(comp ==600){
	   alert(600);
	   progressTimeLength = 600; 
	   TimeBar.innerHTML="<img src=\"/pens_test/images/xxtab2.gif\" border=0 height=30 width="+progressTimeLength+">";
	}else{
	  TimeBar.innerHTML="<img src=\"/pens_test/images/xxtab2.gif\" border=0 height=30 width="+progressTimeLength+">";
	  ID=window.setTimeout("update(1);",500);
	}
}

//call ajax
//window.onload =function(){
	<% if( "submited".equals(request.getParameter("action"))){ %>
	   var status = document.form1.status; 
	   if (status.value != "1" && status.value != "-1"){
		   window.setTimeout("update(1);", 500);
		   window.setTimeout("checkStatusProcess();", 500);
	   }
	<%} %>
//}

function checkStatusProcess(){
   // alert($('#userName').val());
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/interfacesAjax.jsp",
			data : "id=<%=request.getParameter("id")%>",
			success: function(getData){
				var returnString = jQuery.trim(getData);
				document.form1.status.value = returnString;
			}
		}).responseText;   
	});
	setTimeout("checkStatus()",5000);
}

function checkStatus(){
	  var status =  document.form1.status.value;
	  //alert("checkStatus status:"+status);
	   if(status != '1' && status != "-1"){
           update(1);
           window.setTimeout("checkStatusProcess();", 500);
	   }else{
		   window.setTimeout("update(600);", 500);
		   window.close();
     }
}
</script>

</body>
</html>