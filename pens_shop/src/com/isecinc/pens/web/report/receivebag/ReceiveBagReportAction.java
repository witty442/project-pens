package com.isecinc.pens.web.report.receivebag;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.receivebag.ReceiveBagReport;
import com.isecinc.pens.report.receivebag.ReceiveBagReportProcess;

/**
 * Receive Bag Report Action
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBagReportAction.java,v 1.0 03/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiveBagReportAction extends I_ReportAction<ReceiveBagReport>{

	/**
	 * Search for report.
	 */
	protected List<ReceiveBagReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		
		ReceiveBagReportProcess process = new ReceiveBagReportProcess();
		ReceiveBagReportForm receiveBagForm = (ReceiveBagReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		parameterMap.put("shipment_date", DateToolsUtil.dateNumToWord(receiveBagForm.getReceiveBagReport().getShipmentDate()));
		setFileType(receiveBagForm.getCriteria().getFileType());
		setFileName("receive_bag_report");
		
		return process.doReport(receiveBagForm.getReceiveBagReport(), user, conn);
	}

	/**
	 * Set new criteria.
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ReceiveBagReportForm receiveBagForm = (ReceiveBagReportForm)form;
		receiveBagForm.setReceiveBagReport(new ReceiveBagReport());
	}
}
