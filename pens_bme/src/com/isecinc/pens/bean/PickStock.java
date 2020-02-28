package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PickStock implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
	
	//Head
	private String issueReqDate;
	private String issueReqNo;
	private String issueReqStatus;
	private String issueReqStatusDesc;
	
	private String confirmIssueDate;
	private String confirmIssueNo;
	private String confirmIssueStatus;
	private String confirmIssueStatusDesc;
	
	private String pickUser;
	private String pickType;
	private String subPickType;
	private String pickTypeDesc;
	private String remark;
	private int totalQty;
	private String totalBox;
	private int totalIssueQty;
	private String storeCode;
	private String storeName;
	private String subInv;
	private String storeNo;
	private String custGroup;
	private String custGroupDesc;
	private String invoiceNo;
	
	//Line
	private int lineId;
	private String jobId;
	private String jobName;
	private String boxNo;
	private String barcode;
	private String pensItem;
	private String materialMaster;
	private String groupCode;
	private String onhandQty;
	private String qty;
	private String issueQty;
	private String orgQty;
	private String totalBoxQty;
	private int qtyInt;
	private String wholePriceBF;
	private String retailPriceBF;
	
	private List<PickStock> items;
	private String createUser;
	private String updateUser;
	private String deliveryDate;

	//optional
	private boolean canEdit;
	private boolean canCancel;
	private boolean canConfirm;
	private boolean newReq;
	private boolean canComplete;
	private boolean canAutoSubTrans;
	
	private String barcodeItemStatus;
	private String lineItemErrorStyle ;
	private boolean resultProcess;
	private boolean modeConfirm;
	private boolean modeEdit;
	private boolean modeComplete;
	private String selected ;
	private Map<String,String> pensItemMapAll;
	private Map<String,String> boxNoMapAll;
	private int totalQtyNotInCurPage;
	
	//pickStock By Group
	private String boxNoFrom;
	private String boxNoTo;
	private String groupCodeFrom;
	private String groupCodeTo;
	private String orderBy;
	private String pickRefKey;
	private String workStep;
    private String page;
	
    //optional search criteria
    private String issueReqDateFrom;
    private String issueReqDateTo;
	private String issueReqNoFrom;
	private String issueReqNoTo;
	private String forwarder;
	private boolean autoTrans;
	
	
	public boolean isAutoTrans() {
		return autoTrans;
	}
	public void setAutoTrans(boolean autoTrans) {
		this.autoTrans = autoTrans;
	}
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public boolean isCanAutoSubTrans() {
		return canAutoSubTrans;
	}
	public void setCanAutoSubTrans(boolean canAutoSubTrans) {
		this.canAutoSubTrans = canAutoSubTrans;
	}
	public String getForwarder() {
		return forwarder;
	}
	public void setForwarder(String forwarder) {
		this.forwarder = forwarder;
	}
	public String getTotalBox() {
		return totalBox;
	}
	public void setTotalBox(String totalBox) {
		this.totalBox = totalBox;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getIssueReqDateFrom() {
		return issueReqDateFrom;
	}
	public void setIssueReqDateFrom(String issueReqDateFrom) {
		this.issueReqDateFrom = issueReqDateFrom;
	}
	public String getIssueReqDateTo() {
		return issueReqDateTo;
	}
	public void setIssueReqDateTo(String issueReqDateTo) {
		this.issueReqDateTo = issueReqDateTo;
	}
	public String getIssueReqNoFrom() {
		return issueReqNoFrom;
	}
	public void setIssueReqNoFrom(String issueReqNoFrom) {
		this.issueReqNoFrom = issueReqNoFrom;
	}
	public String getIssueReqNoTo() {
		return issueReqNoTo;
	}
	public void setIssueReqNoTo(String issueReqNoTo) {
		this.issueReqNoTo = issueReqNoTo;
	}
	public boolean isModeComplete() {
		return modeComplete;
	}
	public void setModeComplete(boolean modeComplete) {
		this.modeComplete = modeComplete;
	}
	public boolean isCanComplete() {
		return canComplete;
	}
	public void setCanComplete(boolean canComplete) {
		this.canComplete = canComplete;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getWorkStep() {
		return workStep;
	}
	public void setWorkStep(String workStep) {
		this.workStep = workStep;
	}
	public int getTotalIssueQty() {
		return totalIssueQty;
	}
	public void setTotalIssueQty(int totalIssueQty) {
		this.totalIssueQty = totalIssueQty;
	}
	public String getIssueQty() {
		return issueQty;
	}
	public void setIssueQty(String issueQty) {
		this.issueQty = issueQty;
	}
	public Map<String, String> getBoxNoMapAll() {
		return boxNoMapAll;
	}
	public void setBoxNoMapAll(Map<String, String> boxNoMapAll) {
		this.boxNoMapAll = boxNoMapAll;
	}
	public String getOrgQty() {
		return orgQty;
	}
	public void setOrgQty(String orgQty) {
		this.orgQty = orgQty;
	}
	public String getPickRefKey() {
		return pickRefKey;
	}
	public void setPickRefKey(String pickRefKey) {
		this.pickRefKey = pickRefKey;
	}
	public String getBoxNoFrom() {
		return boxNoFrom;
	}
	public void setBoxNoFrom(String boxNoFrom) {
		this.boxNoFrom = boxNoFrom;
	}
	public String getBoxNoTo() {
		return boxNoTo;
	}
	public void setBoxNoTo(String boxNoTo) {
		this.boxNoTo = boxNoTo;
	}
	public String getGroupCodeFrom() {
		return groupCodeFrom;
	}
	public void setGroupCodeFrom(String groupCodeFrom) {
		this.groupCodeFrom = groupCodeFrom;
	}
	public String getGroupCodeTo() {
		return groupCodeTo;
	}
	public void setGroupCodeTo(String groupCodeTo) {
		this.groupCodeTo = groupCodeTo;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public int getTotalQtyNotInCurPage() {
		return totalQtyNotInCurPage;
	}
	public void setTotalQtyNotInCurPage(int totalQtyNotInCurPage) {
		this.totalQtyNotInCurPage = totalQtyNotInCurPage;
	}
	public String getSubPickType() {
		return subPickType;
	}
	public void setSubPickType(String subPickType) {
		this.subPickType = subPickType;
	}
	public String getPickTypeDesc() {
		return pickTypeDesc;
	}
	public void setPickTypeDesc(String pickTypeDesc) {
		this.pickTypeDesc = pickTypeDesc;
	}
	public String getTotalBoxQty() {
		return totalBoxQty;
	}
	public void setTotalBoxQty(String totalBoxQty) {
		this.totalBoxQty = totalBoxQty;
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
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public String getStoreNo() {
		return storeNo;
	}
	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}
	public String getCustGroupDesc() {
		return custGroupDesc;
	}
	public void setCustGroupDesc(String custGroupDesc) {
		this.custGroupDesc = custGroupDesc;
	}
	public int getQtyInt() {
		return qtyInt;
	}
	public void setQtyInt(int qtyInt) {
		this.qtyInt = qtyInt;
	}
	public Map<String, String> getPensItemMapAll() {
		return pensItemMapAll;
	}
	public void setPensItemMapAll(Map<String, String> pensItemMapAll) {
		this.pensItemMapAll = pensItemMapAll;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public String getPickType() {
		return pickType;
	}
	public void setPickType(String pickType) {
		this.pickType = pickType;
	}
	public boolean isModeEdit() {
		return modeEdit;
	}
	public void setModeEdit(boolean modeEdit) {
		this.modeEdit = modeEdit;
	}
	public boolean isModeConfirm() {
		return modeConfirm;
	}
	public void setModeConfirm(boolean modeConfirm) {
		this.modeConfirm = modeConfirm;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public boolean isNewReq() {
		return newReq;
	}
	public void setNewReq(boolean newReq) {
		this.newReq = newReq;
	}
	public int getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}
	public boolean isCanConfirm() {
		return canConfirm;
	}
	public void setCanConfirm(boolean canConfirm) {
		this.canConfirm = canConfirm;
	}
	public boolean isResultProcess() {
		return resultProcess;
	}
	public void setResultProcess(boolean resultProcess) {
		this.resultProcess = resultProcess;
	}
	public String getLineItemErrorStyle() {
		return lineItemErrorStyle;
	}
	public void setLineItemErrorStyle(String lineItemErrorStyle) {
		this.lineItemErrorStyle = lineItemErrorStyle;
	}
	public String getBarcodeItemStatus() {
		return barcodeItemStatus;
	}
	public void setBarcodeItemStatus(String barcodeItemStatus) {
		this.barcodeItemStatus = barcodeItemStatus;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getWholePriceBF() {
		return wholePriceBF;
	}
	public void setWholePriceBF(String wholePriceBF) {
		this.wholePriceBF = wholePriceBF;
	}
	public String getRetailPriceBF() {
		return retailPriceBF;
	}
	public void setRetailPriceBF(String retailPriceBF) {
		this.retailPriceBF = retailPriceBF;
	}
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}
	
	public String getOnhandQty() {
		return onhandQty;
	}
	public void setOnhandQty(String onhandQty) {
		this.onhandQty = onhandQty;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getIssueReqDate() {
		return issueReqDate;
	}
	public void setIssueReqDate(String issueReqDate) {
		this.issueReqDate = issueReqDate;
	}
	public String getIssueReqNo() {
		return issueReqNo;
	}
	public void setIssueReqNo(String issueReqNo) {
		this.issueReqNo = issueReqNo;
	}
	public String getIssueReqStatus() {
		return issueReqStatus;
	}
	public void setIssueReqStatus(String issueReqStatus) {
		this.issueReqStatus = issueReqStatus;
	}
	public String getIssueReqStatusDesc() {
		return issueReqStatusDesc;
	}
	public void setIssueReqStatusDesc(String issueReqStatusDesc) {
		this.issueReqStatusDesc = issueReqStatusDesc;
	}
	public String getConfirmIssueDate() {
		return confirmIssueDate;
	}
	public void setConfirmIssueDate(String confirmIssueDate) {
		this.confirmIssueDate = confirmIssueDate;
	}
	public String getConfirmIssueNo() {
		return confirmIssueNo;
	}
	public void setConfirmIssueNo(String confirmIssueNo) {
		this.confirmIssueNo = confirmIssueNo;
	}
	public String getConfirmIssueStatus() {
		return confirmIssueStatus;
	}
	public void setConfirmIssueStatus(String confirmIssueStatus) {
		this.confirmIssueStatus = confirmIssueStatus;
	}
	public String getConfirmIssueStatusDesc() {
		return confirmIssueStatusDesc;
	}
	public void setConfirmIssueStatusDesc(String confirmIssueStatusDesc) {
		this.confirmIssueStatusDesc = confirmIssueStatusDesc;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getMaterialMaster() {
		return materialMaster;
	}
	public void setMaterialMaster(String materialMaster) {
		this.materialMaster = materialMaster;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	
	
	public String getPickUser() {
		return pickUser;
	}
	public void setPickUser(String pickUser) {
		this.pickUser = pickUser;
	}
	public List<PickStock> getItems() {
		return items;
	}
	public void setItems(List<PickStock> items) {
		this.items = items;
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
	
	
}
