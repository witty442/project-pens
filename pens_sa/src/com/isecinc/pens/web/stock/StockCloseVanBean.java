package com.isecinc.pens.web.stock;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import util.Utils;

public class StockCloseVanBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4104083172800233911L;
    private int no;
	private String period;
	private String subinventory ;
	private String segment1 ;
	private String description ;
	private String uom_code;
	private String begin_pri_qty ;
	private String begin_sec_char;
	private String sale_pri_qty ;
	private String sale_sec_char;
	private String prem_pri_qty; 
	private String prem_sec_char;
	private String total_sale_pri_qty ;
	private String total_sale_sec_char;
	private String rma_pri_qty;
	private String rma_sec_char;
	private String receipt_pri_qty ;
	private String receipt_sec_char;
	private String return_pri_qty ;
	private String return_sec_char;
	private String adj_issue_pri_qty ;
	private String adj_issue_sec_char;
	private String adj_receipt_pri_qty ;
	private String adj_receipt_sec_char;
	private String end_pri_qty ;
	private String end_sec_char;
	private List<StockCloseVanBean> itemsList;
	
	public String getAdj_receipt_pri_qty() {
		return adj_receipt_pri_qty;
	}
	public void setAdj_receipt_pri_qty(String adj_receipt_pri_qty) {
		this.adj_receipt_pri_qty = adj_receipt_pri_qty;
	}
	public String getAdj_receipt_sec_char() {
		return adj_receipt_sec_char;
	}
	public void setAdj_receipt_sec_char(String adj_receipt_sec_char) {
		this.adj_receipt_sec_char = adj_receipt_sec_char;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getSubinventory() {
		return subinventory;
	}
	public void setSubinventory(String subinventory) {
		this.subinventory = subinventory;
	}
	public String getSegment1() {
		return segment1;
	}
	public void setSegment1(String segment1) {
		this.segment1 = segment1;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUom_code() {
		return uom_code;
	}
	public void setUom_code(String uom_code) {
		this.uom_code = uom_code;
	}
	public String getBegin_pri_qty() {
		return begin_pri_qty;
	}
	public void setBegin_pri_qty(String begin_pri_qty) {
		this.begin_pri_qty = begin_pri_qty;
	}
	public String getBegin_sec_char() {
		return begin_sec_char;
	}
	public void setBegin_sec_char(String begin_sec_char) {
		this.begin_sec_char = begin_sec_char;
	}
	public String getSale_pri_qty() {
		return sale_pri_qty;
	}
	public void setSale_pri_qty(String sale_pri_qty) {
		this.sale_pri_qty = sale_pri_qty;
	}
	public String getSale_sec_char() {
		return sale_sec_char;
	}
	public void setSale_sec_char(String sale_sec_char) {
		this.sale_sec_char = sale_sec_char;
	}
	public String getPrem_pri_qty() {
		return prem_pri_qty;
	}
	public void setPrem_pri_qty(String prem_pri_qty) {
		this.prem_pri_qty = prem_pri_qty;
	}
	public String getPrem_sec_char() {
		return prem_sec_char;
	}
	public void setPrem_sec_char(String prem_sec_char) {
		this.prem_sec_char = prem_sec_char;
	}
	public String getTotal_sale_pri_qty() {
		return total_sale_pri_qty;
	}
	public void setTotal_sale_pri_qty(String total_sale_pri_qty) {
		this.total_sale_pri_qty = total_sale_pri_qty;
	}
	public String getTotal_sale_sec_char() {
		return total_sale_sec_char;
	}
	public void setTotal_sale_sec_char(String total_sale_sec_char) {
		this.total_sale_sec_char = total_sale_sec_char;
	}
	public String getRma_pri_qty() {
		return rma_pri_qty;
	}
	public void setRma_pri_qty(String rma_pri_qty) {
		this.rma_pri_qty = rma_pri_qty;
	}
	public String getRma_sec_char() {
		return rma_sec_char;
	}
	public void setRma_sec_char(String rma_sec_char) {
		this.rma_sec_char = rma_sec_char;
	}
	public String getReceipt_pri_qty() {
		return receipt_pri_qty;
	}
	public void setReceipt_pri_qty(String receipt_pri_qty) {
		this.receipt_pri_qty = receipt_pri_qty;
	}
	public String getReceipt_sec_char() {
		return receipt_sec_char;
	}
	public void setReceipt_sec_char(String receipt_sec_char) {
		this.receipt_sec_char = receipt_sec_char;
	}
	public String getReturn_pri_qty() {
		return return_pri_qty;
	}
	public void setReturn_pri_qty(String return_pri_qty) {
		this.return_pri_qty = return_pri_qty;
	}
	public String getReturn_sec_char() {
		return return_sec_char;
	}
	public void setReturn_sec_char(String return_sec_char) {
		this.return_sec_char = return_sec_char;
	}
	public String getAdj_issue_pri_qty() {
		return adj_issue_pri_qty;
	}
	public void setAdj_issue_pri_qty(String adj_issue_pri_qty) {
		this.adj_issue_pri_qty = adj_issue_pri_qty;
	}
	public String getAdj_issue_sec_char() {
		return adj_issue_sec_char;
	}
	public void setAdj_issue_sec_char(String adj_issue_sec_char) {
		this.adj_issue_sec_char = adj_issue_sec_char;
	}
	public String getEnd_pri_qty() {
		return end_pri_qty;
	}
	public void setEnd_pri_qty(String end_pri_qty) {
		this.end_pri_qty = end_pri_qty;
	}
	public String getEnd_sec_char() {
		return end_sec_char;
	}
	public void setEnd_sec_char(String end_sec_char) {
		this.end_sec_char = end_sec_char;
	}
	public List<StockCloseVanBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<StockCloseVanBean> itemsList) {
		this.itemsList = itemsList;
	}

	

}
