<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.pens.util.*"%>
<!-- Execute delete db(sales-out) by fileName -->
 <% 
 User user = (User) request.getSession().getAttribute("user");
 if("admin".equalsIgnoreCase(user.getUserName())){ 
  String tableName = "",topicName="";
 if("king".equalsIgnoreCase(request.getParameter("page")) 
 	|| "bigC".equalsIgnoreCase(request.getParameter("page"))
 	|| "tops".equalsIgnoreCase(request.getParameter("page"))
 	|| "ImportExcelSalesOutLotus".equalsIgnoreCase(request.getParameter("pageName"))
 	|| "ImportSalesOutBigCFromText".equalsIgnoreCase(request.getParameter("pageName"))
	) {
 	
 	 if("king".equalsIgnoreCase(request.getParameter("page"))) {
 		 tableName = "PENSBI.PENSBME_SALES_FROM_KING";
 		 topicName = "Sales-Out Duty Free";
 	 }else  if("bigC".equalsIgnoreCase(request.getParameter("page"))) {
 		 tableName = "PENSBI.PENSBME_SALES_FROM_BIGC";
 		 topicName = "Sales-Out BigC";
 	 }else  if("tops".equalsIgnoreCase(request.getParameter("page"))) {
 		 tableName = "PENSBI.PENSBME_SALES_FROM_TOPS";
 		 topicName = "Sales-Out Tops";
 	 }else  if("ImportExcelSalesOutLotus".equalsIgnoreCase(request.getParameter("pageName"))) {
 		 tableName = "PENSBI.PENSBME_SALES_FROM_LOTUS";
 		 topicName = "Sales-Out Lotus";
 	}else  if("ImportSalesOutBigCFromText".equalsIgnoreCase(request.getParameter("pageName"))) {
		 tableName = "PENSBI.PENSBME_SALES_FROM_BIGC_TEMP";
		 topicName = "Sales-Out BigC(รายวัน-Temp)";
	 }
 %>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
<tr>
<td align="center"><b>ลบข้อมูล <%=topicName %></b> (Table :<%=tableName %>)</td>
 </tr>
 <tr>
<td align="center">
  ระบุชื่อ ไฟล์ 
 <input type="text" name ="fileNameTxt" id ="fileNameTxt" 
   value="" autocomplete="off" size="30"/><font color="red">*</font>
    <a href="#" onclick="deleteDB('${pageContext.request.contextPath}')">
<input type="button" name ="delBtn" value="ลบข้อมูล" class="newNegBtn">
</a>
<input type="hidden" name ="tableNameTxt" id ="tableNameTxt" 
 value="<%=tableName%>" />
</td>
 </tr>
  </table>
  
  <script>
function deleteDB(path){
	var returnString = "";
	if(document.getElementById("fileNameTxt").value ==''){
		alert("กรุณาระบชื่อไฟล์ ที่ต้องการลบ")
		document.getElementById("fileNameTxt").focus();
		return false;
	}
	var param =  " delete from "+document.getElementById("tableNameTxt").value;
	    param += " where file_name ='" +document.getElementById("fileNameTxt").value+"'"; 
	
    if(confirm("ยืนยัน ต้องการที่จะลบข้อมูล นี้ ไฟล์ :"+document.getElementById("fileNameTxt").value)){
		$(function(){
			var getData = $.ajax({
					url: path+"/jsp/ajax/deleteDBBySqlAjax.jsp",
					data : "delSQL=" +encodeURI(param),
					async: false,
					cache: false,
					success: function(getData){
						returnString = jQuery.trim(getData);
					}
				}).responseText;
		});
		
		if(returnString != -1){
			alert("ลบข้อมูลสำเร็จ ของไฟล์:"+document.getElementById("fileNameTxt").value+" จำนวนรายการที่ลบ :"+returnString);
		}else{
			alert("ไม่สามารถ ลบข้อมูลได้โปรดตรวจสอบ กับฝ่ายไอที");
		}
	}
}
</script>
<%}} %>