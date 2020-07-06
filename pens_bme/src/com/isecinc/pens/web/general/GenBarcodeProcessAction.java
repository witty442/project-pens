package com.isecinc.pens.web.general;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.BarcodeUtils;
import com.pens.util.Utils;

public class GenBarcodeProcessAction extends DispatchAction{
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GeneralForm aForm =(GeneralForm) form;
		String action = Utils.isNull(request.getParameter("action"));
		try {
			 if("new".equalsIgnoreCase(action)){
			   aForm.setBean(new GeneralBean());
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return "prepare";
	}
	
	public ActionForward submitExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("submitExecute");
		GeneralForm aForm =(GeneralForm) form;
		try {
			GeneralBean bean = aForm.getBean();
			if( !Utils.isNull(bean.getProductCode()).equals("")){
				bean.setBarcode(BarcodeUtils.genBarcode(Utils.isNull(bean.getProductCode())));
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
	   return mapping.findForward("prepare");
	}
}
