<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="com.isecinc.pens.ApplicationVersion"%>
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

List<District> districtsAll = new ArrayList<District>();
District dBlank = new District();
dBlank.setId(0);
dBlank.setName("");
districtsAll.add(dBlank);

List<District> districts = new MDistrict().lookUp();
districtsAll.addAll(districts);
pageContext.setAttribute("districts", districtsAll, PageContext.PAGE_SCOPE);

List<References> territorys = InitialReferences.getReferenes().get(InitialReferences.TERRITORY);
pageContext.setAttribute("territorys", territorys, PageContext.PAGE_SCOPE);

List<References> actives= InitialReferences.getReferenes().get(InitialReferences.ACTIVE);
pageContext.setAttribute("actives",actives,PageContext.PAGE_SCOPE);

//Show Cust Show Only have trip
List<References> custShowTrip = InitialReferences.getReferenes(InitialReferences.CUST_SHOW_TRIP,"Y");
String custShowTripFlag = "";
if(custShowTrip != null && custShowTrip.size() >0){
   //System.out.println("custShowTrip:"+(custShowTrip.get(0)).getKey());
   custShowTripFlag = custShowTrip.get(0).getKey();
   if("Y".equalsIgnoreCase(custShowTripFlag)){
      customerForm.getCustomer().setDispHaveTrip("true");
   }
}

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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/customer.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/customerTransaction.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<script async defer src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>" type="text/javascript"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript">
function loadMe(){
	loadProvince(); 
	
	document.getElementsByName('customer.searchProvince')[0].value = ${customerForm.customer.searchProvince};
	
	loadDistrict();
	<%if( !"".equals(customerForm.getCustomer().getDistrict())){ %>
	  document.getElementsByName('customer.district')[0].value = <%=customerForm.getCustomer().getDistrict()%>;
	<% } %>
}

function loadProvince(){
	var cboProvince = document.getElementsByName('customer.searchProvince')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/ProvinceTerritory.jsp",
			data : "refId=" + document.getElementsByName('customer.territory')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboProvince.innerHTML=returnString;
			}
		}).responseText;
	});
}

function loadDistrict(){
	var cboDistrict = document.getElementsByName('customer.district')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/DistrictAjax.jsp",
			data : "refId=" + document.getElementsByName('customer.searchProvince')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}

<%if("true".equalsIgnoreCase(request.getParameter("showMsg"))){ %>

$(function() {
	$("#dialog").dialog({ height: 300,width:600,modal:false });
  });
 
 function close(){
	 $("#dialog").dialog('close');
 }
 
 function linkToInterfaces(path){
	window.location = path+"/jsp/interfaces/interfaces.jsp";
 }
 
 setTimeout(function(){ $("#dialog").dialog('close');},9000);
 
 <%}else{%>
 
 <%}%>
 function gotoPage(path,page){
		document.customerForm.action = path + "/jsp/customerAction.do?do=searchPage&rf=Y";
		document.getElementsByName('curPage')[0].value = page;
		document.customerForm.submit();
		return true;
	}
//change pv
 function changePV(pvid){
	 var disId = parseInt(document.getElementsByName('customer.district')[0].value);
	// alert("disId["+disId+"],pvid:"+pvid);
 	
	 $("#district").html("");
 	<%for(District d : districts){%>
	 	if(pvid == <%=d.getProvinceId()%>){
	 		 if(disId  == <%=d.getId()%> ){
	 			 alert(dispId);
	 			 $("<option selected value=<%=d.getId()%>><%=d.getName()%></option>").appendTo("#district");
	 		}else{
	 			 $("<option value=<%=d.getId()%>><%=d.getName()%></option>").appendTo("#district");
	 		} 
	 	}
   <%}%>
 }

 function MarkLocationMap(path){
	  var width= window.innerWidth-50;
	  var height= window.innerHeight-20;
	 // alert(width+","+height);
	 PopupCenter(path+"/jsp/location/markLocationMap.jsp?", "Mark location map",width,height); 
}
 function exportToExcel(path){
		document.customerForm.action = path + "/jsp/customerAction.do?do=exportToExcel";
		document.customerForm.submit();
		return true;
	}
</script>

</head>
<body class="sb-nav-fixed" onload="loadMe()">

	   <!-- Include Header Mobile  -->
       <jsp:include page="../header.jsp"  flush="true"/>
       <!-- /Include Header Mobile -->
       
       <jsp:include page="../program.jsp">
		 <jsp:param name="function" value="Customer"/>
	   </jsp:include>
       
	    <!-- Content -->
		<html:form action="/jsp/customerAction">
		  <jsp:include page="../error.jsp"/>
		  
		    <!-- Content Body -->
           <div class="card-body">
               <div class="form-row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="medium mb-1" for="territory">เขตการขาย (ภาค)</label>
							  <html:select property="customer.territory" styleId="territory" onchange="loadProvince();"  styleClass="form-control">
								<html:option value=""></html:option>
								<html:options collection="territorys" property="key" labelProperty="name"/>
							</html:select>
                        </div>
                    </div>
                </div>
                <div class="form-row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="medium mb-1" for="searchProvince">จังหวัด </label>
                               <html:select property="customer.searchProvince" styleId="searchProvince" onchange="loadDistrict();" styleClass="form-control">
					           
					          </html:select>
                        </div>
                    </div>
                </div>
                <div class="form-row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="medium mb-1" for="district">เขต/อำเภอ </label>
                            <html:select property="customer.district" styleId="district" styleClass="form-control">
                               
					          </html:select>
                        </div>
                     </div>
                 </div>
                 <div class="form-group">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="medium mb-1" for="inputLastName">รหัสร้านค้า</label>
                            <html:text property="customer.code"  
                              styleClass="form-control py-3\" autoComplete=\"off" styleId="customerCode" />
                        </div>
                    </div>
                    <div class="col-md-8">
                        <div class="form-group">
                            <label class="medium mb-1" for="customerName">ชื่อร้านค้า </label>
                            <html:text property="customer.name"  styleId="customerName"  
                            styleClass="form-control py-3\" autoComplete=\"off"/>
                        </div>
                    </div>
                 </div>
                  <div class="form-inline">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="medium mb-1" for="isActive">สถานะ &nbsp;</label>
                            <html:select property="customer.isActive" styleId="isActive" styleClass="form-control">
						        <html:options collection="actives" property="key" labelProperty="name"/>
					        </html:select>
                        </div>
                    </div>
                    <div class="col-md-8">
                        <div class="form-check form-check-inline">
                             <html:checkbox property="customer.dispHaveTrip" styleClass="form-check-input"></html:checkbox>
                            <label class="medium mb-1" for="district">แสดงเฉพาะร้านที่มี Trip</label>
                          
                        </div>
                    </div>
                 </div>
           </div>

		<br>
		 <!-- Button -->
        <div class="card mb-1 shadow-sm text-center">
			<div class="card-header">
			   <input type="button" value="ค้นหา" class="btn btn-primary" onclick="search('${pageContext.request.contextPath}')">
			   <input type="button" value="Clear" class="btn btn-primary" onclick="clearForm('${pageContext.request.contextPath}')">
				
				<a href="#" onclick="return MarkLocationMap('${pageContext.request.contextPath}');">
					<input type="button" value="แสดงร้านค้าทั้งหมดบน แผนที่" class="btn btn-primary">
				</a> 
				&nbsp;&nbsp;&nbsp;
				<a href="#" onclick="return exportToExcel('${pageContext.request.contextPath}');">
				   <b>....</b>
				</a> 
		     </div>
        </div>
		<!-- BUTTON -->
				
		<!-- RESULT -->
		<!-- Paging -->
		<c:if test="${customerForm.results != null}">
		 <%
		    if(Utils.isNull(session.getAttribute("dispHaveTrip")).equals("Y")) {
			    int currPage =  customerForm.getCurrPage();
			    List<Customer> tripList= ((List)session.getAttribute("tripPageList"));
		 
			    out.println(PageingGenerate.genTripPageing(tripList,currPage)); 
		    }else{ 
			    
			   int totalPage = customerForm.getTotalPage();
			   int totalRecord = customerForm.getTotalRecord();
			   int currPage =  customerForm.getCurrPage();
			   int startRec = customerForm.getStartRec();
			   int endRec = customerForm.getEndRec();
			   int no = startRec;
			   boolean mobileFlage = true;
			   user.setMobile(true);
				
			   out.println(PageingGenerate.genPageing(user,totalPage, totalRecord, currPage, startRec, endRec, no)); 
		   } 
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
