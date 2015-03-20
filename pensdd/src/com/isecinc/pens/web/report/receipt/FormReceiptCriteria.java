package com.isecinc.pens.web.report.receipt;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receipt.FormReceipt;

/**
 * Shipment Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportCriteria.java,v 1.0 10/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class FormReceiptCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 5052297366952650948L;

	private FormReceipt receiptForm = new FormReceipt();

	private String fileType = SystemElements.EXCEL;
	
	public FormReceipt getReceiptForm() {
		return receiptForm;
	}

	public void setReceiptForm(FormReceipt receiptForm) {
		this.receiptForm = receiptForm;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
