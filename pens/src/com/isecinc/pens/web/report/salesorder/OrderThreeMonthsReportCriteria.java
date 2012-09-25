package com.isecinc.pens.web.report.salesorder;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.report.salesorder.OrderThreeMonths;

/**
 * Order 3 Months Report Criteria
 * 
 * @author atiz.b
 * @version $Id: OrderThreeMonthsReportCriteria.java,v 1.0 17/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderThreeMonthsReportCriteria extends I_ReportCriteria {

	private static final long serialVersionUID = 6727430686022225907L;

	private OrderThreeMonths orderThreeMonths = new OrderThreeMonths();

	public OrderThreeMonths getOrderThreeMonths() {
		return orderThreeMonths;
	}

	public void setOrderThreeMonths(OrderThreeMonths orderThreeMonths) {
		this.orderThreeMonths = orderThreeMonths;
	}

}
