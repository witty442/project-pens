<%@page import="com.isecinc.pens.web.nissin.NSConstant"%>
<%@page import="com.isecinc.pens.dao.NSDAO"%>
<%@page import="com.isecinc.pens.web.rt.RTConstant"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.bean.NSBean"%>
<%@page import="com.isecinc.pens.bean.PayBean"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="nsForm" class="com.isecinc.pens.web.nissin.NSForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
String role = user.getRole().getKey();

if(session.getAttribute("channelList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(NSDAO.searchChannelList(new PopupForm(),""));
	
	session.setAttribute("channelList",billTypeList);
}

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}

span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}
#scroll {
    width:1200px;
    background:#A3CBE0;
	border:1px solid #000;
	overflow:auto;
	white-space:nowrap;
	box-shadow:0 0 25px #000;
	}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('orderDateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('orderDateTo'));
}
function clearForm(path){
	var form = document.nsForm;
	form.action = path + "/jsp/nsAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.nsForm;
	
	form.action = path + "/jsp/nsAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}

function printReport(path){
	var form = document.nsForm;
	
	form.action = path + "/jsp/nsAction.do?do=printReport";
	form.submit();
	return true;
}

function exportToExcel(path){
	var form = document.nsForm;
	
	form.action = path + "/jsp/nsAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function newDoc(path){
	 var form = document.nsForm;
	var param ="";
	form.action = path + "/jsp/nsAction.do?do=prepare&mode=add"+param;
	form.submit();
	return true; 
}

function openEdit(path,orderId){
	 var form = document.nsForm;
	var param ="&orderId="+orderId;
	form.action = path + "/jsp/nsAction.do?do=prepare&mode=edit"+param;
	form.submit();
	return true; 
}

function openPensEdit(path,orderId){
	 var form = document.nsForm;
	var param ="&orderId="+orderId;
	form.action = path + "/jsp/nsAction.do?do=preparePens&mode=edit"+param;
	form.submit();
	return true; 
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
	        <%if("PENS".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="nsPens"/>
				</jsp:include>
			<%}else  {%>
		      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="ns"/>
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
						<html:form action="/jsp/nsAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td> ID &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                       <font color="red"></font>
                                        <html:text property="bean.orderId" styleClass="" styleId="orderId"></html:text>
                                    </td>
									<td nowrap>	ประเภท
										 <html:select property="bean.customerType" styleId="customerType" >
										    <html:option value=""></html:option>
											<html:option value="School">School</html:option>
										    <html:option value="Mini">Mini</html:option>
											<html:option value="Shop">Shop</html:option>
										   </html:select>
										   
										    สถานะ
										 <html:select property="bean.status" styleId="status" >
										    <html:option value=""></html:option>
											<html:option value="O">OPEN</html:option>
										    <html:option value="C">COMPLETE</html:option>
											<html:option value="P">PENDING</html:option>
										   </html:select>
										   
										       ภาค <font color="red"></font>
										  <html:select property="bean.channelId" styleId="channelId">
											<html:options collection="channelList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> จากวันที่บันทึก  
                                       <font color="red"></font>
                                        <html:text property="bean.orderDateFrom" styleClass="" styleId="orderDateFrom"></html:text>
                                    </td>
									<td>ถึงวันที่บันทึก
										  <font color="red"></font>
                                        <html:text property="bean.orderDateTo" styleClass="" styleId="orderDateTo"></html:text>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									    <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
										  <input type="button" value="   ExportToExcel    " class="newPosBtnLong"> 
										</a>
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<%if( Utils.isNull(request.getParameter("page")).equalsIgnoreCase("nissin") ){%>
											<a href="javascript:newDoc('${pageContext.request.contextPath}')">
											  <input type="button" value="    เพิ่มรายการใหม่      " class="newPosBtnLong"> 
											</a>
										<%} %>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${nsForm.resultsSearch != null}">
                  	<div id ="scroll" >
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearch">
						       <tr>
						            <th >Action</th>
						            <th >Status</th>
						            <th >ID</th>
									<th >วันที่บันทึก</th>
									<th >ประเภท</th>
									<th >รหัสร้านค้า</th>
									<th >ชื่อร้านค้า</th>
									<th >ภาค</th>
									<th >จังหวัด</th>
									<th >ที่อยู่ Line1</th>
									<th >ที่อยู่ Line2</th>
									<th >เบอร์โทรศัพท์</th>
									<th >Invoice No</th>
									<th >Invoice Date</th>
									<th >รหัส Sale</th>
									<th >Cup72 (หีบ)</th>
									<th >Cup72 (ถ้วย)</th>
									<th >ซอง (หีบ)</th>
									<th >ซอง (ซอง)</th>
									<th >Pooh72 (หีบ)</th>
									<th >Pooh72 (ถ้วย)</th>
									<th >Remark1</th>
									<th >Pending Reason</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<NSBean> resultList = nsForm.getResultsSearch();
							
							for(int n=0;n<resultList.size();n++){
								NSBean mc = (NSBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>">
									<td class="td_text_center" width="5%">
										<%if ( Utils.isNull(request.getParameter("page")).equalsIgnoreCase("pens") ){%>
										    <% if(mc.getStatus().equals(NSConstant.STATUS_OPEN)	){%>
											 <a href="javascript:openPensEdit('${pageContext.request.contextPath}','<%=mc.getOrderId()%>')">
											             แก้ไข
											 </a>
											 <%}else{ %>
											   <a href="javascript:openPensEdit('${pageContext.request.contextPath}','<%=mc.getOrderId()%>')">
											             ดู
											   </a>
									        <% } %>
									    
										<%}else{ 
										    if(mc.getStatus().equals(RTConstant.STATUS_OPEN)){
										     %>
											 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getOrderId()%>')">
											             แก้ไข
											 </a>
											 <%}else{ %>
											   <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getOrderId()%>')">
											             ดู
											   </a>
									    <%    } 
									      }
									     %>
										</td>
										<td class="td_text_center" width="3%" nowrap><%=mc.getStatusDesc()%></td>
										<td class="td_text_center" width="5%" nowrap><%=mc.getOrderId()%></td>
										<td class="td_text_center" width="5%" nowrap ><%=mc.getOrderDate()%></td>
										<td class="td_text_center" width="4%" nowrap><%=mc.getCustomerType()%></td>
										<td class="td_text" width="5%" ><%=mc.getCustomerCode()%></td>
									    <td class="td_text" width="5%" ><%=mc.getCustomerName() %></td>
									    <td class="td_text" width="5%" ><%=mc.getChannelName() %></td>
									    <td class="td_text" width="5%" ><%=mc.getProvinceName() %></td>
									    <td class="td_text" width="8%" ><%=mc.getAddressLine1()%></td>
									    <td class="td_text" width="8%" ><%=mc.getAddressLine2()%></td>
										<td class="td_text" width="5%" nowrap><%=mc.getPhone()%></td>
										<td class="td_text" width="5%" nowrap><%=mc.getInvoiceNo()%></td>
										<td class="td_text" width="5%" nowrap><%=mc.getInvoiceDate()%></td>
										<td class="td_text" width="5%" nowrap><%=mc.getSaleCode()%></td>
										<td class="td_text_center" width="5%"><%=mc.getCupQty()%></td>
										<td class="td_text_center" width="5%"><%=mc.getCupNQty()%></td>
										<td class="td_text_center" width="5%"><%=mc.getPacQty()%></td>
										<td class="td_text_center" width="5%"><%=mc.getPacNQty()%></td>
										<td class="td_text_center" width="5%"><%=mc.getPoohQty()%></td>
										<td class="td_text_center" width="5%"><%=mc.getPoohNQty()%></td>
										<td class="td_text" width="25%"><%=mc.getRemark()%></td> 
										<td class="td_text" width="5%"><%=mc.getPendingReason()%></td> 
										<!-- 65 -->
									</tr>
							<%} %>
							 
					</table>
					</div>
				</c:if>
				
		<!-- ************************Result ***************************************************-->	
					<!-- hidden field -->
					<input type="hidden" name="page" value="<%=request.getParameter("page") %>"/>
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