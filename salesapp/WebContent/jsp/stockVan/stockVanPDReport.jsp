<%@page import="java.net.InetAddress"%>
<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.pens.util.EncyptUtils"%>
<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<%
  String role = ((User)session.getAttribute("user")).getType();
  User user = (User)session.getAttribute("user");
  
  /** No of menu **/
  int no = 0;
  int subNo = 0;
  EnvProperties env = EnvProperties.getInstance();

 // System.out.println("Role:"+user.getRole().getKey());
  
  String userName =user.getUserName();
  String password=user.getPassword();
  
  String contextPathServerCross = "/pens_sa";
  String ipServerCross = env.getProperty("ftp.ip.server");
  String hostServerCross = "http://"+ipServerCross+":8081";
  
  String currentIP =InetAddress.getLocalHost().getHostAddress();
  //System.out.println("Current IP:"+currentIP);

  //case Server Test contextPath = pens_sa_test
	if("192.168.202.8".equals(currentIP)){ //On Witty dev
		 contextPathServerCross ="/pens_sa";
	     hostServerCross = "http://localhost:8080";
	 }else{
		 if("production".equalsIgnoreCase(env.getProperty("config.type"))){
			 contextPathServerCross ="/pens_sa"; //ON PRODUCTION SERVER
			 hostServerCross = "http://"+ipServerCross+":8081";
		 }else{
			 contextPathServerCross ="/pens_sa_test"; //ON UAT SERVER DD_SERVER
			 hostServerCross = "http://192.168.202.7:8082"; 
			 
			 //test now on local
			 //contextPathServerCross ="/pens_sa";
			 //hostServerCross = "http://192.168.37.185:8081";
		 }//if
	 }//if
  
  System.out.println("hostServerCross:"+hostServerCross);
  System.out.println("currentIP:"+currentIP);
%>

<script>
function link(url){
	var returnString = "";
	document.getElementById("url").value = url;
	
	/**Control Test URL Conn Lock Screen **/
	startControlTestURLConnLockScreen("stockVanPDReport");
	
	//check vpn is connected
	 $(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/checkVPNConnected.jsp",
			data :"",
			async: true,
			success: function(getData){
				returnString = jQuery.trim(getData);
			}
		}).responseText;
	}); 
 }
function linkSub(){
	linkModelSub(document.getElementById("url").value);
}

 <!--Production -->
 function linkModelSub(url){
	  var newUrl ;
 	  var pathRedirect = url.substring(url.indexOf("jsp")-1,url.length);
	      pathRedirect = ReplaceAll (pathRedirect , '&', '$');
	         //alert(pathRedirect);
	         
 	   var newUrl = "<%=hostServerCross%><%=contextPathServerCross%>/login.do?do=loginCrossServer&pathRedirect="+pathRedirect;
 	   url = newUrl+"&userName=<%=userName%>&password=<%=EncyptUtils.base64encode(password)%>";
 	   PopupCenter(encodeURI(url),"Stock Van Sales",700,500);
  }

 function loadMe(){

 }
function refreshPage(){
	window.location = document.getElementById("path").value+"/jsp/stockVan/stockVanPDReport.jsp";
}

</script>

</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="StockVanPDReport"/>
			</jsp:include>
			
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
						 <div align="center">
							<table  border="0" cellpadding="3" cellspacing="0" >
							  <tr>
								<td align="center"  colspan="2">
								  <font size="3">
								    <b><bean:message key="StockVanPDReport" bundle="sysprop"/></b>
								  </font>
								</td>
								</tr>
								<tr>
									<td align="left" colspan="2">&nbsp;</td>
								</tr>
								<tr>
								   <td align="right">
								           <b>รายงานปิดรอบ Van Sales</b>
								   </td>
									<td align="left">
						            	<a href="#" onclick="javascript:link('<%=contextPathServerCross%>/jsp/stockAction.do?do=prepareSearch&pageName=STOCK_CLOSE_VAN&action=new');">
						                    <input type="button" value="รายงานปิดรอบ Van Sales" class="newPosBtnLong" style="width: 250px;"> 
								        </a>
									</td>
								</tr>
								<tr>
									<td align="left"  colspan="2">&nbsp;</td>
								</tr>
								<tr>
								    <td align="right">
								           <b>รายงานปิดรอบ PD</b>
								   </td>
									<td align="left">
						            	<a href="#" onclick="javascript:link('<%=contextPathServerCross%>/jsp/stockAction.do?do=prepareSearch&pageName=STOCK_CLOSEPD_VAN&action=new');">
						                    <input type="button" value="รายงานปิดรอบ PD" class="newPosBtnLong" style="width: 250px;"> 
								        </a>
									</td>
								</tr>
							</table>
							<br/>
				             <font color="blue" size="6"><b>!!!  รายงานนี้ จะต้องเชื่อมต่อ VPN ก่อนทุกครั้ง  !!!</b></font>
				             <br/>
				              <font color="red" size="6"><b> <span id="vpn_text"></span></b></font>
			             </div>
			             <!-- Hidden field -->
			              <input type="hidden" name="url" id="url" />
					    <!-- BODY -->
					   
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
 <!-- Control Save Lock Screen -->
<jsp:include page="../controlTestURLConnLockScreen.jsp" flush="true"/> 
<!-- Control Save Lock Screen -->

