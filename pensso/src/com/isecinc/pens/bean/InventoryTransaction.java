package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.model.MUser;
import com.pens.util.DateToolsUtil;

/**
 * Inventory Transaction
 * 
 * @author Aneak.t
 * @version $Id: InventoryTransaction.java,v 1.0 11/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InventoryTransaction extends I_PO implements Serializable {

	private static final long serialVersionUID = -8287090603614825813L;

	/**
	 * Default Constructor
	 */
	public InventoryTransaction() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public InventoryTransaction(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("TRANSACTION_ID"));
		setTransactionType(rst.getString("TRANSACTION_TYPE"));
		setUser(new MUser().find(String.valueOf(rst.getInt("USER_ID"))));
		setProduct(new MProduct().find(String.valueOf(rst.getInt("PRODUCT_ID"))));
		setUom(new MUOM().find(rst.getString("UOM_ID")));
		setQty(rst.getDouble("QTY"));
		setMovementDate(DateToolsUtil.convertToString(rst.getDate("MOVEMENT_DATE")));

		// set display label
		setDisplayLabel();

	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {}

	private int id;
	private String transactionType;
	private User user = new User();
	private Product product = new Product();
	private UOM uom = new UOM();
	private double qty;
	private String movementDate;

	/** Condition from screen **/
	private String movementDateFrom;
	private String movementDateTo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public UOM getUom() {
		return uom;
	}

	public void setUom(UOM uom) {
		this.uom = uom;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		this.qty = qty;
	}

	public String getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(String movementDate) {
		this.movementDate = movementDate;
	}

	public String getMovementDateFrom() {
		return movementDateFrom;
	}

	public void setMovementDateFrom(String movementDateFrom) {
		this.movementDateFrom = movementDateFrom;
	}

	public String getMovementDateTo() {
		return movementDateTo;
	}

	public void setMovementDateTo(String movementDateTo) {
		this.movementDateTo = movementDateTo;
	}

}
