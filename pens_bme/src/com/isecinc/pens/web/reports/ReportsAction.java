package com.isecinc.pens.web.reports;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.DiffStockSummary;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.PhysicalSummary;
import com.isecinc.pens.bean.TransactionSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.summary.process.GenerateMonthEndLotus;
import com.isecinc.pens.summary.process.GenerateStockEndDateLotus;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.batchtask.task.GenStockOnhandTempTask;
import com.isecinc.pens.web.reports.page.ReportEndDateLotusAction;
import com.isecinc.pens.web.summary.report.OpenBillRobinsonReportAction;
import com.isecinc.pens.web.summary.report.ReportOnhandAsOfKingAction;
import com.isecinc.pens.web.summary.report.ReportOnhandAsOfRobinsonAction;
import com.isecinc.pens.web.summary.report.ReportOnhandBigCOracleAction;
import com.isecinc.pens.web.summary.report.ReportOnhandSizeColorBigCAction;
import com.isecinc.pens.web.summary.report.ReportOnhandSizeColorKingAction;
import com.isecinc.pens.web.summary.report.ReportOnhandSizeColorLotusAction;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ReportsAction extends I_Action {

	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "reports";
		ReportsForm reportsForm = (ReportsForm) form;
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 reportsForm.setResults(null);
				 
				 //Default display have qty
				 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					return new ReportEndDateLotusAction().prepare(form, request, response);
				 }
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
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
		return "reports";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReportsForm reportsForm = (ReportsForm) form;
		logger.debug("page["+reportsForm.getPageName()+"]");
		try {
			 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(reportsForm.getPageName())) ){
				 return new ReportEndDateLotusAction().search(reportsForm, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "reports";
	}
	public ActionForward genMonthEnd(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genMonthEnd");
		ReportsForm reportsForm = (ReportsForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String storeType = "";
		String yearMonth = "";
		String[] results = new String[3];
		try{
			storeType = Utils.isNull(request.getParameter("storeType"));
			if("lotus".equalsIgnoreCase(storeType)){
				
				//results = GenerateMonthEndLotus.generateMonthLotus(reportsForm.getBean(), user);
				if("".equals(results[2])){
				  request.setAttribute("Message", "Generate Month End เรียบร้อยแล้ว");
				}else{
				  request.setAttribute("Message", "ไม่สามารถ Generate Month End ได้ \n "+results[2]);
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reports");
	}
	
	public ActionForward genEndDate(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genMonthEnd");
		ReportsForm reportsForm = (ReportsForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String storeType = "";
		String yearMonth = "";
		String[] results = new String[3];
		try{
			storeType = Utils.isNull(request.getParameter("storeType"));
			if("lotus".equalsIgnoreCase(storeType)){
				
				/*results = GenerateEndDateLotus.generateEndDateLotus(summaryForm.getOnhandSummary(), user);
				if("".equals(results[2])){
				  request.setAttribute("Message", "Generate End Date เรียบร้อยแล้ว");
				}else{
				  request.setAttribute("Message", "ไม่สามารถ Generate End Date ได้ \n "+results[2]);
				}*/
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reports");
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		ReportsForm reportsForm = (ReportsForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			logger.debug("PageAction:"+request.getParameter("page"));
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reports");
	}
	
	
		
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//Connection conn = null;
		//SummaryForm summaryForm = (SummaryForm) form;
		try {
			 
		} catch (Exception e) {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
	}

	/** For batch popup **/
	public ActionForward genStockOnhandTemp(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genStockOnhandTemp");
		ReportsForm aForm = (ReportsForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Prepare Parameter to BatchTask
			Map<String, String> batchParaMap = new HashMap<String, String>();
			batchParaMap.put(GenStockOnhandTempTask.PARAM_ASOF_DATE, aForm.getBean().getAsOfDate());
			batchParaMap.put(GenStockOnhandTempTask.PARAM_STORE_CODE, aForm.getBean().getPensCustCodeFrom());
			
			logger.debug("storeCode:"+aForm.getBean().getPensCustCodeFrom());
			
			request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
			request.setAttribute("action","submitedGenStockOnhandTemp");//set to popup page to BatchTask
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
		}
		return mapping.findForward("reports");
	}
	/** For batch popup after Task success**/
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		ReportsForm aForm = (ReportsForm) form;
		User user = (User) request.getSession().getAttribute("user");
		//String pageName = aForm.getPageName(); 
		try {
			//logger.debug("searchBatch :pageName["+pageName+"]");
	
			 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
			 logger.debug("batchTaskForm result size:"+batchTaskForm.getResults().length);
			 
			 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
			 
			 logger.debug("batchName:"+batchTaskForm.getResults()[0].getName());
			 logger.debug("fileName:"+batchTaskForm.getMonitorItem().getFileName());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("reports");
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReportsForm reportsForm = (ReportsForm) form;
		try {
			
			 	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
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
