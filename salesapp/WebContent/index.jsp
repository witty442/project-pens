<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.Utils"%> 
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="tempForm" class="com.isecinc.pens.web.temp.TempForm" scope="session" />
<%

%>
<html>
<head>
 <meta charset="utf-8" />
 <meta http-equiv="X-UA-Compatible" content="IE=edge" />
 <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
 <meta name="description" content="" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">

 <!-- Include Bootstrap Resource  -->
 <jsp:include page="/jsp/resourceBootstrap.jsp"  flush="true"/>
 <!-- /Include Bootstrap Resource -->
 
<style>
</style>
<script type="text/javascript">
function gologin(e){
	if(e == null || (e != null && e.keyCode == 13)){
		login('${pageContext.request.contextPath}');
	}
}
function login(path){
	var w = screen.width;
	var h = screen.height;

	//alert(w+":"+h);
	document.getElementsByName('screenWidth')[0].value = w;
	document.getElementsByName('screenHeight')[0].value = h;
	
    if(document.getElementsByName('userName')[0].value==''){
       document.getElementsByName('userName')[0].focus();
       return false;
    }
    if(document.getElementsByName('password')[0].value==''){
   	   document.getElementsByName('password')[0].focus();
       return false;
    }
    document.loginForm.action=path+"/login.do?do=login";
    document.loginForm.submit();
    return true;
}
</script>
</head>
<body class="sb-nav-fixed">
  <html:form action="/login" onsubmit="return false;">
	  <div id="layoutAuthentication">
            <div id="layoutAuthentication_content">
                <main>
                    <div class="container">
                        <div class="row justify-content-center">
                            <div class="col-lg-5">
                                <div class="card shadow-lg border-0 rounded-lg mt-5">
                                    <div class="card-header"><h3 class="text-center font-weight-light my-4">Login</h3></div>
                                    <div class="card-body">
                                        <form>
                                            <div class="form-group">
                                                <label class="small mb-1" for="userName">รหัสผู้ใช้</label>
                                                <!-- <input class="form-control py-4" id="userName" type="text" placeholder="ระบุรหัสผู้ใช้" /> -->
                                                <html:text property="userName" styleId="userName"  onkeypress="gologin(event);" styleClass="form-control py-4"/>
                                            </div>
                                            <div class="form-group">
                                                <label class="small mb-1" for="password">รหัสผ่าน</label>
                                                <!-- <input class="form-control py-4" id="password" type="password" placeholder="ระบุรหัสผ่าน" /> -->
                                                 <html:password property="password" styleId="password"  onkeypress="gologin(event);" styleClass="form-control py-4"/>
                                            </div>
                                            <div class="form-group">
                                                <div class="custom-control custom-checkbox">
                                                    <input class="custom-control-input" id="rememberPasswordCheck" type="checkbox" />
                                                    <label class="custom-control-label" for="rememberPasswordCheck">Remember password</label>
                                                </div>
                                            </div>
                                            <div class="form-group d-flex align-items-center justify-content-between mt-4 mb-0">
                                                <a class="small" href="password.html">Forgot Password?</a>
                                                <a class="btn btn-primary" href="#" onclick="login('${pageContext.request.contextPath}');">Login</a>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="card-footer text-center">
                                        <div class="small"><a href="register.html">Need an account? Sign up!</a></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
            <div id="layoutAuthentication_footer">
                <footer class="py-4 bg-light mt-auto">
                    <div class="container-fluid">
                        <div class="d-flex align-items-center justify-content-between small">
                            <div class="text-muted">Copyright &copy; pens 2020</div>
                            <div>
                                <a href="#">Privacy Policy</a>
                                &middot;
                                <a href="#">Terms &amp; Conditions</a>
                            </div>
                        </div>
                    </div>
                </footer>
            </div>
        </div>
         <input type="hidden" id = "screenWidth" name="screenWidth" />
		 <input type="hidden" id = "screenHeight" name="screenHeight" />
     </html:form>
</body>
</html>


