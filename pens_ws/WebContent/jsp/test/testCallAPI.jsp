<%@ page language="java" contentType="text/html; charset=ISO-8859-1"pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.unobtrusive-ajax.js"></script>
<script type="text/javascript">
function getBOTAPIJson(){
	var dataShow = "";
	var resultData = document.getElementById("resultData");
	$.ajax({
		  type: "GET",
		  url: "https://iapi.bot.or.th/Stat/Stat-ReferenceRate/DAILY_REF_RATE_V1/?start_period=2002-01-12&end_period=2002-01-15",
		  beforeSend: function(xhr) {
		        xhr.setRequestHeader(
		            'api-key','U9G1L457H6DCugT7VmBaEacbHV9RX0PySO05cYaGsm'
		        );
		  },
		 dataType: 'json',
	    success: function(dataOutput) {
	        //set your variable to the result 
	        //alert("Success:"+resultS.result.success);
	        //alert( resultS.result.data.data_header.report_name_th );
	         dataShow = " result Success:"+dataOutput.result.success+"<br/>";
	         dataShow += ",report th:"+dataOutput.result.data.data_header.report_name_th+"<br/>";
	         //error
	         //dataShow += ",data_detail start period:"+dataOutput.result.data.data_detail.period+"<br/>";
	         //dataShow += ",data_detail rate:"+dataOutput.result.data.data_detail.rate+"<br/>";
	         resultData.innerHTML = dataShow;
	    },
	    error: function(result) {
	        //handle the error 
	    	alert("error:"+result);
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
			   <input type="button" name="test" value="getBOTAPI" onclick="getBOTAPIJson()"/>
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