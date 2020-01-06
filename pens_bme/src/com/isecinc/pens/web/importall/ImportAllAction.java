package com.isecinc.pens.web.importall;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.importall.page.ImportMasterOrderREPAction;
import com.isecinc.pens.web.reportall.page.ReportEndDateLotusAction;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ImportAllAction extends I_Action {

	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "importAll";
		ImportAllForm importAllForm = (ImportAllForm) form;
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 importAllForm.setResults(null);
				 
				 //Default display have qty
				 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 //return new ReportEndDateLotusAction().prepare(form, request, response);
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
		try {
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "importAll";
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm aForm = (ImportAllForm) form;
		try {
			logger.debug("PageAction:"+request.getParameter("page"));
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("importAll");
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

	public ActionForward importExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm aForm = (ImportAllForm) form;
		try {
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ImportMasterOrderREPAction().importExcel(mapping, aForm, request, response);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
		}
		return mapping.findForward("importAll");
	}
	
	/** For batch popup after Task success**/
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm aForm = (ImportAllForm) form;
		try {
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 //return new ReportEndDateLotusAction().searchBatch(mapping, aForm, request, response);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("importAll");
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
