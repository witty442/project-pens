package com.isecinc.pens.web.report.performance_subreport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.PdfFont;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.report.I_ReportAction;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.District;
import com.isecinc.pens.bean.Trip;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MContact;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MDistrict;
import com.isecinc.pens.model.MTrip;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.report.performance.PerformanceReport;
import com.isecinc.pens.report.performance.PerformanceReportProcess;
import com.isecinc.pens.web.customer.CustomerCriteria;
import com.isecinc.pens.web.customer.CustomerForm;
import com.isecinc.pens.web.report.creditpaid.CreditNoPaidReportForm;
import com.isecinc.pens.web.report.creditpaid.CreditPaidReport;
import com.isecinc.pens.web.report.performance.PerformanceReportCriteria;
import com.isecinc.pens.web.report.performance.PerformanceReportForm;
import com.isecinc.pens.web.report.transfer.BankTransferReport;
import com.isecinc.pens.web.report.transfer.BankTransferReportProcess;
import com.lowagie.text.pdf.BaseFont;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.EnvProperties;

/**
 * Performance Report Action
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportAction.java,v 1.0 11/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class PerformanceReportActionSubReport extends I_Action  {
	
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug(" Prepare Form");
		PerformanceReportFormSubReport reportForm = (PerformanceReportFormSubReport) form;
		User user = (User) request.getSession(true).getAttribute("user");
		try {
			if(Utils.isNull(request.getParameter("action")).equals("new")){
				  PerformanceReport bean = new PerformanceReport();
				  bean.setOrderDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  reportForm.setFileType("PDF");
				  reportForm.setBean(bean);
			}
			//saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "report";
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Prepare Form without ID");
		PerformanceReportFormSubReport reportForm = (PerformanceReportFormSubReport) form;
		try {
			if(Utils.isNull(request.getParameter("action")).equals("new")){
				  PerformanceReport bean = new PerformanceReport();
				  bean.setOrderDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  reportForm.setBean(bean);
			}
			// Save Token
			//saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}
		return "report";
	}
	public ActionForward searchReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug("search Report");
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		PerformanceReportProcess process = new PerformanceReportProcess();
		BankTransferReportProcess subProcess = new BankTransferReportProcess();
		PerformanceReportFormSubReport reportForm = (PerformanceReportFormSubReport) form;
		HashMap parameterMap = new HashMap();
		try{
			//init connection
			conn= DBConnection.getInstance().getConnection();
			
		   // get sum all.
			PerformanceReport p = process.getSumAll(reportForm.getBean(), user, conn);
			if (p != null) {
				parameterMap.put("order_date", DateToolsUtil.dateNumToWord(reportForm.getBean().getOrderDate()));
				parameterMap.put("total_discount", p.getAllDiscount());
				parameterMap.put("total_amount", p.getAllCashAmount());
				parameterMap.put("total_ap_amount", p.getAllAirpayAmount());
				parameterMap.put("total_receipt", p.getAllReceiptAmount());
				parameterMap.put("total_vat", p.getAllVatAmount());
				parameterMap.put("total_net", p.getAllNetAmount());
				parameterMap.put("total_net_nodis_nonvat", p.getAllNetAmountNoDisNonVat());
				parameterMap.put("target_amount", p.getAllTargetAmount());
				parameterMap.put("total_vat_cash", p.getAllVatCashAmount());
				parameterMap.put("total_vat_receipt", p.getAllVatReceiptAmount());
				parameterMap.put("total_cancel_amount", p.getTotalCancelAmountToday());
				
			}
			parameterMap.put("total_visit", process.getCountVisit(reportForm.getBean(), user, conn));
			parameterMap.put("total_customer", process.getCountCustomer(reportForm.getBean(), user, conn));
			
			int countRecord[] = process.getCountOrderItem(reportForm.getBean(), user, conn);
			parameterMap.put("total_record_item",countRecord[0] );
			parameterMap.put("total_record_cancel",countRecord[1] );
			
			//get main dataList
			List<PerformanceReport> mainDataList= process.doReport(reportForm.getBean(), user, conn);
			
			//validate data
			if(mainDataList ==null ||(mainDataList !=null &&  mainDataList.size()==0) ){
				request.setAttribute("Message", "ไม่พบข้อมูล");
				return mapping.findForward("report"); 
			}
			
			//get Sub1 dataList 
			BankTransferReport subCriBean = new BankTransferReport();
			subCriBean.setCreateDate(reportForm.getBean().getOrderDate());
			BankTransferReport subDataBean = subProcess.getData(subCriBean, user, conn);
			List<BankTransferReport> subDataList = subDataBean.getLstData();
			if(subDataList==null || (subDataList != null && subDataList.size()==0)){
				subDataList = new ArrayList<BankTransferReport>();
				subDataList.add(new BankTransferReport());
			}
			
			File rptMainReportFile = null;
			File rptFileSubReport = null;
			String fileName = "performance.pdf";
		
			logger.debug("pathReport:"+BeanParameter.getReportPath()+"performance_report_mainreport" + ".jasper");
			
			rptMainReportFile = new File(BeanParameter.getReportPath()+"performance_report_mainreport" + ".jasper");
			JRDataSource mainDataSource = createDataSource(mainDataList);
			
			//subreport
			rptFileSubReport = new File(BeanParameter.getReportPath()+"performance_subreport1" + ".jasper");
			
			JasperReport rtfMainReport = (JasperReport) JRLoader.loadObject(rptMainReportFile.getPath());
			JasperReport rtfSubReport = (JasperReport) JRLoader.loadObject(rptFileSubReport.getPath());
			
			//parameter for subreport
			HashMap parameter_subreport = new HashMap();
			parameter_subreport.put("userPrint", user.getSourceName());
			
			//parameter for subreport
			parameterMap.put("subreportParameter", rtfSubReport);
			parameterMap.put("subDataList", subDataList);
			parameterMap.put("SUBREPORT_DIR",BeanParameter.getReportPath());
			parameterMap.put("found_data_subreport",subDataList!=null && subDataList.size()>0?"found":"");
			parameterMap.put("parameter_subreport",parameter_subreport);
			
			
			JasperPrint rtfPrint = JasperFillManager.fillReport(rtfMainReport, parameterMap, mainDataSource);

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
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setContentType("application/pdf");
			response.setContentLength(bytes.length);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(bytes, 0, bytes.length);
			servletOutputStream.flush();
			servletOutputStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
			  if(conn != null){
				conn.close();conn=null;
			  }
			}catch(Exception ee){}
		}
		return mapping.findForward("report"); 
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("search Report");
		User user = (User) request.getSession().getAttribute("user");
		try{
		   
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
		return "report";
	}
	
	public JRDataSource createDataSource(Collection reportRows) {
		JRBeanCollectionDataSource dataSource;
		dataSource = new JRBeanCollectionDataSource(reportRows);

		return dataSource;
	}

	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		PerformanceReportFormSubReport customerForm = (PerformanceReportFormSubReport) form;
		try {

			// Save Token
			saveToken(request);
		} catch (Exception e) {
		   
			try {
				conn.rollback();
			} catch (Exception e2) {}
			e.printStackTrace();
			return "prepare";
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "view";
	}


	@Override
	protected void setNewCriteria(ActionForm form) {
		PerformanceReportFormSubReport customerForm = (PerformanceReportFormSubReport) form;
		//customerForm.setCriteria(new CustomerCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
