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
try{
int tabIndex = 0;
User user = (User)session.getAttribute("user");
String pageName = Utils.isNull(request.getParameter("pageName")); 
String mobile = Utils.isNull(request.getParameter("mobile")); 
String action = Utils.isNull(request.getParameter("action")); 
%>
<html>
<head>
 <meta charset="utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
  <meta name="description" content="" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>


<!-- Bootstrap -->
 <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css?v=<%=SIdUtils.getInstance().getIdSession()%>">
<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />


<script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/js/all.min.js" crossorigin="anonymous"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap_styles.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<style>
</style>
<script type="text/javascript">

</script>
</head>
<body class="sb-nav-fixed">

	   <!-- Include Header Mobile  -->
       <jsp:include page="../templates/header.jsp"  flush="true"/>
       <!-- /Include Header Mobile -->
       
      
	    <!-- Content -->
		<div id="layoutSidenav_content">
           <main>
           
            <!-- Content -->
		     <html:form action="/jsp/tempAction">
	        <jsp:include page="../error.jsp"/>
	        
                    <div class="container-fluid">
                        <h1 class="mt-4">test </h1>
                        <table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
							   <a href="#" onclick="gotoStockMCMStep2();">
								  <input type="button" value="àÃÔèÁ¡ÃÍ¡¢éÍÁÙÅÊµêÍ¡"  class="btn btn-primary">
							    </a>
							 </td>
						</tr>
						<tr>
							<td align="center">
							   <a href="#" onclick="gotoStockMCMSearch();">
								  <input type="button" value="       ´Ù»ÃĞÇÑµÔ            "  class="btn btn-primary">
							    </a>
							 </td>
						</tr>
					</table>
                    </div>
                    
                    <!-- Hidden Field -->
				 <input type="hidden" name="pageName" value="<%=pageName %>"/>
				 <input type="hidden" name="mobile" value="<%=mobile %>"/>
			     <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
                    
                </html:form>
           </main>
                
                
         <!-- BODY -->
   
     <!-- Include Footer Mobile  -->
     <jsp:include page="../templates/footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->	
		     
     
</body>
</html>
<%}catch(Exception e){
  e.printStackTrace();
}
	%>

