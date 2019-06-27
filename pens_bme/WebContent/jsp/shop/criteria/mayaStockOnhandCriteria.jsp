<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<script>
function loadCalendar(){
	//new Epoch('epoch_popup', 'th', document.getElementById('asOfDate'));
	 $('#asOfDate').calendarsPicker({calendar: $.calendars.instance('thai','th')});
}
function loadMe(){}
function search(path){
	var form = document.shopForm;
	var asOfDate = form.asOfDate; 
	if(asOfDate.value ==""){
		 asOfDate.focus();
		 alert("กรุณากรอก วันที่ขาย (As Of Date)");
		 return false;
	}
	form.action = path + "/jsp/shopAction.do?do=search&pageName=<%=request.getParameter("pageName")%>";
	form.submit();
	return true;
}

function openPopup(path,pageName,fieldName,multipleCheck){
	var form = document.shopForm;
	var param = "&pageName="+pageName;
	    param +="&fieldName="+fieldName;
        param +="&groupStore="+form.custGroup.value;
        param +="&groupStoreName=MAYA&hideAll=true";
        
	url = path + "/jsp/popupSearchAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName,fieldName){
	var form = document.shopForm;
	document.getElementById(fieldName).value = code;
} 
</script>
<table  border="0" cellpadding="3" cellspacing="0" width="50%">
<tr>
	<td align="right" width="10%"> Shop</td>
	<td align="left" width="6%"><html:text property="bean.custGroup" styleId="custGroup" styleClass="disableText" readonly="true" size="15"/></td>
	<td align="right" width="6%"> </td>
	<td align="left" width="10%"></td>
  </tr>
 <tr>
	<td align="right" width="10%"> วันที่ขาย (As Of Date)<font color="red">*</font></td>
	<td align="left" width="6%"><html:text property="bean.asOfDate" styleId="asOfDate" readonly="false" size="15" styleClass="\" autoComplete=\"off"/></td>
	<td align="right" width="6%"> </td>
	<td align="left" width="10%"></td>
  </tr>
   <tr>
	<td align="right" width="10%"> Group</td>
	<td align="left" width="6%">
	<html:text property="bean.groupCodeFrom" styleId="groupCodeFrom" size="15" maxlength="6"/>
	 <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_GroupCode','groupCodeFrom','false')"/>   
	</td>
	<td align="right" width="6%"> </td>
	<td align="left" width="10%">   
	</td>
  </tr>
  <tr>
	<td align="right" width="10%"> From Style</td>
	<td align="left" width="6%"><html:text property="bean.styleFrom" styleId="styleFrom"size="15"/>
	 <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_Style','styleFrom','false')"/>   
	</td>
	<td align="right" width="6%">To Style</td>
	<td align="left" width="10%"><html:text property="bean.styleTo" styleId="styleTo" size="15"/>
	<input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_Style','styleTo','false')"/> 
	</td>
  </tr>
  <tr>
	<td align="right" width="10%"> From Pens Item</td>
	<td align="left" width="6%"><html:text property="bean.pensItemFrom" styleId="pensItemFrom" size="15"/>
	<input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_PensItem','pensItemFrom','false')"/> 
	</td>
	<td align="right" width="6%">To Pens Item</td>
	<td align="left" width="10%"><html:text property="bean.pensItemTo" styleId="pensItemTo"  size="15"/>
	<input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_PensItem','pensItemTo','false')"/> 
	</td>
  </tr>
</table>