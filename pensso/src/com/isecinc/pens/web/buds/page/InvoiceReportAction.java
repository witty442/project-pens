package com.isecinc.pens.web.buds.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.InvoiceReportBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.buds.BudsAllBean;
import com.isecinc.pens.web.buds.BudsAllForm;
import com.isecinc.pens.web.buds.page.export.ControlPickingExport;
import com.pens.util.BeanParameter;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class InvoiceReportAction extends I_Action {

	public static int pageSize = 60;
	
	public ActionForward prepareSearchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearchHead");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		BudsAllBean bean = new BudsAllBean();
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				InvoiceReportBean invoiceReportBean = new InvoiceReportBean();
				//01/mm/yyyy
				String startDate = DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				startDate = "01"+startDate.substring(2,startDate.length());
				invoiceReportBean.setTransactionDateFrom(startDate);
				invoiceReportBean.setTransactionDateTo(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				//invoiceReportBean.setSearchProvince("0");
				invoiceReportBean.setDispHaveRemain("true");
				bean.setInvoiceReportBean(invoiceReportBean);
				aForm.setBean(bean);
			 }
			 aForm.setPageName(Utils.isNull(request.getParameter("pageName")));
			 aForm.setSubPageName(Utils.isNull(request.getParameter("subPageName")));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return mapping.findForward("budsAll");
	}
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "budsAll";
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "";
	}

	/**
	 * Search
	 */
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "budsAll";
	}
	
	public ActionForward printInvoiceReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printInvoiceReport");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		InvoiceReportBean bean = null;
		try {
			//search and export
			 bean = InvoiceReportDAO.searchInvoiceReport(budsAllForm.getBean().getInvoiceReportBean(), false, user);
			
			if(bean.getDataStrBuffer() != null &&  bean.getDataStrBuffer().length() >0){
				request.setAttribute("budsAllForm_RESULTS",bean.getDataStrBuffer());
				request.setAttribute("budsAllForm_total_RESULTS", bean.getRowTotalStrBuffer());
				bean.setDataStrBuffer(null);//clear memory
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
			budsAllForm.getBean().setInvoiceReportBean(bean);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
	
	public ActionForward exportInvoiceReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportInvoiceReport");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		InvoiceReportBean bean = null;
		try {
			//set parameter
			
			//search and export
			 bean = InvoiceReportDAO.searchInvoiceReport(budsAllForm.getBean().getInvoiceReportBean(), true, user);
			
			if(bean.getDataStrBuffer() != null &&  bean.getDataStrBuffer().length() >0){
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(bean.getDataStrBuffer().toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			    
			    //clear memory
			    bean.setDataStrBuffer(null);
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			 
		} catch (Exception e) {
			request.setAttribute("Message",e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
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
