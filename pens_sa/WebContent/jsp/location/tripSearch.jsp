
<%@page import="com.isecinc.pens.bean.CConstantsBean"%>
<%@page import="com.pens.util.CConstants"%>
<%@page import="com.pens.util.PageVisit"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.location.LocationBean"%>
<%@page import="com.isecinc.pens.web.location.LocationForm"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.web.location.LocationInitial"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="locationForm" class="com.isecinc.pens.web.location.LocationForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "locationForm");
/** Count Visit Page */
PageVisit.processPageVisit(request,"Trip");

/** Get CustTypeLsit **/
 List<CConstantsBean> custTypeList = CConstants.getConstantsDataList(CConstants.CUST_TYPE);
 String selected ="";
 CConstantsBean custTypeItemBean = null;
 int i=0;
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/trip.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
function loadMe(path){

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

function search(path,currPage){
	 var salesrepCode = document.getElementById("salesrepCode");
	 if(salesrepCode.value ==""){
		 alert("กรุณาระบุ พนักงานขาย ");
		 salesrepCode.focus();
		 return false;
	 }
	var form = document.locationForm;
	form.action = path + "/jsp/locationAction.do?do=search&action=newsearch&pageName=<%=request.getParameter("pageName")%>&currPage="+currPage;
	form.submit();
	return true;
}
function exportReport(path){
	var salesrepCode = document.getElementById("salesrepCode");
	 if(salesrepCode.value ==""){
		 alert("กรุณาระบุ พนักงานขาย ");
		 salesrepCode.focus();
		 return false;
	 }
    document.locationForm.action = path + "/jsp/locationAction.do?do=exportReport";
    document.locationForm.submit();
    return true;
}
function gotoPage(path,currPage){
	var form = document.locationForm;
	form.action = path + "/jsp/locationAction.do?do=search&pageName=<%=request.getParameter("pageName")%>&currPage="+currPage;
    form.submit();
    return true;
}
function viewTripDetail(path,customerCode,currTrip,salesrepId){
	var form = document.locationForm;
	var param = "customerCode="+customerCode+"&currTrip="+currTrip;
		param +="&custCatNo="+form.custCatNo.value;
		param +="&salesChannelNo="+form.salesChannelNo.value;
	    param +="&province="+form.province.value;
	    param +="&district="+form.district.value;
	    if(salesrepId ==''){
	       param +="&salesrepId="+form.salesrepCode.value;
	    }else{
	       param +="&salesrepId="+salesrepId;	
	    }
	   if(form.salesrepCode.value ==""){
		   alert("กรุณาระบุพนักงานขาย ก่อน เพิ่มข้อมูล");
		   form.salesrepCode.focus();
		   return false;
	   } 
	    
	var url = path + "/jsp/location/tripDetail.jsp?"+param;
	PopupCenter(url,"Manage Cust Trip",800,700);
	return true;
}
function validateTripDay(tripDayObj){
	if(tripDayObj.value != ""){
		//validate is number
		if( !isNumPositive(tripDayObj)){
			return false;
		}
		//validate x>0 and x<23
		if(tripDayObj.value > 23 && tripDayObj.value != 98){
			alert("ไม่สามารถระบุ จุดได้มากกว่าวันที่ 23");
			tripDayObj.focus();
			tripDayObj.value ="";
			return false;
		}
		if(tripDayObj.value <= 0){
			alert("ไม่สามารถระบุ จุดเป็น 0 ");
			tripDayObj.focus();
			tripDayObj.value ="";
			return false;
		}
	}
	return true;
}
function saveTripByCustAjax(index,customerCode){
	var form = document.locationForm;
	var returnString = "";
	var tripDay = document.getElementsByName("tripDay")[index];
	var tripDay2 = document.getElementsByName("tripDay2")[index];
	var tripDay3 = document.getElementsByName("tripDay3")[index];
	var customerType = document.getElementsByName("customerType")[index];
	
	if(form.custCatNo.value =='C'){//check VanSales only
		if(customerType.value ==""){
			alert("กรุณาระบุประเภทร้านค้า");
			customerType.focus();
			return false;
		}
	}
	var param  ="&tripDay="+tripDay.value;
	    param +="&tripDay2="+tripDay2.value;
	    param +="&tripDay3="+tripDay3.value;
	    param +="&customerType="+customerType.value;
	    
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/location/ajax/saveTripByCustAjax.jsp",
			data : "customerCode=" + customerCode +param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if(returnString =='บันทึกข้อมูลเรียบร้อย'){
		alert(returnString);
	}else{
		alert(returnString +"\n"+"กรุณา กดบันทึกข้อมูล อีกครั้ง");
	}
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
				<jsp:param name="function" value="Trip"/>
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
								  	 <td width="20%" align="left" nowrap>
								  	     <b>ประเภทขาย</b>&nbsp;&nbsp;
								  	    <html:select property="bean.custCatNo" styleId="custCatNo" styleClass="txt_style" 
								  	    onchange="loadSalesrepCodeList('${pageContext.request.contextPath}')">
								       <html:options collection="CUST_CAT_LIST" property="custCatNo" labelProperty="custCatDesc"/>
							           </html:select>
							           </td>
								     <td width="10%" align="right" nowrap><b>ภาคการขาย</b> </td>
								     <td width="30%" align="left" nowrap>
								      <html:select property="bean.salesChannelNo" styleId="salesChannelNo" 
								      onchange="loadSalesrepCodeList('${pageContext.request.contextPath}')">
						                <html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
				                      </html:select>
				                       &nbsp;&nbsp;          
										<b>ภาคตามสายดูแล </b>
									  <html:select property="bean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList('${pageContext.request.contextPath}')">
									    <html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
									  </html:select>
                                     </td> 
									<td width="10%" align="right" nowrap> <b>พนักงานขาย <font color="red">*</font></b> </td>
									<td width="10%" align="left">
									<html:select property="bean.salesrepCode" styleId="salesrepCode" >
										<html:options collection="SALESREP_LIST" property="salesrepId" labelProperty="salesrepCode"/>
								    </html:select>
									</td>
									<td width="10%" align="right"><b>Trip/จุด</b></td>
								    <td width="10%" align="left"> <html:text property="bean.tripDay"  styleId="tripDay" size="8"  
								    styleClass="\" autoComplete=\"off"/></td>
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
								       <html:text property="bean.customerCode"  styleId="customerCode" size="15" styleClass="\" autoComplete=\"off"/>
									   <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerLocation')"/>   
									</td>
									<td width="25%" align="right" nowrap> 
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
							<td align="center" width="80%">
								<input type="button" value="  ค้นหา  " class="newPosBtnLong" 
								onclick="search('${pageContext.request.contextPath}','')">
								 &nbsp;
								<input type="button" value="เพิ่มรายการใหม่" class="newPosBtnLong" 
								onclick="viewTripDetail('${pageContext.request.contextPath}','','<%=Utils.isNull(request.getParameter("currPage"))%>','')"> 
								 &nbsp;
								 <input type="button" value=" Export " class="newPosBtnLong" 
								 onclick="exportReport('${pageContext.request.contextPath}')">
								 &nbsp;
								<input type="button" value="  Clear  " class="newPosBtnLong" onclick="clearForm('${pageContext.request.contextPath}')">
							 <!--  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>จุด 99 (ยังไม่มีการ เซต trip/จุด)</b> -->
							
							</td>
						</tr>
					</table>					
				    <!-- ************************Result ***************************************************-->
				    <c:if test="${locationForm.results != null}">
				    <span class="pagebanner">แสดงรายการ Trip </span>
					 <span class="pagelinks">จุด
						<%
						int curPage = locationForm.getCurrPage();
						List<LocationBean> its= ((List)session.getAttribute("tripPageList"));
					    for(i=0;i<its.size();i++){
					    	int trip = Integer.parseInt(its.get(i).getTripDay()); 
					    	if( curPage != trip){
						 %>
						     <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=trip%>')"><%=trip%></a>&nbsp;,
						 <% }else{ %>
						      &nbsp;<b><%=trip%>&nbsp;,</b>
						 <% } 
					     } %>
				    </span>
				    
				    <table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
	                 <tr>
	                    <th>No</th>
			            <th>หมายเลขลูกค้า</th>
						<th>ชื่อ</th>
						<th>ที่อยู่</th>
						<!-- <th>Action</th> -->
					    <th>ประเภทร้านค้า</th>
						<th>จุด 1</th>
						<th>จุด 2</th>
						<th>จุด 3</th>
						<th>บันทึกการแก้ไข</th>
					</tr>
					<% 
					String tabclass ="lineE";
					List<LocationBean> resultList = locationForm.getResults();
					for(int n=0;n<resultList.size();n++){
						LocationBean mc = (LocationBean)resultList.get(n);
						if(n%2==0){
							tabclass="lineO";
						}
						%>
							<tr class="<%=tabclass%>">
							    <td class="td_text_center" width="4%"><%=(n+1)%></td>
								<td class="td_text" width="8%"><%=mc.getCustomerCode()%></td>
								<td class="td_text" width="10%"><%=mc.getCustomerName()%></td>
								<td class="td_text" width="25%"><%=mc.getAddress()%></td>
								<%-- <td class="td_text_center" width="10%">
							     <a  href="javascript:viewTripDetail('${pageContext.request.contextPath}','<%=mc.getCustomerCode()%>','','<%=mc.getSalesrepCode()%>')">แก้ไข/ดู</a>
								</td> --%>
								
								 <td class="td_text_center" width="5%">
								   <select name="customerType" id="customerType" disabled="true">
								    <option></option>
								   <%
									   selected ="";
									   for(i=0;i<custTypeList.size();i++){
										   custTypeItemBean = custTypeList.get(i);
										   selected = "";
										   if(custTypeItemBean.getValue().equalsIgnoreCase(mc.getCustomerType())){
											   selected ="selected";
										   }
									   %>
								       <option value="<%=custTypeItemBean.getValue() %>" <%=selected%>><%=custTypeItemBean.getCodeDesc() %></option>
								   <% } %>
								   </select>
								   <font color="red">*</font>
								</td> 
								<td class="td_text_center" width="5%">
								   <input type="text" name="tripDay" value="<%=mc.getTripDay()%>" 
								    onblur="validateTripDay(this)" size ="3" class="enableNumber" autocomplete="off"/>
								</td>
								<td class="td_text_center" width="5%">
								   <input type="text" name="tripDay2" value="<%=mc.getTripDay2()%>" 
								    onblur="validateTripDay(this)" size ="3" class="enableNumber"  autocomplete="off"/>
								</td>
								<td class="td_text_center" width="5%">
								   <input type="text" name="tripDay3" value="<%=mc.getTripDay3()%>" 
								    onblur="validateTripDay(this)" size ="3" class="enableNumber"  autocomplete="off"/>
								</td>
							    <td class="td_text_center" width="8%">
							       <b>
							        <a href="javascript:saveTripByCustAjax(<%=n %>,'<%=mc.getCustomerCode()%>')">บันทึก</a>
							      </b>
								</td> 
							</tr>
					    <%} %>
					</table>
				    </c:if>
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
