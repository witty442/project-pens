package com.isecinc.pens.web.report.shipmentbenecol;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.shipmentbenecol.ShipmentBenecolReport;
import com.isecinc.pens.report.shipmentbenecol.ShipmentBenecolReportProcess;


/**
 * Shipment Benecol Report Action
 * 
 * @author Aneak.t
 * @version $Id: ShipmentBenecolReportAction.java,v 1.0 17/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentBenecolReportAction extends I_ReportAction<ShipmentBenecolReport>{

	/**
	 *	Search for report 
	 **/
	protected List<ShipmentBenecolReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		
		ShipmentBenecolReportProcess process = new ShipmentBenecolReportProcess();
		ShipmentBenecolReportForm reportForm = (ShipmentBenecolReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		parameterMap.put("shipping_date", DateToolsUtil.dateNumToWord(reportForm.getShipmentBenecolReport().getShippingDate()));
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("shipment_benecol_report");
		
		return process.doReport(reportForm.getShipmentBenecolReport(), user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ShipmentBenecolReportForm reportForm = (ShipmentBenecolReportForm)form;
		reportForm.setCriteria(new ShipmentBenecolReportCriteria());
	}
}
