package com.isecinc.pens.web.report.analyst.bean;

import java.io.Serializable;
import java.util.Map;

public class ConditionFilterBean implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8302204403620233387L;
	String currCondType;
	String currCondNo;
	String currCondTypeValue;
	
	String condType;
	String condCode;
	String condValueDisp;
	boolean relate;
	
	String condType1;
	String condCode1;
	String condValueDisp1;
	
	String condType2;
	String condCode2;
	String condValueDisp2;
	
	String condType3;
	String condCode3;
	String condValueDisp3;
	
	String condType4;
	String condCode4;
	String condValueDisp4;
	
	Map<String, ConditionFilterBean> condAllMap ;
	
	
	public boolean isRelate() {
		return relate;
	}
	public void setRelate(boolean relate) {
		this.relate = relate;
	}
	public String getCondType() {
		return condType;
	}
	public void setCondType(String condType) {
		this.condType = condType;
	}
	public String getCondCode() {
		return condCode;
	}
	public void setCondCode(String condCode) {
		this.condCode = condCode;
	}
	public String getCondValueDisp() {
		return condValueDisp;
	}
	public void setCondValueDisp(String condValueDisp) {
		this.condValueDisp = condValueDisp;
	}
	public Map<String, ConditionFilterBean> getCondAllMap() {
		return condAllMap;
	}
	public void setCondAllMap(Map<String, ConditionFilterBean> condAllMap) {
		this.condAllMap = condAllMap;
	}
	public String getCurrCondTypeValue() {
		return currCondTypeValue;
	}
	public void setCurrCondTypeValue(String currCondTypeValue) {
		this.currCondTypeValue = currCondTypeValue;
	}
	public String getCurrCondType() {
		return currCondType;
	}
	public void setCurrCondType(String currCondType) {
		this.currCondType = currCondType;
	}
	public String getCurrCondNo() {
		return currCondNo;
	}
	public void setCurrCondNo(String currCondNo) {
		this.currCondNo = currCondNo;
	}
	public String getCondType1() {
		return condType1;
	}
	public void setCondType1(String condType1) {
		this.condType1 = condType1;
	}
	public String getCondCode1() {
		return condCode1;
	}
	public void setCondCode1(String condCode1) {
		this.condCode1 = condCode1;
	}
	public String getCondType2() {
		return condType2;
	}
	public void setCondType2(String condType2) {
		this.condType2 = condType2;
	}
	public String getCondCode2() {
		return condCode2;
	}
	public void setCondCode2(String condCode2) {
		this.condCode2 = condCode2;
	}
	public String getCondType3() {
		return condType3;
	}
	public void setCondType3(String condType3) {
		this.condType3 = condType3;
	}
	public String getCondCode3() {
		return condCode3;
	}
	public void setCondCode3(String condCode3) {
		this.condCode3 = condCode3;
	}
	public String getCondType4() {
		return condType4;
	}
	public void setCondType4(String condType4) {
		this.condType4 = condType4;
	}
	public String getCondCode4() {
		return condCode4;
	}
	public void setCondCode4(String condCode4) {
		this.condCode4 = condCode4;
	}
	public String getCondValueDisp1() {
		return condValueDisp1;
	}
	public void setCondValueDisp1(String condValueDisp1) {
		this.condValueDisp1 = condValueDisp1;
	}
	public String getCondValueDisp2() {
		return condValueDisp2;
	}
	public void setCondValueDisp2(String condValueDisp2) {
		this.condValueDisp2 = condValueDisp2;
	}
	public String getCondValueDisp3() {
		return condValueDisp3;
	}
	public void setCondValueDisp3(String condValueDisp3) {
		this.condValueDisp3 = condValueDisp3;
	}
	public String getCondValueDisp4() {
		return condValueDisp4;
	}
	public void setCondValueDisp4(String condValueDisp4) {
		this.condValueDisp4 = condValueDisp4;
	}
	

}
