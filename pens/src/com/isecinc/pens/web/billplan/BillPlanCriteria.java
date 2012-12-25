package com.isecinc.pens.web.billplan;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.BillPlan;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.Summary;

public class BillPlanCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private BillPlan billPlan = new BillPlan();

	public BillPlan getBillPlan() {
		return billPlan;
	}

	public void setBillPlan(BillPlan billPlan) {
		this.billPlan = billPlan;
	}


}
