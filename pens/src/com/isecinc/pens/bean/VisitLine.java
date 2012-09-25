package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.model.MUser;

/**
 * Visit Line
 * 
 * @author Aneak.t
 * @version $Id: VisitLine.java,v 1.0 23/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class VisitLine extends I_PO implements Serializable {

	private static final long serialVersionUID = 7960103464275938610L;

	/**
	 * Default Constructor
	 */
	public VisitLine() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public VisitLine(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("VISIT_LINE_ID"));
		setLineNo(rst.getInt("LINE_NO"));
		setProduct(new MProduct().find(String.valueOf(rst.getInt("PRODUCT_ID"))));
		setIsActive(rst.getString("ISACTIVE").trim());
		setAmount(rst.getDouble("AMOUNT"));
		setAmount2(rst.getDouble("AMOUNT2"));
		setVisitId(rst.getInt("VISIT_ID"));
		setUom(new MUOM().find(rst.getString("UOM_ID")));
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
	protected void setDisplayLabel() {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	private int id;
	private int lineNo;
	private Product product = new Product();
	private String isActive;
	private double amount;
	private double amount2;
	private int visitId;
	private UOM uom = new UOM();
	private Timestamp created;
	private User createdBy = new User();
	private Timestamp updated;
	private User updatedBy = new User();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getVisitId() {
		return visitId;
	}

	public void setVisitId(int visitId) {
		this.visitId = visitId;
	}

	public UOM getUom() {
		return uom;
	}

	public void setUom(UOM uom) {
		this.uom = uom;
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

	public double getAmount2() {
		return amount2;
	}

	public void setAmount2(double amount2) {
		this.amount2 = amount2;
	}
}
