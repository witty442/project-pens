
<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="util.AppversionVerify"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620" />
<title>Update SalesApp System</title>

<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" type="text/css" />
<%

%>
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<%
  if(request.getParameter("submit_update") != null){
	  //Get Folder for Software SalesAppUpdater  
	  AppversionVerify.getApp().downloadSalesAppUpdater(true); 
					   
	  //Start Software Update Sales App
	  AppversionVerify.startSalesAppUpdater();
  }

%>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td>
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- Body -->
	    	<!-- PROGRAM HEADER -->
	      	
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
		            
						<!-- BODY -->
						<form method="post">
						<jsp:include page="../error.jsp"/>
						
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="60%">
						<tr>
							<td align="center"><b>ระบบอัพเดต Sales App</b></td>
						</tr>
						<tr>
							<td align="left">หากท่านพบข้อความ  <font color="red">ระบบ SalesApp ไม่ใช่เวอร์ชันล่าสุดติดต่อไอทีเพื่อทำการอัพเดต"</font> ให้ทำการ อัพเดตเป็นเวอร์ชั่นใหม่ด่วน เพราะอาจทำให้ข้อมูลไม่ถูกต้องได้
							ท่านสามารถอัพเดตได้เองโดยทำการขั้นตอนข้างล่าง  หรือทำการแจ้งให้ฝ่ายไอทีทำการอัพเดตให้
							</td>
						</tr>
						<tr>
							<td align="left">ขั้นตอนการอัพเดต Sales App </td>
						</tr>
						<tr>
							<td align="left">1 .ทำการเชื่อมต่ออินเทอร์เน็ต   ไม่ต้องต่อ VPN </td>
						</tr>
					    <tr>
							<td align="left">2 .หลังจากนั้นกดปุ่ม "อัพเดตโปรแกรม SalesApp" ระบบจะแสดงหน้าจอให้ทำการกดอัพเดต    กดปุ่มอัพเดตในโปรแกรมที่เปิดขึ้นมา รอสักครู่ จนระบบทำงานเรียบร้อยแล้ว</td>
						</tr>
						 <tr>
							<td align="left">3.หลังจากโปรแกรมทำงานเสร็จแล้ว ให้สังเกตข้อความว่า </td>
						</tr>
						<tr>
							<td align="left">3.1.หากขึ้นว่า "อัพเดตเรียบร้อยแล้ว" แสดงว่าระบบอัพเดตได้เรียบร้อบ</td>
						</tr>
						 <tr>
							<td align="left">3.2.หากขึ้นว่า  "ไม่สามารถอัพเดตได้" แสดงว่าระบบไม่สามารถอัพเดตได้  ให้ติดต่อฝ่ายไอที เพื่อให้ฝ่ายไอทีจัดการอัพเดตให้</td>
						</tr>
					    <tr>
							<td align="left">4. จากนั้นให้ปิด Google Chrome แล้วเปิดขึ้นมาใหม่ ทำการ Login และตรวจสอบว่าไม่มีข้อความแสดงเตือนบนหน้าจอหรือด้านล่าง</td>
						</tr>
					   </table>
					   
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
							   <a href="https://www.dropbox.com/s/z2z6904qm5x46s3/pensclient.war?dl=1">
								  <input type="button" name ="submit_download" 
								  value="ดาวน์โหลด pensclient.war"  class="newPosBtn"> 
								</a>
								<!-- <a href="#">
								  <input type="submit" name ="submit_update"   class="newPosBtn"
								  value="อัพเดตโปรแกรม SalesApp" > 
								</a> -->
							</td>
						</tr>
					</table>
					
					</form>
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
   <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>