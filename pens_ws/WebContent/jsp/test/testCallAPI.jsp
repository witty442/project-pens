<%@ page language="java" contentType="text/html; charset=ISO-8859-1"pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.unobtrusive-ajax.js"></script>
<script type="text/javascript">

function getGoogleCloudFunction(){
	 var url= "https://us-central1-temporal-genius-163109.cloudfunctions.net/getLocNode";
	 //var jsonData = $.get(url);
	 
	 //alert(jQuery.parseJSON(jsonData));
	 var xmlHttp = new XMLHttpRequest();
	    xmlHttp.open( "GET", url, false ); // false for synchronous request
	    xmlHttp.send( null );
	    alert(xmlHttp.responseText);
}
function xx(){
	var dataShow = "";
	var resultData = document.getElementById("resultData");
	 $.ajax({
		  type: "GET",
		  url: "https://us-central1-temporal-genius-163109.cloudfunctions.net/getLocNode",
		  dataType: 'json',
	
	      success: function(data) {
	        //set your variable to the result 
	         resultData.innerHTML = data;
	    },
	    error: function(data) {
	        //handle the error 
	    	alert("error: data:"+data);
	    }
	}); 
}

function getCustomerServiceJson(){
	var dataShow = "";
	var resultData = document.getElementById("resultData");
	$.ajax({
		  type: "GET",
		  url: "http://localhost:8080/pens_ws/service/customer/get",
		  dataType: 'json',
	      success: function(data) {
	        //set your variable to the result 
	         dataShow = "CustomerCode:"+data.customerCode+"\n";
	         dataShow +=",Customer Name:"+data.customerName+"\n";
	         resultData.innerHTML = dataShow;
	    },
	    error: function(result) {
	        //handle the error 
	    	alert("error:"+result);
	    }
	});
}

function postCustomerServiceJson(){
	var returnString = "";
	var form = document.tempForm;
	var dataShow = "";
	var customer = {
           customerCode: "001 ",
           customerName : "Pens "
        };
	
	 $('#resultData').html('sending..');
	
	var getData = $.ajax({
			url: "http://localhost:8080/pens_ws/service/customer/send",
			type :'post',
			data: JSON.stringify(customer),
			contentType: "application/json; charset=utf-8",
			dataType: 'json',
			success: function (data) {
				dataShow = "CustomerCode:"+data.customerCode+"\n";
		        dataShow +=",Customer Name:"+data.customerName+"\n";
		        dataShow +=",Status:"+data.status+"\n";
                $('#resultData').html(dataShow);
            }
		}).responseText;
}

</script>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test call API</title>
</head>
<body>
<form name="tempForm">
    <table  border="0" cellpadding="3" cellspacing="0" align="center">
		<tr>
			<td align="left"> 	
			Test Call API Service (Restful) <br/>
			   <input type="button" name="test" value="getGoogleCloudFunction" onclick="getGoogleCloudFunction()"/>
			   <input type="button" name="test1" value="getCustomerServiceJson" onclick="getCustomerServiceJson()"/>
			   <input type="button" name="test2" value="postCustomerServiceJson" onclick="postCustomerServiceJson()"/>
			  
			</td> 
		</tr>
		<tr>
			<td > Result Response</td>
		</tr>
		<tr>
			<td > <div id="resultData"></div></td>
		</tr>
	</table>
	
</form>
</body>
</html>