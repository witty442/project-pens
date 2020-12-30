package com.isecinc.pens.web.popup;

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
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.Transport;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.PopupDAO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MStockDiscount;
import com.isecinc.pens.model.MStockReturn;
import com.isecinc.pens.model.MTransfer;
import com.isecinc.pens.model.MTransport;
import com.isecinc.pens.web.buds.page.ConfPickingDAO;

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
			 logger.debug("prepare page["+request.getParameter("page")+"]");
			 request.getSession().removeAttribute("search_submit");
			 
			 if("new".equalsIgnoreCase(request.getParameter("action")) && "BRAND".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("BRAND_LIST", null);
				 PopupBean bean = new PopupBean();
				 bean.setCodeSearch("");
				 bean.setDescSearch("");
				 popupForm.setBean(bean);
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchBrand";
			 }else  if("new".equalsIgnoreCase(request.getParameter("action")) && "INVOICE".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("INVOICE_LIST", null);
				 PopupBean bean = new PopupBean();
				 bean.setCodeSearch("");
				 bean.setDescSearch("");
				 popupForm.setBean(bean);
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchInvoice";
			 }else  if("new".equalsIgnoreCase(request.getParameter("action")) && "INVOICE_STOCK_RETURN".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("INVOICE_LIST", null);
				 PopupBean bean = new PopupBean();
				 bean.setCodeSearch("");
				 bean.setDescSearch("");
				 popupForm.setBean(bean);
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchInvoiceStockReturn";
			 }else  if("new".equalsIgnoreCase(request.getParameter("action")) && "INVOICE_STOCK_DISCOUNT".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("INVOICE_LIST", null);
				 PopupBean bean = new PopupBean();
				 bean.setCodeSearch("");
				 bean.setDescSearch("");
				 popupForm.setBean(bean);
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchInvoiceStockDiscount";
				 
			 }else  if("new".equalsIgnoreCase(request.getParameter("action")) && "TRANSPORT".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("DATA_LIST", null);
				 PopupBean bean = new PopupBean();
				 bean.setCodeSearch("");
				 bean.setDescSearch("");
				 popupForm.setBean(bean);
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchTransport";
			 }else  if("new".equalsIgnoreCase(request.getParameter("action")) && "PICKING_NO".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("DATA_LIST", null);
				 PopupBean bean = new PopupBean();
				 bean.setCodeSearch("");
				 bean.setDescSearch("");
				 popupForm.setBean(bean);
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchPickingNo";
			 }else  if("new".equalsIgnoreCase(request.getParameter("action")) && "SUBBRAND_STOCK".equalsIgnoreCase(request.getParameter("page"))){
				 request.setAttribute("DATA_LIST", null);
				 PopupBean bean = new PopupBean();
				 bean.setCodeSearch("");
				 bean.setDescSearch("");
				 popupForm.setBean(bean);
				 request.getSession().setAttribute("INIT_SUBBRAND_LIST", PopupDAO.searchSubbrandList(popupForm,false));
			
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
				 forward = "searchSubBrandStock";
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
			logger.debug("Page["+request.getParameter("page")+"]");
			logger.debug("StoreType["+storeType+"]");
			
			if("INVOICE".equalsIgnoreCase(request.getParameter("page"))){
				String productCode = Utils.isNull(request.getParameter("productCode"));
				String userId = Utils.isNull(request.getParameter("userId"));
				String customerCode = Utils.isNull(request.getParameter("customerCode"));
				
				PopupBean bean = new PopupBean();
				bean.setCustomerCode(customerCode);
				bean.setUserId(userId);
				bean.setProductCode(productCode);
				
				 List<PopupBean> results = PopupDAO.searchInvoice(bean,"");
				 
				 if(results != null && results.size() >0){
					 request.setAttribute("INVOICE_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข้อมูล");
				 }
				 forward = "searchInvoice";
				 
			}else if("INVOICE_STOCK_RETURN".equalsIgnoreCase(request.getParameter("page"))){
				String productCode = Utils.isNull(request.getParameter("productCode"));
				String userId = Utils.isNull(request.getParameter("userId"));
				String customerCode = Utils.isNull(request.getParameter("customerCode"));
				String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
				
				PopupBean bean = new PopupBean();
				bean.setCustomerCode(customerCode);
				bean.setUserId(userId);
				bean.setProductCode(productCode);
				bean.setRequestNumber(requestNumber);
				
				 List<PopupBean> results = MStockReturn.searchInvoiceStockReturn(bean,"",user);
				 
				 if(results != null && results.size() >0){
					 request.setAttribute("INVOICE_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข้อมูล");
				 }
				 forward = "searchInvoiceStockReturn";
				 
			}else if("INVOICE_STOCK_DISCOUNT".equalsIgnoreCase(request.getParameter("page"))){
				String productCode = Utils.isNull(request.getParameter("productCode"));
				String userId = Utils.isNull(request.getParameter("userId"));
				String customerCode = Utils.isNull(request.getParameter("customerCode"));
				String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
				
				logger.debug("popup requestNumber:"+requestNumber);
				PopupBean bean = new PopupBean();
				bean.setCustomerCode(customerCode);
				bean.setUserId(userId);
				bean.setProductCode(productCode);
				bean.setRequestNumber(requestNumber);
				
				List<PopupForm> results = null;//MStockDiscount.searchInvoiceStockDiscount(bean,"",user);
				 
				if(results != null && results.size() >0){
				   request.setAttribute("INVOICE_LIST", results);
				}else{
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				forward = "searchInvoiceStockDiscount";
			}else if("TRANSPORT".equalsIgnoreCase(request.getParameter("page"))){
		
			/*	List<Transport> results = MTransport.searchTransport(popupForm,user);
				 
				if(results != null && results.size() >0){
				   request.setAttribute("DATA_LIST", results);
				}else{
				   request.setAttribute("Message", "ไม่พบข่อมูล");
				}
				forward = "searchTransport";*/
				 
			}else if("SUBBRAND_STOCK".equalsIgnoreCase(request.getParameter("page"))){
				
				PopupBean c = PopupDAO.searchProductStockBySubbrand(popupForm.getBean(),user);
				
				if(c.getDataList() != null && c.getDataList() .size() >0){
					popupForm.setBean(c);
				}else{
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				forward = "searchSubBrandStock";
           }else if("PICKING_NO".equalsIgnoreCase(request.getParameter("page"))){
        	   List<PopupBean> results =PopupDAO.searchPickingNoList(popupForm,"PICKING_NO");
				if(results != null && results.size() >0){
				  request.setAttribute("DATA_LIST", results);
				}else{
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				forward = "searchPickingNo";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
	}
	
	public ActionForward prepareAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareAll");
		PopupForm popupForm = (PopupForm) form;
		String forward = "popupAll";
		String pageName  =Utils.isNull(request.getParameter("pageName"));
		String action  =Utils.isNull(request.getParameter("action"));
		try {
			logger.debug("prepare action:"+action+",pageName["+pageName+"]");
			 
			 if("new".equalsIgnoreCase(action) ){
				 request.getSession().setAttribute("search_submit", null);
				 request.getSession().setAttribute("DATA_LIST", null);
				 popupForm.setBean(new PopupBean());
				 popupForm.setPageName(pageName);
				 popupForm.setCriteriaMap(null);
				 
				 //set Criteria From Main Page
				 String[] queryStr = request.getQueryString().split("\\&");
				 logger.debug("queryStr:"+queryStr);
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
				 request.getSession().setAttribute("descs2", null);
				 request.getSession().setAttribute("descs3", null);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward(forward);
	}
	/** NEW CODE **/
	public ActionForward searchAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchAll");
		PopupForm popupForm = (PopupForm) form;
		String forward = "popupAll";
		try {
			
			if( !"newsearch".equalsIgnoreCase(Utils.isNull(request.getParameter("action")))){
				return mapping.findForward(forward);
			}else{
				//newsearch clear session
				request.getSession().setAttribute("DATA_LIST", null);
			}
			
			if("BRAND".equalsIgnoreCase(popupForm.getPageName()) ){
				 List<PopupBean> results = PopupDAO.searchBrand(popupForm,false);
				 if(results != null && results.size() >0){
					 request.getSession().setAttribute("DATA_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
			 }else if("SUB_BRAND".equalsIgnoreCase(popupForm.getPageName()) ){
				 List<PopupBean> results = PopupDAO.searchSubbrandList(popupForm,false);
				 if(results != null && results.size() >0){
					 request.getSession().setAttribute("DATA_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
			 }else if("PRODUCT_INFO".equalsIgnoreCase(popupForm.getPageName()) ){
				 List<PopupBean> results = PopupDAO.searchProductInfoList(popupForm,false);
				 if(results != null && results.size() >0){
					 request.getSession().setAttribute("DATA_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
			 }else if("PICKING_NO".equalsIgnoreCase(popupForm.getPageName()) ){
				 List<PopupBean> results = PopupDAO.searchPickingNoList(popupForm,popupForm.getPageName());
				 if(results != null && results.size() >0){
					 request.getSession().setAttribute("DATA_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
			 }else if("PICKING_NO_PRINT".equalsIgnoreCase(popupForm.getPageName()) ){
				 List<PopupBean> results = PopupDAO.searchPickingNoList(popupForm,popupForm.getPageName());
				 if(results != null && results.size() >0){
					 request.getSession().setAttribute("DATA_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
		     }else if("PICKING_NO_INVOICE".equalsIgnoreCase(popupForm.getPageName()) ){
				 List<PopupBean> results = PopupDAO.searchPickingNoList(popupForm,popupForm.getPageName());
				 if(results != null && results.size() >0){
					 request.getSession().setAttribute("DATA_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
		     }else if("CUSTOMER".equalsIgnoreCase(popupForm.getPageName()) ){
				 List<PopupBean> results = PopupDAO.searchCustomerList(popupForm,false);
				 if(results != null && results.size() >0){
					 request.getSession().setAttribute("DATA_LIST", results);
				 }else{
					 request.setAttribute("Message", "ไม่พบข่อมูล");
				 }
		     }
			
			 request.getSession().setAttribute("search_submit","search_submit");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward(forward);
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
