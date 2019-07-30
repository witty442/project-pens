<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%@page import="com.isecinc.pens.SystemProperties"%>
<%
 User userDefault = new MUser().getCurrentUserName();
String userName = Utils.isNull(userDefault.getUserName());
String password = Utils.isNull(userDefault.getPassword()); 

/* String userName ="";
String password =""; */
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	background-repeat: repeat;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript">
<!--
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->

function gologin(e){
	if(e == null || (e != null && e.keyCode == 13)){
		login('${pageContext.request.contextPath}');
	}
}
function detectmob() { 
	 if( navigator.userAgent.match(/Android/i)
	 || navigator.userAgent.match(/webOS/i)
	 || navigator.userAgent.match(/iPhone/i)
	 || navigator.userAgent.match(/iPad/i)
	 || navigator.userAgent.match(/iPod/i)
	 || navigator.userAgent.match(/BlackBerry/i)
	 || navigator.userAgent.match(/Windows Phone/i)
	 ){
	    return true;
	 }else {
	    return false;
	 }
}
	
function login(path){
	var w = 0;
	var h = 0;
   if(detectmob()){
     //alert("Mobile");
	  var ratio = window.devicePixelRatio || 1;
	   
	  w = window.innerWidth * ratio;
	  h = window.innerHeight * ratio;
   }else{
     w = screen.width;
     h = screen.height;
   }
	//alert(w+":"+h);
	document.getElementsByName('screenWidth')[0].value = w;
	document.getElementsByName('screenHeight')[0].value = h;
	
   if(Trim(document.getElementsByName('userName')[0].value)==''){
       document.getElementsByName('userName')[0].focus();
       return false;
   }
   if(Trim(document.getElementsByName('password')[0].value)==''){
   	document.getElementsByName('password')[0].focus();
       return false;
   }
   document.loginForm.action=path+"/login.do?do=login";
   document.loginForm.submit();
   return true;
}
</script>
</head>
<body onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_login2.png','${pageContext.request.contextPath}/images2/button_forgotpwd2.png')" topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0">
<table width="939" height="517" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
  <tr>
    <td background="${pageContext.request.contextPath}/images2/loginbox_all.png" valign="top">
    	<table width="939" border="0" align="center" cellpadding="0" cellspacing="0">
      	<tr>
        	<td width="61">&nbsp;</td>
       	 	<td width="355" valign="top">
       	 		<html:form action="/login" onsubmit="return false;">
       	 		<table width="355" border="0" cellspacing="0" cellpadding="0">
		            <tr>
		              <td colspan="5"><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="50" /></td>
		            </tr>
		            <tr>
		              <td colspan="5"><img src="${pageContext.request.contextPath}/images2/logo_salesystem.gif" width="303" height="38" /></td>
		            </tr>
		            <tr>
		              <td width="103">&nbsp;</td>
		              <td width="94">&nbsp;</td>
		              <td colspan="3">&nbsp;</td>
		            </tr>
		            <tr>
		              <td>
		              	<bean:message key="User.UserName" bundle="sysele"/>
		              </td>
		              <td>
		              	<bean:message key="User.Password" bundle="sysele"/>
		              </td>
		              <td width="50" rowspan="3">
		              	<a href="#" onclick="login('${pageContext.request.contextPath}');" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image1','','${pageContext.request.contextPath}/images2/button_login2.png',1)"><img src="${pageContext.request.contextPath}/images2/button_login1.png" name="Image1" width="46" height="46" border="0" id="Image1" /></a>  
		              </td>
		              <td width="101" rowspan="3"></td>
		              <td width="7" rowspan="3">&nbsp;</td>
		            </tr>
		            <tr>
		              <td><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="5" /></td>
		              <td><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="5" /></td>
		            </tr>
		            <tr>
		            <%if( !Utils.isNull(userName).equals("")) {%>
		                 <td><html:text property="userName" size="10" onkeypress="gologin(event);" value="<%=userName %>"/></td>
		                 <td><html:password property="password" size="10" onkeypress="gologin(event);" value="<%=password %>"/></td>
		              <%}else{ %>
		                 <td><html:text property="userName" size="10" onkeypress="gologin(event);" /></td>
		                 <td><html:password property="password" size="10" onkeypress="gologin(event);"/></td>
		              <%} %>
		            </tr>
		            <tr>
		            	<td colspan="2" nowrap><b>
		            		Application Version
		            		<font color="red"><bean:message bundle="sysprop" key="AppVersion"/>
		            		(<%=EnvProperties.getInstance().getProperty("config.type") %>)
		            		</font>
		            		</b>
		            	</td>
		            </tr>
		            <tr>
		            	<td colspan="2">
		            		<span class="errormsg">${errormsg}</span>
		            	</td>
		            </tr>
		        </table>
		         <input type="hidden" id = "screenWidth" name="screenWidth" />
		          <input type="hidden" id = "screenHeight" name="screenHeight" />
		          ...
		        </html:form>
			</td>
        	<td width="523">&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td valign="top"><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="250" /></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td valign="top"><table width="355" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="197"><img src="${pageContext.request.contextPath}/images2/footer.jpg" width="454" height="38" /></td>
          </tr>
        </table></td>
        <td>&nbsp;</td>
      </tr>
    </table></td>
  </tr>
</table>
</body>
</html>
