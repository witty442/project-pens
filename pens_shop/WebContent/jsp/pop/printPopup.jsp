<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.web.sales.OrderUtils"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
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
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">

<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<script type="text/javascript">
<%
String msg = "";
String width = Utils.isNull(request.getParameter("width"));
String height = Utils.isNull(request.getParameter("height"));
if(Utils.isNull(width).equals("") || Utils.isNull(width).equals("0")){
	width ="600";
}
if(Utils.isNull(height).equals("") || Utils.isNull(height).equals("0")){
	height ="700";
}

System.out.println("reportName:"+request.getParameter("report_name"));
System.out.println("reportNameAttr:"+Utils.isNull(request.getAttribute("report_name")));
User user = ((User)session.getAttribute("user"));
%>
function loadMe(_path){
	//MZ320
   <%
	if("list_order_product".equals(request.getParameter("report_name"))){
		String customerId = request.getParameter("customerId");
		%>
	
		document.orderForm.action = _path + "/jsp/saleOrderAction.do?do=printListOrderProductReport&customerId=<%=customerId%>";
		document.orderForm.submit();
		
	//MZ320
  <%}else if("tax_invoice_summary".equals(request.getParameter("report_name"))){ 
	   String orderId = request.getParameter("orderId");
	   String reportType = request.getParameter("reportType");
	   String orderNo = Utils.isNull(request.getParameter("orderNo"));
	   
   %>
	    //Other Report No check
	    document.orderForm.action = _path + "/jsp/saleOrderAction.do?do=printReportSummary&orderId=<%=orderId%>&fileType=PRINTER&reportType=<%=reportType%>";
        document.orderForm.submit();
	
//report LQ300 Receipt_TaxInvoice first report
  <%}else if("printReport".equals(request.getParameter("report_name"))){ 
	   String orderId = request.getParameter("orderId");
	   String i = request.getParameter("i");
	   String visitDate = request.getParameter("visitDate");
	   String fileType = request.getParameter("fileType");
  %>
	   var param  = "do=printReport";
	   param += "&orderId=<%=orderId%>";
	   param += "&i=<%=i%>";
	   param += "&visitDate=<%=visitDate%>&fileType=<%=fileType%>";
	   
	   <%if(fileType.equalsIgnoreCase("PDF")){%>
	     setTimeout(function(){printReport2();},3000);
	   <%}%>
       document.orderForm.action = _path + "/jsp/saleOrderAction.do?"+param;
	   document.orderForm.submit();
	   
	    function printReport2(){
		   var param  = "do=printReport2";
		   param += "&orderId=<%=orderId%>";
		   param += "&i=1";
		   param += "&visitDate=<%=visitDate%>&fileType=<%=fileType%>";
		   //alert("printReport2");
		   
	       document.orderForm.action = _path + "/jsp/saleOrderAction.do?"+param;
		   document.orderForm.submit(); 
	   } 
<% 
  }else{ 
       if(Utils.isNull(msg).equals("")){
%>
         setTimeout(function(){window.close();},20000);
<%  
       } 
     }
 %>
}

function gotoPagePDReceipt(path){
	PopupCenter(path + "/jsp/pd/shortcutPDReceiptPopup.jsp","PD Receipt",<%=width%>,<%=height%>);
	window.close();
}
</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<!-- BODY -->
<html:form action="/jsp/saleOrderAction">
<input type="hidden" name="load" value="">
<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value="Transaction"/>
	<jsp:param name="function" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="center">
			<!-- Display Error msg -->
			<font color="red" size="2">
			   <%out.println(Utils.isNull(request.getAttribute("ERROR_MSG"))); %>
			</font>
			<font size="2">
			<%if( Utils.isNull(request.getParameter("report_name")).equalsIgnoreCase("list_order_product")){ %>
			  กำลังพิมพ์  ใบหยิบของ
			<%}else if( Utils.isNull(request.getParameter("report_name")).equalsIgnoreCase("tax_invoice_summary")){ %>
				<%if( !Utils.isNull(msg).equals("")) {%>
				   <span><font size="3" color="red"><%=msg %></font></span><br/><br/>
				   <input type="button" style="width:360px" value="ไปยัง หน้าบันทึกการเก็บเงินขายเชื่อให้ PD" class="newPosBtnLong" 
				   onclick="gotoPagePDReceipt('${pageContext.request.contextPath}')">
				<%}else{ %>
				        กำลังพิมพ์ ใบเสร็จ (เครื่องพิมพ์เล็ก)
				 <%} %>
			<%}else if( Utils.isNull(request.getParameter("report_name")).equalsIgnoreCase("printReport")){ %>
			         กำลังพิมพ์ ใบเสร็จ (เครื่องพิมพ์ใหญ่)
			<%} %>
			.......กรุณา รอสักครู่
			</font>
			<br/><br/>
			<a href="#" onclick="window.close();">
			  <input type="button" value="ปิดหน้าจอ" class="newPosBtnLong">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</html:form>
<!-- BODY -->
</body>
</html>