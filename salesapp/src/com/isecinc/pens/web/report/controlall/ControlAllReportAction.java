package com.isecinc.pens.web.report.controlall;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.detailedsales.DetailedSalesReport;
import com.isecinc.pens.report.detailedsales.DetailedSalesReportProcess;
import com.pens.util.Utils;

/**
 * ControlAllReportAction
 * 
 * @author Witty.B
 * @version $Id: ControlAllReportAction.java,v 1.0 10/07/2019 15:52:00  $
 * 
 */

public class ControlAllReportAction extends I_ReportAction<ControlAllReport>{

	/**
	 * Search for report.
	 */
	protected List<ControlAllReport> searchReport(ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			HashMap parameterMap, Connection conn)
			throws Exception {
		
		ControlAllReportForm reportForm = (ControlAllReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		List<ControlAllReport> listData = new ArrayList<ControlAllReport>();
		ControlAllReportProcess process = new ControlAllReportProcess();
		ControlAllReport report = null;
		//Initial parameter
		try{
			//set parameter
			parameterMap.put("period", reportForm.getControlAllReport().getStartDate()+" - "+reportForm.getControlAllReport().getEndDate());
		    
			//MoveOrder :MoveOrderRequisition
			report = process.getMoveOrder(reportForm.getControlAllReport(), user, conn,"MoveOrderRequisition");
			listData.addAll(report.getItems());
			
			//MoveOrder :MoveOrderReturn
			report = process.getMoveOrder(reportForm.getControlAllReport(), user, conn,"MoveOrderReturn");
			listData.addAll(report.getItems());
				
			//Order 
			report = process.getOrder(reportForm.getControlAllReport(), user, conn);
			listData.addAll(report.getItems());
			
			setFileType(reportForm.getCriteria().getFileType());
			setFileName("control_all_report");
		
		  return listData;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Set New criteria.
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ControlAllReportForm reportForm = (ControlAllReportForm)form;
		reportForm.setCriteria(new ControlAllReportCriteria());
	}
}
