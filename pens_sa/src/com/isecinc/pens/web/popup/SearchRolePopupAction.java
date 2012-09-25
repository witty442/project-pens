package com.isecinc.pens.web.popup;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.Role;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MRole;
import com.isecinc.pens.report.salesanalyst.SABean;
import com.isecinc.pens.report.salesanalyst.SAProcess;
import com.isecinc.pens.report.salesanalyst.helper.SAGenCondition;
import com.isecinc.pens.web.report.salesanalyst.SAReportForm;
import com.isecinc.pens.web.role.RoleForm;


/**
  WITTY
  SalesAnalystReportAction
 * 
 */
public class SearchRolePopupAction extends I_Action {
	protected Logger logger = Logger.getLogger("PENS");
	
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SearchValuePopupAction Prepare Form 1");
		SearchRolePopupForm formBean = (SearchRolePopupForm) form;
		String returnText = "prepare";
		try {
			request.getSession().setAttribute("VALUE_LIST",null);
			formBean.setSalesBean(new SABean());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		return returnText;
	}
	
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SearchValuePopupAction Prepare Form 2");
		SearchRolePopupForm formBean = (SearchRolePopupForm) form;
		String returnText = "prepare";
		try {
			 request.getSession().setAttribute("VALUE_LIST",null);
			 formBean.setSalesBean(new SABean());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			throw e;
		}
		
		return returnText;
	}
	
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("SearchRolePopupAction Search Current Action");
		SearchRolePopupForm forms = (SearchRolePopupForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		try {
			String condType = request.getParameter("condType");
			String condNo = request.getParameter("condNo");
			String searchType = request.getParameter("searchType");
			
			request.getSession().setAttribute("VALUE_LIST", SAProcess.getInstance().getConditionValueList4Role(request,condType,forms.getSalesBean().getCode(),forms.getSalesBean().getDesc()));	

		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
	}
	
	/**
	 * Set New Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		
	}
	
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;

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
