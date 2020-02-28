package com.isecinc.pens.web.reportall;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.importall.ImportAllForm;
import com.isecinc.pens.web.importall.page.ImportExcelPICG899ToG07Action;
import com.isecinc.pens.web.reportall.page.ReportEndDateLotusAction;
import com.isecinc.pens.web.reportall.page.ReportOnhandAsOfRobinsonAction;
import com.isecinc.pens.web.reportall.page.ReportOnhandLotusAction;
import com.isecinc.pens.web.reportall.page.ReportSizeColorBigCAction;
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
				 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new ReportEndDateLotusAction().prepare(form, request, response);
				 }else if("reportSizeColorBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new ReportSizeColorBigCAction().prepare(form, request, response);
				 }else if("reportOnhandLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new ReportOnhandLotusAction().prepare(form, request, response);
				 }else if("reportOnhandAsOfRobinson".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new ReportOnhandAsOfRobinsonAction().prepare(form, request, response);
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
		ReportAllForm reportAllForm = (ReportAllForm) form;
		logger.debug("page["+reportAllForm.getPageName()+"]");
		try {
			 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(reportAllForm.getPageName())) ){
				 return new ReportEndDateLotusAction().search(form, request, response);
			 }else if("reportSizeColorBigC".equalsIgnoreCase(Utils.isNull(reportAllForm.getPageName())) ){
				 return new ReportSizeColorBigCAction().search(form, request, response);
			 }else if("reportOnhandLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ReportOnhandLotusAction().search(form, request, response);
			 }else if("reportOnhandAsOfRobinson".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ReportOnhandAsOfRobinsonAction().search(form, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "reportAll";
	}
	public ActionForward genMonthEnd(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genMonthEnd");
		ReportAllForm aForm = (ReportAllForm) form;
		try{
			 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportEndDateLotusAction().genMonthEnd(mapping, aForm, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
	
	public ActionForward genEndDate(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genEndDate");
		ReportAllForm aForm = (ReportAllForm) form;
		try{
			 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportEndDateLotusAction().genEndDate(mapping, aForm, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ReportAllForm aForm = (ReportAllForm) form;
		try {
			logger.debug("PageName:"+Utils.isNull(aForm.getPageName()));
			 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportEndDateLotusAction().export(mapping, form, request, response);
			 }else if("reportSizeColorBigC".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportSizeColorBigCAction().export(mapping, form, request, response);
			 }else if("reportOnhandLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportOnhandLotusAction().export(mapping, form, request, response);
			 }else if("reportOnhandAsOfRobinson".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ReportOnhandAsOfRobinsonAction().export(mapping,form, request, response);
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

	/** For batch popup **/
	public ActionForward genStockOnhandRepTemp(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ReportAllForm aForm = (ReportAllForm) form;
		try {
			 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportEndDateLotusAction().genStockOnhandRepTemp(mapping, form, request, response);
			 }else  if("reportSizeColorBigC".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportSizeColorBigCAction().genStockOnhandRepTemp(mapping, form, request, response);
			 }
			 
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
		ReportAllForm aForm = (ReportAllForm) form;
		try {
			 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportEndDateLotusAction().searchBatch(mapping, form, request, response);
			 }else if("reportSizeColorBigC".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				return new ReportSizeColorBigCAction().searchBatch(mapping, form, request, response);
			 }else if("reportOnhandLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				return new ReportOnhandLotusAction().searchBatch(mapping, form, request, response);
			 }else if("reportOnhandAsOfRobinson".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ReportOnhandAsOfRobinsonAction().searchBatch(mapping,form, request, response);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("reportAll");
	}
	public ActionForward downloadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ReportAllForm aForm = (ReportAllForm) form;
		try {
			 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				
			 }else if("reportSizeColorBigC".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				
			 }else if("reportOnhandLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportOnhandLotusAction().downloadFile(mapping, form, request, response);
			 }else if("reportOnhandAsOfRobinson".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ReportOnhandAsOfRobinsonAction().downloadFile(mapping,form, request, response);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("reportAll");
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReportAllForm aForm = (ReportAllForm) form;
		try {
			 if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ReportEndDateLotusAction().clear(mapping, form, request, response);
			 }
			 	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	
	/** Search BatchTask Lastest Run **/
	public ActionForward searchBatchForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatchForm");
		ReportAllForm aForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 if("".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 
			 }else if("reportOnhandLotus".equalsIgnoreCase(aForm.getPageName())){
		         return new ReportOnhandLotusAction().searchBatchForm(mapping, aForm, request, response);
			 }else if("reportOnhandAsOfRobinson".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ReportOnhandAsOfRobinsonAction().searchBatchForm(mapping,form, request, response);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("reportAll");
	}
	
	/** Clear Search BatchTask Lastest Run **/
	public ActionForward clearBatchForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearBatchForm");
		ReportAllForm aForm = (ReportAllForm) form;
		try {
			 if("".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 
			 }else if("reportOnhandLotus".equalsIgnoreCase(aForm.getPageName())){
		        return new ReportOnhandLotusAction().clearBatchForm(mapping, aForm, request, response);
			 }else if("reportOnhandAsOfRobinson".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ReportOnhandAsOfRobinsonAction().clearBatchForm(mapping,form, request, response);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("reportAll");
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
