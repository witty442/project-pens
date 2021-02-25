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
 <meta charset="tis-620" />
 <meta http-equiv="X-UA-Compatible" content="IE=edge" />
 <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
 <meta name="description" content="" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">


<!-- Include Bootstrap Resource  -->
 <jsp:include page="resourceBootstrap.jsp"  flush="true"/>
 <!-- /Include Bootstrap Resource -->
 
<style>
</style>
<script type="text/javascript">

</script>
</head>
<body class="sb-nav-fixed">

	   <!-- Include Header Mobile  -->
       <jsp:include page="header.jsp"  flush="true"/>
       <!-- /Include Header Mobile -->
       
	    <!-- Content -->
        <div class="container-fluid">
             <h1 class="mt-4">Sales Information</h1>
             <ol class="breadcrumb mb-4">
                 <li class="breadcrumb-item active"><%=user.getCode() %>-<%=user.getName() %></li>
             </ol>
             <div class="row">
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-wrainig text-white mb-4">
                         <div class="card-body" >
                          <a href="${pageContext.request.contextPath}/jsp/customerAction.do?do=prepareCustTrip">ลูกค้า</a>
                         </div>
                     </div>
                 </div>
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-wrainig text-white mb-4">
                         <div class="card-body">
                            <a href="${pageContext.request.contextPath}/jsp/customerAction.do?do=prepare">สร้างลูกค้าใหม่</a>
                         </div>
                     </div>
                 </div>
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-primary text-white mb-4">
                         <div class="card-body">ทริป</div>
                     </div>
                 </div>
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-primary text-white mb-4">
                         <div class="card-body">เบิกสินค้าจาก จาก PD</div>
                     </div>
                 </div>
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-primary text-white mb-4">
                         <div class="card-body">คืนสินค้าจาก จาก PD</div>
                     </div>
                 </div>
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-primary text-white mb-4">
                         <div class="card-body">รับ Bill-T</div>
                     </div>
                 </div>
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-primary text-white mb-4">
                         <div class="card-body">บันทึกเก็บเงิน การขายเชื่อ</div>
                     </div>
                 </div>
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-primary text-white mb-4">
                         <div class="card-body">ใบคุมเอกสาร ที่จัดส่งเข้าบริษัท</div>
                     </div>
                 </div>
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-primary text-white mb-4">
                         <div class="card-body">บันทึกส่งเงิน โอนเข้าบริษัท</div>
                     </div>
                 </div>
                 <div class="col-xl-3 col-md-6">
                     <div class="card bg-primary text-white mb-4">
                         <div class="card-body">รายงาน</div>
                         
                     </div>
                 </div>
             </div> 
         </div>
        
    <!-- Include Footer Mobile  -->
     <jsp:include page="footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->	
</body>
</html>
<%}catch(Exception e){
  e.printStackTrace();
}
	%>

