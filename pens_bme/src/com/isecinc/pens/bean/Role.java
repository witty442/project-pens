package com.isecinc.pens.bean;

import java.util.List;

import com.isecinc.core.bean.References;

/**
 * @author WITTY
 *
 */
public class Role {
	
  private int index;	

  private String roleId;
  private String roleName;
  private String roleColumnAccess;
  private String roleColumnAccessDesc;
  private String roleDataAccess;
  private String roleDataAccessDesc;
  private String returnStrAjax;
  
  private List<References> roleDataAccessList;
  
  /** optional **/
  private String styleClass;
  private String roleIdDisp;
  private String indexDisp;

  
public String getRoleName() {
	return roleName;
}
public void setRoleName(String roleName) {
	this.roleName = roleName;
}
public String getIndexDisp() {
	return indexDisp;
}
public void setIndexDisp(String indexDisp) {
	this.indexDisp = indexDisp;
}
public String getRoleIdDisp() {
	return roleIdDisp;
}
public void setRoleIdDisp(String roleIdDisp) {
	this.roleIdDisp = roleIdDisp;
}
public String getStyleClass() {
	return styleClass;
}
public void setStyleClass(String styleClass) {
	this.styleClass = styleClass;
}
public List<References> getRoleDataAccessList() {
	return roleDataAccessList;
}
public void setRoleDataAccessList(List<References> roleDataAccessList) {
	this.roleDataAccessList = roleDataAccessList;
}
public String getRoleColumnAccessDesc() {
	return roleColumnAccessDesc;
}
public void setRoleColumnAccessDesc(String roleColumnAccessDesc) {
	this.roleColumnAccessDesc = roleColumnAccessDesc;
}
public String getRoleDataAccessDesc() {
	return roleDataAccessDesc;
}
public void setRoleDataAccessDesc(String roleDataAccessDesc) {
	this.roleDataAccessDesc = roleDataAccessDesc;
}
public String getReturnStrAjax() {
	return returnStrAjax;
}
public void setReturnStrAjax(String returnStrAjax) {
	this.returnStrAjax = returnStrAjax;
}
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
public String getRoleId() {
	return roleId;
}
public void setRoleId(String roleId) {
	this.roleId = roleId;
}
public String getRoleColumnAccess() {
	return roleColumnAccess;
}
public void setRoleColumnAccess(String roleColumnAccess) {
	this.roleColumnAccess = roleColumnAccess;
}
public String getRoleDataAccess() {
	return roleDataAccess;
}
public void setRoleDataAccess(String roleDataAccess) {
	this.roleDataAccess = roleDataAccess;
}
  
  
  
  
}
