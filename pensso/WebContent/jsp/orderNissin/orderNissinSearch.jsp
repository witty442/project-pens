<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.isecinc.pens.bean.OrderNissin"%>
<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="orderNissinForm" class="com.isecinc.pens.web.ordernissin.OrderNissinForm" scope="request" />
<%
User user = (User)session.getAttribute("user");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/orderNissin.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('orderDateFrom'));
	new Epoch('epoch_popup', 'th', document.getElementById('orderDateTo'));
	
	loadProvince();
	<%if( !"".equals(Utils.isNull(orderNissinForm.getBean().getCustomerNis().getProvinceId()))){ %>
	   document.getElementsByName('bean.customerNis.provinceId')[0].value = <%=orderNissinForm.getBean().getCustomerNis().getProvinceId()%>;
	<%}%>
	
	loadDistrict();
	<%if( !"".equals(Utils.isNull(orderNissinForm.getBean().getCustomerNis().getDistrictId()))){ %>
	  document.getElementsByName('bean.customerNis.districtId')[0].value = <%=orderNissinForm.getBean().getCustomerNis().getDistrictId()%>;
	<% } %>
}

function loadProvince(){
	//notInProvinceId=178:Myanmar
	var cboProvince = document.getElementsByName('bean.customerNis.provinceId')[0];
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
	var cboDistrict = document.getElementsByName('bean.customerNis.districtId')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/DistrictAjax.jsp",
			data : "refId=" + document.getElementsByName('bean.customerNis.provinceId')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
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
	    	 <%if ( !UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS_PENS}) ){%>
              	<jsp:include page="../program.jsp">
				   <jsp:param name="function" value="OrderNissinSearch2"/>
			   </jsp:include>
             <%}else{ %>
              	<jsp:include page="../program.jsp">
				    <jsp:param name="function" value="OrderNissinSearch"/>
			    </jsp:include>
            <%} %>
	      
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
						<html:form action="/jsp/orderNissinAction">
						<jsp:include page="../error.jsp"/>
						
						
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						   <tr>
							    <td align="right" width="40%">จังหวัด&nbsp;&nbsp;</td>
								<td align="left" width="10%">
									<html:select property="bean.customerNis.provinceId" styleId="provinceId" onchange="loadDistrict();">
									</html:select>
								</td>
								<td align="left" width="40%">
								     เขต/อำเภอ
								     <html:select property="bean.customerNis.districtId" styleId="districtId">
									</html:select>
								</td>
						  </tr>
						 <tr>
							    <td align="right" width="40%">ประเภทร้านค้า&nbsp;&nbsp;</td>
								<td align="left" width="10%">
									<html:select property="bean.customerNis.customerType" styleId="customerType" >
									    <html:option value=""></html:option>
										<html:option value="School">School</html:option>
									    <html:option value="Mini">Mini</html:option>
										<html:option value="Shop">Shop</html:option>
									</html:select>
								</td>
								<td align="left" width="40%">
								     Order ID
									 <html:text property="bean.id" styleId="orderId" styleClass="\" autoComplete=\"off" />
								</td>
						  </tr>
						  <tr>
								<td align="right">ชื่อร้านค้า&nbsp;&nbsp;</td>
								<td align="left" colspan="2">
								<html:text property="bean.customerNis.name"  styleClass="\" autoComplete=\"off" /></td>
							</tr>
						  <tr>
							    <td align="right" width="40%">รหัสเซล์ PENS&nbsp;&nbsp;</td>
								<td align="left" width="10%">
									<html:text property="bean.salesrepCode" styleId="salesrepCode" styleClass="\" autoComplete=\"off" />
								</td>
								<td align="left" width="40%">
								     สถานะ
								     <html:select property="bean.docStatus" styleId="docStatus" >
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
									<html:text property="bean.orderDateFrom" styleId="orderDateFrom" readonly="true"/>
								</td>
								<td align="left" width="40%">
								     ถึง วันที่รายการ
								   <html:text property="bean.orderDateTo" styleId="orderDateTo" readonly="true"/>
								</td>
						  </tr>
					   </table>
					   
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								  <input type="button" value="    ค้นหา      " class="newPosBtnLong" 
								  onclick="search('${pageContext.request.contextPath}')"> 
								  
								  <input type="button" value="   Clear   " class="newPosBtnLong" 
								  onclick="clearSearch('${pageContext.request.contextPath}')">	
								  
								  <input type="button" value="Export To Excel" class="newPosBtnLong" 
								  onclick="exportToExcel('${pageContext.request.contextPath}')">
								  
								  <%if(Utils.isNull(request.getParameter("fromPage")).equalsIgnoreCase("customerNissinDetail")){ %>
									  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									  <input type="button" value="ย้อนกลับ (ร้านค้า)" class="newPosBtnLong" 
									  onclick="backToCustDetail('${pageContext.request.contextPath}')">	
								  <%}%>										
							</td>
						</tr>
					</table>
					<!-- RESULT -->
				    
			        <c:if test="${orderNissinForm.results != null}">
							<% 
					   int totalPage = orderNissinForm.getTotalPage();
					   int totalRecord = orderNissinForm.getTotalRecord();
					   int currPage =  orderNissinForm.getCurrPage();
					   int startRec = orderNissinForm.getStartRec();
					   int endRec = orderNissinForm.getEndRec();
					   int pageSize = orderNissinForm.getPageSize();
					   int no = Utils.calcStartNoInPage(currPage, pageSize);
					%>
					
					<%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
					
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearch">
						       <tr>
						         	<th >บันทึกรายละเอียด</th>
						            <th >Order Id</th>
						            <th >วันที่ Nissin คีย์ สั่งซื้อ</th>
						            <th >Ref Cust ID</th>
						            <th >ชื่อร้านค้า</th>
						            <th >จังหวัด</th>
						            <th >อำเภอ</th>
									<th >สถานะ</th>
									<th >รหัสเซลล์ PENS</th>
									<th>ผู้บันทึก Order</th>
									<% if( !UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS})){%>
										<th >Invoice No</th>
										<th >Invoice Date</th>
										<th >Oracle Cust No</th>
										<th >ผู้บันทึก Order</th>
									<%}else{ %>
										<th >Invoice Date</th>
									<%} %>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<OrderNissin> resultList = orderNissinForm.getBean().getItemsList();
							
							for(int n=0;n<resultList.size();n++){
								OrderNissin mc = (OrderNissin)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>">
									   <td class="td_text_center" width="7%">
										  <font size="3">
										    <b>
											     <a href="javascript:viewDetail('${pageContext.request.contextPath}','<%=mc.getId()%>')">
											      
											      <%if ( UserUtils.userInRole("ROLE_ALL",user,new String[]{User.ADMIN,User.NIS_PENS,User.NIS}) ){%>
											          <%if(mc.isCanEdit()){ %>
											             <img src="${pageContext.request.contextPath}/icons/process.gif">
											         <%}else{ %>
											             <img src="${pageContext.request.contextPath}/icons/lookup.gif">
											         <%} %>
											         
											      <%}else{%>
											          <%if ( UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS_VIEW}) ){%>
											            <img src="${pageContext.request.contextPath}/icons/lookup.gif">
											          <%} %>
											      <% }%>
												 </a>
											 </b>
										  </font>
										</td>
									  <td class="td_text_center" width="5%"><%=mc.getId()%></td>
									  <td class="td_text_center" width="5%"><%=mc.getOrderDate()%></td>
									  <td class="td_text_center" width="4%"><%=mc.getCustomerNis().getId()%></td>
									  <td class="td_text_center" width="10%"><%=mc.getCustomerNis().getName()%></td>
									  <td class="td_text_center" width="10%"><%=mc.getCustomerNis().getProvinceName()%></td>
									  <td class="td_text_center" width="10%"><%=mc.getCustomerNis().getDistrictName()%></td>
									  
									  <td class="td_text_center" width="5%"><%=mc.getDocStatus()%></td>
									  <td class="td_text_center" width="5%"><%=mc.getSalesrepCode()%></td>
									   <td class="td_text_center" width="5%"><%=mc.getNisCreateUser()%></td>
									  <% if( !UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS})){%>
										  <td class="td_text_center" width="5%"><%=mc.getInvoiceNo()%></td>
										  <td class="td_text_center" width="5%"><%=mc.getInvoiceDate()%></td>
										  <td class="td_text_center" width="5%"><%=mc.getOraCustomerCode()%></td>
										  <td class="td_text_center" width="5%"><%=mc.getNisCreateUser()%></td>
									  <%}else{ %>
										  <td class="td_text_center" width="5%"><%=mc.getInvoiceDate()%></td>
									  <%} %>
									</tr>
							<%} %>
					  </table>
                    </c:if>
                    
					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
					<input type="hidden" name ="fromPage" id="fromPage" value="<%=Utils.isNull(request.getParameter("fromPage"))%>"/>
					<input type="hidden" name ="shortCustId" id="shortCustId" value="<%=Utils.isNull(request.getParameter("shortCustId"))%>"/>
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