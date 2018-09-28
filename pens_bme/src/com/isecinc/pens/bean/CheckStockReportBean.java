package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class CheckStockReportBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9008978294153751220L;
	private int no;
	private String initDate;
	private String warehouse;
	private String transDate;
	private String style;
	private String materialMaster;
	private String finishingQty;
	private String transferQty;
	private String issueQty;
	private String sumQty;
	private String onhandQty;
	private String diffQty;
    private String dispDiffQtyNoZero;

    
	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
    
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getInitDate() {
		return initDate;
	}

	public void setInitDate(String initDate) {
		this.initDate = initDate;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
    
   
	public String getMaterialMaster() {
		return materialMaster;
	}

	public void setMaterialMaster(String materialMaster) {
		this.materialMaster = materialMaster;
	}

	public String getFinishingQty() {
		return finishingQty;
	}

	public void setFinishingQty(String finishingQty) {
		this.finishingQty = finishingQty;
	}

	public String getTransferQty() {
		return transferQty;
	}

	public void setTransferQty(String transferQty) {
		this.transferQty = transferQty;
	}

	public String getIssueQty() {
		return issueQty;
	}

	public void setIssueQty(String issueQty) {
		this.issueQty = issueQty;
	}

	public String getSumQty() {
		return sumQty;
	}

	public void setSumQty(String sumQty) {
		this.sumQty = sumQty;
	}

	public String getOnhandQty() {
		return onhandQty;
	}

	public void setOnhandQty(String onhandQty) {
		this.onhandQty = onhandQty;
	}

	public String getDiffQty() {
		return diffQty;
	}

	public void setDiffQty(String diffQty) {
		this.diffQty = diffQty;
	}

	public String getDispDiffQtyNoZero() {
		return dispDiffQtyNoZero;
	}

	public void setDispDiffQtyNoZero(String dispDiffQtyNoZero) {
		this.dispDiffQtyNoZero = dispDiffQtyNoZero;
	}

}
