<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">

<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<script type="text/javascript">
<%
 String printShipment_TaxInvoiceSuccess = Utils.isNull(request.getAttribute("printShipment_TaxInvoiceSuccess"));
 
 String orderId = Utils.isNull(request.getAttribute("orderId"));
 String i = Utils.isNull(request.getAttribute("i"));
 String visitDate = Utils.isNull(request.getAttribute("visitDate"));
 String fileType = Utils.isNull(request.getAttribute("fileType"));
 
 System.out.println("printShipment_TaxInvoiceSuccess:"+printShipment_TaxInvoiceSuccess);
 System.out.println("orderId:"+orderId);
 System.out.println("fileType:"+fileType);
%>
function loadMe(_path){
   var param  = "do=printReport2";
   param += "&orderId=<%=orderId%>";
   param += "&i=1";
   param += "&visitDate=<%=visitDate%>&fileType=<%=fileType%>";
   //alert("printReport2");
   <%if(Utils.isNull(printShipment_TaxInvoiceSuccess).equals("") && fileType.equalsIgnoreCase("PRINTER")){ %>
      document.orderForm.action = _path + "/jsp/saleOrderAction.do?"+param;
      document.orderForm.submit(); 
   <%}%>
   setTimeout(function(){window.close();},20000);
}

</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<!-- BODY -->
<html:form action="/jsp/saleOrderAction">
<input type="hidden" name="load" value="">
<!-- INCLUDE -->
<jsp:include page="../../program.jsp">
	<jsp:param name="system" value="Transaction"/>
	<jsp:param name="function" value=""/>
</jsp:include>
<jsp:include page="../../error.jsp"/>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="right">
			กำลังพิมพ์ 
			<%if( Utils.isNull(request.getParameter("report_name")).equalsIgnoreCase("list_order_product")){ %>
			   ใบหยิบของ
			<%}else if( Utils.isNull(request.getParameter("report_name")).equalsIgnoreCase("tax_invoice_summary")){ %>
			 ใบเสร็จ (เครื่องพิมพ์เล็ก)
			<%}else if( Utils.isNull(request.getParameter("report_name")).equalsIgnoreCase("printReport")){ %>
			 ใบเสร็จ (เครื่องพิมพ์ใหญ่)
			<%} %>
			<a href="#" onclick="window.close();">
			<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</html:form>
<!-- BODY -->
</body>
</html>