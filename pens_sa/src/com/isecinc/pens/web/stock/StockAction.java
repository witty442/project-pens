package com.isecinc.pens.web.stock;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
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
import util.DBConnection;
import util.ReportUtilServlet;
import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.salesanalyst.helper.FileUtil;
import com.isecinc.pens.web.promotion.RequestPromotionLine;
import com.lowagie.text.pdf.BaseFont;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockAction extends I_Action {

	public static int pageSize = 90;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockForm aForm = (StockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String pageName ="";
		String forward ="search";
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String popup = Utils.isNull(request.getParameter("popup")); 
			pageName = Utils.isNull(request.getParameter("pageName"));
			
			if("new".equals(action)){
				pageName = Utils.isNull(request.getParameter("pageName"));
				request.getSession().setAttribute("RESULTS",null);
				request.getSession(true).setAttribute("GEN_PDF_SUCCESS", null);
				
				StockBean sales = new StockBean();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
				//init Connection
				conn = DBConnection.getInstance().getConnection();
				
				if (StockConstants.PAGE_CREDIT.equalsIgnoreCase(pageName)){
					StockControlPage.prepareSearchCreditReport(request, conn, user,pageName);
					sales.setDispRequestDate("true");
					sales.setDispLastUpdate("true");
					
				}else if (StockConstants.PAGE_STOCK_CLOSE_VAN.equalsIgnoreCase(pageName)){
					forward ="stockVanReport";
					StockControlPage.prepareSearchStockCloseVanReport(request, conn, user,pageName);
					if(popup.equalsIgnoreCase("false")){
						forward ="search";
					}
					
				}else if (StockConstants.PAGE_STOCK_CLOSEPD_VAN.equalsIgnoreCase(pageName)){
					forward ="stockVanReport";
					StockControlPage.prepareSearchStockPDVanReport(request, conn, user,pageName);
					if(popup.equalsIgnoreCase("false")){
						forward ="search";
					}
				}
				aForm.setBean(sales);
			}else if("back".equals(action)){
				pageName = aForm.getPageName();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
			}
			logger.debug("forward:"+forward);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward(forward);
	}
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		StockForm aForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean foundData = false;
		String pageName = aForm.getPageName();
		String action = Utils.isNull(request.getParameter("action")); 
		try {
			logger.debug("search Head :pageName["+pageName+"]");
	
			  //Search Report
			if(StockConstants.PAGE_CREDIT.equalsIgnoreCase(pageName)){
				if(action.equalsIgnoreCase("sort")){
					aForm.getBean().setColumnNameSort(Utils.isNull(request.getParameter("columnNameSort")));
					aForm.getBean().setOrderSortType(Utils.isNull(request.getParameter("orderSortType")));
				}else{
					//search new
					request.getSession().setAttribute("RESULTS",null);
					aForm.getBean().setItemsList(null);
				}
				StockBean stockResult = StockReport.searchReport(request.getContextPath(),aForm.getBean(),false);
				StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
				if(resultHtmlTable != null){
					 request.getSession().setAttribute("RESULTS",resultHtmlTable);
					 foundData = true;
				}
				aForm.getBean().setItemsList(stockResult.getItemsList());
			}
			
			if(foundData==false){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   request.getSession().setAttribute("RESULTS",null);
			   aForm.getBean().setItemsList(null);
			}
			logger.debug("pageName:"+aForm.getPageName());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportReport : ");
		User user = (User) request.getSession().getAttribute("user");
		StockForm aForm = (StockForm) form;
		StringBuffer resultHtmlTable =null;
		String pageName = aForm.getPageName();
		Connection conn = null;
		String action = "";
		String popup = "";
		String forward ="stockVanReport";
		try {
			action = Utils.isNull(request.getParameter("action"));
			popup = Utils.isNull(request.getParameter("popup"));
			if(popup.equalsIgnoreCase("false")){
				forward ="search";
			}
			if(StockConstants.PAGE_CREDIT.equalsIgnoreCase(pageName)){
				StockBean stockResult = StockReport.searchReport(request.getContextPath(),aForm.getBean(),true);
				resultHtmlTable = stockResult.getDataStrBuffer();
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(resultHtmlTable.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
            }else if (StockConstants.PAGE_STOCK_CLOSE_VAN.equalsIgnoreCase(pageName)){
            	forward ="stockVanReport";
            	String fileNameExport = "stock_close_van_"+user.getUserName()+".pdf";
            	
				//pdf
            	conn = DBConnection.getInstance().getConnectionApps();
            	ReportUtilServlet reportServlet = new ReportUtilServlet();
    			HashMap<String,Object> parameterMap = new HashMap<String,Object>();
    			ServletContext context = request.getSession().getServletContext();
    			List<StockCloseVanBean> lstData = null;
    	
            	String fileName = "stock_close_van_report";
                String fileJasper =  BeanParameter.getReportPath() + fileName;
                
                //get criteria
                StockBean criBean = new StockBean();
                criBean.setPeriod(aForm.getBean().getPeriod());
                
                //get data
                StockCloseVanBean beanReport = StockCloseVanReport.searchStockCloseVanList(conn,criBean,user);
                if(beanReport != null){
                	lstData = beanReport.getItemsList();
                }
                //set parameterMap
                Date periodDate = Utils.parse(criBean.getPeriod(), Utils.MMM_YY);
                String thPeriodDate = Utils.stringValue(periodDate, Utils.MMMM_YYYY,Utils.local_th);
				parameterMap.put("period",thPeriodDate);
				
				//summary map
				parameterMap.put("s_begin_pri_qty",beanReport.getBegin_pri_qty());
				parameterMap.put("s_begin_sec_char",beanReport.getBegin_sec_char());
				parameterMap.put("s_sale_pri_qty",beanReport.getSale_pri_qty());
				parameterMap.put("s_sale_sec_char",beanReport.getSale_sec_char());
				parameterMap.put("s_prem_pri_qty",beanReport.getPrem_pri_qty());
				parameterMap.put("s_prem_sec_char",beanReport.getPrem_sec_char());
				parameterMap.put("s_total_sale_pri_qty",beanReport.getTotal_sale_pri_qty());
				parameterMap.put("s_total_sale_sec_char",beanReport.getTotal_sale_sec_char());
				parameterMap.put("s_rma_pri_qty",beanReport.getRma_pri_qty());
				parameterMap.put("s_rma_sec_char",beanReport.getRma_sec_char());
				parameterMap.put("s_receipt_pri_qty",beanReport.getReceipt_pri_qty());
				parameterMap.put("s_receipt_sec_char",beanReport.getReceipt_sec_char());
				parameterMap.put("s_return_pri_qty",beanReport.getReturn_pri_qty());
				parameterMap.put("s_return_sec_char",beanReport.getReturn_sec_char());
				parameterMap.put("s_adj_issue_pri_qty",beanReport.getAdj_issue_pri_qty());
				parameterMap.put("s_adj_issue_sec_char",beanReport.getAdj_issue_sec_char());
				parameterMap.put("s_adj_receipt_pri_qty",beanReport.getAdj_receipt_pri_qty());
				parameterMap.put("s_adj_receipt_sec_char",beanReport.getAdj_receipt_sec_char());
				parameterMap.put("s_end_pri_qty",beanReport.getEnd_pri_qty());
				parameterMap.put("s_end_sec_char",beanReport.getEnd_sec_char());
			
				//Get SalesRepName
				SalesrepBean salesrepBean =  SalesrepDAO.getSalesrepBeanByCode(conn, user.getUserName());
				parameterMap.put("salesFullName",user.getUserName()+"-"+salesrepBean.getSalesrepFullName());
				
				if(lstData != null && lstData.size() >0){ 
					//Calc Total Page PageSize =30
				    int totalPage = Utils.calcTotalPage(lstData.size(), 30);
				    parameterMap.put("p_total_page",totalPage);
				    logger.debug("totalPage:"+totalPage);
				    
					runReportListToODF_TOLOCAL_FILE(request, response, conn, fileJasper, lstData, parameterMap, fileName, fileNameExport);
				
				    request.getSession(true).setAttribute("GEN_PDF_SUCCESS", "TRUE");
				    
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				
            }else if (StockConstants.PAGE_STOCK_CLOSEPD_VAN.equalsIgnoreCase(pageName)){
            	forward ="stockVanReport";
            	String fileNameExport = "stock_closepd_van_"+user.getUserName()+".pdf";
            	
				//pdf
            	conn = DBConnection.getInstance().getConnectionApps();
    			HashMap<String,Object> parameterMap = new HashMap<String,Object>();
    			List<StockPDVanBean> lstData = null;
    	
            	String fileName = "stock_closepd_van_report";
                String fileJasper =  BeanParameter.getReportPath() + fileName;
                
                //get criteria
                StockBean criBean = new StockBean();
                criBean.setPeriod(aForm.getBean().getPeriod());
                criBean.setPdCode(aForm.getBean().getPdCode());
                
                //get data
                StockPDVanBean beanReport = StockPDVanReport.searchStockPDVanList(conn,criBean,user);
                if(beanReport != null){
                	lstData = beanReport.getItemsList();
                }
                //set parameterMap
                Date periodDate = Utils.parse(criBean.getPeriod(), Utils.MMM_YY);
                String thPeriodDate = Utils.stringValue(periodDate, Utils.MMMM_YYYY,Utils.local_th);
				parameterMap.put("period",thPeriodDate);
				
				//summary map
				parameterMap.put("s_begin_pri_qty",beanReport.getBegin_pri_qty());
				parameterMap.put("s_begin_sec_char",beanReport.getBegin_sec_char());
				parameterMap.put("s_receipt_pri_qty",beanReport.getReceipt_pri_qty());
				parameterMap.put("s_receipt_sec_char",beanReport.getReceipt_sec_char());
				parameterMap.put("s_transact_pri_qty",beanReport.getTransact_pri_qty());
				parameterMap.put("s_transact_sec_char",beanReport.getTransact_sec_char());
				parameterMap.put("s_return_pri_qty",beanReport.getReturn_pri_qty());
				parameterMap.put("s_return_sec_char",beanReport.getReturn_sec_char());
				parameterMap.put("s_van_pri_qty",beanReport.getVan_pri_qty());
				parameterMap.put("s_van_sec_char",beanReport.getVan_sec_char());
				parameterMap.put("s_end_pri_qty",beanReport.getEnd_pri_qty());
				parameterMap.put("s_end_sec_char",beanReport.getEnd_sec_char());
			
				//Get PD Desc
				parameterMap.put("pdDesc",aForm.getBean().getPdCode()+"-"+beanReport.getPdDesc());
				
				if(lstData != null && lstData.size() >0){
				   // reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName, lstData,fileNameExport);
					//Calc Total Page PageSize =30
				   
					int totalPage = Utils.calcTotalPage(lstData.size(), 30);
				    parameterMap.put("p_total_page",totalPage);
				    logger.debug("totalPage:"+totalPage);
				    
					runReportListToODF_TOLOCAL_FILE(request, response, conn, fileJasper, lstData, parameterMap, fileName, fileNameExport);
				
				    request.getSession(true).setAttribute("GEN_PDF_SUCCESS", "TRUE");
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				if(conn != null){
				   conn.close();
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward(forward);
	}
	
	
	private void runReportListToODF_TOLOCAL_FILE(HttpServletRequest request, HttpServletResponse response, Connection conn,
			String fileJasper, List lstData, HashMap parameterMap, String fileName,String fileNameExport) throws ServletException,
			Exception, JRException {
     
		File rptFile = null;
		fileName = fileName + ".pdf";
		String pathFileNameTemp = BeanParameter.getTempPath()+"\\"+ fileNameExport;
		try {
			logger.debug("temps real temp path :"+pathFileNameTemp);
			
			rptFile = new File(fileJasper + ".jasper");
			JRDataSource jrDataSource = new JRBeanCollectionDataSource(lstData);

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
			/*response.setHeader("Content-Disposition", "attachment;filename=" + fileNameExport);
			response.setContentType("application/pdf");
			response.setContentLength(bytes.length);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(bytes, 0, bytes.length);
			servletOutputStream.flush();
			servletOutputStream.close();*/
			
			//write to temp folder
			FileUtil.writeFile(pathFileNameTemp, bytes);

		} catch (JRException e) {
			throw e;
		}catch(Exception ee){
			throw ee;
		}
	}
	public ActionForward loadPDFReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportReport : ");
		User user = (User) request.getSession().getAttribute("user");
		StockForm aForm = (StockForm) form;
		String pageName = aForm.getPageName();
		String action = "";
		String popup = "";
		String forward = "stockVanReport";
		String fileNameExport = "";
		String pathFileNameTemp = BeanParameter.getTempPath()+"\\";
		try {
			
			action = Utils.isNull(request.getParameter("action"));
			popup = Utils.isNull(request.getParameter("popup"));
			if(popup.equalsIgnoreCase("false")){
				forward ="search";
			}
			/** load pdf to screen from temp file **/
			 if (StockConstants.PAGE_STOCK_CLOSE_VAN.equalsIgnoreCase(pageName)){
				 fileNameExport = "stock_close_van_"+user.getUserName()+".pdf";
				 pathFileNameTemp = BeanParameter.getTempPath()+"\\"+"stock_close_van_"+user.getUserName()+".pdf";
				 logger.debug("pathFileNameTemp:"+pathFileNameTemp);
				 
				//read file from temp file
				 byte[] bytes = FileUtil.readFileToByte(new FileInputStream(pathFileNameTemp));
				 
				response.setHeader("Content-Disposition", "attachment; filename=" + fileNameExport);
				response.setContentType("application/pdf");
				
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.write(bytes, 0, bytes.length);
				servletOutputStream.flush();
				servletOutputStream.close();
				
				/** load pdf to screen from temp file **/
			 }else  if (StockConstants.PAGE_STOCK_CLOSEPD_VAN.equalsIgnoreCase(pageName)){
				 fileNameExport = "stock_closepd_van_"+user.getUserName()+".pdf";
				 pathFileNameTemp = BeanParameter.getTempPath()+"\\"+"stock_closepd_van_"+user.getUserName()+".pdf";
				 logger.debug("pathFileNameTemp:"+pathFileNameTemp);
				 
				//read file from temp file
				 byte[] bytes = FileUtil.readFileToByte(new FileInputStream(pathFileNameTemp));
				 
				response.setHeader("Content-Disposition", "attachment; filename=" + fileNameExport);
				response.setContentType("application/pdf");
				
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.write(bytes, 0, bytes.length);
				servletOutputStream.flush();
				servletOutputStream.close();
			}
			 logger.debug("pathFileNameTemp:"+pathFileNameTemp);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
		}
		return mapping.findForward(forward);
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		StockForm aForm = (StockForm) form;
		String pageName = aForm.getPageName();
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		StockForm aForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "detail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockForm orderForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{}
		return "search";
	}
	
	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * Set new Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {

	}
	
	
}
