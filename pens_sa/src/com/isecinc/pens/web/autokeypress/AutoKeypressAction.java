package com.isecinc.pens.web.autokeypress;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupDAO;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.Utils;

public class AutoKeypressAction {
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	
	/**
	 * Search
	 * resultAjax = size|codeSearch|desc|desc2|...
	 *  not found = -1|codeSerach||
	 */
	public String search(HttpServletRequest request) throws Exception {
		String resultAjax = "";
		PopupForm criteriaForm = new PopupForm();
		try {
			String pageName = Utils.isNull(request.getParameter("pageName"));
			
			if("CustomerProjectC".equalsIgnoreCase(pageName) ){
				//set criteria
				criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("storeCode")));
				
				List<PopupForm> popupList=   PopupDAO.searchCustomerCreditSalesProjectCList(request,criteriaForm);
				PopupForm popupForm = null;
				if(popupList != null && popupList.size() >0){
					popupForm = popupList.get(0);
				}
				if(popupForm != null){
					resultAjax = "2|"+popupForm.getCode()+"|"+popupForm.getDesc();
				}else{
					resultAjax = "-1|||";
				}
			}else if("BranchProjectC".equalsIgnoreCase(pageName) ){
				//set criteria
				criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("branchId")));
				
				List<PopupForm> popupList=   PopupDAO.searchCustomerBranchCreditSalesProjectCList(request,criteriaForm);
				PopupForm popupForm = null;
				if(popupList != null && popupList.size() >0){
					popupForm = popupList.get(0);
				}
				if(popupForm != null){
					resultAjax = "2|"+popupForm.getCode()+"|"+popupForm.getDesc();
				}else{
					resultAjax = "-1|||";
				}
			}else if("Brand".equalsIgnoreCase(pageName) ){
				//set criteria
				criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("brand")));
				
				List<PopupForm> popupList=   PopupDAO.searchBrandList(criteriaForm);
				PopupForm popupForm = null;
				if(popupList != null && popupList.size() >0){
					popupForm = popupList.get(0);
				}
				if(popupForm != null){
					resultAjax = "2|"+popupForm.getCode()+"|"+popupForm.getDesc();
				}else{
					resultAjax = "-1|||";
				}
			}else if("CustomerStockMC".equalsIgnoreCase(pageName) ){
				//set criteria
				criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("customerCode")));
				
				List<PopupForm> popupList=   PopupDAO.searchCustomerStockMC(criteriaForm);
				PopupForm popupForm = null;
				if(popupList != null && popupList.size() >0){
					popupForm = popupList.get(0);
				}
				if(popupForm != null){
					resultAjax = "2|"+popupForm.getCode()+"|"+popupForm.getDesc();
				}else{
					resultAjax = "-1|||";
				}
			}else if("Brand".equalsIgnoreCase(pageName) ){
				//set criteria
				criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("brand")));
				
				List<PopupForm> popupList=   PopupDAO.searchBrandList(criteriaForm);
				PopupForm popupForm = null;
				if(popupList != null && popupList.size() >0){
					popupForm = popupList.get(0);
				}
				if(popupForm != null){
					resultAjax = "2|"+popupForm.getCode()+"|"+popupForm.getDesc();
				}else{
					resultAjax = "-1|||";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return resultAjax;
	}
}
