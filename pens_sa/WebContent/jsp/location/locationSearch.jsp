
<%@page import="util.PageVisit"%>
<%@page import="util.SessionUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.web.location.LocationInitial"%>
<%@page import="util.Utils"%>
<%@page import="util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="util.GoogleMapJavaScriptAPI"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<jsp:useBean id="locationForm" class="com.isecinc.pens.web.location.LocationForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "locationForm");
/** Count Visit Page */
PageVisit.processPageVisit(request,"Location");
		
String typeSearch = Utils.isNull(request.getAttribute("DATA"));
//String path = request.getP
java.util.List yearList = null;
if(session.getAttribute("yearList") != null) 
	yearList = (java.util.List)session.getAttribute("yearList");

java.util.List yearListASC = null;
if(session.getAttribute("yearListASC") != null) 
	yearListASC = (java.util.List)session.getAttribute("yearListASC");

String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->

fieldset { border:1px solid black }

legend {
  padding: 0.2em 0.5em;
  border:1px solid green;
  color:green;
  font-size:90%;
  text-align:left;
  }
  
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/location.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
function loadMe(path){
	new Epoch('epoch_popup','th',document.getElementById('day'));
	new Epoch('epoch_popup','th',document.getElementById('dayTo'));
	chkSearch();
	chkYear();
	
	//popup Map
	<%if(session.getAttribute("CUST_LOC_LIST") != null){%>
	  MarkLocationMap(path);
	<%}%>
	
	//hide table DATA
	if(document.locationForm.dispType.value =='MAP'){
	   showTable('table_map');
	   hideTable('table_data');
	}else{
	   showTable('table_data');
	   hideTable('table_map');
	}
	
	// Load SalesrepCodeList By CustCatNo salecChannel
	<%if( !Utils.isNull(locationForm.getBean().getCustCatNo()).equals("")
		|| !Utils.isNull(locationForm.getBean().getSalesChannelNo()).equals("")
			){%>
	   document.getElementsByName('bean.custCatNo')[0].value = "<%=Utils.isNull(locationForm.getBean().getCustCatNo())%>";
	   document.getElementsByName('bean.salesChannelNo')[0].value = "<%=Utils.isNull(locationForm.getBean().getSalesChannelNo())%>";
	   loadSalesrepCodeList('${pageContext.request.contextPath}');
	   document.getElementsByName('bean.salesrepCode')[0].value = "<%=Utils.isNull(locationForm.getBean().getSalesrepCode())%>";
	<%} %>
	
	// Load Amphur By Province
	<%if( !Utils.isNull(locationForm.getBean().getProvince()).equals("")){%>
	   document.getElementsByName('bean.province')[0].value = "<%=Utils.isNull(locationForm.getBean().getProvince())%>";
	   loadDistrict('${pageContext.request.contextPath}');
	   document.getElementsByName('bean.district')[0].value = "<%=Utils.isNull(locationForm.getBean().getDistrict())%>";
	<%} %>
}
</script>

</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe('${pageContext.request.contextPath}');MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="Location"/>
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
						<html:form action="/jsp/locationAction">
						<jsp:include page="../error.jsp"/>
						<table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
						
							   <tr><td colspan="8" align="right">
							        <fieldset>
									    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
										<tr class="txt_style" >
									  	<td width="13%" align="left"><b>รอบเวลา</b> &nbsp;&nbsp;<html:select property="bean.typeSearch" onchange="chkSearch()" styleClass="txt_style">
									         <html:options collection="typeSearchList" property="key" labelProperty="name"/>
								           </html:select>
								           </td>
									     <td width="6%" align="right"><b>วันที่</b></td>
									     <td width="27%" align="left">
									      <html:text property="bean.day" readonly="true" styleId="day" size="15"/>
									      &nbsp;&nbsp;-&nbsp;&nbsp;
									      <html:text property="bean.dayTo" readonly="true" styleId="dayTo" size="15"/>
	                                     </td> 
										<td width="2%" align="right"><b>ปี</b></td>
										<td width="6%" align="left">
										  <html:select property="bean.year" styleId="yearList" onchange="chkYear()" styleClass="txt_style">
									         <html:options collection="yearList" property="key" labelProperty="name"/>
							              </html:select>
										</td>
									    <td width="8%" align="right"></td>
									    <td width="20%" align="left"></td>
									     </tr>
									   </table>
								   </fieldset>
								</td></tr>
								<!-- ###################################### -->
								<tr><td colspan="8" align="left">
								     <fieldset>
									    <table width="100%" border="0"  class="disable_text">
									    <c:forEach var="item" items="${yearList}" >
                                         <tr id="${item.key}" class="txt_style" >
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}01</html:multibox>&nbsp;ม.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}02</html:multibox>&nbsp;ก.พ.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}03</html:multibox>&nbsp;มี.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}04</html:multibox>&nbsp;เม.ย.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}05</html:multibox>&nbsp;พ.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}06</html:multibox>&nbsp;มิ.ย.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}07</html:multibox>&nbsp;ก.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}08</html:multibox>&nbsp;ส.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}09</html:multibox>&nbsp;ก.ย.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}10</html:multibox>&nbsp;ต.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}11</html:multibox>&nbsp;พ.ย.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}12</html:multibox>&nbsp;ธ.ค.</td>
                                         </tr>
                                         </c:forEach>
                                       </table>
                                       </fieldset>
                                 </td></tr>
                                 <!-- ###################################### -->
                                  <tr><td colspan="8" align="right">
                                    <fieldset>
									    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
										<tr class="txt_style" >
									  	  <td width="100%" align="left"><b>ลักษณะการแสดงผล </b>&nbsp;&nbsp;
									  	  <html:select property="bean.dispType" styleId="dispType" onchange="changeDispCri(this)" styleClass="txt_style">
									         <html:options collection="dispTypeList" property="key" labelProperty="name"/>
								           </html:select>
								          </td>
								         </tr>
									   </table>
								   </fieldset>
								</td></tr>
								<!-- ###################################### -->
								<tr><td colspan="8" align="right">
								  <fieldset>
								    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
									<tr class="txt_style" >
								  	 <td width="20%" align="left" nowrap>
								  	     <b>ประเภทขาย</b>&nbsp;&nbsp;
								  	    <html:select property="bean.custCatNo" styleId="custCatNo" styleClass="txt_style" 
								  	    onchange="loadSalesrepCodeList('${pageContext.request.contextPath}')">
								       <html:options collection="CUST_CAT_LIST" property="custCatNo" labelProperty="custCatDesc"/>
							           </html:select>
							           </td>
								     <td width="10%" align="right"  nowrap><b>ภาคการขาย</b> </td>
								     <td width="10%" align="left">
								      <html:select property="bean.salesChannelNo" styleId="salesChannelNo" 
								      onchange="loadSalesrepCodeList('${pageContext.request.contextPath}')">
						                <html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
				                      </html:select>
                                     </td> 
                                    <td width="10%" align="right"nowrap><b>ภาคตามสายดูแล</b></td>
								    <td width="20%" align="left">
								    <html:select property="bean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList('${pageContext.request.contextPath}')">
									    <html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
									</html:select>
                                     </td>
									<td width="10%" align="right" nowrap> <b>พนักงานขาย</b> </td>
									<td width="20%" align="left">
									<html:select property="bean.salesrepCode" styleId="salesrepCode" >
										<html:options collection="SALESREP_LIST" property="salesrepId" labelProperty="salesrepCode"/>
								    </html:select>
									</td>
								    </tr>
								   </table>
								   </fieldset>
								</td></tr>
								<!-- ################################## -->
								<tr><td colspan="8" align="right">
								   <fieldset>
								    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
									<tr class="txt_style" >
								  	<td width="15%" align="left" nowrap>
								  	    <b>จังหวัด</b> &nbsp;&nbsp;
								  	    <html:select property="bean.province" styleId="province" styleClass="txt_style" onchange="loadDistrict('${pageContext.request.contextPath}');">
								           <html:options collection="PROVINCE_LIST" property="province" labelProperty="provinceName"/>
							           </html:select>
							         </td>
								     <td width="10%" align="right" nowrap><b>อำเภอ</b></td>
								     <td width="10%" align="left">
								      <html:select property="bean.district" styleId="district" >
						                <%-- <html:options collection="DISTRICT_LIST" property="district" labelProperty="districtName"/> --%>
				                      </html:select>
                                     </td> 
									<td width="10%" align="right" nowrap> <b>รหัสร้านค้า</b> </td>
									<td width="15%" align="left"  nowrap>
								       <html:text property="bean.customerCode"  styleId="customerCode" size="15"/>
									  <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerLocation')"/>   
									</td>
									<td width="25%" align="right" nowrap> 
									 <b>ประเภทร้านค้า</b>&nbsp;
										     <html:select property="bean.customerType" styleId="customerType" styleClass="txt_style">
									            <html:option value="">ร้านค้าทั้งหมด</html:option>
									            <html:option value="P">ร้านโชห่วย</html:option>
									            <html:option value="B">ร้านธงฟ้า</html:option>
								            </html:select>
									</td>
								    <td width="12%" align="left"></td>
								    </tr>
								   </table>
								   </fieldset>
								</td></tr>
								<!-- ################################## -->
								<tr><td colspan="8" align="right">
							    	<fieldset>
								    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1"  id="table_map">
									  <tr class="txt_style" >
							              <td  width="20%" align="right" valign="bottom" nowrap>
                                               <html:checkbox property="bean.dispAllNoOrder">&nbsp;<b>ร้านค้าที่ไม่ได้ขาย</b></html:checkbox>
									  	       <img src="${pageContext.request.contextPath}/icons/chk_no_order.png" width="16px" height ="16px"/>
									      </td>
									      <td  width="20%" align="right" valign="bottom" nowrap>
                                             <html:checkbox property="bean.dispAllOrder">&nbsp;<b>ร้านที่ได้ขาย</b></html:checkbox>
									  	     <img src="${pageContext.request.contextPath}/icons/chk_order.png" width="16px" height ="16px"/>
									      </td>
									     <td  width="20%" align="right" valign="bottom" nowrap>
										      <html:checkbox property="bean.dispAllVisit">&nbsp;<b>ร้านที่ได้เยี่ยม</b></html:checkbox>
										  	  <img src="${pageContext.request.contextPath}/icons/chk_visit.png" width="16px" height ="16px"/>  
	                                     </td> 
										<td width="15%" align="left"> </td>
									  </tr>
								   </table>
								   
								   <!-- Table DATA -->
								   <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1" id="table_data">
									<tr class="txt_style" >
								     <td width="35%" align="left" colspan="3"><b>รูปแบบการแสดงผล</b>
								     <html:select property="bean.reportType" styleId="reportType" styleClass="txt_style">
								           <html:options collection="REPORT_TYPE_LIST" property="key" labelProperty="name"/>
							           </html:select>
                                     </td> 
									<td width="10%" align="right"><b>ข้อมูล Trip</b></td>
									<td width="25%" align="left">
									 <html:select property="bean.tripType" styleId="tripType" styleClass="txt_style">
								           <html:options collection="TRIP_TYPE_LIST" property="key" labelProperty="name"/>
							           </html:select>
									</td>
								    <td width="15%" align="left"></td>
								    </tr>
								   </table>
								   
								  </fieldset>
								</td></tr>
							   <!-- ################################## -->
							 </table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body"  width="100%">
						<tr>
							<td align="center" width="80%">
								<input type="button" value="ค้นหา" class="newPosBtnLong" onclick="search('${pageContext.request.contextPath}')">
								<input type="button" value="Export" class="newPosBtnLong" onclick="exportReport('${pageContext.request.contextPath}')"> 
								<input type="button" value="Clear" class="newPosBtnLong" onclick="clearForm('${pageContext.request.contextPath}')">
							 
							 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							 <img src="${pageContext.request.contextPath}/icons/generalshop.png" width="32px" height ="32px"/><b>ร้านโชห่วย</b>
							   <img src="${pageContext.request.contextPath}/icons/blueflagicon.png" width="32px" height ="32px"/><b>ร้านธงฟ้า</b>
							</td>
							
						</tr>
					</table>					
				    <!-- ************************Result ***************************************************-->
					  <%
					 // System.out.println("Results:"+request.getSession().getAttribute("RESULTS"));
					  if(request.getSession().getAttribute("RESULTS") != null) {
					     out.println(request.getSession().getAttribute("RESULTS"));
					  }
					  %>
					<!-- ************************Result ***************************************************-->
					<!-- Hidden Field -->
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
</body>
</html>
