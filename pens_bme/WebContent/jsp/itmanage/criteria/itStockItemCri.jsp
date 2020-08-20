<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('docDate'));
}

function clearForm(path){
	var form = document.itManageForm;
	form.action = path + "/jsp/itManageAction.do?do=clearHead";
	form.submit();
	return true;
}

function search(path){
	var form = document.itManageForm;
	
	form.action = path + "/jsp/itManageAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.itManageForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/itManageAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}
function newDoc(path){
	 var form = document.itManageForm;
	var param ="&id=0";
	form.action = path + "/jsp/itManageAction.do?do=prepare&mode=add"+param;
	form.submit();
	return true; 
}

function exportAll(path){
	 var form = document.itManageForm;
	form.action = path + "/jsp/itManageAction.do?do=exportAll&";
	form.submit();
	return true; 
}

function openEdit(path,id){
	 var form = document.itManageForm;
	var param ="&id="+id;
	form.action = path + "/jsp/itManageAction.do?do=prepare&mode=edit"+param;
	form.submit();
	return true; 
}

function openCopy(path,id){
	 var form = document.itManageForm;
	var param ="&id="+id;
	form.action = path + "/jsp/itManageAction.do?do=prepare&mode=copy"+param;
	form.submit();
	return true; 
}

	
/* function printReport(path,docNo){
   var url = path+"/jsp/popup/printPayPopup.jsp?report_name=PayInReport&docNo="+docNo;
   //, "Print2", "width=800,height=400,location=No,resizable=No");
	PopupCenter(url,'Printer',800,350);
} */

function openPopup(path,pageName){
	var form = document.itManageForm;
	var param = "&hideAll=true&pageName="+pageName;
	 if("SalesrepSalesAll" == pageName){
		param += "&selectone=true";
	}
	url = path + "/jsp/popupAction.do?do=prepareAll&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,desc2,desc3,pageName){
	var form = document.itManageForm;
	if('SalesrepSalesAll' == pageName){
		form.salesrepCode.value = code;
		form.salesrepFullName.value = desc;
		form.zone.value = desc2;
		form.zoneName.value = desc3;
	}
} 
function getAutoOnblur(e,obj,pageName){
	var form = document.itManageForm;
	if(obj.value ==''){
		if("SalesrepSalesAll" == pageName){
			form.salesrepCode.value = '';
			form.salesrepFullName.value = "";
			form.zone.value = "";
			form.zoneName.value = "";
		}
	}else{
		getAutoDetail(obj,pageName);
	}
}
function getAutoKeypress(e,obj,pageName){
	var form = document.itManageForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("SalesrepSalesAll" == pageName){
				form.salesrepCode.value = '';
				form.salesrepFullName.value = "";
				form.zone.value = "";
				form.zoneName.value = "";
			}
		}else{
			getAutoDetail(obj,pageName);
		}
	}
}

function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.itManageForm;
	
	//prepare parameter
	var param = "";
	if("SalesrepSalesAll"==pageName){
		param  ="pageName="+pageName;
		param +="&salesrepCode="+obj.value;
	}
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getAutoKeypressAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if("SalesrepSalesAll" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.salesrepCode.value = retArr[1];
			form.salesrepFullName.value = retArr[2];
			form.zone.value = retArr[3];
			form.zoneName.value = retArr[4];
		}else{
			alert("ไม่พบข้อมูล");
			form.salesrepCode.focus();
			form.salesrepCode.value = '';
			form.salesrepFullName.value = "";
			form.zone.value = "";
			form.zoneName.value = "";
		}
	}
}
function editItemMaster(path) {
    var url = path + "/jsp/itmanage/manageITMaster.jsp?action=new";
    PopupCenter(url, "", 600, 300) ;
}

</script>
		
	   <div align="center">
	    <table align="center" border="0" cellpadding="3" cellspacing="0" >
			<tr>
	            <td align="right">พนักงานขาย</td>
				<td>
				    <html:text property="bean.salesrepCode" styleId="salesrepCode" size="10" 
				    styleClass="\" autoComplete=\"off" 
				    onkeypress="getAutoKeypress(event,this,'SalesrepSalesAll')"
				    onblur="getAutoOnblur(event,this,'SalesrepSalesAll')"/>
				     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','SalesrepSalesAll')"/>   
				     <html:text property="bean.salesrepFullName" styleId="salesrepFullName" styleClass="disableText" readonly="true" size="40"/>
						&nbsp;&nbsp;&nbsp;
					วันที่ทำรายการ 
					<html:text property="bean.docDate" styleId ="docDate"  size="10" styleClass="\" autoComplete=\"off" />
				</td>
			</tr>	
			<tr>
                 <td> ภาคตามการดูแล<font color="red"></font></td>
				<td>		
					<html:text property="bean.zoneName" styleId ="zoneName" styleClass="disableText" readonly="true" size="50"></html:text>
				   <html:hidden property="bean.zone" styleId ="zone" />
				
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				ประเภทเอกสาร:
				<html:select property="bean.docType">
				  <html:option value=""></html:option>
				  <html:option value="Requisition">เบิก</html:option>
				  <html:option value="Return">คืน</html:option>
				</html:select>
				</td>
			</tr>
	   </table>
	   
	   <table  border="0" cellpadding="3" cellspacing="0" >
			<tr>
				<td align="left">
				   
					<a href="javascript:search('${pageContext.request.contextPath}')">
					  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
					</a> &nbsp; &nbsp;	
					<a href="javascript:newDoc('${pageContext.request.contextPath}')">
					  <input type="button" value="    เพิ่มรายการใหม่      " class="newPosBtnLong"> 
					</a> &nbsp; &nbsp;	
					<a href="javascript:clearForm('${pageContext.request.contextPath}')">
					  <input type="button" value="   Clear   " class="newPosBtnLong">
					</a>&nbsp; &nbsp;	
					<a href="javascript:exportAll('${pageContext.request.contextPath}')">
					  <input type="button" value=" Export ข้อมูลการเบิกล่าสุดของทุกหน่วย " class="newPosBtnLong"> 
					</a>&nbsp; &nbsp;	
					<%-- <a href="javascript:editItemMaster('${pageContext.request.contextPath}')">
					     <input type="button" name="addItemMaster"
					      value="เพิ่ม/แก้ไข ลิสรายชื่ออุปกรณ์"  class="newPosBtnLong"/>
					 </a> --%>
		           			
				</td>
			</tr>
		</table>
  </div>
