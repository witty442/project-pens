package com.isecinc.pens.web.report.receipt;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.receipt.TaxReceipt;
import com.isecinc.pens.report.receipt.TaxReceiptProcess;

/**
 * Shipment Report Action
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportAction.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class TaxReceiptAction extends I_ReportAction<TaxReceipt>{
	/**
	 *	Search for report 
	 **/
	protected List<TaxReceipt> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		
		TaxReceiptProcess process = new TaxReceiptProcess();
		TaxReceiptForm reportForm = (TaxReceiptForm)form;
		User user = (User)request.getSession().getAttribute("user");
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("tax_receipt");
		
		return process.doReport(reportForm.getTaxReceipt() , user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		FormReceiptForm reportForm = (FormReceiptForm)form;
		reportForm.setCriteria(new FormReceiptCriteria());
	}
}
