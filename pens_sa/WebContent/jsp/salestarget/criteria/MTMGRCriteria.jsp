<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	var form = document.salesTargetForm;
	// new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
	
	//setDateMonth
	setPeriodDate(form.periodDesc);
}

function clearForm(path){
	var form = document.salesTargetForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/salesTargetAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}

function search(path){
	var form = document.salesTargetForm;
	 if( $('#salesChannelNo').val()==""){
		alert("กรุณากรอก ภาคการขาย");
		return false;
	} 
	form.action = path + "/jsp/salesTargetAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}

function openView(path,id){
	var form = document.salesTargetForm;
	var param  ="&ids="+id;
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
	if(confirm("กรุณายืนยันการอนุมัติเป้าหมาย")){
		var r = checkCanFinish();
		//alert(r);
		if(r){
			form.action = path + "/jsp/salesTargetAction.do?do=salesManagerFinish";
			form.submit();
			return true;
		}else{
			alert("มีบางสถานะ ที่ไม่สามารถ อนุมัติ เป้าหมายได้");
			return false;
		}
	}
	return false;
}
function checkCanFinish(){
	var r= true;
	var status = document.getElementsByName("statusSpan");
	for(var i=0;i<status.length;i++){
	  if(status[i].innerHTML != 'Finish') { //Not check row finish
		  if(status[i].innerHTML != 'Accept') {
			  //alert(itemCode[i].value+","+status[i].value);
			  r = false;
			  break;
		  }
	  }
	}
	return r;
}
function unacceptRow(path,id,rowId){
	var status = document.getElementsByName("statusSpan")[rowId-1];
	var unacceptDiv = document.getElementsByName("unaccept_div")[rowId-1];
	//alert(unacceptDiv);
	
	var returnString = "";
	var param = "id="+id;
	if(confirm("ยืนยัน ไม่อนุมัติ เป้าหมายรายการนี้")){
		var getData = $.ajax({
				url: path+"/jsp/ajax/salesManagerUnacceptRowAjax.jsp",
				data : encodeURI(param),
				async: true,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		//set new status and hide unaccept Action
		status.innerHTML ="Unaccept";
		unacceptDiv.style.display = 'none'; 
	}
}
function acceptRow(path,id,rowId){
	var status = document.getElementsByName("statusSpan")[rowId-1];
	var acceptDiv = document.getElementsByName("accept_div")[rowId-1];
	//alert(unacceptDiv);
	
	var returnString = "";
	var param = "id="+id;
	if(confirm("ยืนยัน อนุมัติ เป้าหมายรายการนี้")){
		var getData = $.ajax({
				url: path+"/jsp/ajax/salesManagerAcceptRowAjax.jsp",
				data : encodeURI(param),
				async: true,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		//set new status and hide unaccept Action
		status.innerHTML ="Finish";
		acceptDiv.style.display = 'none'; 
	}
}
</script>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
	       <tr>
                <td> เดือน <font color="red">*</font></td>
				<td>					
					 <html:select property="bean.periodDesc" styleId="periodDesc" onchange="setPeriodDate(this)">
						<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
				    </html:select>
				     <html:hidden property="bean.period" styleId="period"/>
				</td>
				<td colspan="2"> 
				     <html:text property="bean.startDate" styleId="startDate" size="20" readonly="true" styleClass="disableText"/>
				        -
					<html:text property="bean.endDate" styleId="endDate" size="20" readonly="true" styleClass="disableText"/>
					&nbsp;&nbsp;&nbsp;&nbsp;
				      ภาคการขาย <font color="red">*</font>
				    <html:select property="bean.salesChannelNo" styleId="salesChannelNo">
						<html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
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