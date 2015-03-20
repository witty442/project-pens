package com.isecinc.pens.web.receiptAll;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ReceiptConfirm;
import com.isecinc.pens.bean.ReceiptGenerateSummary;
import com.isecinc.pens.web.sales.OrderCriteria;

public class ReceiptAllForm extends I_Form {
	
	public ReceiptAllForm(){
		Factory factory = new Factory() {
			public Object create() {
				return new ReceiptConfirm();
			}
		};
		confirms = LazyList.decorate(new ArrayList<OrderLine>(), factory);
	}

	private static final long serialVersionUID = -427868075358525185L;

	private OrderCriteria criteria = new OrderCriteria();
	
	private ReceiptAllCriteria receiptAllCriteria = new ReceiptAllCriteria();

	private List<OrderLine> orderLines = null;
	
	private OrderLine[] results = null;
	
	private String confirmDate ;
	
	private List<ReceiptConfirm> confirms =  null;
	
	private com.isecinc.pens.bean.ReceiptGenerateSummary summary = null;
	
	public List<OrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}

	public Order getOrder() {
		return criteria.getOrder();
	}

	public void setOrder(Order order) {
		criteria.setOrder(order);
	}

	public OrderLine[] getResults() {
		return results;
	}

	public void setResults(OrderLine[] results) {
		this.results = results;
	}
	
	public OrderCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(OrderCriteria criteria) {
		this.criteria = criteria;
	}

	public ReceiptAllCriteria getReceiptAllCriteria() {
		return receiptAllCriteria;
	}

	public void setReceiptAllCriteria(ReceiptAllCriteria receiptAllCriteria) {
		this.receiptAllCriteria = receiptAllCriteria;
	}

	public String getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}

	public List<ReceiptConfirm> getConfirms() {
		return confirms;
	}

	public void setConfirms(List<ReceiptConfirm> confirms) {
		this.confirms = confirms;
	}

	public com.isecinc.pens.bean.ReceiptGenerateSummary getSummary() {
		return summary;
	}

	public void setSummary(ReceiptGenerateSummary summary) {
		this.summary = summary;
	}
}
