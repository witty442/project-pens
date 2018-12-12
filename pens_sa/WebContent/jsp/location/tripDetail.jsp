<%@page import="com.isecinc.pens.bean.SalesrepBean"%>
<%@page import="com.isecinc.pens.dao.SalesrepDAO"%>
<%@page import="com.isecinc.pens.web.location.TripAction"%>
<%@page import="util.DBConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.web.location.LocationBean"%>
<%@page import="com.isecinc.pens.web.location.LocationForm"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.web.location.LocationInitial"%>
<%@page import="util.Utils"%>
<%@page import="util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="locationForm" class="com.isecinc.pens.web.location.LocationForm" scope="session" />
<%
Connection conn = null;
LocationBean beanCheck = null;
LocationBean bean = null;
User user = (User) request.getSession().getAttribute("user");
List<References> tripDayList = (List)session.getAttribute("tripDayList");
String action = Utils.isNull(request.getParameter("action"));
String customerCode = "";
String errorMsg = "";
String message = "";
String currTrip = "1";//defualt 1
if(Utils.isNull(request.getParameter("currTrip")).equals("")){
	if(session.getAttribute("tripPageList") != null){
		List<LocationBean> tripPageList =(List)session.getAttribute("tripPageList");
		currTrip = tripPageList.size()>=1?tripPageList.get(0).getTripDay():"1";
	}
}else{
	currTrip = Utils.isNull(request.getParameter("currTrip"));
}
	
String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
String salesChannelNo = Utils.isNull(request.getParameter("salesChannelNo"));
String province = Utils.isNull(request.getParameter("province"));
String district = Utils.isNull(request.getParameter("district"));
String salesrepId = Utils.isNull(request.getParameter("salesrepId"));
try {	
	conn = DBConnection.getInstance().getConnectionApps();
	System.out.println("action:"+action);
	
	if("saveEdit".equalsIgnoreCase(action)){
		customerCode = Utils.isNull(request.getParameter("customerCode"));
		
		String tripDay = Utils.isNull(request.getParameter("tripDay"));
		String tripDay2 = Utils.isNull(request.getParameter("tripDay2"));
		String tripDay3 = Utils.isNull(request.getParameter("tripDay3"));
		
		//get Cust trip Detail
		bean = new LocationBean();
		bean.setCustomerCode(customerCode); 
		bean = TripAction.searchCustomerTripDetail(conn, bean);
		
		//set new trip
		bean.setTripDay(tripDay);
		bean.setTripDay2(tripDay2);
		bean.setTripDay3(tripDay3);
		bean.setCreateUser(user.getUserName());
		
		//insert cust trip,update trip master
		String msg = TripAction.updateCustTrip(bean);
		
		//search refresh to display
		if( !"".equals(customerCode)){
		   bean = TripAction.searchCustomerTripDetail(conn, bean);
		}
		if(msg.startsWith("success")){
		   message ="บันทึกข้อมูลเรียบร้อย";
		}else{
		   errorMsg ="ไม่สามารถบันทึกข้อมูลเรียบร้อย \n"+msg;
		}

	}else if("saveNew".equalsIgnoreCase(action)){
		customerCode = Utils.isNull(request.getParameter("customerCode"));
		
		//validate customerCode
		//get Cust trip Detail
		bean = new LocationBean();
		bean.setCustomerCode(customerCode);
		beanCheck = TripAction.searchCustomerTripDetail(conn, bean);
		if(beanCheck ==null){
			errorMsg ="ไม่พบหมายเลขลลูกค้า ["+customerCode+"]  ในระบบ";
		}else{	
			String tripDay = Utils.isNull(request.getParameter("tripDay"));
			String tripDay2 = Utils.isNull(request.getParameter("tripDay2"));
			String tripDay3 = Utils.isNull(request.getParameter("tripDay3"));
		
			//set new edit trip
			bean = new LocationBean();
			bean.setCustomerCode(customerCode);
			bean.setTripDay(tripDay);
			bean.setTripDay2(tripDay2);
			bean.setTripDay3(tripDay3);
			//check db data to input screen fro update 
			if( Utils.isNull(bean.getTripDay()).equals("")){
				bean.setTripDay(beanCheck.getTripDayDB());
			}
			if( Utils.isNull(bean.getTripDay2()).equals("")){
				bean.setTripDay2(beanCheck.getTripDayDB2());
			}
			if( Utils.isNull(bean.getTripDay3()).equals("")){
				bean.setTripDay3(beanCheck.getTripDayDB3());
			}
			
			bean.setCreateUser(user.getUserName());
			bean.setCustAccountId(beanCheck.getCustAccountId());
			bean.setPartySiteId(beanCheck.getPartySiteId());
			
			//Update Trip Customer
			String msg = TripAction.updateCustTrip(bean);
			
			//search refresh to display
			if( !"".equals(customerCode)){
			   bean = TripAction.searchCustomerTripDetail(conn, bean);
			}
			if(msg.startsWith("success")){
			   message ="บันทึกข้อมูลเรียบร้อย";
			}else{
			   errorMsg ="ไม่สามารถบันทึกข้อมูลเรียบร้อย \n"+msg;
			}
		}
	}else{
		//add new
		customerCode = Utils.isNull(request.getParameter("customerCode"));
		System.out.println("add customerCode:"+customerCode);
		if("".equals(customerCode)){
		   //Case Add new
		   action = "add";
		   bean = new LocationBean();
		   bean.setCustomerCode(customerCode);
		   bean.setCustomerName("");
		   //Get salesre
		   SalesrepBean salesBean = SalesrepDAO.getSalesrepBeanById(conn, salesrepId);
		   if(salesBean != null){
			   bean.setSalesrepCode(salesBean.getCode());
		   }
		}else{
		   //Edit
		   bean = new LocationBean();
		   bean.setCustomerCode(customerCode);
		   
		   //view old data
		   bean = TripAction.searchCustomerTripDetail(conn, bean);
		}
	}//if
}catch(Exception e){
	e.printStackTrace();
} finally {
	try {
		 conn.close();
	} catch (Exception e2) {}
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>Manage Trip</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<script type="text/javascript">
function loadMe(path){
	setTimeout("reloadMainPage()",2000);
}
function reloadMainPage(){
	 <%if( !"".equals(message)){ %>
	   window.opener.search('${pageContext.request.contextPath}',document.getElementsByName("tripDay")[0].value);
	   //alert('<%=message%>');
	   window.close();
	 <%}%>
}
function save(path){
	<%if("add".equals(action)){%>
	   //validate customerCode
	   var customerCode = document.getElementsByName("customerCode")[0];
	   if(customerCode.value ==""){
		   alert("กรุณาระบุ หมายเลขลูกค้า ");
		   customerCode.focus();
		   return false;
	   }
	   document.getElementsByName("action")[0].value ="saveNew";
	<%}else{%>
	   document.getElementsByName("action")[0].value ="saveEdit";
	<%}%>
    var form = document.tempLocationForm;
	form.submit();
	return true; 
}
function openPopup(path,pageName){
	var form = document.tempLocationForm;
	var param = "&pageName="+pageName;
	param +="&hideAll=true";
	param +="&custCatNo="+form.custCatNo.value;
	param +="&salesChannelNo="+form.salesChannelNo.value;
    param +="&province="+form.province.value;
    param +="&district="+form.district.value;
	param +="&salesrepId="+form.salesrepId.value;
	
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterWithWinName(url,"",500,500,"xxWin");
}
function setDataPopupValue(code,desc,pageName){
	var form = document.tempLocationForm;
	if("CustomerLocation" == pageName){
		form.customerCode.value = code;
		form.customerName.value = desc;
		//get trip detail
		setTimeout(getCustName(form.customerCode),3000);;
	}
}

function getCustNameKeypress(e,custCode){
	var form = document.tempLocationForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			form.customerCode.value = '';
			form.tripDay.value = "";
			form.tripDay2.value = "";
			form.tripDay3.value = "";
		}else{
		    getCustName(custCode);
		}
	}
}
function getCustNameClick(){
	var form = document.tempLocationForm;
	if(form.customerCode.value != ""){
	   getCustName(form.customerCode);
	}
}
function getCustName(custCode){
	var returnString = "";
	var form = document.tempLocationForm;
		var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/location/ajax/getCustTripDetailAjax.jsp",
				data : "customerCode=" + custCode.value,
				async: false,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		
		if(returnString !=''){
			alert("พบข้อมูลร้านนี้มีการ บันทึก trip/จุด ไว้แล้ว ");
			var retArr = returnString.split("|");
			form.customerName.value = retArr[0];
			form.tripDay.value = retArr[1];
			form.tripDay2.value = retArr[2];
			form.tripDay3.value = retArr[3];
		}else{
			alert("ไม่พบข้อมูล");
			form.customerCode.focus();
			form.customerCode.value ="";
			form.customerName.value ="";
			form.tripDay.value ="";
			form.tripDay2.value ="";
			form.tripDay3.value ="";
		}
}
function closeForm(){
	window.close();
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe('${pageContext.request.contextPath}');MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">

<!-- BODY -->
<form name="tempLocationForm" method="post" action="tripDetail.jsp">
   <!-- Hidden field -->
    <input type="hidden" name="action" id="action" value ="<%=action %>"/>
    <input type="hidden" name="currTrip" id="currTrip" value ="<%=currTrip %>"/>
    <input type="hidden" name="custCatNo" id="custCatNo" value ="<%=custCatNo %>"/>
    <input type="hidden" name="salesChannelNo" id="salesChannelNo" value ="<%=salesChannelNo %>"/>
    <input type="hidden" name="province" id="province" value ="<%=province %>"/>
    <input type="hidden" name="district" id="district" value ="<%=district %>"/>
    <input type="hidden" name="salesrepId" id="salesrepId" value ="<%=salesrepId %>"/>
    
   <table width="80%" border="0" align="center" cellpadding="5" cellspacing="5">
      
     <tr class="txt_style" >
  	     <td width="60%" colspan ="5" align="left" nowrap> 
  	     <%if( !"".equals(errorMsg)){ %>
  	        <font size="3" color="red"><b><%=errorMsg %></b></font>
  	     <%} %>
  	      <%if( !"".equals(message)){ %>
  	        <font size="3" color="green"><b><%=message%></b></font>
  	     <%} %>
  	     </td>
     </tr>
     
	 <tr class="txt_style" >
  	     <td width="60%" colspan ="5" align="left" nowrap> 
  	     <%if( !"".equals(customerCode)){ %>
  	        <font size="3"><b>แก้ไข ข้อมูล Trip/จุด ของร้านค้า </b></font>
  	     <%}else{ %>
  	        <font size="3"><b>เพิ่มข้อมูล Trip/จุด  ของร้านค้า</b></font>
  	     <%} %>
  	     </td> 
     </tr>
     <tr class="txt_style" >
  	 <td  width="60%" colspan ="5" align="left" nowrap><b>รหัสพนักงาน</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  	        <input type="text" name="salesRepCode" value="<%=bean.getSalesrepCode() %>" class="disableText" readonly="true" size="20"/>
     </td>
    </tr>
	<tr class="txt_style" >
  	 <td  width="60%" colspan ="5" align="left" nowrap><b>หมายเลขลูกค้า</b>&nbsp;&nbsp;
  	   <%if( !"add".equals(action)) {%>
  	        <input type="text" name="customerCode" value="<%=bean.getCustomerCode() %>" class="disableText" readonly="true" size="20"/>
        <%}else{ %>
             <input type="text" name="customerCode" value="" size="20" onkeypress="getCustNameKeypress(event,this)"/>
             <input type="button" name="x0" class="btnSmallLong" value="ตรวจสอบข้อมูล Trip เดิม" onclick="getCustNameClick()"/> 
                
             <input type="button" name="x1" class="btnSmallLong" value="ค้นหาร้านค้า" onclick="openPopup('${pageContext.request.contextPath}','CustomerLocation')"/>   
        <%} %>
     </td>
    </tr>
    <tr class="txt_style" >
  	  <td width="60%" colspan ="5" align="left" nowrap><b>ชื่อ</b> &nbsp;&nbsp;&nbsp;&nbsp;
  	      <input type="text" name="customerName" value="<%=bean.getCustomerName() %>" 
  	      class="disableText" readonly="true" size="60"/>
     </td>
    </tr>
     <%if( !"add".equals(action)) { %>
	    <tr class="txt_style" >
	  	  <td width="60%" colspan ="5" align="left" nowrap> <b>ที่อยู่</b> &nbsp;&nbsp;
	          <textarea rows="2" cols="80" name="address" disabled="true"><%=bean.getAddress() %> </textarea>
	  	  </td>
	    </tr>
     <%} %>  
    <tr class="txt_style" >
  	 <td width="60%" align="left" colspan="5" nowrap><b>Trip/จุด 1)&nbsp;</b>
		<select name="tripDay">
		<% String selected= "";
		for(int r=0;r<tripDayList.size();r++){ 
		  References ref = tripDayList.get(r);
		  if("add".equalsIgnoreCase(action)){
			  selected =ref.getKey().equalsIgnoreCase(currTrip)?"selected":"";
		  }else{
			  selected =ref.getKey().equalsIgnoreCase(bean.getTripDay())?"selected":"";
		  }
		 %>
		   <option value="<%=ref.getKey()%>" <%=selected%>><%=ref.getName() %></option>
		<%} %>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
     <b>Trip/จุด  2)&nbsp; </b> 
      <select name="tripDay2">
		<% selected= "";
		for(int r=0;r<tripDayList.size();r++){ 
		  References ref = tripDayList.get(r);
		  selected =ref.getKey().equalsIgnoreCase(bean.getTripDay2())?"selected":"";
		 %>
		   <option value="<%=ref.getKey()%>" <%=selected%>><%=ref.getName() %></option>
		<%} %>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
       <b>Trip/จุด  3)&nbsp;</b> 
	   <select name="tripDay3"> 
		<% selected= "";
		for(int r=0;r<tripDayList.size();r++){ 
		  References ref = tripDayList.get(r);
		  selected =ref.getKey().equalsIgnoreCase(bean.getTripDay3())?"selected":"";
		 %>
		   <option value="<%=ref.getKey()%>" <%=selected%>><%=ref.getName() %></option>
		<%} %>
		</select>
	</td>
  </tr>
</table>
			
<br>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" class="body"  width="100%">
	<tr>
		<td align="center" width="80%">
			<input type="button" value="บันทึกข้อมูล" class="newPosBtnLong" onclick="save('${pageContext.request.contextPath}')">
		<input type="button" value="ปิดหน้านี้" class="newPosBtnLong" onclick="closeForm('${pageContext.request.contextPath}')">
		</td>
	</tr>
</table>					

</form>
<!-- BODY -->
					

</body>
</html>
