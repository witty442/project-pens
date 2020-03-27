package com.isecinc.pens.web.itmanage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ITManageAction extends I_Action {

	public static int pageSize = 30;
	public static String STATUS_SAVE ="SV";
	public static String PAGE_IT_STOCK ="ITStock";
	public static String PAGE_IT_M_W ="Wait";
	
	public ActionForward prepareHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareHead");
		ITManageForm aForm = (ITManageForm) form;
		Connection conn = null;
		try {
			String pageName = Utils.isNull(request.getParameter("pageName"));
			aForm.setPageName(pageName);
			
			if(PAGE_IT_STOCK.equals(aForm.getPageName())){
				return new ITStockAction().prepare2(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		ITManageForm aForm = (ITManageForm) form;
		try {
			if(PAGE_IT_STOCK.equals(aForm.getPageName())){
			   return new ITStockAction().search2(mapping, aForm, request, response);			
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	

	public ActionForward clearHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearSearch");
		ITManageForm aForm = (ITManageForm) form;
		try {
			if(PAGE_IT_STOCK.equals(aForm.getPageName())){
				 return new ITStockAction().clear2(mapping, aForm, request, response);			
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		ITManageForm aForm = (ITManageForm) form;
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ITManageForm aForm = (ITManageForm) form;
		try {
			logger.debug("prepare ID");
			if(PAGE_IT_STOCK.equals(aForm.getPageName())){
				 return new ITStockAction().prepare(aForm, request, response);			
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "detail";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ITManageForm aForm = (ITManageForm) form;
		try {
			if(PAGE_IT_STOCK.equals(aForm.getPageName())){
				 return new ITStockAction().save(aForm, request, response);			
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			try {
				
			} catch (Exception e2) {}
			return "";
		} finally {
		}
		return "";
	}
	
	public ActionForward deleteAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug(" deleteAction ");
		ITManageForm aForm = (ITManageForm) form;
		try {
			if(PAGE_IT_STOCK.equals(aForm.getPageName())){	
				 return new ITStockAction().deleteAction(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		} finally {
			
		}
		return  mapping.findForward("");
	}
	
	/**
	 * Print Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug(" Printreport ");
		ITManageForm aForm = (ITManageForm) form;
		try {
			if(PAGE_IT_STOCK.equals(aForm.getPageName())){
				//Update Or Save  Before  Print
				 new ITStockAction().save(aForm, request, response);	
				 
				 return new ITStockAction().printReport(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			logger.info("Print report  Error");
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		} finally {
			
		}
		return  mapping.findForward("printPayPopup");
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
