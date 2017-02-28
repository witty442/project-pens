package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


public class LockItemOrderBean implements Serializable{

	private static final long serialVersionUID = -3452177400451463302L;
	
	private BigDecimal id;
	private String groupCode;
	private String groupStore;
	private String groupStoreName	;
	
	private String storeCode;
	private String storeName;
	private String lockDate;

	//Line
	private String unlockDate;
	private String allStore;
	
	//optional
	private String createUser;
	private String updateUser;
	private List<LockItemOrderBean> items;
	private List<LockItemOrderBean> storeList;
	private String disableTextClass;
	private boolean canEdit = false;
	private boolean canCancel = false;
	private String mode;
	private String chkFlag;
	
	
    
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public String getChkFlag() {
		return chkFlag;
	}
	public void setChkFlag(String chkFlag) {
		this.chkFlag = chkFlag;
	}
	public List<LockItemOrderBean> getStoreList() {
		return storeList;
	}
	public void setStoreList(List<LockItemOrderBean> storeList) {
		this.storeList = storeList;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getGroupStore() {
		return groupStore;
	}
	public void setGroupStore(String groupStore) {
		this.groupStore = groupStore;
	}
	
	public String getGroupStoreName() {
		return groupStoreName;
	}
	public void setGroupStoreName(String groupStoreName) {
		this.groupStoreName = groupStoreName;
	}
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
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
	public void setUnlockDate(String unLockDate) {
		this.unlockDate = unLockDate;
	}
	public String getAllStore() {
		return allStore;
	}
	public void setAllStore(String allStore) {
		this.allStore = allStore;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public List<LockItemOrderBean> getItems() {
		return items;
	}
	public void setItems(List<LockItemOrderBean> items) {
		this.items = items;
	}
	public String getDisableTextClass() {
		return disableTextClass;
	}
	public void setDisableTextClass(String disableTextClass) {
		this.disableTextClass = disableTextClass;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}

	
}
