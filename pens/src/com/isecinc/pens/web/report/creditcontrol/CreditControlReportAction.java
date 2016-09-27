package com.isecinc.pens.web.report.creditcontrol;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.detailedsales.DetailedSalesReport;
import com.isecinc.pens.report.detailedsales.DetailedSalesReportProcess;

/**
 * Detailed Sales Report Action.
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReportAction.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class CreditControlReportAction extends I_ReportAction<CreditControlReport>{

	/**
	 * Search for report.
	 */
	protected List<CreditControlReport> searchReport(ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			HashMap parameterMap, Connection conn)
			throws Exception {
		
		CreditControlReportForm reportForm = (CreditControlReportForm)form;
		CreditControlReportProcess process = new CreditControlReportProcess();
		User user = (User)request.getSession().getAttribute("user");
		List<CreditControlReport> listData = null;
		//Init parameter
		try{
			parameterMap.put("start_date", reportForm.getCreditControlReport().getStartDate());
			parameterMap.put("end_date", reportForm.getCreditControlReport().getEndDate());
			parameterMap.put("user_code",user.getCode());
			parameterMap.put("user_name",user.getName());
			parameterMap.put("total_customer", process.getCountCustomer(reportForm.getCreditControlReport(), user, conn));
			
			parameterMap.put("prev_total_order_amt",process.sumPrevTotalOrderAmt(reportForm.getCreditControlReport(), user, conn));
			
			CreditControlReport report = process.getData(reportForm.getCreditControlReport(), user, conn);
			if(report != null){
				parameterMap.put("total_order_amt",report.getTotalOrderAmt());
				
				listData = report.getItems();
				if(listData != null && listData.size() >0){
				  parameterMap.put("total_record_item",listData.size() );
				}else{
				  parameterMap.put("total_record_item",0);	
				}
			}
			setFileType(reportForm.getCriteria().getFileType());
			setFileName("credit_control_report");
		
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
		CreditControlReportForm reportForm = (CreditControlReportForm)form;
		reportForm.setCriteria(new CreditControlReportCriteria());
	}
}
