package com.isecinc.pens.web.report.analyst;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.salesanalyst.SABean;
import com.isecinc.pens.web.report.analyst.bean.ABean;

/**
WITTY
 * 
 */
public class AReportForm extends I_Form {

	private static final long serialVersionUID = 3636927584638032550L;
	private String reportName;
	private ACriteria criteria = new ACriteria();
	public ABean getSalesBean() {
		return criteria.getSalesBean();
	}

	public void setSalesBean(ABean salesBean) {
		criteria.setSalesBean(salesBean);
	}

	public ACriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ACriteria criteria) {
		this.criteria = criteria;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	

}
