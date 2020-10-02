<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.model.MDistrict"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@page import="com.isecinc.pens.bean.InvoiceReportBean"%>
<%@page import="com.isecinc.pens.web.buds.BudsAllForm"%>
<%@page import="com.isecinc.core.model.I_PO"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="budsAllForm" class="com.isecinc.pens.web.buds.BudsAllForm" scope="session" />
<%
InvoiceReportBean bean = ((BudsAllForm)session.getAttribute("budsAllForm")).getBean().getInvoiceReportBean();

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
	String screenWidth = ""+(Utils.convertToInt(Utils.isNull(session.getAttribute("screenWidth")))-50);
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight")); 
	String hideAll = "true"; 
	String pageName = !Utils.isNull(request.getParameter("pageName")).equals("")?Utils.isNull(request.getParameter("pageName")):budsAllForm.getPageName();
	String subPageName = !Utils.isNull(request.getParameter("subPageName")).equals("")?Utils.isNull(request.getParameter("subPageName")):budsAllForm.getSubPageName();

%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- For fix Head and Column Table -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('transactionDateFrom'));
	new Epoch('epoch_popup', 'th', document.getElementById('transactionDateTo'));
	
	loadProvince();
	
	<%if( !"".equals(Utils.isNull(bean.getSearchProvince()))){ %>
       setTimeout(function(){document.getElementsByName('bean.invoiceReportBean.searchProvince')[0].value = <%=bean.getSearchProvince()%>  }, 1500);
    <% } %>
    setTimeout(function(){loadDistrict(); }, 1700);
   
	<%if( !"".equals(Utils.isNull(bean.getDistrict()))){ %>
	   setTimeout(function(){document.getElementsByName('bean.invoiceReportBean.district')[0].value = <%=bean.getDistrict()%>  }, 2100);
	<% } %>
}

function loadProvince(){
	var cboProvince = document.getElementById('searchProvinceTemp');
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/ProvinceTerritory.jsp",
			data : "refId=" + document.getElementsByName('bean.invoiceReportBean.territory')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboProvince.innerHTML=returnString;
			}
		}).responseText;
	});
}

function loadDistrict(){
	var cboDistrict = document.getElementsByName('bean.invoiceReportBean.district')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/DistrictAjax.jsp",
			data : "refId=" + document.getElementsByName('bean.invoiceReportBean.searchProvince')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}
function search(){
	var form = document.budsAllForm;
	var path =document.getElementById("path").value 
	form.action = path + "/jsp/budsAllAction.do?do=printReport&reportName=InvoiceReport&action=newsearch";
	form.submit();
	return true;
}

function exportExcel(){
	var form = document.budsAllForm;
	var path =document.getElementById("path").value 
	var param = "&reportName=InvoiceReport";
	form.action = path + "/jsp/budsAllAction.do?do=export"+param;
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.payForm;
	var path = document.getElementById("path").value ;
	form.action = path + "/jsp/payAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}
function clearForm(path){
	var form = document.budsAllForm;
	form.action = path + "/jsp/budsAllAction.do?do=prepareSearchHead&action=new";
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
				<jsp:param name="function" value="<%=subPageName%>"/>
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
						<html:form action="/jsp/budsAllAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
				    	<!-- ***** Criteria ******* -->
				    	<table align="center" border="0" cellpadding="3" cellspacing="0" >
					         <tr>
							<td width="35%" align="right"><bean:message key="Customer.Territory" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="2">
								<html:select property="bean.invoiceReportBean.territory" onchange="loadProvince();">
									<html:option value=""></html:option>
									<html:options collection="territorys" property="key" labelProperty="name"/>
								</html:select>
							</td>
							
						</tr>
						<tr>
						    <td align="right"><bean:message key="Address.Province" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="3">
								<html:select property="bean.invoiceReportBean.searchProvince" styleId="searchProvinceTemp"
								onchange="loadDistrict();">
								</html:select>
							&nbsp;&nbsp;
							     เขต/อำเภอ
							     <html:select property="bean.invoiceReportBean.district" styleId="district">
								</html:select>
							</td>
						</tr>
							
							<tr>
							  <td align="right"><bean:message key="Customer.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
							  <td align="left" colspan="3">
							  <html:text property="bean.invoiceReportBean.customerCode"  styleClass="\" autoComplete=\"off" />
							  &nbsp;&nbsp;
							   <bean:message key="Customer.Name" bundle="sysele"/>
							   <html:text property="bean.invoiceReportBean.customerName" size="30" styleClass="\" autoComplete=\"off" />
						      
							  </td>
						    </tr>
						
							<tr>
								<td align="right" valign="top">ใบแจ้งหนี้ ตั้งแต่วันที่&nbsp;&nbsp;
								</td>
								<td align="left" colspan="3" valign="top">
								    <html:text property="bean.invoiceReportBean.transactionDateFrom"
									maxlength="10" size="15" readonly="true" styleId="transactionDateFrom" />
									&nbsp;&nbsp;&nbsp;&nbsp;
								    ถึงวันที่
								    <html:text property="bean.invoiceReportBean.transactionDateTo"
									maxlength="10" size="15" readonly="true" styleId="transactionDateTo" />
								
								<html:checkbox property="bean.invoiceReportBean.dispHaveRemain">&nbsp;แสดงเฉพาะรายการที่มียอดคงค้าง</html:checkbox>
								</td>
							</tr>
							
						</table>
					<br/><br/>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="100%">
								<a href="javascript:search()">
								  <input type="button" value="  ค้นหา  " class="newPosBtnLong">
								</a>
								&nbsp;
								<a href="javascript:exportExcel()">
								  <input type="button" value="  Export " class="newPosBtnLong">
								</a>
								&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value=" Clear  " class="newPosBtnLong">
								</a>&nbsp;
								
							</td>
							 <td align="right" width="40%" nowrap></td>
						</tr>
					</table>
			  </div>
			 <br/><br/>
			     <!-- ****** RESULT *************************************************** -->
			  <%
			       if(request.getAttribute("budsAllForm_RESULTS") != null ){
			    	%>
			    	 <div style="height:400px;width:<%=screenWidth%>px;">
			    	<%
			    	    out.println(((StringBuffer)request.getAttribute("budsAllForm_RESULTS")).toString());
			    	%>
			    	 </div>
			    	 
			    	 <script>
						//load jquery
						 $(function() { 
							//Load fix column and Head
							$('#tblProduct').stickyTable({overflowy: true});
						}); 
					 </script>
					 <%
			    	    //out.println(((StringBuffer)request.getAttribute("budsAllForm_total_RESULTS")).toString());
			    	 %>
			    <%} %>
			     <!-- ***************************************************************** -->
					<!-- hidden field -->
					<input type="hidden" name="pageName" id="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="subPageName" id="subPageName" value="<%=request.getParameter("subPageName") %>"/>
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
