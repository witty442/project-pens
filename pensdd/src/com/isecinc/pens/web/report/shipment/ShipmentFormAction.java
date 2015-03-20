package com.isecinc.pens.web.report.shipment;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.shipment.FormShipmentReport;
import com.isecinc.pens.report.shipment.FormShipmentReportProcess;

/**
 * Shipment Report Action
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportAction.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentFormAction extends I_ReportAction<FormShipmentReport>{
	/**
	 *	Search for report 
	 **/
	protected List<FormShipmentReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		
		FormShipmentReportProcess process = new FormShipmentReportProcess();
		ShipmentFormForm reportForm = (ShipmentFormForm)form;
		User user = (User)request.getSession().getAttribute("user");
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("shipment_frm");
		
		return process.doReport(reportForm.getFormShipment() , user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ShipmentFormForm reportForm = (ShipmentFormForm)form;
		reportForm.setCriteria(new ShipmentFormCriteria());
	}
}
