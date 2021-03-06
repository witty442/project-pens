package com.isecinc.pens.web.popup;

import java.io.Serializable;
import java.util.Map;

import org.apache.struts.action.ActionForm;



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
	private String pageName;
	/** Labels */
	private int no;
	private String codeSearch;
	private String descSearch;
	private String barcodeSearch;

    private String code;
    private String code2;
    private String desc;

    private Map<String, String> criteriaMap;
    
    
	public String getCode2() {
		return code2;
	}
	public void setCode2(String code2) {
		this.code2 = code2;
	}
	public String getBarcodeSearch() {
		return barcodeSearch;
	}
	public void setBarcodeSearch(String barcodeSearch) {
		this.barcodeSearch = barcodeSearch;
	}
	public Map<String, String> getCriteriaMap() {
		return criteriaMap;
	}
	public void setCriteriaMap(Map<String, String> criteriaMap) {
		this.criteriaMap = criteriaMap;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
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
	
	
}
