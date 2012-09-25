package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.model.MUser;

/**
 * Member Product
 * 
 * @author Aneak.t
 * @version $Id: MemberProduct.java,v 1.0 11/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class MemberProduct extends I_PO implements Serializable {

	private static final long serialVersionUID = -3373804686820790302L;

	/**
	 * Default Constructor
	 */
	public MemberProduct() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public MemberProduct(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("MEMBER_PRODUCT_ID"));
		setProduct(new MProduct().find(String.valueOf(rst.getInt("PRODUCT_ID"))));
		setCustomerId(rst.getInt("CUSTOMER_ID"));
		setOrderQty(rst.getInt("ORDER_QTY"));
		UOM uom = new MUOM().find(rst.getString("UOM_ID"));
		setUomId(String.valueOf(uom.getId()));
		setUomLabel(uom.getCode());
		setCreated(rst.getTimestamp("CREATED"));
		setCreatedBy(new MUser().find(String.valueOf(rst.getInt("CREATED_BY"))));
		setUpdated(rst.getTimestamp("UPDATED"));
		setUpdatedBy(new MUser().find(String.valueOf(rst.getInt("UPDATED_BY"))));

		// set display label
		setDisplayLabel();

	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {}

	private int id;
	private Product product = new Product();
	private int customerId;
	private int orderQty;
	private String uomId;
	private Timestamp created;
	private User createdBy = new User();
	private Timestamp updated;
	private User updatedBy = new User();

	private String uomLabel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(int orderQty) {
		this.orderQty = orderQty;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public String getUomLabel() {
		return uomLabel;
	}

	public void setUomLabel(String uomLabel) {
		this.uomLabel = uomLabel;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

}
