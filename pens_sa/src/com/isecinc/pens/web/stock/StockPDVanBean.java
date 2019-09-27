package com.isecinc.pens.web.stock;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.pens.util.Utils;

public class StockPDVanBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4104083172800233911L;
    private int no;
    private String pdDesc;
	private String period;
	private String segment1 ;
	private String description ;
	private String uom_code;
	private String begin_pri_qty ;
	private String begin_sec_char;
	private String receipt_pri_qty ;
	private String receipt_sec_char;
	private String transact_pri_qty ;
	private String transact_sec_char;
	private String return_pri_qty ;
	private String return_sec_char;
	private String van_pri_qty ;
	private String van_sec_char;
	private String end_pri_qty ;
	private String end_sec_char;
	private List<StockPDVanBean> itemsList;
	
	
	public String getPdDesc() {
		return pdDesc;
	}
	public void setPdDesc(String pdDesc) {
		this.pdDesc = pdDesc;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
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
	public String getTransact_pri_qty() {
		return transact_pri_qty;
	}
	public void setTransact_pri_qty(String transact_pri_qty) {
		this.transact_pri_qty = transact_pri_qty;
	}
	public String getTransact_sec_char() {
		return transact_sec_char;
	}
	public void setTransact_sec_char(String transact_sec_char) {
		this.transact_sec_char = transact_sec_char;
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
	public String getVan_pri_qty() {
		return van_pri_qty;
	}
	public void setVan_pri_qty(String van_pri_qty) {
		this.van_pri_qty = van_pri_qty;
	}
	public String getVan_sec_char() {
		return van_sec_char;
	}
	public void setVan_sec_char(String van_sec_char) {
		this.van_sec_char = van_sec_char;
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
	public List<StockPDVanBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<StockPDVanBean> itemsList) {
		this.itemsList = itemsList;
	}
	
	
	}
