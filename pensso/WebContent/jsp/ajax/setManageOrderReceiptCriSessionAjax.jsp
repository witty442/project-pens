<%@page import="com.isecinc.pens.web.admin.ManageOrderReceiptForm"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%
String documentDateFrom = Utils.isNull(request.getParameter("documentDateFrom"));
String documentDateTo = Utils.isNull(request.getParameter("documentDateTo"));
String customerCode = Utils.isNull(request.getParameter("customerCode"));
String customerName = Utils.isNull(request.getParameter("customerName"));
    customerName = new String(customerName.getBytes("ISO8859_1"), "UTF-8");
    
/* System.out.println("setRegionSession :regionAll="+regionAll);
System.out.println("setProvinceSession :provinceAll="+provinceAll);
System.out.println("setAmphurSession :amphurAll="+amphurAll); */

ManageOrderReceiptForm cri = new ManageOrderReceiptForm();
cri.setDocumentDateFrom(documentDateFrom);
cri.setDocumentDateTo(documentDateTo);
cri.setCustomerCode(customerCode);
cri.setCustomerName(customerName);

session.setAttribute("ManageOrderReceiptForm_cri", cri);
%>