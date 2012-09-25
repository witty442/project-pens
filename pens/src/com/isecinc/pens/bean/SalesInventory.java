package com.isecinc.pens.bean;

import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;

/**
 * Sales Inventory Class
 * 
 * @author atiz.b
 * @version $Id: SalesInventory.java,v 1.0 8/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class SalesInventory extends I_PO {

	private static final long serialVersionUID = -6311750496168312979L;

	/** SUB INVENTORY ID */
	private int subInventoryId;

	/** USER ID */
	private int userId;

	/**
	 * Default Constructor
	 */
	public SalesInventory() {}

	/**
	 * Default Constructor with rst
	 */
	public SalesInventory(ResultSet rst) throws Exception {
		setSubInventoryId(rst.getInt("SUB_INVENTORY_ID"));
		setUserId(rst.getInt("USER_ID"));
	}

	protected void setDisplayLabel() throws Exception {

	}

	public String toString() {
		return String.format("SALES INVENTORY [User %s, Sub Inv %s]", getUserId(), getSubInventoryId());
	}

	public int getSubInventoryId() {
		return subInventoryId;
	}

	public void setSubInventoryId(int subInventoryId) {
		this.subInventoryId = subInventoryId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
