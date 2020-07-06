package com.isecinc.pens.web.report.salesorder;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.salesorder.OrderThreeMonths;

/**
 * Order 3 Months Report Form
 * 
 * @author atiz.b
 * @version $Id: OrderThreeMonthsReportForm.java,v 1.0 17/11/2010 00:00:00 aneak.t Exp $
 * 
 */
public class OrderThreeMonthsReportForm extends I_Form {

	private static final long serialVersionUID = 3636927584638032550L;

	private OrderThreeMonthsReportCriteria criteria = new OrderThreeMonthsReportCriteria();

	private OrderThreeMonths[] results = null;
    private double totalAmount = 0;
    
    
	public OrderThreeMonthsReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(OrderThreeMonthsReportCriteria criteria) {
		this.criteria = criteria;
	}

	public OrderThreeMonths getOrderThreeMonths() {
		return criteria.getOrderThreeMonths();
	}

	public void setOrderThreeMonths(OrderThreeMonths orderThreeMonths) {
		criteria.setOrderThreeMonths(orderThreeMonths);
	}

	public OrderThreeMonths[] getResults() {
		return results;
	}

	public void setResults(OrderThreeMonths[] results) {
		this.results = results;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	

}
