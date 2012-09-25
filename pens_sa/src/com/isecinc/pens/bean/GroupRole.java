package com.isecinc.pens.bean;

/**
 * @author WITTY
 *
 */
public class GroupRole {
  private String userGroupId;
  private String userGroupName;
  private String roleId;
  private String returnStrAjax;
  private int index;
  private String readonly;
  private String disabled;
  private String styleClass;
  

public String getUserGroupName() {
	return userGroupName;
}
public void setUserGroupName(String userGroupName) {
	this.userGroupName = userGroupName;
}
public String getStyleClass() {
	return styleClass;
}
public void setStyleClass(String styleClass) {
	this.styleClass = styleClass;
}
public String getDisabled() {
	return disabled;
}
public void setDisabled(String disabled) {
	this.disabled = disabled;
}
public String getReadonly() {
	return readonly;
}
public void setReadonly(String readonly) {
	this.readonly = readonly;
}
public String getReturnStrAjax() {
	return returnStrAjax;
}
public void setReturnStrAjax(String returnStrAjax) {
	this.returnStrAjax = returnStrAjax;
}
public String getUserGroupId() {
	return userGroupId;
}
public void setUserGroupId(String userGroupId) {
	this.userGroupId = userGroupId;
}
public String getRoleId() {
	return roleId;
}
public void setRoleId(String roleId) {
	this.roleId = roleId;
}
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
  
  
}
