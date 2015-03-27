package com.isecinc.pens.bean;

import java.io.Serializable;

public class DiffStockSummary implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3194267626571399195L;
	/**criteria **/
	private String asOfDate;
	private String pensCustCodeFrom;
	private String pensCustNameFrom;
	private String haveQty;
	
	private String custCode;
	private String item;
	private String description;
	private String orderConsign;
	private String orderFromLotus;
	private String dataFromPhysical;
	private String adjust;
	private String diff;
	
	
	public String getAsOfDate() {
		return asOfDate;
	}
	public void setAsOfDate(String asOfDate) {
		this.asOfDate = asOfDate;
	}
	public String getPensCustCodeFrom() {
		return pensCustCodeFrom;
	}
	public void setPensCustCodeFrom(String pensCustCodeFrom) {
		this.pensCustCodeFrom = pensCustCodeFrom;
	}
	public String getPensCustNameFrom() {
		return pensCustNameFrom;
	}
	public void setPensCustNameFrom(String pensCustNameFrom) {
		this.pensCustNameFrom = pensCustNameFrom;
	}
	public String getHaveQty() {
		return haveQty;
	}
	public void setHaveQty(String haveQty) {
		this.haveQty = haveQty;
	}
	public String getCustCode() {
		return custCode;
	}
	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrderConsign() {
		return orderConsign;
	}
	public void setOrderConsign(String orderConsign) {
		this.orderConsign = orderConsign;
	}
	public String getOrderFromLotus() {
		return orderFromLotus;
	}
	public void setOrderFromLotus(String orderFromLotus) {
		this.orderFromLotus = orderFromLotus;
	}
	
	public String getDataFromPhysical() {
		return dataFromPhysical;
	}
	public void setDataFromPhysical(String dataFromPhysical) {
		this.dataFromPhysical = dataFromPhysical;
	}
	public String getAdjust() {
		return adjust;
	}
	public void setAdjust(String adjust) {
		this.adjust = adjust;
	}
	public String getDiff() {
		return diff;
	}
	public void setDiff(String diff) {
		this.diff = diff;
	}
	
	
	

}
