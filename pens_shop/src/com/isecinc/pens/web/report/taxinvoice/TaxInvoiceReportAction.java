package com.isecinc.pens.web.report.taxinvoice;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.report.taxinvoice.TaxInvoiceReport;

/**
 * Tax Invoice Report Action
 * 
 * @author Aneak.t
 * @version $Id: TaxInvoiceReportAction.java,v 1.0 01/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class TaxInvoiceReportAction extends I_ReportAction<TaxInvoiceReport>{


	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		
	}

	/**
	 * Search Report. 
	 */
	protected List<TaxInvoiceReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		return null;
	}

}
