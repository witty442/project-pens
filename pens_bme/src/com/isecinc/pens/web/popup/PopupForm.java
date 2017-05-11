package com.isecinc.pens.web.popup;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.MCEmpBean;

/**
 * PopupForm Class
 * 
 * 
 */
public class PopupForm extends ActionForm implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4943665003292647343L;
	/** Labels */
	private int no;
	private String codeSearch;
	private String matCodeSearch;
	private String descSearch;
	private String storeType;
	private String code;
	private String desc;
	private String price;
	
	//search3
	private String storeCode;
	private String storeName;
	private String storeNo;
	private String subInv;
	private String wareHouse;
	private String wareHouseDesc;
	
	//new 
	private String custGroup;
	private String issueReqDate;
	private String requestor;
	private String totalReqQty;
	private String totalQty;
	private String remark;
	private String boxNo;
	
	private String name;
	private String surname;
	private String branch;
	private String groupStore;
	
	//
	private String page;
	private String checkStockDate;
	private String mat;
	private String pensItem;
	private String qty;
	
	
	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getPensItem() {
		return pensItem;
	}

	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}

	public String getMat() {
		return mat;
	}

	public void setMat(String mat) {
		this.mat = mat;
	}

	public String getCheckStockDate() {
		return checkStockDate;
	}

	public void setCheckStockDate(String checkStockDate) {
		this.checkStockDate = checkStockDate;
	}

	public String getGroupStore() {
		return groupStore;
	}

	public void setGroupStore(String groupStore) {
		this.groupStore = groupStore;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public String getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(String totalQty) {
		this.totalQty = totalQty;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCustGroup() {
		return custGroup;
	}

	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}

	public String getIssueReqDate() {
		return issueReqDate;
	}

	public void setIssueReqDate(String issueReqDate) {
		this.issueReqDate = issueReqDate;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getTotalReqQty() {
		return totalReqQty;
	}

	public void setTotalReqQty(String totalReqQty) {
		this.totalReqQty = totalReqQty;
	}
	//MC EMPOLYEE
	private MCEmpBean mcEmpBean;
	
	public PopupForm(){
	}
	
	public PopupForm(String code,String desc){
		this.code = code;
		this.desc = desc;
	}
	
	
	public MCEmpBean getMcEmpBean() {
		return mcEmpBean;
	}

	public void setMcEmpBean(MCEmpBean mcEmpBean) {
		this.mcEmpBean = mcEmpBean;
	}

	public String getWareHouse() {
		return wareHouse;
	}

	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}

	public String getWareHouseDesc() {
		return wareHouseDesc;
	}

	public void setWareHouseDesc(String wareHouseDesc) {
		this.wareHouseDesc = wareHouseDesc;
	}

	public String getMatCodeSearch() {
		return matCodeSearch;
	}

	public void setMatCodeSearch(String matCodeSearch) {
		this.matCodeSearch = matCodeSearch;
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

	public String getStoreNo() {
		return storeNo;
	}

	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}

	public String getSubInv() {
		return subInv;
	}

	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}

	public String getCodeSearch() {
		return codeSearch;
	}
	public void setCodeSearch(String codeSearch) {
		this.codeSearch = codeSearch;
	}
	public String getDescSearch() {
		return descSearch;
	}
	public void setDescSearch(String descSearch) {
		this.descSearch = descSearch;
	}
	public String getStoreType() {
		return storeType;
	}
	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}

	
}
