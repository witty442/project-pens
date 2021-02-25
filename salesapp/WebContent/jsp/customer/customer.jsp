<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MDistrict"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@page import="com.isecinc.pens.model.MProvince"%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="com.isecinc.pens.web.customer.CustomerHelper"%>
<%@page import="util.SessionGen"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="customerForm" class="com.isecinc.pens.web.customer.CustomerForm" scope="request" />
<%
String action = (String)request.getParameter("action");

String role = ((User)session.getAttribute("user")).getType();
User user = (User)session.getAttribute("user");

List<References> territories = InitialReferences.getReferenes().get(InitialReferences.TERRITORY);
pageContext.setAttribute("territories",territories,PageContext.PAGE_SCOPE);

/** Edit **/
List<References> paymentTerm = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_TERM);
pageContext.setAttribute("paymentTerm",paymentTerm,PageContext.PAGE_SCOPE);

List<References> vatCode = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatCode",vatCode,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes(InitialReferences.PAYMENT_METHOD,"CS");
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);

List<References> shippingMethod = InitialReferences.getReferenes().get(InitialReferences.SHIPMENT);
pageContext.setAttribute("shippingMethod",shippingMethod,PageContext.PAGE_SCOPE);

List<References> partyTypeList = InitialReferences.getReferenes().get(InitialReferences.PARTY_TYPE);
pageContext.setAttribute("partyTypeList",partyTypeList,PageContext.PAGE_SCOPE);

//init tripList
request.setAttribute("tripDayList", CustomerHelper.initTripList());

List<District> districts = new MDistrict().lookUp();
pageContext.setAttribute("districtList",districts,PageContext.PAGE_SCOPE);

List<Province> provincesList = new MProvince().lookUp(Integer.parseInt(customerForm.getCustomer().getTerritory()));
pageContext.setAttribute("provincesList",provincesList,PageContext.PAGE_SCOPE);
%>

<html>
<head>
 <meta charset="utf-8" />
 <meta http-equiv="X-UA-Compatible" content="IE=edge" />
 <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
 <meta name="description" content="" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/customer.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/google_maps.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script async defer src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>" type="text/javascript"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript">

function loadMe(){
	
    //loadProvince(); 
    <%if( !"".equals(customerForm.getCustomer().getProvince())){ %>
	   document.getElementById('province').value = '${customerForm.customer.province}';
	<%}%>
	
	//loadDistrict();
	<%if( !"".equals(customerForm.getCustomer().getDistrict())){ %>
	 // document.getElementsByName('customer.district')[0].value = '<%=customerForm.getCustomer().getDistrict()%>';
	<% } %>
	
	new Epoch('epoch_popup','th',document.getElementById('birthDay'));
	new Epoch('epoch_popup','th',document.getElementById('trip'));
	
	switchPrintType();
}

function switchPrintType(){
	//var printType = $('input[name=customer.printType]:checked').val();
	var printType = document.getElementsByName("customer.printType");
	//alert(printType[0].checked)
	if(printType[0].checked){
		$('#printBranchDesc').attr('readonly', true);
		$("input#printBranchDesc").attr("class", "disableText");
	}else{
		$('#printBranchDesc').attr('readonly', false);
		$("input#printBranchDesc").attr("class", "normalText");
	}
}
function loadProvince(){
	var cboProvince = document.getElementsByName('customer.province')[0];
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
			data : "refId=" + document.getElementsByName('customer.province')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}

//init placeholder
$(function() {
   // $("#abc").attr("customerCode", "some text");
});

</script>
</head>

<body class="sb-nav-fixed" onload="loadMe()">
    <!-- Include Header Mobile  -->
    <jsp:include page="../header.jsp"  flush="true"/>
    <!-- /Include Header Mobile -->
      
    <!-- PROGRAM HEADER -->
    <jsp:include page="../program.jsp">
		<jsp:param name="function" value="CustomerInfo"/>
		<jsp:param name="code" value="${customerForm.customer.code}"/>
	</jsp:include>
    <!-- TABLE BODY -->

	<!-- BODY -->
	<html:form action="/jsp/customerAction"  enctype="multipart/form-data">
	<jsp:include page="../error.jsp"/>
	
	<!-- Content Body -->
        <div class="card-body">
                <div class="form-row">
                    <div class="col-md-10">
                        <div class="form-group">
                            <label class="medium mb-1" for="customerCode">รหัสร้านค้า</label>
                            <html:text property="customer.code" readonly="true" 
                              styleClass="form-control py-3\" autoComplete=\"off" styleId="customerCode" />
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="form-group">
                            <label class="medium mb-1" for="tripDay">จุด</label>
                             <font color="red">*</font>
						     <html:text property="customer.tripDay"  styleId="tripDay"  styleClass="form-control py-3" 
							 onblur="return isNum(this);" maxlength="2"/>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label class="medium mb-1" for="customerName">ชื่อร้านค้า <font color="red">*</font></label>
                    <html:text property="customer.name"  styleId="customerName"  styleClass="form-control py-3\" autoComplete=\"off"/>
                </div>
              
                <div class="form-group">
                    <label class="medium mb-1" for="line1">เลขที่/อาคาร/หมู่บ้าน</label>
                    <html:text property="customer.address.line1"  styleId="line1" styleClass="form-control py-3\" autoComplete=\"off"/>
                </div>
                <div class="form-group">
                    <label class="medium mb-1" for="line2">ถนน</label>
                    <html:text property="customer.address.line2"  styleId="line2" styleClass="form-control py-3\" autoComplete=\"off"/>
                </div>
                <div class="form-row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="medium mb-1" for="line3">แขวง/ตำบล <font color="red">*</font></label>
                             <html:text property="customer.address.line3"  styleId="line3" styleClass="form-control\" autoComplete=\"off"/>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="medium mb-1" for="district">เขต/อำเภอ <font color="red">*</font></label>
                           <%--  <html:text property="address.line4"  styleId="line4" styleClass="form-control py-3\" autoComplete=\"off"/> --%>
                            <html:select property="customer.district" styleId="district" styleClass="form-control">
                               <html:options collection="districtList" property="id" labelProperty="name"/>
					          </html:select>
                        </div>
                    </div>
                </div>
                <div class="form-row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="medium mb-1" for="province">จังหวัด <font color="red">*</font></label>
                               <html:select property="customer.province" styleId="province" onchange="loadDistrict();" styleClass="form-control">
					            <html:options collection="provincesList" property="id" labelProperty="name"/>
					          </html:select>
                        </div>
                    </div>
                </div>
                <div class="form-row">
                    <div class="col-md-4">
                        <div class="form-group">
                           <label class="medium mb-1" for="province">รหัสไปรษณีย์ <font color="red">*</font></label>
                            <%--  <html:select property="address.postalCode" styleId="postalCode"  styleClass="form-control">
					         </html:select>  --%>
					          <html:text property="customer.address.postalCode" styleId="postalCode" styleClass="form-control"/>
                        </div>
                    </div>
                </div>
                <div class="form-row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="medium mb-1" for="contactTo">ชื่อผู้ติดต่อ</label>
                            <html:text property="customer.contact.contactTo" styleId="contactTo" styleClass="form-control"/>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="medium mb-1" for="mobile">เบอร์โทรศัพท์</label>
                            <html:text property="customer.contact.mobile" styleId="mobile" styleClass="form-control"/>
                        </div>
                    </div>
                </div>
                <div class="form-row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="medium mb-1" for="businessType">ประเภทร้านค้า <font color="red">*</font></label>
                              <html:select property="customer.businessType" styleId="businessType" styleClass="form-control" >
									<html:option value=""></html:option>
									<html:options collection="partyTypeList" property="key" labelProperty="name"/>
							  </html:select>
                        </div>
                    </div>
                </div>
               <div class="form-row">
                    <div class="col-md-6 py-3">
                        <div class="form-check form-check-inline">
                           <html:radio property="customer.printType"  styleClass="form-check-input" styleId="printTypeH" value="H" onclick="switchPrintType()">
                           </html:radio>
                            <label class="medium mb-1" for="printTypeH">&nbsp;สำนักงานใหญ่</label>
                        </div>
                    </div>

                    <div class="col-md-6 py-3">
                        <div class="form-check form-check-inline">
                           <html:radio property="customer.printType"  styleClass="form-check-input" styleId="printTypeB" value="B" onclick="switchPrintType()">
                           </html:radio>
                            <label class="medium mb-1" for="printTypeB">&nbsp;สาขาที่ : </label>
                             &nbsp;<html:text property="customer.printBranchDesc"  styleId="printBranchDesc" maxlength="5" readonly="true" onkeydown="return inputNum(event);" styleClass="form-control py-3" />
                        </div>
                    </div>
               </div>
                 <div class="form-group">
                    <label class="medium mb-1" for="taxNo">เลขประจำตัวผู้เสียภาษี</label>
                   	<html:text property="customer.taxNo" styleId="taxNo" maxlength="20"  onkeydown="return inputNum2(event,this)"  
                   	styleClass="form-control py-3\" autoComplete=\"off"/>
                </div>
                <div class="form-row">
                     <div class="col-md-6 py-3">
                         <div class="form-check form-check-inline">
                             <html:checkbox property="customer.printTax" styleId="printTax" value="Y"></html:checkbox>
                             <label class="medium mb-1" for="printTax">&nbsp; พิมพ์เลขที่ผู้เสียภาษี</label>
                         </div>
                     </div>

                     <div class="col-md-6 py-3">
                         <div class="form-check form-check-inline">
                             <html:checkbox property="customer.printHeadBranchDesc" styleId="printHeadBranchDesc" value="Y"></html:checkbox>
                             <label class="medium mb-1" for="printHeadBranchDesc">&nbsp; พิมพ์ สนญ./สาขาที่</label>
                         </div>
                     </div>
                </div>
                 
        </div>
        <!-- Button -->
        <div class="card mb-1 shadow-sm text-center">
			 <div class="card-header">
				  <input type="button" name="saveBT" value="บันทึกข้อมูล" class="btn btn-primary"
				  onclick="return save('${pageContext.request.contextPath}');"/>
		     
		          <input type="button" name="backBT" value="ปิดหน้าจอนี้" class="btn btn-primary"
				   onclick="backsearch('${pageContext.request.contextPath}');"/>
		     </div>
        </div>

	<!-- /Content Body -->
	
    <!-- Hidden Field -->
    
    <html:hidden property="customer.shipToAddressId"/>
    <html:hidden property="customer.billToAddressId"/>
    <html:hidden property="customer.territory" value="<%=user.getTerritory() %>"/>
    <html:hidden property="customer.name2"/>
    <html:hidden property="customer.website"/>
    <html:hidden property="customer.airpayFlag"/>
    <html:hidden property="customer.birthDay" />
    <html:hidden property="customer.parentID"/>
	<html:hidden property="customer.parentCode"/>
	<html:hidden property="customer.parentName" />
	<html:hidden property="customer.paymentTerm" />
	<html:hidden property="customer.vatCode" />
	<html:hidden property="customer.paymentMethod" />
	<html:hidden property="customer.trip" />
	<html:hidden property="customer.isActive" />
	<html:hidden property="customer.interfaces" />			
				
	<input type="hidden" name="tf" value="N"/>
	<html:hidden property="customer.id" styleId="customerId"/>
	<html:hidden property="customer.exported" value="N"/>
 </html:form>
 <!-- BODY -->
			
 <!-- Include Footer Mobile  -->
  <jsp:include page="../footer.jsp" flush="true"/>
 <!-- /Include Footer Mobile -->	
					
</body>
</html>