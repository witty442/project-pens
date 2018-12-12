<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<script>
function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
}

function search(path){
	var form = document.shopForm;
   var startDate = form.startDate;
   var endDate = form.endDate;
   
   if(startDate.value =="" && endDate.value =="" ){
	   startDate.focus();
	   alert("กรุณากรอกข้อมูลในการค้นหาอย่างน้อยหนึ่งรายการ");
	   return false;
   }
   form.action = path + "/jsp/shopAction.do?do=search&pageName=<%=request.getParameter("pageName")%>";
   form.submit();
   return true;
}
</script>
<table  border="0" cellpadding="3" cellspacing="0" class="body" width="50%">
   <tr>
	<td align="right" width="10%"> จาก วันที่ขาย</td>
	<td align="left" width="20%">
	<html:text property="bean.startDate" styleId="startDate" readonly="true" size="10"/>
	&nbsp;&nbsp;ถึง วันที่ขาย&nbsp;&nbsp;
    <html:text property="bean.endDate" styleId="endDate" readonly="true" size="10"/>
	</td>
  </tr>
   <tr>
	<td align="right" width="10%"> Group Code</td>
	<td align="left" width="20%">
	<html:text property="bean.groupCode" styleId="groupCode" maxlength="6"  size="10" styleClass="\" autoComplete=\"off"/>
	</td>
  </tr>
   <tr>
	<td align="right" width="10%"> รูปแบบ</td>
	<td align="left" width="20%">
	 <html:select property="bean.reportType">
	   <html:option value="DETAIL">Detail</html:option>
	   <html:option value="SUMMARY">Summary</html:option>
	 </html:select>
	</td>
  </tr>
</table>