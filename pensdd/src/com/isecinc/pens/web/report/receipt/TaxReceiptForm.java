package com.isecinc.pens.web.report.receipt;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.receipt.TaxReceipt;

/**
 * Shipment Report Form Class
 * 
 * @author Pasuwat.W
 * 
 */

public class TaxReceiptForm extends I_Form{

	private static final long serialVersionUID = 7880870786572837611L;

	private TaxReceiptCriteria criteria = new TaxReceiptCriteria();
	
	public TaxReceiptCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TaxReceiptCriteria criteria) {
		this.criteria = criteria;
	}

	public TaxReceipt getTaxReceipt() {
		return criteria.getTaxReceipt();
	}

	public void setTaxReceipt(TaxReceipt taxReceipt) {
		criteria.setTaxReceipt(taxReceipt);
	}
}
