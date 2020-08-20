
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="net.sf.jasperreports.view.JRViewer"%>
<%@page import="java.awt.BorderLayout"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="javax.swing.JFrame"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="net.sf.jasperreports.engine.JasperExportManager"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter"%>
<%@page import="net.sf.jasperreports.engine.JRExporterParameter"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporter"%>
<%@page import="net.sf.jasperreports.engine.JRExporter"%>
<%@page import="com.lowagie.text.pdf.BaseFont"%>
<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
<%@page import="net.sf.jasperreports.engine.JasperFillManager"%>
<%@page import="net.sf.jasperreports.engine.JasperPrint"%>
<%@page import="net.sf.jasperreports.engine.util.JRLoader"%>
<%@page import="net.sf.jasperreports.engine.JasperReport"%>
<%@page import="net.sf.jasperreports.engine.JRDataSource"%>
<%@page import="java.io.File"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.isecinc.pens.SystemElements"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.ConvertNullUtil"%><%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<style type="text/css">
@media print{
body{ background-color:#FFFFFF; background-image:none; color:#000000 }
#ad{ display:none;}
#leftbar{ display:none;}
#contentarea{ width:100%;}
}
</style>
<link rel="stylesheet" type="text/css" media="print" href="print.css?v=<%=SIdUtils.getInstance().getIdSession()%>">
<body leftmargin="0" topmargin="0" bottommargin="0" rightmargin="0" onload="print();window.close();">
<%
String reqDate = ConvertNullUtil.convertToString(request.getParameter("reqDate"));

ReportUtilServlet reportServlet = new ReportUtilServlet();
HashMap parameterMap = new HashMap();
List lstData = null;
String fileJasper = BeanParameter.getReportPath() + "printSticker";
File rptFile = null;

Connection conn = null;
try{
	conn = new DBCPConnectionProvider().getConnection(conn);
	lstData = new ArrayList();
	lstData.add("S");
	reportServlet.runReport(request, response, conn, fileJasper, SystemElements.HTML, parameterMap, "invoice_payment_report", lstData);
	
	System.out.println(request.getAttribute("htmlexporter"));
	//reportServlet.runReport(request, response, conn, fileJasper, SystemElements.HTML, parameterMap, "invoice_payment_report", lstData);
	
	/*rptFile = new File(fileJasper + ".jasper");
	JRDataSource jrDataSource = new ReportUtilServlet().createDataSource(lstData);

	JasperReport rtfReport = (JasperReport) JRLoader.loadObject(rptFile.getPath());
	JasperPrint rtfPrint = JasperFillManager.fillReport(rtfReport, parameterMap, jrDataSource);
	*/
	
	/*
	File f = new File("out1.xml");
	f.createNewFile();
	System.out.println(f.getAbsolutePath());
	
	OutputStream ost=new FileOutputStream(f);
	JasperExportManager.exportReportToXmlStream(rtfPrint,ost);
	System.out.println(ost);
	ost.close();
	InputStream inputStream = new FileInputStream(f);
	*/
	
	/*JFrame frame = new JFrame("xxx");
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	JRViewer viewer = new JRViewer(rtfPrint);
	frame.add(BorderLayout.CENTER, viewer);
	frame.setSize(1024, 748);
	frame.setVisible(true);*/
	
	
	/*
	PrintService pService = PrintServiceLookup.lookupDefaultPrintService();

	// Set font.
	HashMap fontMap = new HashMap();
	FontKey key = new FontKey("Angsana New", false, false);
	PdfFont font = new PdfFont("ANGSAU.TTF", BaseFont.IDENTITY_H, true);
	fontMap.put(key, font);

	FontKey key2 = new FontKey("Angsana New", true, false);
	PdfFont font2 = new PdfFont("ANGSAUB.TTF", BaseFont.IDENTITY_H, false);
	fontMap.put(key2, font2);
	
	JRExporter exporter = new JRPrintServiceExporter();
	exporter.setParameter(JRExporterParameter.JASPER_PRINT, rtfPrint);
	exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);
	exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, pService.getAttributes());
	exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
	exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

	exporter.exportReport();
	
	
*/
}catch(Exception e){
	e.printStackTrace();
}finally{
	conn.close();
}
%>
</body>
