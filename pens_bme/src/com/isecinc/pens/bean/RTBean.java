package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class RTBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1011918568953216669L;

	private String docNo;
	private String docDate;
	
	private String createUser;
	private String updateUser;
	
	private String custGroup;
	private String custGroupName;
	private String storeCode;
	private String storeName;
	private String storeFullName;
	
	private String refDoc;
	private String rtnNo;
	private String rtnQtyCTN;
	private String rtnQtyEA;

	private String picRcvDate;
	private String picRcvQtyCTN;
	private String picRcvQtyEA;
	private String status;
	private String statusDesc;
	private String remark;

	private String deliveryBy	;
	private String deliveryDate;
	private String deliveryQty;
	private String attach1	;
	private String attach2	;
	private String attach3	;
	private String attach4	;
	private String remarkTeamPic;
	
	private boolean canSave;
	private boolean canPicSave;
	private boolean canCancel;
	private boolean canComplete;
	private String noPicRcv;
	private String orderType;
	
	private List<RTBean> items;

	
	public String getOrderType() {
		return orderType;
	}


	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}


	public String getStoreFullName() {
		return storeFullName;
	}


	public void setStoreFullName(String storeFullName) {
		this.storeFullName = storeFullName;
	}


	public String getDeliveryBy() {
		return deliveryBy;
	}

   
	public String getRemarkTeamPic() {
		return remarkTeamPic;
	}


	public void setRemarkTeamPic(String remarkTeamPic) {
		this.remarkTeamPic = remarkTeamPic;
	}


	public void setDeliveryBy(String deliveryBy) {
		this.deliveryBy = deliveryBy;
	}


	public String getDeliveryDate() {
		return deliveryDate;
	}


	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}


	public String getDeliveryQty() {
		return deliveryQty;
	}


	public void setDeliveryQty(String deliveryQty) {
		this.deliveryQty = deliveryQty;
	}


	public String getAttach1() {
		return attach1;
	}


	public void setAttach1(String attach1) {
		this.attach1 = attach1;
	}


	public String getAttach2() {
		return attach2;
	}


	public void setAttach2(String attach2) {
		this.attach2 = attach2;
	}


	public String getAttach3() {
		return attach3;
	}


	public void setAttach3(String attach3) {
		this.attach3 = attach3;
	}


	public String getAttach4() {
		return attach4;
	}


	public void setAttach4(String attach4) {
		this.attach4 = attach4;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public boolean isCanPicSave() {
		return canPicSave;
	}


	public void setCanPicSave(boolean canPicSave) {
		this.canPicSave = canPicSave;
	}


	public String getNoPicRcv() {
		return noPicRcv;
	}


	public void setNoPicRcv(String noPicRcv) {
		this.noPicRcv = noPicRcv;
	}


	public boolean isCanSave() {
		return canSave;
	}


	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
	}


	public boolean isCanCancel() {
		return canCancel;
	}


	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}


	public boolean isCanComplete() {
		return canComplete;
	}


	public void setCanComplete(boolean canComplete) {
		this.canComplete = canComplete;
	}


	public String getDocNo() {
		return docNo;
	}
    

	public String getStatusDesc() {
		return statusDesc;
	}


	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}


	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}


	public String getDocDate() {
		return docDate;
	}


	public void setDocDate(String docDate) {
		this.docDate = docDate;
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


	public String getCustGroup() {
		return custGroup;
	}


	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}


	public String getCustGroupName() {
		return custGroupName;
	}


	public void setCustGroupName(String custGroupName) {
		this.custGroupName = custGroupName;
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


	public String getRefDoc() {
		return refDoc;
	}


	public void setRefDoc(String refDoc) {
		this.refDoc = refDoc;
	}


	public String getRtnNo() {
		return rtnNo;
	}


	public void setRtnNo(String rtnNo) {
		this.rtnNo = rtnNo;
	}


	public String getRtnQtyCTN() {
		return rtnQtyCTN;
	}


	public void setRtnQtyCTN(String rtnQtyCTN) {
		this.rtnQtyCTN = rtnQtyCTN;
	}


	public String getRtnQtyEA() {
		return rtnQtyEA;
	}


	public void setRtnQtyEA(String rtnQtyEA) {
		this.rtnQtyEA = rtnQtyEA;
	}


	public String getPicRcvDate() {
		return picRcvDate;
	}


	public void setPicRcvDate(String picRcvDate) {
		this.picRcvDate = picRcvDate;
	}


	public String getPicRcvQtyCTN() {
		return picRcvQtyCTN;
	}


	public void setPicRcvQtyCTN(String picRcvQtyCTN) {
		this.picRcvQtyCTN = picRcvQtyCTN;
	}


	public String getPicRcvQtyEA() {
		return picRcvQtyEA;
	}


	public void setPicRcvQtyEA(String picRcvQtyEA) {
		this.picRcvQtyEA = picRcvQtyEA;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public List<RTBean> getItems() {
		return items;
	}


	public void setItems(List<RTBean> items) {
		this.items = items;
	}
	

	
}
