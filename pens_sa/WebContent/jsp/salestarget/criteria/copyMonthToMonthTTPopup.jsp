<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetTTCopyNMonth"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetCopyNMonth"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetUtils"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="util.Utils"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetForm"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetBean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>

<%
//Calc from current Time 
int prevMonth = 4;
int nextMonth = 4;
List<PopupBean> periodList =SalesTargetUtils.initPeriodAllYear(prevMonth,nextMonth);
%>

<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	<%-- <%if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("copy") ){ %>
	   document.getElementById("btn_copy").style.display ='none';
       document.getElementById("progress").style.display ='block';
    <%}%> --%>
}

function copy(path,e){
	var form = document.form1;
	
	if( $('#fromPeriod').val()==""){
		alert("กรุณากรอก From เดือน");
		return false;
	 } 
	if( $('#toPeriod').val()==""){
		alert("กรุณากรอก To เดือน");
		return false;
	 } 
	if( $('#toPeriod').val()==$('#fromPeriod').val()){
		alert("ไม่สามารถ กรอก From เดือน ,To เดือน เป็นช่วงเดือนเดียวกันได้");
		return false;
	}
	
	if(confirm('ยืนยัน Copy From '+$('#fromPeriod').val() +" to "+$('#toPeriod').val())){
		
		 //To disable f5
		   $(document).bind("keydown", disableF5);
		  $(function() {
			///$("#dialog").dialog({ height: 200,width:650,modal:true });
			  $.blockUI({ message: $('#dialog'), css: {left:'20%', right:'20%' ,top: '40%',height: '25%', width: '60%' } }); 
		   });  
		  
	     var param  = "&fromPeriod="+$('#fromPeriod').val();
	      param += "&fromStartDate="+$('#fromStartDate').val();
	      param += "&toPeriod="+$('#toPeriod').val();
	      param += "&toStartDate="+$('#toStartDate').val();
	      
	    form.action = path + "/jsp/salestarget/criteria/copyMonthToMonthTTPopup.jsp?action=copy"+param;
		form.submit();

	 //  var url = path + "/jsp/salestarget/criteria/copyMonthToMonthPopupAjax.jsp?"+param; 

	 /*  var saveData = $.ajax({
	          type: 'POST',
	          url: url,
	          data: '',
	          dataType: "text",
	          async:false,
	          success: function(resultData) { 
	        	  var msg = "Copy Success";
	        	  if("DATA_CUR_EXIST_EXCEPTION" ==resultData){
	        		  msg = "ข้อมูลเดือน "+$('#toPeriod').val() +" มีการ key ข้อมูลบางส่วนไปแล้ว";
	        	  }else if("DATA_CUR_EXIST_EXCEPTION" ==resultData){
	        		  msg = "ไม่พบข้อมูลเดือน "+$('#fromPeriod').val() +"";
	        	  }
	        	  
	        	  alert(msg) ;
	        	  document.getElementById("progress").style.visibilty ='none';
	      
	        	}
	    }); */
	  
	}
	return false;
}

function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
}
//To re-enable f5
$(document).unbind("keydown", disableF5);
 
function setPeriodDate(periodDesc,type){
	var form = document.form1;
	//alert(periodDesc);
	if('from'==type){
	  form.fromPeriod.value = periodDesc.value.split("|")[0];
	  form.fromStartDate.value = periodDesc.value.split("|")[1];
	  form.fromEndDate.value = periodDesc.value.split("|")[2]; 
    }else if('to'==type){
      form.toPeriod.value = periodDesc.value.split("|")[0];
  	  form.toStartDate.value = periodDesc.value.split("|")[1];
  	  form.toEndDate.value = periodDesc.value.split("|")[2]; 
    }
}
</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<form name="form1" method="post">

 <!-- Progress Bar -->
 <div id="dialog" title=" กรุณารอสักครู่......"  style="display:none">
 <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
    <tr>
		<td align="center" width ="100%">
		   <div style="height:50px;align:center">
		          กรุณารอสักครู่......
		   </div>
		  <div id="progress_bar" style="align:center">
              <img src="${pageContext.request.contextPath}/images2/waiting.gif" width="100" height="100" />
          </div>
		 </td>
   </tr>
  </table>   	      
</div>
 <!-- Progress Bar -->

<table align="center" border="0" cellpadding="3" cellspacing="0" >
	<tr>
		<td align="center" colspan="3"><b>Copy SalesTarget From Month To Month</b></td>
	</tr>
     <tr>
           <td> From เดือน <font color="red">*</font></td>
		<td>					
		     <select name="fromPeriodDesc" id ="fromPeriodDesc" onchange="setPeriodDate(this,'from')">
		       <option value="">Select Month</option>
		       <%for(int i=0;i<periodList.size();i++) {
		          PopupBean p = periodList.get(i);
		        %>
			      <option value="<%=p.getValue()%>"><%=p.getKeyName()%></option>
			   <%} %>
			</select>
			<input type="hidden" name="fromPeriod" id= "fromPeriod"/>
		</td>
		<td> 
		 <input type="text" name="fromStartDate" id= "fromStartDate" size="10" disabled/>
		 <input type="text" name="fromEndDate" id= "fromEndDate" size="10" disabled/>
		  </td>
	<td></td>
</tr> 
<tr>
    <td> To เดือน <font color="red">*</font></td>
		<td>					
		     <select name="toPeriodDesc" id ="toPeriodDesc" onchange="setPeriodDate(this,'to')">
		       <option value="">Select Month</option>
		       <%for(int i=0;i<periodList.size();i++) {
		          PopupBean p = periodList.get(i);
		        %>
			      <option value="<%=p.getValue()%>"><%=p.getKeyName()%></option>
			   <%} %>
			</select>
			<input type="hidden" name="toPeriod" id= "toPeriod"/>
		</td>
		<td> 
		 <input type="text" name="toStartDate" id= "toStartDate" size="10" disabled/>
		 <input type="text" name="toEndDate" id= "toEndDate" size ="10" disabled/>
		</td>
	<td></td>
</tr>
<tr>
	<td align="center" colspan="3">
	 <!-- Copy Month To Month -->
	   <div id="btn_copy" >
			<a href="javascript:copy('${pageContext.request.contextPath}',event)">
			  <input type="button" value="Copy Data" class="newPosBtnLong">
			</a>	
				
			<a href="javascript:window.close()">
			  <input type="button" value=" ปิดหน้าจอนี้ " class="newPosBtnLong">
			</a>	
	  </div>
	  <div id="progress" style="display: none;">
	      <%-- <img src="${pageContext.request.contextPath}/images2/waiting.gif" width="100px" height="100px"/> --%>
	  </div>
	</td>
</tr>
 </table>
	<%
	System.out.println("action:"+Utils.isNull(request.getParameter("action")));
	if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("copy") ){
		String fromPeriod = Utils.isNull(request.getParameter("fromPeriod"));
		String fromStartDate = Utils.isNull(request.getParameter("fromStartDate"));
		String toPeriod = Utils.isNull(request.getParameter("toPeriod"));
		String toStartDate = Utils.isNull(request.getParameter("toStartDate"));
		String msg = "Copy Success From Month["+fromPeriod+"] To Month["+toPeriod+"]";
		String returnStr = "";
		try{
			returnStr = SalesTargetTTCopyNMonth.copy(fromPeriod, fromStartDate, toPeriod, toStartDate);
			
      	  if("DATA_CUR_EXIST_EXCEPTION" ==returnStr){
      		  msg = "ข้อมูลเดือน "+toPeriod +" มีการ key ข้อมูลบางส่วนไปแล้ว";
      	  }else if("DATA_CUR_EXIST_EXCEPTION" ==returnStr){
      		  msg = "ไม่พบข้อมูลเดือน "+fromPeriod +"";
      	  }
      	  
      	 out.println("<script>");
      	 out.println("  alert('"+msg+"')");
      	 out.println("</script>");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	%>
</form>
</body>
</html>