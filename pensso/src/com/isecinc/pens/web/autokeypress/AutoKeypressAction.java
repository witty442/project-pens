package com.isecinc.pens.web.autokeypress;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
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
		PopupBean criteriaForm = new PopupBean();
		PopupBean popupForm  = null;
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			String pageName = Utils.isNull(request.getParameter("pageName"));
			
			if("Product".equalsIgnoreCase(pageName) ){
				//set criteria
				criteriaForm.setCodeSearch(Utils.isNull(request.getParameter("productCode")));
				
				popupForm = AutoKeypressDAO.searchProduct(criteriaForm); 
				if(popupForm != null){
					resultAjax  = "5|"+popupForm.getCode()+"|"+popupForm.getProductId()+"|"+popupForm.getDesc();
					resultAjax += "|"+popupForm.getUom1()+"|"+popupForm.getUom2();
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
