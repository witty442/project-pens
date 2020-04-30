
<%@page import="com.pens.util.DBConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.web.itmanage.ITStockDAO"%>
<%@page import="com.isecinc.pens.web.itmanage.ITManageBean"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%
User user = (User)session.getAttribute("user");
String action = Utils.isNull(request.getParameter("action"));
List<ITManageBean> itemNameList = (List<ITManageBean>)session.getAttribute("ITEM_NAME_LIST");
String itemNameChange = "";
String msg ="";
String errorMsg = "";
String seq = "";

System.out.println("action:"+action);
Connection conn = null;
try{
	if(action.equalsIgnoreCase("new")){
		
	}else if(action.equalsIgnoreCase("save")){
		conn = DBConnection.getInstance().getConnectionApps();
		String itemName = Utils.isNull(request.getParameter("itemName"));//No Chnage PK
		itemNameChange = Utils.isNull(request.getParameter("itemNameChange"));
		seq = request.getParameter("seq");
		
		System.out.println("itemNameChange:"+itemNameChange);
		System.out.println("seq:"+seq);
		
		ITManageBean bean = new ITManageBean();
		bean.setItemName(itemNameChange);
		bean.setSeq(Utils.convertToInt(seq));
		
		if(itemName.equals("")){//Case add new Item
			itemName = itemNameChange;
		}
		
		int r= ITStockDAO.updateITMaster(conn, itemName, bean); 
		System.out.println("update:"+r);
		if(r==0){
		  ITStockDAO.insertITMaster(conn, bean);
		}
		//Reinit itemNameList
		//request.getSession().setAttribute("ITEM_NAME_LIST", ITStockDAO.initItemList());
		itemNameList = (List<ITManageBean>)session.getAttribute("ITEM_NAME_LIST");
		msg ="บันทึกข้อมูลเรียบร้อยแล้ว <br/>";
	}

}catch(Exception e){
	e.printStackTrace();
	errorMsg ="ไม่สามารถบันทึกข้อมูลเรียบร้อยแล้ว <br/> "+e.getMessage();
}finally{
	if(conn != null){
	   conn.close();
	}
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript">
 function loadMe(){
	 document.getElementById("itemName").value ='<%=Utils.isNull(itemNameChange)%>';
	 document.getElementById("itemNameChange").value ='<%=Utils.isNull(itemNameChange)%>';
	 document.getElementById("seq").value ='<%=Utils.isNull(seq)%>';
	 
 }
 function changeItemName(){
	var itemName = document.getElementById("itemName");
	if(itemName.value != ''){
		var itemNameText = itemName.options[itemName.selectedIndex].text;
		//seq-itemName
		var itemNameTextArr = itemNameText.split("-");
		document.getElementById("seq").value =itemNameTextArr[0];
		document.getElementById("itemNameChange").value =itemNameTextArr[1];
	}else{
		document.getElementById("seq").value ='';
		document.getElementById("itemNameChange").value ='';
	}
}
 
 function addItemName(path){
	 var newItemName = prompt("เพิ่มชื่อ อุปกรณ์", "");
	 if (newItemName != null) {
	     document.getElementById("itemNameChange").value =newItemName;
	 }
	 
    //document.frmAdd.action = path + "/jsp/itmanage/manageITMaster.jsp?action=add";
	//document.frmAdd.submit();
}
 
 function save(path){
	var itemNameChange = document.getElementById("itemNameChange");
	if(itemNameChange.value ==""){
		alert('กรุณาระบุ ชื่ออุปกรณ์');
		itemNameChange.focus();
		return false;
	}
	document.frmAdd.action = path + "/jsp/itmanage/manageITMaster.jsp?action=save";
	document.frmAdd.submit();
 }
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe('${pageContext.request.contextPath}');MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">

	<!-- BODY -->
	<form name="frmAdd" method="post" action="manageITMaster.jsp?action=save"> 
	<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	    <tr>
			<td width="100%" colspan="2" align="center">
			<font size="1" color='green'><%=msg %></font>
			<font size="1" color='red'><%=errorMsg %></font>
			<font size="2"><b>
			          เพิ่ม/แก้ไข  รายชื่ออุปกรณ์ 
			  </b>
			</font>
			</td>
		</tr>
		<tr>
			<td width="30%">&nbsp;</td>
			<td width="70%"></td>
			
		</tr>
		<tr>
			<td align="right"><b>อุปกรณ์ :</b></td>
			<td align="left">
			  <select name="itemName" id="itemName" onchange="changeItemName()">
			     <option ></option>
			     <%for(int i=0;i<itemNameList.size();i++){ 
			    	 ITManageBean item = itemNameList.get(i);
			    	 if(item.getItemName().equalsIgnoreCase(itemNameChange)){
			    	   out.println("<option value='"+item.getItemName()+"' selected>"+item.getSeq()+"-"+item.getItemName()+"</option>");
			    	 }else{
			           out.println("<option value='"+item.getItemName()+"'>"+item.getSeq()+"-"+item.getItemName()+"</option>");
			    	 }
			     }
			     %>
			   </select>
			   (เลือกรายการที่ต้องการเปลี่ยนแปลง)
			</td>
		</tr>
		<tr>
			<td align="right" nowrap><b>ชื่ออุปกรณ์:</b></td>
			<td align="left">
			  <input type="text" name="itemNameChange" id="itemNameChange"  autoComplete='off'/>
			</td>
		</tr>
		<tr>
			<td align="right" nowrap><b>Seq:</b></td>
			<td align="left">
			  <input type="text" name="seq" id="seq" autoComplete='off'/>
			</td>
		</tr>
	</table>
	<br>
	<!-- BUTTON -->
	<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
		<tr>
			<td align="center">
				<a href="#" onclick="return save('${pageContext.request.contextPath}');">
				  <input type="button" value="  บันทึก   " class="newPosBtn"/>
				</a>
				 
				<a href="#" onclick="window.close()">
				<input type="button" value="ปิดหน้าจอ" class="newNegBtn"/>
				</a>
				<%-- &nbsp;&nbsp;
				  <a href="#" onclick="return addItemName('${pageContext.request.contextPath}');">
				  <input type="button" value="เพิ่มรายการใหม่" class="newPosBtn"/>
				</a> --%>
			</td>
		</tr>
	</table>

	</form>
	<!-- BODY -->
				
</body>
</html>