package com.isecinc.pens.web.report.salesanalyst;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Trip;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.interfaces.InterfacesForm;

/**
  WITTY
  SalesAnalystReportAction
 * 
 */
public class SalesAnalystReportAction extends I_Action {
	public static Logger logger = Logger.getLogger("PENS");
	
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SalesAnalystReportAction Prepare Form");
		SalesAnalystReportForm formBean = (SalesAnalystReportForm) form;
		String returnText = "prepare";
		try {
			
			//formBean.setSalesBean(new SalesAnalystBean());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		return returnText;
	}
	
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SalesAnalystReportAction Prepare Form");
		SalesAnalystReportForm formBean = (SalesAnalystReportForm) form;
		String returnText = "prepare";
		try {
			
			//formBean.setSalesBean(new SalesAnalystBean());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		
		return returnText;
	}
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("SalesAnalystReportAction Search Current Action");
		SalesAnalystReportForm formBean = (SalesAnalystReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		try {
			
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
	}
	
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("SalesAnalystReportAction Search Current Action");
		SalesAnalystReportForm formBean = (SalesAnalystReportForm) form;
		try {	
			

		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}


	/**
	 * Set New Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		
	}
	
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		InterfacesForm formBean = (InterfacesForm) form;
		try {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			request.setAttribute("type", SystemElements.ADMIN);
			request.setAttribute("searchKey", formBean.getCriteria().getSearchKey());
			
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "re-search";
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
