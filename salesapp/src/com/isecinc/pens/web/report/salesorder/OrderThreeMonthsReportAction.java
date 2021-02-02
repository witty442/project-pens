package com.isecinc.pens.web.report.salesorder;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.salesorder.OrderThreeMonths;
import com.isecinc.pens.report.salesorder.OrderThreeMonthsProcess;
import com.pens.util.DBCPConnectionProvider;

/**
 * Order 3 months Report Action
 * 
 * @author atiz.b
 * @version $Id: OrderThreeMonthsReportAction.java,v 1.0 17/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderThreeMonthsReportAction extends I_ReportAction<OrderThreeMonths> {

	/**
	 * Search
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		OrderThreeMonthsReportForm orderForm = (OrderThreeMonthsReportForm) form;
		try {
			User user = (User) request.getSession().getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);

			OrderThreeMonthsReportCriteria criteria = getSearchCriteria(request, orderForm.getCriteria(), this
					.getClass().toString());
			orderForm.setCriteria(criteria);

			List<OrderThreeMonths> pos = new OrderThreeMonthsProcess().doReport(orderForm.getOrderThreeMonths(), user,conn);
			if (pos.size() == 0) {
				orderForm.setResults(null);
				orderForm.setTotalAmount(0);
				
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			} else {
				orderForm.setResults(pos.toArray(new OrderThreeMonths[pos.size()]));
				double totalAmt = 0;
				for (OrderThreeMonths line : pos) {
					totalAmt += line.getTotalAmount();
				}
				orderForm.setTotalAmount(totalAmt);
			}
			orderForm.getCriteria().setSearchResult(pos.size());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("report");
	}

	protected List<OrderThreeMonths> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		return null;
	}

	/**
	 * Set New Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		OrderThreeMonthsReportForm oForm = (OrderThreeMonthsReportForm) form;
		oForm.setCriteria(new OrderThreeMonthsReportCriteria());
	}

}
