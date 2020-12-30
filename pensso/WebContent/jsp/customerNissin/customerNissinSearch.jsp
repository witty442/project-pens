<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
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
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<jsp:useBean id="customerNissinForm" class="com.isecinc.pens.web.customernissin.CustomerNissinForm" scope="request" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "customerNissinForm");
User user = (User)session.getAttribute("user");
String role = user.getType();
if(user.getUserName().equalsIgnoreCase("admin")){
	role = "ADMIN";
}

List<District> districtsAll = new ArrayList<District>();
District dBlank = new District();
dBlank.setId(0);
dBlank.setName("");
districtsAll.add(dBlank);

List<District> districts = new MDistrict().lookUp();
districtsAll.addAll(districts);
pageContext.setAttribute("districts", districtsAll, PageContext.PAGE_SCOPE);

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" type="text/css" />

<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<script type="text/javascript">

function loadMe(){
	loadProvince();
	<%if( !"".equals(Utils.isNull(customerNissinForm.getCustomer().getProvinceId()))){ %>
	  document.getElementsByName('customer.provinceId')[0].value = ${customerNissinForm.customer.provinceId};
	<%}%>
	
	loadDistrict();
	<%if( !"".equals(Utils.isNull(customerNissinForm.getCustomer().getDistrictId()))){ %>
	  document.getElementsByName('customer.districtId')[0].value = <%=customerNissinForm.getCustomer().getDistrictId()%>;
	<% } %>
}

function loadProvince(){
	//notInProvinceId=178:Myanmar
	var cboProvince = document.getElementsByName('customer.provinceId')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/ProvinceTerritory.jsp",
			data : "refId=-1&notInProvinceId=178",
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboProvince.innerHTML=returnString;
			}
		}).responseText;
	});
}

function loadDistrict(){
	var cboDistrict = document.getElementsByName('customer.districtId')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/DistrictAjax.jsp",
			data : "refId=" + document.getElementsByName('customer.provinceId')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}

function search(path){
	document.customerNissinForm.action = path + "/jsp/customerNissinAction.do?do=search&action=newsearch&rf=Y";
	document.customerNissinForm.submit();
	return true;
}
function clearForm(path){
	document.customerNissinForm.action = path + "/jsp/customerNissinAction.do?do=clearFormSearch&action=new";
	document.customerNissinForm.submit();
	return true;
}
function openCustomer(path,mode,id){
	if(id !=''){
	   document.customerNissinForm.action = path + "/jsp/customerNissinAction.do?do=prepare&fromPage=customerNissin&action=edit&id="+id;
	}else{
	   document.customerNissinForm.action = path + "/jsp/customerNissinAction.do?do=prepare&fromPage=customerNissin&action=new";
	}
	document.customerNissinForm.submit();
	return true;
}
function viewOrderByCustomerId(path,customerId){
	var form = document.customerNissinForm;
	form.action = path + "/jsp/orderNissinAction.do?do=viewDetail&action=view&fromPage=customerNissin&customerId="+customerId;
	form.submit();
	return true;
}
function viewOrderDetail(path,customerId,orderId){
	var form = document.customerNissinForm;
	if(orderId ==''){
	   form.action = path + "/jsp/orderNissinAction.do?do=viewDetail&action=new&fromPage=customerNissin&customerId="+customerId;
	}else if(orderId !=''){
	   form.action = path + "/jsp/orderNissinAction.do?do=viewDetail&action=edit&fromPage=customerNissin&customerId="+customerId+"&orderId="+orderId;
	}
	
	form.submit();
}
 function gotoPage(currPage){
		var form = document.customerNissinForm;
		var path = document.getElementById("path").value;
		form.action = path + "/jsp/customerNissinAction.do?do=search&currPage="+currPage;
	    form.submit();
	    return true;
	}
//change pv
 function changePV(pvid){
	 var disId = parseInt(document.getElementsByName('customer.districtId')[0].value);
	// alert("disId["+disId+"],pvid:"+pvid);
 	
	$("#districtId").html("");
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

</script>

</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerSP.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="NissinCustomerSearch"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/customerNissinAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						
						<tr>
						    <td align="right" width="40%"><bean:message key="Address.Province" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" width="10%">
								<html:select property="customer.provinceId" styleId="provinceId" onchange="loadDistrict();">
								</html:select>
							</td>
							<td align="left" width="40%">
							     เขต/อำเภอ
							     <html:select property="customer.districtId" styleId="districtId">
									<%-- <html:options collection="districts" property="id" labelProperty="name"/> --%>
								</html:select>
							</td>
						</tr>
						
						<tr>
							<td align="right"><bean:message key="Customer.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="2"><html:text property="customer.name"  styleClass="\" autoComplete=\"off" /></td>
						</tr>
					</table>
					
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
							    <input type="button" value="เพิ่มร้านค้าใหม่" class="newPosBtn" onclick="openCustomer('${pageContext.request.contextPath}','add','')">
								<input type="button" value="ค้นหา" class="newPosBtn" onclick="search('${pageContext.request.contextPath}')">
								<input type="button" value="Clear" class="newNegBtn" onclick="clearForm('${pageContext.request.contextPath}')">
							</td>
						</tr>
					</table>				
                    
					<!-- RESULT -->
					<!-- Paging -->
					 <c:if test="${customerNissinForm.results != null}">
				        <%
					     int totalPage = customerNissinForm.getTotalPage();
					     int totalRecord = customerNissinForm.getTotalRecord();
					     int currPage =  customerNissinForm.getCurrPage();
					     int startRec = customerNissinForm.getStartRec();
					     int endRec = customerNissinForm.getEndRec();
					     int no = Utils.calcStartNoInPage(currPage, customerNissinForm.getPageSize());
					   
						 out.println(PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no));
					    %>
					    <table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr >
							    <th >No.</th>
							    <th >ทำรายการ สั่งซื้อ</th>
								<th >แก้ไข/ดู ลูกค้า</th>
								<th >หมายเลขลูกค้า</th>
								<th >ชื่อร้านค้า</th>
								<th >ที่อยู่</th>
								<th >เบอร์ติดต่อ</th>
								<th >ประเภทร้านค้า</th>
							</tr>	
							<c:forEach var="item" items="${customerNissinForm.results}" varStatus="rows">
							<c:choose>
								<c:when test="${item.no %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when> 
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>">
							   <td class="td_text_center" width="3%"><c:out value='${item.no}'/></td>
							   <td class="td_text_center" width="5%">
							      <!-- NEW Order -->
									<a href="#" onclick="viewOrderDetail('${pageContext.request.contextPath}',${item.id},'')">
								       <img src="${pageContext.request.contextPath}/images2/b_order.png" width="32" height="32" border="0" class="newPicBtn">
								    </a> 	 
								</td>
								<td class="td_text_center" width="5%">
								    <c:if test="${item.canEdit==true}"> 
							         <a href="#" onclick="javascript:openCustomer('${pageContext.request.contextPath}','edit','${item.id}');">
									    <img border=0 src="${pageContext.request.contextPath}/icons/process.gif">
									 </a>
								    </c:if>	
								    <c:if test="${item.canEdit==false}">
							         <a href="#" onclick="javascript:openCustomer('${pageContext.request.contextPath}','view','${item.id}');">
									    <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif">
									 </a>
								   </c:if> 
								</td>
								
								<td class="td_text_center" width="7%"><c:out value='${item.id}'/></td>
								<td class="td_text" width="10%"><c:out value='${item.name}'/></td>
								<td class="td_text" width="20%"><c:out value='${item.addressSummary}'/></td>
								<td class="td_text_center" width="7%"><c:out value='${item.mobile}'/></td>
								<td class="td_text_center" width="5%"><c:out value='${item.customerType}'/></td>
							 </tr>
							</c:forEach>
						 </table>
					</c:if>	
					
				<!-- Result -->	
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						<tr>
							<td align="right">
								<a href="#" onclick="window.location='./mainpage.jsp'">
								<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
								<!-- <img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"> --></a>
							</td>
							<td width="10%">&nbsp;</td>
						</tr>
					</table>
					<!-- Hidden Field -->
					<html:hidden property="curPage"/>
					<html:hidden property="totalPage"/>
					<html:hidden property="totalRow"/>
					
					<!-- For Paging -->
				    <html:hidden property="totalRecord" styleId="totalRecord"/> 
					<input type="hidden" name= "path" id="path" value="${pageContext.request.contextPath}"/>
					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					</html:form>
					<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
   <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>

<div id="dialog" title="คำแนะนำ" style="display:none">
   
</div>

</body>
</html>
