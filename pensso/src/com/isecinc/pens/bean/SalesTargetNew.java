package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import util.DateToolsUtil;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.model.MUser;

/**
 * Sales Target New
 * 
 * @author atiz.b
 * @version $Id: SalesTargetNew.java,v 1.0 9/12/2010 15:52:00 atiz.b Exp $
 * 
 */
public class SalesTargetNew extends I_PO implements Serializable {

	private static final long serialVersionUID = 4372813772650235387L;

	/**
	 * Default Constructor
	 */
	public SalesTargetNew() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 */
	public SalesTargetNew(ResultSet rst) throws Exception {
		setId(rst.getInt("SALES_TARGET_ID"));
		setTargetFrom("");
		if (rst.getTimestamp("TARGET_FROM") != null)
			setTargetFrom(DateToolsUtil.convertToString(rst.getTimestamp("TARGET_FROM")));
		setTargetTo("");
		if (rst.getTimestamp("TARGET_TO") != null)
			setTargetTo(DateToolsUtil.convertToString(rst.getTimestamp("TARGET_TO")));
		
		//System.out.println("productId:"+rst.getString("PRODUCT_ID")); 
		
		setProduct(new MProduct().find(rst.getString("PRODUCT_ID")));
		
		setUom(new MUOM().find(rst.getString("UOM_ID")));
		setTargetQty(rst.getInt("TARGET_QTY"));
		setUserId(rst.getInt("USER_ID"));

		//System.
		setDisplayLabel();
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() throws Exception {
		// Calculate Price...
		try{
		  calculateTargetAmount();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void calculateTargetAmount() throws Exception {
		try{
		User u = new MUser().find(String.valueOf(getUserId()));
		PriceList priceList = new MPriceList().getPriceList(u.getOrderType().getKey(), getTargetFrom(), getTargetTo());
		ProductPrice pp = new MProductPrice().lookUp(getProduct().getId(), priceList.getId(), getUom().getId());
		
		if (pp != null) setTargetAmount(pp.getPrice() * getTargetQty());
		else setTargetAmount(0);
				
		setPriceList(priceList);
		}catch(Exception e){
			
		}
	}

	@Override
	public String toString() {
		return String.format("Sales Target[%s] [%s-%s] %s, [%s/%s] ", getId(), getTargetFrom(), getTargetTo(),
				getProduct(), getTargetQty(), getTargetAmount());
	}

	/** SALES_TARGET_ID */
	private int id;

	/** TARGET_FROM */
	private String targetFrom;

	/** TARGET_TO */
	private String targetTo;

	/** PRODUCT */
	private Product product;

	/** UOM */
	private UOM uom;

	/** TARGET_QTY */
	private int targetQty;

	/** TARGET_AMOUNT */
	private double targetAmount;

	/** USER_ID */
	private int userId;

	/** Sold Amount **/
	private double soldAmount;

	/** Percent Compare */
	private double percentCompare;

	/** Base QTY */
	private int baseQty;

	/** Sub QTY */
	private int subQty;

	/** Base PROMO */
	private int basePromo;

	/** Sub PROMO */
	private int subPromo;

	/** Price List */
	private PriceList priceList;

	/** Base UOM */
	private UOM baseUOM;

	/** Sub UOM */
	private UOM subUOM;

	private String period;
	private String month;
	private String year;
	
	/** period Sales Date **/
	private String salesStartDate;
	private String salesEndDate;
	
	
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getSalesStartDate() {
		return salesStartDate;
	}

	public void setSalesStartDate(String salesStartDate) {
		this.salesStartDate = salesStartDate;
	}

	public String getSalesEndDate() {
		return salesEndDate;
	}

	public void setSalesEndDate(String salesEndDate) {
		this.salesEndDate = salesEndDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTargetFrom() {
		return targetFrom;
	}

	public void setTargetFrom(String targetFrom) {
		this.targetFrom = targetFrom;
	}

	public String getTargetTo() {
		return targetTo;
	}

	public void setTargetTo(String targetTo) {
		this.targetTo = targetTo;
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

	public int getTargetQty() {
		return targetQty;
	}

	public void setTargetQty(int targetQty) {
		this.targetQty = targetQty;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public double getSoldAmount() {
		return soldAmount;
	}

	public void setSoldAmount(double soldAmount) {
		this.soldAmount = soldAmount;
	}

	public double getPercentCompare() {
		return percentCompare;
	}

	public void setPercentCompare(double percentCompare) {
		this.percentCompare = percentCompare;
	}

	public int getBaseQty() {
		return baseQty;
	}

	public void setBaseQty(int baseQty) {
		this.baseQty = baseQty;
	}

	public int getSubQty() {
		return subQty;
	}

	public void setSubQty(int subQty) {
		this.subQty = subQty;
	}

	public int getBasePromo() {
		return basePromo;
	}

	public void setBasePromo(int basePromo) {
		this.basePromo = basePromo;
	}

	public int getSubPromo() {
		return subPromo;
	}

	public void setSubPromo(int subPromo) {
		this.subPromo = subPromo;
	}

	public PriceList getPriceList() {
		return priceList;
	}

	public void setPriceList(PriceList priceList) {
		this.priceList = priceList;
	}

	public UOM getBaseUOM() {
		return baseUOM;
	}

	public void setBaseUOM(UOM baseUOM) {
		this.baseUOM = baseUOM;
	}

	public UOM getSubUOM() {
		return subUOM;
	}

	public void setSubUOM(UOM subUOM) {
		this.subUOM = subUOM;
	}

}
