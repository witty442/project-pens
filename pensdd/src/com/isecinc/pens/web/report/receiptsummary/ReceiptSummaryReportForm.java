package com.isecinc.pens.web.report.receiptsummary;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receiptsummary.ReceiptSummaryReport;

public class ReceiptSummaryReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private ReceiptSummaryReportCriteria criteria = new ReceiptSummaryReportCriteria();

	public ReceiptSummaryReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ReceiptSummaryReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ReceiptSummaryReport getReceiptSummaryReport(){
		return criteria.getReceiptSummaryReport();
	}

	public void setReceiptSummaryReport(ReceiptSummaryReport receiptSummaryReport){
		criteria.setReceiptSummaryReport(receiptSummaryReport);
	}
	
	private String fileType = SystemElements.EXCEL;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
