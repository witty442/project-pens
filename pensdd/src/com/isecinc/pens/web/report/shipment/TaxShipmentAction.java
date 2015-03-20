package com.isecinc.pens.web.report.shipment;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.shipment.TaxShipmentReport;
import com.isecinc.pens.report.shipment.TaxShipmentProcess;

/**
 * Shipment Report Action
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportAction.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class TaxShipmentAction extends I_ReportAction<TaxShipmentReport>{
	/**
	 *	Search for report 
	 **/
	protected List<TaxShipmentReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		
		TaxShipmentProcess process = new TaxShipmentProcess();
		TaxShipmentForm reportForm = (TaxShipmentForm)form;
		User user = (User)request.getSession().getAttribute("user");
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("tax_shipment");
		
		return process.doReport(reportForm.getTaxShipment() , user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		TaxShipmentForm reportForm = (TaxShipmentForm)form;
		reportForm.setCriteria(new TaxShipmentCriteria());
	}
}
