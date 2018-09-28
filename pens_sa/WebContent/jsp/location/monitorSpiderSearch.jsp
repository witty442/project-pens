<%@page import="util.CConstants"%>
<%@page import="com.isecinc.pens.bean.CConstantsBean"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="locationForm" class="com.isecinc.pens.web.location.LocationForm" scope="session" />
<%
String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}
//get constants config all by ref_code
Map<String, CConstantsBean> constantsMap = (Map<String, CConstantsBean>)request.getSession().getAttribute("CONSTANTS_MAP");

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
.config_txt{
  padding: 0.25em 0.25em;
  border:1px solid black;
  font-size: 13px;
  font-weight: bold;
}
a:link {
	 color: blue;
	 font-weight: bold;
	/*TEXT-DECORATION: none; */
}
a:visited {
	 color: blue;
	 font-weight: bold;
	/*TEXT-DECORATION: none; */
}
A:hover {
	 COLOR: blue;
	 font-weight: bold;
	/*TEXT-DECORATION: none; */
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/monitorSpider.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/DateUtils.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
function loadMe(path){
	new Epoch('epoch_popup','th',document.getElementById('day'));
	new Epoch('epoch_popup','th',document.getElementById('dayTo'));
	
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
function viewDetail(detailType,salesrepId,tripDate){
	var param = "&detailType="+detailType+"&salesrepId="+salesrepId+"&tripDate="+tripDate;
    document.locationForm.action = "<%=request.getContextPath()%>/jsp/locationAction.do?do=viewDetail"+param;
    document.locationForm.submit();
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
				<jsp:param name="function" value="MonitorSpider"/>
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
							   <tr><td colspan="8" align="left">
							        <fieldset>
									    <table width="100%" border="0" align="left" cellpadding="3" cellspacing="1">
										<tr class="txt_style" >
									     <td width="5%" align="left"><b>วันที่</b></td>
									     <td width="95%" align="left">
									      <html:text property="bean.day" readonly="true" styleId="day" size="15"/>
									      &nbsp;&nbsp;-&nbsp;&nbsp;
									      <html:text property="bean.dayTo" readonly="true" styleId="dayTo" size="15"/>
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
									<td width="10%" align="right" nowrap> <b>พนักงานขาย</b> </td>
									<td width="20%" align="left">
									<html:select property="bean.salesrepCode" styleId="salesrepCode" >
										<html:options collection="SALESREP_LIST" property="salesrepId" labelProperty="salesrepCode"/>
								    </html:select>
									</td>
									<td width="10%" align="right"></td>
								    <td width="20%" align="left"></td>
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
							 </table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body"  width="100%">
						<tr>
						<td align="right" width="32%"> 
							   <table align="right" border="0" cellpadding="3" >
							    <tr><td align="center">
							      <b>Credit Sales</b>
							    </td></tr>
							     <tr><td >
								   <%CConstantsBean bean = constantsMap.get("Credit"+CConstants.MIN_VISIT_BY_REAL);%>
								   <span class="config_txt" style="background-color:<%=bean.getValue2()%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
								     <u><b>คือ บันทึกเยี่ยมจริง น้อยกว่า <%=bean.getValue()%> ร้านค้า</b></u>
								 </td></tr>  
								 
								 <tr><td >
								   <%bean = constantsMap.get("Credit"+CConstants.MAX_NOT_EQUALS_MASLOC);
								   CConstantsBean bean2 = constantsMap.get(CConstants.MAX_DISTANCE);
								   %>
								   <span class="config_txt" style="background-color:<%=bean.getValue2()%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
								     <u><b> คือ บันทึกไกลกว่าพิกัดร้าน <%=Utils.convMeterToKilometer(bean2.getValue(),2)%> กม. มีมากกว่า  <%=bean.getValue()%> ร้านค้า</b></u>
							     </td></tr>
							     
								 <tr><td >
								   <%bean = constantsMap.get("Credit"+CConstants.MAX_NOT_EQUALS_TRIP);%>
								   <span class="config_txt" style="background-color:<%=bean.getValue2()%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
								      <u><b> คือ บันทึกไม่ตรงทริปที่กำหนด เกิน <%=bean.getValue()%> ร้านค้า</b></u>
								 </td></tr>
								 
							     <tr><td >
								  <% bean = constantsMap.get("Credit"+CConstants.MIN_VISIT_BY_TRIP);%>
								  <span class="config_txt" style="background-color:<%=bean.getValue2()%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
								   <u><b>คือ บันทึกเยี่ยมตาม Trip น้อยกว่า <%=bean.getValue()%> ร้านค้า</b></u> 
								</td></tr>
							  </table>
						   </td>
							<td align="center" width="36%" valign="bottom">
								<input type="button" value="ค้นหา" class="newPosBtnLong" onclick="search('${pageContext.request.contextPath}')">
								<input type="button" value="Export" class="newPosBtnLong" onclick="exportReport('${pageContext.request.contextPath}')"> 
								<input type="button" value="Clear" class="newPosBtnLong" onclick="clearForm('${pageContext.request.contextPath}')">
							</td>
							
						   <td align="right" width="32%"> 
							   <table align="right" border="0" cellpadding="3" >
							    <tr><td align="center">
							      <b>Van Sales</b>
							    </td></tr>
							     <tr><td >
								   <%bean = constantsMap.get("Van"+CConstants.MIN_VISIT_BY_REAL);%>
								   <span class="config_txt" style="background-color:<%=bean.getValue2()%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
								     <u><b>คือ บันทึกเยี่ยมจริง น้อยกว่า <%=bean.getValue()%> ร้านค้า</b></u>
								 </td></tr>  
								 
								 <tr><td >
								   <%bean = constantsMap.get("Van"+CConstants.MAX_NOT_EQUALS_MASLOC);
								     bean2 = constantsMap.get(CConstants.MAX_DISTANCE);
								   %>
								   <span class="config_txt" style="background-color:<%=bean.getValue2()%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
								     <u><b> คือ บันทึกไกลกว่าพิกัดร้าน <%=Utils.convMeterToKilometer(bean2.getValue(),2)%> กม. มีมากกว่า  <%=bean.getValue()%> ร้านค้า</b></u>
							     </td></tr>
							     
								 <tr><td >
								   <%bean = constantsMap.get("Van"+CConstants.MAX_NOT_EQUALS_TRIP);%>
								   <span class="config_txt" style="background-color:<%=bean.getValue2()%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
								      <u><b> คือ บันทึกไม่ตรงทริปที่กำหนด เกิน <%=bean.getValue()%> ร้านค้า</b></u>
								 </td></tr>
								 
							     <tr><td >
								  <% bean = constantsMap.get("Van"+CConstants.MIN_VISIT_BY_TRIP);%>
								  <span class="config_txt" style="background-color:<%=bean.getValue2()%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
								   <u><b>คือ บันทึกเยี่ยมตาม Trip น้อยกว่า <%=bean.getValue()%> ร้านค้า</b></u> 
								</td></tr>
							  </table>
						</td><tr>
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
