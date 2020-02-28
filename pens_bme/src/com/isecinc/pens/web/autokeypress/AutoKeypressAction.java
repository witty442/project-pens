package com.isecinc.pens.web.autokeypress;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.dao.BMEProductDAO;
import com.isecinc.pens.dao.PopupDAO;
import com.isecinc.pens.init.InitialMessages;
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
			
			if("SalesrepSales".equalsIgnoreCase(pageName) ){
				//set criteria
				criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("salesrepCode")));
				
				PopupForm popupForm = AutoKeypressDAO.searchSalesrepSalesDetail(criteriaForm);
				if(popupForm != null){
					resultAjax = "4|"+popupForm.getCode()+"|"+popupForm.getDesc()+"|"+popupForm.getDesc2()+"|"+popupForm.getDesc3();
				}else{
					resultAjax = "-1|||";
				}
			}else if("CustomerAutoSub".equalsIgnoreCase(pageName) ){
				//set criteria
				criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("toStoreCode")));
				
				List<PopupForm> popupList=   PopupDAO.searchCustomerAutoSubList(criteriaForm);
				PopupForm popupForm = null;
				if(popupList != null && popupList.size() >0){
					popupForm = popupList.get(0);
				}
				if(popupForm != null){
					resultAjax = "4|"+popupForm.getCode()+"|"+popupForm.getDesc()+"|"+popupForm.getDesc2()+"|"+popupForm.getDesc3();
				}else{
					resultAjax = "-1|||";
				}
			}else if("BMEProduct".equalsIgnoreCase(pageName) ){
				
				//set criteria
				PopupBean criBean = new PopupBean();
				criBean.setCustGroup(Utils.isNull(request.getParameter("custGroup")));
				criBean.setPensItem(Utils.isNull(request.getParameter("pensItem")));
				criBean.setCustGroup(Utils.isNull(request.getParameter("custGroup")));
				
				PopupBean resultPopupBean = BMEProductDAO.searchBMEProduct(criBean);
				if(resultPopupBean != null){
					resultAjax = "3|"+resultPopupBean.getPensItem()+"|"+resultPopupBean.getMaterialMaster()+"|"+resultPopupBean.getGroupCode();
				}else{
					resultAjax = "-1|||";
				}
            }else if("StoreOrderRep".equalsIgnoreCase(pageName) ){
				
                criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("storeCode")));
				
				List<PopupForm> popupList=   PopupDAO.searchStoreOrderRepList(criteriaForm);
				PopupForm popupForm = null;
				if(popupList != null && popupList.size() >0){
					popupForm = popupList.get(0);
				}
				if(popupForm != null){
					resultAjax = "2|"+popupForm.getCode()+"|"+popupForm.getDesc();
				}else{
					resultAjax = "-1|||";
				}
            }else if("Store".equalsIgnoreCase(pageName) ){
				
                criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("storeCode")));
				
				List<PopupForm> popupList=   PopupDAO.searchStoreList(criteriaForm);
				PopupForm popupForm = null;
				if(popupList != null && popupList.size() >0){
					popupForm = popupList.get(0);
				}
				if(popupForm != null){
					//no of return|storeCode|storeName|storeNo|subInv
					resultAjax = "4|"+popupForm.getCode()+"|"+popupForm.getDesc()+"|"+popupForm.getDesc2()+"|"+popupForm.getDesc3();
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
