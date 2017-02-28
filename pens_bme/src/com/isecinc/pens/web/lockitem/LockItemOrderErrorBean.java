package com.isecinc.pens.web.lockitem;

public class LockItemOrderErrorBean {


	private String errorCode;
	private String errorMsg;
	private String storeCode;
	private String lockDate;
	private String unlockDate;
	
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getLockDate() {
		return lockDate;
	}
	public void setLockDate(String lockDate) {
		this.lockDate = lockDate;
	}
	public String getUnlockDate() {
		return unlockDate;
	}
	public void setUnlockDate(String unlockDate) {
		this.unlockDate = unlockDate;
	}
    
	
}
