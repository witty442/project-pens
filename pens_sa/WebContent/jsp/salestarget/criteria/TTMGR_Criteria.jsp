<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="util.*"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetForm"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetBean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js"></script>
<%
SalesTargetBean bean = ((SalesTargetForm)session.getAttribute("salesTargetForm")).getBean();
User user = (User) request.getSession().getAttribute("user");
//String role = user.getRoleSalesTarget();

%>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.salesTargetForm;
	// new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
	
	//setDateMonth
	setPeriodDate(form.periodDesc);
	
	<%if( !"".equals(Utils.isNull(bean.getSalesChannelNo())) ) { %>
         document.getElementsByName('bean.salesChannelNo')[0].value = "<%=bean.getSalesChannelNo()%>";
         loadCustCatNoList();
	      document.getElementsByName('bean.custCatNo')[0].value = '<%=bean.getCustCatNo()%>';
	<% } %>
}

function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
}
//To re-enable f5
$(document).unbind("keydown", disableF5);

function clearForm(path){
	var form = document.salesTargetForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/salesTargetAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.salesTargetForm;
	 if( $('#periodDesc').val()==""){
		alert("กรุณากรอก เดือน");
		return false;
	 } 
	form.action = path + "/jsp/salesTargetAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}

function openEdit(path,salesZone,brand,custCatNo,mode){
	var form = document.salesTargetForm;
	var param  = "&salesZone="+salesZone;
	    param += "&brand="+brand;
	    param += "&custCatNo="+custCatNo+"&mode="+mode;
	form.action = path + "/jsp/salesTargetAction.do?do=prepare"+param;
	form.submit();
	return true;
}

function setPeriodDate(periodDesc){
	var form = document.salesTargetForm;
	//alert(periodDesc);
	form.period.value = periodDesc.value.split("|")[0];
	form.startDate.value = periodDesc.value.split("|")[1];
	form.endDate.value = periodDesc.value.split("|")[2]; 
}

function salesManagerFinish(path){
	var form = document.salesTargetForm;
	var r = checkCanFinish();
	//alert(r);
	if(r){
	    if(confirm("กรุณายืนยันการ อนุมัติเป้าหมายทุกแบรนด์")){
	    	/**Control Save Lock Screen **/
	    	startControlSaveLockScreen();
	    	
			form.action = path + "/jsp/salesTargetAction.do?do=salesManagerFinish";
			form.submit();
			return true;
	    }
	}else{
		alert("มีบางสถานะ ที่ไม่สามารถ อนุมัติ เป้าหมายได้  สถานะทั้งหมดต้องเป็น Accept");
		return false;
	}
	return false;
}

function checkCanFinish(){
	var r= true;
	var status = document.getElementsByName("status");
	
	//alert(status.length);
	for(var i=0;i<status.length;i++){
	  //alert("["+status[i].value+"]");
	  status[i].className ='disableText';
	  
	  document.getElementById("span_cust_cat_no_"+i).className='normalText';
	  document.getElementById("span_sales_zone_"+i).className='normalText';
	  document.getElementById("span_brand_"+i).className='normalText';
	  document.getElementById("span_brand_name_"+i).className='normalText';
	  document.getElementById("span_target_qty_"+i).className='normalText';
	  document.getElementById("span_target_amount_"+i).className='normalText';
	  document.getElementById("span_sales_target_qty_"+i).className='normalText';
	  document.getElementById("span_sales_target_amount_"+i).className='normalText';
	  if(status[i].value != 'Finish') { //Not check row finish
		  if(status[i].value != 'Accept') {
			 // alert(status[i].innerHTML);
			  r = false;
			  //break;
			  
			  status[i].className ='errorTextBold';
			  document.getElementById("span_cust_cat_no_"+i).className='errorTextBold';
			  document.getElementById("span_sales_zone_"+i).className='errorTextBold';
			  document.getElementById("span_brand_"+i).className='errorTextBold';
			  document.getElementById("span_brand_name_"+i).className='errorTextBold';
			  document.getElementById("span_target_qty_"+i).className='errorTextBold';
			  document.getElementById("span_target_amount_"+i).className='errorTextBold';
			  document.getElementById("span_sales_target_qty_"+i).className='errorTextBold';
			  document.getElementById("span_sales_target_amount_"+i).className='errorTextBold';
		  }//if
	  }//if
	}//for
	return r;
}
function unacceptRow(path,salesZone,brand,custCatNo,period,startDate,rowId){
	var status = document.getElementsByName("status")[rowId-1];
	var unacceptDiv = document.getElementsByName("unaccept_div")[rowId-1];
	var div_msg = document.getElementById("div_msg");
	//alert(unacceptDiv);
	
	var returnString = "";
	var param = "salesZone="+salesZone+"&brand="+brand;
	    param += "&custCatNo="+custCatNo+"&period="+period;
	    param += "&startDate="+startDate;
	if(confirm("ยืนยัน ไม่อนุมัติ เป้าหมายรายการนี้")){
		var getData = $.ajax({
				url: path+"/jsp/ajax/salesManagerTTUnacceptRowAjax.jsp",
				data : encodeURI(param),
				async: true,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		//set new status and hide unaccept Action
		status.value ="Unaccept";
		unacceptDiv.style.display = 'none'; 
		div_msg.style.display = 'block'; 
	}
}
function acceptRow(path,salesZone,brand,custCatNo,period,startDate,rowId){
	var status = document.getElementsByName("status")[rowId-1];
	var acceptDiv = document.getElementsByName("accept_div")[rowId-1];
	var div_msg = document.getElementById("div_msg");
	//alert(unacceptDiv);
	
	var returnString = "";
	var param  = "salesZone="+salesZone+"&brand="+brand;
        param += "&custCatNo="+custCatNo+"&period="+period;
        param += "&startDate="+startDate;
	if(confirm("ยืนยัน อนุมัติ เป้าหมายรายการนี้")){
		var getData = $.ajax({
				url: path+"/jsp/ajax/salesManagerTTAcceptRowAjax.jsp",
				data : encodeURI(param),
				async: true,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		//set new status and hide unaccept Action
		status.value ="Finish";
		acceptDiv.style.display = 'none'; 
		div_msg.style.display = 'block'; 
	}
}
</script>

 <!-- Progress Bar -->
 <%-- <div id="dialog" title=" กรุณารอสักครู่......"  style="display:none">
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
</div> --%>
 <!-- Progress Bar -->
 <div id="div_msg" style="display:none">
   <b><font size="2" color="red">บันทึกข้อมูลเรียบร้อยแล้ว</font></b>
 </div>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
	       <tr>
                <td> เดือน <font color="red">*</font></td>
				<td>					
					 <html:select property="bean.periodDesc" styleId="periodDesc" onchange="setPeriodDate(this)">
						<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
				    </html:select>
				     <html:hidden property="bean.period" styleId="period"/>
				</td>
				<td> 
				     <html:text property="bean.startDate" styleId="startDate" size="20" readonly="true" styleClass="disableText"/>
				        -
					<html:text property="bean.endDate" styleId="endDate" size="20" readonly="true" styleClass="disableText"/>
				</td>
				<td>							
				</td>
			</tr>
			<tr>
                <td> ประเภทขาย <font color="red"></font></td>
				<td colspan="2">
				    <html:select property="bean.custCatNo" styleId="custCatNo">
				      <html:options collection="CUSTOMER_CATEGORY_LIST" property="custCatNo" labelProperty="custCatDesc"/>
				    </html:select>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    ภาคตามสายดูแล
				    <html:select property="bean.salesZone" styleId="salesZone">
						<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
				    </html:select> 
				</td>
			</tr>	
	   </table>
	   <table  border="0" cellpadding="3" cellspacing="0" >
			<tr>
				<td align="left">
					<a href="javascript:search('${pageContext.request.contextPath}')">
					  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
					</a>
					<a href="javascript:clearForm('${pageContext.request.contextPath}')">
					  <input type="button" value="   Clear   " class="newPosBtnLong">
					</a>		
				</td>
			</tr>
		</table>