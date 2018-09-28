<%@page import="com.isecinc.pens.bean.SaveInvoiceBean"%>
<%@page import="com.isecinc.pens.bean.PayBean"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.*"%>
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
<jsp:useBean id="saveInvoiceForm" class="com.isecinc.pens.web.interfaces.SaveInvoiceForm" scope="session" />
<%
User user = (User) request.getSession().getAttribute("user");
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

.day {
  width: 14%;
}
.special {
  background-color: #F78181;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('BILL_DATE'));
}
function clearForm(path){
	var form = document.saveInvoiceForm;
	form.action = path + "/jsp/saveInvoiceAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.saveInvoiceForm;
	if( $('#BILL_DATE').val()==""){
		alert("กรุณาระบุ BILL DATE");
		return false;
	}
	/* if( $('#productName').val()==""){
		alert("กรุณาระบุ Product");
		return false;
	} */
	form.action = path + "/jsp/saveInvoiceAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}

function save(path){
	var form = document.saveInvoiceForm;
	if( $('#BILL_DATE').val()==""){
		alert("กรุณาระบุ BILL DATE");
		return false;
	}
	form.action = path + "/jsp/saveInvoiceAction.do?do=save";
	form.submit();
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
				<jsp:param name="function" value="SaveInvoice"/>
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
						<html:form action="/jsp/saveInvoiceAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td> Bill Date<font color="red">*</font></td>
									<td>		
										 <html:text property="bean.BILL_DATE" styleClass="" styleId="BILL_DATE"></html:text>
										
									</td>
									<td> Product <font color="red"></font>
									  <html:select property="bean.productName" styleId="productName">
											<html:options collection="productNameList" property="key" labelProperty="name"/>
									    </html:select>
									</td>
								</tr>
								
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${saveInvoiceForm.resultsSearch != null}">
						<table id="tblProduct" align="center" border="0" cellpadding="1" cellspacing="1" class="tableSearch">
						       <tr>
						        <th>ORACLE INVOICE_NO</th>
						        <th>รหัสร้านค้า</th>
						        <th>ชื่อร้านค้า</th>
						        <th>ACTIVITY CODE</th>
								<th>BILL </th>
								<th>CUST ID</th>
								<th>SHIP NO </th>
								<th class="special" nowrap>TOTAL QTY</th>
								<!-- <th class="special">COST</th> -->
								<th class="special" nowrap>NET BVAT</th>
								<th>BUS CODE</th>
								<th>DEPT CODE</th>
								<th>PRODUCT CODE</th>
								<th>SPD CODE</th>
								<th>PRODUCT TNAME</th>
								<th>BIL _DATE</th>
								<th>SHIP TO_ADDRESS</th>
								<th>SHIP NAME</th>
								<th>TSC ID</th>
								<th>TSC NAME</th>
								<th>SITE ID</th>
								<th>NET AMOUNT</th>
								<th>TDH GEN_NO</th>
								<th>PRODUCT BARCODE</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<SaveInvoiceBean> resultList = saveInvoiceForm.getResultsSearch();
							
							for(int n=0;n<resultList.size();n++){
								SaveInvoiceBean mc = (SaveInvoiceBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>">
									<td class="td_text_center" width="5%">
									    <input type ="text" name="oracle_invoice_no" value="<%=mc.getORACLE_INVOICE_NO()%>"/>
									      <input type ="hidden" name="bill_10" value="<%=mc.getBILL_10() %>"/>
									      <input type ="hidden" name="bill_date" value="<%=mc.getBILL_DATE() %>"/>
									      <input type ="hidden" name="busCode" value="<%=mc.getBUS_CODE() %>"/>
									      <input type ="hidden" name="deptCode" value="<%=mc.getDEPT_CODE() %>"/>
									      <input type ="hidden" name="productCode" value="<%=mc.getPRODUCT_CODE() %>"/>
									</td>
									    <td class="td_text" width="7%" nowrap><%=mc.getCustCode() %></td>
									    <td class="td_text" width="7%"><%=mc.getCustDesc() %></td>
										<td class="td_text" width="5%"><%=mc.getACTIVITY_CODE() %></td>
										<td class="td_text" width="5%"><%=mc.getBILL_10()%></td>
										<td class="td_text" width="5%"><%=mc.getCUST_ID()%></td>
									    <td class="td_text" width="5%"><%=mc.getSHIP_NO()%></td>
									    
									    <td class="special" width="5%" ><%=mc.getTotalQty()%></td>
									    <%-- <td class="special" width="5%"><%=mc.getCost()%></td> --%>
									    <td class="special" width="5%"><%=mc.getNetBVat()%></td>
									    
									    <td class="td_text" width="2%"><%=mc.getBUS_CODE()%></td>
										<td class="td_text" width="2%"><%=mc.getDEPT_CODE()%></td>
										<td class="td_text" width="2%"><%=mc.getPRODUCT_CODE()%></td>
										<td class="td_text" width="2%"><%=mc.getSPD_CODE()%></td>
										<td class="td_text" width="5%"><%=mc.getPRODUCT_TNAME()%></td>
									    <td class="td_text" width="5%"><%=mc.getBILL_DATE()%></td>
									    <td class="td_text" width="5%"><%=mc.getSHIP_TO_ADDRESS()%></td>
									    <td class="td_text" width="5%"><%=mc.getSHIP_NAME()%></td>
									    <td class="td_text" width="5%"><%=mc.getTSC_ID()%></td>
									    <td class="td_text" width="5%"><%=mc.getTSC_NAME()%></td>
										<td class="td_text" width="5%"><%=mc.getSITE_ID()%></td>
										<td class="td_text" width="5%"><%=mc.getNET_AMOUNT()%></td>
										<td class="td_text" width="5%"><%=mc.getTDH_GEN_NO()%></td>
										<td class="td_text" width="5%"><%=mc.getPRODUCT_BARCODE()%></td>
										
									</tr>
							<%} %>
							 <tr>
						        <td></td>
						        <td></td>
						        <td></td>
						        <td></td>
								<td></td>
								<td></td>
								<td></td>
								<td class="special">${saveInvoiceForm.bean.grandTotalQty}</td>
								<td class="special">${saveInvoiceForm.bean.grandNetBVat}</td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							   </tr>
					</table>
					
					 <table  border="0" cellpadding="3" cellspacing="0" >
						<tr>
							<td align="left">
							   
								<a href="javascript:save('${pageContext.request.contextPath}')">
								  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
								</a>
								
								<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
								  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
								</a>						
							</td>
						</tr>
					</table>
				</c:if>
				
				
		<!-- ************************Result ***************************************************-->	
					<!-- hidden field -->
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