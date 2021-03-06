package com.pens.util;

/**
 * @Title:ReportUtil
 * @Description: utility of jasper report
 * @Author A-neak.t
 * @Version 1.0
 * @CreateDate: 10/11/2010
 * @CurrentVersion 1.0
 * 
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.naming.NamingException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.PdfFont;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import org.apache.log4j.Logger;

import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.User;
import com.lowagie.text.pdf.BaseFont;

@SuppressWarnings( { "static-access", "serial", "deprecation" })
public class ReportUtilServlet extends HttpServlet {

	Logger logger = Logger.getLogger("PENS");

	BeanParameter beanParameter = new BeanParameter();
	public String path = beanParameter.getReportPath();

	/**
	 * 
	 * @param fileName
	 * @Description for complie xxx.jrxml to xxx.jasper
	 */
	@SuppressWarnings("unused")
	private JasperReport compiledReport(String fileName) throws JRException {
		JasperReport jasperReport = null;
		try {
			jasperReport = JasperCompileManager.compileReport(fileName + ".jrxml");
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return jasperReport;
	}

	/**
	 * 
	 * @param reportRows
	 * @return JRDataSource
	 * @Description for prepare datasource for ireport the datasource are array of Object or Bean
	 * 
	 */
	public JRDataSource createDataSource(Object[] reportRows) {
		JRBeanArrayDataSource dataSource;
		dataSource = new JRBeanArrayDataSource(reportRows);

		return dataSource;
	}

	/**
	 * 
	 * @param reportRows
	 * @return JRDataSource
	 * @Description for prepare datasource for ireport the datasource are Collection etc ArrayList containing bean
	 *              object or hash-map data
	 */
	@SuppressWarnings("unchecked")
	public JRDataSource createDataSource(Collection reportRows) {
		JRBeanCollectionDataSource dataSource;
		dataSource = new JRBeanCollectionDataSource(reportRows);

		return dataSource;
	}

	@SuppressWarnings("unchecked")
	public void fillReport(ResultSet resultSet, String jasperFileName) {
		JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(resultSet);
		try {
			JasperFillManager.fillReportToFile(jasperFileName, new HashMap(), resultSetDataSource);

		} catch (JRException jre) {
			jre.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public JasperPrint fillReportJasperPrint(ResultSet resultSet, String jasperFileName, HashMap param)
			throws JRException {
		JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(resultSet);
		JasperPrint jasperPrint = null;
		jasperPrint = JasperFillManager.fillReport(jasperFileName + ".jasper", param, resultSetDataSource);

		return jasperPrint;
	}

	@SuppressWarnings( { "unchecked", "unused" })
	private void runReportListToRTF(HttpServletRequest request, HttpServletResponse response, Connection conn,
			String fileJasper, List lstData, HashMap parameterMap, String fileName) throws ServletException,
			IOException, JRException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		String userPrint = "";

		ResourceBundle bundle = null;
		bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));

		if (user != null) {
			userPrint = bundle.getString("PrinterName") + " " + user.getName() + "  " + user.getOrganization();
		}
		parameterMap.put("userPrint", userPrint);
		File rptFile = null;
		fileName = fileName + ".doc";

		try {
			ServletOutputStream servletOutputStream = response.getOutputStream();
			JRDataSource jrDataSource = createDataSource(lstData);
			rptFile = new File(fileJasper + ".jasper");

			JasperReport rtfReport = (JasperReport) JRLoader.loadObject(rptFile.getPath());
			JasperPrint rtfPrint = null;
			rtfPrint = JasperFillManager.fillReport(rtfReport, parameterMap, jrDataSource);

			JRRtfExporter exporter = new JRRtfExporter();
			ByteArrayOutputStream rtfOutput = new ByteArrayOutputStream();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, rtfPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, rtfOutput);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);
			exporter.exportReport();

			response.setContentType("application/msword");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

			byte[] bytes = null;
			bytes = rtfOutput.toByteArray();
			response.setContentLength(bytes.length);

			servletOutputStream.write(bytes, 0, bytes.length);
			servletOutputStream.flush();
			servletOutputStream.close();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param dataSource
	 * @throws IOException
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 * @throws NamingException
	 * @Description for run jasper report
	 * @Example fileJasper ="/reports/FirstReport.jasper" typeOfOutputReport = "application/pdf"
	 */

	@SuppressWarnings("unchecked")
	public void runReport(HttpServletRequest request, HttpServletResponse response, Connection conn, String fileJasper,
			String fileType, HashMap parameterMap, String fileName, List lstData,String fileNameExport) throws Exception {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		String userPrint = "";
		ResourceBundle bundle = null;
		bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		if (user != null) {
			userPrint = bundle.getString("PrinterName") + " " + user.getName() + "  " + user.getOrganization();
		}
		parameterMap.put("logo", BeanParameter.getLogo());
		parameterMap.put("userPrint", userPrint);

		try {

			if (fileType.equals(SystemElements.WORD)) {
				try {
					// generateRTFOutput(response, new HashMap(), jasperReport, jasperPrint, fileName);
				} catch (Exception ex) {
					System.out.print(ex.toString());
				}
			} else if (fileType.equals(SystemElements.EXCEL)) {
				try {
					runReportListToXLS(request, response, conn, fileJasper, lstData, parameterMap, fileName);
				} catch (Exception ex) {
					System.out.print(ex.toString());
				}
			} else if (fileType.equals(SystemElements.PDF)) {
				try {
					runReportListToPDF(request, response, conn, fileJasper, lstData, parameterMap, fileName,fileNameExport);
				} catch (Exception ex) {
					System.out.print(ex.toString());
				}
			} else if (fileType.equals(SystemElements.PRINTER)) {
				try {
					runReportListToPrinter(request, response, conn, fileJasper, lstData, parameterMap, fileName);
				} catch (Exception ex) {
					System.out.print(ex.toString());
				}
			} else if (fileType.equals(SystemElements.HTML)) {
				try {
					// generateHtmlOutput(jasperPrint, request, response);
					generateHTML(request, response, fileJasper);
				} catch (Exception e) {
					System.out.println(e.toString());
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings( { "unchecked", "unused" })
	private void generateRTFOutput(HttpServletResponse response, Map parameters, JasperReport jasperReport,
			JasperPrint jasperPrint, String fileName) throws JRException, NamingException, SQLException, IOException {

		byte[] bytes = null;
		fileName = fileName + ".doc";
		JRRtfExporter exporter = new JRRtfExporter();
		ByteArrayOutputStream rtfReport = new ByteArrayOutputStream();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, rtfReport);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);
		exporter.exportReport();

		bytes = rtfReport.toByteArray();

		response.setContentType("application/msword");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		response.setContentLength(bytes.length);
		ServletOutputStream servletOutputStream = response.getOutputStream();
		servletOutputStream.write(bytes, 0, bytes.length);
		servletOutputStream.flush();
		servletOutputStream.close();
	}

	@SuppressWarnings("unused")
	private void generateHtmlOutput(JasperPrint jasperPrint, HttpServletRequest req, HttpServletResponse response)
			throws IOException, JRException {
		JRHtmlExporter exporter = new JRHtmlExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, response.getWriter());
		exporter.exportReport();
	}

	@SuppressWarnings( { "unchecked", "unused" })
	private void generateHTML(HttpServletRequest request, HttpServletResponse resp, String fileJasper) {
		JRHtmlExporter exporter = new JRHtmlExporter();
		File rptFile = null;
		try {
			// Map imagesMap = new HashMap();
			// JasperReport jasperReport = JasperCompileManager.compileReport(fileJasper + ".jrxml");

			rptFile = new File(fileJasper + ".jasper");
			JasperReport rtfReport = (JasperReport) JRLoader.loadObject(rptFile.getPath());
			JasperPrint jasperPrint = JasperFillManager.fillReport(rtfReport, new HashMap(), new JREmptyDataSource());
			resp.setContentType("text/html");

			request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
			exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "TIS-620");
			exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
			exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);

			exporter.setParameter(JRHtmlExporterParameter.SIZE_UNIT, "px");
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, resp.getWriter());

			exporter.exportReport();

			request.setAttribute("htmlexporter", exporter);

		} catch (IOException e) {

			e.printStackTrace();
		} catch (JRException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@SuppressWarnings( { "unchecked" })
	private void runReportListToXLS(HttpServletRequest request, HttpServletResponse response, Connection conn,
			String fileJasper, List lstData, HashMap parameterMap, String fileName) throws ServletException,
			IOException, JRException {

		File rptFile = null;
		fileName = fileName + ".xls";

		try {
			JRDataSource jrDataSource = createDataSource(lstData);
			rptFile = new File(fileJasper + ".jasper");

			JasperReport rtfReport = (JasperReport) JRLoader.loadObject(rptFile.getPath());
			JasperPrint rtfPrint = null;
			rtfPrint = JasperFillManager.fillReport(rtfReport, parameterMap, jrDataSource);

			JRXlsExporter exporter = new JRXlsExporter();
			ByteArrayOutputStream rtfOutput = new ByteArrayOutputStream();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, rtfPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, rtfOutput);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);
			exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
			exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);

			exporter.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.FALSE);
			exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.FALSE);

			exporter.exportReport();

			byte[] bytes = null;
			bytes = rtfOutput.toByteArray();
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setContentType("application/ms-excel");
			response.setContentLength(bytes.length);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(bytes, 0, bytes.length);
			servletOutputStream.flush();
			servletOutputStream.close();
		} catch (JRException e) {
			throw e;
		}
	}

	/**
	 * sample fileName = "C1_report"; Run report to pdf file without list data.
	 */
	@SuppressWarnings("unchecked")
	public void runReportPDF(HttpServletRequest request, HttpServletResponse response, Connection conn,
			String fileJasper, HashMap parameterMap, String fileName) {

		String jrxmlFileName = fileJasper + ".jrxml";
		String jasperFileName = fileJasper + ".jasper";

		try {
			JasperCompileManager.compileReportToFile(jrxmlFileName, jasperFileName);
			JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(jasperFileName, parameterMap, conn);

			HashMap fontMap = new HashMap();
			FontKey key = new FontKey("Angsana New", false, false);
			PdfFont font = new PdfFont("ANGSAU.TTF", BaseFont.IDENTITY_H, true);
			fontMap.put(key, font);

			FontKey key2 = new FontKey("Angsana New", true, false);
			PdfFont font2 = new PdfFont("ANGSAUB.TTF", BaseFont.IDENTITY_H, false);
			fontMap.put(key2, font2);

			ByteArrayOutputStream rtfOutput = new ByteArrayOutputStream();
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jprint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, rtfOutput);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);
			//exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);
			exporter.exportReport();

			byte[] bytes = null;
			bytes = rtfOutput.toByteArray();
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setContentType("application/pdf");
			response.setContentLength(bytes.length);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(bytes, 0, bytes.length);
			servletOutputStream.flush();
			servletOutputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings( { "unchecked" })
	private void runReportListToPDF(HttpServletRequest request, HttpServletResponse response, Connection conn,
			String fileJasper, List lstData, HashMap parameterMap, String fileName,String fileNameExport) throws ServletException,
			IOException, JRException {

		File rptFile = null;
		fileName = fileName + ".pdf";
		try {
			//Wit Edit
		    logger.info("Run report to PDF");
            
			rptFile = new File(fileJasper + ".jasper");
			JRDataSource jrDataSource = createDataSource(lstData);

			JasperReport rtfReport = (JasperReport) JRLoader.loadObject(rptFile.getPath());
			JasperPrint rtfPrint = JasperFillManager.fillReport(rtfReport, parameterMap, jrDataSource);

			// Set font for pdf.
			HashMap fontMap = new HashMap();
			FontKey key = new FontKey("Angsana New", false, false);
			PdfFont font = new PdfFont("fonts/ANGSAU.TTF", BaseFont.IDENTITY_H, true);
			fontMap.put(key, font);

			FontKey key2 = new FontKey("Angsana New", true, false);
			PdfFont font2 = new PdfFont("fonts/ANGSAUB.TTF", BaseFont.IDENTITY_H, false);
			fontMap.put(key2, font2);
			
			ByteArrayOutputStream rtfOutput = new ByteArrayOutputStream();
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, rtfPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, rtfOutput);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);
			exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);
			exporter.exportReport();

			byte[] bytes = null;
			bytes = rtfOutput.toByteArray();
			response.setHeader("Content-Disposition", "attachment;filename=" + fileNameExport);
			response.setContentType("application/pdf");
			response.setContentLength(bytes.length);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(bytes, 0, bytes.length);
			servletOutputStream.flush();
			servletOutputStream.close();

		} catch (JRException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private void runReportListToPrinter(HttpServletRequest request, HttpServletResponse response, Connection conn,
			String fileJasper, List lstData, HashMap parameterMap, String fileName) throws ServletException,
			IOException, JRException {
		File rptFile = null;
		User user = (User) request.getSession().getAttribute("user");
		logger.debug("Print to PRINTER " + fileJasper);
		try {
			rptFile = new File(fileJasper + ".jasper");
			JRDataSource jrDataSource = createDataSource(lstData);

			logger.debug("Data Source " + jrDataSource);

			JasperReport rtfReport = (JasperReport) JRLoader.loadObject(rptFile.getPath());
			JasperPrint rtfPrint = JasperFillManager.fillReport(rtfReport, parameterMap, jrDataSource);

			logger.debug("RTF Print " + rtfPrint);

			PrintService pService = PrintServiceLookup.lookupDefaultPrintService();
			PrintServiceAttributeSet printServiceDefaultAttributeSet = pService.getAttributes();//default printer
			
			logger.debug("default printerName:"+pService.getName());
			
			/** Set property printer and Report **/
			HashMap fontMap = new HashMap();
			
			if("pay_in_report".equalsIgnoreCase(fileName) || "pay_in_white_report".equalsIgnoreCase(fileName)){
		
				String printerPayInName = "";//user.getPrinterName();//"HP LaserJet Professional P1606dn";
				
				PrintServiceAttributeSet printServiceAttributeSetManual = new HashPrintServiceAttributeSet();
				printServiceAttributeSetManual.add(new PrinterName(printerPayInName, Locale.getDefault()));
		        
		        //check Printer PENS_A5 is Exist
		        logger.debug("Case pay_in_report :new printerName:"+printerPayInName);
		        
		        logger.debug("Step 1 Check Printer "+printerPayInName+" is Exist");
		        try{
		        	logger.debug("pay_in_report Step 1 Fix printer "+printerPayInName+"");

					JRExporter exporter = new JRPrintServiceExporter();
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, rtfPrint);
					//exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);
					
		        	exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSetManual);
					exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
					exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
		
					logger.debug("pay_in_report Step 1 Fix printer "+printerPayInName+" Start exported...");
					exporter.exportReport();
					logger.debug("pay_inreport Step 1 Fix printer "+printerPayInName+" End exported");
					
		        }catch(Exception e){
		        	
                    logger.debug("print pay_in_report Error not found printer name:"+printerPayInName);
		        	//logger.debug("tax_invoice_report Step 2 User Printer defalut ");
/*
					JRExporter exporter = new JRPrintServiceExporter();
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, rtfPrint);
					//exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);
					
					exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceDefaultAttributeSet);
					exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
					exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);*/
		
					//logger.debug("pay_in_report Step 2 User Printer defalut  Start exported...");
					//exporter.exportReport();
					//logger.debug("pay_in_report Step 2 User Printer defalut  End exported");
		        }
		     /** Normal **/
			}else{

				JRExporter exporter = new JRPrintServiceExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, rtfPrint);
				//exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);

				exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceDefaultAttributeSet);
				exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
				exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
	
				logger.debug("Start Printer Normal exported...");
		
				exporter.exportReport();
	
				logger.debug("End printer Normal exported");
			}
			
		} catch (JRException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
	
	
}
