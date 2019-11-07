package com.isecinc.pens.web.autoorder;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.order.OrderForm;
import com.isecinc.pens.web.summary.SummaryExport;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.EnvQuartzProperties;
import com.pens.util.Utils;

/**
 * AutoOrderAction
 * 
 * @author witty:17/10/2019
 * @version 
 * 
 */
public class AutoOrderAction extends I_Action {

	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		AutoOrderForm aForm = (AutoOrderForm) form;
		String action = Utils.isNull(request.getParameter("action"));
		try {
			if("new".equalsIgnoreCase(action)){
				//prepare parameter
				AutoOrderBean bean = new AutoOrderBean();
				bean.setOrderDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				//for test
				bean.setStoreCode("020047-1");
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
	   return mapping.findForward("search");
	}
	
	public ActionForward genAutoOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genAutoOrder");
		AutoOrderForm aForm = (AutoOrderForm) form;
		String action = Utils.isNull(request.getParameter("action"));
		try {
			if("new".equalsIgnoreCase(action)){
				//prepare parameter
				AutoOrderBean bean = new AutoOrderBean();
				bean.setOrderDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
	   return mapping.findForward("search");
	}
	 public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		AutoOrderForm aForm = (AutoOrderForm) form;;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 //searchBatch
			 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
			 
			 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		String htmlTable = "";
		String fileName ="data.xls";
		try {
			/** Export before search **/
			if(request.getAttribute("DATA_JOJO") ==null){
				String reportType = Utils.isNull(request.getParameter("reportType"));
				String startDate = Utils.isNull(request.getParameter("startDate"));
				String endDate = Utils.isNull(request.getParameter("endDate"));
				
				logger.debug("reportType:"+reportType);
				logger.debug("startDate:"+startDate);
				logger.debug("endDate:"+endDate);
			    String result = AutoOrderDAO.searchReport(reportType, startDate, endDate);
				
				request.setAttribute("DATA_JOJO", result);
			}
			htmlTable = (String)request.getAttribute("DATA_JOJO");
			
		    java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable);
		    w.flush();
		    w.close();
		
		    out.flush();
		    out.close();
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
	   return mapping.findForward("export");
	}
    
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return "";
	}
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "search";
	}
    
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("prepare1");
		request.getSession().setAttribute("DATA_JOJO", null);
		return "process";
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("prepare2");
		request.getSession().setAttribute("DATA_JOJO", null);
		return "search";
	}
	
	@Override
	protected void setNewCriteria(ActionForm form) {
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
