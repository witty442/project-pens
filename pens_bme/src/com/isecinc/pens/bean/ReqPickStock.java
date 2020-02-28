package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ReqPickStock implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
	
	//Head
	private String issueReqDate;
	private String issueReqNo;
	private String status;
	private String statusDesc;
	private String statusDate;
	private String requestor;
	private String remark;
	private int totalQty;
	private int totalReqQty;
	private int totalOnhandQty;
	private String storeCode;//CustomerNo
	private String storeName;
	private String subInv;
	private String storeNo;
	private String custGroup;
	private String custGroupDesc;
	private String needDate;
	private String wareHouse;
	private String deliveryDate;
	private int totalCtn;
	private String exported;
	private String invoiceNo;
	
	//Line
	private int lineId;
	private String pensItem;
	private String materialMaster;
	private String groupCode;
	private String barcode;
	private String qty;
	private String onhandQty;
	private String issueQty;
	private int issueQtyInt;
	private int qtyInt;
	
	private List<ReqPickStock> items;
	private String createUser;
	private String updateUser;

	//optional
	private boolean canEdit;
	private boolean canCancel;
	private boolean canConfirm;
	private boolean canPrint;
	private boolean canExport;
	private boolean canEditDeliveryDate;
	private boolean newReq;
	private boolean newSearch;
	private boolean noSearch;
	private boolean disableCustGroup;
	private boolean canAutoSubTrans;
	private boolean autoTrans;
	private String barcodeItemStatus;
	private String lineItemStyle ;
	private String lineItemId ;
	private boolean lineErrorStock;
	
	private boolean resultProcess;
	private boolean modeConfirm;
	private boolean modeEdit;
	
	private String selected ;
	private Map<String,String> pensItemMapAll;
	private int totalQtyNotInCurPage;
	
	private String rowIndex;
	private String actionDB;
	private int totalIssueQty;
	private String forwarder;
	
	
	public boolean isAutoTrans() {
		return autoTrans;
	}
	public void setAutoTrans(boolean autoTrans) {
		this.autoTrans = autoTrans;
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
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public boolean isDisableCustGroup() {
		return disableCustGroup;
	}
	public void setDisableCustGroup(boolean disableCustGroup) {
		this.disableCustGroup = disableCustGroup;
	}
	public boolean isCanEditDeliveryDate() {
		return canEditDeliveryDate;
	}
	public void setCanEditDeliveryDate(boolean canEditDeliveryDate) {
		this.canEditDeliveryDate = canEditDeliveryDate;
	}
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public int getTotalCtn() {
		return totalCtn;
	}
	public void setTotalCtn(int totalCtn) {
		this.totalCtn = totalCtn;
	}
	public String getExported() {
		return exported;
	}
	public void setExported(String exported) {
		this.exported = exported;
	}
	public int getTotalIssueQty() {
		return totalIssueQty;
	}
	public void setTotalIssueQty(int totalIssueQty) {
		this.totalIssueQty = totalIssueQty;
	}
	public String getWareHouse() {
		return wareHouse;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}
	public boolean isCanExport() {
		return canExport;
	}
	public void setCanExport(boolean canExport) {
		this.canExport = canExport;
	}
	public int getIssueQtyInt() {
		return issueQtyInt;
	}
	public void setIssueQtyInt(int issueQtyInt) {
		this.issueQtyInt = issueQtyInt;
	}
	public int getTotalReqQty() {
		return totalReqQty;
	}
	public void setTotalReqQty(int totalReqQty) {
		this.totalReqQty = totalReqQty;
	}
	public int getTotalOnhandQty() {
		return totalOnhandQty;
	}
	public void setTotalOnhandQty(int totalOnhandQty) {
		this.totalOnhandQty = totalOnhandQty;
	}
	public String getActionDB() {
		return actionDB;
	}
	public void setActionDB(String actionDB) {
		this.actionDB = actionDB;
	}
	public String getIssueQty() {
		return issueQty;
	}
	public void setIssueQty(String issueQty) {
		this.issueQty = issueQty;
	}
	public boolean isCanPrint() {
		return canPrint;
	}
	public void setCanPrint(boolean canPrint) {
		this.canPrint = canPrint;
	}
	public boolean isNoSearch() {
		return noSearch;
	}
	public void setNoSearch(boolean noSearch) {
		this.noSearch = noSearch;
	}
	public String getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(String rowIndex) {
		this.rowIndex = rowIndex;
	}
	public boolean isNewSearch() {
		return newSearch;
	}
	public void setNewSearch(boolean newSearch) {
		this.newSearch = newSearch;
	}
	public boolean isLineErrorStock() {
		return lineErrorStock;
	}
	public void setLineErrorStock(boolean lineErrorStock) {
		this.lineErrorStock = lineErrorStock;
	}
	public String getLineItemId() {
		return lineItemId;
	}
	public void setLineItemId(String lineItemIdStyle) {
		this.lineItemId = lineItemIdStyle;
	}
	public String getLineItemStyle() {
		return lineItemStyle;
	}
	public void setLineItemStyle(String lineItemStyle) {
		this.lineItemStyle = lineItemStyle;
	}
	public String getOnhandQty() {
		return onhandQty;
	}
	public void setOnhandQty(String onhandQty) {
		this.onhandQty = onhandQty;
	}
	public String getNeedDate() {
		return needDate;
	}
	public void setNeedDate(String needDate) {
		this.needDate = needDate;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getStatusDate() {
		return statusDate;
	}
	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
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
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public int getQtyInt() {
		return qtyInt;
	}
	public void setQtyInt(int qtyInt) {
		this.qtyInt = qtyInt;
	}
	public List<ReqPickStock> getItems() {
		return items;
	}
	public void setItems(List<ReqPickStock> items) {
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
	public boolean isCanConfirm() {
		return canConfirm;
	}
	public void setCanConfirm(boolean canConfirm) {
		this.canConfirm = canConfirm;
	}
	public boolean isNewReq() {
		return newReq;
	}
	public void setNewReq(boolean newReq) {
		this.newReq = newReq;
	}
	public String getBarcodeItemStatus() {
		return barcodeItemStatus;
	}
	public void setBarcodeItemStatus(String barcodeItemStatus) {
		this.barcodeItemStatus = barcodeItemStatus;
	}

	public boolean isResultProcess() {
		return resultProcess;
	}
	public void setResultProcess(boolean resultProcess) {
		this.resultProcess = resultProcess;
	}
	public boolean isModeConfirm() {
		return modeConfirm;
	}
	public void setModeConfirm(boolean modeConfirm) {
		this.modeConfirm = modeConfirm;
	}
	public boolean isModeEdit() {
		return modeEdit;
	}
	public void setModeEdit(boolean modeEdit) {
		this.modeEdit = modeEdit;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public Map<String, String> getPensItemMapAll() {
		return pensItemMapAll;
	}
	public void setPensItemMapAll(Map<String, String> pensItemMapAll) {
		this.pensItemMapAll = pensItemMapAll;
	}
	public int getTotalQtyNotInCurPage() {
		return totalQtyNotInCurPage;
	}
	public void setTotalQtyNotInCurPage(int totalQtyNotInCurPage) {
		this.totalQtyNotInCurPage = totalQtyNotInCurPage;
	}
	
	
	
}
