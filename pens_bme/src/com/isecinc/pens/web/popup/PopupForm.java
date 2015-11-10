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
