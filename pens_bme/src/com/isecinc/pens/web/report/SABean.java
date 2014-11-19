/**
 * 
 */
package com.isecinc.pens.web.report;

import java.io.Serializable;

/**
 * @author WITTY
 *
 */
public class SABean implements Serializable{

	private static final long serialVersionUID = -1664161276084180700L;
	
	private String typeSearch;
	private String year;
	private String day;
	private String dayTo;
	private String[] chkMonth;
	private String[] chkQuarter;
	private String[] chkYear;
	private String groupBy;
	private String dispBy;
	private String orderBy;
	
	/** Condition Search **/
	private String condCode1;
	private String condCode2;
	private String condCode3;
	private String condCode4;
	
	private String condName1;
	private String condName2;
	private String condName3;
	private String condName4;
	
	private String condValue1;
	private String condValue2;
	private String condValue3;
	private String condValue4;
	
	private String condValueDisp1;
	private String condValueDisp2;
	private String condValueDisp3;
	private String condValueDisp4;
	
	/** Display Column */
	private String colNameDisp1;
	private String colNameDisp2;
	private String colNameDisp3;
	private String colNameDisp4;
	
	private String colNameUnit1;
	private String colNameUnit2;
	private String colNameUnit3;
	private String colNameUnit4;
	
	private String compareDisp1;
	private String compareDisp2;
	
	private String summaryType;

	private String returnString;
	private String includePos;
	
	/** for Search **/
	String code;
	String desc;
	
	/** for order column **/ 

	private String order_type = "";
	private String order_by_name= "";

	
	
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getDispBy() {
		return dispBy;
	}
	public void setDispBy(String dispBy) {
		this.dispBy = dispBy;
	}
	public String getIncludePos() {
		return includePos;
	}
	public void setIncludePos(String includePos) {
		this.includePos = includePos;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	public String getOrder_by_name() {
		return order_by_name;
	}
	
	public void setOrder_by_name(String order_by_name) {
		this.order_by_name = order_by_name;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	public String getReturnString() {
		return returnString;
	}
	public void setReturnString(String returnString) {
		this.returnString = returnString;
	}
	public String[] getChkYear() {
		return chkYear;
	}
	public void setChkYear(String[] chkYear) {
		this.chkYear = chkYear;
	}
	public String getTypeSearch() {
		return typeSearch;
	}
	public void setTypeSearch(String typeSearch) {
		this.typeSearch = typeSearch;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	
	public String[] getChkQuarter() {
		return chkQuarter;
	}
	public void setChkQuarter(String[] chkQuarter) {
		this.chkQuarter = chkQuarter;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	
	public String[] getChkMonth() {
		return chkMonth;
	}
	public void setChkMonth(String[] chkMonth) {
		this.chkMonth = chkMonth;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public String getCondName1() {
		return condName1;
	}
	public void setCondName1(String condName1) {
		this.condName1 = condName1;
	}
	public String getCondName2() {
		return condName2;
	}
	public void setCondName2(String condName2) {
		this.condName2 = condName2;
	}
	public String getCondName3() {
		return condName3;
	}
	public void setCondName3(String condName3) {
		this.condName3 = condName3;
	}
	public String getCondName4() {
		return condName4;
	}
	public void setCondName4(String condName4) {
		this.condName4 = condName4;
	}
	public String getCondValue1() {
		return condValue1;
	}
	public void setCondValue1(String condValue1) {
		this.condValue1 = condValue1;
	}
	public String getCondValue2() {
		return condValue2;
	}
	public void setCondValue2(String condValue2) {
		this.condValue2 = condValue2;
	}
	public String getCondValue3() {
		return condValue3;
	}
	public void setCondValue3(String condValue3) {
		this.condValue3 = condValue3;
	}
	public String getCondValue4() {
		return condValue4;
	}
	public void setCondValue4(String condValue4) {
		this.condValue4 = condValue4;
	}
	public String getColNameDisp1() {
		return colNameDisp1;
	}
	public void setColNameDisp1(String colNameDisp1) {
		this.colNameDisp1 = colNameDisp1;
	}
	public String getColNameDisp2() {
		return colNameDisp2;
	}
	public void setColNameDisp2(String colNameDisp2) {
		this.colNameDisp2 = colNameDisp2;
	}
	public String getColNameDisp3() {
		return colNameDisp3;
	}
	public void setColNameDisp3(String colNameDisp3) {
		this.colNameDisp3 = colNameDisp3;
	}
	public String getColNameDisp4() {
		return colNameDisp4;
	}
	public void setColNameDisp4(String colNameDisp4) {
		this.colNameDisp4 = colNameDisp4;
	}
	
	public String getColNameUnit1() {
		return colNameUnit1;
	}
	public void setColNameUnit1(String colNameUnit1) {
		this.colNameUnit1 = colNameUnit1;
	}
	public String getColNameUnit2() {
		return colNameUnit2;
	}
	public void setColNameUnit2(String colNameUnit2) {
		this.colNameUnit2 = colNameUnit2;
	}
	public String getColNameUnit3() {
		return colNameUnit3;
	}
	public void setColNameUnit3(String colNameUnit3) {
		this.colNameUnit3 = colNameUnit3;
	}
	public String getColNameUnit4() {
		return colNameUnit4;
	}
	public void setColNameUnit4(String colNameUnit4) {
		this.colNameUnit4 = colNameUnit4;
	}
	public String getCompareDisp1() {
		return compareDisp1;
	}
	public void setCompareDisp1(String compareDisp1) {
		this.compareDisp1 = compareDisp1;
	}
	public String getCompareDisp2() {
		return compareDisp2;
	}
	public void setCompareDisp2(String compareDisp2) {
		this.compareDisp2 = compareDisp2;
	}
	public String getCondCode1() {
		return condCode1;
	}
	public void setCondCode1(String condCode1) {
		this.condCode1 = condCode1;
	}
	public String getCondCode2() {
		return condCode2;
	}
	public void setCondCode2(String condCode2) {
		this.condCode2 = condCode2;
	}
	public String getCondCode3() {
		return condCode3;
	}
	public void setCondCode3(String condCode3) {
		this.condCode3 = condCode3;
	}
	public String getCondCode4() {
		return condCode4;
	}
	public void setCondCode4(String condCode4) {
		this.condCode4 = condCode4;
	}
	
	// Create Day to In Criteria
	public String getDayTo() {
		return dayTo;
	}
	public void setDayTo(String dayTo) {
		this.dayTo = dayTo;
	}
	
	// Create Summary Type
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}
}
