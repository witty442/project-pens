package com.isecinc.pens.web.report.receivebenecol;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.receivebenecol.ReceiveBenecolReport;
import com.isecinc.pens.report.receivebenecol.ReceiveBenecolReportProcess;

/**
 * Receive Benecol Report Action
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBenecolReportAction.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiveBenecolReportAction extends I_ReportAction<ReceiveBenecolReport>{

	/**
	 * Search for report.
	 */
	protected List<ReceiveBenecolReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		ReceiveBenecolReportProcess process = new ReceiveBenecolReportProcess();
		ReceiveBenecolReportForm receiveForm = (ReceiveBenecolReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		parameterMap.put("order_date", DateToolsUtil.dateNumToWord(receiveForm.getReceiveBenecolReport().getOrderDate()));
		setFileType(receiveForm.getCriteria().getFileType());
		setFileName("receive_benecol_report");
		
		return process.doReport(receiveForm.getReceiveBenecolReport(), user, conn);
	}

	/**
	 * Set new criteria.
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ReceiveBenecolReportForm receiveForm = (ReceiveBenecolReportForm)form;
		receiveForm.setReceiveBenecolReport(new ReceiveBenecolReport());
	}

}
