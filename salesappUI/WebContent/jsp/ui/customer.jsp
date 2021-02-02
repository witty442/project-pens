<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
%>
<html>
<head>
 <meta charset="utf-8" />
 <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
 <meta name="description" content="" />
<title>Customer UI</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>

<!-- Bootstrap -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css?v=">
<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js?v="></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=" type="text/css" />


<script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap-template1.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap_template1_styles.css?v=" type="text/css" />

<style></style>
<script type="text/javascript"></script>
</head>
<body class="sb-nav-fixed">

	   <!-- Include Header Mobile  -->
       <jsp:include page="../templates/header.jsp"  flush="true"/>
       <!-- /Include Header Mobile -->
       
      
	    <!-- Content -->
		<div id="layoutSidenav_content">
           <main>
           
            <!-- Content -->
		     <form name="form1">
	       
	        
                    <div class="container-fluid">
                        <h1 class="mt-4">Customer Info </h1>
                        <table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
							   <a href="#" onclick="gotoStockMCMStep2();">
								  <input type="button" value="เริ่มกรอกข้อมูลสต๊อก"  class="btn btn-primary">
							    </a>
							 </td>
						</tr>
						<tr>
							<td align="center">
							   <a href="#" onclick="gotoStockMCMSearch();">
								  <input type="button" value="       ดูประวัติ            "  class="btn btn-primary">
							    </a>
							 </td>
						</tr>
					</table>
                    </div>
                    
                
                </form>
           </main>
                
                
         <!-- BODY -->
   
     <!-- Include Footer Mobile  -->
     <jsp:include page="../templates/footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->	
		     
     
</body>
</html>

