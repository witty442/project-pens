
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.web.order.OrderErrorBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
OrderErrorBean orderErrorBean = null;
String action= Utils.isNull(request.getParameter("action"));
System.out.println("action:"+action);

if(session.getAttribute("ORDER_ERROR") != null){ 
	orderErrorBean = (OrderErrorBean)session.getAttribute("ORDER_ERROR"); 
}

if("delete".equalsIgnoreCase(action)){
	if(orderErrorBean!= null){ 
	  for(int i=0;i<orderErrorBean.getStoreErrorList().size();i++){
		 String storeCode = orderErrorBean.getStoreErrorList().get(i);
		 String orderNo =  Utils.isNull(request.getParameter("radio_"+storeCode));
		 System.out.println("orderNo:"+orderNo);
	  }//for
	}//if
}
%>
<html>
<head>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />

<script type="text/javascript">
 function deleteOrder(){
	 //validate Choose OrderNo
	 <%
		if(orderErrorBean!= null){ 
			 for(int i=0;i<orderErrorBean.getStoreErrorList().size();i++){
				 String storeCode = orderErrorBean.getStoreErrorList().get(i);
	%>		
	           var  radioStore1 = document.getElementsByName("radio_<%=storeCode%>")[0];
	           var  radioStore2 = document.getElementsByName("radio_<%=storeCode%>")[1];
	           //alert(radioStore1.checked+":"+radioStore2.checked);
	           if(!radioStore1.checked && !radioStore2.checked){
	        	   alert("กรุณาเลือก Order ที่ต้องการลบอย่างน้อย 1 รายการ");
	        	   radioStore1.focus();
	        	   return false;
	           }
    <%
			 }
		}
	 %>
	 document.orderErrorForm.submit();
	// document.getElementById("orderErrorForm").submit();
 }
 function closeWindow(){
	 window.opener.unblockUI();
	 window.close();
 }
</script>
</head>
<body>  

<form method ="POST" id="orderErrorForm" action="<%=request.getContextPath()%>/jsp/order/orderError.jsp?action=delete"> 
<div align="center">
	<h2><font color="red">พบข้อมูล Order ซ้ำ <!-- กรุณาเลือกลบข้อมูลออกอย่างน้อยหนึ่งรายการ --> โปรดติดต่อไอที เพื่อทำการลบข้อมูล ก่อนทำรายการต่อไป</font></h2>
<!-- 	<input type="text" name="orderNoDelAll" value=""/> -->
	<% 
	if(orderErrorBean != null){ 
	    out.println(orderErrorBean.getResultError());
	} 
	%>
	<br/>
	<!--  <input type="button" name="submit" value ="ลบข้อมูล" onclick="deleteOrder()" /> -->
	 <input type="button" name="close" value ="ปิดหน้าจอนี้(เพื่อ Unblock หน้าจอหลัก)" 
	 class="newPosBtnLong" onclick="closeWindow()"/> 
 </div>
</form>  

</body>
</html>