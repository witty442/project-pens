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
	private String staffIdFrom;
	private String staffIdTo;
	
	//staff
	private String orgStaffId;
	private String staffId;
	private String staffType;
	private String mcArea;
	private String mcAreaDesc;
	private String mcRoute;
	private String mcRouteDesc;
	private String name;
	private String sureName;
	private String mobile;
	private String active;
	
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
	
	//optional
	private int maxDay;
	private int startDayOfMonth;
	private boolean canEdit = false;
	private boolean canCancel = false;
	private String mode;
	
	
	public String getOrgStaffId() {
		return orgStaffId;
	}
	public void setOrgStaffId(String orgStaffId) {
		this.orgStaffId = orgStaffId;
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
	public String getStaffType() {
		return staffType;
	}
	public void setStaffType(String staffType) {
		this.staffType = staffType;
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
	public String getStaffIdFrom() {
		return staffIdFrom;
	}
	public void setStaffIdFrom(String staffIdFrom) {
		this.staffIdFrom = staffIdFrom;
	}
	public String getStaffIdTo() {
		return staffIdTo;
	}
	public void setStaffIdTo(String staffIdTo) {
		this.staffIdTo = staffIdTo;
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
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
	public String getSureName() {
		return sureName;
	}
	public void setSureName(String sureName) {
		this.sureName = sureName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	
	
	
}
