<%@page import="com.isecinc.pens.web.reportall.bean.OrderNissin"%>
<%@page import="com.isecinc.pens.web.projectc.ProjectCBean"%>
<%@page import="com.isecinc.pens.web.reportall.ReportAllForm"%>
<%@page import="com.isecinc.pens.web.reportall.ReportAllBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="reportAllForm" class="com.isecinc.pens.web.reportall.ReportAllForm" scope="session" />
<%
OrderNissin bean = ((ReportAllForm)session.getAttribute("reportAllForm")).getBean().getOrderNissin();
System.out.println("bean:"+bean);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));
	String pageName = Utils.isNull(request.getParameter("pageName"));
	String hideAll = "true";
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<script type="text/javascript">

function loadMe(){
	var form = document.reportAllForm;
	
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateFrom'));
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateTo'));
		
		loadProvince();
		<%if( !"".equals(Utils.isNull(reportAllForm.getBean().getOrderNissin().getCustomerNis().getProvinceId()))){ %>
		   document.getElementsByName('bean.customerNis.provinceId')[0].value = <%=reportAllForm.getBean().getOrderNissin().getCustomerNis().getProvinceId()%>;
		<%}%>
		
		loadDistrict();
		<%if( !"".equals(Utils.isNull(reportAllForm.getBean().getOrderNissin().getCustomerNis().getDistrictId()))){ %>
		  document.getElementsByName('bean.customerNis.districtId')[0].value = <%=reportAllForm.getBean().getOrderNissin().getCustomerNis().getDistrictId()%>;
		<% } %>
	}

	function loadProvince(){
		//notInProvinceId=178:Myanmar
		var cboProvince = document.getElementsByName('bean.orderNissin.customerNis.provinceId')[0];
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/getProvinceListAjax.jsp",
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
		var cboDistrict = document.getElementsByName('bean.orderNissin.customerNis.districtId')[0];
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/getDistrictListAjax.jsp",
				data : "refId=" + document.getElementsByName('bean.orderNissin.customerNis.provinceId')[0].value,
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					cboDistrict.innerHTML=returnString;
				}
			}).responseText;
		});
	}
	
	function search(path){
		var form = document.reportAllForm;
		form.action = path + "/jsp/reportAllAction.do?do=search";
		form.submit();
		return true;
	}
	function exportExcel(path){
		var form = document.reportAllForm;
		form.action = path + "/jsp/reportAllAction.do?do=export";
		form.submit();
		return true;
	}
	function clearForm(path){
		var form = document.reportAllForm;
		form.action = path + "/jsp/reportAllAction.do?do=prepare&action=new";
		form.submit();
		return true;
	}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
			<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="<%=pageName %>"/>
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
						<html:form action="/jsp/reportAllAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
					
				    	<!-- ***** Criteria ******* -->
				    	<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						    <tr>
				                <td align="right" width="40%"> ภาคตามสายดูแล  <font color="red"></font></td>
								<td colspan="2" align="left" width="50%">	
								      <html:select property="bean.orderNissin.salesZone" styleId="salesZone">
										<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
								    </html:select>
								   
								</td>
							</tr>
						   <tr>
							    <td align="right" width="40%">จังหวัด&nbsp;&nbsp;</td>
								<td align="left" width="10%">
									<html:select property="bean.orderNissin.customerNis.provinceId" styleId="provinceId" onchange="loadDistrict();">
									</html:select>
								</td>
								<td align="left" width="40%">
								     เขต/อำเภอ
								     <html:select property="bean.orderNissin.customerNis.districtId" styleId="districtId">
									</html:select>
								</td>
						  </tr>
						 <tr>
							    <td align="right" width="40%">ประเภทร้านค้า&nbsp;&nbsp;</td>
								<td align="left" width="10%">
									<html:select property="bean.orderNissin.customerNis.customerType" styleId="customerType" >
									    <html:option value=""></html:option>
										<html:option value="School">School</html:option>
									    <html:option value="Mini">Mini</html:option>
										<html:option value="Shop">Shop</html:option>
									</html:select>
								</td>
								<td align="left" width="40%">
								     Order ID
									 <html:text property="bean.orderNissin.id" styleId="orderId" styleClass="\" autoComplete=\"off" />
								</td>
						  </tr>
						  <tr>
								<td align="right">ชื่อร้านค้า&nbsp;&nbsp;</td>
								<td align="left" colspan="2">
								<html:text property="bean.orderNissin.customerNis.name"  styleClass="\" autoComplete=\"off" /></td>
							</tr>
						  <tr>
							    <td align="right" width="40%">รหัสเซล์ PENS&nbsp;&nbsp;</td>
								<td align="left" width="10%">
									<html:text property="bean.orderNissin.salesrepCode" styleId="salesrepCode" styleClass="\" autoComplete=\"off" />
								</td>
								<td align="left" width="40%">
								     สถานะ
								     <html:select property="bean.orderNissin.docStatus" styleId="docStatus" >
									    <html:option value=""></html:option>
										<html:option value="OPEN">OPEN</html:option>
									    <html:option value="PENDING">PENDING</html:option>
										<html:option value="COMPLETE">COMPLETE</html:option>
									</html:select>
								</td>
						  </tr>
							 <tr>
							    <td align="right" width="40%">จาก วันที่รายการ&nbsp;&nbsp;</td>
								<td align="left" width="10%">
									<html:text property="bean.orderNissin.orderDateFrom" styleId="orderDateFrom" readonly="true"/>
								</td>
								<td align="left" width="40%">
								     ถึง วันที่รายการ
								   <html:text property="bean.orderNissin.orderDateTo" styleId="orderDateTo" readonly="true"/>
								</td>
						  </tr>
					   </table>
				    	
				    	<!-- ***** Criteria ******* -->
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="60%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtnLong"> 
								</a>&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtnLong">
								</a>&nbsp;
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtnLong">
								</a>
							</td>
							 <td align="right" width="40%" nowrap></td>
						</tr>
					</table>
			  </div>
			
		     <!-- ****** RESULT ***************************************************************** -->
		     <%
		      if(bean!= null  && bean.getDataStrBuffer() != null ){
		    	 out.println(bean.getDataStrBuffer().toString());
		      }
		     %>
		     <!-- ******************************************************************************* -->
					<!-- hidden field -->
					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
					
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
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>