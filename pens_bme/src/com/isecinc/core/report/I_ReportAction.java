package com.isecinc.core.report;

import java.sql.Connection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import util.BeanParameter;
import util.DBCPConnectionProvider;

/**
 * I_ReportAction Class
 * 
 * @author Aneak.t
 * @version $Id: I_ActionReport.java,v 1.0 17/11/2010 15:52:00 atiz.b Exp $
 * 
 */

public abstract class I_ReportAction<E> extends DispatchAction {

	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");

	private static String fileType;
	private static String fileName;

	/**
	 * Search for Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	@SuppressWarnings("unchecked")
	public ActionForward searchReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Search for report : " + this.getClass());
//		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		List<E> lstData = null;
		Connection conn = null;

		try {
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			if (conn != null) {
				lstData = searchReport(form, request, response, parameterMap, conn);
			}
			
			String fileJasper = BeanParameter.getReportPath() + fileName;
			logger.debug("Report Path : " + fileJasper);
			if (lstData != null && lstData.size() > 0) {
				//reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, lstData);
			} else {
				 request.setAttribute("Message", "ผิดพลาด ไม่สามารถแสดงรายงานได้");
			}
		} catch (Exception e) {
			request.setAttribute("Message", e.toString());
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward("report");
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
	 * Get Search Criteria
	 * 
	 * @param <T>
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends I_ReportCriteria> T getSearchCriteria(HttpServletRequest request, T criteria,
			String attributeName) throws Exception {
		T t = null;
		String searchKey = "";

		try {
			if (request.getParameter("rf") != null) {
				searchKey = criteria.getSearchKey();
				criteria.setSearchKey("");
				request.getSession(true).removeAttribute(searchKey);
				request.getSession(true).setAttribute(attributeName, "");
			}
			if (criteria.getSearchKey().trim().length() != 0) {
				t = (T) request.getSession(true).getAttribute(criteria.getSearchKey());
			}
			if (t == null) {
				searchKey = String.valueOf(Calendar.getInstance().getTimeInMillis());
				criteria.setSearchKey(searchKey);
				request.getSession(true).setAttribute(searchKey, criteria);
				request.getSession(true).setAttribute(attributeName, searchKey);
				t = criteria;
			}
		} catch (Exception e) {
			throw e;
		}
		return t;
	}

	/**
	 * New Criteria
	 * 
	 * @param form
	 */
	protected abstract void setNewCriteria(ActionForm form) throws Exception;

	/**
	 * Search for report
	 * 
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected abstract List<E> searchReport(ActionForm form, HttpServletRequest request, HttpServletResponse response,
			HashMap<String, String> parameterMap, Connection conn) throws Exception;

	public static String getFileType() {
		return fileType;
	}

	public static void setFileType(String fileType) {
		I_ReportAction.fileType = fileType;
	}

	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String fileName) {
		I_ReportAction.fileName = fileName;
	}

}
