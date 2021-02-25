package com.pens.rest.api.mapping;

import java.util.List;

import com.isecinc.pens.bean.Invoice;

public class InvoiceAPIBean {
  private List<Invoice> invoiceList;

public List<Invoice> getInvoiceList() {
	return invoiceList;
}

public void setInvoiceList(List<Invoice> invoiceList) {
	this.invoiceList = invoiceList;
}
  
  
}
