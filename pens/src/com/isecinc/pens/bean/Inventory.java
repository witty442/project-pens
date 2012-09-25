package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MSubInventory;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.model.MUser;

/**
 * Inventory
 * 
 * @author Aneak.t
 * @version $Id: Inventory.java,v 1.0 07/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class Inventory extends I_PO implements Serializable , Comparable {

	private static final long serialVersionUID = -6066319846611200068L;

	/**
	 * Default Constructor
	 */
	public Inventory() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public Inventory(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("INVENTORY_ONHAND_ID"));
		setUser(new MUser().find(String.valueOf(rst.getInt("USER_ID"))));
		setProduct(new MProduct().find(String.valueOf(rst.getInt("INVENTORY_ITEM_ID"))));
		setUom(new MUOM().find(rst.getString("UOM_ID")));
		setAvailableQty(rst.getDouble("AVAILABLE_QTY"));

		setSubInventory(new MSubInventory().find(rst.getString("SUB_INVENTORY_ID")));

		// set display label
		// setDisplayLabel();

	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {

	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("Inventory[%s]-[%s]  Product[%s] UOM[%s] Qty[%s]", getId(), getSubInventory(),
				getProduct(), getUom(), getAvailableQty());
	}

	private int id;
	private User user = new User();
	private Product product = new Product();
	private UOM uom = new UOM();
	private double availableQty;
	private SubInventory subInventory = new SubInventory();

	/** GROUP UOM */
	private UOM uom1 = new UOM();
	private UOM uom2 = new UOM();
	private double availableQty1;
	private double availableQty2;
	private double salesQty1;
	private double salesQty2;
	private double remainQty1;
	private double remainQty2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public double getAvailableQty() {
		return availableQty;
	}

	public void setAvailableQty(double availableQty) {
		this.availableQty = availableQty;
	}

	public SubInventory getSubInventory() {
		return subInventory;
	}

	public void setSubInventory(SubInventory subInventory) {
		this.subInventory = subInventory;
	}

	public UOM getUom1() {
		return uom1;
	}

	public void setUom1(UOM uom1) {
		this.uom1 = uom1;
	}

	public UOM getUom2() {
		return uom2;
	}

	public void setUom2(UOM uom2) {
		this.uom2 = uom2;
	}

	public double getAvailableQty1() {
		return availableQty1;
	}

	public void setAvailableQty1(double availableQty1) {
		this.availableQty1 = availableQty1;
	}

	public double getAvailableQty2() {
		return availableQty2;
	}

	public void setAvailableQty2(double availableQty2) {
		this.availableQty2 = availableQty2;
	}

	public double getSalesQty1() {
		return salesQty1;
	}

	public void setSalesQty1(double salesQty1) {
		this.salesQty1 = salesQty1;
	}

	public double getSalesQty2() {
		return salesQty2;
	}

	public void setSalesQty2(double salesQty2) {
		this.salesQty2 = salesQty2;
	}

	public double getRemainQty1() {
		return remainQty1;
	}

	public void setRemainQty1(double remainQty1) {
		this.remainQty1 = remainQty1;
	}

	public double getRemainQty2() {
		return remainQty2;
	}

	public void setRemainQty2(double remainQty2) {
		this.remainQty2 = remainQty2;
	}

	public int compareTo(Object obj) {
		Inventory inv = null;
		if(obj instanceof Inventory)
			inv = (Inventory)obj;
		else
			return 0;
		
		int ret = this.getProduct().getCode().compareTo(inv.product.getCode()); 
		return ret;
	}
}
