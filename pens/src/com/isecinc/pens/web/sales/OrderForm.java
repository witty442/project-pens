package com.isecinc.pens.web.sales;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;

/**
 * Order Form
 * 
 * @author atiz.b
 * @version $Id: OrderForm.java,v 1.0 14/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderForm extends I_Form {

	private static final long serialVersionUID = -427868075358525185L;

	private OrderCriteria criteria = new OrderCriteria();

	private Order[] results = null;

	private List<OrderLine> lines = null;

	private List<ReceiptBy> bys = null;

	private String productIds;

	private String deletedId = "";

	private int orderLineQty = 0;

	private String newShipDate;

	private String newReqDate;

	private int editTripNo;

	private OrderLine memberNewLine = new OrderLine();
	
	private String autoReceiptFlag;
	
	public String getAutoReceiptFlag() {
		return autoReceiptFlag;
	}

	public void setAutoReceiptFlag(String autoReceiptFlag) {
		this.autoReceiptFlag = autoReceiptFlag;
	}

	@SuppressWarnings("unchecked")
	public OrderForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new OrderLine();
			}
		};
		lines = LazyList.decorate(new ArrayList<OrderLine>(), factory);
		Factory factory2 = new Factory() {
			public Object create() {
				return new ReceiptBy();
			}
		};
		bys = LazyList.decorate(new ArrayList<ReceiptBy>(), factory2);
	}

	public Receipt getAutoReceipt() {
		return criteria.getAutoReceipt();
	}

	public void setAutoReceipt(Receipt autoReceipt) {
		criteria.setAutoReceipt(autoReceipt);
	}

	public OrderCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(OrderCriteria criteria) {
		this.criteria = criteria;
	}

	public Order[] getResults() {
		return results;
	}

	public void setResults(Order[] results) {
		this.results = results;
	}

	public Order getOrder() {
		return criteria.getOrder();
	}

	public void setOrder(Order order) {
		criteria.setOrder(order);
	}

	public List<OrderLine> getLines() {
		return lines;
	}

	public void setLines(List<OrderLine> lines) {
		this.lines = lines;
	}

	public String getProductIds() {
		return productIds;
	}

	public void setProductIds(String productIds) {
		this.productIds = productIds;
	}

	public String getDeletedId() {
		return deletedId;
	}

	public void setDeletedId(String deletedId) {
		this.deletedId = deletedId;
	}

	public List<ReceiptBy> getBys() {
		return bys;
	}

	public void setBys(List<ReceiptBy> bys) {
		this.bys = bys;
	}

	public int getOrderLineQty() {
		return orderLineQty;
	}

	public void setOrderLineQty(int orderLineQty) {
		this.orderLineQty = orderLineQty;
	}

	public String getNewShipDate() {
		return newShipDate;
	}

	public void setNewShipDate(String newShipDate) {
		this.newShipDate = newShipDate;
	}

	public String getNewReqDate() {
		return newReqDate;
	}

	public void setNewReqDate(String newReqDate) {
		this.newReqDate = newReqDate;
	}

	public int getEditTripNo() {
		return editTripNo;
	}

	public void setEditTripNo(int editTripNo) {
		this.editTripNo = editTripNo;
	}

	public OrderLine getMemberNewLine() {
		return memberNewLine;
	}

	public void setMemberNewLine(OrderLine memberNewLine) {
		this.memberNewLine = memberNewLine;
	}

}
