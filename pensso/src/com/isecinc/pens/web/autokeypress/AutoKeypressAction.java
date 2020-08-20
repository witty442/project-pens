package com.isecinc.pens.web.autokeypress;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.PopupDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class AutoKeypressAction {
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	
	/**
	 * Search
	 * resultAjax 
	 *      found = size|codeSearch|desc|desc2|...
	 *  not found = -1|codeSerach|..|...
	 */
	public String search(HttpServletRequest request) throws Exception {
		String resultAjax = "";
		PopupForm criteriaForm = new PopupForm();
		PopupBean popupBeanResult = null;
		PopupBean cri = new PopupBean();
		Map<String, String> criteriaMap = new HashMap<String, String>();
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			String pageName = Utils.isNull(request.getParameter("pageName"));
			
			if("Product".equalsIgnoreCase(pageName) ){
				//set criteria
				cri.setCodeSearch(Utils.isNull(request.getParameter("productCode")));
				
				popupBeanResult = AutoKeypressDAO.searchProduct(cri); 
				if(popupBeanResult != null){
					resultAjax  = "5|"+popupBeanResult.getCode()+"|"+popupBeanResult.getProductId()+"|"+popupBeanResult.getDesc();
					resultAjax += "|"+popupBeanResult.getUom1()+"|"+popupBeanResult.getUom2();
				}else{
					resultAjax = "-1||";
				}
			}else if("BRAND".equalsIgnoreCase(pageName) ){
				//set criteria
				cri.setCodeSearch(Utils.isNull(request.getParameter("brand")));
				criteriaForm.setBean(cri);
				List<PopupBean> resultList = PopupDAO.searchBrand(criteriaForm,true);
				
				if(resultList != null && resultList.size()>0){
					popupBeanResult = resultList.get(0) ;
					resultAjax  = "2|"+popupBeanResult.getCode()+"|"+popupBeanResult.getDesc();
				}else{
					resultAjax = "-1||";
				}
			}else if("SUB_BRAND".equalsIgnoreCase(pageName) ){
				//set criteria
				cri.setCodeSearch(Utils.isNull(request.getParameter("subBrand")));
				criteriaMap.put("brand", Utils.isNull(request.getParameter("brand")));
				
				criteriaForm.setBean(cri);
				criteriaForm.setCriteriaMap(criteriaMap);
				List<PopupBean> resultList = PopupDAO.searchSubbrandList(criteriaForm,true);
				
				if(resultList != null && resultList.size()>0){
					popupBeanResult = resultList.get(0) ;
					resultAjax  = "2|"+popupBeanResult.getCode()+"|"+popupBeanResult.getDesc();
				}else{
					resultAjax = "-1||";
				}
			}else if("PRODUCT_INFO".equalsIgnoreCase(pageName) ){
				//set criteria
				cri.setCodeSearch(Utils.isNull(request.getParameter("productCode")));
                criteriaMap.put("brand", Utils.isNull(request.getParameter("brand")));
                criteriaMap.put("subBrand", Utils.isNull(request.getParameter("subBrand")));
                
				criteriaForm.setBean(cri);
				criteriaForm.setCriteriaMap(criteriaMap);
				List<PopupBean> resultList = PopupDAO.searchProductInfoList(criteriaForm,true);
				
				if(resultList != null && resultList.size()>0){
					popupBeanResult = resultList.get(0) ;
					resultAjax  = "2|"+popupBeanResult.getCode()+"|"+popupBeanResult.getDesc();
				}else{
					resultAjax = "-1||";
				}
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}
		return resultAjax;
	}
}
