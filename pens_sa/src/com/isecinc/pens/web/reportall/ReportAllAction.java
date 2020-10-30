package com.isecinc.pens.web.reportall;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.analyst.ProjectCTargetAction;
import com.isecinc.pens.web.reportall.page.BoxNoNissinReportAction;
import com.isecinc.pens.web.reportall.page.EffectiveSKUReportAction;
import com.isecinc.pens.web.reportall.page.ProjectCReportAction;
import com.isecinc.pens.web.reportall.page.StockReturnAction;
import com.isecinc.pens.web.reportall.page.StockExpireDetailAction_DESPRICATE;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ReportAllAction extends I_Action {

	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "reportAll";
		ReportAllForm reportAllForm = (ReportAllForm) form;
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 reportAllForm.setResults(null);
				 
				 //Default display have qty
				 if("ProjectCReport".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new ProjectCReportAction().prepare(form, request, response);
				 }else if("StockReturn".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new StockReturnAction().prepare(form, request, response);
				 }else if("StockExpireDetail".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					// return new StockExpireDetailAction().prepare(form, request, response);
				 }else if("BoxNoNissinReport".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new BoxNoNissinReportAction().prepare(form, request, response);
				 }else if("EffectiveSKUReport".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new EffectiveSKUReportAction().prepare(form, request, response);
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
		ReportAllForm aForm = (ReportAllForm) form;
		logger.debug("page["+aForm.getPageName()+"]");
		try {
			 if("ProjectCReport".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ProjectCReportAction().search(form, request, response);
			 }else if("StockReturn".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new StockReturnAction().search(form, request, response);
			 }else if("StockExpireDetail".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				// return new StockExpireDetailAction().search(form, request, response);
			 }else if("BoxNoNissinReport".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new BoxNoNissinReportAction().search(form, request, response);
			 }else if("EffectiveSKUReport".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new EffectiveSKUReportAction().search(form, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "reportAll";
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ReportAllForm aForm = (ReportAllForm) form;
		try {
			logger.debug("PageAction:"+request.getParameter("page"));
			 if("ProjectCReport".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ProjectCReportAction().export(mapping, aForm, request, response);
			 }else if("BoxNoNissinReport".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new BoxNoNissinReportAction().export(mapping, aForm, request, response);
			 }else if("EffectiveSKUReport".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new EffectiveSKUReportAction().export(mapping, aForm, request, response);
			 }else if("StockExpireDetail".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				// return new StockExpireDetailAction().export(mapping, aForm, request, response);
		     }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
	
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ReportAllForm aForm = (ReportAllForm) form;
		try {
			logger.debug("PageName:"+aForm.getPageName());
		    if("StockReturn".equalsIgnoreCase(aForm.getPageName()) ){
			   return new StockReturnAction().printReport(mapping, form, request, response);
		    }else if("StockExpireDetail".equalsIgnoreCase(aForm.getPageName()) ){
			  // return new StockExpireDetailAction().printReport(mapping, form, request, response);
		    }else if("BoxNoNissinReport".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
			   return new BoxNoNissinReportAction().printReport(mapping, aForm, request, response);
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
	
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ReportAllForm aForm = (ReportAllForm) form;
		try {
			logger.debug("PageName:"+aForm.getPageName());
		    if("StockExpireDetail".equalsIgnoreCase(aForm.getPageName()) ){
			   return new StockExpireDetailAction_DESPRICATE().printReport(mapping, form, request, response);
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
