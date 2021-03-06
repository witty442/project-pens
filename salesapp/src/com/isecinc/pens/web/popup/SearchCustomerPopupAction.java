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
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SearchCustomerPopupAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		PopupForm popupForm = (PopupForm) form;
		try {
			 logger.debug("prepare");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.setAttribute("CUSTOMER_LIST", null);
				 popupForm.setCode("");
				 popupForm.setDesc("");
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
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
			logger.debug("prepare 2");
		
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
		try {
			String storeType = Utils.isNull(request.getParameter("storeType"));
			logger.debug("StoreType["+storeType+"]");
			
			 List<PopupForm> results = null;//SummaryDAO.searchCustomerMaster(popupForm,storeType,"");
			 if(results != null && results.size() >0){
				 request.setAttribute("CUSTOMER_LIST", results);
			 }else{
				 request.setAttribute("Message", "��辺������");
			 }
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
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
		PopupForm summaryForm = (PopupForm) form;
		try {
			 request.getSession().setAttribute("results", null);
			 
			 request.getSession().setAttribute("codes", null);
			 request.getSession().setAttribute("keys", null);
			 request.getSession().setAttribute("descs", null);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		PopupForm popupForm = (PopupForm) form;
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.setAttribute("CUSTOMER_LIST", null);
				 popupForm.setCode("");
				 popupForm.setDesc("");
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		PopupForm popupForm = (PopupForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String storeType = Utils.isNull(request.getParameter("storeType"));
			logger.debug("StoreType["+storeType+"]");
			
			 List<PopupForm> results = PopupDAO.searchCustomerMaster(popupForm,"");
			 if(results != null && results.size() >0){
				 request.setAttribute("CUSTOMER_LIST", results);
			 }else{
				 request.setAttribute("Message", "��辺������");
			 }
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search2");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		PopupForm popupForm = (PopupForm) form;
		try {
             request.getSession().setAttribute("results", null);
			 
			 request.getSession().setAttribute("codes", null);
			 request.getSession().setAttribute("keys", null);
			 request.getSession().setAttribute("descs", null);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear2");
	}
	public ActionForward prepare3(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare3");
		PopupForm popupForm = (PopupForm) form;
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.setAttribute("CUSTOMER_LIST", null);
				 popupForm.setCode("");
				 popupForm.setDesc("");
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepare3");
	}
	
	public ActionForward search3(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search3");
		PopupForm popupForm = (PopupForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String storeType = Utils.isNull(request.getParameter("storeType"));
			logger.debug("StoreType["+storeType+"]");
			
			 List<PopupForm> results = PopupDAO.searchCustomerMasterAndAddress(popupForm,"",user);
			 if(results != null && results.size() >0){
				 request.setAttribute("CUSTOMER_LIST", results);
			 }else{
				 request.setAttribute("Message", "��辺������");
			 }
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepare3");
	}
	
	public ActionForward clear3(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear3");
		PopupForm popupForm = (PopupForm) form;
		try {
             request.getSession().setAttribute("results", null);
			 
			 request.getSession().setAttribute("codes", null);
			 request.getSession().setAttribute("keys", null);
			 request.getSession().setAttribute("descs", null);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepare3");
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
