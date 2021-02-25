<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="com.isecinc.pens.ApplicationVersion"%>
<%@page import="com.isecinc.pens.inf.manager.batchwork.AppversionVerifyWorker"%>
<%@page import="util.SessionUtils"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="java.util.Iterator"%>
<%@page import="util.SessionGen"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@page import="com.isecinc.pens.model.MDistrict"%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="customerForm" class="com.isecinc.pens.web.customer.CustomerForm" scope="request" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "customerForm");
User user = (User)session.getAttribute("user");
String role = user.getType();

%>
<html>
<head>
 <meta charset="utf-8" />
 <meta http-equiv="X-UA-Compatible" content="IE=edge" />
 <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
 <meta name="description" content="" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_mobile_style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/customer.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerTransaction.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<script async defer src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>" type="text/javascript"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript">
</script>

</head>
<body class="sb-nav-fixed">

	   <!-- Include Header Mobile  -->
       <jsp:include page="../header.jsp"  flush="true"/>
       <!-- /Include Header Mobile -->
       
       <jsp:include page="../program.jsp">
		 <jsp:param name="function" value="CustomerTrip"/>
	   </jsp:include>
       
	    <!-- Content -->
		<html:form action="/jsp/customerAction">
		  <jsp:include page="../error.jsp"/>
		  
		 <!-- Content Body -->
          <div class="card shadow-lg border-0 rounded-lg mt-2">
              <!-- Nav pills -->
              <ul class="nav nav-tabs nav-pills nav-fill mb-3">
                  <li class="nav-item">
                      <a class="nav-link active" data-toggle="pill" href="#today">ทริปวันนี้</a>
                  </li>
                  <li class="nav-item">
                      <a class="nav-link" data-toggle="pill" href="#map">แผนที่</a>
                  </li>
                  <li class="nav-item">
                      <a class="nav-link" 
                         href="${pageContext.request.contextPath}/jsp/customer/customerSearch.jsp">
                         ค้นหาทั้งหมด
                      </a>
                  </li>
              </ul>
         </div>
 
		<!-- RESULT -->
		<!-- Paging -->
		<c:if test="${customerForm.results != null}">
		 <%
		   // int currPage =  customerForm.getCurrPage();
		    //List<Customer> tripList= ((List)session.getAttribute("tripPageList"));
	 
		    //out.println(PageingGenerate.genTripPageing(tripList,currPage)); 
		   %>
		   <div class="table-responsive">
              <table class="table table-bordered table-striped table-light"
                   id="dataTable" width="100%" cellspacing="0">
                 <thead class="thead-dark">
				  <tr >
					<th class="text-center">ร้านค้า</th>
					<th class="text-center">บันทึกเยี่ยม</th>
					<th class="text-center">บันทึกขาย</th>
					<% if( role.equalsIgnoreCase(User.TT)){ %>
					  <th>บันทึกรับเงิน</th>
					<%} %>
					<th class="text-center">รายละเอียดร้าน</th>
					<% if( role.equalsIgnoreCase(User.TT)){ %>
					  <th class="text-center">ตั้งกองโชวร์</th>
					  <th class="text-center">จัดรายการ</th>
				    <%} %>
				  </tr>	
				</thead>
				<%
				Customer[] customerList = customerForm.getResults();
				for(int i=0;i<customerList.length;i++){
					Customer item = customerList[i];
				%>
				<tr class="<c:out value='${tabclass}'/>">
					<td class="">
					   <%=item.getNo() %>.
					   <%=item.getCode() %>
					   <%=item.getName() %>
					   <%=item.getAddressSummary() %>
					</td>
					<td class="text-center">
 					    <%if(Utils.isNull(item.getIsActive()).equalsIgnoreCase("Y")) {%>
					    <a href="#" onclick="toVisit('${pageContext.request.contextPath}','add',<%=item.getId()%>)">
				           <img src="${pageContext.request.contextPath}/images2/b_order.png" width="32" height="32" border="0" class="newPicBtn">
				        </a> 
				        <%} %>
					</td>
					<td class="text-center">
 					    <%if(Utils.isNull(item.getIsActive()).equalsIgnoreCase("Y")) {%>
					    <a href="#" onclick="toCreateNewOrder('${pageContext.request.contextPath}','add',<%=item.getId()%>)">
				           <img src="${pageContext.request.contextPath}/images2/b_order.png" width="32" height="32" border="0" class="newPicBtn">
				        </a> 
				        <%} %>
					</td>
					
					<% if( role.equalsIgnoreCase(User.TT)){ %>
						<td class="text-center">
						  <%if(Utils.isNull(item.getIsActive()).equalsIgnoreCase("Y")) {%>
						    <a href="#" onclick="toCreateNewReceipt('${pageContext.request.contextPath}','add',<%=item.getId()%>);">
						         <img src="${pageContext.request.contextPath}/images2/b_receipt.jpg" width="32" height="32" border="0" class="newPicBtn"/>
						    </a>
						  <%} %>
						</td>
				    <%} %>
				   
					<td class="text-center">  
				          <a href="#" onclick="javascript:viewCustomer('${pageContext.request.contextPath}',<%=item.getId()%>);">
						     <img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif">
						  </a>	  
					</td>
						
				 <% if( role.equalsIgnoreCase(User.TT)){ %>
					<td class="text-center">
						 <%if(Utils.isNull(item.getIsActive()).equalsIgnoreCase("Y")) {%>
					       <a href="#" onclick="manageProdShowTT('${pageContext.request.contextPath}','<%=item.getCode()%>');">
				                <b>  ตั้งกองโชว์</b>
				           </a>
				          <%} %>
					</td>
				<%} %>
				 <% if( role.equalsIgnoreCase(User.TT)){ %>
						<td class="">
						 <c:if test="${item.isActive=='Y'}">
						    <a href="#" onclick="toCreateNewReqPromotion('${pageContext.request.contextPath}','<%=item.getId() %>','customerSearch');">
						     <b>  จัดรายการ</b>
						    </a>
						    </c:if>
						</td>
					<%} %>
				
				</tr>
				<%} %>
			</table>
		 </div>
		</c:if>	
		
	<!-- Result -->	
	
		<!-- Hidden Field -->
		<html:hidden property="curPage"/>
		<html:hidden property="totalPage"/>
		<html:hidden property="totalRow"/>
</html:form>
				
    <!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->	
</body>
</html>
