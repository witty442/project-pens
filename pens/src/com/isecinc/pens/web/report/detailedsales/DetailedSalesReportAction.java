package com.isecinc.pens.web.report.detailedsales;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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

public class DetailedSalesReportAction extends I_ReportAction<DetailedSalesReport>{

	/**
	 * Search for report.
	 */
	protected List<DetailedSalesReport> searchReport(ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			HashMap<String, String> parameterMap, Connection conn)
			throws Exception {
		
		DetailedSalesReportForm reportForm = (DetailedSalesReportForm)form;
		DetailedSalesReportProcess process = new DetailedSalesReportProcess();
		User user = (User)request.getSession().getAttribute("user");
		parameterMap.put("startDate", reportForm.getDetailedSalesReport().getStartDate());
		parameterMap.put("endDate", reportForm.getDetailedSalesReport().getEndDate());
		
		if(!StringUtils.isEmpty(reportForm.getDetailedSalesReport().getOrderType())){
			if("Y".equals(reportForm.getDetailedSalesReport().getOrderType())){
			   parameterMap.put("orderType" ,"ขายสด");
			}else{
			   parameterMap.put("orderType" ,"ขายเชื่อ");
			}
		}else{
			parameterMap.put("orderType" ,"");
		}
		
		if(!StringUtils.isEmpty(reportForm.getDetailedSalesReport().getPdPaid())){
			parameterMap.put("pdPaid" ,reportForm.getDetailedSalesReport().getPdPaid());
		}else{
			parameterMap.put("pdPaid" ,"");
		}
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("detailed_sales_report");
		
		return process.doReport(reportForm.getDetailedSalesReport(), user, conn);
	}

	/**
	 * Set New criteria.
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		DetailedSalesReportForm reportForm = (DetailedSalesReportForm)form;
		reportForm.setCriteria(new DetailedSalesReportCriteria());
	}
}
