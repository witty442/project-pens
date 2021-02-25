package com.isecinc.pens.web.manual;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MManualUpdateAddress;
import com.pens.util.Utils;

/**
 * ManualUpdateAddress Class
 * 
 * @author Witty.B
 * @version $Id: ManualUpdateAddress.java ,v 1.0 19/10/2010 00:00:00
 * 
 */

public class ManualUpdateAddressAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Prepare Form");
		ManualUpdateAddressForm formBean = (ManualUpdateAddressForm) form;
		String returnText = "prepare";
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		
		return returnText;
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Prepare Form without ID");
		ManualUpdateAddressForm ManualUpdateAddressForm = (ManualUpdateAddressForm) form;
		String returnText = "prepare";
		try {
			ManualUpdateAddressForm.setResults(null);
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
		}finally{
			//conn.close();
		}
		return returnText;
	}

	
	
	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Interfaces Search Current Action");
		ManualUpdateAddressForm formBean = (ManualUpdateAddressForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		try {
			String textRemove = Utils.isNull(request.getParameter("textRemove"));
		
			MManualUpdateAddress md = new MManualUpdateAddress();
            ManualUpdateAddressBean[] m = md.findUpdateAddress(textRemove,null);
            if(m != null){
                formBean.setResults(m);
                formBean.setSize(m.length);
            }else{
            	request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
            }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
	}

	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ManualUpdateAddressForm formBean = (ManualUpdateAddressForm) form;
		try {
			String textRemove = Utils.isNull(request.getParameter("textRemove"));
            MManualUpdateAddress md = new MManualUpdateAddress();
            int c = md.updateAddress(textRemove);
			request.setAttribute("Message","\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E40\u0E23\u0E35\u0E22\u0E1A\u0E23\u0E49\u0E2D\u0E22\u0E41\u0E25\u0E49\u0E27 <br> \u0E08\u0E33\u0E19\u0E27\u0E19\u0E23\u0E32\u0E22\u0E01\u0E32\u0E23\u0E17\u0E35\u0E48\u0E2D\u0E31\u0E1E\u0E40\u0E14\u0E15\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 ID \u0E43\u0E2B\u0E21\u0E48 :"+c);
			
			String[] addressIds = request.getParameterValues("addressIds");
			String addressIdsCond = "";
			if(addressIds != null){
				for(int i=0;i<addressIds.length;i++){
					if(i==addressIds.length-1){
					   addressIdsCond +=addressIds[i];
					}else{
					   addressIdsCond +=addressIds[i]+",";	
					}
				}
			}
			
            ManualUpdateAddressBean[] m = md.findUpdateAddress(textRemove,addressIdsCond);
            if(m != null){
                formBean.setResults(m);
                formBean.setSize(m.length);
            }
            
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()+ e.toString());	
		}
		return "prepare";
	}

	
	@Override
	protected void setNewCriteria(ActionForm form) {
		
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
