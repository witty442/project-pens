<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui.min.js"></script>


<!--  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script>-->
<!--  <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>-->
<!--  <script src="http://digitalbush.com/wp-content/uploads/2007/02/jqueryprogressbar.js" type="text/javascript"></script>-->

<script>
/*
 * Copyright (c) 2007 Josh Bush (digitalbush.com)
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:

 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE. 
 */
 
/*
 * Progress Bar Plugin for jQuery
 * Version: Alpha 2
 * Release: 2007-02-26
 */ 
(function($) {	
	//Main Method
	$.fn.reportprogress = function(val,maxVal) {			
		var max=100;
		if(maxVal)
			max=maxVal;
		return this.each(
			function(){		
				var div=$(this);
				var innerdiv=div.find(".progress");
				
				if(innerdiv.length!=1){						
					innerdiv=$("<div class='progress'></div>");					
					div.append("<div class='text'>&nbsp;</div>");
					$("<span class='text'>&nbsp;</span>").css("width",div.width()).appendTo(innerdiv);					
					div.append(innerdiv);					
				}
				var width=Math.round(val/max*100);
				innerdiv.css("width",width+"%");	
				div.find(".text").html(width+" %");
			}
		);
	};
})(jQuery);
 
</script>

<style type="text/css">
/* progress bar container */
#progressbar{
        border:1px solid black;
        width:200px;
        height:20px;
        position:relative;
        color:black; 
}
/* color bar */
#progressbar div.progress{
        position:absolute;
        width:0;
        height:100%;
        overflow:hidden;
        background-color:#369;
}
/* text on bar */
#progressbar div.progress .text{
        position:absolute;
        text-align:center;
        color:white;
}
/* text off bar */
#progressbar div.text{
        position:absolute;
        width:100%;
        height:100%;
        text-align:center;
}
</style>


<script type="text/javascript" language="javascript">

var pct=0;
var handle=0;
function update(){
	     pct = pct +5;
        $("#progressbar").reportprogress(pct);
        if(pct==100){
            clearInterval(handle);
            $("#run").val("start");
            pct=0;
        }
}
//call ajax
window.onload =function(){
	<% if( "submited".equals(request.getParameter("action"))){ %>
	   var status = document.form1.status; 
	   if (status.value != "1" && status.value != "-1"){
		   update();
	       checkStatusProcess();
	   }
	<%} %>
}
function checkStatusProcess(){
   // alert($('#userName').val());
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/interfacesAjax.jsp",
			data : "id=<%=request.getParameter("id")%>",
			success: function(getData){
				var returnString = jQuery.trim(getData);
				document.form1.status.value = returnString;
				// $('#status').val(returnString);
			}
		}).responseText;   
	});
	setTimeout("checkStatus()",5000);
}

function checkStatus(){
	  var status =  document.form1.status.value;
	  //alert("checkStatus status:"+status);
	   if(status != '1' && status != "-1"){
         update();
         checkStatusProcess();
	   }else{
		 $("#progressbar").reportprogress(100);
		 window.close();
     }
}
</script>
<title>Test</title>
</head>
<body>

<form name="form1" action="progressBarPopup.jsp">

<div id="progressbar" style="width:600px"></div>

<input type="hidden" name="status" size="2"/><br/>
 
</form>


</body>
</html>