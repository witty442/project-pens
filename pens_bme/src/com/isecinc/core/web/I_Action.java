package com.isecinc.core.web;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import util.Paginator;

/**
 * I_Action Class
 * 
 * @author Atiz.b
 * @version $Id: I_Action.java,v 1.0 13/06/2010 15:52:00 atiz.b Exp $
 * 
 */
public abstract class I_Action extends DispatchAction {

	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");

	/**
	 * Prepare form to Add/Edit
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Prepare " + this.getClass());
		String forward = "prepare";
		try {
			if (request.getParameter("id") != null) {
				forward = prepare(request.getParameter("id"), form, request, response);
			} else {
				forward = prepare(form, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward(forward);
	}

	/**
	 * Search
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Search " + this.getClass());
		String forward = "search";
		try {
			forward = search(form, request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward(forward);
	}

	/**
	 * Save
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Save " + this.getClass());
		String forward = "prepare";
		try {
			forward = save(form, request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward(forward);
	}

	/**
	 * Change Active
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward changeActive(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Change Active " + this.getClass());
		String forward = "search";
		try {
			forward = changeActive(form, request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward(forward);
	}

	/**
	 * Clear Form
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward clearForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Clear Form " + this.getClass());
		String searchKey = (String) request.getSession(true).getAttribute(this.getClass().toString());
		if (searchKey != null) {
			request.getSession(true).removeAttribute(searchKey);
			request.removeAttribute(this.getClass().toString());
		}
		// Clear Criteria
		try {
			setNewCriteria(form);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("clearform");
	}

	/**
	 * Prepare Add, Edit, View
	 * 
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected abstract String prepare(String id, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	/**
	 * Prepare
	 * 
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 * 
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected abstract String search(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	/**
	 * Save
	 * 
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected abstract String save(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	/**
	 * Change Active
	 * 
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected abstract String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	/**
	 * New Criteria
	 * 
	 * @param form
	 */
	protected abstract void setNewCriteria(ActionForm form) throws Exception;

	/**
	 * Get Search Criteria
	 * 
	 * @param <T>
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends I_Criteria> T getSearchCriteria(HttpServletRequest request, T criteria, String attributeName) {
		T t = null;
		if (request.getParameter("rf") != null) {
			String searchKey = criteria.getSearchKey();
			criteria.setSearchKey("");
			logger.debug("New Search (Remove) : " + searchKey);
			// New Search .. Remove Search Key
			request.getSession(true).removeAttribute(searchKey);
			// Reset SearchKey in Session
			request.getSession(true).setAttribute(attributeName, "");
		}
		if (criteria.getSearchKey().trim().length() != 0) {
			// Get Criteria from Session with SearchKey
			t = (T) request.getSession(true).getAttribute(criteria.getSearchKey());
			logger.debug("Get Old Criteria : " + criteria.getSearchKey());
		}
		if (t == null) {
			String searchKey = String.valueOf(Calendar.getInstance().getTimeInMillis());
			criteria.setSearchKey(searchKey);
			logger.debug("Set New Criteria key : " + searchKey);
			// Set Criteria to Session with SearchKey
			request.getSession(true).setAttribute(searchKey, criteria);
			// Reset SearchKey in Session
			request.getSession(true).setAttribute(attributeName, searchKey);
			t = criteria;
		}
		return t;
	}

	// start paginator
	public ActionForward first(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//BaseForm baseForm = (BaseForm) form;
		
		Paginator page = (Paginator) request.getSession().getAttribute("PAGINATOR");
		page.first();
		request.getSession().setAttribute("PAGINATOR", page);
		return mapping.findForward("paging");
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//BaseForm baseForm = (BaseForm) form;
		
		Paginator page = (Paginator) request.getSession().getAttribute("PAGINATOR");
		page.previous();
		request.getSession().setAttribute("PAGINATOR", page);
		return mapping.findForward("paging");
	}

	public ActionForward next(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		//BaseForm baseForm = (BaseForm) form;
		
		Paginator page = (Paginator) request.getSession().getAttribute("PAGINATOR");
		page.next();
		request.getSession().setAttribute("PAGINATOR", page);
		return mapping.findForward("paging");
	}

	public ActionForward last(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//BaseForm baseForm = (BaseForm) form;
		
		Paginator page = (Paginator) request.getSession().getAttribute("PAGINATOR");
		page.last();
		request.getSession().setAttribute("PAGINATOR", page);
		return mapping.findForward("paging");
	}

	/*public ActionForward gotoPage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		BaseForm baseForm = (BaseForm) form;
		
		Paginator page = (Paginator) request.getSession().getAttribute("PAGINATOR");
		int pageNumber = Integer.parseInt(baseForm.getCurrentPage());
		page.gotoPage(pageNumber);
		request.getSession().setAttribute("PAGINATOR", page);
		return mapping.findForward("paging");
	}*/
	// end paginator
}
