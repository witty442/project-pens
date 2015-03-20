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

public class ShipmentDetailReportAction extends I_ReportAction<ShipmentReport>{
	/**
	 *	Search for report 
	 **/
	protected List<ShipmentReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		
		ShipmentReportProcess process = new ShipmentReportProcess();
		ShipmentDetailReportForm reportForm = (ShipmentDetailReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		parameterMap.put("shipment_date", DateToolsUtil.dateNumToWord(reportForm.getShipmentReport().getShipmentDate()));
		reportForm.getShipmentReport().setIsDetail(true);
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("shipmentDetail_report");
		
		return process.doReport(reportForm.getShipmentReport(), user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ShipmentDetailReportForm reportForm = (ShipmentDetailReportForm)form;
		reportForm.setCriteria(new ShipmentDetailReportCriteria());
	}
}
