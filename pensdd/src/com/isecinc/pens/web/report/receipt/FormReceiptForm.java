package com.isecinc.pens.web.report.receipt;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.receipt.FormReceipt;
import com.isecinc.pens.report.shipment.FormShipmentReport;

/**
 * Shipment Report Form Class
 * 
 * @author Pasuwat.W
 * 
 */

public class FormReceiptForm extends I_Form{

	private static final long serialVersionUID = 7880870786572837611L;

	private FormReceiptCriteria criteria = new FormReceiptCriteria();
	
	public FormReceiptCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FormReceiptCriteria criteria) {
		this.criteria = criteria;
	}

	public FormReceipt getReceiptForm() {
		return criteria.getReceiptForm();
	}

	public void setReceiptForm(FormReceipt receiptForm) {
		criteria.setReceiptForm(receiptForm);
	}
}
