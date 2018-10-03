package com.isecinc.pens.web.pd;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Receipt;
/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class PDReceiptForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private PDReceiptCriteria criteria = new PDReceiptCriteria();

	private Receipt[] pdReceipts = null;

	public PDReceiptCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(PDReceiptCriteria criteria) {
		this.criteria = criteria;
	}

	public Receipt getReceipt() {
		return criteria.getReceipt();
	}

	public void setReceipt(Receipt receipt) {
		criteria.setReceipt(receipt);
	}

	public Receipt[] getPdReceipts() {
		return pdReceipts;
	}

	public void setPdReceipts(Receipt[] pdReceipts) {
		this.pdReceipts = pdReceipts;
	}
	
}
