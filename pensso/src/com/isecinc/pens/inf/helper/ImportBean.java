package com.isecinc.pens.inf.helper;

import java.sql.PreparedStatement;

public class ImportBean {
  private PreparedStatement psStatement;
  private String receiptId;
  private String receiptLineId;
  

public String getReceiptId() {
	return receiptId;
}
public void setReceiptId(String receiptId) {
	this.receiptId = receiptId;
}
public PreparedStatement getPsStatement() {
	return psStatement;
}
public void setPsStatement(PreparedStatement psStatement) {
	this.psStatement = psStatement;
}
public String getReceiptLineId() {
	return receiptLineId;
}
public void setReceiptLineId(String receiptLineId) {
	this.receiptLineId = receiptLineId;
}
  
  
}
