package com.isecinc.pens.inf.manager.process;

public class ResultImportBean {

	/**
	 *   results[0] = firstErrorMsg;
		 results[1] = firstErrorCode;
		 results[2] = completeRow+"";
		 results[3] = (completeRow+errorRow)+"";
		 results[4] = receiptNoAll;//ReceiptNo|xxxxx|xxxx
	 */
	
	private String firstErrorMsg;
	private String firstErrorCode;
	private int errorRow;
	private int successRow;
	private int allRow;
	private String receiptNoAll;
	
	
	public int getErrorRow() {
		return errorRow;
	}
	public void setErrorRow(int errorRow) {
		this.errorRow = errorRow;
	}
	public String getFirstErrorMsg() {
		return firstErrorMsg;
	}
	public void setFirstErrorMsg(String firstErrorMsg) {
		this.firstErrorMsg = firstErrorMsg;
	}
	public String getFirstErrorCode() {
		return firstErrorCode;
	}
	public void setFirstErrorCode(String firstErrorCode) {
		this.firstErrorCode = firstErrorCode;
	}
	
	public int getSuccessRow() {
		return successRow;
	}
	public void setSuccessRow(int successRow) {
		this.successRow = successRow;
	}
	public int getAllRow() {
		return allRow;
	}
	public void setAllRow(int allRow) {
		this.allRow = allRow;
	}
	public String getReceiptNoAll() {
		return receiptNoAll;
	}
	public void setReceiptNoAll(String receiptNoAll) {
		this.receiptNoAll = receiptNoAll;
	}
	
	
}
