
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
							<td align="center"><b>�к��Ѿവ Sales App</b></td>
						</tr>
						<tr>
							<td align="left">�ҡ��ҹ����ͤ���  <font color="red">�к� SalesApp ����������ѹ����ش�Դ����ͷ����ͷӡ���Ѿവ"</font> ���ӡ�� �Ѿവ��������������ǹ �����Ҩ�������������١��ͧ��
							��ҹ����ö�Ѿവ���ͧ�·ӡ�â�鹵͹��ҧ��ҧ  ���ͷӡ�����������ͷշӡ���Ѿവ���
							</td>
						</tr>
						<tr>
							<td align="left">��鹵͹����Ѿവ Sales App </td>
						</tr>
						<tr>
							<td align="left">1 .�ӡ�����������Թ������   ����ͧ��� VPN </td>
						</tr>
					    <tr>
							<td align="left">2 .��ѧ�ҡ��鹡����� "�Ѿവ����� SalesApp" �к����ʴ�˹�Ҩ����ӡ�á��Ѿവ    �������Ѿവ����������Դ����� ���ѡ���� ���к��ӧҹ���º��������</td>
						</tr>
						 <tr>
							<td align="left">3.��ѧ�ҡ������ӧҹ�������� ����ѧࡵ��ͤ������ </td>
						</tr>
						<tr>
							<td align="left">3.1.�ҡ������ "�Ѿവ���º��������" �ʴ�����к��Ѿവ�����º��ͺ</td>
						</tr>
						 <tr>
							<td align="left">3.2.�ҡ������  "�������ö�Ѿവ��" �ʴ�����к��������ö�Ѿവ��  ���Դ��ͽ����ͷ� �����������ͷըѴ����Ѿവ���</td>
						</tr>
					    <tr>
							<td align="left">4. �ҡ������Դ Google Chrome �����Դ��������� �ӡ�� Login ��е�Ǩ�ͺ�������բ�ͤ����ʴ���͹��˹�Ҩ����ʹ�ҹ��ҧ</td>
						</tr>
					   </table>
					   
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
							   <a href="https://www.dropbox.com/s/z2z6904qm5x46s3/pensclient.war?dl=1">
								  <input type="button" name ="submit_download" 
								  value="��ǹ���Ŵ pensclient.war"  class="newPosBtn"> 
								</a>
								<!-- <a href="#">
								  <input type="submit" name ="submit_update"   class="newPosBtn"
								  value="�Ѿവ����� SalesApp" > 
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