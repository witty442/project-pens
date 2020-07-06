package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MSalesTarget;

/**
 * SalesTargetProduct
 * 
 * @author Aneak.t
 * @version $Id: SalesTargetProduct.java,v 1.0 07/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class SalesTargetProduct extends I_PO implements Serializable{

	public static final String ADMIN = "AD";
	public static final String TT = "TT";
	public static final String VAN = "VAN";
	public static final String DD = "DD";
	
	private static final long serialVersionUID = 8998958844335280300L;


	/**
	 * Default Constructor
	 */
	public SalesTargetProduct() {
		
	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public SalesTargetProduct(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("SALES_TARGET_PRODUCT_ID"));
		setProduct(new MProduct().find(String.valueOf(rst.getInt("PRODUCT_ID"))));
		setSalesTarget(new MSalesTarget().find(String.valueOf(rst.getInt("SALES_TARGET_ID"))));
		setTargetAmount(rst.getDouble("TARGET_AMOUNT"));
		setIsActive(rst.getString("ISACTIVE"));
		
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
	private Product product = new Product();
	private SalesTarget salesTarget = new SalesTarget();
	private double targetAmount;
	private String isActive;
	
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
	public SalesTarget getSalesTarget() {
		return salesTarget;
	}
	public void setSalesTarget(SalesTarget salesTarget) {
		this.salesTarget = salesTarget;
	}
	public double getTargetAmount() {
		return targetAmount;
	}
	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	
}
