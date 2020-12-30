<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.EnvProperties"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%
String gotoPage = Utils.isNull(request.getParameter("gotoPage"));
%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

  <title>Login</title>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
  <!-- Bootstrap -->
 <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css">
 <script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js"></script>
 <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

   <script>
   function login(path){
		var w = 0;
	    if(detectmob()){
	      //alert("Mobile");
		  var ratio = window.devicePixelRatio || 1;
		  w = screen.width * ratio;
		  var h = screen.height * ratio;
	    }else{
	    	w = screen.width;
	    }
		//alert(w+":"+h);
		document.getElementsByName('screenWidth')[0].value = w;
		
	    if(Trim(document.getElementsByName('userName')[0].value)==''){
	        document.getElementsByName('userName')[0].focus();
	        return false;
	    }
	    if(Trim(document.getElementsByName('password')[0].value)==''){
	    	document.getElementsByName('password')[0].focus();
	        return false;
	    }
	    document.loginForm.action=path+"/login.do?do=login&mobile=true";
	    document.loginForm.submit();
	    return true;
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
		  }
		 else {
		    return false;
		  }
	}
   </script>
  </head>
  <body class="text-center">
  	<html:form action="/login" onsubmit="return false;">
	  
	  <h1 class="h3 mb-3 font-weight-normal">PENS Application</h1>
	    <p>
	     <label>รหัสผู้ใช้:</label>
	     <html:text property="userName"   size="10"/>
	    </p>
	    <p>
	    <label>รหัสผ่าน:</label>
	      <html:password property="password"   onkeypress="gologin(event);" size="10"/>
	   </p>
	  <p>
	    <button class="btn btn-lg btn-primary" type="submit" onclick="return login('${pageContext.request.contextPath}');"> Login </button>
	 </p>
	  <p class="mt-5 mb-3 text-muted">
	      <font color="red">
	      <b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
           		<b>(<%=EnvProperties.getInstance().getProperty("product.type")%>)
         </b>
	  </p>
	  <input type="hidden" id = "screenWidth" name="screenWidth" />
	  <input type="hidden" id = "screenHeight" name="screenHeight" />
	  <input type="hidden" id = "gotoPage" name="gotoPage" value="<%=gotoPage %>"/>
   </html:form>
</body>
</html>
