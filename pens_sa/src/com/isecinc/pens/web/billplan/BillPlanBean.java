package com.isecinc.pens.web.billplan;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.pens.bean.User;

public class BillPlanBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4104083172800233911L;

	
	private String salesZone;
	private String salesrepCode;
	private String salesrepName;
	private String billTNo;
	private String billTDate;
	private String item;
	private String itemName;
	private String planQty;
	private List<BillPlanBean> items = new ArrayList<BillPlanBean>();
	public String getSalesZone() {
		return salesZone;
	}
	public void setSalesZone(String salesZone) {
		this.salesZone = salesZone;
	}
	public String getSalesrepCode() {
		return salesrepCode;
	}
	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}
	public String getSalesrepName() {
		return salesrepName;
	}
	public void setSalesrepName(String salesrepName) {
		this.salesrepName = salesrepName;
	}
	public String getBillTNo() {
		return billTNo;
	}
	public void setBillTNo(String billTNo) {
		this.billTNo = billTNo;
	}
	public String getBillTDate() {
		return billTDate;
	}
	public void setBillTDate(String billTDate) {
		this.billTDate = billTDate;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getPlanQty() {
		return planQty;
	}
	public void setPlanQty(String planQty) {
		this.planQty = planQty;
	}
	public List<BillPlanBean> getItems() {
		return items;
	}
	public void setItems(List<BillPlanBean> items) {
		this.items = items;
	}
	
	
		
}
