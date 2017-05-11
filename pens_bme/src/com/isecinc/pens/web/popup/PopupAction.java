package com.isecinc.pens.web.popup;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.PopupDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PopupAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		PopupForm popupForm = (PopupForm) form;
		try {
			 logger.debug("prepare action:"+request.getParameter("action"));
			 if("new".equalsIgnoreCase(request.getParameter("action")) 
					&& "searchPensItemByGroupPopup".equalsIgnoreCase(request.getParameter("page")) ){
				 forward = "searchPensItemByGroupPopup";
				 
				 request.setAttribute("GROUP_LIST", null);
				 popupForm.setCode("");
				 popupForm.setDesc("");
				 popupForm.setNo(0);
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
			 }else if("new".equalsIgnoreCase(request.getParameter("action")) 
						&& "searchEmpPopup".equalsIgnoreCase(request.getParameter("page")) ){
					 forward = "searchEmpPopup";
					 
					 request.setAttribute("EMP_LIST", null);
					 popupForm.setCode("");
					 popupForm.setDesc("");
					 popupForm.setNo(0);
					 
					 request.getSession().setAttribute("codes", null);
					 request.getSession().setAttribute("keys", null);
					 request.getSession().setAttribute("descs", null);
					 
			 }else if("new".equalsIgnoreCase(request.getParameter("action")) 
						&& "searchCheckStockDatePopup".equalsIgnoreCase(request.getParameter("page")) ){
					 request.getSession().setAttribute("codes", null);
					 request.getSession().setAttribute("keys", null);
					 request.getSession().setAttribute("descs", null);
					 
					 String empId = Utils.isNull(request.getParameter("empId"));
					 String type = Utils.isNull(request.getParameter("type"));
					 
					 forward = "searchCheckStockDatePopup";
					 List<PopupForm> results = PopupDAO.searchSACheckStockDate(empId,type);
					 if(results != null && results.size() >0){
						 request.setAttribute("CHECK_STOCK_DATE_LIST", results);
					 }else{
						 request.setAttribute("Message", "ไม่พบข่อมูล");
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
		PopupForm popupForm = (PopupForm) form;
		try {
			logger.debug("prepare 2 action:"+request.getParameter("action"));
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PopupForm popupForm = (PopupForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String forward = "";
		try {
			if("searchPensItemByGroupPopup".equalsIgnoreCase(request.getParameter("page")) ){
				 forward = "searchPensItemByGroupPopup";
				 List<PopupForm> results = PopupDAO.searchPensItemByGroupCode(popupForm);
				 if(results != null && results.size() >0){
					 request.setAttribute("GROUP_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
			}else if("searchEmpPopup".equalsIgnoreCase(request.getParameter("page")) ){
					 forward = "searchEmpPopup";
					 List<PopupForm> results = PopupDAO.searchSAEmp(popupForm);
					 if(results != null && results.size() >0){
						 request.setAttribute("EMP_LIST", results);
					 }else{
						 request.setAttribute("Message", "ไม่พบข่อมูล");
					 }
			}else if("searchCheckStockDatePopup".equalsIgnoreCase(request.getParameter("page")) ){
				 forward = "searchCheckStockDatePopup";
				 String empId = Utils.isNull(request.getParameter("empId"));
				 String type = Utils.isNull(request.getParameter("type"));
				 List<PopupForm> results = PopupDAO.searchSACheckStockDate(empId,type);
				 if(results != null && results.size() >0){
					 request.setAttribute("CHECK_STOCK_DATE_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
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

	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		PopupForm popupForm = (PopupForm) form;
		String forward = "";
		try {
			if("searchPensItemByGroupPopup".equalsIgnoreCase(request.getParameter("page")) ){
				 forward = "searchPensItemByGroupPopup";
				 request.setAttribute("GROUP_LIST", null);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward(forward);
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
