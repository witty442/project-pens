package com.isecinc.pens.web.report.receipt;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receipt.TaxReceipt;

public class TaxReceiptCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 5052297366952650948L;

	private TaxReceipt taxReceipt = new TaxReceipt();

	private String fileType = SystemElements.EXCEL;

	public TaxReceipt getTaxReceipt() {
		return taxReceipt;
	}

	public void setTaxReceipt(TaxReceipt taxReceipt) {
		this.taxReceipt = taxReceipt;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
