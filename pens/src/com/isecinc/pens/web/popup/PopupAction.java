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
		String forward = "";
		PopupForm popupForm = (PopupForm) form;
		try {
			 logger.debug("prepare");
			 
			 if("new".equalsIgnoreCase(request.getParameter("action")) && "BRAND".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("BRAND_LIST", null);
				 popupForm.setCodeSearch("");
				 popupForm.setDescSearch("");
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchBrand";
			 }else  if("new".equalsIgnoreCase(request.getParameter("action")) && "INVOICE".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("INVOICE_LIST", null);
				 popupForm.setCodeSearch("");
				 popupForm.setDescSearch("");
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchInvoice";
			 }else  if("new".equalsIgnoreCase(request.getParameter("action")) && "INVOICE_STOCK_RETURN".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("INVOICE_LIST", null);
				 popupForm.setCodeSearch("");
				 popupForm.setDescSearch("");
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchInvoiceStockReturn";
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
		return "search";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PopupForm popupForm = (PopupForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String forward = "";
		try {
			String storeType = Utils.isNull(request.getParameter("storeType"));
			logger.debug("StoreType["+storeType+"]");
			if("BRAND".equalsIgnoreCase(request.getParameter("page"))){
				 List<PopupForm> results = PopupDAO.searchBrand(popupForm,"");
				 if(results != null && results.size() >0){
					 request.setAttribute("BRAND_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
				 forward = "searchBrand";
			}else if("INVOICE".equalsIgnoreCase(request.getParameter("page"))){
				String productCode = Utils.isNull(request.getParameter("productCode"));
				String userId = Utils.isNull(request.getParameter("userId"));
				String customerCode = Utils.isNull(request.getParameter("customerCode"));
				
				popupForm.setCustomerCode(customerCode);
				popupForm.setUserId(userId);
				popupForm.setProductCode(productCode);
				
				 List<PopupForm> results = PopupDAO.searchInvoice(popupForm,"");
				 
				 if(results != null && results.size() >0){
					 request.setAttribute("INVOICE_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
				 forward = "searchInvoice";
				 
			}else if("INVOICE_STOCK_RETURN".equalsIgnoreCase(request.getParameter("page"))){
				String productCode = Utils.isNull(request.getParameter("productCode"));
				String userId = Utils.isNull(request.getParameter("userId"));
				String customerCode = Utils.isNull(request.getParameter("customerCode"));
				String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
				
				popupForm.setCustomerCode(customerCode);
				popupForm.setUserId(userId);
				popupForm.setProductCode(productCode);
				popupForm.setRequestNumber(requestNumber);
				
				 List<PopupForm> results = PopupDAO.searchInvoiceStockReturn(popupForm,"",user);
				 
				 if(results != null && results.size() >0){
					 request.setAttribute("INVOICE_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
				 forward = "searchInvoiceStockReturn";
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
		return "search";
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		PopupForm summaryForm = (PopupForm) form;
		try {
			 request.getSession().setAttribute("BRAND_LIST", null);
			 
			 request.getSession().setAttribute("codes", null);
			 request.getSession().setAttribute("keys", null);
			 request.getSession().setAttribute("descs", null);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
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
