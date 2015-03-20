package com.isecinc.pens.web.report.vat;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.vat.SalesVATReport;

public class SalesVATReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private SalesVATReportCriteria criteria = new SalesVATReportCriteria();

	public SalesVATReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SalesVATReportCriteria criteria) {
		this.criteria = criteria;
	}

	public SalesVATReport getSalesVATReport(){
		return criteria.geSalesVATReport();
	}

	public void setSalesVATReport(SalesVATReport salesVATReport){
		criteria.setSalesVATReport(salesVATReport);
	}
}
