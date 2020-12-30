package com.isecinc.pens.web.checkkeyexist;

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
import com.isecinc.pens.web.buds.page.OrderEDIDAO;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class CheckKeyExistAllAction {
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	
	/**
	 * checkKeyExist 
	 *      true = key exist
	 *      false = key no exist
	 */
	public String checkKeyExist(HttpServletRequest request) throws Exception {
		String returnText = "false";
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			String pageName = Utils.isNull(request.getParameter("pageName"));
			String keyCheck = Utils.isNull(request.getParameter("keyCheck"));
			
			if("EDI_MANUAL".equalsIgnoreCase(pageName) ){
				String keyCheckResult = OrderEDIDAO.getCustPONoOrderEDIExist(keyCheck);
				if( !Utils.isNull(keyCheckResult).equals("")){
					returnText = "true";
				}
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}
		return returnText;
	}
}
