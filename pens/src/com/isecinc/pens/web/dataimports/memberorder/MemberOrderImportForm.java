package com.isecinc.pens.web.dataimports.memberorder;

import java.util.List;

import org.apache.struts.upload.FormFile;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Order;

/**
 * Member Import Form
 * 
 * @author atiz.b
 * @version $Id: MemberOrderImportForm.java,v 1.0 13/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MemberOrderImportForm extends I_Form {

	private static final long serialVersionUID = -5219125720916222232L;

	private String orderDate;

	private FormFile importFile;

	private String[] memberIds;

	private List<Order> orders = null;

	private int totalOrder;

	public FormFile getImportFile() {
		return importFile;
	}

	public void setImportFile(FormFile importFile) {
		this.importFile = importFile;
	}

	public String[] getMemberIds() {
		return memberIds;
	}

	public void setMemberIds(String[] memberIds) {
		this.memberIds = memberIds;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public int getTotalOrder() {
		return totalOrder;
	}

	public void setTotalOrder(int totalOrder) {
		this.totalOrder = totalOrder;
	}
}
