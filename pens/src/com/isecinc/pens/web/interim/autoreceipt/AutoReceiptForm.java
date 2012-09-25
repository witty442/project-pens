package com.isecinc.pens.web.interim.autoreceipt;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.interim.bean.IOrderToReceipt;
import com.isecinc.pens.web.sales.OrderCriteria;

public class AutoReceiptForm extends I_Form {

	private static final long serialVersionUID = -3377115951015868391L;

	private FormFile invoiceFile;
	
	private OrderCriteria criteria = new OrderCriteria();
	
	private List<IOrderToReceipt> results = new ArrayList<IOrderToReceipt>();

	public FormFile getInvoiceFile() {
		return invoiceFile;
	}

	public void setInvoiceFile(FormFile invoiceFile) {
		this.invoiceFile = invoiceFile;
	}

	public OrderCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(OrderCriteria criteria) {
		this.criteria = criteria;
	}

	public List<IOrderToReceipt> getResults() {
		return results;
	}

	public void setResults(List<IOrderToReceipt> results) {
		this.results = results;
	}
	
}
