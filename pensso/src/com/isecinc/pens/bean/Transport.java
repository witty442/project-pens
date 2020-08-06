package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class Transport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4214359103959749843L;
	private String region;
	private String province;
	private String amphur;
	private List<Transport> itemsList;
	private List<Transport> regionList;
	private List<Transport> provinceList;
	

	public List<Transport> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<Transport> itemsList) {
		this.itemsList = itemsList;
	}
	public List<Transport> getRegionList() {
		return regionList;
	}
	public void setRegionList(List<Transport> regionList) {
		this.regionList = regionList;
	}
	public List<Transport> getProvinceList() {
		return provinceList;
	}
	public void setProvinceList(List<Transport> provinceList) {
		this.provinceList = provinceList;
	}
	
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getAmphur() {
		return amphur;
	}
	public void setAmphur(String amphur) {
		this.amphur = amphur;
	}
	
	
}
