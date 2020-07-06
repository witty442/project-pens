package com.isecinc.pens.web.report.creditpaid;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.detailedsales.DetailedSalesReport;

/**
 * Detailed Sales Report Form.
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReportForm.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class CreditPaidReportForm extends I_Form{

	private static final long serialVersionUID = -4562564368284660315L;

	private CreditPaidReport bean = new CreditPaidReport();
	private String fileType = SystemElements.PDF;

	public CreditPaidReport getBean() {
		return bean;
	}

	public void setBean(CreditPaidReport bean) {
		this.bean = bean;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	
}
