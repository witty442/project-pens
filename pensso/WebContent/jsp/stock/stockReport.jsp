<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@page import="com.isecinc.pens.model.MDistrict"%>
<%@page import="com.pens.util.AppversionVerify"%>
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

System.out.println("haveStock:"+stockForm.getBean().getHaveStock());
%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerTransaction.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('requestDateFrom'));
	new Epoch('epoch_popup', 'th', document.getElementById('requestDateTo'));
}

function search(path){
	document.stockForm.action = path + "/jsp/stockAction.do?do=stockReport";
	document.stockForm.submit();
	return true;
}
function exportToExcel(path){
	document.stockForm.action = path + "/jsp/stockAction.do?do=stockReport&action=exportToExcel";
	document.stockForm.submit();
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
				<jsp:param name="function" value="StockReport"/>
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
							        <font color="black" size="5"> <b> ค้นหาข้อมูลสต๊อกร้านค้า</b></font>
							    </td>
							</tr>
						<tr>
							<td width="35%" align="right">&nbsp;&nbsp;จากวันที่บันทึกสต๊อก</td>
							<td align="left" colspan="2">
							<html:text property="bean.requestDateFrom" styleId="requestDateFrom"  size="15"  styleClass=""/>
							&nbsp;&nbsp;
								ถึงวันที่บันทึกสต๊อก
							<html:text property="bean.requestDateTo" styleId="requestDateTo"  size="15" styleClass=""/>
							สถานะ
							<html:select property="bean.status">
							  <html:option value="SV">ปกติ(SV)</html:option>
							  <html:option value="VO">ยกเลิก(VO)</html:option>
							</html:select>
							
							 <html:checkbox property="bean.haveStock"></html:checkbox>แสดงเฉพาะรายการที่มีบันทึกยอดนับ 
							</td>
						</tr>
					</table>
					
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
							   <input type="button" value="Export To Excel" class="newPosBtn" onclick="exportToExcel('${pageContext.request.contextPath}')">
								&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="button" value="ค้นหา" class="newPosBtn" onclick="search('${pageContext.request.contextPath}')">
								<input type="button" value="Clear" class="newNegBtn" onclick="clearForm('${pageContext.request.contextPath}')">
							</td>
						</tr>
					</table>				
                    
					<!-- RESULT -->
					<% if(session.getAttribute("RESULTS") != null){
					   out.println((StringBuffer)session.getAttribute("RESULTS"));
					} %>
				    <!-- Result -->	
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						<tr>
							<td align="right">
								<a href="#" onclick="window.location='./mainpage.jsp'">
								<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
								</a>
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
</body>
</html>
