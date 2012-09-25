package com.isecinc.pens.web.report.shipmenttemp;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.shipmenttemp.ShipmentTempReport;
import com.isecinc.pens.report.shipmenttemp.ShipmentTempReportProcess;

/**
 * Shipment Temporary Report Action
 * 
 * @author Aneak.t
 * @version $Id: ShipmentTempReportAction.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentTempReportAction extends I_ReportAction<ShipmentTempReport>{
	
	/**
	 *	Search for report 
	 **/
	protected List<ShipmentTempReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		
		ShipmentTempReportProcess process = new ShipmentTempReportProcess();
		ShipmentTempReportForm reportForm = (ShipmentTempReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		parameterMap.put("shipment_date", DateToolsUtil.dateNumToWord(reportForm.getShipmentTempReport().getShipmentDate()));
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("shipment_temp_report");
		
		return process.doReport(reportForm.getShipmentTempReport(), user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ShipmentTempReportForm reportForm = (ShipmentTempReportForm)form;
		reportForm.setCriteria(new ShipmentTempReportCriteria());
	}
}
