package com.isecinc.pens.web.popup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
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
		String forward = "popup";
		PopupForm popupForm = (PopupForm) form;
		String pageName  =Utils.isNull(request.getParameter("pageName"));
		String action  =Utils.isNull(request.getParameter("action"));
		try {
			 logger.debug("prepare action:"+action+",pageName["+pageName+"]");
			 
			 if("new".equalsIgnoreCase(action) ){
				 request.getSession().setAttribute("search_submit", null);
				 request.getSession().setAttribute("DATA_LIST", null);
				 popupForm.setCodeSearch("");
				 popupForm.setDescSearch("");
				 popupForm.setNo(0);
				 popupForm.setPageName(pageName);
				 popupForm.setCriteriaMap(null);
				 
				 //set Criteria From Main Page
				 String[] queryStr = request.getQueryString().split("\\&");
				// logger.debug("queryStr:"+queryStr);
				 if(queryStr != null && queryStr.length>0){
					 Map<String, String> criteriaMap = new HashMap<String, String>();
					 String paramName = "";
					 String paramValue = "";
					 String[] paramNameAll = null;
					 for(int i=0;i<queryStr.length;i++){
						
						 if( !queryStr[i].startsWith("do") && !queryStr[i].startsWith("action") 
							&& !queryStr[i].startsWith("pageName")){
							 
							logger.debug("queryStr[i]:"+queryStr[i]);
							 
						    paramNameAll = queryStr[i].split("\\=");
						    paramName = paramNameAll[0];
						    if(paramNameAll.length >1){
						       paramValue = paramNameAll[1];
						    }else{
						       paramValue ="";
						    }
						    logger.debug(paramName+":"+paramValue);
						    criteriaMap.put(paramName, paramValue);
						 }//if
					 }
					 popupForm.setCriteriaMap(criteriaMap);
				 }
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("descs", null);
				 
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
		String forward = "popup";
		List<PopupForm> results = null;
		try {
			if("Brand".equalsIgnoreCase(popupForm.getPageName()) ){
				 results = PopupDAO.searchBrandList(popupForm);
				 
			}else if("BrandStock".equalsIgnoreCase(popupForm.getPageName()) ){
				 results = PopupDAO.searchBrandStockList(popupForm);
				 
			}else if("BrandProdShow".equalsIgnoreCase(popupForm.getPageName()) ){
				 results = PopupDAO.searchBrandProdShowList(popupForm);
				 
			}else if("BrandStockVan".equalsIgnoreCase(popupForm.getPageName()) ){
				 results = PopupDAO.searchBrandStockVanList(popupForm);
				 
			}else if("Customer".equalsIgnoreCase(popupForm.getPageName()) ){
				//For SalesTarget
				 results = PopupDAO.searchCustomerList(popupForm);
				 
			}else if("CustomerStock".equalsIgnoreCase(popupForm.getPageName()) ){
				//For Stock
				 results = PopupDAO.searchCustomerStockList(popupForm);
				 
			}else if("CustomerVanProdShow".equalsIgnoreCase(popupForm.getPageName()) ){
				//For ProdShow Van
				 results = PopupDAO.searchCustomerVanProdShowList(popupForm);
				 
			}else if("CustomerCreditPromotion".equalsIgnoreCase(popupForm.getPageName()) ){
				//For Promotion Credit
				 results = PopupDAO.searchCustomerCreditPromotionList(popupForm);
				 
			}else if("ItemStock".equalsIgnoreCase(popupForm.getPageName()) ){
				//For Stock
				 results = PopupDAO.searchItemStockList(popupForm);
				 
			}else if("ItemCreditPromotion".equalsIgnoreCase(popupForm.getPageName()) ){
				//For Stock
				 results = PopupDAO.searchItemCreditPromotionList(popupForm);
				 
			}else if("CustomerLocation".equalsIgnoreCase(popupForm.getPageName()) ){
				//For Stock
				 results = PopupDAO.searchCustomerLocationList(popupForm);
				 
			}else if("CustomerLocNoTrip".equalsIgnoreCase(popupForm.getPageName()) ){
				//For Edit Trip
				 results = PopupDAO.searchCustomerLocNoTripList(popupForm);
				 
			}else if("PDStockVan".equalsIgnoreCase(popupForm.getPageName()) ){
				 results = PopupDAO.searchPDStockVanList(popupForm); 
				 
			}else if("ItemStockVan".equalsIgnoreCase(popupForm.getPageName()) ){
				 results = PopupDAO.searchItemStockVanList(popupForm); 
				 
			}else if("CustomerStockMC".equalsIgnoreCase(popupForm.getPageName()) ){
				results = PopupDAO.searchCustomerStockMC(popupForm);
				
			}else if("SubInvOnhand".equalsIgnoreCase(popupForm.getPageName()) ){
				results = PopupDAO.searchSubInvList(popupForm);
				
			}else if("Item".equalsIgnoreCase(popupForm.getPageName()) ){
				results = PopupDAO.searchItemList(popupForm);
				
			}else if("SalesrepCreditSales".equalsIgnoreCase(popupForm.getPageName()) ){
				results = PopupDAO.searchSalesrepCreditSalesList(popupForm);
				
			}else if("CustomerCreditSales".equalsIgnoreCase(popupForm.getPageName()) ){
				//For SalesTarget
				 results = PopupDAO.searchCustomerCreditSalesList(popupForm);
			}
			
			 if(results != null && results.size() >0){
				 request.getSession().setAttribute("DATA_LIST", results);
			 }else{
				 request.getSession().setAttribute("Message", "ไม่พบข่อมูล");
			 }
			 //mark flag search
			 request.getSession().setAttribute("search_submit", "search");
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
