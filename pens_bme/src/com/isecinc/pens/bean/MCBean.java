package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MCBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2984706968964245517L;
	private int lineId;
	private int no;
	//criteria
	private String empIdFrom;
	private String empIdTo;
	
	//staff
	private String orgEmpRefId;//For edit Staff Setup
	private String empRefId;
	private String orgEmpId;
	private String empId;
	private String empType;
	private String empTypeDesc;
	private String mcArea;
	private String mcAreaDesc;
	private String mcRoute;
	private String mcRouteDesc;
	private String name;
	private String surName;
	private String fullName;
	private String mobile1;
	private String mobile2;
	private String active;
	private String empRouteName;
	
	//trans head
	private String monthTrip;
	private String monthTripDesc;
	private String remark;
	
	//trans detail
	private String detail;
	private String day;
	private String dayOfWeek;
	
	private String createUser;
	private String updateUser;
	private List<MCBean> items;
	private Map<String, String> daysMap = new HashMap<String, String>();//key monthTrip+day ,detail
	
	//Staff Time
	private String staffDate;
	private String startTime;
	private String endTime;
	private String reasonLeave;
	private String note;
	private String title;
	private String status;
	private String region;
	private String regionDesc;
	private boolean staffTimeExist;
	private String totalTime;
	private boolean holiday;
	
	//Staff Criteria
	private String staffYear;
	private String staffMonth;
	
	//optional
	private int maxDay;
	private int startDayOfMonth;
	private boolean canEdit = false;
	private boolean canCancel = false;
	private String mode;
	private int totalHH = 0;
	private int totalMM = 0;
	
	
	public String getEmpRouteName() {
		return empRouteName;
	}
	public void setEmpRouteName(String empRouteName) {
		this.empRouteName = empRouteName;
	}
	public String getOrgEmpRefId() {
		return orgEmpRefId;
	}
	public void setOrgEmpRefId(String orgEmpRefId) {
		this.orgEmpRefId = orgEmpRefId;
	}
	
	public int getTotalHH() {
		return totalHH;
	}
	public void setTotalHH(int totalHH) {
		this.totalHH = totalHH;
	}
	public int getTotalMM() {
		return totalMM;
	}
	public void setTotalMM(int totalMM) {
		this.totalMM = totalMM;
	}
	public String getEmpTypeDesc() {
		return empTypeDesc;
	}
	public void setEmpTypeDesc(String empTypeDesc) {
		this.empTypeDesc = empTypeDesc;
	}
	public String getRegionDesc() {
		return regionDesc;
	}
	public void setRegionDesc(String regionDesc) {
		this.regionDesc = regionDesc;
	}
	public boolean isHoliday() {
		return holiday;
	}
	public void setHoliday(boolean holiday) {
		this.holiday = holiday;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}
	public boolean isStaffTimeExist() {
		return staffTimeExist;
	}
	public void setStaffTimeExist(boolean staffTimeExist) {
		this.staffTimeExist = staffTimeExist;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getStaffYear() {
		return staffYear;
	}
	public void setStaffYear(String staffYear) {
		this.staffYear = staffYear;
	}
	public String getStaffMonth() {
		return staffMonth;
	}
	public void setStaffMonth(String staffMonth) {
		this.staffMonth = staffMonth;
	}
	public String getStaffDate() {
		return staffDate;
	}
	public void setStaffDate(String staffDate) {
		this.staffDate = staffDate;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getReasonLeave() {
		return reasonLeave;
	}
	public void setReasonLeave(String reasonLeave) {
		this.reasonLeave = reasonLeave;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getEmpRefId() {
		return empRefId;
	}
	public void setEmpRefId(String empRefId) {
		this.empRefId = empRefId;
	}
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getMcRoute() {
		return mcRoute;
	}
	public void setMcRoute(String mcRoute) {
		this.mcRoute = mcRoute;
	}
	public String getMcRouteDesc() {
		return mcRouteDesc;
	}
	public void setMcRouteDesc(String mcRouteDesc) {
		this.mcRouteDesc = mcRouteDesc;
	}
	public int getStartDayOfMonth() {
		return startDayOfMonth;
	}
	public void setStartDayOfMonth(int startDayOfMonth) {
		this.startDayOfMonth = startDayOfMonth;
	}
	public Map<String, String> getDaysMap() {
		return daysMap;
	}
	public void setDaysMap(Map<String, String> daysMap) {
		this.daysMap = daysMap;
	}
	public int getMaxDay() {
		return maxDay;
	}
	public void setMaxDay(int maxDay) {
		this.maxDay = maxDay;
	}
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getMcArea() {
		return mcArea;
	}
	public void setMcArea(String mcArea) {
		this.mcArea = mcArea;
	}
	public String getMcAreaDesc() {
		return mcAreaDesc;
	}
	public void setMcAreaDesc(String mcAreaDesc) {
		this.mcAreaDesc = mcAreaDesc;
	}
	public String getMonthTripDesc() {
		return monthTripDesc;
	}
	public void setMonthTripDesc(String monthTripDesc) {
		this.monthTripDesc = monthTripDesc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurName() {
		return surName;
	}
	public void setSurName(String surName) {
		this.surName = surName;
	}
	public String getMobile1() {
		return mobile1;
	}
	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}
	public String getMobile2() {
		return mobile2;
	}
	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}
	public String getMonthTrip() {
		return monthTrip;
	}
	public void setMonthTrip(String monthTrip) {
		this.monthTrip = monthTrip;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
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
	public List<MCBean> getItems() {
		return items;
	}
	public void setItems(List<MCBean> items) {
		this.items = items;
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
	public String getEmpIdFrom() {
		return empIdFrom;
	}
	public void setEmpIdFrom(String empIdFrom) {
		this.empIdFrom = empIdFrom;
	}
	public String getEmpIdTo() {
		return empIdTo;
	}
	public void setEmpIdTo(String empIdTo) {
		this.empIdTo = empIdTo;
	}
	public String getOrgEmpId() {
		return orgEmpId;
	}
	public void setOrgEmpId(String orgEmpId) {
		this.orgEmpId = orgEmpId;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getEmpType() {
		return empType;
	}
	public void setEmpType(String empType) {
		this.empType = empType;
	}
	
	
	
}
