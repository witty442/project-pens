package com.isecinc.pens.report.moveorder;

import java.io.Serializable;
import java.util.Date;



/**
 * InvoiceDetailReport Report
 * 
 * @author WITTY
 * @version $Id: PerformanceReport.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class MoveOrderReport implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1454412685103136347L;
	
	private String orderDateFrom;
	private String orderDateTo;

	private String moveOrderType;
	private String requestNumber ;       
	private String requestDate ;
	private String pdCodeFrom;
	private String pdCodeTo ;
	
	private double qty1;
	private double qty2;
	private double totalAmount;
	private String docStatus;
	private String interfaces;
	
	
	public String getOrderDateTo() {
		return orderDateTo;
	}
	public void setOrderDateTo(String orderDateTo) {
		this.orderDateTo = orderDateTo;
	}
	public String getOrderDateFrom() {
		return orderDateFrom;
	}
	public void setOrderDateFrom(String orderDateFrom) {
		this.orderDateFrom = orderDateFrom;
	}
	public String getMoveOrderType() {
		return moveOrderType;
	}
	public void setMoveOrderType(String moveOrderType) {
		this.moveOrderType = moveOrderType;
	}
	public String getRequestNumber() {
		return requestNumber;
	}
	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}
	public String getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}
	public String getPdCodeFrom() {
		return pdCodeFrom;
	}
	public void setPdCodeFrom(String pdCodeFrom) {
		this.pdCodeFrom = pdCodeFrom;
	}
	public String getPdCodeTo() {
		return pdCodeTo;
	}
	public void setPdCodeTo(String pdCodeTo) {
		this.pdCodeTo = pdCodeTo;
	}
	public double getQty1() {
		return qty1;
	}
	public void setQty1(double qty1) {
		this.qty1 = qty1;
	}
	public double getQty2() {
		return qty2;
	}
	public void setQty2(double qty2) {
		this.qty2 = qty2;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	public String getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}
	
	
  
}
