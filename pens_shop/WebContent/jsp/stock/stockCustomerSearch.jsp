<%@page import="util.SessionGen"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@page import="com.isecinc.pens.model.MDistrict"%>
<%@page import="util.AppversionVerify"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://displaytag.sf.net" prefix="display_cust" %>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="stockForm" class="com.isecinc.pens.web.stock.StockForm" scope="request" />
<%
String role = ((User)session.getAttribute("user")).getType();

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


String[] msg4 = new String[2];
if(request.getSession().getAttribute("appVersionCheckMsg") != null){
	msg4 =  (String[])request.getSession().getAttribute("appVersionCheckMsg");
}else{
	msg4 = AppversionVerify.getApp().checkAppVersion(request);
}
%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerTransaction.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript">

function loadMe(){
	loadProvince();
	
	document.getElementsByName('customer.searchProvince')[0].value = ${stockForm.customer.searchProvince};
	
	loadDistrict();
	<%if( !"".equals(stockForm.getCustomer().getDistrict())){ %>
	  document.getElementsByName('customer.district')[0].value = <%=stockForm.getCustomer().getDistrict()%>;
	<% } %>
}

function clearForm(path) {
	document.stockForm.action = path + "/jsp/stockAction.do?do=prepareCustomer"+"&action=new";//clearForm
	document.stockForm.submit();
}

function search(path){
	document.stockForm.action = path + "/jsp/stockAction.do?do=searchCustomer&search=new";
	document.stockForm.submit();
	return true;
}

function view(path,customer_id){
	var param ="&customer_id="+customer_id;
	    param +="&backAvgMonth="+document.getElementsByName('bean.backAvgMonth')[0].value;
	document.stockForm.action = path + "/jsp/stockAction.do?do=prepare&action=searchStock"+param;
	document.stockForm.submit();
	return true;
}

function createNewStock(path,customerId){
	var param = "&customer_id="+customerId;
	    param +="&backAvgMonth="+document.getElementsByName('bean.backAvgMonth')[0].value;
	document.stockForm.action = path + "/jsp/stockAction.do?do=createNewStock&backPage=stockCustomerSearch"+param;
	document.stockForm.submit();
	return true;
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
	$("#dialog").dialog({ height: 260,width:540,modal:false });
  });
 
 function close(){
	 $("#dialog").dialog('close');
 }
 
 function linkToInterfaces(path){
	window.location = path+"/jsp/interfaces/interfaces.jsp";
 }
 
 setTimeout(function(){ $("#dialog").dialog('close');},4000);
 
 <%}else{%>
 
 
 <%}%>
 
 function gotoPage(path,page){
		document.stockForm.action = path + "/jsp/stockAction.do?do=searchCustomer&rf=Y";
		document.getElementsByName('curPage')[0].value = page;
		document.stockForm.submit();
		return true;
	}
 
 
//change pv
 function changePV(pvid){
	 var disId = parseInt(document.getElementsByName('customer.district')[0].value);
	 alert("disId["+disId+"],pvid:"+pvid);
 	
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
				<jsp:param name="function" value="Stock"/>
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
						<html:form action="/jsp/stockAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						<tr>
								<td colspan="4" align="center">
							        <font color="black" size="5"> <b> ค้นหาข้อมูลสต๊อกร้านค้า</b> </font>
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
							<td align="left" colspan="2"><html:text property="customer.code"/></td>
						</tr>
						<tr>
							<td align="right"><bean:message key="Customer.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="2"><html:text property="customer.name"/></td>
						</tr>
						<tr>
							<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="2">
								<html:select property="customer.isActive">
									<html:options collection="actives" property="key" labelProperty="name"/>
								</html:select>
								&nbsp;&nbsp;&nbsp;
								<html:checkbox property="bean.haveStock"></html:checkbox>แสดงเฉพาะรายการที่มีบันทึกสต๊อก
							</td>
						</tr>
						<tr>
							<td align="right">ใช้ข้อมูลการสั่งซื้อย้อนหลังเฉลี่ย &nbsp;&nbsp;</td>
							<td align="left" colspan="2">
								<html:text property="bean.backAvgMonth" size="5" styleClass="normalCenterText"/>&nbsp;เดือน
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
							</td>
						</tr>
					</table>				
                    
					<!-- RESULT -->
					<!-- Paging -->
					 <c:if test="${stockForm.resultsCust != null}">
						<span class="pagebanner">พบรายการ  ${stockForm.totalRow} รายการ  ,แสดงรายการทั้งหมด </span>
						 <span class="pagelinks">หน้า
							<c:forEach var="i" begin="1" end="${stockForm.totalPage}">
							     <c:if test="${stockForm.curPage != i}">
								    <a href="javascript:gotoPage('${pageContext.request.contextPath}','${i}')"><c:out value="${i}"/></a>&nbsp;,
								 </c:if> 
								 <c:if test="${stockForm.curPage == i}">
								    &nbsp;<b><c:out value="${i}"/>&nbsp;,</b>
								 </c:if> 
						    </c:forEach>
					    </span>
					    
					    <table align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
							<tr >
								<th>No.</th>
								<th>หมายเลขลูกค้า</th>
								<th>ชื่อ</th>
								<th>ที่อยู่</th>
								<th>สร้างรายการบันทึกสต๊อก</th>
								<th>ดูประวัติ</th>
							</tr>	
							<c:forEach var="item" items="${stockForm.resultsCust}" varStatus="rows">
							<c:choose>
								<c:when test="${item.no %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>">
								<td class="td_text" width="5%"><c:out value='${item.no}'/></td>
								<td class="td_text" width="15%"><c:out value='${item.code}'/></td>
								<td class="td_text" width="20%"><c:out value='${item.name}'/></td>
								<td class="td_text" width="20%"><c:out value='${item.addressSummary}'/></td>
								
								<td class="td_text_center" width="10%">
								   <a href="#" onclick="javascript:createNewStock('${pageContext.request.contextPath}','${item.id}');">
									  <img border=0 src="${pageContext.request.contextPath}/icons/process.gif">
								   </a>
								</td>
								<td class="td_text_center" width="10%">
								   <a href="#" onclick="javascript:view('${pageContext.request.contextPath}','${item.id}');">
									   <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif">
									</a>
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
    <p align="center"><b>
     <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/softwareUpdater/SalesAppUpdater.jsp';"> <font color="red"><%=msg4[0]%></a> 
     OR <%=msg4[1] %></font></b>
    </p>
    <%=AppversionVerify.getMessageToSales(request)%>
	<p><b>กรุณาดึงข้อมูลจากส่วนกลาง อย่างน้อยวันละหนึ่งครั้ง  ก่อนทำ รายการขาย/รายการรับเงิน   เพื่อที่ข้อมูลจะได้ถูกต้อง</b></p>
	<p align="center"> <a href="javascript:close();"><input class="newPosBtn"  type="submit" onclick="linkToInterfaces('<%=request.getContextPath() %>');" value="ไปยังหน้าดึงข้อมูลจากส่วนกลาง"/></a>&nbsp;&nbsp;
	 <a href="javascript:close();"><input class="newPosBtn"  type="submit" onclick="close();" value="ปิดหน้าจอ"/></a></p>
</div>

</body>
</html>
