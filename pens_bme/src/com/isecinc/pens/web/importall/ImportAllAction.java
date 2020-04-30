package com.isecinc.pens.web.importall;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.batchtask.task.ImportExcelSwitchItemAdjStockTask;
import com.isecinc.pens.web.importall.page.ImportExcelPICG899ToG07Action;
import com.isecinc.pens.web.importall.page.ImportFileSwitchItemAdjustStockAction;
import com.isecinc.pens.web.importall.page.ImportMasterOrderREPAction;
import com.pens.util.Utils;

/**
 * ImportAllAction
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
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
			     return new ImportMasterOrderREPAction().prepare(form, request, response);
			 }else if("ImportExcelPICG899ToG07".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ImportExcelPICG899ToG07Action().prepare(form, request, response);
			 }else if("ImportFileSwitchItemAdjustStock".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ImportFileSwitchItemAdjustStockAction().prepare(form, request, response);
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
	public ActionForward importExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm aForm = (ImportAllForm) form;
		try {
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ImportMasterOrderREPAction().importExcel(mapping, aForm, request, response);
			 }else if("ImportExcelPICG899ToG07".equalsIgnoreCase(aForm.getPageName())){
				return new ImportExcelPICG899ToG07Action().importExcel(mapping,form, request, response);
			 }else if("ImportFileSwitchItemAdjustStock".equalsIgnoreCase(aForm.getPageName()) ){
				 return new ImportFileSwitchItemAdjustStockAction().importExcel(mapping,form, request, response);
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
		}
		return mapping.findForward("importAll");
	}
	
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm aForm = (ImportAllForm) form;
		try {
			logger.debug("PageName:"+aForm.getPageName());
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ImportMasterOrderREPAction().view(mapping, aForm, request, response);
			 }else if("ImportExcelPICG899ToG07".equalsIgnoreCase(aForm.getPageName())){
				 
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("importAll");
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm aForm = (ImportAllForm) form;
		try {
			logger.debug("PageName:"+aForm.getPageName());
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ImportMasterOrderREPAction().export(mapping, aForm, request, response);
			 }else if("ImportExcelPICG899ToG07".equalsIgnoreCase(aForm.getPageName())){
				 
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("importAll");
	}
	/** For batch popup after Task success**/
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm aForm = (ImportAllForm) form;
		try {
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ImportMasterOrderREPAction().searchBatch(mapping, aForm, request, response);
			 }else if("ImportExcelPICG899ToG07".equalsIgnoreCase(aForm.getPageName())){
		        return new ImportExcelPICG899ToG07Action().searchBatch(mapping, aForm, request, response);
			 }else if("ImportFileSwitchItemAdjustStock".equalsIgnoreCase(aForm.getPageName()) ){
				 return new ImportFileSwitchItemAdjustStockAction().searchBatch(mapping,form, request, response);
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{	
		}
		return mapping.findForward("importAll");
	}
	/** Search BatchTask Lastest Run **/
	public ActionForward searchBatchForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatchForm");
		ImportAllForm aForm = (ImportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ImportMasterOrderREPAction().searchBatchForm(mapping, aForm, request, response);
			 }else if("ImportExcelPICG899ToG07".equalsIgnoreCase(aForm.getPageName())){
		        return new ImportExcelPICG899ToG07Action().searchBatchForm(mapping, aForm, request, response);
			 }else if("ImportFileSwitchItemAdjustStock".equalsIgnoreCase(aForm.getPageName()) ){
				 return new ImportFileSwitchItemAdjustStockAction().searchBatchForm(mapping,form, request, response);
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{	
		}
		return mapping.findForward("importAll");
	}
	/** Clear Search BatchTask Lastest Run **/
	public ActionForward clearBatchForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearBatchForm");
		ImportAllForm aForm = (ImportAllForm) form;
		try {
			 if("importMasterOrderREP".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ImportMasterOrderREPAction().clearBatchForm(mapping, aForm, request, response);
			 }else if("ImportExcelPICG899ToG07".equalsIgnoreCase(aForm.getPageName())){
		        return new ImportExcelPICG899ToG07Action().clearBatchForm(mapping, aForm, request, response);
			 }else if("ImportFileSwitchItemAdjustStock".equalsIgnoreCase(aForm.getPageName()) ){
				 return new ImportFileSwitchItemAdjustStockAction().clearBatchForm(mapping,form, request, response);
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{	
		}
		return mapping.findForward("importAll");
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
