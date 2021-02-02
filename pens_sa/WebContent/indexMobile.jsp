<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.EnvProperties"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

  <title>Login</title>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
  <!-- Bootstrap -->
 <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css">
 <script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js"></script>
 <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

   <script>
   function login(path){
		var w = screen.width;
	    
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
  
   </script>
  </head>
  <body class="text-center">
  	<html:form action="/login" onsubmit="return false;">
	  
	     <h1 class="h3"> SALES ANALYSIS SYSTEM</h1>
	 
	     <div class="form-outline mb-4">
	        <label class="form-label" for="userName">รหัสผู้ใช้</label>
	        <html:text property="userName" styleId="userName" size="10" />
	      </div>
	    
	   <div class="form-outline mb-4">
	      <label class="form-label" for="password">รหัสผ่าน</label>
	      <html:password property="password" styleId="password"  onkeypress="gologin(event);" size="10" />
	   </div>
	  <p>
	    <%--   <button class="btn btn-primary" type="submit" onclick="return login('${pageContext.request.contextPath}');">    Login    </button> --%>
	      <input type="submit" name="backBT" value="     Login      " class="btn btn-primary"
					      onclick="return login('${pageContext.request.contextPath}');"/> 
	 </p>
	  <p class="mt-5 mb-3 text-muted">
	      <font color="red">
	      <b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
           		<b>(<%=EnvProperties.getInstance().getProperty("product.type")%>)
         </b>
	  </p>
	  <input type="hidden" id = "screenWidth" name="screenWidth" />
	  <input type="hidden" id = "screenHeight" name="screenHeight" />
   </html:form>
</body>
</html>
