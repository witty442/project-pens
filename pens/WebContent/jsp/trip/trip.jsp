<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="tripForm" class="com.isecinc.pens.web.trip.TripForm" scope="request" />
<%
User user = (User) session.getAttribute("user");

String rf = request.getParameter("rf") != null ? (String) request.getParameter("rf") : "";
if(rf.equals("")){
	rf = request.getAttribute("rf") != null ? (String) request.getAttribute("rf") : "";
}

String sort = request.getAttribute("sort") != null ? (String) request.getAttribute("sort") : "";

List<References> roles = InitialReferences.getReferenes().get(InitialReferences.ROLE);
pageContext.setAttribute("roles",roles,PageContext.PAGE_SCOPE);

List<References> actives= InitialReferences.getReferenes().get(InitialReferences.ACTIVE);
pageContext.setAttribute("actives",actives,PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="userForm" class="com.isecinc.pens.web.user.UserForm" scope="request" />

<%@page import="com.isecinc.pens.bean.User"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/trip.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablednd_0_5.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" language="javascript">
//call ajax
function loadSales(e){
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/userQuery.jsp",
				data : "uCode=" + $('#uCode').val(),
				success: function(getData){
					var returnString = jQuery.trim(getData);
					$('#userId').val(returnString.split('||')[0]);
					$('#userName').val(returnString.split('||')[1]);
				}
			}).responseText;
		});
	}
}

function loadCustomer(e){
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/customerQuery.jsp",
				data : "cCode=" + $('#cCode').val() + "&uId=" + $('#userId').val(),
				success: function(getData){
					var returnString = jQuery.trim(getData);
					$('#cId').val(returnString.split('||')[0]);
					$('#cName').val(returnString.split('||')[1]);
				}
			}).responseText;
		});
	}
}

// Lookup sales
function showSales(path){
	window.open(path + "/jsp/pop/view/userViewPopup.jsp", "User List", "width=500,height=350,location=No,resizable=No");
}
function setSales(code, name){
	$('#uCode').val(code);
	$('#userName').val(name);
	loadSales(null);
}

//Lookup customer
function showMainCustomer(path){
	window.open(path + "/jsp/pop/view/customerViewPopup.jsp?uId="+ $('#userId').val(), "Customer List", "width=500,height=350,location=No,resizable=No");
}
function setMainCustomer(code, name){
	$('#cCode').val(code);
	$('#cName').val(name);
	loadCustomer(null);
}
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('tripDateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('tripDateTo'));
	loadId();
}

function sortTrip(path, type){
	document.tripForm.action = path + "/jsp/tripAction.do?do=search&rf=Y&type="+type+"&sort=Y";
	document.tripForm.submit();
	return true;
}

function loadId(){
	var id = document.getElementsByName('id');
	var i;
	for(i=0;i<id.length;i++){
		document.getElementsByName('ids')[0].value += ',' + id[i].value;
	}
}

function adjustTrip(){
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/adjustTripQuery.jsp",
			data : "",
			success: function(getData){
				var returnString = jQuery.trim(getData);
				$('#msg').html(returnString);
			}
		}).responseText;
	});
}

</script>

<!-- Move for new index. -->
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe(); MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="code" value=""/>
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
						<html:form action="/jsp/tripAction">
						<jsp:include page="../error.jsp"/>
						<!-- CRITERIA -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="42%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Sales" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
								    <html:text property="trip.user.code" styleId="uCode" onkeyup="loadSales(event)" size="15"/>&nbsp;
								    <html:hidden property="trip.user.id" styleId="userId"/>
									<a href="#" onclick="showSales('${pageContext.request.contextPath}');" id="lookUser">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"/></a>
								</td>
							</tr>
							<tr>
								<td align="right"></td>
								<td align="left">
									<html:text property="trip.user.name" styleId="userName" size="40" readonly="true" styleClass="disableText"/>&nbsp;
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Trip.No" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:text property="trip.tripDateFrom" styleId="tripDateFrom" readonly="true" size="15"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="To" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:text property="trip.tripDateTo" styleId="tripDateTo" readonly="true" size="15"/>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="javascript:search('${pageContext.request.contextPath}','admin')">
									<!--<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ค้นหา" >
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}','admin')">
									<!--<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">-->
									<input type="button" value="Clear" >
									</a>
									<a href="javascript:adjustTrip();">
									<input type="button" value="ปรับปรุง Trip" >
									</a>
									<%if(user.getRole().getKey().equals(User.ADMIN)){ %>
										<a href="#">
										<input type="button" value="คัดลอกทริปทั้งเดือน"  onclick="open_copy_trip_month('${pageContext.request.contextPath}')">
										</a>
									<%} %>
								</td>
							</tr>
						</table>
						<!-- RESULT -->
						<%if(!sort.equals("Y")){ %>
						<%if(rf.equals("Y")){ %>
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
						&nbsp;<span class="searchResult">${tripForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
						<table align="left" border="0" cellpadding="3" cellspacing="1" width="100%">
							<tr>
								<td colspan="4" align="left">
									&nbsp;<input type="button" value="เพิ่มรายการ" onclick="addTrip();"/>
									&nbsp;<input type="button" value="จัดเรียงลำดับทริปใหม่" onclick="sortTrip('${pageContext.request.contextPath}','admin');"/>
									&nbsp;<%if(user.getRole().getKey().equals(User.ADMIN)){ %>
										<input type="button" value="คัดลอกทริปทั้งวัน" onclick="open_copy_trip('${pageContext.request.contextPath}')"/>
									<%} %>
									<br/>
								</td>
							</tr>			
						</table>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th width="7%"><bean:message key="No"  bundle="sysprop"/></th>
								<th width="5%"><input type="checkbox" name="chkAll"
									onclick="checkSelect(this,document.getElementsByName('ids'));" /></th>
								<th width="20%"><bean:message key="Trip.TripDate"  bundle="sysele"/></th>
								<th><bean:message key="Trip.CustomerName"  bundle="sysele"/></th>
							</tr>
							<c:forEach var="results" items="${tripForm.results}" varStatus="rows">
							<c:choose>
								<c:when test="${rows.index %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}">
								<td width="7%"><c:out value='${rows.index+1}'/></td>
								<td align="center" width="5%"><input type="checkbox" name="ids" value="${results.id}" /></td>
								<td align="center" width="20%">${results.tripDateFrom}</td>
								<td align="left">
									${results.customer.code}&nbsp;${results.customer.name}&nbsp;${results.customer.name2}
									<input type="hidden" value="${results.customer.code}" name="customerCode"/>
								</td>
							</tr>
							</c:forEach>
						</table>
						<div id="div_new_record" style="display: none;">
							<table align="center" border="0" cellpadding="3" cellspacing="1" width="98%" class="result">
								<tr class="lineO">
									<td width="7%" align="center"><c:out value='${tripForm.criteria.searchResult + 1}'/></td>
									<td align="center" width="5%"><input type="checkbox" disabled="disabled"/></td>
									<td align="center" width="20%"><font color="red">*</font>
										<input type="text" id="trip_date" name="trip_date" size="15" readonly="readonly"/>
									</td>
									<td align="left">
										<bean:message key="Customer.Code" bundle="sysele"/>&nbsp;
										<input type="text" id="cCode" name="cCode" onkeyup="loadCustomer(event)" size="15"/>&nbsp;
										<a href="#" onclick="showMainCustomer('${pageContext.request.contextPath}');">
										<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"/></a>&nbsp;&nbsp;&nbsp;
										<input type="text" id="cName" name="cName" size="40" readonly="readonly" class="disableText"/>&nbsp;
										<html:hidden property="trip.customer.id" styleId="cId"/>
										<input type="button" value="บันทึก" onclick="save('${pageContext.request.contextPath}');"/>
										<input type="button" value="ยกเลิก" onclick="cancel();"/>
									</td>
								</tr>
							</table>
						</div>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<td align="left" colspan="12" class="footer">&nbsp;
									<a href="#" onclick="javascript:deleteTrip('${pageContext.request.contextPath}');"> 
									<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif">&nbsp;
									<bean:message key="Delete" bundle="sysprop"/></a>
								</td>
							</tr>
						</table>
						<%} %>
						<%} else {%>
							<br/><jsp:include page="sortTrip.jsp"/>
						<%} %>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									</a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<input type="hidden" name="tripId"/>
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
</body>
</html>