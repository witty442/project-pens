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
import com.isecinc.pens.report.shipment.ShipmentReport;
import com.isecinc.pens.report.shipment.ShipmentReportProcess;

/**
 * Shipment Report Action
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportAction.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentReportAction extends I_ReportAction<ShipmentReport>{
	/**
	 *	Search for report 
	 **/
	protected List<ShipmentReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		
		ShipmentReportProcess process = new ShipmentReportProcess();
		ShipmentReportForm reportForm = (ShipmentReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		parameterMap.put("shipment_date", DateToolsUtil.dateNumToWord(reportForm.getShipmentReport().getShipmentDate()));
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("shipment_report");
		
		return process.doReport(reportForm.getShipmentReport(), user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ShipmentReportForm reportForm = (ShipmentReportForm)form;
		reportForm.setCriteria(new ShipmentReportCriteria());
	}
}
