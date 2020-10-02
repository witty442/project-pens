
<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.isecinc.pens.ApplicationVersion"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@page import="com.isecinc.pens.model.MDistrict"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="customerForm" class="com.isecinc.pens.web.customer.CustomerForm" scope="request" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "customerForm");
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
      //customerForm.getCustomer().setDispHaveTrip("true");
   }
}


%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%><html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customer.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerTransaction.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script async defer src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>" type="text/javascript"></script>

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

/* $(function() {
	$("#dialog").dialog({ height: 300,width:600,modal:false });
  });
 
 function close(){
	 $("#dialog").dialog('close');
 }
 
 function linkToInterfaces(path){
	window.location = path+"/jsp/interfaces/interfaces.jsp";
 }
 
 setTimeout(function(){ $("#dialog").dialog('close');},9000); */
 
 <%}else{%>
 
 <%}%>
/*  function gotoPage(path,page){
		document.customerForm.action = path + "/jsp/customerAction.do?do=searchPage&rf=Y";
		document.getElementsByName('curPage')[0].value = page;
		document.customerForm.submit();
		return true;
	} */
	
function search(path){
	document.customerForm.action = path + "/jsp/customerAction.do?do=search&action=newsearch&rf=Y";
	document.customerForm.submit();
	return true;
}

 function gotoPage(currPage){
		var form = document.customerForm;
		var path = document.getElementById("path").value;
		form.action = path + "/jsp/customerAction.do?do=search&currPage="+currPage;
	    form.submit();
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
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
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
				<jsp:param name="function" value="CustomerInfo"/>
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
						<html:form action="/jsp/customerAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						<tr>
							<td colspan="3" align="left">
								<%if(role.equalsIgnoreCase(User.VAN)){ %>
								  <a href="#" onclick="prepare('${pageContext.request.contextPath}','add')">
								  <img border=0 src="${pageContext.request.contextPath}/icons/user_add.gif" align="absmiddle">
								  &nbsp;<bean:message key="CreateNewRecord" bundle="sysprop"/></a>
								<%} %>
							</td>
						</tr>
						<tr>
							<td width="35%" align="right"><bean:message key="Customer.Territory" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="2">
								<html:select property="customer.territory" onchange="loadProvince();">
									<html:option value=""></html:option>
									<html:options collection="territorys" property="key" labelProperty="name"/>
								</html:select>
							</td>
							
						</tr>
						<tr>
						    <td align="right"><bean:message key="Address.Province" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" width="15%">
								<html:select property="customer.searchProvince" onchange="loadDistrict();">
								</html:select>
							</td>
							<td align="left">
							     เขต/อำเภอ
							     <html:select property="customer.district" styleId="district">
									<%-- <html:options collection="districts" property="id" labelProperty="name"/> --%>
								</html:select>
							</td>
						</tr>
						<tr>
							<td align="right"><bean:message key="Customer.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="2"><html:text property="customer.code"  styleClass="\" autoComplete=\"off" /></td>
						</tr>
						<tr>
							<td align="right"><bean:message key="Customer.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="2"><html:text property="customer.name"  styleClass="\" autoComplete=\"off" /></td>
						</tr>
						<tr>
							<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="2">
								<html:select property="customer.isActive">
									<html:options collection="actives" property="key" labelProperty="name"/>
								</html:select>
								
								&nbsp;&nbsp; <html:checkbox property="customer.dispTotalInvoice">&nbsp;แสดงยอดค้างชำระ</html:checkbox>
								&nbsp;&nbsp; <html:checkbox property="customer.dispHaveTrip">&nbsp;แสดงเฉพาะร้านที่มี Trip</html:checkbox>
							  
							</td>
						</tr>
					</table>
					
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								<input type="button" value="ค้นหา" class="newPosBtn" onclick="search('${pageContext.request.contextPath}')">
								<input type="button" value="Clear" class="newNegBtn" onclick="clearForm('${pageContext.request.contextPath}')">
								
								  <%-- <a href="#" onclick="return MarkLocationMap('${pageContext.request.contextPath}');">
									<input type="button" value="แสดงร้านค้าทั้งหมดบน แผนที่" class="newPosBtn">
								</a>  --%>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<%-- <a href="#" onclick="return exportToExcel('${pageContext.request.contextPath}');">
								 <b>....</b>
								</a>  --%>
							</td>
						</tr>
					</table>				
                    
					<!-- RESULT -->
					<!-- Paging -->
					 <c:if test="${customerForm.results != null}">
					 
					 <%if(Utils.isNull(session.getAttribute("dispHaveTrip")).equals("Y")) {%>
					     <span class="pagebanner">แสดงรายการ Trip </span>
							 <span class="pagelinks">จุด
								<%
								int curPage = customerForm.getCurPage();
								List<Customer> its= ((List)session.getAttribute("tripPageList"));
							    for(int i=0;i<its.size();i++){
							    	int trip =Integer.parseInt(its.get(i).getTripDay());
							    	if( curPage != trip){
								 %>
								     <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=trip%>')"><%=trip%></a>&nbsp;,
								 <% }else{ %>
								      &nbsp;<b><%=trip%>&nbsp;,</b>
								 <% } 
							     } %>
						    </span>
					 <%}else{
					     int totalPage = customerForm.getTotalPage();
					     int totalRecord = customerForm.getTotalRecord();
					     int currPage =  customerForm.getCurrPage();
					     int startRec = customerForm.getStartRec();
					     int endRec = customerForm.getEndRec();
					     int no = Utils.calcStartNoInPage(currPage, customerForm.getPageSize());
					   
						 out.println(PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no));
					   } %>
					    <table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr >
								<th >No.</th>
								<th >หมายเลขลูกค้า</th>
								<th >ชื่อ</th>
								<th >ที่อยู่</th>
								<th >วงเงินสินเชื่อ</th>
								<th >ยอดบิลค้างชำระ</th>
								<th >สถานะ</th>
								<th >ทำรายการขาย</th>
								<% if( role.equalsIgnoreCase(User.TT)){ %>
								  <th>ทำรายการรับเงิน</th>
								 <!--  <th class="cust_actionReceipt">ทำจัดรายการ</th> -->
								<%} %>
								<%if( role.equalsIgnoreCase(User.VAN)){ %>
								   <th>แก้ไข ข้อมูลลูกค้า</th>
								<%}else{ %>
								  <!--  <th class="cust_actionEditCust" >เปิดบิลพิเศษ</th> -->
								 <!--  <th class="cust_actionEditCust" >ตั้งกองโชว์</th> -->
								<%} %>
								<th >แสดง</th>
								<th >ทำรายการ</th>
								<th>แสดงรูปร้านค้า</th>
							</tr>	
							<c:forEach var="item" items="${customerForm.results}" varStatus="rows">
							<c:choose>
								<c:when test="${item.no %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when> 
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>">
								<td class="td_text_center"><c:out value='${item.no}'/></td>
								<td class="td_text_center"><c:out value='${item.code}'/></td>
								<td class="td_text"><c:out value='${item.name}'/></td>
								<td class="td_text"><c:out value='${item.addressSummary}'/></td>
								<td class="td_text_right"><fmt:formatNumber pattern="#,##0.00" value="${item.creditLimit}"/></td>
								<td class="td_text_right"><fmt:formatNumber pattern="#,##0.00" value="${item.totalInvoice}"/></td>
								<td class="td_text"><c:out value='${item.activeLabel}'/></td>
								
								<td class="td_text_center">
			 					    <c:if test="${item.isActive=='Y'}">
								    <a href="#" onclick="toCreateNewOrder('${pageContext.request.contextPath}','add',${item.id})">
							           <img src="${pageContext.request.contextPath}/images2/b_order.png" width="32" height="32" border="0" class="newPicBtn">
							        </a> 
							        </c:if>
								</td>
								
								<% if( role.equalsIgnoreCase(User.TT)){ %>
									<td class="td_text_center">
									 <c:if test="${item.displayActionReceipt==''}">
									   <c:if test="${item.isActive=='Y'}">
									    <a href="#" onclick="toCreateNewReceipt('${pageContext.request.contextPath}','add','${item.id}');">
									         <img src="${pageContext.request.contextPath}/images2/b_receipt.jpg" width="32" height="32" border="0" class="newPicBtn"/>
									    </a>
									    </c:if>
									 </c:if>
									</td>
									<%-- <td class="td_text">
									 <c:if test="${item.isActive=='Y'}">
									     <a href="#" onclick="toCreateNewReqPromotion('${pageContext.request.contextPath}','${item.id}','customerSearch');">
									       <img src="${pageContext.request.contextPath}/images2/b_reqpromotion.png" width="64" height="20" border="0" class="newPicBtn"/>
									     <b>  จัดรายการ</b>
									    </a> 
									   </c:if>
									</td> --%>
								<%} %>
								<% if( role.equalsIgnoreCase(User.VAN)){ %>
									<td class="td_text_center">
									   <c:if test="${item.displayActionEditCust==''}">
										   <c:if test="${item.canActionEditCust=='true'}">
										     <c:if test="${item.isActive=='Y'}">
									          <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit','${item.id}');">
											     <img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif">
											  </a>
											  </c:if>
											</c:if>
											<c:if test="${item.canActionEditCust2=='true'}">
											 <c:if test="${item.isActive=='Y'}">
									          <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit2','${item.id}');">
											     <img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif">
											  </a>
											  </c:if>
											</c:if>
									   </c:if>
									</td>
							  <% }else if( role.equalsIgnoreCase(User.TT)){ %>
							   <!-- Edit Customer -->
								<%-- <td class="td_text">
							        <c:if test="${item.canActionEditCust2=='true'}">
							           <c:if test="${item.isActive=='Y'}">
									        <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit2','${item.id}');">
											    <img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif">
									       </a>
									    </c:if>
									</c:if>
								</td> --%>
								<!-- Order Special -->
								<%-- <td class="td_text">
									 <c:if test="${item.isActive=='Y'}">
								       <a href="#" onclick="toCreateNewOrderSpecial('${pageContext.request.contextPath}','add',${item.id})">
							            <img src="${pageContext.request.contextPath}/images2/b_order_special.png" width="32" height="32" border="0" class="newPicBtn">
							          </a>  
							        </c:if>
								</td> --%>
								<%-- <td class="td_text">
									 <c:if test="${item.isActive=='Y'}">
								       <a href="#" onclick="manageProdShowTT('${pageContext.request.contextPath}','${item.code}');">
							                <b>  ตั้งกองโชว์</b>
							          </a>  
							        </c:if>
								</td> --%>
							<%} %>
							
								<td class="td_text_center">
								 <c:if test="${item.isActive=='Y'}">
								   <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','${item.id}');">
									   <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif">
									</a>
									</c:if>
								</td>
								<td class="td_text_center">
								  <c:if test="${item.isActive=='Y'}">
								    <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','process','${item.id}');">
									  <img border=0 src="${pageContext.request.contextPath}/icons/process.gif">
								    </a>
								  </c:if>
								</td>
								<td class="td_text_center">
								    <c:if test="${item.imageFileName != ''}">
										<a href="#" onclick="return showImage('${pageContext.request.contextPath}','${item.id}','${item.imageFileName}');">
											<b>แสดงรูปภาพ </b>
										 </a>
									 </c:if>
							    </td>
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
